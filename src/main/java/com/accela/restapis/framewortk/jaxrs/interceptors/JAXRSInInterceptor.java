package com.accela.restapis.framewortk.jaxrs.interceptors;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.jaxrs.JAXRSServiceImpl;
import org.apache.cxf.jaxrs.ext.RequestHandler;
import org.apache.cxf.jaxrs.impl.MetadataMap;
import org.apache.cxf.jaxrs.impl.RequestPreprocessor;
import org.apache.cxf.jaxrs.impl.UriInfoImpl;
import org.apache.cxf.jaxrs.model.ClassResourceInfo;
import org.apache.cxf.jaxrs.model.OperationResourceInfo;
import org.apache.cxf.jaxrs.model.ProviderInfo;
import org.apache.cxf.jaxrs.model.URITemplate;
import org.apache.cxf.jaxrs.provider.ProviderFactory;
import org.apache.cxf.jaxrs.utils.HttpUtils;
import org.apache.cxf.jaxrs.utils.JAXRSUtils;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.apache.cxf.service.Service;

import com.accela.restapis.framewortk.model.RestThreadLocal;

public class JAXRSInInterceptor extends AbstractPhaseInterceptor<org.apache.cxf.message.Message>
{
	public static final String ROOT_RESOURCE_CLASS = "root.resource.class";
	public static final String RELATIVE_PATH = "relative.path";
	
    public JAXRSInInterceptor() {
        super(Phase.PRE_STREAM);
    }

	public void handleMessage(Message message) throws Fault
	{


		try
		{
			processRequest(message);
		}
		catch (Fault fault)
		{
			throw fault;
		}
		catch (RuntimeException ex)
		{
			Response excResponse = JAXRSUtils.convertFaultToResponse(ex, message);
			if (excResponse == null)
			{
				ProviderFactory.getInstance(message).clearThreadLocalProxies();
				throw ex;
			}
			message.getExchange().put(Response.class, excResponse);
			Fault fault = new Fault(new Exception(String.format(String.valueOf(excResponse.getStatus()))));
			throw fault;
		}


	}

	private void processRequest(Message message)
	{
		 if (message.getExchange().get(OperationResourceInfo.class) != null) {
	            // it's a suspended invocation;
	            return;
	        }
		 
		 RequestPreprocessor rp = ProviderFactory.getInstance(message).getRequestPreprocessor();
	        if (rp != null) {
	            rp.preprocess(message, new UriInfoImpl(message, null));
	        }
		
	        String requestContentType = (String)message.get(Message.CONTENT_TYPE);
	        if (requestContentType == null) {
	            requestContentType = "*/*";
	        }
	        
	        String rawPath = HttpUtils.getPathToMatch(message, true);
	        
	      //1. Matching target resource class
	        Service service = message.getExchange().get(Service.class);
	        List<ClassResourceInfo> resources = ((JAXRSServiceImpl)service).getClassResourceInfos();
	        
	        String acceptTypes = (String)message.get(Message.ACCEPT_CONTENT_TYPE);
	        if (acceptTypes == null) {
	            acceptTypes = "*/*";
	            message.put(Message.ACCEPT_CONTENT_TYPE, acceptTypes);
	        }
	        List<MediaType> acceptContentTypes = JAXRSUtils.sortMediaTypes(acceptTypes);
	        message.getExchange().put(Message.ACCEPT_CONTENT_TYPE, acceptContentTypes);
	        
	        MultivaluedMap<String, String> values = new MetadataMap<String, String>();
	        
	        ClassResourceInfo resource = getClassResource(message, rawPath, resources, values);
	       
	        if (resource == null) {
	            throw new WebApplicationException(Response.Status.NOT_FOUND);
	        }

	        message.getExchange().put(ROOT_RESOURCE_CLASS, resource);

	        String httpMethod = (String)message.get(Message.HTTP_REQUEST_METHOD);
	        OperationResourceInfo ori = null;     
	        
	        List<ProviderInfo<RequestHandler>> shs = ProviderFactory.getInstance(message).getRequestHandlers();
	        
	        for (ProviderInfo<RequestHandler> sh : shs) {
	            String newAcceptTypes = (String)message.get(Message.ACCEPT_CONTENT_TYPE);
	            if (!acceptTypes.equals(newAcceptTypes) || ori == null) {
	                acceptTypes = newAcceptTypes;
	                acceptContentTypes = JAXRSUtils.sortMediaTypes(newAcceptTypes);
	                message.getExchange().put(Message.ACCEPT_CONTENT_TYPE, acceptContentTypes);
	                
	                if (ori != null) {
	                    values = new MetadataMap<String, String>();
	                    resource = JAXRSUtils.selectResourceClass(resources, 
	                                                              rawPath, 
	                                                              values);
	                }
	                
	                ori = JAXRSUtils.findTargetMethod(resource, values.getFirst(URITemplate.FINAL_MATCH_GROUP), 
	                                                  httpMethod, values, requestContentType, acceptContentTypes);
	                message.getExchange().put(OperationResourceInfo.class, ori);
	            }
	            Response response = sh.getProvider().handleRequest(message, resource);
	            if (response != null) {
	                message.getExchange().put(Response.class, response);
	                return;
	            }
	        }
	        
	        String newAcceptTypes = (String)message.get(Message.ACCEPT_CONTENT_TYPE);
	        if (!acceptTypes.equals(newAcceptTypes) || ori == null) {
	            acceptTypes = newAcceptTypes;
	            acceptContentTypes = JAXRSUtils.sortMediaTypes(acceptTypes);
	            message.getExchange().put(Message.ACCEPT_CONTENT_TYPE, acceptContentTypes);
	            if (ori != null) {
	                values = new MetadataMap<String, String>();
	                resource = JAXRSUtils.selectResourceClass(resources, 
	                                                          rawPath, 
	                                                          values);
	            }
	            ori = JAXRSUtils.findTargetMethod(resource, values.getFirst(URITemplate.FINAL_MATCH_GROUP), 
	                                              httpMethod, values, requestContentType, acceptContentTypes);
	            message.getExchange().put(OperationResourceInfo.class, ori);
	        }

	        
//	        logger.info("Request path is: " + rawPath);
//	        logger.info("Request HTTP method is: " + httpMethod);
//	        logger.info("Request contentType is: " + requestContentType);
//	        logger.info("Accept contentType is: " + acceptTypes);
//	        
//	        logger.info("Found operation: " + ori.getMethodToInvoke().getName());
	        System.out.println("Request path is: " + rawPath);
	        System.out.println("Request HTTP method is: " + httpMethod);
	        System.out.println("Request contentType is: " + requestContentType);
	        System.out.println("Accept contentType is: " + acceptTypes);
	        
	        System.out.println("Found operation: " + ori.getMethodToInvoke().getName());
	        
	        message.getExchange().put(OperationResourceInfo.class, ori);
	        message.put(RELATIVE_PATH, values.getFirst(URITemplate.FINAL_MATCH_GROUP));
	        message.put(URITemplate.TEMPLATE_PARAMETERS, values);
	        
	        
	        //put post to do
	        
	        List<Object> params = JAXRSUtils.processParameters(ori, values, message);
	        message.setContent(List.class, params);
	        
		
	}
	
	
	private ClassResourceInfo getClassResource(Message message, String rawPath, List<ClassResourceInfo> resources,
			MultivaluedMap<String, String> values)
	{
		//1.0 Get version information from the url.
		String[] pathResources = rawPath.split("/");
        int version = 0;
        
        //2.0 Check version name for the APIs which don't have versions
		if(pathResources[1].startsWith("v") || pathResources[1].startsWith("V"))
        {
			version = Integer.valueOf(pathResources[1].substring(1,pathResources[1].length()));
        }
		
		//set version to thread.
		RestThreadLocal.setVersion(version);
		
		//3.0 if there is not current version, then get the lower version.
		ClassResourceInfo resource = null;
		if(version==0)
		{
			resource = JAXRSUtils.selectResourceClass(resources, rawPath, values);
		}
		else
		{
			String acceptTypes = (String)message.get(Message.ACCEPT_CONTENT_TYPE);
	        if (acceptTypes == null) {
	            acceptTypes = "*/*";
	            message.put(Message.ACCEPT_CONTENT_TYPE, acceptTypes);
	        }
			List<MediaType> acceptContentTypes = JAXRSUtils.sortMediaTypes(acceptTypes);
			String httpMethod = (String)message.get(Message.HTTP_REQUEST_METHOD);
			 String requestContentType = (String)message.get(Message.CONTENT_TYPE);
		        if (requestContentType == null) {
		            requestContentType = "*/*";
		        }
			for (int i =version; i>=0; i--)
			{
				String rawVersion ="";
				if(i>0)
				{
					rawVersion ="/v"+i;
				}
				String path = rawVersion+rawPath.substring(rawPath.indexOf("/",2), rawPath.length());
				
				resource = JAXRSUtils.selectResourceClass(resources, path, values);
				MultivaluedMap<String, String> tempValues = cloneMap(values);
				if(resource!=null)
				{
					try
					{
						///Check if the method exist in high version.
						JAXRSUtils.findTargetMethod(resource, values.getFirst(URITemplate.FINAL_MATCH_GROUP), 
	                        httpMethod, tempValues, requestContentType, acceptContentTypes);
					}
					catch(Exception e)
					{
						continue;
					}
					//update the request path.
					HttpUtils.updatePath(message, path);
					break;
				}
			}
		}
		
		return resource;
	}
	
	  private static <K, V> MultivaluedMap<K, V> cloneMap(MultivaluedMap<K, V> map1) {
	        
	        MultivaluedMap<K, V> map2 = new MetadataMap<K, V>();
	        for (Map.Entry<K, List<V>> entry : map1.entrySet()) {
	            map2.put(entry.getKey(), new ArrayList<V>(entry.getValue()));
	        }
	        return map2;
	        
	    }

}
