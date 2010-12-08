package org.ppi.common.store;

import java.io.File;
import java.io.PrintWriter;
import java.util.List;

public class ResultWriter {

	public static void store(List<String[]> matchings, File file) throws Exception {
		
		PrintWriter out = null;
		
		try {
			
			out = new PrintWriter(file);
			
			int pos = 0;
			for(String[] mat : matchings) {
				StringBuffer buf = new StringBuffer();
				for(int i=0; i<mat.length; i++) {
					buf.append(mat[i]);
					if(i<mat.length-1)
						buf.append('\t');
				}
				
				out.print(buf);
				
				pos++;
				if(pos<matchings.size())
					out.println();
			}
			
			out.flush();
			out.close();
			
		} finally {
			try {
				if(out!=null) out.close();
			} catch(Exception ex) {
				// who cares
			}
		}
	}
	
}
