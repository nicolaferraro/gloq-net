package org.ppi.core.parse.result;

import org.ppi.core.parse.result.parser.DefaultResultParser;

public class ResultParserFactory {

protected static ResultParserFactory instance;
	
	public ResultParserFactory() {
		
	}
	
	public static ResultParserFactory getInstance() {
		if(instance==null)
			instance = new ResultParserFactory();
		return instance;
	}
	
	public Class<? extends AbstractResultParser> getDefaultParserClass() {
		return DefaultResultParser.class;
	}
	
	
}
