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

package org.terryxu.parsleyframework.facet.groovy

import javax.servlet.RequestDispatcher;

import org.terryxu.parsleyframework.core.Facet
import org.terryxu.parsleyframework.core.WebApp
import org.terryxu.parsleyframework.core.uom.IParsleyRequest
import org.terryxu.parsleyframework.core.uom.IParsleyResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 *
 * @author Xu Lijia
 */
class GroovyFacet extends Facet {

	private static final Logger LOGGER=LoggerFactory.getLogger(GroovyFacet)

	private static _ext

	@Override
	public boolean handle(Object instance, IParsleyRequest preq, IParsleyResponse pres) {
		def resName=preq.requestedResource
		LOGGER.debug("Try handling '${resName}'...")

		_initExtName()

		if(instance!=null&&instance.metaClass.respondsTo(instance, 'do$Self', IParsleyRequest, IParsleyResponse)){
			LOGGER.debug("Handling '${resName}' yourself...")
			instance.'do$Self'(preq, pres)
			return true
		}else{
			def resUrl
			if(!resName){
				resUrl=WebApp.RESOURCES.getByName(WebApp.RESOURCE_FOLDER+"index$_ext")
			}else if(resName.endsWith('/')){
				resUrl=WebApp.RESOURCES.getByName(WebApp.RESOURCE_FOLDER+resName+'index'+_ext)
			}else{
				resUrl=WebApp.RESOURCES.getByName(WebApp.RESOURCE_FOLDER+resName+_ext)
			}
			
			if(resUrl){
				LOGGER.debug("'$resUrl' found. Generating web page...")
				
				pres.setContentType('text/html;charset=UTF-8')
				
				def helper=new ParsleyScriptHelper(instance, resUrl)
				helper.writeTo(pres.getWriter())
				LOGGER.debug("Handling completed.")
				
				return true
			}
		}

		LOGGER.debug("'$resName' not found. Handling completed.")
		false
	}

	private void _initExtName(){
		if(!_ext){
			for(e in allowedExtensions()){
				_ext=e
			}
		}
	}

	@Override
	Set<String> allowedExtensions() {
		def exts=new HashSet<String>()
		exts.add('.groovy')
		return exts
	}

	@Override
	public RequestDispatcher createRequestDispatcher(String pageName) {
		// TODO Auto-generated method stub
		return null
	}
}
