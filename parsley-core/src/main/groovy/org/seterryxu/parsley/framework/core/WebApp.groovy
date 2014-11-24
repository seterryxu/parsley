/*
 * Copyright (c) 2012-2014, Xu Lijia
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

package org.seterryxu.parsley.framework.core

import java.net.URL;
import java.util.logging.Logger

import javax.servlet.ServletContext

import org.seterryxu.parsley.framework.core.lang.facets.Facet

/**
 *
 * @author Xu Lijia
 */
final class WebApp {

//	private static Logger _logger=Logger.getLogger(WebApp.class.name)

	//------------------- web content meta-data -------------------
	static final Set<String> supportedEncodings
	static final Set<String> supportedMimeTypes
	static String RESOURCE_FOLDER
	static WebResource resources

	//------------------- action dispatchers -------------------
	static final List<String> dispatchers=[]
	private static Map<String,URL> _classnames=[:]
	private Set<String> duplicatedClassNames=new HashSet<String>()

	//------------------- app name -------------------
	private static String _appName
	private static Class _rootClass

	private static ServletContext _context

	//TODO singleton
	static void init(ServletContext context){
		this._context=context
		_initResourceFolder()
		_initEncodings()
		_initMimeTypes()
		_generateResourcePaths()
		_initApp()
		_generatePageHandlers()
	}

	private static void _initResourceFolder(){
		RESOURCE_FOLDER=_context.getInitParameter('RESOURCE_FOLDER')?:'/WEB-INF/resource-files/'
	}

	//TODO
	private static void _initEncodings(){

	}

	//TODO
	private static void _initMimeTypes(){

	}

	private static void _generateResourcePaths(){
		//		TODO how to set vars?
		if (Boolean.getBoolean(".noResourcePathCache")) {
			resources = null
			return
		}

		Map<String,URL> paths = new HashMap<String,URL>()
		Stack<String> q = new Stack<String>()
		q.push("/")
		while (!q.isEmpty()) {
			String folder = q.pop()
			Set<String> content = _context.getResourcePaths(folder)
			if (content) {
				for (String c : content) {
					if (c.endsWith("/"))
						q.push(c)
					else {
						URL v = _context.getResource(c)
						if (!v) {
							resources = null
							return
						}
						paths.put(c, v)
					}
				}
			}
		}

		resources=new WebResource(paths)
	}

	private static final class WebResource{
		private static Map<String, URL> _resources

		WebResource(resourcePaths){
			this._resources=Collections.unmodifiableMap(resourcePaths)
		}

		URL get(String name){
			//		TODO sys var?
			if(Boolean.getBoolean(".parsleyNoCache")){
				_context.getResource(name)
			}
	
			if(_resources){
				_resources.get(name)
			}else{
				_context.getResource(name)
			}
		
		}

		List<URL> filterByFolder(String folderName){
			def l=[]
			for(resource in _resources.values()){
				if(resource.toExternalForm().contains("/${folderName}/")){
					l<<resource
				}
			}

			return l
		}

		Map<String,URL> filterWebResources(){
			def m=[:]
			for(key in _resources.keySet()){
				if(key.contains(WebApp.RESOURCE_FOLDER)){
					m.put(key, _resources.get(key))
				}
			}

			return m
		}

		Map<String,URL> filterClasses(){
			def m=[:]
			for(k in _resources.keySet()){
				if(k.endsWith('.class')){
					m.put(k, _resources.get(k))
				}
			}

			return m
		}
		
	}

	//------------------- init app -------------------
	private static void _initApp(){
		_appName=_context.getInitParameter('APP')
		//		TODO check good name?
		if(_appName){
			_rootClass=Class.forName(_appName)
			return
		}

		_rootClass=WebApp.class.name
		_generateClassList()
	}
	
	//------------------- page facet handlers -------------------
	private static void _generatePageHandlers(){
		Facet.discoverExtensions(resources.filterByFolder('lib'))
	}

	//------------------- instantiate object instances -------------------
	private static void _generateClassList(){
		_classnames=resources.filterClasses()
		//		TODO check class name conflicts at compile time?

	}
	
	//	TODO check good name
	//	TODO check duplicated name
	static Class get(String classname){
		if(_appName){
			return _rootClass
		}
		
		if(_classnames&&classname){
			def classUrl=_classnames.get(classname)
			URL[] u=new URL[1]
			u[0]=classUrl
			URLClassLoader ldr=URLClassLoader.newInstance(u)
			return ldr.loadClass(classname)
		}

		return null
	}
	
	//------------------- clean up before shutdown -------------------
	static void cleanUp(){

	}
	
}
