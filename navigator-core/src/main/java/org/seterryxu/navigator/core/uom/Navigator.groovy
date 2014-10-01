package org.seterryxu.navigator.core.uom

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

class Navigator extends HttpServlet {

	@Override
	protected void service(HttpServletRequest req, HttpServletResponse res)
	throws ServletException, IOException {
		
		String reqUrl=getRequestUrl(req)
		
	}
	
	private String getRequestUrl(HttpServletRequest req){
		req.getRequestURI().substring(req.getContextPath())
	}
}
