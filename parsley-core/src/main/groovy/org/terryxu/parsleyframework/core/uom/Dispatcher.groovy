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

package org.terryxu.parsleyframework.core.uom

import static org.terryxu.parsleyframework.core.util.ResourceUtils.*

import org.terryxu.parsleyframework.core.WebApp
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 *
 * @author Xu Lijia
 */
abstract class Dispatcher {

	private static final Logger LOGGER=LoggerFactory.getLogger(Dispatcher)

	// TODO how to get this var?
	private static boolean TRACE=Boolean.getBoolean('Parsley.trace')

	boolean traceable(){
		TRACE
	}

	static void addDispatchers(Class c){
		if(!WebApp.DISPATCHERS.contains(c)){
			LOGGER.debug("Adding a dispatcher for $c")

			c.metaClass.invokeMethod={String name,args->
				//check if this class has a customized navigation method
				//if does, invoke this method and ignore others
				if(c.metaClass.respondsTo(c, 'do$Self', IParsleyRequest, IParsleyResponse)){
					return invokeMethod('do$Self', args)
				}
				
				def name0=capitalFirst(name)

				//check if this class has a parsley action method
				if(c.metaClass.respondsTo(c, "do$name0", IParsleyRequest, IParsleyResponse)){
					return invokeMethod("do$name0", args)
				}

				//check if this class has a simple getter
				if(c.metaClass.respondsTo(c, "get$name0")){
					return invokeMethod("get$name0", args)
				}
				
				//TODO should have multiple getters
				
				//				if(c.metaClass.respondsTo(c,"js$name0")){
				//					return invokeMethod("js$name0", args)
				//				}
				//
				
				throw new MissingMethodException(name0, c, args)
			}

			WebApp.DISPATCHERS<<c
		}
	}

}
