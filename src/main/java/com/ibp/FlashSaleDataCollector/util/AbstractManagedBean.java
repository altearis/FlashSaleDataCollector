package com.ibp.FlashSaleDataCollector.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Locale.LanguageRange;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipException;

import com.ibp.FlashSaleDataCollector.model.APIResponse;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import com.ibp.FlashSaleDataCollector.entity.GlobalVariable;
import com.ibp.FlashSaleDataCollector.entity.UserAgent;

public abstract class AbstractManagedBean {


	protected final Logger log = LoggerFactory.getLogger(getClass());
	protected final String urlRegex = "((https?)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_;|])";
	protected final String digitRegex = "(\\.\\d+)";
	
	protected String CleanUpURL(String url){		
		return url.replaceAll(" ", "-").replaceAll("--", "-").replaceAll("/", "");
	}
	protected List<String> extractString(String text,String regex)
	{
	    List<String> containedUrls = new ArrayList<String>();
	  
	    try{
	    	Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
	    
	    Matcher urlMatcher = pattern.matcher(text);

	    while (urlMatcher.find())
	    {
	        containedUrls.add(text.substring(urlMatcher.start(0),
	                urlMatcher.end(0)));
	    }
	    } catch (Exception e) {
			// TODO: handle exception
		}

	    return containedUrls;
	}
	
	protected Map<String, Object> toMap(JSONObject object) throws JSONException {
        Map<String, Object> map = new HashMap();

        Iterator<String> keysItr = object.keys();
        while(keysItr.hasNext()) {
            String key = keysItr.next();
            Object value = object.get(key);

            if(value instanceof JSONArray) {
                value = toList((JSONArray) value);
            }

            else if(value instanceof JSONObject) {
                value = toMap((JSONObject) value);
            }
            if(String.valueOf(value).equals("null")){
                value="";
            }
            map.put(key, value);
        }
        return map;
    }
    protected List<Object> toList(JSONArray array) throws JSONException  {
        List<Object> list = new ArrayList<>();
        for(int i = 0; i < array.length(); i++) {
            Object value = array.get(i);
            if(value instanceof JSONArray) {
                value = toList((JSONArray) value);
            }

            else if(value instanceof JSONObject) {
                value = toMap((JSONObject) value);
            }
            list.add(value);
        }
        return list;
    }//*/
    protected static String decompressGZIP(InputStream inputStream) throws IOException
    {
    	try{
	        InputStream bodyStream = new GZIPInputStream(inputStream);
	        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
	        byte[] buffer = new byte[4096];
	        int length;
	        while ((length = bodyStream.read(buffer)) > 0) 
	        {
	            outStream.write(buffer, 0, length);
	        }	    	
	
	        return new String(outStream.toByteArray());
    	}catch(ZipException e){
    		e.printStackTrace();
    		return inputStream.toString();
    	}    	
    }
    protected JSONObject parseObject(JSONObject mainObject, String url,String method) throws JSONException, KeyManagementException, NoSuchAlgorithmException, KeyStoreException{
		Random rand = new Random(); 
		int maxData = GlobalVariable.useragent.size()-1;
		UserAgent uagent = GlobalVariable.useragent.get(rand.nextInt(maxData));
		
		String response = null;
        HttpHeaders headers = new HttpHeaders();	    
	    headers.setContentType(MediaType.APPLICATION_JSON);
	    List<LanguageRange> a = new ArrayList<LanguageRange>();
	    a.addAll(LanguageRange.parse("en-US"));
	    //headers.set("Accept-Charset", "utf-8");
	    List<Charset> acceptCharset = Collections.singletonList(StandardCharsets.UTF_8);
	    headers.setAcceptCharset(acceptCharset);
	    headers.set("Accept", "*/*");
	    headers.setPragma("no-cache");
	    headers.setConnection("keep-alive");
	    headers.set("Accept-Encoding", "gzip, deflate");
	    //headers.set("Content-Encoding", "gzip");
	    headers.set("Accept-Language", "en-US,en;q=0.9");	    
		headers.set("User-Agent",uagent.getUserAgent());
				
		log.debug("url : "+url);
	    log.debug("Request Headers ->");
	    
	    StringBuilder printHeader = new StringBuilder();
	    printHeader.append("{");
	    headers.toSingleValueMap().forEach((key, value) -> printHeader.append("\""+key + "\":\"" + value+"\" ,"));
	    printHeader.deleteCharAt(printHeader.lastIndexOf(","));
	    printHeader.append("}");
	    log.debug(printHeader.toString());
	    if(method.toUpperCase() == "POST"){
		    log.debug("Request -> "+mainObject.toString());
		    
		    HttpEntity<String> request = new HttpEntity<String>(mainObject.toString(), headers);
		    RestTemplate restTemplate = new RestTemplate();//*
		    for (HttpMessageConverter converter : restTemplate.getMessageConverters()) {
		        if (converter instanceof StringHttpMessageConverter) {
		            ((StringHttpMessageConverter) converter).setWriteAcceptCharset(false);
		        }
		    }
		    ResponseEntity<Resource> responseAll = restTemplate.postForEntity( url, request , Resource.class );	 
		    try {
		    	String contentEncoding = responseAll.getHeaders().getFirst(HttpHeaders.CONTENT_ENCODING);

		        if ("gzip".equalsIgnoreCase(contentEncoding)) {		        	
		        	response =decompressGZIP(responseAll.getBody().getInputStream());
		        } else{
		        	response =responseAll.getBody().toString();
		        }
		    JSONObject jsonObj = new JSONObject(response);
	        return (jsonObj);
		    } catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		    return null;
	        
	    }else{
	    	HttpEntity request = new HttpEntity(headers);
		    RestTemplate restTemplate = new RestTemplate();
		    ResponseEntity<Resource> responseAll = restTemplate.exchange( url, HttpMethod.GET, request, Resource.class );

		    try {
		    	String contentEncoding = responseAll.getHeaders().getFirst(HttpHeaders.CONTENT_ENCODING);

		        if ("gzip".equalsIgnoreCase(contentEncoding)) {		        	
		        	response =decompressGZIP(responseAll.getBody().getInputStream());
		        } else{
		        	response =responseAll.getBody().toString();
		        }
					    
		   	    JSONObject jsonObj = new JSONObject(response);

			    return (jsonObj);
		    } catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		    return null;
		   // SSLUtil.turnOnSslChecking();
	    }
	
	}
    protected ResponseEntity<Object> response(HttpStatus statuscd,List<Object> errorList,List<Object> listObj,Object...resp){
		APIResponse apiRsp;
		if(errorList != null)
		{
			apiRsp = new APIResponse(errorList,String.valueOf(resp[0]),String.valueOf(resp[1]));
		}else if(listObj != null){
			apiRsp =new APIResponse(String.valueOf(resp[0]),String.valueOf(resp[1]),listObj);
		}else{
			apiRsp = new APIResponse(String.valueOf(resp[0]),String.valueOf(resp[1]));
		}
		return new ResponseEntity<Object>(apiRsp,statuscd);
	}
    protected ResponseEntity<Object> response(HttpStatus statuscd,List<Object> errorList,Object listObj,Object...resp){
		APIResponse apiRsp;
		if(errorList != null)
		{
			apiRsp = new APIResponse(errorList,String.valueOf(resp[0]),String.valueOf(resp[1]));
		}else if(listObj != null){
			apiRsp =new APIResponse(String.valueOf(resp[0]),String.valueOf(resp[1]),listObj);
		}else{
			apiRsp = new APIResponse(String.valueOf(resp[0]),String.valueOf(resp[1]));
		}
		return new ResponseEntity<Object>(apiRsp,statuscd);
	}

}
