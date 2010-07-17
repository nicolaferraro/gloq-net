package org.ppi;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.ppi.common.execute.PartialResultObserver;
import org.ppi.common.manager.DictionaryManager;
import org.ppi.common.result.Matching;
import org.ppi.core.algorithm.GlobalMatching;
import org.ppi.core.algorithm.LocalMatching;
import org.ppi.core.graph.Graph;
import org.ppi.core.graph.Node;
import org.ppi.core.parse.dictionary.AbstractDictionaryParser;
import org.ppi.core.parse.dictionary.DictionaryParserFactory;
import org.ppi.core.parse.dictionary.DictionaryParserResult;
import org.ppi.core.parse.network.AbstractNetworkParser;
import org.ppi.core.parse.network.NetworkParserFactory;
import org.ppi.core.parse.network.NetworkParserResult;
import org.ppi.preference.Constants;
import org.ppi.preference.Preferences;
import org.ppi.shell.AlignmentChoice;
import org.ppi.shell.ParameterParser;

public class ShellApplication {

	protected static Logger logger;

	protected static PrintWriter result;

	public static void main(String[] args) throws Exception {
		
		try {

			PropertyConfigurator.configure(ShellApplication.class.getResource("/log4j.properties"));
			logger = Logger.getLogger(ShellApplication.class);

			logger.info("Application Started");
			
			ParameterParser params = new ParameterParser(args);
			if(params.getNetworkFiles().size()<2) {
				System.out.println("You must insert at least 2 network files (option -n)");
				return;
			}
			if(params.getDictionaryFiles().size()<1) {
				System.out.println("You must insert at least 1 dictionary file (option -d)");
				return;
			}
			if(params.getAlignmentChoice()==null) {
				System.out.println("You must specify the type of alignment (option -a, values: GLOBAL/LOCAL)");
				return;
			}
			
			List<File> networkFiles = params.getNetworkFiles();

			List<File> dictionaryFiles = params.getDictionaryFiles();

			String resultFileName = "Result";
			for(File f : networkFiles) {
				resultFileName += "_" + cleanFileName(f.getName());
			}
			resultFileName += "_" + getTimestamp();
			
			String ext = ".txt";
			
			File outputFile = new File(resultFileName + "_01" + ext);
			int prg = 2;
			while(outputFile.exists()) {
				outputFile = new File(resultFileName + "_" + pad(prg, 2) + ext);
				prg++;
			}
			
			result = new PrintWriter(outputFile);

			/*
			 * Networks
			 */

			List<Graph> graphs = new ArrayList<Graph>();

			for (File netFile : networkFiles) {
				NetworkParserFactory netParserFactory = NetworkParserFactory.getInstance();
				Class<? extends AbstractNetworkParser> netParserClass = netParserFactory.getDefaultParserClass();
				AbstractNetworkParser netParser = netParserClass.newInstance();
				netParser.setFile(netFile);
				netParser.launch();
				netParser.waitForCompletion();

				if (!netParser.hasCompleted()) {
					logger.error("Error while loading the networks");
					return;
				} else {
					NetworkParserResult parserRes = netParser.getResult();
					if (parserRes.getErrorCount() > 0) {
						boolean cont = askUser("The parser found " + parserRes.getErrorCount() + " error(s) while reading the file. Continue?");
						if (cont) {
							graphs.add(parserRes.getGraph());
						} else {
							logger.error("Closing the application");
							return;
						}
					} else {
						graphs.add(parserRes.getGraph());
					}
				}
			}

			/*
			 * Dictionary
			 */

			for (File f : dictionaryFiles) {
				Class<? extends AbstractDictionaryParser> parseClass = DictionaryParserFactory.getInstance().getDefaultDictionaryParserClass();
				AbstractDictionaryParser parser = parseClass.newInstance();
				parser.setFile(f);

				parser.launch();
				parser.waitForCompletion();

				if (!parser.hasCompleted()) {
					logger.error("Error while reading the dictionary file");
					return;
				} else {
					DictionaryParserResult parserRes = parser.getResult();
					if (parserRes.getErrorCount() > 0) {
						boolean cont = askUser("The parser found " + parserRes.getErrorCount() + " error(s) while reading the file. Continue?");
						if (cont) {
							DictionaryManager.getInstance().getDictionary().mergeDictionary(parserRes.getDictionary());
						}
					} else {
						DictionaryManager.getInstance().getDictionary().mergeDictionary(parserRes.getDictionary());
					}
				}
			}
			
			if(params.getAlignmentChoice()==AlignmentChoice.GLOBAL) {
				GlobalMatching algo = new GlobalMatching(graphs, DictionaryManager.getInstance().getDictionary(), Preferences.getInstance().getDepth());
				algo.addPartialResultObserver(new PartialResultObserver<Set<Matching>>() {

					@Override
					public void partialResultComputed(Set<Matching> partialResult) {

						for (Matching matching : partialResult) {
							List<String> strToPrint = new ArrayList<String>();
							for (Node n : matching.getNodeList()) {
								strToPrint.add(n.getName());
							}

							strToPrint.add(getDictSim(matching.getNodeList()));

							strToPrint.add(matching.getMatchingTransitionsLogScore() + "");

							for (int i = 0; i < strToPrint.size(); i++) {
								result.print(strToPrint.get(i));
								if (i < strToPrint.size() - 1)
									result.print("\t");
							}

							result.println();
						}

						result.flush();
					}

					protected String getDictSim(List<Node> nodes) {
						double simSum = 0;
						int count = 0;
						for (int i = 0; i < nodes.size(); i++) {
							for (int j = i + 1; j < nodes.size(); j++) {
								Node n1 = nodes.get(i);
								Node n2 = nodes.get(j);
								double sim = DictionaryManager.getInstance().getDictionary().getSimilarity(n1.getName(), n2.getName());
								if (sim > 0) {
									simSum += sim;
									count++;
								}
							}
						}

						double totalSim = 0d;
						if (count > 0) {
							totalSim = simSum / count;
						}

						return "" + totalSim;
					}

				});
				
				algo.launch();
				algo.waitForCompletion();
				
			} else {
				LocalMatching algo = new LocalMatching(graphs, DictionaryManager.getInstance().getDictionary(), Preferences.getInstance().getDepth());
				algo.addPartialResultObserver(new PartialResultObserver<Set<Matching>>() {

					@Override
					public void partialResultComputed(Set<Matching> partialResult) {
						
						if(partialResult.size()==0)
							return;
						
						for (Matching matching : partialResult) {
							List<String> strToPrint = new ArrayList<String>();
							for (Node n : matching.getNodeList()) {
								strToPrint.add(n.getName());
							}

							strToPrint.add(getDictSim(matching.getNodeList()));

							strToPrint.add(matching.getMatchingTransitionsLogScore() + "");

							for (int i = 0; i < strToPrint.size(); i++) {
								result.print(strToPrint.get(i));
								if (i < strToPrint.size() - 1)
									result.print("\t");
							}

							result.println();
						}
						
						Matching firstMat = partialResult.iterator().next();
						int num = firstMat.getNodeList().size();
						num+=2; // similarity and score
						
						
						for(int i=0; i<num; i++) {
							result.print(Constants.RESULT_MATCHINGS_SEPARATOR);
							if(i<num-1)
								result.print("\t");
						}
						result.println();

						result.flush();
					}

					protected String getDictSim(List<Node> nodes) {
						double simSum = 0;
						int count = 0;
						for (int i = 0; i < nodes.size(); i++) {
							for (int j = i + 1; j < nodes.size(); j++) {
								Node n1 = nodes.get(i);
								Node n2 = nodes.get(j);
								double sim = DictionaryManager.getInstance().getDictionary().getSimilarity(n1.getName(), n2.getName());
								if (sim > 0) {
									simSum += sim;
									count++;
								}
							}
						}

						double totalSim = 0d;
						if (count > 0) {
							totalSim = simSum / count;
						}

						return "" + totalSim;
					}

				});
				
				algo.launch();
				algo.waitForCompletion();
			}
			
			

			System.out.println();
			System.out.println();
			System.out.println();
			System.out.println("Tutto a posto Simona.. i risultati sono nel file " + outputFile);
			System.out.println("Cia");
			
			
		} finally {
			try {
				if (result != null)
					result.close();
			} catch (Exception ex) {
			}
		}

	}

	protected static boolean askUser(String message) {
		Boolean result = null;
		do {
			System.out.println();
			System.out.print(message + " [Y/N] ");
			Scanner scanner = new Scanner(System.in);

			if (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				if (line != null && line.trim().equalsIgnoreCase("Y")) {
					result = true;
				} else if (line != null && line.trim().equalsIgnoreCase("N")) {
					result = false;
				}
			}
		} while (result == null);
		return result;

	}
	
	protected static String getTimestamp() {
		Calendar cal = Calendar.getInstance();
		
		String res = "";
		String separator = "-";
		String separator_dt = "__";
		String separator_t = "-";
		
		res+=pad(cal.get(Calendar.YEAR), 4);
		res+=separator;
		res+=pad(cal.get(Calendar.MONTH) + 1, 2);
		res+=separator;
		res+=pad(cal.get(Calendar.DAY_OF_MONTH), 2);
		res+=separator_dt;
		
		res+=pad(cal.get(Calendar.HOUR_OF_DAY), 2);
		res+=separator_t;
		res+=pad(cal.get(Calendar.MINUTE), 2);
		
		return res;
	}
	
	protected static String pad(long num, int length) {
		String res = "" + num;
		while(res.length()<length)
			res = "0" + res;
		return res;
	}
	
	protected static String cleanFileName(String file) {
		if(file==null)
			return "";
		file = file.trim();
		int pos = file.lastIndexOf('.');
		if(pos>0) {
			file = file.substring(0, pos);
		}
		return file;
	}

}
