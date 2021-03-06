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

import javax.servlet.ServletOutputStream

import org.terryxu.parsleyframework.core.util.ResourceUtils;
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 *
 * @author Xu Lijia
 */
class StaticResourceResponse implements IHttpResponse {

	private static final Logger LOGGER=LoggerFactory.getLogger(StaticResourceResponse)

	private static final int BUFFER_SIZE=4096

	private IParsleyRequest _preq

	private InputStream _stream

	StaticResourceResponse(IParsleyRequest preq){
		this._preq=preq
	}

	private static InputStream _toStream(URL url){
		url.openConnection().getInputStream()
	}

	@Override
	public void generateResponse(IParsleyResponse pres) {
		def res=_preq.requestedResource
		def page=ResourceUtils.getStaticResource(res)

		if(page){
			this._stream=_toStream(page)
			_generateResponse(pres)
		}else{
			def errorMsg="Static Resource $res not found."
			LOGGER.error(errorMsg)
			HttpResponseFactory.notFound(errorMsg, pres)
		}
	}

	private void _generateResponse(IParsleyResponse pres) {
		def out=pres.getOutputStream()

		byte[]buffer=new byte[BUFFER_SIZE]
		int len
		while((len=_stream.read(buffer))>0){
			out.write(buffer, 0, len)
		}

		out.close()
	}

}