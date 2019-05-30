/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.meteplus.http.wraper;

import com.meteplus.http.HttpRequest;

/**
 *
 * @author HuangMing
 */
public interface HttpErrorResponseHandler {
    
    /**
     * @param req
     * @param respCode
     * @return 
     */
    public HttpResponseHandlePlan handleHttpErrorResponse(HttpRequest req,int respCode);
    
    /**
     * 
     * @param req
     * @param errorCode
     * @return 
     */
    public HttpResponseHandlePlan handleIOExceptionBeforeResponse(HttpRequest req,int errorCode);
    
    /**
     * 
     * @param req
     * @param errorCode
     * @return 
     */
    public HttpResponseHandlePlan handleIOExceptionInResponseProcess(HttpRequest req,int errorCode);
    
}
