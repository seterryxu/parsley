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

		if(preq.isIndexPageRequest()){
			def indexPage=_getIndexPage()
			if(indexPage){
				HttpResponseFactory.indexPage(pres,indexPage)
			}else{
				HttpResponseFactory.notFound(pres)
			}

			return
		}

		if(preq.isStaticResourceRequest()){
			URL resUrl=LocalizedResourcesSelector.selectByLocale(preq.getRequestedResourceName(),preq.getRequestedLocale())
			if(resUrl){
				HttpResponseFactory.staticResource(pres,resUrl)
			}else{
				HttpResponseFactory.notFound(pres)
			}

			return
		}

		//		TODO
		if(preq.isRestfulRequest()){
		}

		//		TODO
		if(preq.isJavaScriptRequest()){
		}

		
		//		TODO: lazy loading MetaClass?
		//		TODO: recursive??
		if(tryNavigate(this, preq, pres)){
			return
		}

		for(facet in Facet.facets){
			if(facet.handle(this,preq,pres)){
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
	private boolean tryNavigate(instance, IParsleyRequest preq, IParsleyResponse pres){

		preq.tokenizedUrl
		while(preq.tokenizedUrl.hasMore()){
			for(Dispatcher d:WebApp.dispatchers) {
				if(d.dispatch(preq)){
					break
				}
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

	private URL _getResource(String name){
		//		TODO sys var?
		if(Boolean.getBoolean(".parsleyNoCache")){
			_context.getResource(name)
		}

		if(WebApp.resources){
			WebApp.resources.get(name)
		}else{
			_context.getResource(name)
		}
	}

	//------------------- index page process -------------------
	private URL _getIndexPage(){
		URL index
		for(page in INDEX_PAGES){
			index=_getResource("${WebApp.RESOURCE_FOLDER}${page}")
			if(index){
				break
			}
		}

		return index
	}

	private static final List<String> INDEX_PAGES=['index.html', 'index.htm']

}
