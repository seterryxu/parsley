package org.seterryxu.navigator.core.parse

abstract class AbstractParser<V> implements Parsable<V> {

	abstract V load(V view);
	
	String parse(V view){
		
	}
}
