package org.ppi.core.parse.result;

import java.io.File;

import org.ppi.common.execute.Executable;


public abstract class AbstractResultParser extends Executable<ResultParserResult> {

	protected File file;
	
	public void setFile(File file) {
		this.file = file;
	}
	
}
