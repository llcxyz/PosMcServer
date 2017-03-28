/*
 * Copyright (C) 2014 Aron llc_xyz@qq.com
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

package com.zhuc.nupay.mcm.messages;

import com.zhuc.nupay.mcm.command.PosMcCommand;
import com.zhuc.nupay.mcm.exceptions.InvalidMessageException;
import com.zhuc.nupay.mcm.messages.base.PosMcRequest;
import com.zhuc.nupay.mcm.misc.Tools;

/**
 * data 节 封包结构: ....
 * | 15字节商户ID| 8字节终端ID 
 * @author Aron llc_xyz@qq.com
 */
public class UpdateDataRequest extends PosMcRequest {
    
    private String merchantId;

    private String terminalId;
    
    
    
    public UpdateDataRequest(){
        this.cmd = PosMcCommand.UPDATE;
    }
    /**
     * @return the merchantId
     */
    public String getMerchantId() {
        return merchantId;
    }

    /**
     * @param merchantId the merchantId to set
     */
    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    /**
     * @return the terminalId
     */
    public String getTerminalId() {
        return terminalId;
    }

    /**
     * @param terminalId the terminalId to set
     */
    public void setTerminalId(String terminalId) {
        this.terminalId = terminalId;
    }

    @Override
    protected void parseData() throws InvalidMessageException {
       
        this.setMerchantId(new String(this.data, 0, 15));
        this.setTerminalId(new String(this.data, 15, 8));
        
    }

    @Override
    protected void packData() throws InvalidMessageException {
        
        this.data = new byte[23];

        System.arraycopy(Tools.fixLength(this.getMerchantId().getBytes(), 15), 0,
                this.data, 0, 15);

        System.arraycopy(Tools.fixLength(this.getTerminalId().getBytes(), 8), 0,
                this.data, 15, 8);
        
    }
    
}
