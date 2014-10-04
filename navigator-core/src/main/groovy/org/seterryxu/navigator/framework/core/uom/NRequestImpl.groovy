package org.seterryxu.navigator.framework.core.uom

import javax.servlet.http.HttpServletRequestWrapper

class NRequestImpl extends HttpServletRequestWrapper implements
NavigatorRequest {

	private tokenizedReqUrl=[]

	NRequestImpl(NavigatorRequest req,String reqUrl){
		super(req)
		this.tokenizedReqUrl=tokenize(reqUrl)
	}

	private String tokenize(String url){
		new StringTokenizer(url, '/').each {tokenizedReqUrl<<it}
	}
}
