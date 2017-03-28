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
 * data 节封包结构.
 * ..|4字节总积分| 4字节可用积分|4字节自定义消息长度|N字节自定义消息|..
 * @author Aron
 */
public class InquiryResponse extends PosMcResponse{
    
    
    /**
     * 账面积分.
     */
    private int totalPoints;
    
    /**
     * 可用积分
     */
    private int avaiablePoints;
    
    
    /**
     * 自定义消息.
     */
    private String customMsg;
    
    
    public InquiryResponse(){
        this.cmd = PosMcCommand.INQUERY;
        customMsg = "";
    }
    @Override
    protected void parseData() throws InvalidMessageException {
        
        log.debug(" data byte is:"+ByteUtilities.asHex(this.data,","));
        
        //little-endian change to big-endian.
        
        //ByteUtilities.changeWordEndianess(this.data, 0, 4);
        byte[] total = new byte[4];
        byte[] aval = new byte[4];
        byte[] msglen = new byte[4];
        
        System.arraycopy(this.data, 0, total,0, 4);
        
        System.arraycopy(this.data, 4, aval, 0, 4);
        
        System.arraycopy(this.data, 8, msglen, 0, 4);
        
        int mlen = ByteUtilities.makeIntFromByte4(msglen);
        
        try {
            this.setCustomMsg(new String(this.data,12,mlen,"UTF-8"));
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(InquiryResponse.class.getName()).log(Level.SEVERE, null, ex);
            
            throw new InvalidMessageException(ex.getMessage());
            
        }
        
        this.setTotalPoints(Tools.bcd2int(total));//ByteUtilities.makeIntFromByte4(total));
        this.setAvaiablePoints(Tools.bcd2int(aval));//ByteUtilities.makeIntFromByte4(aval));
        
        
    }

    @Override
    protected void packData() throws InvalidMessageException {
        
        
        byte[] total = Tools.int2bcd(totalPoints, 4);//IoBuffer.allocate(4).putInt(this.totalPoints).array();
        
        byte[] aval =  Tools.int2bcd(this.avaiablePoints,4);//IoBuffer.allocate(4).putInt(this.avaiablePoints).array();
        
        this.data =new byte[12+this.getCustomMsg().length()];
        System.arraycopy(total, 0, this.data, 0,4);
        
        System.arraycopy(aval,0 , this.data, 4, 4);
        
        byte []  msgs;
        try {
            
            msgs = this.getCustomMsg().getBytes("UTF-8");
            
            byte[] mlen = IoBuffer.allocate(4).putInt(msgs.length).array();
            
            System.arraycopy(mlen, 0, this.data, 8, mlen.length);
            
            System.arraycopy(msgs, 0, this.data, 12, msgs.length);
            
            
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(InquiryResponse.class.getName()).log(Level.SEVERE, null, ex);
            throw new InvalidMessageException(ex.getMessage());
            
        }
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
     * @return the customerMsg
     */
    public String getCustomMsg() {
        return customMsg;
    }

    /**
     * @param customerMsg the customerMsg to set
     */
    public void setCustomMsg(String customerMsg) {
        this.customMsg = customerMsg;
    }

    
    
}
