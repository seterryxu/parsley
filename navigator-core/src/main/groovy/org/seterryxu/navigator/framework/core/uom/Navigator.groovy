package org.seterryxu.navigator.framework.core.uom

import java.util.logging.Logger

import javax.servlet.ServletConfig
import javax.servlet.ServletContext
import javax.servlet.ServletException
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class Navigator extends HttpServlet {

	private static final Logger LOGGER=Logger.getLogger Navigator.class.name


	private ServletContext context
	private static Map<String, URL>resourcePathsCache

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);

		context=config.getServletContext()
		generateResourcePathsCache()
	}

	private void generateResourcePathsCache(){
		def resourceUrls=[:]
		Stack<String> resourceSubDir=new Stack<String>()
		resourceSubDir.push('/')

		while(!resourceSubDir.isEmpty()){
			String currentDir=resourceSubDir.pop()
			Set<String>content=context.getResourcePaths(currentDir)

			if(content){
				content.each {
					if(it.endsWith('/')){
						resourceSubDir.push(it)
					}else{
						URL url=context.getResource(it)
						resourceUrls.put(it, url)
					}
				}
			}
		}

		resourcePathsCache=Collections.unmodifiableMap(resourceUrls)
	}

	@Override
	protected void service(HttpServletRequest req, HttpServletResponse res)
	throws ServletException, IOException {

		String reqUrl=getRequestUrl(req)

		NavigatorRequest nreq=new NRequestImpl(req, reqUrl)
		NavigatorResponse nres=new NResponseImpl(res)

		tryNavigate(req,res)
	}

	private String getRequestUrl(HttpServletRequest req){
		req.getRequestURI().substring(req.getContextPath())
	}

	private void tryNavigate(NavigatorRequest req, NavigatorResponse res){
	}

	private LocalizedResourcesSelector={
		def resources=[:]

		def findLocalizedResources={
			resourcePathsCache.keySet().each {
				if(it.endsWith('.properties')){
					resources.put(it, resourcePathsCache[it])
				}
			}
		}

		URL selectAResourceByName={String name->resources[name]}
	}
}
