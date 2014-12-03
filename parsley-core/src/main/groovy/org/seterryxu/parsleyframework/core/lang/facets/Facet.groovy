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

package org.seterryxu.parsleyframework.core.lang.facets

import java.util.logging.Logger
import java.util.Set

import org.apache.commons.discovery.tools.Service;
import org.seterryxu.parsleyframework.core.WebApp
import org.seterryxu.parsleyframework.core.uom.IParsleyRequest
import org.seterryxu.parsleyframework.core.uom.IParsleyResponse

/**
 * @author Xu Lijia
 *
 */
abstract class Facet {

	private static Logger _logger=Logger.getLogger Facet.class.name

	private static final List<Facet> facets=[]

	//	TODO how to discover extensions?
	//	TODO how to disable inheritance
	static final List<Facet> lookupFacets(){
		def facetImpls=Service.providers(this.class)
		
		for(f in facetImpls){
			facets.add(f.class)
		}
	}

	//------------------- all facets should maintain localized resources -------------------
	
	//	TODO where to place this class?
	protected static class LocalizedResourcesSelector{
		protected static final Map<String,URL> localizedResources

		static{
			if(WebApp.resources){
				localizedResources=WebApp.resources.filterWebResources()
			}
		}

		static URL selectByLocale(String name, Locale locale){
			if(localizedResources){
				if(name.equals('/')){
					return localizedResources
				}

				int i=name.lastIndexOf('.')
				if(i>0){
					def filename=name.substring(0, i)
					def extname=name.subSequence(i)
					def lang=locale.getLanguage()
					def country=locale.getCountry()
					return localizedResources.get("${filename}_${lang}_${country}.${extname}")
				}

				return null
			}

			return null
		}
	}

	//------------------- handlers for subclasses to implement -------------------

	abstract boolean handle(instance,IParsleyRequest preq,IParsleyResponse pres)

	abstract Set<String> allowedExtensions()
}
