/*
 * Copyright (C) 2013 Aron/llc_yz@qq.com
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
package com.zhuc.nupay.mcm.codec;

import com.zhuc.nupay.mcm.command.PosMcCommand;
import com.zhuc.nupay.mcm.exceptions.InvalidMessageException;
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
import com.zhuc.nupay.mcm.messages.base.Message;
import com.zhuc.nupay.mcm.messages.base.PosMcMessage;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.mina.proxy.utils.ByteUtilities;

/**
 *
 * @author Aron/llc_yz@qq.com
 */
public class PosMcMessageFactory {

    private final static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(PosMcMessageFactory.class.getName());
    private static final Map<Integer, Class> reqCmdMap = new HashMap<Integer, Class>();
    private static final Map<Integer, Class> respCmdMap = new HashMap<Integer, Class>();

    static {

        registerCmdMsgMap(PosMcCommand.INQUERY & 0xff, InquiryRequest.class, InquiryResponse.class);
        registerCmdMsgMap(PosMcCommand.SUBMIT & 0xff, SubmitRequest.class, SubmitResponse.class);
        registerCmdMsgMap(PosMcCommand.REFUND & 0xff, ReFundRequest.class, ReFundResponse.class);
        registerCmdMsgMap(PosMcCommand.BATCH_OP & 0xff, BatchOpRequest.class, BatchOpResponse.class);
        registerCmdMsgMap(PosMcCommand.UPDATE & 0xff,UpdateDataRequest.class, UpdateDataResponse.class);
    }

    public static void registerCmdMsgMap(int cmd, Class reqCls, Class respCls) {
        reqCmdMap.put(cmd, reqCls);
        respCmdMap.put(cmd, respCls);

    }

    /**
     * 
     * @param msg
     * @param request  是否是请求消息.
     * @return Message
     */
    public static Message createPosMcMessage(byte[] msg, boolean request) {

        try {
            int cmd = msg[2]&0xff;
            Message newInstance = null;
            
            if (request) {
                
                if (reqCmdMap.containsKey(cmd)) {
                    newInstance = (Message) (reqCmdMap.get(cmd).newInstance());
                }
            }
            else{
                
                if (respCmdMap.containsKey(cmd)) {
                    newInstance = (Message) (respCmdMap.get(cmd).newInstance());
                }
            }
            
            if(newInstance!=null)
                newInstance.decodeBinary(msg);
            
            return newInstance;
            
            
        } catch (InvalidMessageException ex) {
            Logger.getLogger(PosMcMessageFactory.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            Logger.getLogger(PosMcMessageFactory.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(PosMcMessageFactory.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;

    }
    
    public static  boolean checkCrc(byte[] msg) {
        byte[] crc = new byte[2];
        crc[0] = msg[msg.length - 2];
        crc[1] = msg[msg.length - 1];
        byte[] buffer = new byte[msg.length - 2];

        System.arraycopy(msg, 0, buffer, 0, buffer.length);

        byte[] calc_crc = PosMcMessage.crc16(buffer);

        log.debug("CRC 校验: [" + ByteUtilities.asHex(crc, ",") + "]<<->>[" + ByteUtilities.asHex(calc_crc, ",") + "]");

        return crc[0] == calc_crc[0] && crc[1] == calc_crc[1];

    }
}
