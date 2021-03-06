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

import static org.terryxu.parsleyframework.facet.freemarker.ResourceLoader.*

import org.codehaus.groovy.control.CompilerConfiguration
import org.codehaus.groovy.runtime.InvokerHelper
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 *
 * @author Xu Lijia
 */
class ParsleyScriptHelper {

	private static final Logger LOGGER=LoggerFactory.getLogger(ParsleyScriptHelper)

	private _instance
	private URL _scriptUrl

	ParsleyScriptHelper(instance, URL scriptUrl){
		this._instance=instance
		this._scriptUrl=scriptUrl
	}

	void writeTo(Writer writer){
		def loader=_createGroovyClassLoader()
		//	TODO add security constraints?
		def scriptSrc=new GroovyCodeSource(_scriptUrl)
		LOGGER.debug("Script $_scriptUrl loaded.")

		def script=InvokerHelper.createScript(loader.parseClass(scriptSrc), new Binding()) as ParsleyScript
		loadResources()

		def builder=new FreemarkerBuilder(_instance, conf, writer)
		//	TODO why cannot directly "script.delegate=..."
		script.setDelegate(builder)
		script.run()

		LOGGER.debug("Finished running script $_scriptUrl")
	}

	private GroovyClassLoader _createGroovyClassLoader(){
		def config=new CompilerConfiguration()
		config.setScriptBaseClass(ParsleyScript.class.name)
		config.setRecompileGroovySource(false)

		new GroovyClassLoader(this.class.getClassLoader(), config)
	}
}
