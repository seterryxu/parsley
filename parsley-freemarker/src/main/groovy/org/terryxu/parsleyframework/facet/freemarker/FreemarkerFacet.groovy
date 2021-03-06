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

package org.terryxu.parsleyframework.facet.freemarker

import static org.terryxu.parsleyframework.facet.freemarker.ResourceLoader.*

import javax.servlet.RequestDispatcher;

import org.terryxu.parsleyframework.core.Facet
import org.terryxu.parsleyframework.core.WebApp
import org.terryxu.parsleyframework.core.uom.IParsleyRequest
import org.terryxu.parsleyframework.core.uom.IParsleyResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import freemarker.template.Template

/**
 *
 * @author Xu Lijia
 */
class FreemarkerFacet extends Facet{

	private static final Logger LOGGER=LoggerFactory.getLogger(FreemarkerFacet)

	private Template _t

	private static _ext

	@Override
	boolean handle(instance, IParsleyRequest preq, IParsleyResponse pres){
		def resName=preq.requestedResource
		LOGGER.debug("Try handling '${resName}'...")

		_initExtName()

		LOGGER.debug("Initializing required resources...")
		loadResources()

		if(instance!=null&&instance.metaClass.respondsTo(instance, 'do$Self', IParsleyRequest, IParsleyResponse)){
			LOGGER.debug("Handling '${resName}' yourself...")
			instance.'do$Self'(preq, pres)
			return true
		}else{
			def templateName
			try{
				if(!resName){
					// is index.ftl
					templateName="index$_ext"
				}else if(resName.endsWith('/')){
					templateName=resName+'index'+_ext
				}else{
					templateName=resName+_ext
				}
				_t=conf.getTemplate(templateName)

				LOGGER.debug("Template '$templateName' found.")
			}catch(FileNotFoundException e){
				LOGGER.debug("Template '$templateName' not found. Handling completed.")
				return false
			}
		}


		def root=[:]
		root.put('it', instance!=null?instance:new Object())
		root.put("contextPath", WebApp.CONTEXT_PATH)

		pres.setContentType('text/html;charset=UTF-8')

		LOGGER.debug("Generating web page...")
		_t.process(root, pres.getWriter())
		LOGGER.debug("Handling completed.")

		return true
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
		exts.add('.ftl')
		return exts
	}

	@Override
	public RequestDispatcher createRequestDispatcher(String pageName) {
		
		// TODO Auto-generated method stub
		return null;
	}
}
