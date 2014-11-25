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

import org.seterryxu.parsley.framework.core.WebApp
import org.seterryxu.parsley.framework.core.lang.facets.Facet
import org.seterryxu.parsley.framework.core.lang.facets.Facet.LocalizedResourcesSelector

import java.util.Set

/**
 * @author Xu Lijia
 *
 */
class StaticResourceFacet extends Facet {

	@Override
	public boolean handle(instance, IParsleyRequest preq,
			IParsleyResponse pres) {

		if(_isIndexPageRequest(preq)){
			def indexPage=_getIndexPage()
			if(indexPage){
				HttpResponseFactory.indexPage(pres,indexPage)
			}else{
				HttpResponseFactory.notFound(pres)
			}
			
			return true
		}
		
		def resUrl=LocalizedResourcesSelector.selectByLocale(preq.getRequestedResourceName(), preq.getRequestedLocale())

		if(resUrl){
			HttpResponseFactory.staticResource(pres,resUrl)
			return true
		}else{
			HttpResponseFactory.notFound(pres)
		}

		return false
	}

	boolean _isIndexPageRequest(IParsleyRequest preq) {
		if(!preq.tokenizedUrl.hasMore()){
			return true
		}

		return false
	}

	//------------------- index page process -------------------
	private URL _getIndexPage(){
		URL index
		for(ext in allowedExtensions()){
			index=WebApp.resources.get("${WebApp.RESOURCE_FOLDER}index${ext}")
			if(index){
				break
			}
		}

		return index
	}

	@Override
	Set<String> allowedExtensions() {
		def exts=new HashSet<String>()
		exts.add('.html')
		exts.add('.htm')

		return exts
	}

}
