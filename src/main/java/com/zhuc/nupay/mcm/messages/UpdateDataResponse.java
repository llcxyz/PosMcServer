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
import com.zhuc.nupay.mcm.messages.base.PosMcResponse;
import com.zhuc.nupay.mcm.misc.Tools;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.proxy.utils.ByteUtilities;

/**
 * data 节结构.
 * |3字节返利基数|1字节返利比例|64字节服务器地址|4字节服务器端口|
 * @author Aron llc_xyz@qq.com
 */
public class UpdateDataResponse extends PosMcResponse{

    /**
     * 返利比例. 百分之. 
     */
    private int rebate_percent;
    
    /**
     * 返利基数.
     */
    private int rebate_base;
    
    private String server;
    
    private int port;
    
    
    public UpdateDataResponse(){
        this.cmd = PosMcCommand.UPDATE;
        this.server= "";
        this.port = 0;
        this.rebate_base = 0;
        
        
    }
      

    
    @Override
    protected void parseData() throws InvalidMessageException {
        
        byte[] base = new byte[3];
        byte [] bates = new byte[1];
        byte [] server = new byte[64];
        byte [] port = new byte[4];
        
        System.arraycopy(this.data,0,base,0,3);
        
        System.arraycopy(this.data, 3, bates, 0, 1);
        
        System.arraycopy(this.data, 4, server, 0, 64);
        
        System.arraycopy(this.data, 68, port, 0,4);
        
        this.setServer(new String(server));
        
        this.setPort(ByteUtilities.makeIntFromByte4(port));
        
        this.setRebate_percent(Tools.bcd2int(bates));
        
        this.setRebate_base(Tools.bcd2int(base));
        
        
    }

    @Override
    protected void packData() throws InvalidMessageException {
        
        this.data =new byte[72];
        
        byte[] rebates_base = Tools.int2bcd(this.getRebate_base(), 3);
        
        byte[] rebates = Tools.int2bcd(this.getRebate_percent(), 1);
        
        System.arraycopy(rebates_base, 0, this.data,0, 3);
        
        this.data[3] = rebates[0];
                
        System.arraycopy(Tools.fixLength(this.getServer().getBytes(),64),0, this.data,4, 64);
        
        System.arraycopy(IoBuffer.allocate(4).putInt(this.getPort()).array(), 0, this.data, 68, 4);
        
        
        
    }



    /**
     * @return the rebate_percent
     */
    public int getRebate_percent() {
        return rebate_percent;
    }

    /**
     * @param rebate_percent the rebate_percent to set
     */
    public void setRebate_percent(int rebate_percent) {
        this.rebate_percent = rebate_percent;
    }

    /**
     * @return the server
     */
    public String getServer() {
        return server;
    }

    /**
     * @param server the server to set
     */
    public void setServer(String server) {
        this.server = server;
    }

    /**
     * @return the port
     */
    public int getPort() {
        return port;
    }

    /**
     * @param port the port to set
     */
    public void setPort(int port) {
        this.port = port;
    }

    /**
     * @return the rebate_base
     */
    public int getRebate_base() {
        return rebate_base;
    }

    /**
     * @param rebate_base the rebate_base to set
     */
    public void setRebate_base(int rebate_base) {
        this.rebate_base = rebate_base;
    }
    
}
