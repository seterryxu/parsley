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

package org.seterryxu.parsleyframework.facet.freemarker.util

import java.util.jar.JarFile

/**
 * @author Xu Lijia
 *
 */
class JarUtils {

	static boolean decompress(File jarFile,String outputPath){
		if(!jarFile||!outputPath){
			return false
		}

		if(!jarFile.exists()){
			return false
		}

		def oPath=new File(outputPath)
		if(!oPath.exists()){
			if(oPath.mkdirs()){
				return false
			}
		}

		def jar=new JarFile(jarFile)
		def entries=jar.entries()
		while(entries.hasMoreElements()){
			def entry=entries.nextElement()
			//TODO filter .css, .js ? if(!entry.getName().endsWith(''))
			def f=new File("${outputPath}/${entry.getName()}")
			if(entry.isDirectory()){
				if(!f.exists()){
					if(!f.mkdirs()){
						return false
						break
					}
				}
			}else{
				def parentFile=f.getParentFile()
				if(!parentFile.exists()){
					if(!parentFile.mkdirs()){
						return false
						break
					}
				}
				
				if(!entry.name.endsWith('.css')||entry.name.endsWith('.js')){
					continue
				}
				
				def is=jar.getInputStream(entry)
				def os=new BufferedOutputStream(new FileOutputStream(f))
				
				byte[]buffer=new byte[4096]
				int len
				while((len=is.read(buffer))>0){
					os.write(buffer, 0, len)
				}
				
				os.flush()
				os.close()
				is.close()
			}
		}
	}
}
