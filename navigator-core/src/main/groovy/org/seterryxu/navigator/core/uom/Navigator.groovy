package org.seterryxu.navigator.core.uom

import javax.servlet.ServletException
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class Navigator extends HttpServlet {

	private static ThreadLocal<NavigatorRequest> CURRENT_REQUEST=new ThreadLocal<NavigatorRequest>()
	private static ThreadLocal<NavigatorResponse> CURRENT_RESPONSE=new ThreadLocal<NavigatorResponse>()

	@Override
	protected void service(HttpServletRequest req, HttpServletResponse res)
	throws ServletException, IOException {

		String reqUrl=getRequestUrl(req)
		NavigatorRequest nreq=new NRequestImpl(req, reqUrl)
		NavigatorResponse nres=new NResponseImpl(res)
		
		tryNavigate(req,res)
	}

	private String getRequestUrl(HttpServletRequest req){
		req.getRequestURI().substring(req.getContextPath())
	}
	
	private void tryNavigate(NavigatorRequest req, NavigatorResponse res){
		
	}
}
