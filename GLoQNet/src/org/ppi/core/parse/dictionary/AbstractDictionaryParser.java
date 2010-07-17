package org.ppi.core.parse.dictionary;

import java.io.File;

import org.ppi.common.execute.Executable;


public abstract class AbstractDictionaryParser extends Executable<DictionaryParserResult> {

	protected File file;
	
	public void setFile(File file) {
		this.file = file;
	}
	
}
