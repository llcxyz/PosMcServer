/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.zhuc.nupay.mcm;

import com.zhuc.nupay.mcm.codec.PosMcAppProtocolCodecFactory;
import com.zhuc.nupay.mcm.handler.PosMcAppClientHandler;
import java.net.InetSocketAddress;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.mina.core.RuntimeIoException;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

/**
 *
 * @author Aron/llc_xyz@qq.com
 */
public class PosMcAppClient implements Runnable{
    
    private static final int CONNECT_TIMEOUT = 3000;
    
    private String hostName;
    
    private int port;
    
    private IoSession session;
    
    
    public PosMcAppClient(){
    
    }
    public PosMcAppClient(String hostname,int port){
        this.hostName = hostname;
        this.port = port;
        
    }
            
    public void run(){
        NioSocketConnector connector = new NioSocketConnector();
        connector.setConnectTimeoutMillis(CONNECT_TIMEOUT);
        connector.getFilterChain().addLast("codec",new ProtocolCodecFilter(new PosMcAppProtocolCodecFactory(true)));
        connector.getFilterChain().addLast("logger", new LoggingFilter());
        connector.setHandler(new PosMcAppClientHandler());


        for (;;) {
            try {
                ConnectFuture future = connector.connect(new InetSocketAddress(hostName, port));
                future.awaitUninterruptibly();
                setSession(future.getSession());
                break;
            } catch (RuntimeIoException e) {
                System.err.println("Failed to connect "+this.getHostName()+":"+this.getPort());
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException ex) {
                    Logger.getLogger(PosMcAppClient.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

        // wait until the summation is done
        getSession().getCloseFuture().awaitUninterruptibly();
        connector.dispose();
    }
    
    public static void main(String[] argv){
        PosMcAppClient client= new PosMcAppClient();
        client.setHostName("127.0.0.1");
        client.setPort(9990);
        
    }

    /**
     * @return the HostName
     */
    public String getHostName() {
        return hostName;
    }

    /**
     * @param HostName the HostName to set
     */
    public void setHostName(String HostName) {
        this.hostName = HostName;
    }

    /**
     * @return the port
     */
    public int getPort() {
        return port;
    }

    /**
     * @param port the port to set
     */
    public void setPort(int port) {
        this.port = port;
    }

    /**
     * @return the session
     */
    public IoSession getSession() {
        return session;
    }

    /**
     * @param session the session to set
     */
    public void setSession(IoSession session) {
        this.session = session;
    }

  
}
