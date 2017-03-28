/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.zhuc.nupay.mcm.messages.base;

import com.zhuc.nupay.mcm.exceptions.InvalidMessageException;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.proxy.utils.ByteUtilities;

/**
 *
 * @author Aron
 */
public  abstract class PosMcRequest extends PosMcMessage {

    
     
    
   
    
    
    /**
     * used for client mode.
     * @return byte[]
     * @throws com.zhuc.nupay.mcm.exceptions.InvalidMessageException 
     */
    public byte[] encodeBinary() throws InvalidMessageException{
        this.packData();
        
        if(getData()==null) setData(new byte[0]);
        
        short rlen = (short)(getData().length+3);
        
        this.len = IoBuffer.allocate(2).putShort(rlen).array();
        
        byte[] msg = new byte[rlen+2];     //请求消息.
        
        msg[0] = len[0];
        msg[1] = len[1];
        
        msg[2] = cmd;
        
        System.arraycopy(this.data, 0, msg, 3, this.data.length);
        
        byte[] calc_data = new byte[this.data.length+3];
        
        System.arraycopy(msg,0,calc_data,0,this.data.length + 3);
        
        crc = crc16(calc_data);
        
        msg[msg.length-2] =crc[0] ;
        msg[msg.length-1] = crc[1];

       log.debug("packet["+ByteUtilities.asHex(msg,",")+"]");

        return msg;

    }
    public void decodeBinary(byte[] msg) throws InvalidMessageException {
        
        this.len = new byte[2];
       
        this.len[0]  =msg[0];
        this.len[1]  =msg[1];
        
        this.cmd = msg[2];
        
        int rlen = ByteUtilities.makeIntFromByte2(this.len);
        
        byte[] rcrc = new byte[2];
        
        int datalen = rlen - 3;
        byte[] rdata = new byte[datalen];
        
        System.arraycopy(msg, 3, rdata, 0, datalen);
        
        this.setData(rdata);
        
        rcrc[0] = msg[rlen];
        rcrc[1] = msg[rlen+1];
        
        this.crc = rcrc;
        
        this.parseData();
        

    }
    
   
    
    
}
