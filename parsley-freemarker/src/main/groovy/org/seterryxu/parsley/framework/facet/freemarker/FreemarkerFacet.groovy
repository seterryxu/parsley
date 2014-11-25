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

package org.seterryxu.parsley.framework.facet.freemarker

import java.util.Set;

import org.seterryxu.parsley.framework.core.lang.facets.Facet
import org.seterryxu.parsley.framework.core.uom.IParsleyRequest
import org.seterryxu.parsley.framework.core.uom.IParsleyResponse

import freemarker.template.Configuration
import freemarker.template.Template

/**
 * @author Xu Lijia
 *
 */
class FreemarkerFacet extends Facet{

	private static Configuration _conf
	private Template _t

	FreemarkerFacet(){
		_conf=new Configuration()
		//TODO
		//		_conf.setDirectoryForTemplateLoading(null)
		_conf.setDefaultEncoding('UTF-8')
	}

	boolean handle(instance,IParsleyRequest preq,IParsleyResponse pres){
		_t=_conf.getTemplate(preq.getRequestedResourceName())
		def out=pres.getOutputStream()
		_t.process(instance, out)
	}

	@Override
	Set<String> allowedExtensions() {
		def exts=new HashSet<String>()
		exts.add('.ftl')
		return exts;
	}
}
