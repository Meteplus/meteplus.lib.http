/*
 * Proxy.java
 * 
 * Created on 2007-11-14, 10:47:21
 * 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.meteplus.http.utils;
import java.io.IOException;
import java.io.Serializable;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
 
public class Proxy extends java.net.Proxy implements Serializable {

        private final String url;
	private final int port;
	private final String user;
	private final String password;
	
	public Proxy(Type type, String url, int port, String user, String password)
                throws UnknownHostException, IOException {
		super(type, new Socket(url, port).getRemoteSocketAddress());
		this.url = url;
		this.port = port;
		this.user = user;
		this.password = password;
	}
	
	public URLConnection getConnection(URL url) throws IOException {
                URLConnection con = url.openConnection(this);
                sun.misc.BASE64Encoder encoder = new sun.misc.BASE64Encoder();
                String encodedUserPwd = encoder.encode((user + ':' + password).getBytes());
                con.setRequestProperty("Proxy-Authorization", "Basic " + encodedUserPwd);
                return con;
	}

	public String getPassword() {
		return password;
	}

	public String getUrl() {
		return url;
	}

	public int getPort() {
		return port;
	}

	public String getUser() {
		return user;
	}

}
