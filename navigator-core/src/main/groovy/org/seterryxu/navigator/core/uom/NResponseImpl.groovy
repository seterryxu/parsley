package org.seterryxu.navigator.core.uom

import javax.servlet.http.HttpServletResponseWrapper

class NResponseImpl extends HttpServletResponseWrapper implements
NavigatorResponse {

	NResponseImpl(NavigatorResponse res){
		super(res)
		
	}
}
