package org.ppi.core.parse.dictionary;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.ppi.core.parse.dictionary.parser.DIPDictionaryParser;
import org.ppi.core.parse.dictionary.parser.TabDictionaryParser;

public class DictionaryParserFactory {

protected static DictionaryParserFactory instance;

	protected static final String DEFAULT_DICTIONARY_FORMAT_NAME = "Tab separated file";

	Map<String, Class<? extends AbstractDictionaryParser>> parsers;
	
	public DictionaryParserFactory() {
		
		this.parsers = new HashMap<String, Class<? extends AbstractDictionaryParser>>();
		
		this.parsers.put(DEFAULT_DICTIONARY_FORMAT_NAME, TabDictionaryParser.class);
		this.parsers.put("DIP dictionary format", DIPDictionaryParser.class);
		
	}
	
	public static DictionaryParserFactory getInstance() {
		if(instance==null)
			instance = new DictionaryParserFactory();
		return instance;
	}
	
	
	public Set<String> getParserNames() {
		return new TreeSet<String>(parsers.keySet());
	}
	
	public Class<? extends AbstractDictionaryParser> getClass(String parserName) {
		return parsers.get(parserName);
	}
	
	public Class<? extends AbstractDictionaryParser> getDefaultDictionaryParserClass() {
		return parsers.get(DEFAULT_DICTIONARY_FORMAT_NAME);
	}
	
	
}
