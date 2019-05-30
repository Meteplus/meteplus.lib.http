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
public class ErrCode {
    public int code;
    public String errMsg;
    
    public ErrCode(int code,String errMsg){
        this.code=code;
        this.errMsg=errMsg;
    }
    
}
