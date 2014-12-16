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

package org.seterryxu.parsleyframework.facet.groovy

import java.util.Set

import org.seterryxu.parsleyframework.core.Facet
import org.seterryxu.parsleyframework.core.WebApp;
import org.seterryxu.parsleyframework.core.uom.IParsleyRequest
import org.seterryxu.parsleyframework.core.uom.IParsleyResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * @author Xu Lijia
 *
 */
class GroovyFacet extends Facet {

	private static final Logger LOGGER=LoggerFactory.getLogger(GroovyFacet)

	private static boolean _isInitialized
	private static _ext

	@Override
	public boolean handle(Object instance, IParsleyRequest preq,
			IParsleyResponse pres) {
		if(!_isInitialized){
			for(e in allowedExtensions()) {
				_ext=e
			}

			_isInitialized=true
		}

		def resName=WebApp.RESOURCE_FOLDER+preq.requestedResource.substring(1)+'/index'+_ext
		def resUrl=WebApp.resources.getByName(resName)
		if(resUrl){
			ParsleyScriptInvoker invoker=new ParsleyScriptInvoker(resUrl)
			invoker.invoke()
			return true
		}
		
		return false
	}

	@Override
	Set<String> allowedExtensions() {
		def exts=new HashSet<String>()
		exts.add('.groovy')
		return exts
	}
}
