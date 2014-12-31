package test;

import static org.junit.Assert.*;

import org.junit.Test;
import org.terryxu.parsleyframework.facet.groovy.FreemarkerBuilder;

class TestGsp {

	@Test
	public void test() {
		def b=new Binding()
		b.builder=FreemarkerBuilder.class
		
		def sh=new GroovyShell(b)
		sh.evaluate(new File('D:/workspace/parsley/parsley-groovy/src/test/resources/testgsps/TestPanel.groovy'))
	}

}
