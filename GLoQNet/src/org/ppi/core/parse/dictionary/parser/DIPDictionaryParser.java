package org.ppi.core.parse.dictionary.parser;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.StringTokenizer;

import org.ppi.core.dictionary.Dictionary;
import org.ppi.core.parse.dictionary.AbstractDictionaryParser;
import org.ppi.core.parse.dictionary.DictionaryParserResult;
import org.ppi.preference.Preferences;


public class DIPDictionaryParser extends AbstractDictionaryParser {

	@Override
	protected void execute() throws Exception {

		BufferedReader br = new BufferedReader(new FileReader(file));

		String line;
		Dictionary dict = new Dictionary();
		int errorCount = 0;
		
		signalCurrentOperation("Loading the file");
		
		while ((line = br.readLine()) != null) {
			StringTokenizer st = new StringTokenizer(line, " \t");

			String first = null;
			String second = null;
			double value = Double.NaN;
			
			int column = 0;
			
			while (st.hasMoreTokens()) {
				String s = st.nextToken();
				
				if(column==0) {
					
				} else if(column==1) {
					try {
						value = Double.parseDouble(s);
					} catch(NumberFormatException nfe) {
						break;
					}
				} else if(column==2) {
					if(s.startsWith("DIP:")) {
						first = s.substring(s.indexOf(':')+1);
					} else {
						break;
					}
				} else if(column==3) {
					if(s.startsWith("DIP:")) {
						second = s.substring(s.indexOf(':')+1);
					} else {
						break;
					}
				}
				
				column++;
			}

			if (first != null && second != null && !Double.isNaN(value)) {
				if(value>Preferences.getInstance().getSimilarityThreshold()) {
					dict.addEntry(first, second, value);
				}
			} else {
				errorCount++;
			}
			
			checkPoint();
		}
		
		result = new DictionaryParserResult();
		result.setDictionary(dict);
		result.setErrorCount(errorCount);
		
	}

}
