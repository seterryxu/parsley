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
import org.seterryxu.parsley.framework.core.lang.facets.Facet

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

		//judge request type
		if(preq.isStaticResourceRequest()){
			URL resUrl=LocalizedResourcesSelector.selectByLocale(preq.getRequestedResourceName(),preq.getRequestedLocale())
			new StaticResourceFacet(resUrl)

			return
		}

		//		TODO
		if(preq.isRestfulRequest()){
		}

		//		TODO
		if(preq.isJavaScriptRequest()){
		}

		if(_navigate(preq, pres)){
			return
		}

		//		TODO how to handle?
		//static resource facet excluded
		for(facet in Facet.facets){
			if(facet.handle(instance,preq,pres)){
				return
			}
		}

		//out of options
		HttpResponseFactory.notFound(pres)
	}

	//------------------- navigating methods -------------------
	/**
	 * recursive method for navigation
	 */
	private boolean _navigate(IParsleyRequest preq, IParsleyResponse pres){
		def rootClass=_findRootClass(preq)
		if(!rootClass){
			return false
		}

		_generateDispatchers(rootClass)
		
		def instance=rootClass.newInstance()

		//		tryNavigate()
		//		TODO: recursive??
		while(preq.tokenizedUrl.hasMore()){
			preq.tokenizedUrl.next()
			if(tryNavigate(instance,preq,pres))
				break
		}
		
		return true
	}

	private Class _findRootClass(IParsleyRequest preq){
		String root=preq.tokenizedUrl.current()
		WebApp.get(root)
	}

	private void _generateDispatchers(Class root){
		Dispatcher.addDispatchers(root)
	}

	private tryNavigate(instance,IParsleyRequest preq, IParsleyResponse pres){
		
		for(Dispatcher d in WebApp.dispatchers) {
			if(d.dispatch(rootClass,preq,pres)){
				break
			}
		}
	}

	//------------------- resource process -------------------
	private static class LocalizedResourcesSelector{
		private static final Map<String,URL> _localizedResources

		static{
			if(WebApp.resources){
				_localizedResources=WebApp.resources.filterWebResources()
			}
		}

		static URL selectByLocale(String name, Locale locale){
			if(_localizedResources){
				int i=name.lastIndexOf('.')
				if(i>0){
					def filename=name.substring(0, i)
					def extname=name.subSequence(i)
					def lang=locale.getLanguage()
					def country=locale.getCountry()
					return _localizedResources.get("${filename}_${lang}_${country}.${extname}")
				}

				return null
			}

			return null
		}
	}

}
