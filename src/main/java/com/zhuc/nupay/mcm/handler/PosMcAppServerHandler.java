/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.zhuc.nupay.mcm.handler;

import com.zhuc.nupay.mcm.PosMcApp;
import com.zhuc.nupay.mcm.messages.BatchOpRequest;
import com.zhuc.nupay.mcm.messages.InquiryRequest;
import com.zhuc.nupay.mcm.messages.ReFundRequest;
import com.zhuc.nupay.mcm.messages.SubmitRequest;
import com.zhuc.nupay.mcm.messages.UpdateDataRequest;
import com.zhuc.nupay.mcm.messages.base.Message;
import org.apache.log4j.Logger;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;

/**
 *
 * @author Aron
 */
public class PosMcAppServerHandler  extends IoHandlerAdapter
{
    private static final Logger log = Logger.getLogger(PosMcAppServerHandler.class.getName());
    
    private PosMcApp app;
    
    @Override
    public void exceptionCaught( IoSession session, Throwable cause ) throws Exception
    {
        log.error("Exception Caughted");
        cause.printStackTrace();
    }
    @Override
    public void messageReceived( IoSession session, Object message ) throws Exception
    {
//        PosMcResponse response = new PosMcResponse();
//        response.setCmd(PosMcCommand.INQUERY);
//        response.setStatus(PosMcResponseStatus.FAILED);
//        response.setData("aoe".getBytes());
//        session.write(response);
         Message response=null;
         
         if(app==null) {
             log.error("not set app processor,message will not be processed");
             return ;
         }
         
         if(message instanceof InquiryRequest){
             response =(Message)getApp().Inquiry((InquiryRequest)message);   
         }
         
         else if (message instanceof ReFundRequest) {
            response = (Message) getApp().Refund((ReFundRequest) message);

         }
         
         else if(message instanceof SubmitRequest){
             response= (Message)getApp().Submit((SubmitRequest)message);
         }
         
         else if(message instanceof BatchOpRequest){
             response = (Message)getApp().BatchOp((BatchOpRequest)message);
         }
         else if (message instanceof UpdateDataRequest) {
            response = (Message) getApp().Update((UpdateDataRequest) message);
        }
         
         if(response!=null)
             session.write(response);
    }
    
    @Override
    public void sessionIdle( IoSession session, IdleStatus status ) throws Exception
    {
       log.debug("IDLE " + session.getIdleCount( status ));
    }

    /**
     * @return the app
     */
    public PosMcApp getApp() {
        return app;
    }

    /**
     * @param app the app to set
     */
    public void setApp(PosMcApp app) {
        this.app = app;
    }
   
}