package org.ppi.gui.label;

import java.util.ArrayList;
import java.util.List;

public class TextLabelGenerator implements LabelGenerator {

	protected static final String VALUES = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	
	protected List<Integer> indices;
	
	public TextLabelGenerator() {
		this.indices = new ArrayList<Integer>();
		indices.add(new Integer(0));
	}
	
	@Override
	public String nextLabel() {
		try {
			
			String v = "";
			for(int p : indices) {
				v+=VALUES.charAt(p);
			}
			
			// increment
			int i=indices.size()-1;
			boolean carry;
			do {
				carry = false;
				int p = indices.get(i);
				p++;
				if(p>=VALUES.length()) {
					p=0;
					carry = true;
				}
				indices.set(i, p);
				i--;
			} while(carry && i>=0);
			
			if(carry) {
				indices.add(0, 0);
			}
			
			return v;
			
		} catch(Exception ex) {
			System.err.println("Errors while generating next label");
		}
		return VALUES;
	}
	
}
