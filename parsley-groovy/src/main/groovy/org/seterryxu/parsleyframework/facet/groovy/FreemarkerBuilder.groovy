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

import org.slf4j.Logger
import org.slf4j.LoggerFactory

import freemarker.template.Configuration
import freemarker.template.Template

/**
 * DSL builder for Freemarker templates.
 * 
 * @author Xu Lijia
 */
class FreemarkerBuilder {

	private static final Logger LOGGER=LoggerFactory.getLogger(FreemarkerBuilder)

	private _instance
	private Configuration _conf
	private Writer _writer

	FreemarkerBuilder(instance, conf, writer){
		this._instance=instance
		this._conf=conf
		this._writer=writer
	}

	private StringWriter _templateBuilder

	private Stack<String> _directiveStack=new Stack<String>()

	def namespace(Class lib){
		def a=lib.getAnnotation(LibUri) as LibUri
		if(!a){
			def errorMsg="'$lib' is not a valid namespace."
			LOGGER.error(errorMsg)
			throw new IllegalArgumentException(errorMsg)
		}

		lib.metaClass.methodMissing=this.&methodMissing
	}

	//	TODO ns <-> cls_ns
	Namespace namespace(String ns){
		new Namespace(ns)
	}


	def methodMissing(String name,args){
		LOGGER.debug("Calling '$name' with '$args'")

		if(!_templateBuilder){
			_templateBuilder=new StringWriter()
			_createTemplate('@imp_bootstrap', null, null, null)
		}

		_parseArguments(name, args)

		while(!_directiveStack.isEmpty()){
			def n=_directiveStack.pop()
			_templateBuilder.append("</$n>")
		}

		_runScript(new Template(null, _templateBuilder.toString(), _conf))

		_clearBuilder()
	}

	private void _parseArguments(name, args){
		//judge what parameters we have
		Map argz
		Closure closure
		String innerText

		switch(args.size()){
			case 0:break
			case 1:
				if(args[0] instanceof Map){
					argz=args[0]
				}else if(args[0] instanceof Closure){
					closure=args[0]
				}else{
					innerText=args[0]
				}
				break
			case 2:
				if(args[0] instanceof Map&&args[1] instanceof Closure){
					argz=args[0]
					closure=args[1]
				}else{
					throw new MissingMethodException(name, getClass(), args)
				}
				break

			default:
				throw new MissingMethodException(name, getClass(), args)
		}

		def t=_getTemplate(name)
		if(t){
			def name0="@$name"
			_directiveStack.push(name0)
			_createTemplate(name0, argz, closure, innerText)
		}else{
			_directiveStack.push(name)
			_createTemplate(name, argz, closure, innerText)
		}

	}

	private _getTemplate(String name){
		try{
			_conf.getTemplate("${name}.ftl")
		}catch(FileNotFoundException e){
			null
		}
	}

	private void _createTemplate(String name, Map args, closure, innerText){
		if(name.startsWith('@')){
			_doImport(name.substring(1))
		}

		_templateBuilder.append("<$name")

		if(args){
			for(def k in args.keySet()){
				def v=args.get(k)
				_templateBuilder.append(" $k=\"${v}\"")
			}
		}
		_templateBuilder.append(">")

		if(closure){
			def result=closure()
			if(result){
				_templateBuilder.append(result)
			}
		}

		if(innerText){
			_templateBuilder.append(innerText)
		}

		_templateBuilder.append("</$name>")

		if(!_directiveStack.isEmpty()){
			_directiveStack.pop()
		}
	}

	private void _doImport(name){
		_templateBuilder.append("<#include \"/${name}.ftl\">").append(LINE_SEPARATOR)
	}

	private void _runScript(Template t){
		def root=[:]
		root.put('instance', _instance?:new Object()) // TODO 'it' is a key word

		LOGGER.debug('Template String: '+t.toString())
		t.process(root,_writer)
	}

	private void _clearBuilder(){
		_templateBuilder.getBuffer().setLength(0)
	}

	private static final LINE_SEPARATOR=File.pathSeparator==';'?'\r\n':'\n'

}
