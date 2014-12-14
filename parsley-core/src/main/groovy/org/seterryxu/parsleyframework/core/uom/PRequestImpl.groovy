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

package org.seterryxu.parsleyframework.core.uom

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.seterryxu.parsleyframework.core.WebApp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Xu Lijia
 *
 */
class PRequestImpl extends HttpServletRequestWrapper implements IParsleyRequest {

	private static final Logger LOGGER=LoggerFactory.getLogger(PRequestImpl)
	
	private final String _requestedUrl

	final TokenizedUrl tokenizedUrl

	PRequestImpl(HttpServletRequest req){
		super(req)
		this._requestedUrl=_getRequestedUrl(req)
		tokenizedUrl=new TokenizedUrl()
	}

	//------------------- pre-process url -------------------
	private String _getRequestedUrl(HttpServletRequest req){
		req.getRequestURI().substring(req.getContextPath().size())
	}

	//------------------- tokenize url -------------------
	private class TokenizedUrl{
		//TODO need final?
		private String[] _tokens

		TokenizedUrl(){
			StringTokenizer tk=new StringTokenizer(_requestedUrl, '/\\')
			_tokens=new String[tk.countTokens()]
			//			TODO maybe a better way?
			def i=0
			while(tk.hasMoreTokens()){
				_tokens[i++]=tk.nextToken()
			}
		}

		private int index

		String current(){
			_tokens[index]
		}

		String nextToken(){
			if(hasMore()){
				return _tokens[++index]
			}
			
			return null
		}

		int nextAsInt(){
			_tokens[++index] as int
		}

		public boolean hasMore() {
			index<_tokens.size()-1
		}

		@Override
		public String toString() {
			StringBuilder b=new StringBuilder()
			def i=0
			while(i!=_tokens.size()){
				if(i!=index){
					b.append(_tokens[i])
				}else{
					b.append('!'+_tokens[i])
				}

				i++
			}

			return b.toString()
		}


	}

	//------------------- static resource -------------------
	@Override
	public boolean isStaticResourceRequest() {
		String lowerCasedRequestedUrl=_requestedUrl.toLowerCase()
		if(lowerCasedRequestedUrl
		&&!lowerCasedRequestedUrl.startsWith('/meta-inf')
		&&!lowerCasedRequestedUrl.startsWith('/web-inf')){
			//			TODO check index existence?
			//			TODO check existence?
			def resName=getRequestedResourceName()
			if(resName.equals('/')){
				return true
			}

			//			TODO not a decent way to do this
			for(ext in ['.htm', 'html']){
				if(WebApp.resources.get("${WebApp.RESOURCE_FOLDER}${resName}${ext}")){
					return true
				}
			}
		}

		return false
	}

	@Override
	public String getRequestedResourceName() {
		_requestedUrl.replace('.', '/')
	}

	@Override
	public Locale getRequestedLocale() {
		getLocale()?:Locale.ENGLISH
	}

	//------------------- preq info -------------------
	@Override
	public boolean isRestfulRequest() {
		//		TODO
		if(_requestedUrl.startsWith('/$Parsley/')){
			return true
		}

		return false
	}

	@Override
	public boolean isJavaScriptRequest() {
		//		TODO
		if(_requestedUrl.contains('/$Js/')){
			return true
		}

		return false
	}


}
