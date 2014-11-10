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

package org.seterryxu.parsley.framework.core.uom

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletRequestWrapper

/**
 * @author Xu Lijia
 *
 */
class PRequestImpl extends HttpServletRequestWrapper implements IParsleyRequest {

	private final String requestedUrl
	private final List<String> tokenizedUrl=[]

	PRequestImpl(HttpServletRequest req){
		super(req)
		this.requestedUrl=getRequestedUrl(req)
		tokenizeUrl()
	}

	private String getRequestedUrl(HttpServletRequest req){
		req.getRequestURI().substring(req.getContextPath().size())
	}

	private void tokenizeUrl(){
		StringTokenizer tk=new StringTokenizer(requestedUrl, '/\\')
		tk.each {tokenizedUrl<<it }
	}

	public boolean isRestfulRequest() {
		if(requestedUrl.startsWith('/$Parsley/')){
			return true
		}

		return false
	}

	public boolean isStaticResourceRequest() {
		if(_isValid()&&requestedUrl.contains('/$Static/')){
			return true
		}

		return false
	}

	public boolean isJavaScriptRequest() {
		if(requestedUrl.contains('/$Js/')){
			return true
		}

		return false
	}

	public boolean hasMoreTokens() {
		// TODO Auto-generated method stub
		return false
	}

	private boolean _isValid() {
		String lowerCasedRequestedUrl=requestedUrl.toLowerCase()
		if(lowerCasedRequestedUrl&&!lowerCasedRequestedUrl.startsWith('/meta-inf')&&!lowerCasedRequestedUrl.startsWith('/web-inf')){
			return true
		}

		return false
	}

	public String getRequestedResourceName() {
		// TODO Auto-generated method stub
		return null
	}

	public Locale getRequestedLocale() {
		// TODO Auto-generated method stub
		return null
	}
}
