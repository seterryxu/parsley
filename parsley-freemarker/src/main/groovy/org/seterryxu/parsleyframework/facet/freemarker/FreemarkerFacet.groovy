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

package org.seterryxu.parsleyframework.facet.freemarker

import org.seterryxu.parsleyframework.core.Facet;
import org.seterryxu.parsleyframework.core.WebApp
import org.seterryxu.parsleyframework.core.uom.IParsleyRequest
import org.seterryxu.parsleyframework.core.uom.IParsleyResponse
import org.seterryxu.parsleyframework.facet.freemarker.util.JarUtils

import freemarker.cache.ClassTemplateLoader
import freemarker.cache.FileTemplateLoader
import freemarker.cache.MultiTemplateLoader
import freemarker.cache.TemplateLoader
import freemarker.template.Configuration
import freemarker.template.Template

/**
 *
 * @author Xu Lijia
 */
class FreemarkerFacet extends Facet{

	private static Configuration _conf
	private Template _t
	private static boolean _isInitialized

	FreemarkerFacet(){
		_conf=new Configuration()
		_conf.setDefaultEncoding('UTF-8')

		def claLoader=new ClassTemplateLoader(FreemarkerFacet.class, '/components')
		def resLoader=new FileTemplateLoader(new File(WebApp.RESOURCE_FOLDER))
		TemplateLoader[]loaders=[claLoader, resLoader]
		def multiLoaders=new MultiTemplateLoader(loaders)
		_conf.setTemplateLoader(claLoader)
	}

	@Override
	boolean handle(instance,IParsleyRequest preq,IParsleyResponse pres){
		if(!_isInitialized){
			def url=this.class.getProtectionDomain().getCodeSource().getLocation()
			def jarFile=new File(url.toURI())

			JarUtils.decompress(jarFile, preq.getServletContext().getRealPath('/'))
			_isInitialized=true
		}

		def ext
		for(e in allowedExtensions()){
			ext=e
		}

		try{
			_t=_conf.getTemplate(preq.getRequestedResourceName()+'/index'+ext)
		}catch(FileNotFoundException e){

			try{
				_t=_conf.getTemplate(preq.getRequestedResourceName()+ext)
				return true
			}catch(FileNotFoundException e2){
				return false
			}
		}

		def out=pres.getOutputStream()
		def root=[:]
		root.put('it', instance)
		_t.process(root, out)

		return true
	}

	@Override
	Set<String> allowedExtensions() {
		def exts=new HashSet<String>()
		exts.add('.ftl')
		return exts
	}
}
