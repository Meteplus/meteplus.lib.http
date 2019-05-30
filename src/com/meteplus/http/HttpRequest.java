/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.meteplus.http;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author HuangMing
 */
public class HttpRequest {
    
    //------------------------------------------------------------------------------------
    public static final String REQ_HEADER_NAME_ACCEPT="Accept";
    public static final String REQ_HEADER_NAME_ACCEPT_LANGUAGE="Accept-Language";
    public static final String REQ_HEADER_NAME_ACCEPT_ENCODING="Accept-Encoding";
    public static final String REQ_HEADER_NAME_X_REQ_With="X-Requested-With";
    public static final String REQ_HEADER_NAME_USER_AGENT="User-Agent";
    public static final String REQ_HEADER_NAME_CONTENT_TYPE="Content-Type";
    public static final String REQ_HEADER_NAME_CONNECTION="Connection";
    public static final String REQ_HEADER_NAME_KEEP_ALIVE="Keep-Alive";
    public static final String REQ_HEADER_NAME_CACHE_CONTROL="Cache-Control";
    public static final String REQ_HEADER_NAME_REFERER="Referer";
    public static final String REQ_HEADER_NAME_COOKIE="Cookie";
    public static final String REQ_HEADER_NAME_CONTENT_DISPOSITION="Content-Disposition";
        
    //------------------------------------------------------------------------------------
    public static final String ACCEPT_DEFAULT_VALUE="application/json, text/javascript, text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8";
    public static final String ACCEPT_LANGUAGE_DEFAULT_VALUE="en";
    public static final String ACCEPT_ENCODING_DEFAULT_VALUE="gzip, deflate";
    public static final String X_REQ_With_DEFAULT_VALUE="XMLHttpRequest";
    public static final String CONNECTION_DEFAULT_VALUE="Keep-Alive";
    public static final String KEEP_ALIVE_DEFAULT_VALUE="300";
    public static final String CONTROL_DEFAULT_VALUE="no-cache";    
    
    //------------------------------------------------------------------------------------
    public static final String USER_AGENT_WEB="Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/38.0.2125.111 Safari/537.36";
    public static final String USER_AGENT_ANDROID="Mozilla/5.0 (Linux; U; Android 4.0.4; zh-cn; MI 1S Build/IMM76D) AppleWebKit/534.30 (KHTML, like Gecko) Version/4.0 Mobile Safari/534.30";
    public static final String USER_AGENT_IPHONE="Mozilla/5.0 (iPhone; U; CPU iPhone OS 4_3_3 like Mac OS X; en-us) AppleWebKit/533.17.9 (KHTML, like Gecko) Version/5.0.2 Mobile/8J2 Safari/6533.18.5";
    public static final String USER_AGENT_IPAD="Mozilla/5.0 (iPad; U; CPU OS 4_3_3 like Mac OS X; en-us) AppleWebKit/533.17.9 (KHTML, like Gecko) Version/5.0.2 Mobile/8J2 Safari/6533.18.5";
    

//------------------------------------------------------------------------------------
    public static final String CONTENT_TYPE_JSON="application/json; charset=UTF-8";
    public static final String CONTENT_TYPE_FORM="application/x-www-form-urlencoded; charset=UTF-8";
        
    
    private final HashMap<String,String> commonReqHeaders;  
    private HashMap<String,String> cookies;
    private String binFilePath=null;
    private String url=null;
    private byte[] body;
    private String pageEncode="UTF-8";
    private String method=HttpMethod.GET;
    
    public HttpRequest(){
        this.commonReqHeaders = new HashMap<>(10);
        setDefaultRequestHeader();
    }        
    
    public HttpRequest url(String url){
        this.url=url;
        return this;
    }
    
    public HttpRequest get(){
        method=HttpMethod.GET;
        return this;
    }
    
    public HttpRequest post(){
        method=HttpMethod.POST;
        return this;        
    }
    
    public HttpRequest put(){
        method=HttpMethod.PUT;
        return this;        
    }   
    
    public HttpRequest head(){
        method=HttpMethod.HEAD;
        return this;        
    }    
    
    public HttpRequest connect(){
        method=HttpMethod.CONNECT;
        return this;        
    }    
    
    public HttpRequest delete(){
        method=HttpMethod.DELETE;
        return this;        
    }     
    
    public HttpRequest options(){
        method=HttpMethod.OPTIONS;
        return this;        
    }    
    
    public HttpRequest trace(){
        method=HttpMethod.TRACE;
        return this;        
    }        
    
    private void setDefaultRequestHeader(){
        
        commonReqHeaders.put(REQ_HEADER_NAME_ACCEPT,ACCEPT_DEFAULT_VALUE );
        commonReqHeaders.put(REQ_HEADER_NAME_ACCEPT_LANGUAGE, ACCEPT_LANGUAGE_DEFAULT_VALUE);
        commonReqHeaders.put(REQ_HEADER_NAME_ACCEPT_ENCODING, ACCEPT_ENCODING_DEFAULT_VALUE);
        commonReqHeaders.put(REQ_HEADER_NAME_X_REQ_With,X_REQ_With_DEFAULT_VALUE);

        setUserAgent(USER_AGENT_WEB);
        
        commonReqHeaders.put(REQ_HEADER_NAME_CONNECTION, CONNECTION_DEFAULT_VALUE);
        commonReqHeaders.put(REQ_HEADER_NAME_KEEP_ALIVE,KEEP_ALIVE_DEFAULT_VALUE);
        commonReqHeaders.put(REQ_HEADER_NAME_CACHE_CONTROL,CONTROL_DEFAULT_VALUE);    
        
    }   
    
    public HttpRequest addRequestHeader(String key,String value){
        commonReqHeaders.put(key, value);
        return this;
    }

    public HttpRequest clearRequestHeaders(){
        commonReqHeaders.clear();
        return this;
    }
    
    public synchronized HttpRequest clearAndSetCookies(HashMap<String,String> cookies){
        if(cookies!=null){
            cookies.clear();
        }
        this.cookies=cookies;
        //updateCookies();     
        return this;
    }    

    public synchronized HttpRequest putCookie(String key,String value){
        if(cookies==null){
            cookies=new HashMap<>();
        }
        cookies.put(key, value);
        //updateCookies();       
        return this;
    }      
    
    public String getCookie(String key){
        if(cookies==null){
            return null;
        }
        return cookies.get(key);
    }
    
    public synchronized HttpRequest clearCookies(){
        this.cookies.clear();
        //updateCookies();
        return this;
    }
    
//    private void updateCookies(){
//        String cookie=cookieToString();
//        commonReqHeaders.put(REQ_HEADER_NAME_COOKIE,cookie);         
//    }
//    
    
    public HttpRequest setContentType(String contentType){
        commonReqHeaders.put(REQ_HEADER_NAME_CONTENT_TYPE, contentType);
        return this;
    }
    
    public HttpRequest setUserAgent(String userAgent){
        commonReqHeaders.put(REQ_HEADER_NAME_USER_AGENT, userAgent);
        return this;
    }

    public HttpRequest setReferer(String referer){
        commonReqHeaders.put(REQ_HEADER_NAME_REFERER, referer);
        return this;
    }

    
    public String getMethod(){
        return this.method;
    }
    
    public HttpRequest web(){
        setUserAgent(USER_AGENT_WEB);
        return this;
    }
    
    public HttpRequest android(){
        setUserAgent(USER_AGENT_ANDROID);
        return this;
    }    
        
    public HttpRequest iphone(){
        setUserAgent(USER_AGENT_IPHONE);
        return this;
    }     
    
    public HttpRequest ipad(){
        setUserAgent(USER_AGENT_IPAD);
        return this;
    }     
    
    //add form
    private StringBuilder sbBody;
    public HttpRequest addFormData(String key,Object value){
       return addFormData(key,value==null?"null":value.toString());   
    }    
    
//    public HttpRequest addFormData(String key,int value){
//        return addFormData(key,String.valueOf(value));  
//    }
//    
//    public HttpRequest addFormData(String key,long value){
//        return addFormData(key,String.valueOf(value)); 
//    }
//    
//    public HttpRequest addFormData(String key,float value){
//        return addFormData(key,String.valueOf(value)); 
//    }    
//    
//    public HttpRequest addFormData(String key,double value){
//        return addFormData(key,String.valueOf(value)); 
//    }    
//    
//    public HttpRequest addFormData(String key,boolean value){
//        return addFormData(key,String.valueOf(value)); 
//    }      
    
    public HttpRequest addFormData(String key,String value){

        commonReqHeaders.put(REQ_HEADER_NAME_CONTENT_TYPE, CONTENT_TYPE_FORM);
        if(sbBody==null){
            sbBody=new StringBuilder();
        }
        try{
            sbBody.append(key).
                    append("=").
                    append(URLEncoder.encode(value, "UTF-8")).
                    append("&");
        }catch(UnsupportedEncodingException e){
            e.printStackTrace();
        }catch(Exception e){
            e.printStackTrace();
        }
        return this;
    }
    
    
    
    //add json
    public HttpRequest addJsonObj(JSONObject jsonObj){
        
        commonReqHeaders.put(REQ_HEADER_NAME_CONTENT_TYPE, CONTENT_TYPE_JSON);
        try{
            sbBody=new StringBuilder(jsonObj.toString());
        }catch(Exception e){
            e.printStackTrace();
        }
        return this;
    }
    
    public HttpRequest addJsonArray(JSONArray jsonArr){
        commonReqHeaders.put(REQ_HEADER_NAME_CONTENT_TYPE, CONTENT_TYPE_JSON);
        try{
            sbBody=new StringBuilder(jsonArr.toString());
        }catch(Exception e){
            e.printStackTrace();
        }
        return this;
    }        
    
    
    //add multipart/formdata
    //文件上传（multipart/formdata）比较复杂，稍后在实现
    private String createMultiPart(){
        String multipart="multipart/form-data; boundary=";
        return multipart;
    }
    
    public HttpRequest addMultiPart(){
        commonReqHeaders.put(REQ_HEADER_NAME_CONTENT_TYPE, createMultiPart());
        try{
            sbBody=new StringBuilder();
        }catch(Exception e){
            e.printStackTrace();
        }
        return this;
    }         
    
    
    public HttpRequest build(){
        
        String cookie=cookieToString();
        commonReqHeaders.put(REQ_HEADER_NAME_COOKIE,cookie);  
        setBody(sbBody==null?null:sbBody.toString()); 
        
        return this;
    }
    
    private HttpRequest setBody(String body){
        
       if(body!=null){
            try{
                this.body=body.getBytes("UTF-8");
            }catch(UnsupportedEncodingException e){
                e.printStackTrace();
            }catch(Exception e){
                e.printStackTrace();
            }  
       }
       return this;
       
    }
    
    
    public byte[] getBody(){
        return body;
    }
    
    public HttpRequest setBinFilePath(String binFilePath){
        this.binFilePath=binFilePath;
        return this;
    }          
    
    public String getBinFilePath(){
        return binFilePath;
    }
    
    public HttpRequest setURL(String url){
        this.url=url;
        return this;
    }
    
    public String getURL(){
        return url;
    }

    public HttpRequest setPageEncode(String encode){
        pageEncode=encode;
        return this;
    }
    
    public String getPageEncode(){
        return this.pageEncode;
    }
    

    private String cookieToString(){
        if(cookies==null) {
            return "";
        }
        StringBuilder cookie=new StringBuilder();
        if(cookies!=null){
            Set<Map.Entry<String,String>> set=cookies.entrySet();
            for (Map.Entry<String,String> entry : set) {
                if(entry!=null){
                    cookie.append(entry.getKey()).append("=").append(entry.getValue()).append("; ");
                }
            }
        }
        return cookie.toString();
    }    
    
    public HttpRequest addRequestProperties(HttpURLConnection conn){
        if(commonReqHeaders!=null){
            for (Map.Entry entry : commonReqHeaders.entrySet()) {
                String key=(String)entry.getKey();
                String value=(String)entry.getValue();
                conn.addRequestProperty(key,value);
            }
        }    
        return this;
    }
    
    
}
