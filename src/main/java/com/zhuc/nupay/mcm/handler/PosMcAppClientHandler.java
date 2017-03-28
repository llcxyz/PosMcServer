/*
 * Copyright (C) 2013 Aron/llc_yz@qq.com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package com.zhuc.nupay.mcm.handler;

import com.zhuc.nupay.mcm.messages.BatchOpResponse;
import com.zhuc.nupay.mcm.messages.InquiryResponse;
import com.zhuc.nupay.mcm.messages.ReFundResponse;
import com.zhuc.nupay.mcm.messages.SubmitResponse;
import com.zhuc.nupay.mcm.messages.UpdateDataResponse;
import org.apache.log4j.Logger;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;

/**
 *
 * @author Aron/llc_xyz@qq.com
 */
public class PosMcAppClientHandler extends IoHandlerAdapter{
    
    private static final Logger log = Logger.getLogger(PosMcAppClientHandler.class.getName());
    
   @Override
   public void sessionOpened(IoSession session) throws Exception {
        //session.setAttribute(INDEX_KEY, 0);
    }
     
    @Override
    public void messageReceived(IoSession session, Object message) throws Exception {
        
        if(message instanceof InquiryResponse){
            InquiryResponse response = (InquiryResponse)message;
            log.debug("查询积分返回->"+response.getStatus()+",错误消息:"+response.getErrmsg()+",总积分:"+response.getTotalPoints()
                    +",可用积分:"+response.getAvaiablePoints()+",定制消息:"+response.getCustomMsg());
            
           }
        else if (message instanceof SubmitResponse){
            SubmitResponse response = (SubmitResponse)message;
            log.debug("消费请求返回状态["+response.getStatus()+"],错误消息:" 
                    + response.getErrmsg()+",返利:"+response.getReBates()+",收款卡号:"+response.getReceivableCardNo()
                    +",交易号:"+response.getTxnId()+",总积分"+response.getTotalPoints()
                    +",可用积分:"+response.getAvaiablePoints()+",定制消息:"+response.getCustomerMsg());
            
        }
        else if (message instanceof ReFundResponse) {
            ReFundResponse response = (ReFundResponse)message;
            log.debug("退款请求返回状态["+response.getStatus()+"],错误消息:" + response.getErrmsg() +"->交易号"+response.getTxnId()+"]->总积分" + response.getTotalPoints()
                    + ",可用积分:" + response.getAvaiablePoints() + ",定制消息:" + response.getCustomerMsg());
        }
        else if (message instanceof BatchOpResponse) {
            BatchOpResponse response = (BatchOpResponse) message;
            log.debug("批量操作请求返回状态[" + response.getStatus() + "]"+"],错误消息:" + response.getErrmsg());
            
        }
        else if (message instanceof UpdateDataResponse){
            UpdateDataResponse response = (UpdateDataResponse )message;
            log.debug("批量操作请求返回状态[" + response.getStatus() 
                    + "],返利比例 [" +response.getRebate_percent()+"%],返利基数:"+response.getRebate_base()
                    +",服务器:"+response.getServer().trim()+":"+response.getPort()
                    +"],错误消息:" + response.getErrmsg());
            
        }
        
        log.debug("received message->"+message.getClass());
        
    }
    @Override
    public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
        //IoSessionLogger sessionLogger = IoSessionLogger.getLogger(session, logger);
        //sessionLogger.warn(cause.getMessage(), cause);
    }
}
