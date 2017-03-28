/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.zhuc.nupay.mcm.messages.base;

import com.zhuc.nupay.mcm.exceptions.InvalidMessageException;

/**
 *
 * @author Aron/llc_yz@qq.com
 */
public interface Message {
     /**
     * 二进制编码.
     * @return byte[]
     * @throws com.zhuc.nupay.mcm.exceptions.InvalidMessageException 
     */
    public abstract byte[] encodeBinary() throws InvalidMessageException;
    
    public abstract void  decodeBinary(byte[] msg) throws InvalidMessageException;
    
    
}
