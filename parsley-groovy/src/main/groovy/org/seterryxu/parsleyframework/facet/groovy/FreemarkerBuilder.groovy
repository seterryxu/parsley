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
 *
 * @author Xu Lijia
 */
class FreemarkerBuilder {

	private static final Logger LOGGER=LoggerFactory.getLogger(FreemarkerBuilder)

	Configuration conf

	Writer writer

	private StringWriter _w

	private Stack<String> _stack=new Stack<String>()

	//------------------- namespace methods -------------------
	def namespace(Class lib){
		def a=lib.getAnnotation(LibUri) as LibUri
		if(!a){
			def errorMsg="$lib is not a valid namespace."
			LOGGER.error(errorMsg)
			//	TODO ex type
			throw new Exception(errorMsg)
		}

		lib.metaClass.invokeMethod=this.&invokeMethod
	}

	//	TODO ns <-> cls_ns
	Namespace namespace(String ns){
		new Namespace(ns)
	}

	//------------------- handler methods -------------------
	def methodMissing(String name,args){
		LOGGER.debug("Calling method: name $name, args $args")

		if(!_w){
			_w=new StringWriter()
			_doImport('imp_bootstrap')
			_w.append("<@imp_bootstrap/>")
		}

		_parseArguments(name, args)

		while(!_stack.isEmpty()){
			def n=_stack.pop()
			_w.append("</$n>")
		}
		
		_runScript(new Template(null, _w.toString(), conf))
	}

	private void _doImport(name){
		//	TODO add line separator ?
		_w.append("<#include \"/${name}.ftl\">")
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
				}
				break

			default:
			//			TODO how to get class?
				throw new MissingMethodException(name, getClass(), args)
		}

		def t=_getTemplate(name)
		if(t){
			def name0="@$name"
			_stack.push(name0)
			_createTemplate(name0, argz, closure, innerText)
		}else{
			_stack.push(name)
			_createTemplate(name, argz, closure, innerText)
		}

	}

	private _getTemplate(String name){
		try{
			conf.getTemplate("${name}.ftl")
		}catch(FileNotFoundException e){
			null
		}
	}

	private void _createTemplate(String name, Map args, closure, innerText){
		if(name.startsWith('@')){
			_doImport(name.substring(1))
		}

		_w.append("<$name")

		if(args){
			for(def k in args.keySet()){
				def v=args.get(k)
				_w.append(" $k=\"${v}\"")
			}
		}
		_w.append(">")

		if(closure){
			_w.append(closure())
		}
		
		if(innerText){
			_w.append(innerText)
		}

		_w.append("</$name>")
		if(!_stack.isEmpty()){
			_stack.pop()
		}
	}

	private void _runScript(Template t){
		def root=[:]
		//	TODO add some properties later
		LOGGER.debug(t.toString())
		t.process(root,writer)
	}

}
