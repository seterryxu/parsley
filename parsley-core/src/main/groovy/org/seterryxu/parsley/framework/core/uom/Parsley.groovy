/*
 * Copyright (c) 2012-2014, Xu Lijia
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided
 * that the following conditions are met:
 *
 *     * Redistributions of source code must retain the above copyright notice, this list of
 *       conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright notice, this list of
 *       conditions and the following disclaimer in the documentation and/or other materials
 *       provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS
 * OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY
 * AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS
 * BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER
 * IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF
 * THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.seterryxu.parsley.framework.core.uom

import javax.servlet.ServletConfig
import javax.servlet.ServletContext
import javax.servlet.ServletException
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

import org.seterryxu.parsley.framework.core.WebApp

/**
 * @author Xu Lijia
 *
 */
final class Parsley extends HttpServlet {

	private ServletContext _context

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config)
		_context=config.getServletContext()
	}

	@Override
	protected void service(HttpServletRequest req, HttpServletResponse res)
	throws ServletException, IOException {
		//wrapper
		IParsleyRequest preq=new PRequestImpl(req)
		IParsleyResponse pres=new PResponseImpl(res)

		//		if(preq.isRestfulRequest()){
		//		}

		if(preq.isStaticResourceRequest()){
			URL resUrl=LocalizedResourceSelector.selectByLocale(preq.getRequestedResourceName(),preq.getRequestedLocale())
			//TODO where to implement this method?
			pres.handleStaticResource(resUrl)
		}

		//		if(preq.isJavaScriptRequest()){
		//		}

		/*		while(preq.hasMoreTokens()){
		 for(Dispatcher d:WebApp.dispatchers) {
		 if(d.dispatch(preq)){
		 break
		 }
		 }
		 }*/
	}

	//------------------- navigating methods -------------------
	/**
	 * recursive method for navigation
	 */
	private tryNavigate(instance, IParsleyRequest preq, IParsleyResponse pres){
		preq.tokenizedUrl
	}


	//------------------- resource process -------------------
	private static class LocalizedResourceSelector extends Closure<URL>{
		static URL selectByLocale(String name, Locale locale){

		}
	}

	private URL getResource(String name){
		//		TODO sys var?
		if(Boolean.getBoolean(".parsleyNoCache")){
			_context.getResource(name)
		}

		if(WebApp.resources){
			//TODO no get?
			WebApp.resources.get(name)
		}else{
			_context.getResource(name)
		}
	}

	//------------------- index page process -------------------
	private URL getIndexPage(){
		def indexPagePath="/WEB-INF/${WebApp.RESOURCE_DIR}/"

		for(page in INDEX_PAGES){
			if(getResource(indexPagePath+page))
				break
		}
	}

	//TODO make unmodified: as ? or ?
	private static final Collection<String> INDEX_PAGES=Collections.singletonList('index.html','index.htm','index.ftl','index.jsp')

}
