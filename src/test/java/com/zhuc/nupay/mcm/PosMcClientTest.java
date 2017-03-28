/*
 * Copyright (C) 2013 Aron/llc_xyz@qq.com
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

import com.zhuc.nupay.mcm.command.PosMcCommand;
import com.zhuc.nupay.mcm.entity.TradeEntity;
import com.zhuc.nupay.mcm.messages.BatchOpRequest;
import com.zhuc.nupay.mcm.messages.InquiryRequest;
import com.zhuc.nupay.mcm.messages.ReFundRequest;
import com.zhuc.nupay.mcm.messages.SubmitRequest;
import com.zhuc.nupay.mcm.messages.UpdateDataRequest;
import com.zhuc.nupay.mcm.misc.Tools;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.mina.proxy.utils.ByteUtilities;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Aron/llc_xyz@qq.com
 */
public class PosMcClientTest {
    
    private  static PosMcAppClient client;
    
    private static String CARD_NO = "0860200100000107";
    private static String MID = "100160";
    private static String TID = "00010011";
            
    public PosMcClientTest() {
       
    }
    
    @BeforeClass
    public static void setUpClass() {
        
        client = new PosMcAppClient("121.199.37.185", 9991);
        Thread thread = new Thread(client);
        thread.start();
        try {
            Thread.sleep(2000);
        } catch (InterruptedException ex) {
            Logger.getLogger(PosMcClientTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    @AfterClass
    public static void tearDownClass() {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException ex) {
            Logger.getLogger(PosMcClientTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        
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
   //@Test
    public void testInquriry() {

       System.out.println(" testInquiry");
        InquiryRequest req = new InquiryRequest();
        req.setCardNo(CARD_NO);
        req.setMerchantId(MID);
        req.setTerminalId(TID);
        
        client.getSession().write(req);
        

    }
    
    @Test
    public void testSubmit() {
        System.out.println(" testSubmit");
        SubmitRequest sbreq = new SubmitRequest();
        sbreq.setMerchantId(MID);
        sbreq.setTerminalId(TID);
        sbreq.setCardNo(CARD_NO);
        sbreq.setAmount(1000);
        System.out.println(" timestamp = "+sbreq.getTimestamp());
        client.getSession().write(sbreq);

    }
 @Test
   public void testRefund(){
       ReFundRequest req = new ReFundRequest();
       req.setMerchantId(MID);
       req.setTerminalId(TID);
       req.setAmount(850);
       req.setCardNo(CARD_NO);
       
       client.getSession().write(req);
       
   }
   protected String genCard(int i){
       return "88888888888888888888888"+i;
   }
   @Test
   public void testBatchOpRequest(){
       BatchOpRequest req = new BatchOpRequest();
       req.setMerchantId(MID);
       req.setTerminalId(TID);
       List<TradeEntity> trades = new ArrayList<TradeEntity>();
       TradeEntity tr1 = new TradeEntity();
       tr1.setAmount(110);
       tr1.setCardNo(genCard(1));
       tr1.setRebate(10);
       TradeEntity tr2 = new TradeEntity();
       tr2.setAmount(120);
       tr2.setRebate(20);
       tr2.setCardNo(genCard(2));
       TradeEntity tr3 = new TradeEntity();
       tr3.setAmount(130);
       tr3.setRebate(30);
       tr3.setTradeType(PosMcCommand.REFUND);
       
       tr3.setCardNo(genCard(3));
       TradeEntity tr4 = new TradeEntity();
       tr4.setAmount(140);
       tr4.setCardNo(genCard(4));
       tr4.setRebate(40);
       tr4.setTradeType(PosMcCommand.REFUND);
       trades.add(tr1);
       trades.add(tr2);
       trades.add(tr3);
       trades.add(tr4);
       
       
       req.setTrades(trades);
       
       client.getSession().write(req);
       
       
       
   }
    //@Test
    public void testBatchOpRequest2() {
        BatchOpRequest req = new BatchOpRequest();
        req.setMerchantId("200000000005");
        req.setTerminalId("30009");
        List<TradeEntity> trades = new ArrayList<TradeEntity>();
        TradeEntity tr1 = new TradeEntity();
        tr1.setAmount(110);
        tr1.setCardNo("1001");
        TradeEntity tr2 = new TradeEntity();
        tr2.setAmount(120);
        tr2.setCardNo("1002");
        TradeEntity tr3 = new TradeEntity();
        tr3.setAmount(130);
        tr3.setCardNo("1003");
        TradeEntity tr4 = new TradeEntity();
        tr4.setAmount(140);
        tr4.setCardNo("1004");
        trades.add(tr1);
        trades.add(tr2);
       // trades.add(tr3);
       // trades.add(tr4);

        req.setTrades(trades);

        client.getSession().write(req);

    }
    
  @Test
    public void testUpdateBates(){
        UpdateDataRequest req = new UpdateDataRequest();
        req.setMerchantId(MID);
        req.setTerminalId(TID);
        client.getSession().write(req);
        
    }
   // @Test
    
    public void test(){
        System.out.println("100 base:"+ByteUtilities.asHex(Tools.int2bcd(100, 3),","));
        System.out.println(" bcd on bit:"+ByteUtilities.asHex(Tools.int2bcd(99, 1),","));
        
    }
    
}
