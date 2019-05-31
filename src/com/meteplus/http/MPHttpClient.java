/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.meteplus.http;
  
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import com.meteplus.http.utils.NetworkUtil;
import com.meteplus.http.utils.Proxy;
import com.meteplus.http.interfaces.HttpProgressListener;
import com.meteplus.http.wraper.ResponseCode;
import com.meteplus.utils.Utils;

/**
 *
 * @author IBM
 */
public class MPHttpClient {
    
    public static final int DEFAULT_TIMEOUT=5*60*1000;
    
//    private int iostatu;
//    private String ioExceptionMsg;
    private ResponseCode ioRespCode=new ResponseCode();
    private int connectTimeout=DEFAULT_TIMEOUT;
    private int readTimeout=DEFAULT_TIMEOUT;
    
    //------------------------------------------------------------------------------------
    public static final int NETWORK_OK=1;
    public static final int NETWORK_IO_EXCEPTION=2;
    public static final int NETWORK_UNCAUGHT_EXCEPTION=3;

    
    
    

    //------------------------------------------------------------------------------------
    public static final String RES_HEADER_NAME_SET_COOKIE="Set-Cookie";
    public static final String RES_HEADER_NAME_CONTENT_DISPOSITION="Content-Disposition";
    public static final String RES_HEADER_NAME_LOCATION="location";
    public static final String RES_HEADER_NAME_RETRY_AFTER="Retry-After";
    
    
    //private int tryConnectTimeoutNum=0;

//    public static final int GET=0;
//    public static final int POST=1;
    
    private HashMap<String,String> respHeaders; 
    private StringBuilder respBody=null;
    private ResponseCode httpRespCode=new ResponseCode();
//    private int respCode;
//    private String respMsg;    

    private Proxy proxy=null;
    private HttpRequest currentReq;
    
    
    public static void setFollowRedirects(boolean b){
        HttpURLConnection.setFollowRedirects(b);
    }
    
    
    //public static final Object toomanyrqSync=new Object();
    //public static boolean istoomanyrequest=false;    
    
    public void resetAllStatus(){
        ioRespCode.code=0;
        ioRespCode.msg=null;
        httpRespCode.code=0;
        httpRespCode.msg=null;
    }
    
    /**
     * 判断此次调用中网络操作（连接，输入、输出）是否正常，
     * 注意，这里只是判断http调用请求的操作本身是否执行成功，不涉及http请求返回结果和状态
     * 如socket连接失败，网络异常，代理异常，以及其他一些异常
     * 需要判断http请求的逻辑本身的状态，需要调用getResponseStatus和getResponseMessage
     * @return 
     */
    public boolean isIoSuccess(){
        return NETWORK_OK==ioRespCode.code;
    }
    
    
    /**
     * HTTP RESPONSE OK
     * 基本就意味着，network没有出现问题，连接、写、读信息都OK
     * 因此调用者可以通过这个方法就判断请求成功，直接获取返回的内容进行后续处理。
     * 但有一些可能的特殊情况，比如已经获取到responsecode是200，inputstream读取到一部分的时候
     * 网络断了或者操作问题（内存 溢出等）导致读取的内容不完整
     * 但总体来说，基本可以通过这个方法来判断返回内容是否可用了。
     * @return 
     */
    public boolean isHttpSuccess(){
        return (httpRespCode.code>=200&&httpRespCode.code<300)&&(NETWORK_OK==ioRespCode.code);
    }
    
    public ResponseCode getIoRespCode(){
        return ioRespCode;
    }    
    
    public ResponseCode getHttpRespCode(){
        return httpRespCode;
    }
    

    public String getResponseBody(){
        if(respBody==null){
            return null;
        }
        return respBody.toString();
    }
    
    public Proxy getProxy(){
        return proxy;
    }
    
    public MPHttpClient setProxy(Proxy proxy){
        //tryConnectTimeoutNum=0;
        this.proxy=proxy;
        return this;
    }

//    public MPHttpClient setTryConnectTimeOutNum(int num){
//        tryConnectTimeoutNum=num;
//        return this;
//    }
//    
//    public int getTryConnectTimeOutNum(){
//        return tryConnectTimeoutNum;
//    }
    
    public String getFieldValue(String key){
        if(key==null||respHeaders==null){
            return null;
        }
        return respHeaders.get(key); 
    }
    
    
    public MPHttpClient setConnectTimeOut(int connectTimeout){
        this.connectTimeout=connectTimeout;
        return this;
    }    
    
    public MPHttpClient setReadTimeOut(int readTimeout){
        this.readTimeout=readTimeout;
        return this;
    }      
    
    
    public int getConnectTimOut(){
        return this.connectTimeout;
    }
    
    public int getReadTimeOut(){
        return this.readTimeout;
    }
    
    public String getRedirectUrl(){
        String location=null;
        if(httpRespCode.code>=300&&httpRespCode.code<400){
            location=respHeaders.get(RES_HEADER_NAME_LOCATION);
            System.out.println("location="+location);
        }
        return location;
    }
    

    private InputStream processGzipStream(HttpURLConnection conn) 
            throws IOException{
        
        InputStream in;
        String content_encoding=conn.getContentEncoding();
        if(content_encoding!=null&&content_encoding.startsWith("gzip")){
            in=new GZIPInputStream(conn.getInputStream());
        }else{
            in=conn.getInputStream();
        }       
        return in;
        
    }
    private InputStream processRespBody(HttpURLConnection conn,StringBuilder respBody) 
            throws IOException{
        
        InputStream in=processGzipStream(conn);
        
        BufferedReader bin = 
                new BufferedReader(new InputStreamReader(in, currentReq.getPageEncode()));  
        String line;  
        while ((line = bin.readLine()) != null){  
          respBody.append(line);  
        }         

        return in;

    }


    
    private void processRespHeaders(HttpURLConnection conn,HttpRequest currentReq){

        
        if(respHeaders!=null){
            respHeaders.clear();
        }else{
            respHeaders=new HashMap<>();
        }     
        
        
        for (int i=0; ; i++) {
            String headerName = conn.getHeaderFieldKey(i);
            String headerValue = conn.getHeaderField(i);
            if (headerName == null && headerValue == null) {
                // No more headers
                break;
            }
            if (RES_HEADER_NAME_SET_COOKIE.equalsIgnoreCase(headerName)) {
                // Parse cookie
                String[] fields = headerValue.split(";\\s*");
                String cookieValue = fields[0];
                if(cookieValue!=null){
                    int index=cookieValue.indexOf("=");
                    if(index>0){
                        String value=cookieValue.substring(index+1).trim();
                        if(value!=null/*&&!value.trim().equals("deleted")*/){
                           currentReq.putCookie(cookieValue.substring(0,index).trim(),value);
                        }
                    }
                }
                // Save the cookie...
            }else{
                if(headerName!=null){
                    respHeaders.put(headerName.trim().toLowerCase(), headerValue);
                }
            }
            
        }

    }    
    
    

    public MPHttpClient newCall(HttpRequest currentReq){

        
       OutputStream out=null;
       InputStream in=null;
       HttpURLConnection conn;

       this.currentReq=currentReq;
       /*if(currentReq.getURL()!=null&&currentReq.getURL().contains("teambition.com")){
            synchronized (toomanyrqSync) { 
                 while (istoomanyrequest) { 

                     try{
                         System.err.println(">>>>>> wait..."+currentReq.getURL());
                         toomanyrqSync.wait(); 
                         System.err.println(">>>>>> wait finished..."+currentReq.getURL());
                     }catch(Exception e){

                     }

                 } 
             }
       }*/
       //resetStatus();

        try {

//            if(url!=null&&url.startsWith("https://")){
//                trustAllHttpsCertificates();
//            }



            //----------连接时可能抛出网络异常---------
            conn=(HttpURLConnection)NetworkUtil.getConnection(currentReq.getURL(), proxy);
            //----------连接时可能抛出网络异常---------
            
            
            conn.setRequestMethod(currentReq.getMethod());
            currentReq.addRequestProperties(conn);
            conn.setConnectTimeout(connectTimeout);
            conn.setReadTimeout(readTimeout);

            
            
            //----------写入outputstream时可能抛出网络异常---------
            if(currentReq.getBody()!=null){
                conn.setDoOutput(true); 
                out=conn.getOutputStream();
                out.write(currentReq.getBody());
                out.flush();
            }
            //----------写入outputstream时可能抛出网络异常---------    
            
            
            //process response iostatu and response message
            httpRespCode.code=conn.getResponseCode();
            httpRespCode.msg=conn.getResponseMessage();
            
            //process response headers
            processRespHeaders(conn,currentReq);
            
            //process response body
            if(httpRespCode.code>=200&&httpRespCode.code<300){
                //----------操作inputstream时可能抛出网络异常---------
                respBody=new StringBuilder();
                in=processRespBody(conn,respBody);
                //----------操作inputstream时可能抛出网络异常---------
                
            }
            
            
            
            //relaese all resources
            if(in!=null){
                in.close();
            }
            
            if(out!=null){
                out.close();   
            }

            //iostatu of this call function
            ioRespCode.code=NETWORK_OK;
            ioRespCode.msg="OK";

        }catch (IOException ie) {
            ioRespCode.code=NETWORK_IO_EXCEPTION;
            ioRespCode.msg=ie.getMessage();
            ie.printStackTrace();
        }catch(Exception e){
            ioRespCode.code=NETWORK_UNCAUGHT_EXCEPTION;
            ioRespCode.msg=e.getMessage();
            e.printStackTrace();
        }finally{
            
            try{

                if(out!=null){
                    out.close();
                }
                if(in!=null){
                    in.close();
                }
                //if(conn!=null){
                //    conn.disconnect();
                //}
            }catch(IOException ie){
                ie.printStackTrace();
            }
        }
        
        return this;
              
    }
        
    /**
     * Downloads a file from a URL
     * @param currentReq
     * @param saveDir path of the directory to save the file
     * @param saveFileName
     * @param pl
     * @return 
     * @throws IOException
     */
    public String downloadFile(HttpRequest currentReq,
                               String saveDir,
                               String saveFileName,
                               HttpProgressListener pl)
            throws IOException {
       
       String saveFilePath =null; 
       String fileName; 

       OutputStream out=null;
       InputStream in=null;
       HttpURLConnection conn=null;
       FileOutputStream fout=null;

       this.currentReq=currentReq;
       //resetStatus();
       
        try {

            conn=(HttpURLConnection)NetworkUtil.getConnection(currentReq.getURL(), proxy);
 
            currentReq.addRequestProperties(conn);

            conn.setConnectTimeout(connectTimeout);
            conn.setReadTimeout(readTimeout);
            
            if(currentReq.getBody()!=null){
                
                conn.setDoOutput(true); 
                out=conn.getOutputStream();
                out.write(currentReq.getBody());
                out.flush();
            }            
            
            
            
            //process response iostatu and response message
            httpRespCode.code=conn.getResponseCode();
            httpRespCode.msg=conn.getResponseMessage();
            
            //process response headers
            processRespHeaders(conn,currentReq);
            
            //process response body
            in=processGzipStream(conn);
            
            String location=respHeaders.get(RES_HEADER_NAME_LOCATION);
            System.out.println("location="+location);

            // always check HTTP response code first

            String disposition = conn.getHeaderField(RES_HEADER_NAME_CONTENT_DISPOSITION);
            String contentType = conn.getContentType();
            int contentLength = conn.getContentLength();

            if(saveFileName==null||saveFileName.trim().isEmpty()){
                /*if (disposition != null) {
                    // extracts file name from header field
                    int index = disposition.indexOf("filename=");
                    if (index > 0) {
                        fileName = disposition.substring(index + 10,disposition.length() - 1);
                        fileName=new String(fileName.getBytes("UTF-8"),"iso8859-1");
                    }
                } else*/
                {
                    // extracts file name from URL
                    fileName = currentReq.getURL().substring(currentReq.getURL().lastIndexOf("/") + 1,currentReq.getURL().length());
                }


                if(fileName==null&&contentType!=null){
                    String suffix=null;
                    int lastIndex=contentType.lastIndexOf("/");
                    if(lastIndex>0){
                        suffix=contentType.substring(lastIndex+1);
                    }
                    fileName=Utils.getRandomString(12, Utils.ONLY_CHARS)+(suffix==null?"":"."+suffix);
                }


            }else{
                fileName=saveFileName;
            }

            System.out.println("Content-Type = " + contentType);
            System.out.println("Content-Disposition = " + disposition);
            System.out.println("Content-Length = " + contentLength);
            System.out.println("fileName = " + fileName);

            // opens input stream from the HTTP connection

            if(location==null){

             // opens an output stream to save into file
                fout=new FileOutputStream(saveDir + File.separator +fileName);
                int bytesRead = -1;
                long progress = 0;
                byte[] buffer = new byte[pl!=null?pl.getBlockSize():1024*1024];
                while ((pl!=null&&pl.isContinue()||pl==null)&&(bytesRead = in.read(buffer)) != -1) {
                    fout.write(buffer, 0, bytesRead);
                    progress+=bytesRead;
                    if(pl!=null){
                        pl.update(contentLength, progress);
                    }
                } 
                saveFilePath = saveDir + File.separator +fileName;

            }else{
                // opens an output stream to save into file
                byte[] buffer = new byte[pl!=null?pl.getBlockSize():1024*1024];
                while (in.read(buffer) != -1) {
                }
            }
            
            if(fout!=null){
                fout.close();
            }
            
            if(out!=null){
                out.close();
            }

            if(in!=null){
                in.close();
            }
            
            //conn.disconnect();
            
        }catch (IOException ie) {
            ioRespCode.code=NETWORK_IO_EXCEPTION;
            ioRespCode.msg=ie.getMessage();
            ie.printStackTrace();
        }catch(Exception e){
            ioRespCode.code=NETWORK_UNCAUGHT_EXCEPTION;
            ioRespCode.msg=e.getMessage();
            e.printStackTrace();
        }finally{
            try{
                if(out!=null) {
                    out.close();
                }
                if(in!=null) {
                    in.close();
                }
                if(fout!=null){
                    fout.close();
                }
                if(conn!=null) {
                    //conn.disconnect();
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        
        
        return saveFilePath;
    }    
    
    
    /**
     * 这个方法应该在应用程序启动时,main函数调用此方法配置https，确保应用程序只调用一次就可以
     * @throws Exception 
     */
    public static void trustAllHttpsCertificates() throws Exception {
        TrustManager[] trustAllCerts =new TrustManager[]{new TrustAllHttpsCertificatesTM()};
        SSLContext sc = SSLContext.getInstance("SSL");
        sc.init(null, trustAllCerts, null);
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        HttpsURLConnection.setDefaultHostnameVerifier(
            new HostnameVerifier() {
                @Override
                public boolean verify(String urlHostName, SSLSession session) {
                    return true;
                }
            }
        );
    }    
    
    
    public static class TrustAllHttpsCertificatesTM implements TrustManager,X509TrustManager {
        
        @Override
        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
            return null;
        }

        public boolean isServerTrusted(java.security.cert.X509Certificate[] certs) {
            return true;
        }

        public boolean isClientTrusted(java.security.cert.X509Certificate[] certs) {
            return true;
        }

        @Override
        public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType)
                throws java.security.cert.CertificateException {
        }

        @Override
        public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType)
                throws java.security.cert.CertificateException {
        }
        
    }    
    
    
}
