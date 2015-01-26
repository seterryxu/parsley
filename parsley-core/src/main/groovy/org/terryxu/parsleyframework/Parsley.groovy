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

package org.terryxu.parsleyframework

import static org.terryxu.parsleyframework.core.util.ResourceUtils.*

import javax.servlet.ServletConfig
import javax.servlet.ServletContext
import javax.servlet.ServletException
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

import org.terryxu.parsleyframework.core.Facet
import org.terryxu.parsleyframework.core.WebApp
import org.terryxu.parsleyframework.core.uom.Dispatcher
import org.terryxu.parsleyframework.core.uom.HttpResponseFactory
import org.terryxu.parsleyframework.core.uom.IParsleyRequest
import org.terryxu.parsleyframework.core.uom.IParsleyResponse
import org.terryxu.parsleyframework.core.uom.ParsleyRequestSupport
import org.terryxu.parsleyframework.core.uom.ParsleyResponseSupport
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 *
 * @author Xu Lijia
 */
final class Parsley extends HttpServlet {

	private static final Logger LOGGER=LoggerFactory.getLogger(Parsley)

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config)
	}

	@Override
	protected void service(HttpServletRequest req, HttpServletResponse res)
	throws ServletException, IOException {
		//wrapper
		IParsleyRequest preq=new ParsleyRequestSupport(req)
		IParsleyResponse pres=new ParsleyResponseSupport(res, this)
		LOGGER.debug('Parsley wrapper instantiated.')

		_service(preq, pres)

		LOGGER.info("Parsley Request handling completed.")
	}

	private void _service(IParsleyRequest preq, IParsleyResponse pres){
		def resName=preq.requestedResource
		def type=_checkParsleyRequestType(resName)
		LOGGER.debug("Parsley Requested resource: ${resName?:'Web Root'}, type: $type")

		switch(type){
			case PARSLEY_REQ_TYPE.STATIC:
				HttpResponseFactory.staticResource(preq, pres)
				break

			case PARSLEY_REQ_TYPE.REST:
				println 'REST calls to be implemented.'
				break

			case PARSLEY_REQ_TYPE.JS:
				println 'JS calls to be implemented.'
				break

			case PARSLEY_REQ_TYPE.UNKNOWN:
				def instance=_instantiate(preq)

				if(instance!=null){
					//try actions
					if(_navigate(instance, preq, pres)){
						return
					}
				}

			//try facets
				for(facet in Facet.FACETS){
					if(facet.newInstance().handle(instance,preq,pres)){
						return
					}
				}

			default:
			//out of options
				def errorMsg="No such resource: $resName"
				LOGGER.error(errorMsg)
				HttpResponseFactory.notFound(errorMsg, pres)
		}
	}

	//------------------- Parsley request types -------------------
	private static final enum PARSLEY_REQ_TYPE{
		STATIC, REST, JS, UNKNOWN
	}

	private _checkParsleyRequestType(String resName){
		if(isStaticResource(resName)){
			return PARSLEY_REQ_TYPE.STATIC
		}

		//TODO
		if(resName.startsWith('/$REST/')){
			return PARSLEY_REQ_TYPE.REST
		}

		//TODO
		if(resName.startsWith('/$JS/')){
			return PARSLEY_REQ_TYPE.JS
		}

		PARSLEY_REQ_TYPE.UNKNOWN
	}

	//------------------- instance locating methods -------------------
	private _instantiate(preq){
		String root=preq.resourceTokens.current()
		def rootClass=WebApp.getClazz(root)
		if(!rootClass){
			return null
		}

		Dispatcher.addDispatchers(rootClass)

		rootClass.newInstance()
	}

	//------------------- navigating methods -------------------
	// TODO optimize recursive invocations
	private boolean _navigate(instance, IParsleyRequest preq, IParsleyResponse pres){
		//check the number of remaining arguments
		//and decide which method to call
		while(preq.resourceTokens.remainingCount()>1){
			String tk=preq.resourceTokens.next()
			instance=instance."$token"()
		}

		String tk=preq.resourceTokens.next()
		if(!tk){
			try{
				instance.do$Self(preq, pres)
				return true
			}catch(MissingMethodException e){
				try{
					instance.doIndex(preq, pres)
					return true
				}catch(MissingMethodException e2){
					return false
				}
			}
		}else{
			instance."$tk"(preq, pres)
		}
	}

	def doForward(IParsleyRequest req, String url, IParsleyResponse res){
		def preq=new ParsleyRequestSupport(req, url)
		_service(preq, res)
	}

}
