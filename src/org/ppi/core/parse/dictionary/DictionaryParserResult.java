package org.ppi.core.parse.dictionary;

import org.ppi.core.dictionary.Dictionary;

public class DictionaryParserResult {
	
	Dictionary dictionary;
	int errorCount;
	
	public DictionaryParserResult() {
	}
	
	public void setDictionary(Dictionary dictionary) {
		this.dictionary = dictionary;
	}
	
	public void setErrorCount(int errorCount) {
		this.errorCount = errorCount;
	}
	
	public Dictionary getDictionary() {
		return dictionary;
	}
	
	public int getErrorCount() {
		return errorCount;
	}
}
