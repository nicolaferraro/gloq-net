package org.ppi.core.parse.network;

import java.io.File;

import org.ppi.common.execute.Executable;


public abstract class AbstractNetworkParser extends Executable<NetworkParserResult> {

	protected File file;
	
	public void setFile(File file) {
		this.file = file;
	}
	
}
