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

	Namespace namespace(String ns){
		new Namespace(ns)
	}

	//------------------- handler methods -------------------
	def methodMissing(String name,args){
		LOGGER.debug("Calling method: name $name, args $args")

		def t=_getTemplate(name)
		if(t){
			if(!_w){
				_w=new StringWriter()
				_doImport('imp_bootstrap')
			}

			_parseArguments(name, args)

			runScript(new Template(null, _w.toString(), conf))
		}else{
			def errorMsg="No such lib: $name, $args"
			LOGGER.error(errorMsg)
			//		TODO ex type
			throw new Exception(errorMsg)
		}
	}

	private _getTemplate(String name){
		conf.getTemplate("${name}.ftl")
	}

	private void _doImport(name){
		//		TODO add line separator
		_w.append("<#include \"/${name}.ftl\">")
	}

	private void _parseArguments(name, args){
		//judge what parameters we have
		Map argz
		Closure closure

		switch(args.size()){
			case 0:break
			case 1:
				if(args[0] instanceof Map){
					argz=args[0]
				}else if(args[0] instanceof Closure){
					closure=args[0]
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

		_createTemplate(name, argz, closure)
	}

	private void _createTemplate(name, args, closure){
		_doImport(name)
		_w.append("<@$name")

		if(args){
			for(def k in args.keySet()){
				def v=args.get(k)
				_w.append(" $k=\"${v}\"")
			}
			
			_w.append(">")
		}else{
			_w.append(">")
		}

		if(closure){
			closure()
		}

		_w.append("</@$name>")
	}

	private void runScript(template){
		def root=[:]
		//		TODO add some properties later
		println template
		template.process(root,writer)
	}

}
