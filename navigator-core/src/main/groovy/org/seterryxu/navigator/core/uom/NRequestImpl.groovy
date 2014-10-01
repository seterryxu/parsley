package org.seterryxu.navigator.core.uom

import javax.servlet.http.HttpServletRequestWrapper

class NRequestImpl extends HttpServletRequestWrapper implements
NavigatorRequest {

	private NavigatorRequest req
	private tokenizedReqUrl=[]

	NRequestImpl(NavigatorRequest req,String reqUrl){
		this.req=req
		this.tokenizedReqUrl=tokenize(reqUrl)
	}

	private String tokenize(String url){
		new StringTokenizer(url, '/').each {tokenizedReqUrl<<it}
	}
}
