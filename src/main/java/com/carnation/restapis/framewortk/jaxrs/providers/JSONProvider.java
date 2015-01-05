package com.carnation.restapis.framewortk.jaxrs.providers;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;

import org.apache.cxf.helpers.IOUtils;
import org.apache.cxf.jaxrs.ext.multipart.MultipartBody;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JSONProvider implements MessageBodyReader<Object>, MessageBodyWriter<Object>
{

	public boolean isWriteable(Class<?> paramClass, Type paramType, Annotation[] paramArrayOfAnnotation,
			MediaType paramMediaType)
	{
		return (!MultipartBody.class.isAssignableFrom(paramClass) && !InputStream.class.isAssignableFrom(paramClass));
	}

	public long getSize(Object paramT, Class<?> paramClass, Type paramType, Annotation[] paramArrayOfAnnotation,
			MediaType paramMediaType)
	{
		return 0;
	}

	public void writeTo(Object paramT, Class<?> paramClass, Type paramType, Annotation[] paramArrayOfAnnotation,
			MediaType paramMediaType, MultivaluedMap<String, Object> paramMultivaluedMap, OutputStream paramOutputStream)
			throws IOException, WebApplicationException
	{
		System.out.println("writeTo");
	}

	public boolean isReadable(Class<?> paramClass, Type paramType, Annotation[] paramArrayOfAnnotation,
			MediaType paramMediaType)
	{
		return (!paramClass.equals(MultipartBody.class) && !paramClass.equals(InputStream.class));
	}

	public Object readFrom(Class<Object> paramClass, Type paramType, Annotation[] paramArrayOfAnnotation,
			MediaType paramMediaType, MultivaluedMap<String, String> paramMultivaluedMap, InputStream paramInputStream)
			throws IOException, WebApplicationException
	{
		System.out.println("readFrom");
		String sBody = IOUtils.toString(paramInputStream);
		
		ObjectMapper om = new ObjectMapper();
		Object u = om.readValue(sBody, paramClass);
		
		
		return null;
	}
	
//	public Object toModel(InputStream entityStream, Class<?> modelType) throws IOException, JsonParseException,
//			JsonProcessingException
//	{
//		JsonFactory jsonFactory = new JsonFactory();
//		CustomObjectMapper convert = builderCustomObject();
//		CustomJSONProvider.setObjectMapperThreadLocal(convert);
//		jsonFactory.setCodec(convert);
//		JsonParser parser = jsonFactory.createParser(entityStream);
//		CustomJSONProvider.setRootModelConvertor(RestThreadLocal.getRootModelConvertor().get("request"));
//		CustomJSONProvider.setMethodConvertorThreadLocal(RestThreadLocal.getModelConvertors().get(
//			Constants.CONVERTOR_REQUESTS));
//		CustomJSONProvider.setContext(CustomConvertorDefine.getCustomConvertor());
//		Object result = null;
//		try
//		{
//			result = parser.readValueAs(modelType);
//		}
//		finally
//		{
//			CustomJSONProvider.setContext(null);
//			CustomJSONProvider.setObjectMapperThreadLocal(null);
//			CustomJSONProvider.setRootModelConvertor(null);
//			CustomJSONProvider.setMethodConvertorThreadLocal(null);
//			CustomJSONProvider.setOriginalModel(null);
//		}
//		return result;
//	}

}
