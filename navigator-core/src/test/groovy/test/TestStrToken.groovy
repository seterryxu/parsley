package test;

import static org.junit.Assert.*;

import org.junit.Test;

class TestStrToken {

	@Test
	public void test() {
		StringTokenizer st=new StringTokenizer("some/dir/res1/res2",'/')
//		st.each {
//			println it
//		}
		def lst=[]
		st.each {lst<<it}
		
		lst.each {println it}
		
	}
}
