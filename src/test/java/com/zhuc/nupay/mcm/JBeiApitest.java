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

package com.zhuc.nupay.mcm;

import com.zhuc.nupay.mcm.app.jbei.HttpApiException;
import com.zhuc.nupay.mcm.app.jbei.JBeiApi;
import com.zhuc.nupay.mcm.app.jbei.JBeiBatchSubmitResponse;
import com.zhuc.nupay.mcm.app.jbei.JBeiCSSubmitResponse;
import com.zhuc.nupay.mcm.app.jbei.JBeiPointsDetailResponse;
import com.zhuc.nupay.mcm.app.jbei.JBeiRRQResponse;
import com.zhuc.nupay.mcm.app.jbei.JBeiRSSubmitResponse;
import com.zhuc.nupay.mcm.app.jbei.JBeiTradeEntity;
import com.zhuc.nupay.mcm.command.PosMcCommand;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Aron llc_xyz@qq.com
 */
public class JBeiApitest {
    
    
    private static String cardNo = "1234567890123456";
    private static String mid = "1234567890123456";
    private static String tid = "1000100010001000";
    
    public JBeiApitest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    // @Test
    // public void hello() {}
   // @Test
    public void testPQ(){
        try {
            
            System.out.println("================测试查询 开始====================");
            
            String card = "0860200100000107";
            String mid = "100160";
            String tid = "POSTEST01";
            String rid = ""+System.currentTimeMillis();
            
            JBeiApi api = new JBeiApi();
            JBeiPointsDetailResponse res= api.PointsDetailQuery(card, mid);
//            System.out.println("success:->"+res.isSuccess());
//            System.out.println("errmsg:->" + res.getErrMsg());
//            System.out.println("content:->" + res.getContent());
//            for(String key:res.getResult().keySet()){
//                System.out.println("ret ->"+key+"="+res.getResult().get(key));
//            }
            
            System.out.println("返回状态:"+res.isSuccess()+",总积分:"+res.getTotalPoints()+",可用积分:"+res.getValidpoints());
            
            System.out.println("================测试查询 结束====================");
            
        } catch (HttpApiException ex) {
            Logger.getLogger(JBeiApitest.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
       
        
    }
    @Test
    public void TestCSS(){
        try {
            //
           
            String rid = "" + System.currentTimeMillis();
            String card = "0860200100000107";
                    
            JBeiApi api = new JBeiApi();
            
            System.out.println("================测试提交 开始====================");
            int amount = 12000;
            
            JBeiCSSubmitResponse resp = api.CSSubmit(card, mid, "00010013", amount, System.currentTimeMillis());
            if (resp.isSuccess()) {
                System.out.println("提交成功:总积分->" + resp.getTotalPoints() + ",可用积分:" + resp.getValidpoints() + ",自定义消息:" + resp.getInfo());
                
            } else {
                System.out.println("提交失败:" + resp.getErrCode() + "," + resp.getErrMsg());
                
            }
            System.out.println("================测试提交 结束====================");
        } catch (HttpApiException ex) {
            Logger.getLogger(JBeiApitest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
   //@Test
    public void TestRSS() {
        try {
            //
           
            String rid = "" + System.currentTimeMillis();

            JBeiApi api = new JBeiApi();

            System.out.println("================测试退单 开始====================");
            int amount = 12000;
            
            String card = "0860200100001471";
            
            JBeiRSSubmitResponse resp = api.RSSubmit(card, mid, tid, amount, System.currentTimeMillis());
            if (resp.isSuccess()) {
                System.out.println("退单成功:总积分->" + resp.getTotalPoints() + ",可用积分:" + resp.getValidpoints() + ",自定义消息:" + resp.getInfo());

            } else {
                System.out.println("退单失败:" + resp.getErrCode() + "," + resp.getErrMsg());

            }
            System.out.println("================测试退单 结束====================");
            
        } catch (HttpApiException ex) {
            Logger.getLogger(JBeiApitest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
  // @Test
    public void TestRBatch() {
        try {
            //
         
            String rid = "" + System.currentTimeMillis();
            
            JBeiApi api = new JBeiApi();

            System.out.println("================测试批量退单交易 开始====================");
            int amount = 12000;
            
            List<JBeiTradeEntity> trades = new ArrayList<JBeiTradeEntity>();
            
            JBeiTradeEntity trade1 = new JBeiTradeEntity();
            trade1.setAmount(1200);
            trade1.setCardNo(cardNo);
            trade1.setTimestamp(System.currentTimeMillis());
            trades.add(trade1);
            
            
            JBeiBatchSubmitResponse resp = api.RBatchSubmit(mid, tid,trades);
            
            if (resp.isSuccess()) {
                System.out.println("批量退单成功:提交记录数->" + resp.getCount() + ",成功记录数:" + resp.getRecCount() + ",成功记录:" + resp.getRidList().size());

            } else {
                System.out.println("批量退单失败:" + resp.getErrCode() + "," + resp.getErrMsg());

            }
            System.out.println("================测试批量退单交易 结束====================");

        } catch (HttpApiException ex) {
            Logger.getLogger(JBeiApitest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
   //@Test
    public void TestCBatch() {
        try {
            //
          

            JBeiApi api = new JBeiApi();

            System.out.println("================测试批量消费交易 开始====================");
            int amount = 12000;

            List<JBeiTradeEntity> trades = new ArrayList<JBeiTradeEntity>();

            JBeiTradeEntity trade1 = new JBeiTradeEntity();
            trade1.setAmount(1200);
            trade1.setCardNo(cardNo);
            trade1.setTimestamp(System.currentTimeMillis());
            trade1.setTradeType(PosMcCommand.REFUND);
            trades.add(trade1);
            
            JBeiBatchSubmitResponse resp = api.CBatchSubmit(mid, tid, trades);

            if (resp.isSuccess()) {
                System.out.println("批量消费成功:提交记录数->" + resp.getCount() + ",成功记录数:" + resp.getRecCount() + ",成功记录:" + resp.getRidList().size());

            } else {
                System.out.println("批量消费失败:" + resp.getErrCode() + "," + resp.getErrMsg());

            }
            System.out.println("================测试批量消费交易 结束====================");

        } catch (HttpApiException ex) {
            Logger.getLogger(JBeiApitest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
  //@Test
    public void TestRRQ() {
        try {
            //
            String cardNo = "1234567890123456";
            String mid = "1234567890123456";
            String tid = "1000100010001000";
            String rid = "" + System.currentTimeMillis();

            JBeiApi api = new JBeiApi();

            System.out.println("================测试查询返利比例 开始====================");
            int amount = 12000;

         

            JBeiRRQResponse resp = api.RebateRateQuery(mid, tid);
            
            if (resp.isSuccess()) {
                System.out.println("批量消费成功:返利比例->" + resp.getRate());

            } else {
                System.out.println("查询返利比例 失败:" + resp.getErrCode() + "," + resp.getErrMsg());

            }
            System.out.println("================测试查询返利比例 结束====================");

        } catch (HttpApiException ex) {
            Logger.getLogger(JBeiApitest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    
    
    
    
    
}
