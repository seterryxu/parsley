/*
 * Copyright (c) 2013-2015, Xu Lijia
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

package org.seterryxu.parsleyframework.core

import org.apache.commons.discovery.tools.Service
import org.seterryxu.parsleyframework.core.uom.IParsleyRequest
import org.seterryxu.parsleyframework.core.uom.IParsleyResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.util.Set

/**
 *
 * @author Xu Lijia
 */
abstract class Facet {

	protected static final Logger LOGGER=LoggerFactory.getLogger(Facet)

	static final Set<Facet> FACETS=new HashSet<Facet>()

	static final void lookupFacets(){
		def facets=Service.providers(Facet)

		while(facets.hasMoreElements()){
			def clazz=facets.nextElement().class
			LOGGER.debug("Discovered extension $clazz.")
			FACETS.add(clazz)
		}
	}

	//------------------- all facets should maintain localized resources -------------------
	//	TODO perfect this class
	protected static class LOCALIZED_RESOURCESSelector{
		private static final Map<String,URL> LOCALIZED_RESOURCES

		static{
			if(WebApp.RESOURCES){
				LOCALIZED_RESOURCES=WebApp.RESOURCES.filterPageResources()
			}
		}

		static URL selectByLocale(String name, Locale locale){
			if(LOCALIZED_RESOURCES){
				if(name.equals('/')){
					return null
				}

				int i=name.lastIndexOf('.')
				if(i>0){
					def filename=name.substring(0, i)
					def ext=name.subSequence(i)
					def lang=locale.getLanguage()
					def country=locale.getCountry()
					return LOCALIZED_RESOURCES.get("${filename}_${lang}_${country}.${ext}")
				}

			}

			null
		}
	}

	//------------------- handlers -------------------

	abstract boolean handle(instance,IParsleyRequest preq,IParsleyResponse pres)

	abstract Set<String> allowedExtensions()
}
