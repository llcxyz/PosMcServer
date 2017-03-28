/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.zhuc.nupay.mcm.messages;

import com.zhuc.nupay.mcm.command.PosMcCommand;
import com.zhuc.nupay.mcm.exceptions.InvalidMessageException;
import com.zhuc.nupay.mcm.messages.base.PosMcResponse;
import com.zhuc.nupay.mcm.misc.Tools;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.proxy.utils.ByteUtilities;

/**
 * data 节封包结构:
 *  |8字节交易号|4字节返现金额| 4字节账面积分| 4字节可用积分|24字节收款卡号|4字节自定义消息长度| N字节自定义消息| ...
 * @author Aron
 */
public class SubmitResponse extends PosMcResponse{

    
    private Long txnId;
    
    
    
    /**
     * 返利金额
     */
    private int reBates;
    
    
    /**
     * 收款卡号.
     */
    private String receivableCardNo;
    
    private int totalPoints;
    
    /**
     * 可用积分
     */
    private int  avaiablePoints;
    
    /**
     * 自定义消息.
     */
    private String  customerMsg;
    
    public SubmitResponse(byte cmd) {
        this.cmd = cmd;
        this.customerMsg = "";
        
        this.receivableCardNo = "";
        
    }
    
    public SubmitResponse(){
        this.cmd = PosMcCommand.SUBMIT;
        this.customerMsg = "";
        this.receivableCardNo = "";
    }
    
    @Override
    protected void parseData() throws InvalidMessageException {
        
        byte[] txn = new byte[8];
        byte[] rebate = new byte[4];
       
        byte[] total = new byte[4];
        byte[] aval = new byte[4];
        byte[] pcard = new byte[24];
        
        byte[] msglen = new byte[4];
        
        
        System.arraycopy(this.data, 0, txn, 0, 8);
        
        System.arraycopy(this.data, 8,rebate,0,4);
        
        System.arraycopy(this.data, 12, total, 0, 4);
        
        System.arraycopy(this.data, 16, aval, 0, 4);
        
        System.arraycopy(this.data, 20, pcard, 0, 24);
        
        System.arraycopy(this.data, 44, msglen, 0, 4);
        
        
        this.setTxnId(Tools.bcd2long(txn));
        
        this.setReBates(Tools.bcd2int(rebate));
        
        this.setReceivableCardNo(new String(pcard));
        
        this.setTotalPoints(Tools.bcd2int(total));//ByteUtilities.makeIntFromByte4(total));
        this.setAvaiablePoints(Tools.bcd2int(aval));//ByteUtilities.makeIntFromByte4(aval));
        int slen = ByteUtilities.makeIntFromByte4(msglen);
        
        try {
            
            this.setCustomerMsg(new String(this.data,48,slen,"UTF-8"));
            
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(SubmitResponse.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }

    @Override
    protected void packData() throws InvalidMessageException {
       
        this.data = new byte[48+this.getCustomerMsg().length()];
        byte[] total = Tools.int2bcd(totalPoints, 4);//IoBuffer.allocate(4).putInt(this.totalPoints).array();
        byte[] aval = Tools.int2bcd(this.avaiablePoints,4); // totalPoints)IoBuffer.allocate(4).putInt(this.avaiablePoints).array();
        
        byte[] pcard = Tools.fixLength(this.getReceivableCardNo().getBytes(),24);
 
        byte[] rebate = Tools.int2bcd(this.getReBates(),4);
        
        byte[] msglen = IoBuffer.allocate(4).putInt(this.getCustomerMsg().length()).array();
        
        byte[] txn = Tools.long2bcd(this.getTxnId(), 8);
        
        System.arraycopy(txn, 0, this.data, 0, 8);
        System.arraycopy(rebate,0,this.data,8,4);
        System.arraycopy(total,0, this.data,12,4);
        System.arraycopy(aval, 0, this.data, 16, 4);
        System.arraycopy(pcard, 0, this.data, 20, 24);
        
        System.arraycopy(msglen, 0, this.data, 44, 4);
        
        try {
            System.arraycopy(this.getCustomerMsg().getBytes("UTF-8"), 0, this.data,48,this.getCustomerMsg().length());
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(SubmitResponse.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
        
        
       
    }

    /**
     * @return the customerMsg
     */
    public String getCustomerMsg() {
        return customerMsg;
    }

    /**
     * @param customerMsg the customerMsg to set
     */
    public void setCustomerMsg(String customerMsg) {
        this.customerMsg = customerMsg;
    }

 

    /**
     * @return the avaiablePoints
     */
    public int getAvaiablePoints() {
        return avaiablePoints;
    }

    /**
     * @param avaiablePoints the avaiablePoints to set
     */
    public void setAvaiablePoints(int avaiablePoints) {
        this.avaiablePoints = avaiablePoints;
    }

    /**
     * @return the totalPoints
     */
    public int getTotalPoints() {
        return totalPoints;
    }

    /**
     * @param totalPoints the totalPoints to set
     */
    public void setTotalPoints(int totalPoints) {
        this.totalPoints = totalPoints;
    }

    /**
     * @return the txnId
     */
    public Long getTxnId() {
        return txnId;
    }

    /**
     * @param txnId the txnId to set
     */
    public void setTxnId(Long txnId) {
        this.txnId = txnId;
    }

    /**
     * @return the reBates
     */
    public int getReBates() {
        return reBates;
    }

    /**
     * @param reBates the reBates to set
     */
    public void setReBates(int reBates) {
        this.reBates = reBates;
    }

    /**
     * @return the receivableCardNo
     */
    public String getReceivableCardNo() {
        return receivableCardNo;
    }

    /**
     * @param receivableCardNo the receivableCardNo to set
     */
    public void setReceivableCardNo(String receivableCardNo) {
        this.receivableCardNo = receivableCardNo;
    }
    
}
