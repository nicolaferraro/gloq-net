package org.ppi.common;

public class Tuple<T extends Comparable<T>> implements Comparable<Tuple<T>> {
	
	T[] tuple;
	
	
	public Tuple(T[] tuple) {
		this.tuple = tuple;
	}
	
	@Override
	public int compareTo(Tuple<T> o) {
		if(this.tuple.length!=o.tuple.length)
			throw new IllegalArgumentException("Incompatible types");
		
		for(int i=0; i<tuple.length; i++) {
			if(!tuple[i].equals(o.tuple[i]))
				return tuple[i].compareTo(o.tuple[i]);
		}
		return 0;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Tuple) {
			Tuple<T> o = (Tuple<T>) obj;
			
			if(tuple.length!=o.tuple.length)
				return false;
			
			for(int i=0; i<tuple.length; i++) {
				if(!tuple[i].equals(o.tuple[i]))
					return false;
			}
			return true;
		}
		return false;
	}
	
	public T get(int col) {
		return tuple[col];
	}
	
}
