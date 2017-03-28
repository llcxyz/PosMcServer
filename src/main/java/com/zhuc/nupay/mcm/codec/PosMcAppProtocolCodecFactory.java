
package com.zhuc.nupay.mcm.codec;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;

/**
 *
 * @author Aron
 */
public class PosMcAppProtocolCodecFactory implements ProtocolCodecFactory{
    
    
    private final ProtocolEncoder encoder;
    private final ProtocolDecoder decoder;
    
    public PosMcAppProtocolCodecFactory(boolean client){
        
        if(client){
            encoder = new PosMcAppRequestEncoder();
            decoder = new PosMcAppResponseDecoder();
            
        }
        else{
            encoder = new PosMcAppResponseEncoder();
            decoder = new PosMcAppRequestDecoder();
        }
            
    }
    
    public ProtocolEncoder getEncoder(IoSession is) throws Exception {
       return encoder;
       
    }

    public ProtocolDecoder getDecoder(IoSession is) throws Exception {
        
        return decoder;
        
    }
    
}
