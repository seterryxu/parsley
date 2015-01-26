package org.terryxu.parsleyframework.facet.freemarker

import java.io.IOException

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

class FreemarkerRequestDispatcher implements RequestDispatcher {

	
	FreemarkerRequestDispatcher(){
		
	}
	
	@Override
	public void forward(ServletRequest request, ServletResponse response)
			throws ServletException, IOException {
			
	}

	@Override
	public void include(ServletRequest request, ServletResponse response)
			throws ServletException, IOException {
		forward(request, response)
	}

}
