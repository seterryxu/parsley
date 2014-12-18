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

package org.seterryxu.parsleyframework.core

import static org.seterryxu.parsleyframework.core.util.ResourceUtils.*

import javax.servlet.ServletContext

import org.seterryxu.parsleyframework.core.util.ResourceUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 *
 * @author Xu Lijia
 */
//TODO make singleton?
final class WebApp {

	private static final Logger LOGGER=LoggerFactory.getLogger(WebApp)

	//------------------- web content meta-data -------------------
	static final Set<String> SUPPORTED_ENCODINGS
	static final Set<String> SUPPORTED_MIMETYPES

	static String WEB_ROOT_PATH
	static String RESOURCE_FOLDER
	static String RESOURCE_PATH
	static WebResource resources

	//------------------- action DISPATCHERS -------------------
	static final List<String> DISPATCHERS=[]
	private static ClassUrlMap _map=new ClassUrlMap()

	//------------------- app name -------------------
	private static String _appName
	private static Class _rootClass


	private static ServletContext _context

	static void init(ServletContext context){
		this._context=context

		_initWebRootPath()
		_initSystemProperties()
		_initResourceFolder()
		_initEncodings()
		_initMimeTypes()
		_generateResourcePaths()
		_initApp()
		_generatePageHandlers()
	}

	private static void _initWebRootPath(){
		WEB_ROOT_PATH=_context.getRealPath("/")
	}

	private static void _initSystemProperties(){
		//	TODO how to LOAD global vars?

	}

	private static void _initResourceFolder(){
		RESOURCE_FOLDER=_context.getInitParameter('RESOURCE_FOLDER')?:'WEB-INF/resource-files/'
		RESOURCE_PATH=WEB_ROOT_PATH+RESOURCE_FOLDER
	}

	//TODO
	private static void _initEncodings(){

	}

	//TODO
	private static void _initMimeTypes(){

	}

	private static void _generateResourcePaths(){
		//	TODO how to set vars?
		if (Boolean.getBoolean('Parsley.noResourcePathCache')) {
			resources = null
			return
		}

		def paths = new HashMap<String,URL>()
		def stack = new Stack<String>()
		stack.push("/")
		while (!stack.isEmpty()) {
			String folder = stack.pop()
			Set<String> content = _context.getResourcePaths(folder)
			if (content) {
				for (String c : content) {
					if (c.endsWith("/"))
						stack.push(c)
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

	private static void _initApp(){
		_appName=_context.getInitParameter('APP')
		//	TODO check good name?
		if(_appName){
			_rootClass=Class.forName(_appName)
			return
		}

		_rootClass=WebApp
		_generateClassList()
	}

	private static void _generatePageHandlers(){
		Facet.lookupFacets()
	}

	private static final class WebResource{
		private static Map<String, URL> _resources

		WebResource(resourcePaths){
			this._resources=Collections.unmodifiableMap(resourcePaths)
		}

		URL getByName(String name){
			//TODO sys var?
			if(Boolean.getBoolean('Parsley.noResourcePathCache')){
				return _context.getResource(name)
			}

			if(_resources){
				def resUrl=_resources.get(name)
				if(resUrl){
					return resUrl
				}else{
					return _context.getResource(name)
				}
			}else{
				_context.getResource(name)
			}
		}

		// TODO duck-typing style filtering ?
		Map<String,URL> filterPageResources(){
			def m=[:]
			for(key in _resources.keySet()){
				if(key.contains(WebApp.RESOURCE_FOLDER)){
					m.put(key, _resources.get(key))
				}
			}

			m
		}

		// TODO a decent way to filter classes that belong to current web app?
		void filterClasses(){
			for(k in _resources.keySet()){
				if(k.endsWith('.class')){
					int i=k.lastIndexOf('.')
					def k1=k.substring(0, i)
					int i2=k1.lastIndexOf('/')
					def k2=k1.substring(i2+1)
					int i3=k1.lastIndexOf('classes/')
					def k3=k1.substring(i3+8)

					WebApp._map.add(k3, k2, _resources.get(k))
				}
			}
		}
	}

	private static final class ClassUrlMap{
		private class MapInfo{
			String qualifiedName
			String simpleName
			URL classUrl
		}

		private List<MapInfo> _mapInfo=[]

		boolean contains(String simpleName){
			for(c in _mapInfo){
				if(c.simpleName.equals(simpleName)){
					return true
				}
			}

			false
		}

		void add(String qualifiedName, String simpleName, URL classUrl){
			def c=new MapInfo()
			c.qualifiedName=qualifiedName
			c.simpleName=simpleName
			c.classUrl=classUrl
			_mapInfo.add(c)
		}

		String getQualifiedName(String simpleName){
			for(c in _mapInfo){
				if(c.simpleName.equals(simpleName)){
					return c.qualifiedName
				}
			}

			null
		}

		URL getUrl(String simpleName){
			for(c in _mapInfo){
				if(c.simpleName.equals(simpleName)){
					return c.classUrl
				}
			}

			null
		}
	}

	private static void _generateClassList(){
		//	TODO check simple class name conflicts at compile time?
		resources.filterClasses()
	}

	//	TODO check good name
	//	TODO check duplicated name
	static Class getClazz(String token){
		if(_appName){
			return _rootClass
		}

		if(token){
			if(_map.contains(token)){
				def classname=capitalFirst(token)
				def classUrl=_map.getUrl(classname)
				if(classUrl){
					return Class.forName(_map.getQualifiedName(classname))
				}
			}
		}

		null
	}

	//------------------- clean up before shutdown -------------------
	static void cleanUp(){
		//TODO add some clean up tasks
		
	}

}
