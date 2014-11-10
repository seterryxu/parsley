package test

import static org.junit.Assert.*

import org.junit.Test
import org.seterryxu.navigator.framework.core.uom.PRequestImpl;
import org.seterryxu.navigator.framework.core.uom.PResponseImpl;

class TestClosure {

	@Test
	public void test() {
//		def aa=[2, 1, 3]
//
//		aa.each {
//			println it
//			if(it==1)
//				return
//		}
//
//		for(def a:aa){
//			println a
//			if(a==1)
//				break
//		}

		TestBean.metaClass.invokeMethod={String name,Object argz->
				if(name.startsWith('do'))
					println "$name, $argz"
			}
		
		new TestBean().doAbc()
		
		PRequestImpl pres;
	}
	
	def $(){
		
	}
}
