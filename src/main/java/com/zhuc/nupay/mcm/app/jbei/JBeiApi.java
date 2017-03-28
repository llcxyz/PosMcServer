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

package com.zhuc.nupay.mcm.app.jbei;

import com.zhuc.nupay.mcm.misc.Tools;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.log4j.Logger;

/**
 *
 * @author Aron llc_xyz@qq.com
 */
public class JBeiApi extends HttpApi {
    
    private static final String Pos_Service_Url = "http://www.jbei.net/POSService";
    
    private   String Access_Key = "63827095";
    
    private  String ACCESS_MID = "100160"  ;
            
            
    private static final Logger log = Logger.getLogger(JBeiApi.class.getName());
    
    
    public JBeiApi(String access_mid,String access_key){
        if(access_mid!=null)
            this.ACCESS_MID = access_mid;
        
        if(access_key!=null)
            this.Access_Key = access_key;
        
        log.debug("Access_Key = " + this.getAccess_Key()+ ",Access_MID=" + this.getACCESS_MID());
        
    }
    public JBeiApi(){
        this.Access_Key = "63827095";
        this.ACCESS_MID = "100160";
        
    }
    public JBeiPointsDetailResponse  PointsDetailQuery(String cardNo,String mid) throws HttpApiException{
        
       List<NameValuePair> params = new ArrayList<NameValuePair>();
       
       params.add(new BasicNameValuePair("function", "PQ"));
       params.add(new BasicNameValuePair("cardno",cardNo.trim()));
       params.add(new BasicNameValuePair("mid", getACCESS_MID()));
       
       String signmsg = this.signature(params);
       
       params.add(new BasicNameValuePair("signmsg",signmsg));
       
       JBeiPointsDetailResponse resp = new JBeiPointsDetailResponse();
       
       return (JBeiPointsDetailResponse)this.parseResponse(this.post(Pos_Service_Url, params),resp);
        
       
    }
    /**
     *  单笔消费.
     * @param cardNo 卡号
     * @param mid   商户id.
     * @param tid    终端id
     * @param rid    交易号
     * @param amount   交易金额 单位分. 
     * @param postdt   交易时间.POS机记录的刷卡消费时间，格式为yyyy-MM-dd HH:mm:ss
     * @return 
     */
    public JBeiCSSubmitResponse CSSubmit(String cardNo, String mid,String tid,int amount,Long timestamp) throws HttpApiException {

        List<NameValuePair> params = new ArrayList<NameValuePair>();

        params.add(new BasicNameValuePair("function", "SS"));
        params.add(new BasicNameValuePair("txtype", "0")); //退单
        params.add(new BasicNameValuePair("cardno", cardNo.trim()));
        params.add(new BasicNameValuePair("mid", getACCESS_MID()));
        params.add(new BasicNameValuePair("tid", tid.trim()));
//        params.add(new BasicNameValuePair("rid", this.genRid(cardNo, mid, tid, amount, timestamp)));
        
        log.debug("cent 2 dollor "+amount+"="+this.cents2Dollor(amount));
        
        params.add(new BasicNameValuePair("amount", this.cents2Dollor(amount)));
        
        params.add(new BasicNameValuePair("posdt", Tools.long2datestr(timestamp)));
        
        String signmsg = this.signature(params);

        params.add(new BasicNameValuePair("signmsg", signmsg));
        
        JBeiCSSubmitResponse resp = new JBeiCSSubmitResponse();

        return (JBeiCSSubmitResponse) this.parseResponse(this.post(Pos_Service_Url, params), resp);
        

    }
    /**
     * 退单
     * @param cardNo 卡号
     * @param mid 商户id.
     * @param tid 终端id
     * @param rid 交易号
     * @param amount 交易金额 单位分.
     * @param postdt 交易时间.POS机记录的刷卡消费时间，格式为yyyy-MM-dd HH:mm:ss
     * @return
     */
    
    public JBeiRSSubmitResponse RSSubmit(String cardNo, String mid, String tid, int amount, Long timestamp) throws HttpApiException {

        List<NameValuePair> params = new ArrayList<NameValuePair>();

        params.add(new BasicNameValuePair("function", "SS"));
        params.add(new BasicNameValuePair("txtype","1")); //退单
        params.add(new BasicNameValuePair("cardno", cardNo.trim()));
        params.add(new BasicNameValuePair("mid", getACCESS_MID()));
        params.add(new BasicNameValuePair("tid", tid.trim()));
//        params.add(new BasicNameValuePair("rid", this.genRid(cardNo, mid, tid, amount,timestamp)));

        log.debug("cent 2 dollor " + amount + "=" + this.cents2Dollor(amount));

        params.add(new BasicNameValuePair("amount", this.cents2Dollor(amount)));

        params.add(new BasicNameValuePair("posdt", Tools.long2datestr(timestamp)));

        String signmsg = this.signature(params);

        params.add(new BasicNameValuePair("signmsg", signmsg));

        JBeiRSSubmitResponse resp = new JBeiRSSubmitResponse();

        return (JBeiRSSubmitResponse) this.parseResponse(this.post(Pos_Service_Url, params), resp);
        
    }
    
    /**
     * 批量消费提交.  
     * @param cardNo
     * @param mid
     * @param tid
     * @param trands
     * @return 
     */
    public JBeiBatchSubmitResponse CBatchSubmit( String mid, String tid, List<JBeiTradeEntity> trades) throws HttpApiException {

        List<NameValuePair> params = new ArrayList<NameValuePair>();

        params.add(new BasicNameValuePair("function", "BS"));
        params.add(new BasicNameValuePair("mid", getACCESS_MID()));
        params.add(new BasicNameValuePair("tid", tid.trim()));
        params.add(new BasicNameValuePair("count", String.valueOf(trades.size())));
        
        params.add(new BasicNameValuePair("datainfo",this.Trades2Str(trades)));
        
        params.add(new BasicNameValuePair("signmsg", this.signature(params)));
        
        JBeiBatchSubmitResponse resp = new JBeiBatchSubmitResponse();

        return (JBeiBatchSubmitResponse) this.parseResponse(this.post(Pos_Service_Url, params), resp);
        

    }
    
    /**
     * 批量退单提交.
     * @param mid
     * @param tid
     * @param trades
     * @return
     * @throws HttpApiException 
     * @deprecated 
     */
    public JBeiBatchSubmitResponse RBatchSubmit(String mid, String tid, List<JBeiTradeEntity> trades) throws HttpApiException {

        List<NameValuePair> params = new ArrayList<NameValuePair>();

        params.add(new BasicNameValuePair("function", "BS"));
        params.add(new BasicNameValuePair("mid", getACCESS_MID()));
        params.add(new BasicNameValuePair("tid", tid.trim()));
        //params.add(new BasicNameValuePair("rid", genRid()));
        params.add(new BasicNameValuePair("count", String.valueOf(trades.size())));

        params.add(new BasicNameValuePair("datainfo", this.Trades2Str(trades)));

        params.add(new BasicNameValuePair("signmsg", this.signature(params)));

        JBeiBatchSubmitResponse resp = new JBeiBatchSubmitResponse();
        
        return (JBeiBatchSubmitResponse) this.parseResponse(this.post(Pos_Service_Url, params), resp);
        
    }
    public JBeiRRQResponse RebateRateQuery(String mid,String tid) throws HttpApiException{
        
        List<NameValuePair> params = new ArrayList<NameValuePair>();

        params.add(new BasicNameValuePair("function", "RRQ"));
        params.add(new BasicNameValuePair("mid", getACCESS_MID()));
        params.add(new BasicNameValuePair("tid", tid.trim()));
        params.add(new BasicNameValuePair("signmsg", this.signature(params)));
        JBeiRRQResponse resp = new JBeiRRQResponse();
        
        return (JBeiRRQResponse)this.parseResponse(this.post(Pos_Service_Url, params), resp);
        
    }
    
   
   
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    private String genRid(){
        return ""+System.currentTimeMillis();
        
    }
    public String genRid(String cardNo,String mId,String tId,int amount,Long timestamp){
        
        return DigestUtils.md5Hex(cardNo+","+mId+","+tId+","+amount+","+timestamp);
        
    }
    
    private String genRid(JBeiTradeEntity entity){
        
        return entity.toString();
        
    }
    private String Trades2Str(List<JBeiTradeEntity> trades){
        String str = "";
        for(JBeiTradeEntity entity:trades){
            str += (entity.getTradeType()==2 ?0:1) +","+entity.getCardNo().trim()+","
                    +this.cents2Dollor(entity.getAmount())
                    +","+Tools.long2datestr(entity.getTimestamp())
                    +","+entity.getRebate()+"\n";
        }
        System.out.println(" trade = = ="+str);
                
        return str;
        
        
    }
    /**
     * 将分转换成元 .
     * @param amount
     * @return 
     */
    private String cents2Dollor(int amount){
        
        String dollor = String.valueOf(amount);
        
        return dollor.substring(0, dollor.length()-2) +"."+dollor.substring(dollor.length()-2);
        
    }
    
    protected String signature(List<String> ordervalues,String key){
        
        String str = "";
        for(String value:ordervalues){
            str += value;
        }
        
        return DigestUtils.md5Hex(str+key);
        
    }
    /**
     * 按顺序签名.
     * @param params
     * @return 
     */
    protected String signature(List<NameValuePair> params){
        String str ="";
        for(NameValuePair par:params){
            str +=par.getValue();
            
        }
        return DigestUtils.md5Hex(str + getAccess_Key());
    }
   
    private Map<String,String> parseContent(String content){
        
        Map<String,String> map = new HashMap<String,String>();
        
        String [] result = content.split("\n");
        for(String line:result){
            if(line.indexOf(":")!=-1){
                String[] kv = line.split(":");
                if (kv.length>=2)
                    map.put(kv[0],kv[1]);
            }
            else map.put("signmsg",line);
            
        }
        return map;
        
    }
    /**
     * jbei api 返回头一行是结果状态.
     * 正确: 0 
     *      xxx: 0000
     *      yyy: 0000
     *      1804a4520fdb40c3a37657a0a27659cb
     * 错误: -10101
     *      错误消息
     * 
     * @param <T>
     * @param content
     * @return 
     */
    
   
    
    /**
     * jbei api 返回头一行是结果状态.正确: 0 xxx: 0000 yyy: 0000
     * 1804a4520fdb40c3a37657a0a27659cb 错误: -10101 错误消息
     *
     * @param <T>
     * @param content
     * @param resp
     * @return
     */
    private Response  parseResponse(String content,Response resp){
        
        String result_line = content.split("\n")[0];
        
        if(result_line.indexOf("0")==0){
            resp.setSuccess(true);
            resp.setContent(content.substring(content.indexOf("\n")));
            
            resp.setResult(this.parseContent(resp.getContent()));
            
            //System.out.println("成功返回:");
            resp.unpack();
            
            return resp;
        }
        if (result_line.indexOf("-") == 0) {
            resp.setSuccess(false);
            resp.setContent(content.split("\n")[1]);
            resp.setErrMsg(resp.getContent());
            resp.setErrCode(Integer.parseInt(result_line.replace("\n", "").replace("-", "")));
          //  System.out.println("失败返回:"+resp.getErrMsg());
            return  resp;
            
                    
        }
        return null;
        
            
    }

    /**
     * @return the Access_Key
     */
    public String getAccess_Key() {
        return Access_Key;
    }

    /**
     * @param Access_Key the Access_Key to set
     */
    public void setAccess_Key(String Access_Key) {
        this.Access_Key = Access_Key;
    }

    /**
     * @return the ACCESS_MID
     */
    public String getACCESS_MID() {
        return ACCESS_MID;
    }

    /**
     * @param ACCESS_MID the ACCESS_MID to set
     */
    public void setACCESS_MID(String ACCESS_MID) {
        this.ACCESS_MID = ACCESS_MID;
    }
    
    
    
    
  
      
        
    
    
}
