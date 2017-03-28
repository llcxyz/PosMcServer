package com.zhuc.nupay.mcm.entity;

import com.zhuc.nupay.mcm.command.PosMcCommand;

/*
 * Copyright (C) 2013 Aron/llc_xyz@qq.com
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



/**
 *
 * @author Aron/llc_xyz@qq.com
 */
public  class TradeEntity {
    private String cardNo;
    
    private int amount;
    
    private Long timestamp;
    
    
   /**
    * 参见 {@link com.zhuc.nupay.mcm.command.PosMcCommand}
    * 支持提交与退款/撤销
    */
    private int tradeType;
    
    private int rebate;
    
    
    public TradeEntity(){
        this.tradeType  = PosMcCommand.SUBMIT&0xff;
        this.timestamp = System.currentTimeMillis();
        this.rebate = 0;
        
    }
    public TradeEntity(String cardNo,int amount,Long timestamp,int type){
        this.cardNo = cardNo;
        this.amount = amount;
        this.timestamp = timestamp;
        this.setTradeType(type);
        this.setRebate(0);
        
        
    }

    /**
     * @return the cardNo
     */
    public String getCardNo() {
        return cardNo;
    }

    /**
     * @param cardNo the cardNo to set
     */
    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }

    /**
     * @return the amount
     */
    public int getAmount() {
        return amount;
    }

    /**
     * @param amount the amount to set
     */
    public void setAmount(int amount) {
        this.amount = amount;
    }

    /**
     * @return the tradeType
     */
    public int getTradeType() {
        return tradeType;
    }

    /**
     * @param tradeType the tradeType to set
     */
    public void setTradeType(int tradeType) {
        this.tradeType = tradeType;
    }

    public void setIsSubmitTrade(){
        this.tradeType =PosMcCommand.SUBMIT & 0xff;
    }
    public void setIsRefundTrade() {
        this.tradeType =PosMcCommand.REFUND & 0xff;
    }
    /**
     * @return the timestamp
     */
    public Long getTimestamp() {
        return timestamp;
    }

    /**
     * @param timestamp the timestamp to set
     */
    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * @return the rebate
     */
    public int getRebate() {
        return rebate;
    }

    /**
     * @param rebate the rebate to set
     */
    public void setRebate(int rebate) {
        this.rebate = rebate;
    }
}
