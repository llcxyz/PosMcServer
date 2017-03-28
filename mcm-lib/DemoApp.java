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

package com.zhuc.nupay.mcm.demo;

import com.zhuc.nupay.mcm.PosMcApp;
import com.zhuc.nupay.mcm.PosMcAppServer;
import com.zhuc.nupay.mcm.command.PosMcResponseStatus;
import com.zhuc.nupay.mcm.entity.TradeEntity;
import com.zhuc.nupay.mcm.messages.BatchOpRequest;
import com.zhuc.nupay.mcm.messages.BatchOpResponse;
import com.zhuc.nupay.mcm.messages.InquiryRequest;
import com.zhuc.nupay.mcm.messages.InquiryResponse;
import com.zhuc.nupay.mcm.messages.ReFundRequest;
import com.zhuc.nupay.mcm.messages.ReFundResponse;
import com.zhuc.nupay.mcm.messages.SubmitRequest;
import com.zhuc.nupay.mcm.messages.SubmitResponse;
import java.util.Date;
import org.apache.log4j.Logger;

/**
 * Pos机会员卡APP 服务端接口实现样例.
 * <li>用户只需要实现PosMcApp接口即可与Pos机APP通信</li>
 * 
 * <pre>
 * 
 * public class DemoApp implements PosMcApp{
 *   ...实现接口.
 * }
 * 
 * public static void main(String[] argv){
 *       PosMcAppServer server =new PosMcAppServer(9990);
 *       server.setApp(new DemoApp());
 *       Thread t = new Thread(server);
 *       t.start();
 *
 * }
 * </pre>
 * <ul> 
 * <li>服务端在收到消息后，会自动解包并封装成Request消息,然后根据指令调用PosMcApp的对应方法</li>
 * <li>用户的PosMcApp实现只需要返回封装好的Response对象.服务端会自动完成打包.并传送给客户端.
 * <li>如需了解打包/解包细节见{@link com.zhuc.nupay.mcm.PosMcAppServer}</li>
 * <ul>
 * 
 * @author Aron(llc_xyz@qq.com)
 */
public class DemoApp implements PosMcApp{

//    @Inject MemberDaoImpl mDao;
//    @Inject CardDaoImpl cardDao;
    
    private static final Logger log = Logger.getLogger(DemoApp.class.getName());
    
    public InquiryResponse Inquiry(InquiryRequest inqury) {
        
        System.out.println(" got card number "+inqury.getCardNo());
        InquiryResponse response = new InquiryResponse();
        response.setTotalPoints(9876);
        response.setAvaiablePoints(1234);
        response.setStatus(PosMcResponseStatus.SUCCESS);
       // response.setErrmsg("卡号不存在");
        response.setCustomMsg("Welcome Aron Lau");
        return response;
    }

    public SubmitResponse Submit(SubmitRequest submit) {
        log.debug(" submit request "+submit);
        log.debug(" merchant Id = "+submit.getMerchantId());
        log.debug(" terminal Id = "+submit.getTerminalId());
        log.debug(" car no =" +submit.getCardNo());
        log.debug(" amout = "+submit.getAmount());
        log.debug(" timestuamp ="+submit.getTimestamp());
        Date d = new Date();
        d.setTime(submit.getTimestamp());
        log.debug("date  = "+d.toString());
        
        SubmitResponse resp = new SubmitResponse();
        resp.setStatus(PosMcResponseStatus.SUCCESS);
        resp.setTotalPoints(20000);
        resp.setAvaiablePoints(1999);
        resp.setTxnId(9999999l);
        resp.setCustomerMsg("Hello,Ad from Server,Submit Ok");
        
        return resp;
        
       
    }

    public ReFundResponse Refund(ReFundRequest refund) {
        log.debug(" ReFound  request " + refund);
        
        log.debug(" merchant Id = " + refund.getMerchantId());
        log.debug(" terminal Id = " + refund.getTerminalId());
        log.debug(" car no =" + refund.getCardNo());
        log.debug(" amount = " + refund.getAmount());
        ReFundResponse resp = new ReFundResponse();
        resp.setStatus(PosMcResponseStatus.SUCCESS);
        resp.setTotalPoints(20000);
        resp.setAvaiablePoints(1923);
        resp.setCustomerMsg("Hello,ReFund Ok!!!");
        resp.setTxnId(9999999l);
        return resp;
    }

    public BatchOpResponse BatchOp(BatchOpRequest batreq) {
       
        log.debug("收到批量操作请求->商户ID:"+batreq.getMerchantId()
                +",终端ID:"+batreq.getTerminalId()
                +",批量记录数:"+batreq.getTrades().size());
        
       for(TradeEntity entity:batreq.getTrades()){
           
           log.debug("交易类型:" + entity.getTradeType());
           
           log.debug("交易卡号:"+entity.getCardNo());
           log.debug("交易金额:"+entity.getAmount());
           Date d  = new Date();
           d.setTime(entity.getTimestamp());
           log.debug("交易时间:"+d);
           
           
       }
       
       BatchOpResponse resp = new BatchOpResponse();
       
       resp.setStatus(PosMcResponseStatus.SUCCESS);
       
       return resp;
       
    }
    
    public static void main(String[] argv){
        PosMcAppServer server =new PosMcAppServer();
        server.setApp(new DemoApp());
        Thread t = new Thread(server);
        t.start();
       
    }
}
