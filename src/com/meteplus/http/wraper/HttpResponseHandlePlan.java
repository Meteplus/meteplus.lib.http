/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.meteplus.http.wraper;

import com.meteplus.http.HttpRequest;
import com.meteplus.http.MPHttpClient;

/**
 *
 * @author HuangMing
 */
public class HttpResponseHandlePlan {
    
    private boolean tryAgain=true;
    private HttpRequest newReq;
    private int connectTimemout=MPHttpClient.DEFAULT_TIMEOUT;
    private int readTimemout=MPHttpClient.DEFAULT_TIMEOUT;
    
    public HttpResponseHandlePlan(){
        tryAgain=true;
    }    
    
    public HttpResponseHandlePlan(HttpRequest newReq){
        this.newReq=newReq;
    }
    
    public HttpResponseHandlePlan(boolean tryAgain){
        this.tryAgain=tryAgain;
    }
    
    public HttpResponseHandlePlan(boolean tryAgain,HttpRequest newReq){
        this.tryAgain=tryAgain;
        this.newReq=newReq;
    }
    
    public boolean isTryAgain(){
        return tryAgain;
    }
    
    public HttpRequest getNewRequest(){
        return this.newReq;
    }
    
    public void setTryAgain(boolean b){
        this.tryAgain=b;
    }
    
    public void setNewRequest(HttpRequest newReq){
        this.newReq=newReq;
    }
    
    public void setConnectTime(int timeout){
        this.connectTimemout=timeout;
    }
    
    public int getConnectTimeout(){
        return this.connectTimemout;
    }
    
    public void setReadTimeout(int readTimeout){
        this.readTimemout=readTimeout;
    }
    
    public int getReadTimeout(){
        return this.readTimemout;
    }
    
}
