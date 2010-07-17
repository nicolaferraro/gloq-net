package org.ppi.preference;

public enum SimilarityStrategy {
	
	MAX, AVG;
	
	public static SimilarityStrategy getByName(String name) {
		for(SimilarityStrategy str : values()) {
			if(str.name().equals(name))
				return str;
		}
		return null;
	}
}
