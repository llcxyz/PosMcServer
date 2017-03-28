/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.zhuc.nupay.mcm.messages.base;

import com.zhuc.nupay.mcm.exceptions.InvalidMessageException;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.proxy.utils.ByteUtilities;

/**
 *
 * @author Aron
 */
public abstract class PosMcResponse extends PosMcMessage{
    
    private byte status;
    
    /**
     * 当status非0时. data节全部使用errmsg覆盖.
     * pos机app将显示status与errmsg.
     * 
     */
    private byte[] errmsg=new byte[0];
    
   
    
    public byte[] encodeBinary() throws InvalidMessageException{
        
       

        if (getData() == null) {
            setData(new byte[0]);
        }
        
        if (this.status != 0) {
            this.data = this.errmsg;
        }
        else{
            this.packData();
        }
        
        
        
        short rlen = (short) (getData().length + 4);

        this.len = IoBuffer.allocate(2).putShort(rlen).array();

        byte[] msg = new byte[rlen + 2];     //请求消息.

        msg[0] = len[0];
        msg[1] = len[1];
        msg[2] = this.cmd;
        msg[3] = this.status;
        
        
       

        System.arraycopy(this.data, 0, msg, 4, this.data.length);

        byte[] calc_data = new byte[this.data.length + 4];

        System.arraycopy(msg, 0, calc_data, 0, this.data.length + 4);

        crc = crc16(calc_data);

        msg[msg.length - 2] = crc[0];
        msg[msg.length - 1] = crc[1];

        log.debug("packet[" + ByteUtilities.asHex(msg, ",") + "]");
        return msg;

    }
    public void decodeBinary(byte[] msg) throws InvalidMessageException {

        this.len = new byte[2];

        this.len[0] = msg[0];
        this.len[1] = msg[1];
        this.cmd = msg[2];
        this.status = msg[3];

        int rlen = ByteUtilities.makeIntFromByte2(this.len);

        byte[] rcrc = new byte[2];

        int datalen = rlen - 4;
        byte[] rdata = new byte[datalen];

        System.arraycopy(msg, 4, rdata, 0, datalen);

        this.setData(rdata);

        rcrc[0] = msg[rlen];
        rcrc[1] = msg[rlen + 1];

        this.crc = rcrc;
        
        if(status==0){ //成功消息才解开data/
            this.parseData();
        }else{
            this.setErrmsg(this.data);
        }

    }
    
    

    /**
     * @return the status
     */
    public byte getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(byte status) {
        this.status = status;
    }

    /**
     * @return the errmsg
     */
    public String getErrmsg() {
        try {
            return new String(this.errmsg,"UTF-8");
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(PosMcResponse.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * @param errmsg the errmsg to set
     */
    public void setErrmsg(byte[] errmsg) {
        this.errmsg = errmsg;
    }
    
    public void setErrmsg(String msg){
        try {
            this.errmsg = msg.getBytes("UTF-8");
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(PosMcResponse.class.getName()).log(Level.SEVERE, null, ex);
            
        }
    }
}
