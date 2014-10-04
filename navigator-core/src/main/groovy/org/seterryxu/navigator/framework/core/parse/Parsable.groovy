package org.seterryxu.navigator.framework.core.parse

interface Parsable<V> {

	String getExtension()
	
	String parse(V view)
	
}
