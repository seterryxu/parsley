package test;

import static org.junit.Assert.*

import org.junit.Test
import org.terryxu.parsleyframework.facet.groovy.ParsleyScriptHelper
import java.net.URL

class TestGsp {

	@Test
	public void test() {
		def f=new File('D:/tmpcode/index.groovy')
		def helper=new ParsleyScriptHelper(new Object(), f.toURL())
		helper.writeTo(new OutputStreamWriter(System.out))
	}
}
