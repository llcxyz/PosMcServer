/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.zhuc.nupay.mcm.codec;

import com.zhuc.nupay.mcm.messages.base.Message;
import com.zhuc.nupay.mcm.messages.base.PosMcMessage;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CoderResult;
import org.apache.log4j.Logger;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.apache.mina.proxy.utils.ByteUtilities;

/**
 *
 * @author Aron
 */
public class PosMcAppRequestDecoder extends CumulativeProtocolDecoder {

    private final Logger log = Logger.getLogger(PosMcAppRequestDecoder.class.getName());
    
    private static final String MESSAGE_VERSION_KEY = "MessageVersion";
    private static final String MESSAGE_LENGTH_ARRAY = "LengthArray";
    private static final String MESSAGE_LENGTH_KEY = "MessageLength";
    
    
    @Override
    protected boolean doDecode(IoSession is, IoBuffer ib, ProtocolDecoderOutput pdo) throws Exception {
        
        int length = -1;
        byte cmd;
        byte[] data = null;
        if (ib.remaining() >= 4 //检查长度
                && is.getAttribute(MESSAGE_LENGTH_KEY) == null) {
            // enough bytes to decode length
            log.debug("determine length of message");
            byte [] len =new byte[2];
            len[0] = ib.get();
            len[1] = ib.get();
            length = ByteUtilities.makeIntFromByte2(len);
            
            is.setAttribute(MESSAGE_LENGTH_KEY, new Integer(length));
            log.debug("message length:" + length);

			//session.setAttribute(MESSAGE_VERSION_KEY, version);
            // if the entire message is already available, call doDecode again.
            //如果剩下的数据已经达到长度，则继续解码.
            return (ib.remaining() >= length);
           
        } else if (is.getAttribute(MESSAGE_LENGTH_KEY) != null) {
            log.debug("length already determined, see if enough bytes are available");
            length = ((Integer) is.getAttribute(MESSAGE_LENGTH_KEY)).intValue();
            log.debug("包长=>" + length);
            if (ib.remaining() >= length) {              //数据已经齐了.解码.

                byte[] msg = new byte[length + 2];
                byte[] len = IoBuffer.allocate(2).putShort((short)length).array();
                msg[0] = len[0];
                msg[1] = len[1];

                for (int i = 2; i < length + 2; i++) {
                    msg[i] = (byte) ib.get();
                }

               log.debug("收到=>" + ByteUtilities.asHex(msg, ","));

               Message message = PosMcMessageFactory.createPosMcMessage(msg, true);
                if (message != null) {
                    log.debug("message decoded: " + message.getClass());
                    //check CRC
                    log.debug("解码一个数据包,长度:" + length);
                    if(PosMcMessageFactory.checkCrc(msg)){
                        pdo.write(message);
                    }
                    else{
                        log.debug("CRC 校验错误,忽略此包!");
                    }

                    
                } else {
                     log.error("未知的数据包");
                }
                is.removeAttribute(MESSAGE_LENGTH_KEY);
                
                try {
                    if (ib.remaining() >= 2) {
                        byte[] llen = new byte[2];
                        llen[0] = ib.get();
                        llen[1] = ib.get();
                        length = ByteUtilities.makeIntFromByte2(llen);
                        log.debug("还有数据包未解析..继续");

                        is.setAttribute(MESSAGE_LENGTH_KEY, new Integer(length));
                        if (ib.remaining() - ib.markValue() >= length - 1) {
                            log.debug("another message already in the buffer");
                            return true;
                        } else {
                            log.debug("message not yet completly delivered");
                            return false;
                        }
                    }
                } catch (Exception e) {
                    // not enough bytes to determine length
                    log.debug("not enough bytes to determine message length");
                    return false;
                }

            } else {
                log.debug("not enough bytes to determine message length");
                return false;

            }

        } else {
            log.debug("not enough bytes to determine message length");
            return false;
        }

        return false;
        
        
    }
    
   
    
    
    
}
