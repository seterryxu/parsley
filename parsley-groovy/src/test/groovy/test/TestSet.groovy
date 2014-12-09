package test;

import static org.junit.Assert.*;

import java.util.Set;

import org.junit.Test;

class TestSet {

	@Test
	public void test() {
		Set<String> KEY_WORDS=['if','else',''] as Set<String>
		println KEY_WORDS.contains('if')
		println KEY_WORDS
		
		StringWriter w=new StringWriter()
		w<<'abc'
		println w.toString()
	}

}
