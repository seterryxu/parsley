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

import javax.servlet.http.HttpServletResponse

/**
 *
 * @author Xu Lijia
 */
class HttpResponseFactory {

	//------------------- http exceptions -------------------
	/**
	 * Error 500 INTERNAL_SERVER_ERROR
	 */
	static void error(String errorMsg, IParsleyResponse pres){
		_status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, errorMsg).generateResponse(pres)
	}

	/**
	 * Error 404 NOT_FOUND
	 */
	static void notFound(String errorMsg, IParsleyResponse pres){
		_status(HttpServletResponse.SC_NOT_FOUND, errorMsg).generateResponse(pres)
	}

	/**
	 * Error 403 FORBIDDEN
	 */
	static void forbidden(String errorMsg, IParsleyResponse pres){
		_status(HttpServletResponse.SC_FORBIDDEN, errorMsg).generateResponse(pres)
	}

	private static HttpResponseException _status(int statusCode, String errorMsg){
		new HttpResponseException(errorMsg){
			void generateResponse(IParsleyResponse pres){
				pres.setStatus(statusCode)
				
				pres.setContentType('text/html;charset=UTF-8')
				pres.getWriter().println "<h2>Error occurred.</h2> Reason: $errorMsg <hr/><I>Powered by Parsley</I>"
			}
		}
	}

	//------------------- http page responses -------------------
	static void staticResource(IParsleyRequest preq, IParsleyResponse pres){
		new StaticResourceResponse(preq).generateResponse(pres)
	}

}
