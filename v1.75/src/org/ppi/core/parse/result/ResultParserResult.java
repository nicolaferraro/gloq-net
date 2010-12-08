package org.ppi.core.parse.result;

import java.util.Set;

import org.ppi.common.result.Matching;

public class ResultParserResult {
	
	Set<Set<Matching>> alignments;
	int errorCount;
	
	public ResultParserResult() {
	}
	
	public void setAlignments(Set<Set<Matching>> alignments) {
		this.alignments = alignments;
	}
	
	public Set<Set<Matching>> getAlignments() {
		return alignments;
	}
	
	public void setErrorCount(int errorCount) {
		this.errorCount = errorCount;
	}
	
	public int getErrorCount() {
		return errorCount;
	}
}
