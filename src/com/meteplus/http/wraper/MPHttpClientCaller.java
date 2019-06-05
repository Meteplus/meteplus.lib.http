/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.meteplus.http.wraper;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.meteplus.http.HttpRequest;
import com.meteplus.http.MPHttpClient;
import java.util.HashMap;

/**
 * 这个封装是比较简单的，只是为了调用前端的方便
 * 因为只是对我们自己服务器的简单访问，而我们的服务器暂时还没有做401,429这些特殊情况返回，
 * 所以只考虑网络出异常时进行循环重复尝试，其他情况不做考虑
 * 这个封装不具有任何一般性
 * 调用者可以以此为例，写出自己的适合自己当下应用场景的封装包
 * @author HuangMing
 */
public class MPHttpClientCaller {

    //-----------------------------get-------------------------------------
    
    /**
     * 一次性http请求
     * 无论请求是否成功，过程中是否异常，执行一次后，直接返回
     * @param url
     * @return 
     */
    public static String doDisposableHttpGet(String url){
        return new MPHttpClient().newCall(new HttpRequest().url(url).build()).getResponseBody();
    }     
    
    public static JSONObject doDisposableHttpGetForJsonObj(String url){
        return JSONObject.parseObject(doDisposableHttpGet(url));
    }
    
    public static JSONArray doDisposableHttpGetForJsonArray(String url){
        return JSONObject.parseArray(doDisposableHttpGet(url));
    }        
    
    //----------------------------post-----------------------------------
    
    public static String doDisposableHttpPost(String url,JSONObject jsonObj){
        
       return new MPHttpClient().newCall(new HttpRequest().url(url).post().addJsonObj(jsonObj).build()).getResponseBody();

    }       
        
    public static String doDisposableHttpPost(String url,HashMap<String,Object> paras){
        
        HttpRequest req=new HttpRequest().url(url).post();
        for (String key : paras.keySet()) {
            Object value=paras.get(key);
            req.addFormData(key, value);
        }
        return new MPHttpClient().newCall(req.build()).getResponseBody();
    }          
        
    public static String doDisposableHttpPost(String url,String key,Object value){
        HashMap<String,Object> paras=new HashMap<>();
        paras.put(key, value);
        return doDisposableHttpPost(url,paras);
    }        

    public static JSONObject doDisposableHttpPostForJsonObject(String url,String key,String value){
        return JSONObject.parseObject(doDisposableHttpPost(url,key,value));
    }    
    
    public static JSONArray doDisposableHttpPostForJsonArray(String url,String key,String value){
        return JSONObject.parseArray(doDisposableHttpPost(url,key,value));
    }        
    
    public static JSONObject doDisposableHttpPostForJsonObject(String url,JSONObject jsonObj){
        return JSONObject.parseObject(doDisposableHttpPost(url,jsonObj));
    }      
    
    public static JSONArray doDisposableHttpPostForJsonArray(String url,JSONObject jsonObj){
        return JSONObject.parseArray(doDisposableHttpPost(url,jsonObj));
    } 
    
    public static JSONObject doDisposableHttpPostForJsonObject(String url,HashMap<String,Object> paras){
        return JSONObject.parseObject(doDisposableHttpPost(url,paras));
    }      
    
    public static JSONArray doDisposableHttpPostForJsonArray(String url,HashMap<String,Object> paras){
        return JSONObject.parseArray(doDisposableHttpPost(url,paras));
    }       
        
    
    //------------------------------put-----------------------------
    
    public static String doDisposableHttpPut(String url,HashMap<String,Object> paras){
        
        HttpRequest req=new HttpRequest().url(url).put();
        for (String key : paras.keySet()) {
            Object value=paras.get(key);
            req.addFormData(key, value);
        }
        paras.clear();
        
        return new MPHttpClient().newCall(req.build()).getResponseBody();
    }        
    
    public static String doDisposableHttpPut(String url,JSONObject jsonObj){
       return new MPHttpClient().newCall(new HttpRequest().url(url).put().addJsonObj(jsonObj).build()).getResponseBody();
    }        
        
    public static String doDisposableHttpPut(String url,String key,Object value){
        HashMap<String,Object> paras=new HashMap<>();
        paras.put(key, value);
        return doDisposableHttpPut(url,paras);
    }          
        
    public static JSONObject doDisposableHttpPutForJsonObject(String url,String key,String value){
        return JSONObject.parseObject(doDisposableHttpPut(url,key,value));
    }    
    
    public static JSONArray doDisposableHttpPutForJsonArray(String url,String key,String value){
        return JSONObject.parseArray(doDisposableHttpPut(url,key,value));
    }        
    
    public static JSONObject doDisposableHttpPutForJsonObject(String url,JSONObject jsonObj){
        return JSONObject.parseObject(doDisposableHttpPut(url,jsonObj));
    }      
    
    public static JSONArray doDisposableHttpPutForJsonArray(String url,JSONObject jsonObj){
        return JSONObject.parseArray(doDisposableHttpPut(url,jsonObj));
    } 
    
    public static JSONObject doDisposableHttpPutForJsonObject(String url,HashMap<String,Object> paras){
        return JSONObject.parseObject(doDisposableHttpPut(url,paras));
    }      
    
    public static JSONArray doDisposableHttpPutForJsonArray(String url,HashMap<String,Object> paras){
        return JSONObject.parseArray(doDisposableHttpPut(url,paras));
    }           
    
        
    //----------------------delete--------------------
    public static String doDisposableHttpDelete(String url){
        return new MPHttpClient().newCall(new HttpRequest().delete().url(url).build()).getResponseBody();
    }     
    
    public static JSONObject doDisposableHttpDeleteForJsonObj(String url){
        return JSONObject.parseObject(doDisposableHttpGet(url));
    }
    
    public static JSONArray doDisposableHttpDeleteForJsonArray(String url){
        return JSONObject.parseArray(doDisposableHttpGet(url));
    }          
    
    
    //===========================================================================
    //
    //                          可复用httpclient请求
    //
    //===========================================================================
    
    //------------------------get--------------------------
    
    /**
     * 1、请求网络正常，读写正常，返回200-209.则一次请求成功，返回结果给客户端
     * 2、请求失败（网络或者http返回非200-209状态值）,客户端没有设置异常处理器，
     *    相当于doDisposableHttpGet无论执行成功与否，都只执行一次，就返回
     * --------------------------------------------------------------------------
     *                      以下是请求失败的各种情况处理
     * --------------------------------------------------------------------------
     * 3、网络正常，那么要求客户端处理http服务器返回非200-209状态值的情况
     * 
     *    如果HttpResponseHandlePlan设置了新的HttpRequest(重新设置URL，cookie，headers等）,则用新的request请求，
     *    否则用老的request继续请求但是cookies可能部分已经被服务器的返回重置了，请注意这一点！！！
     * 
     *    如果HttpResponseHandlePlan设置了新的tryAgain,
     *    则用设置新的循环判断，否则使用默认的无限循环tryAgain==true;
     * 
     * 4、网络不正常，有两种情况，一种是连接、写入数据时出现异常（尚未获得服务器返回）
     *                           一种是读取服务器返回信息时，出现网络异常或者操作异常（空指针操作等）
     * 
     * @param url
     * @param handler
     * @return 
     */
    public static String doReusableHttpGet(String url,HttpErrorResponseHandler handler){
        
        if(handler==null) throw new NullPointerException("HttpErrorResponseHandler 不能为空！");
        
        MPHttpClient mpHttpClient=new MPHttpClient();
        HttpRequest req=new HttpRequest().url(url);
        boolean tryAgain=true;
        
        while(tryAgain){
            
            //复用httpClient对象
            mpHttpClient.resetAllStatus();            
            
            //网络异常，关闭之前，连接、读、写均正常而且返回http状态码200
            if(mpHttpClient.newCall(req.build()).isHttpSuccess()){
                break;
            }  
            
            HttpResponseHandlePlan plan;
            //网络是正常的，那么只要处理http非200-209返回值的情况
            if(mpHttpClient.isIoSuccess()){

                plan=handler.handleHttpErrorResponse(req,mpHttpClient.getHttpRespCode()); 

            }else {//网络不正常，则有两种可能，一种发送请求前出现异常。一种是读取返回数据时出了异常

                if(mpHttpClient.getHttpRespCode().code>0){//读取返回数据时出了异常

                    plan=handler.handleIOExceptionInResponseProcess(req,mpHttpClient.getIoRespCode()); 

                }else{//发送请求前出现异常

                    plan=handler.handleIOExceptionBeforeResponse(req,mpHttpClient.getIoRespCode()); 

                }

            }

            if(plan!=null){

                tryAgain=plan.isTryAgain();
                if(mpHttpClient.getConnectTimOut()!=plan.getConnectTimeout()){
                    mpHttpClient.setConnectTimeOut(plan.getConnectTimeout());
                }

                if(mpHttpClient.getReadTimeOut()!=plan.getReadTimeout()){
                    mpHttpClient.setReadTimeOut(plan.getReadTimeout());
                }


                if(tryAgain){

                    HttpRequest newReq=plan.getNewRequest();
                    if(newReq!=null){

                        req=newReq;//用新的请求进行尝试

                    }else{
                        //do nothing
                        //继续使用老请求，老设置进行循环尝试
                        //但是cookies可能部分已经被服务器的返回重置了，请注意这一点！！！
                    }
                }else{//tryAgain为false getNewRequest（）的值被忽略。客户端用来跳出请求循环。
                    //不做任何操作，结束循环
                }

            }else{
                    //do nothing
                    //继续使用老请求，老设置进行循环尝试
                    //但是cookies可能部分已经被服务器的返回重置了，请注意这一点！！！        
            }

            
        }
  
        return mpHttpClient.getResponseBody();
       
    }     
    
    
    public static JSONObject doReusableHttpGetJsonObj(String url,HttpErrorResponseHandler handler){
        return JSONObject.parseObject(doReusableHttpGet(url,handler));
    }
    
    public static JSONArray doReusableHttpGetJsonArray(String url,HttpErrorResponseHandler handler){
        return JSONObject.parseArray(doReusableHttpGet(url,handler));
    }      
    
    
    //------------------------delete----------------------
    public static String doReusableHttpDelete(String url,HttpErrorResponseHandler handler){
        
        if(handler==null) throw new NullPointerException("HttpErrorResponseHandler 不能为空！");
        
        MPHttpClient mpHttpClient=new MPHttpClient();
        HttpRequest req=new HttpRequest().url(url).delete();
        boolean tryAgain=true;
        
        while(tryAgain){
            
            //复用httpClient对象
            mpHttpClient.resetAllStatus();            
            
            //网络异常，关闭之前，连接、读、写均正常而且返回http状态码200
            if(mpHttpClient.newCall(req.build()).isHttpSuccess()){
                break;
            }  
            
            HttpResponseHandlePlan plan;
            //网络是正常的，那么只要处理http非200-209返回值的情况
            if(mpHttpClient.isIoSuccess()){

                plan=handler.handleHttpErrorResponse(req,mpHttpClient.getHttpRespCode()); 

            }else {//网络不正常，则有两种可能，一种发送请求前出现异常。一种是读取返回数据时出了异常

                if(mpHttpClient.getHttpRespCode().code>0){//读取返回数据时出了异常

                    plan=handler.handleIOExceptionInResponseProcess(req,mpHttpClient.getIoRespCode()); 

                }else{//发送请求前出现异常

                    plan=handler.handleIOExceptionBeforeResponse(req,mpHttpClient.getIoRespCode()); 

                }

            }

            if(plan!=null){

                tryAgain=plan.isTryAgain();
                if(mpHttpClient.getConnectTimOut()!=plan.getConnectTimeout()){
                    mpHttpClient.setConnectTimeOut(plan.getConnectTimeout());
                }

                if(mpHttpClient.getReadTimeOut()!=plan.getReadTimeout()){
                    mpHttpClient.setReadTimeOut(plan.getReadTimeout());
                }


                if(tryAgain){

                    HttpRequest newReq=plan.getNewRequest();
                    if(newReq!=null){

                        req=newReq;//用新的请求进行尝试

                    }else{
                        //do nothing
                        //继续使用老请求，老设置进行循环尝试
                        //但是cookies可能部分已经被服务器的返回重置了，请注意这一点！！！
                    }
                }else{//tryAgain为false getNewRequest（）的值被忽略。客户端用来跳出请求循环。
                    //不做任何操作，结束循环
                }

            }else{
                    //do nothing
                    //继续使用老请求，老设置进行循环尝试
                    //但是cookies可能部分已经被服务器的返回重置了，请注意这一点！！！        
            }

            
        }
  
        return mpHttpClient.getResponseBody();
       
    }     
    
    
    public static JSONObject doReusableHttpDeleteJsonObj(String url,HttpErrorResponseHandler handler){
        return JSONObject.parseObject(doReusableHttpGet(url,handler));
    }
    
    public static JSONArray doReusableHttpDeleteJsonArray(String url,HttpErrorResponseHandler handler){
        return JSONObject.parseArray(doReusableHttpGet(url,handler));
    }      
    
    
    
    
    //------------------------post------------------------

    /**
     * 1、请求网络正常，读写正常，返回200-209.则一次请求成功，返回结果给客户端
     * 2、请求失败（网络或者http返回非200-209状态值）,客户端没有设置异常处理器，
     *    相当于doDisposableHttpGet无论执行成功与否，都只执行一次，就返回
     * --------------------------------------------------------------------------
     *                      以下是请求失败的各种情况处理
     * --------------------------------------------------------------------------
     * 3、网络正常，那么要求客户端处理http服务器返回非200-209状态值的情况
     * 
     *    如果HttpResponseHandlePlan设置了新的HttpRequest(重新设置URL，cookie，headers等）,则用新的request请求，
     *    否则用老的request继续请求但是cookies可能部分已经被服务器的返回重置了，请注意这一点！！！
     * 
     *    如果HttpResponseHandlePlan设置了新的tryAgain,
     *    则用设置新的循环判断，否则使用默认的无限循环tryAgain==true;
     * 
     * 4、网络不正常，有两种情况，一种是连接、写入数据时出现异常（尚未获得服务器返回）
     *                           一种是读取服务器返回信息时，出现网络异常或者操作异常（空指针操作等）
     * 
     * @param url
     * @param jsonObj
     * @param handler
     * @return 
     */
    public static String doReusableHttpPost(String url,JSONObject jsonObj,HttpErrorResponseHandler handler){
        
        if(handler==null) throw new NullPointerException("HttpErrorResponseHandler 不能为空！");
        
        MPHttpClient mpHttpClient=new MPHttpClient();
        HttpRequest req=new HttpRequest().url(url).addJsonObj(jsonObj); 
        boolean tryAgain=true;
        
        while(tryAgain){
            
            //复用httpClient对象
            mpHttpClient.resetAllStatus();            
            
            //网络异常，关闭之前，连接、读、写均正常而且返回http状态码200
            if(mpHttpClient.newCall(req.post().build()).isHttpSuccess()){
                break;
            }  
            
            HttpResponseHandlePlan plan;
            //网络是正常的，那么只要处理http非200-209返回值的情况
            if(mpHttpClient.isIoSuccess()){

                plan=handler.handleHttpErrorResponse(req,mpHttpClient.getHttpRespCode()); 

            }else {//网络不正常，则有两种可能，一种发送请求前出现异常。一种是读取返回数据时出了异常

                if(mpHttpClient.getHttpRespCode().code>0){//读取返回数据时出了异常

                    plan=handler.handleIOExceptionInResponseProcess(req,mpHttpClient.getIoRespCode()); 

                }else{//发送请求前出现异常

                    plan=handler.handleIOExceptionBeforeResponse(req,mpHttpClient.getIoRespCode()); 

                }

            }

            if(plan!=null){

                tryAgain=plan.isTryAgain();
                if(mpHttpClient.getConnectTimOut()!=plan.getConnectTimeout()){
                    mpHttpClient.setConnectTimeOut(plan.getConnectTimeout());
                }

                if(mpHttpClient.getReadTimeOut()!=plan.getReadTimeout()){
                    mpHttpClient.setReadTimeOut(plan.getReadTimeout());
                }


                if(tryAgain){

                    HttpRequest newReq=plan.getNewRequest();
                    if(newReq!=null){

                        req=newReq;//用新的请求进行尝试

                    }else{
                        //do nothing
                        //继续使用老请求，老设置进行循环尝试
                        //但是cookies可能部分已经被服务器的返回重置了，请注意这一点！！！
                    }
                }else{//tryAgain为false getNewRequest（）的值被忽略。客户端用来跳出请求循环。
                    //不做任何操作，结束循环
                }

            }else{
                    //do nothing
                    //继续使用老请求，老设置进行循环尝试
                    //但是cookies可能部分已经被服务器的返回重置了，请注意这一点！！！        
            }

            
        }
  
        return mpHttpClient.getResponseBody();
       
    }         
    

    
    /**
     * 1、请求网络正常，读写正常，返回200-209.则一次请求成功，返回结果给客户端
     * 2、请求失败（网络或者http返回非200-209状态值）,客户端没有设置异常处理器，
     *    相当于doDisposableHttpGet无论执行成功与否，都只执行一次，就返回
     * --------------------------------------------------------------------------
     *                      以下是请求失败的各种情况处理
     * --------------------------------------------------------------------------
     * 3、网络正常，那么要求客户端处理http服务器返回非200-209状态值的情况
     * 
     *    如果HttpResponseHandlePlan设置了新的HttpRequest(重新设置URL，cookie，headers等）,则用新的request请求，
     *    否则用老的request继续请求但是cookies可能部分已经被服务器的返回重置了，请注意这一点！！！
     * 
     *    如果HttpResponseHandlePlan设置了新的tryAgain,
     *    则用设置新的循环判断，否则使用默认的无限循环tryAgain==true;
     * 
     * 4、网络不正常，有两种情况，一种是连接、写入数据时出现异常（尚未获得服务器返回）
     *                           一种是读取服务器返回信息时，出现网络异常或者操作异常（空指针操作等）
     * 
     * @param url
     * @param paras
     * @param handler
     * @return 
     */
    public static String doReusableHttpPost(String url,HashMap<String,Object> paras,HttpErrorResponseHandler handler){
        
        if(handler==null) throw new NullPointerException("HttpErrorResponseHandler 不能为空！");
        
        MPHttpClient mpHttpClient=new MPHttpClient();
        HttpRequest req=new HttpRequest().url(url); 
        for (String key : paras.keySet()) {
            Object value=paras.get(key);
            req.addFormData(key, value);
        }        
        boolean tryAgain=true;
        
        while(tryAgain){
            
            //复用httpClient对象
            mpHttpClient.resetAllStatus();            
            
            //网络异常，关闭之前，连接、读、写均正常而且返回http状态码200
            if(mpHttpClient.newCall(req.post().build()).isHttpSuccess()){
                break;
            }  
            
            HttpResponseHandlePlan plan;
            //网络是正常的，那么只要处理http非200-209返回值的情况
            if(mpHttpClient.isIoSuccess()){

                plan=handler.handleHttpErrorResponse(req,mpHttpClient.getHttpRespCode()); 

            }else {//网络不正常，则有两种可能，一种发送请求前出现异常。一种是读取返回数据时出了异常

                if(mpHttpClient.getHttpRespCode().code>0){//读取返回数据时出了异常

                    plan=handler.handleIOExceptionInResponseProcess(req,mpHttpClient.getIoRespCode()); 

                }else{//发送请求前出现异常

                    plan=handler.handleIOExceptionBeforeResponse(req,mpHttpClient.getIoRespCode()); 

                }

            }

            if(plan!=null){

                tryAgain=plan.isTryAgain();
                if(mpHttpClient.getConnectTimOut()!=plan.getConnectTimeout()){
                    mpHttpClient.setConnectTimeOut(plan.getConnectTimeout());
                }

                if(mpHttpClient.getReadTimeOut()!=plan.getReadTimeout()){
                    mpHttpClient.setReadTimeOut(plan.getReadTimeout());
                }


                if(tryAgain){

                    HttpRequest newReq=plan.getNewRequest();
                    if(newReq!=null){

                        req=newReq;//用新的请求进行尝试

                    }else{
                        //do nothing
                        //继续使用老请求，老设置进行循环尝试
                        //但是cookies可能部分已经被服务器的返回重置了，请注意这一点！！！
                    }
                }else{//tryAgain为false getNewRequest（）的值被忽略。客户端用来跳出请求循环。
                    //不做任何操作，结束循环
                }

            }else{
                    //do nothing
                    //继续使用老请求，老设置进行循环尝试
                    //但是cookies可能部分已经被服务器的返回重置了，请注意这一点！！！        
            }

            
        }
  
        return mpHttpClient.getResponseBody();
       
    }         
    

    public static String doReusableHttpPost(String url,String key,Object value,HttpErrorResponseHandler handler){
        HashMap<String,Object> paras=new HashMap<>();
        paras.put(key, value);
        return doReusableHttpPost(url,paras,handler);
    }        
    
    public static JSONObject doReusableHttpPostForJsonObject(String url,String key,String value,HttpErrorResponseHandler handler){
        return JSONObject.parseObject(doReusableHttpPost(url,key,value,handler));
    }    
    
    public static JSONArray doReusableHttpPostForJsonArray(String url,String key,String value,HttpErrorResponseHandler handler){
        return JSONObject.parseArray(doReusableHttpPost(url,key,value,handler));
    }        
    
    public static JSONObject doReusableHttpPostForJsonObject(String url,JSONObject jsonObj,HttpErrorResponseHandler handler){
        return JSONObject.parseObject(doReusableHttpPost(url,jsonObj,handler));
    }      
    
    public static JSONArray doReusableHttpPostForJsonArray(String url,JSONObject jsonObj,HttpErrorResponseHandler handler){
        return JSONObject.parseArray(doReusableHttpPost(url,jsonObj,handler));
    } 
    
    public static JSONObject doReusableHttpPostForJsonObject(String url,HashMap<String,Object> paras,HttpErrorResponseHandler handler){
        return JSONObject.parseObject(doReusableHttpPost(url,paras,handler));
    }      
    
    public static JSONArray doReusableHttpPostForJsonArray(String url,HashMap<String,Object> paras,HttpErrorResponseHandler handler){
        return JSONObject.parseArray(doReusableHttpPost(url,paras,handler));
    }          
        
    //------------------------put--------------------------------
    
    /**
     * 1、请求网络正常，读写正常，返回200-209.则一次请求成功，返回结果给客户端
     * 2、请求失败（网络或者http返回非200-209状态值）,客户端没有设置异常处理器，
     *    相当于doDisposableHttpGet无论执行成功与否，都只执行一次，就返回
     * --------------------------------------------------------------------------
     *                      以下是请求失败的各种情况处理
     * --------------------------------------------------------------------------
     * 3、网络正常，那么要求客户端处理http服务器返回非200-209状态值的情况
     * 
     *    如果HttpResponseHandlePlan设置了新的HttpRequest(重新设置URL，cookie，headers等）,则用新的request请求，
     *    否则用老的request继续请求但是cookies可能部分已经被服务器的返回重置了，请注意这一点！！！
     * 
     *    如果HttpResponseHandlePlan设置了新的tryAgain,
     *    则用设置新的循环判断，否则使用默认的无限循环tryAgain==true;
     * 
     * 4、网络不正常，有两种情况，一种是连接、写入数据时出现异常（尚未获得服务器返回）
     *                           一种是读取服务器返回信息时，出现网络异常或者操作异常（空指针操作等）
     * 
     * @param url
     * @param jsonObj
     * @param handler
     * @return 
     */
    public static String doReusableHttpPut(String url,JSONObject jsonObj,HttpErrorResponseHandler handler){
        
        if(handler==null) throw new NullPointerException("HttpErrorResponseHandler 不能为空！");
        
        MPHttpClient mpHttpClient=new MPHttpClient();
        HttpRequest req=new HttpRequest().url(url).addJsonObj(jsonObj); 
        boolean tryAgain=true;
        
        while(tryAgain){
            
            //复用httpClient对象
            mpHttpClient.resetAllStatus();            
            
            //网络异常，关闭之前，连接、读、写均正常而且返回http状态码200
            if(mpHttpClient.newCall(req.put().build()).isHttpSuccess()){
                break;
            }  
            
            HttpResponseHandlePlan plan;
            //网络是正常的，那么只要处理http非200-209返回值的情况
            if(mpHttpClient.isIoSuccess()){

                plan=handler.handleHttpErrorResponse(req,mpHttpClient.getHttpRespCode()); 

            }else {//网络不正常，则有两种可能，一种发送请求前出现异常。一种是读取返回数据时出了异常

                if(mpHttpClient.getHttpRespCode().code>0){//读取返回数据时出了异常

                    plan=handler.handleIOExceptionInResponseProcess(req,mpHttpClient.getIoRespCode()); 

                }else{//发送请求前出现异常

                    plan=handler.handleIOExceptionBeforeResponse(req,mpHttpClient.getIoRespCode()); 

                }

            }

            if(plan!=null){

                tryAgain=plan.isTryAgain();
                if(mpHttpClient.getConnectTimOut()!=plan.getConnectTimeout()){
                    mpHttpClient.setConnectTimeOut(plan.getConnectTimeout());
                }

                if(mpHttpClient.getReadTimeOut()!=plan.getReadTimeout()){
                    mpHttpClient.setReadTimeOut(plan.getReadTimeout());
                }


                if(tryAgain){

                    HttpRequest newReq=plan.getNewRequest();
                    if(newReq!=null){

                        req=newReq;//用新的请求进行尝试

                    }else{
                        //do nothing
                        //继续使用老请求，老设置进行循环尝试
                        //但是cookies可能部分已经被服务器的返回重置了，请注意这一点！！！
                    }
                }else{//tryAgain为false getNewRequest（）的值被忽略。客户端用来跳出请求循环。
                    //不做任何操作，结束循环
                }

            }else{
                    //do nothing
                    //继续使用老请求，老设置进行循环尝试
                    //但是cookies可能部分已经被服务器的返回重置了，请注意这一点！！！        
            }

            
        }
  
        return mpHttpClient.getResponseBody();
       
    }         
    

    /**
     * 1、请求网络正常，读写正常，返回200-209.则一次请求成功，返回结果给客户端
     * 2、请求失败（网络或者http返回非200-209状态值）,客户端没有设置异常处理器，
     *    相当于doDisposableHttpGet无论执行成功与否，都只执行一次，就返回
     * --------------------------------------------------------------------------
     *                      以下是请求失败的各种情况处理
     * --------------------------------------------------------------------------
     * 3、网络正常，那么要求客户端处理http服务器返回非200-209状态值的情况
     * 
     *    如果HttpResponseHandlePlan设置了新的HttpRequest(重新设置URL，cookie，headers等）,则用新的request请求，
     *    否则用老的request继续请求但是cookies可能部分已经被服务器的返回重置了，请注意这一点！！！
     * 
     *    如果HttpResponseHandlePlan设置了新的tryAgain,
     *    则用设置新的循环判断，否则使用默认的无限循环tryAgain==true;
     * 
     * 4、网络不正常，有两种情况，一种是连接、写入数据时出现异常（尚未获得服务器返回）
     *                           一种是读取服务器返回信息时，出现网络异常或者操作异常（空指针操作等）
     * 
     * @param url
     * @param paras
     * @param handler
     * @return 
     */
    public static String doReusableHttpPut(String url,HashMap<String,Object> paras,HttpErrorResponseHandler handler){
        
        if(handler==null) throw new NullPointerException("HttpErrorResponseHandler 不能为空！");
        
        MPHttpClient mpHttpClient=new MPHttpClient();
        HttpRequest req=new HttpRequest().url(url); 
        for (String key : paras.keySet()) {
            Object value=paras.get(key);
            req.addFormData(key, value);
        }        
        boolean tryAgain=true;
        
        while(tryAgain){
            
            //复用httpClient对象
            mpHttpClient.resetAllStatus();            
            
            //网络异常，关闭之前，连接、读、写均正常而且返回http状态码200
            if(mpHttpClient.newCall(req.put().build()).isHttpSuccess()){
                break;
            }  
            
            HttpResponseHandlePlan plan;
            //网络是正常的，那么只要处理http非200-209返回值的情况
            if(mpHttpClient.isIoSuccess()){

                plan=handler.handleHttpErrorResponse(req,mpHttpClient.getHttpRespCode()); 

            }else {//网络不正常，则有两种可能，一种发送请求前出现异常。一种是读取返回数据时出了异常

                if(mpHttpClient.getHttpRespCode().code>0){//读取返回数据时出了异常

                    plan=handler.handleIOExceptionInResponseProcess(req,mpHttpClient.getIoRespCode()); 

                }else{//发送请求前出现异常

                    plan=handler.handleIOExceptionBeforeResponse(req,mpHttpClient.getIoRespCode()); 

                }

            }

            if(plan!=null){

                tryAgain=plan.isTryAgain();
                if(mpHttpClient.getConnectTimOut()!=plan.getConnectTimeout()){
                    mpHttpClient.setConnectTimeOut(plan.getConnectTimeout());
                }

                if(mpHttpClient.getReadTimeOut()!=plan.getReadTimeout()){
                    mpHttpClient.setReadTimeOut(plan.getReadTimeout());
                }


                if(tryAgain){

                    HttpRequest newReq=plan.getNewRequest();
                    if(newReq!=null){

                        req=newReq;//用新的请求进行尝试

                    }else{
                        //do nothing
                        //继续使用老请求，老设置进行循环尝试
                        //但是cookies可能部分已经被服务器的返回重置了，请注意这一点！！！
                    }
                }else{//tryAgain为false getNewRequest（）的值被忽略。客户端用来跳出请求循环。
                    //不做任何操作，结束循环
                }

            }else{
                    //do nothing
                    //继续使用老请求，老设置进行循环尝试
                    //但是cookies可能部分已经被服务器的返回重置了，请注意这一点！！！        
            }

            
        }
  
        return mpHttpClient.getResponseBody();
       
    }         
        

         
    

    
    public static String doReusableHttpPut(String url,String key,Object value,HttpErrorResponseHandler handler){
        HashMap<String,Object> paras=new HashMap<>();
        paras.put(key, value);
        return doReusableHttpPut(url,paras,handler);
    }          
    
    public static JSONObject doReusableHttpPutForJsonObject(String url,String key,String value,HttpErrorResponseHandler handler){
        return JSONObject.parseObject(doReusableHttpPut(url,key,value,handler));
    }    
    
    public static JSONArray doReusableHttpPutForJsonArray(String url,String key,String value,HttpErrorResponseHandler handler){
        return JSONObject.parseArray(doReusableHttpPut(url,key,value,handler));
    }        
    
    public static JSONObject doReusableHttpPutForJsonObject(String url,JSONObject jsonObj,HttpErrorResponseHandler handler){
        return JSONObject.parseObject(doReusableHttpPut(url,jsonObj,handler));
    }      
    
    public static JSONArray doReusableHttpPutForJsonArray(String url,JSONObject jsonObj,HttpErrorResponseHandler handler){
        return JSONObject.parseArray(doReusableHttpPut(url,jsonObj,handler));
    } 
    
    public static JSONObject doReusableHttpPutForJsonObject(String url,HashMap<String,Object> paras,HttpErrorResponseHandler handler){
        return JSONObject.parseObject(doReusableHttpPut(url,paras,handler));
    }      
    
    public static JSONArray doReusableHttpPutForJsonArray(String url,HashMap<String,Object> paras,HttpErrorResponseHandler handler){
        return JSONObject.parseArray(doReusableHttpPut(url,paras,handler));
    }          
        
    
    
    
            
}
