package org.ppi.preference;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Writer;
import java.util.Properties;

import org.apache.log4j.Logger;

public class Preferences {

	private static final File STORE_FILE = new File("GLoQNet.preferences");
	
	private static Preferences instance;
	
	private Properties properties;
	
	
	public Preferences() {
		this.properties = new Properties();
	}
	
	protected static Preferences load() {
		FileReader in = null;
		
		try {
			in = new FileReader(STORE_FILE);
			
			Preferences preferences = new Preferences();
			preferences.properties.load(in);
			
			return preferences;
			
		} catch(Exception ex) {
			Logger.getLogger(Preferences.class).warn("Unable to load the preference file", ex);
			return null;
		} finally {
			try {
				if(in!=null) in.close();
			} catch (Exception e) {
			}
		}
	}
	
	public void persist() throws Exception {
		Writer out = null;
		
		try {
			out = new FileWriter(STORE_FILE);
			properties.store(out, "GLoQNet preferences");
			
		} catch (Exception ex) {
			Logger.getLogger(Preferences.class).error("Unable to store the preference file", ex);
			throw ex;
		} finally {
			try {
				if(out!=null) out.close();
			} catch (Exception e) {
			}
		}
	}
	
	
	public static Preferences getInstance() {
		if(instance==null) {
			instance = load();
			if(instance==null) {
				// default preferences
				instance = new Preferences();
			}
		}
		return instance;
	}
	
	public static void setInstance(Preferences pref) {
		instance = pref;
	}
	
	
	public void setSimilarityStrategy(SimilarityStrategy similarityStrategy) {
		setProperty(PreferenceKey.SIMILARITY_STRATEGY, similarityStrategy.name());
	}
	
	public SimilarityStrategy getSimilarityStrategy() {
		String name = getProperty(PreferenceKey.SIMILARITY_STRATEGY);
		return SimilarityStrategy.getByName(name);
	}
	
	public double getSpecialStatesEmission() {
		String val = getProperty(PreferenceKey.SPECIAL_STATE_EMISSION);
		return Double.parseDouble(val);
	}
	
	public void setSpecialStatesEmission(double specialStatesEmission) {
		if(specialStatesEmission<=0d)
			throw new IllegalArgumentException();
		setProperty(PreferenceKey.SPECIAL_STATE_EMISSION, String.valueOf(specialStatesEmission));
	}
	
	public double getEscapeStatesEmission() {
		String val = getProperty(PreferenceKey.ESCAPE_STATE_EMISSION);
		return Double.parseDouble(val);
	}
	
	public void setEscapeStatesEmission(double escapeStatesEmission) {
		if(escapeStatesEmission<=0d)
			throw new IllegalArgumentException();
		setProperty(PreferenceKey.ESCAPE_STATE_EMISSION, String.valueOf(escapeStatesEmission));
	}
	
	public int getBranchLimit() {
		String val = getProperty(PreferenceKey.BRANCH_LIMIT);
		return Integer.parseInt(val);
	}
	
	public void setBranchLimit(int branchLimit) {
		if(branchLimit<5)
			throw new IllegalArgumentException();
		setProperty(PreferenceKey.BRANCH_LIMIT, String.valueOf(branchLimit));
	}
	
	public double getFailingTransition() {
		String val = getProperty(PreferenceKey.FAILING_TRANSITION);
		return Double.parseDouble(val);
	}
	
	public void setFailingTransition(double failingTransition) {
		if(failingTransition<=0d)
			throw new IllegalArgumentException();
		setProperty(PreferenceKey.FAILING_TRANSITION, String.valueOf(failingTransition));
	}
	
	public double getMatchingTransition() {
		String val = getProperty(PreferenceKey.MATCHING_TRANSITION);
		return Double.parseDouble(val);
	}
	
	public void setMatchingTransition(double matchingTransition) {
		if(matchingTransition<=0d)
			throw new IllegalArgumentException();
		setProperty(PreferenceKey.MATCHING_TRANSITION, String.valueOf(matchingTransition));
	}
	
	public int getDepth() {
		String val = getProperty(PreferenceKey.DEPTH);
		return Integer.parseInt(val);
	}
	
	public void setDepth(int depth) {
		if(depth<1)
			throw new IllegalArgumentException();
		setProperty(PreferenceKey.DEPTH, String.valueOf(depth));
	}

	public double getSimilarityThreshold() {
		String val = getProperty(PreferenceKey.SIMILARITY_THRESHOLD);
		return Double.parseDouble(val);

	}
	
	public void setSimilarityThreshold(double similarityThreshold) {
		if(similarityThreshold<0d)
			throw new IllegalArgumentException();
		setProperty(PreferenceKey.SIMILARITY_THRESHOLD, String.valueOf(similarityThreshold));
	}
	
	protected String getProperty(PreferenceKey key) {
		String val = properties.getProperty(key.getKey());
		if(val==null)
			return key.getDefaultValue();
		return val;
	}
	
	protected void setProperty(PreferenceKey key, String value) {
		properties.setProperty(key.getKey(), value);
	}
	
	private static enum PreferenceKey {
		
		SIMILARITY_STRATEGY("similarity.strategy", SimilarityStrategy.MAX.name()),
		SPECIAL_STATE_EMISSION("emission.special.states", String.valueOf(1d)),
		ESCAPE_STATE_EMISSION("emission.escape.states", String.valueOf(1d)),
		BRANCH_LIMIT("multi.alignment.branch.limit", String.valueOf(500)),
		MATCHING_TRANSITION("transition.high.scoring", String.valueOf(0.6d)),
		FAILING_TRANSITION("transition.low.scoring", String.valueOf(Math.pow(10, -6))),
		SIMILARITY_THRESHOLD("similarity.threshold", String.valueOf(0.7)),
		DEPTH("linearization.level", String.valueOf(3));
		
		String key;
		String defaultValue;
		
		private PreferenceKey(String key, String defaultValue) {
			this.key = key;
			this.defaultValue = defaultValue;
		}
		
		public String getKey() {
			return key;
		}
		
		public String getDefaultValue() {
			return defaultValue;
		}
	}
	
}
