/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.zhuc.nupay.mcm.codec;

import com.zhuc.nupay.mcm.messages.base.Message;
import com.zhuc.nupay.mcm.messages.base.PosMcResponse;
import org.apache.log4j.Logger;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.mina.filter.codec.ProtocolEncoderAdapter;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;
import org.apache.mina.proxy.utils.ByteUtilities;

/**
 *
 * @author Aron
 */
public class PosMcAppResponseEncoder extends ProtocolEncoderAdapter implements ProtocolEncoder {

    private final Logger log = Logger.getLogger(PosMcAppResponseEncoder.class.getName());


    public void encode(IoSession is, Object message, ProtocolEncoderOutput peo) throws Exception {
       
       byte[] msgs = ((Message)message).encodeBinary();
       
       IoBuffer buffer = IoBuffer.allocate(msgs.length, true);
       buffer.put(msgs);
       buffer.flip();
       peo.write(buffer);
       peo.flush();
       log.debug("encode ..."+ByteUtilities.asHex(msgs,","));
       
       
    }

  
    
}
