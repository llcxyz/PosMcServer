/*
 * Copyright (C) 2014 Aron llc_xyz@qq.com
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

package com.zhuc.nupay.mcm.app;

import com.zhuc.nupay.mcm.PosMcApp;
import com.zhuc.nupay.mcm.PosMcAppServer;
import com.zhuc.nupay.mcm.app.jbei.HttpApiException;
import com.zhuc.nupay.mcm.app.jbei.JBeiApi;
import com.zhuc.nupay.mcm.app.jbei.JBeiBatchSubmitResponse;
import com.zhuc.nupay.mcm.app.jbei.JBeiCSSubmitResponse;
import com.zhuc.nupay.mcm.app.jbei.JBeiPointsDetailResponse;
import com.zhuc.nupay.mcm.app.jbei.JBeiRRQResponse;
import com.zhuc.nupay.mcm.app.jbei.JBeiTradeEntity;
import com.zhuc.nupay.mcm.command.PosMcCommand;
import com.zhuc.nupay.mcm.command.PosMcResponseStatus;
import com.zhuc.nupay.mcm.demo.DemoApp;
import com.zhuc.nupay.mcm.entity.TradeEntity;
import com.zhuc.nupay.mcm.messages.BatchOpRequest;
import com.zhuc.nupay.mcm.messages.BatchOpResponse;
import com.zhuc.nupay.mcm.messages.InquiryRequest;
import com.zhuc.nupay.mcm.messages.InquiryResponse;
import com.zhuc.nupay.mcm.messages.ReFundRequest;
import com.zhuc.nupay.mcm.messages.ReFundResponse;
import com.zhuc.nupay.mcm.messages.SubmitRequest;
import com.zhuc.nupay.mcm.messages.SubmitResponse;
import com.zhuc.nupay.mcm.messages.UpdateDataRequest;
import com.zhuc.nupay.mcm.messages.UpdateDataResponse;
import com.zhuc.nupay.mcm.misc.Tools;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *  Jbei App Server.
 * @author Aron llc_xyz@qq.com
 */
public class JBeiApp implements PosMcApp{

    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(JBeiApp.class.getName());
    
    private final JBeiApi api;
    
    private String server;
    
    private int port;
    
    private String access_key;
    
    private String access_mid;
    
    
    
    public  JBeiApp(Map config){
        
        this.initDefaultSetting();
        this.loadConfig(config);
        log.debug(" update Server=" + this.getServer() + ":" + this.getPort()
                +",Access _Key = "+this.getAccess_key()
                +",Access_MID="+this.getAccess_mid());
        
        api = new JBeiApi(this.getAccess_mid(),this.getAccess_key());
        
    }
    public  JBeiApp() {
        this.initDefaultSetting();
        log.debug(" update Server=" + this.getServer() + ":" + this.getPort());
        api = new JBeiApi(this.getAccess_mid(), this.getAccess_key());
    }
   
    private   void initDefaultSetting(){
        try {
            
            this.setServer(InetAddress.getLocalHost().getHostAddress());
            this.setPort(9991);
            
        } catch (UnknownHostException ex) {
            Logger.getLogger(JBeiApp.class.getName()).log(Level.SEVERE, null, ex);
        }
        
//        log.debug("默认设置,Server="+this.getServer()+":"+this.getPort());
        
    }
    public InquiryResponse Inquiry(InquiryRequest inqury) {
        InquiryResponse response = new InquiryResponse();
        
        try {
            log.debug("收到查询请求,卡号:"+inqury.getCardNo());
            
            JBeiPointsDetailResponse resp= api.PointsDetailQuery(inqury.getCardNo(), inqury.getMerchantId());
            if(resp.isSuccess()){
                response.setStatus(PosMcResponseStatus.SUCCESS);
                response.setAvaiablePoints(resp.getValidpoints());
                response.setTotalPoints(resp.getTotalPoints());
                log.debug("卡号:" + inqury.getCardNo()+",总积分:"+response.getTotalPoints()+",有效积分:"+response.getAvaiablePoints());
                
            }
            else{
                log.debug("卡号:" + inqury.getCardNo()+",查询积分失败:"+resp.getErrCode()+",("+resp.getErrMsg()+")");  
                response.setStatus(Tools.trErrCode(resp.getErrCode()));
                response.setErrmsg(resp.getErrMsg());
            }
            
            
        } catch (HttpApiException ex) {
            log.debug("查询积分异常",ex);
           
            Logger.getLogger(JBeiApp.class.getName()).log(Level.SEVERE, null, ex);
            
            response.setStatus(PosMcResponseStatus.SERVER_API_ERROR);
            response.setErrmsg("Server NetWork Error!");
        }
        
                
        return response;
        
    
    }

    public SubmitResponse Submit(SubmitRequest submit) {
        
       log.debug("收到单个交易请求->卡号:"+submit.getCardNo()+",交易金额:"+submit.getAmount()+",交易时间:"+Tools.long2datestr(submit.getTimestamp()));
        
        SubmitResponse response = new SubmitResponse();
        
       try {
           
            JBeiCSSubmitResponse resp = api.CSSubmit(submit.getCardNo(), submit.getMerchantId(),submit.getTerminalId(),
                    submit.getAmount(),submit.getTimestamp());
            
            if (resp.isSuccess()) {
                response.setStatus(PosMcResponseStatus.SUCCESS);
                response.setAvaiablePoints(resp.getValidpoints());
                response.setTotalPoints(resp.getTotalPoints());
                response.setTxnId(resp.getTxnId());
                response.setReBates(resp.getRebates());
                response.setReceivableCardNo("");
                
              
                
                log.debug("卡号:" + submit.getCardNo() + ",总积分:" + response.getTotalPoints() + ",有效积分:" + response.getAvaiablePoints());

            } else {
                log.debug("卡号:" + submit.getCardNo() + ",提交消费失败:" + resp.getErrCode() + ",(" + resp.getErrMsg() + ")");
                response.setStatus(Tools.trErrCode(resp.getErrCode()));
                response.setErrmsg(resp.getErrMsg());
            }

        } catch (HttpApiException ex) {
            log.debug("提交消费记录异常", ex);

            Logger.getLogger(JBeiApp.class.getName()).log(Level.SEVERE, null, ex);

            response.setStatus(PosMcResponseStatus.SERVER_API_ERROR);
            response.setErrmsg("Server NetWork Error!");
        }
       
       return response;
       
    }

    public ReFundResponse Refund(ReFundRequest refund) {
        
        
        log.debug("收到单个退单请求->卡号:" + refund.getCardNo() + ",交易金额:" + refund.getAmount() + ",交易时间:" + Tools.long2datestr(refund.getTimestamp()));

        ReFundResponse response = new ReFundResponse();

        try {
            
            JBeiCSSubmitResponse resp = api.CSSubmit(refund.getCardNo(), refund.getMerchantId(), refund.getTerminalId(),
                    refund.getAmount(), refund.getTimestamp());

            if (resp.isSuccess()) {
                response.setStatus(PosMcResponseStatus.SUCCESS);
                response.setAvaiablePoints(resp.getValidpoints());
                response.setTotalPoints(resp.getTotalPoints());
                response.setTxnId(resp.getTxnId());
                
                log.debug("卡号:" + refund.getCardNo() + ",总积分:" + response.getTotalPoints() + ",有效积分:" + response.getAvaiablePoints());

            } else {
                log.debug("卡号:" + refund.getCardNo() + ",提交退单失败:" + resp.getErrCode() + ",(" + resp.getErrMsg() + ")");
                response.setStatus(Tools.trErrCode(resp.getErrCode()));
                response.setErrmsg(resp.getErrMsg());
            }

        } catch (HttpApiException ex) {
            log.debug("退单异常", ex);

            Logger.getLogger(JBeiApp.class.getName()).log(Level.SEVERE, null, ex);

            response.setStatus(PosMcResponseStatus.SERVER_API_ERROR);
            response.setErrmsg("Server Internal Error!");
        }
        
        return response;
        
    }

    public BatchOpResponse BatchOp(BatchOpRequest batop) {
        
        log.debug("收到批量操作请求->商户ID:" + batop.getMerchantId()
                + ",终端ID:" + batop.getTerminalId()
                + ",批量记录数:" + batop.getTrades().size());

        List<JBeiTradeEntity> jbei_bats = new ArrayList<JBeiTradeEntity>();
        
        for (TradeEntity entity : batop.getTrades()) {

            log.debug("交易类型:" + entity.getTradeType());

            log.debug("交易卡号:" + entity.getCardNo());
            log.debug("交易金额:" + entity.getAmount());
            Date d = new Date();
            d.setTime(entity.getTimestamp());
            log.debug("交易时间:" + d);
            JBeiTradeEntity jb = new JBeiTradeEntity();
            jb.setAmount(entity.getAmount());
            jb.setCardNo(entity.getCardNo());
            
            jb.setRebate(entity.getRebate());
            
            jb.setTimestamp(entity.getTimestamp());
            //jb.setRid(api.genRid(entity.getCardNo(), batop.getMerchantId(), batop.getTerminalId(),entity.getAmount(),entity.getTimestamp()));
            jb.setTradeType(entity.getTradeType());
            
            jbei_bats.add(jb);
            
        }
        
        BatchOpResponse response = new BatchOpResponse();

        try {
            JBeiBatchSubmitResponse resp1 = null;
            
            if(jbei_bats.size()>0){
                 resp1 = api.RBatchSubmit(batop.getMerchantId(), batop.getTerminalId(), jbei_bats);
                 if(resp1!=null && resp1.isSuccess()){
                     log.debug("批量退单:" + resp1.getCount() + ",成功:" + resp1.getRecCount());
                     response.setStatus(PosMcResponseStatus.SUCCESS);
                 }
                 else if(resp1!=null && !resp1.isSuccess()) {
                     response.setStatus(Tools.trErrCode(resp1.getErrCode()));
                     response.setErrmsg(resp1.getErrMsg());
                     
                 }
            }
            if(resp1==null){
                response.setStatus(PosMcResponseStatus.NO_DATA_PROCESSED);
                response.setErrmsg("NO DATA PROCESSED");
            }

        } catch (HttpApiException ex) {

            Logger.getLogger(JBeiApp.class.getName()).log(Level.SEVERE, null, ex);
            log.debug("批量操作异常", ex);

            Logger.getLogger(JBeiApp.class.getName()).log(Level.SEVERE, null, ex);

            response.setStatus(PosMcResponseStatus.SERVER_API_ERROR);
            response.setErrmsg("Server Internal Error!");
        }

        return response;
        
    }
    
    public UpdateDataResponse Update( UpdateDataRequest req) {

        log.debug("收到更新返利操作请求->商户ID:" + req.getMerchantId()
                + ",终端ID:" + req.getTerminalId());

        UpdateDataResponse response = new UpdateDataResponse();
        
       
        try {
            
            JBeiRRQResponse jbresp;
            jbresp = api.RebateRateQuery(req.getMerchantId(), req.getTerminalId());
            
            if (jbresp.isSuccess()){
                response.setStatus(PosMcResponseStatus.SUCCESS);
                response.setRebate_base(0);
                response.setRebate_percent(jbresp.getRate());
                response.setServer(this.getServer());
                response.setPort(this.getPort());
                
            }
            else {
                log.debug("查询返利比例失败:" + jbresp.getErrCode() + ",(" + jbresp.getErrMsg() + ")");
                response.setStatus(Tools.trErrCode(jbresp.getErrCode()));
                response.setErrmsg(jbresp.getErrMsg());
            }
            
        } catch (HttpApiException ex) {
            response.setStatus(PosMcResponseStatus.SERVER_API_ERROR);
            response.setErrmsg("Server Internal Error!");
            Logger.getLogger(JBeiApp.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return response;

    }
    
    
    
    public static void main(String[] argv) {
        
        PosMcAppServer server2 = new PosMcAppServer();
        server2.setApp(new DemoApp());
        Thread t2 = new Thread(server2);
        t2.start();
        
          Map m = new HashMap<String,String>();
          for(String arg: argv){
              if(arg.contains("=")){
                  String[] par = arg.split("=");
                  if (par.length>=2)
                        m.put(par[0], par[1]);
              }
          }
          
        PosMcAppServer server = new PosMcAppServer(9991);
        server.setApp(new JBeiApp(m));
        Thread t = new Thread(server);
        t.start();
        
        
    }

    private void loadConfig(Map config) {
        if (config.containsKey("server")){
            this.setServer((String)config.get("server"));
        }
        if (config.containsKey("port")){
            this.setPort(Integer.parseInt((String)config.get("port")));
        }
        if (config.containsKey("mid")){
            this.setAccess_mid((String)config.get("mid"));
        }
        if(config.containsKey("key")){
            this.setAccess_key((String)config.get("key"));
        }
        
       
        
    }

    /**
     * @return the server
     */
    public String getServer() {
        return server;
    }

    /**
     * @param server the server to set
     */
    public void setServer(String server) {
        this.server = server;
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
     * @return the access_key
     */
    public String getAccess_key() {
        return access_key;
    }

    /**
     * @param access_key the access_key to set
     */
    public void setAccess_key(String access_key) {
        this.access_key = access_key;
    }

    /**
     * @return the access_mid
     */
    public String getAccess_mid() {
        return access_mid;
    }

    /**
     * @param access_mid the access_mid to set
     */
    public void setAccess_mid(String access_mid) {
        this.access_mid = access_mid;
    }

   
    
}
