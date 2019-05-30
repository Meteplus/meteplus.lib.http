/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.meteplus.http.interfaces;

/**
 *
 * @author HuangMing
 */
public interface HttpProgressListener {
    /**
     * 更新进度
     * 
     * @param contentLength
     * @param progress
     * 当前完成了多少字节
     */
    public void update(long contentLength, long progress);

    public boolean isContinue();

    /**
     * 上传多少字节更新一次进度
     * 
     * @return
     */
    public int getBlockSize();    
}
