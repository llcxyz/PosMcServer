/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.zhuc.nupay.mcm.messages;

import com.zhuc.nupay.mcm.command.PosMcCommand;
import com.zhuc.nupay.mcm.exceptions.InvalidMessageException;
import com.zhuc.nupay.mcm.messages.base.PosMcRequest;
import com.zhuc.nupay.mcm.misc.Tools;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.proxy.utils.ByteUtilities;

/** 
 * 
 * data 节 封包结构:
 *  ....| 15字merchantId.| 8字节terminalId | 16字节卡号 | 4字节交易金额 |8字节时间戳... 
 * 
 *
 * @author Aron
 */
public class SubmitRequest extends PosMcRequest{
    
    /**
     * 交易金额.
     */
    private int amount;
    
    private String merchantId;
    
    private String terminalId;
    
    private String cardNo;
    
    /**
     * 时间戳
     */
    private Long timestamp;
    
    
    public SubmitRequest(byte cmd){
        this.cmd = cmd;
        this.timestamp = System.currentTimeMillis();
    }
    public SubmitRequest(){
        this.cmd = PosMcCommand.SUBMIT;
        this.timestamp = System.currentTimeMillis();
    }

    @Override
    protected void parseData() throws InvalidMessageException {
        try {
            
            this.setMerchantId(new String(this.data,0,15,"UTF-8"));
            this.setTerminalId(new String(this.data,15,8,"UTF-8"));
            this.setCardNo(new String(this.data,23,24,"UTF-8"));
            
            byte[] b = new byte[4];
            
            System.arraycopy(this.data,47,b,0,4);
            
            this.setAmount(Tools.bcd2int(b));//ByteUtilities.makeIntFromByte4(b));
            
            byte[] tsp = new byte[8];
            
            System.arraycopy(this.data, 51, tsp, 0, 8);
            
            this.setTimestamp(Tools.bcd2timestamp(tsp));
            
            
            
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(SubmitRequest.class.getName()).log(Level.SEVERE, null, ex);
            throw new InvalidMessageException(ex.getMessage());
        }
        
        
    }

    @Override
    protected void packData() throws InvalidMessageException {
        
        this.data = new byte[59];
        
        System.arraycopy(fixLength(this.getMerchantId().getBytes(),15), 0, this.data,0,15);
        System.arraycopy(fixLength(this.getTerminalId().getBytes(),8), 0, this.data, 15, 8);
        System.arraycopy(fixLength(this.getCardNo().getBytes(),24), 0, this.data, 23, 24);
        
        byte[] amt = Tools.int2bcd(amount, 4);//IoBuffer.allocate(4).putInt(this.getAmount()).array();
        System.arraycopy(amt, 0, this.data, 47, 4);
        
        byte[] tsp = Tools.timestamp2bcd(timestamp, 8);//IoBuffer.allocate(8).putLong(this.getTimestamp()).array();
        System.arraycopy(tsp, 0, this.data, 51, 8);
        
        
    }
    protected byte[] fixLength(byte[] b,int len){
        
        if(b.length<len){
            byte[] b2 = new byte[len];
            System.arraycopy(b, 0, b2, 0, b.length);
            return b2;
        }
        else return b;
        
        
    }

    /**
     * @return the cardNo
     */
    public String getCardNo() {
        return cardNo;
    }

    /**
     * @param cardNo the cardNo to set
     */
    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }

    /**
     * @return the amount
     */
    public int getAmount() {
        return amount;
    }

    /**
     * @param amount the amount to set
     */
    public void setAmount(int amount) {
        this.amount = amount;
    }

    /**
     * @return the merchantId
     */
    public String getMerchantId() {
        return merchantId;
    }

    /**
     * @param merchantId the merchantId to set
     */
    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    /**
     * @return the terminalId
     */
    public String getTerminalId() {
        return terminalId;
    }

    /**
     * @param terminalId the terminalId to set
     */
    public void setTerminalId(String terminalId) {
        this.terminalId = terminalId;
    }

    /**
     * @return the timestamp
     */
    public Long getTimestamp() {
        return timestamp;
    }

    /**
     * @param timestamp the timestamp to set
     */
    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
}
