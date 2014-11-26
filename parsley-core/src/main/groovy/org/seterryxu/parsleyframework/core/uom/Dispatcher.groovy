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

package org.seterryxu.parsleyframework.core.uom

import org.seterryxu.parsleyframework.core.WebApp;
import org.seterryxu.parsleyframework.core.util.StringUtils;


/**
 * @author Xu Lijia
 *
 */
abstract class Dispatcher {

	private static boolean TRACE=Boolean.getBoolean('Parsley.trace')

	boolean traceable(){
		TRACE
	}

	static void addDispatchers(Class c){
		if(!WebApp.dispatchers.contains(c)){
			c.metaClass.fallback={
				if(it)
				it.call()
			}
			
			c.metaClass.methodMissing={String name,args->
				def n=StringUtils.camelize(name)
				if(c.metaClass.respondsTo(c,"js$n")){
					return invokeMethod("js$n", args)
				}

				if(c.metaClass.respondsTo(c,"get$n")){
					return invokeMethod("get$n", args)
				}

				if(c.metaClass.respondsTo(c,"do$n")){
					return invokeMethod("do$n", args)
				}

				if(c.metaClass.respondsTo(c,'do$Self')){
					return invokeMethod('do$Self', args)
				}

				fallback()
			}

			c.metaClass.propertyMissing={String name->
				if(hasProperty(name)){
					println 'has $name'
				}
			}

			WebApp.dispatchers.add(c)
		}
	}

	void dispatch(instance,IParsleyRequest preq,IParsleyResponse pres){
		// consume url tokens
		if(!pres.tokenizedUrl.hasMore()){

		}
		// do dispatch
		// before doing dispatch
		String classname=instance.class.name



	}

}
