package org.ppi.common.manager;

import org.ppi.core.dictionary.Dictionary;

public class DictionaryManager {
	
	protected static DictionaryManager instance;
	
	Dictionary dictionary;
	
	protected DictionaryManager() {
		this.dictionary = new Dictionary();
	}
	
	public static DictionaryManager getInstance() {
		if(instance==null)
			instance = new DictionaryManager();
		return instance;
	}
	
	public Dictionary getDictionary() {
		return dictionary;
	}
	
}
