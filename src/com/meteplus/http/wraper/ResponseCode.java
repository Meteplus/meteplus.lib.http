/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.meteplus.http.wraper;

/**
 *
 * @author HuangMing
 */
public class ResponseCode {
    public int code;
    public String msg;
    
    public ResponseCode(){}       
    
    public ResponseCode(int code,String msg){
        this.code=code;
        this.msg=msg;
    }
   
    
}
