package test;

import static org.junit.Assert.*;

import org.junit.Test;

class TestStrToken {

	@Test
	public void test() {
		StringTokenizer tk=new StringTokenizer('/b/c/a.html', '/\\')
		String[]_tokens=new String[tk.countTokens()]
		def i=0
		while(tk.hasMoreTokens()){
			_tokens[i++]=tk.nextToken()
		}
		
		println _tokens
		
	}
}
