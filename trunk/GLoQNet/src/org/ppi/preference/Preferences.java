package org.ppi.preference;

import static org.ppi.preference.SimilarityStrategy.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import org.apache.log4j.Logger;

public class Preferences implements Serializable {

	private static transient File STORE_FILE = new File("NetAlign.preferences");
	private static transient Preferences instance;
	
	private static final long serialVersionUID = 7821463683817907064L;
	
	protected SimilarityStrategy similarityStrategy = MAX;
	
	protected double specialStatesEmission = 1d;
	protected double escapeStatesEmission = 1d;
	
	protected int branchLimit = 500;
	
	protected double matchingTransition = 0.6d;
	protected double failingTransition = Math.pow(10, -6);
	
	double similarityThreshold = 0.7;
	
	protected int depth = 2;
	
	public Preferences() {
	}
	
	protected static Preferences load() {
		ObjectInputStream ois = null;
		
		try {
			
			ois = new ObjectInputStream(new FileInputStream(STORE_FILE));
			Preferences pref = (Preferences) ois.readObject();
			ois.close();
			
			return pref;
			
		} catch(Exception ex) {
			Logger.getLogger(Preferences.class).warn("Unable to load the preference file", ex);
			return null;
		} finally {
			try {
				if(ois!=null) ois.close();
			} catch (Exception e) {
			}
		}
	}
	
	public void persist() throws Exception {
		ObjectOutputStream oos = null;
		
		try {
			oos = new ObjectOutputStream(new FileOutputStream(STORE_FILE));
			oos.writeObject(this);
			
			oos.flush();
			oos.close();
			
		} catch (Exception ex) {
			Logger.getLogger(Preferences.class).error("Unable to store the preference file", ex);
			throw ex;
		} finally {
			try {
				if(oos!=null) oos.close();
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
		this.similarityStrategy = similarityStrategy;
	}
	
	public SimilarityStrategy getSimilarityStrategy() {
		return similarityStrategy;
	}
	
	public double getSpecialStatesEmission() {
		return specialStatesEmission;
	}
	
	public void setSpecialStatesEmission(double specialStatesEmission) {
		if(specialStatesEmission<=0d)
			throw new IllegalArgumentException();
		this.specialStatesEmission = specialStatesEmission;
	}
	
	public double getEscapeStatesEmission() {
		return escapeStatesEmission;
	}
	
	public void setEscapeStatesEmission(double escapeStatesEmission) {
		if(escapeStatesEmission<=0d)
			throw new IllegalArgumentException();
		this.escapeStatesEmission = escapeStatesEmission;
	}
	
	public int getBranchLimit() {
		return branchLimit;
	}
	
	public void setBranchLimit(int branchLimit) {
		if(branchLimit<5)
			throw new IllegalArgumentException();
		this.branchLimit = branchLimit;
	}
	
	public double getFailingTransition() {
		return failingTransition;
	}
	
	public void setFailingTransition(double failingTransition) {
		if(failingTransition<=0d)
			throw new IllegalArgumentException();
		this.failingTransition = failingTransition;
	}
	
	public double getMatchingTransition() {
		return matchingTransition;
	}
	
	public void setMatchingTransition(double matchingTransition) {
		if(matchingTransition<=0d)
			throw new IllegalArgumentException();
		this.matchingTransition = matchingTransition;
	}
	
	public int getDepth() {
		return depth;
	}
	
	public void setDepth(int depth) {
		if(depth<1)
			throw new IllegalArgumentException();
		this.depth = depth;
	}

	public double getSimilarityThreshold() {
		return similarityThreshold;
	}
	
	public void setSimilarityThreshold(double similarityThreshold) {
		if(similarityThreshold<0d)
			throw new IllegalArgumentException();
		this.similarityThreshold = similarityThreshold;
	}
}
