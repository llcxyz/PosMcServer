/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.zhuc.nupay.mcm.messages;

import com.zhuc.nupay.mcm.command.PosMcCommand;
import com.zhuc.nupay.mcm.exceptions.InvalidMessageException;
import com.zhuc.nupay.mcm.messages.base.PosMcResponse;

/**
 *
 * @author Aron
 */
public class BatchOpResponse extends PosMcResponse{

    public BatchOpResponse() {
        this.cmd = PosMcCommand.BATCH_OP;
    }
    
    @Override
    protected void parseData() throws InvalidMessageException {
        
    }

    @Override
    protected void packData() throws InvalidMessageException {
       this.data = new byte[1];
    }
    
}
