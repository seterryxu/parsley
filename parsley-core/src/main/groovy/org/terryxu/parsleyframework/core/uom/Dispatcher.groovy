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

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.terryxu.parsleyframework.core.WebApp

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

			c.metaClass.invokeMethod={String name,args->
				LOGGER.debug("Calling method '$name' with '$args' in class '$c' ...")

				if(name=='do$Self'&&c.metaClass.respondsTo(c, 'do$Self', IParsleyRequest, IParsleyResponse)){
					LOGGER.debug("Self-handling method found in class '$c'")
					invokeMethod('do$Self', args)
					return true
				}

				if(name=='doIndex'&&c.metaClass.respondsTo(c, 'doIndex', IParsleyRequest, IParsleyResponse)){
					LOGGER.debug("Default method found in class '$c'")
					invokeMethod('doIndex', args)
					return true
				}

				def name0=capitalFirst(name)

				//TODO has other getters?
				if(args.size()==1){
					if(args[0] instanceof Integer&&c.metaClass.respondsTo(c, "get$name0", int)){
						return invokeMethod("get$name0", args[0])
					}

					if(args[0] instanceof String&&c.metaClass.respondsTo(c, "get$name0", String)){
						return invokeMethod("get$name0", args[0])
					}
				}

				//check if this class has an action method
				if(c.metaClass.respondsTo(c, "do$name0", IParsleyRequest, IParsleyResponse)){
					invokeMethod("do$name0", args)
					return true
				}

				//out of options
				throw new MissingMethodException(name, c, args)
			}

			WebApp.DISPATCHERS<<c
			
			LOGGER.debug("Dispatchers for class '$c' added.")
		}
	}

}
