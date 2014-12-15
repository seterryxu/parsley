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

package org.seterryxu.parsleyframework.core.util

import org.seterryxu.parsleyframework.core.WebApp
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 *
 * @author Xu Lijia
 */
class ResourceUtils {

	private static final Logger LOGGER=LoggerFactory.getLogger(ResourceUtils)

	static String capitalFirst(String token){
		return String.valueOf(Character.toUpperCase(token.charAt(0)))+token.substring(1)
	}

	static boolean isStaticResource(String requestedResource){
		if(getStaticResource(requestedResource)){
			return true
		}else{
			return false
		}
	}

	static URL getStaticResource(String requestedResource){
		def resName=requestedResource.toLowerCase()
		if(resName&&!resName.contains('/meta-inf')&&!resName.contains('/web-inf')){
			if(resName.endsWith('.js')||resName.endsWith('.css')||resName.endsWith('.map')
			||resName.endsWith('.eot')||resName.endsWith('.svg')
			||resName.endsWith('.ttf')||resName.endsWith('.woff')){

				return WebApp.RESOURCE_FOLDER+requestedResource
			}
		}

		//check existence?
		if(resName.endsWith('/')){
			for(ext in STATIC_RESOURCE_EXT){
				def indexPage=WebApp.RESOURCE_FOLDER+(resName.length()==1?'':resName)+'index'+ext
				def pageUrl=WebApp.resources.get(indexPage)
				if(pageUrl){
					LOGGER.debug("$indexPage found.")
					return pageUrl
				}
			}

			null

		}else{//try /xx.htm(l)
			for(ext in STATIC_RESOURCE_EXT){
				def page=WebApp.RESOURCE_FOLDER+trimSlash(resName)+ext
				def pageUrl=WebApp.resources.get(page)
				if(pageUrl){
					LOGGER.debug("$page found.")
					return pageUrl
				}
			}

			null
		}
	}

	static String trimSlash(String str){
		str.substring(1)
	}

	private static final STATIC_RESOURCE_EXT=['.htm', 'html'].asImmutable()

}
