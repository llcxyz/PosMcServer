/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.zhuc.nupay.mcm.messages;

import com.zhuc.nupay.mcm.command.PosMcCommand;
import com.zhuc.nupay.mcm.messages.base.PosMcRequest;
import com.zhuc.nupay.mcm.exceptions.InvalidMessageException;
import com.zhuc.nupay.mcm.misc.Tools;
/**
 * data 节 封包结构: 
 * ....| 15字节商户ID| 8字节终端ID | 24字卡号|...
 * @author Aron
 */
public class InquiryRequest extends PosMcRequest{
    
    private static final int CARDNO_LENGTH = 16;
    
    /**
     * 卡号 24位.
     */
    private String cardNo;
    
    /**
     * 商户ID 15位
     */
    private String merchantId;
    
    /**
     * 终端ID 8位
     */
    private String terminalId;
    
    public InquiryRequest() {
        this.cmd = PosMcCommand.INQUERY;
    }
    
    @Override
    protected  void parseData() throws InvalidMessageException{
       
        this.setMerchantId(new String(this.data,0,15));
        this.setTerminalId(new String(this.data,15,8));
        this.setCardNo(new String(this.data,23,24));
       
    }
    @Override
    protected void packData() throws InvalidMessageException {

        this.data = new byte[47];
        
        System.arraycopy(Tools.fixLength(this.getMerchantId().getBytes(),15),0,
                this.data, 0,15);
        
        System.arraycopy(Tools.fixLength(this.getTerminalId().getBytes(), 8), 0,
                this.data, 15, 8);
        System.arraycopy(Tools.fixLength(this.getCardNo().getBytes(), 24), 0,
                this.data, 23, 24);
        
       

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

   

    
    
}
