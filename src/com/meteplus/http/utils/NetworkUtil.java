/*
 * NetworkUtils.java
 * 
 * Created on 2007-11-14, 10:48:23
 * 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.meteplus.http.utils;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
 
public class NetworkUtil {

    public static HttpURLConnection getConnection(String urlString, Proxy proxy) throws IOException {
        
            //HttpURLConnection.setFollowRedirects(false);
            URL url = new URL(urlString);
            HttpURLConnection connection;
            if (proxy == null){
                connection = (HttpURLConnection) url.openConnection();
            }else {
                connection = (HttpURLConnection) proxy.getConnection(url);
            }
            return connection;
            
    }

}
