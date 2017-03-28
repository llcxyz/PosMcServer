/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.zhuc.nupay.mcm.messages.base;

import com.zhuc.nupay.mcm.exceptions.InvalidMessageException;
import org.apache.log4j.Logger;
import org.apache.mina.proxy.utils.ByteUtilities;

/**
 *
 * @author Aron
 */
public abstract  class PosMcMessage implements Message {
    
    protected  final Logger log = Logger.getLogger(this.getClass().getName());
    
    
    /**
     * 包长
     */
    protected byte[] len;
    /**
     * 指令
     */
    protected byte cmd;
    /**
     * 数据
     */
    protected  byte[] data;
    /**
     * CRC
     */
    protected byte[] crc ;
 
    
   
    /**
     * 客户端解析data
     *
     * @throws InvalidMessageException
     */
    protected abstract void parseData() throws InvalidMessageException;

    /**
     * 服务器端封装data
     *
     * @throws InvalidMessageException
     */
    protected abstract void packData() throws InvalidMessageException;
    
    
    public static  byte[] crc16(final byte[] buffer) {

        int i,j;
        int current_crc_value = 0xffff;
        int POLYNOMIAL = 0x8408;       //0x8408 厂家固定的.
        for(i=0;i<buffer.length;i++)
        {
            current_crc_value=current_crc_value^((short)(buffer[i]&0xff));  //注意要把byte转换成unsigned 类型.
            for (j=0; j<8; j++)
            {

                if((current_crc_value&0x01) ==1){
                    current_crc_value=((current_crc_value>>1)^POLYNOMIAL);
                }
                else{
                    current_crc_value=(current_crc_value>>1);
                }
            }
        }

        byte[] rcrc = new byte[2];

        rcrc[0] = (byte)(current_crc_value&0xff);     //最低8位.

        rcrc[1] = (byte)((current_crc_value>>8)&0xff);  //次低8位.

        return rcrc;
      


    }
   
    /**
     * @return the len
     */
    protected byte[] getLen() {
        return len;
    }

    /**
     * @param len the len to set
     */
    protected void setLen(byte[] len) {
        this.len = len;
    }

    /**
     * @return the cmd
     */
    protected byte getCmd() {
        return cmd;
    }

    /**
     * @param cmd the cmd to set
     */
    protected void setCmd(byte cmd) {
        this.cmd = cmd;
    }

    /**
     * @return the crc
     */
    protected byte[] getCrc() {
        return crc;
    }

    /**
     * @param crc the crc to set
     */
    protected void setCrc(byte[] crc) {
        this.crc = crc;
    }

    /**
     * @return the data
     */
    protected byte[] getData() {
        return data;
    }

    /**
     * @param data the data to set
     */
    protected void setData(byte[] data) {
        this.data = data;
    }
    
}

