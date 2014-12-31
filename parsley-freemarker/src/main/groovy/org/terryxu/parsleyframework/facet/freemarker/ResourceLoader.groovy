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

import org.terryxu.parsleyframework.core.WebApp
import org.terryxu.parsleyframework.facet.freemarker.util.JarUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import freemarker.cache.ClassTemplateLoader
import freemarker.cache.FileTemplateLoader
import freemarker.cache.MultiTemplateLoader
import freemarker.cache.TemplateLoader
import freemarker.template.Configuration

/**
 *
 * @author Xu Lijia
 */
final class ResourceLoader {

	private static final Logger LOGGER=LoggerFactory.getLogger(ResourceLoader)

	private static boolean _isInitialized
	static Configuration conf

	static void loadResources(){
		if(!_isInitialized){
			//TODO error on Linux
			//			def url=ResourceLoader.getProtectionDomain().getCodeSource().getLocation()
			//			def jarFile=new File(url.toURI())
			//
			//			JarUtils.decompress(jarFile, WebApp.RESOURCE_PATH)

			conf=new Configuration()
			conf.setDefaultEncoding('UTF-8')

			def clsLoader=new ClassTemplateLoader(FreemarkerFacet.class, '/components')
			def resLoader=new FileTemplateLoader(new File(WebApp.RESOURCE_PATH))
			TemplateLoader[]loaders=[clsLoader, resLoader] as TemplateLoader[]
			def multiLoaders=new MultiTemplateLoader(loaders)
			conf.setTemplateLoader(multiLoaders)

			_isInitialized=true
		}
	}
}
