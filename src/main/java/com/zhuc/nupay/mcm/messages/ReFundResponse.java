/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.zhuc.nupay.mcm.messages;

import com.zhuc.nupay.mcm.command.PosMcCommand;
import com.zhuc.nupay.mcm.exceptions.InvalidMessageException;
import com.zhuc.nupay.mcm.messages.base.PosMcResponse;
import com.zhuc.nupay.mcm.misc.Tools;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.proxy.utils.ByteUtilities;

/**
 * data 节封包结构:
 *  |8字节交易号| 4字节账面积分| 4字节可用积分|4字节自定义消息长度| N字节自定义消息| ...
 * 
 * @author Aron
 */
public class ReFundResponse extends PosMcResponse{
    
    private Long txnId;

    private int totalPoints;

    /**
     * 可用积分
     */
    private int avaiablePoints;

    /**
     * 自定义消息.
     */
    private String customerMsg;

    public ReFundResponse(byte cmd) {
        this.cmd = cmd;
        this.customerMsg = "";

    }

    public ReFundResponse() {
        this.cmd = PosMcCommand.REFUND;
        this.customerMsg = "";

    }

    @Override
    protected void parseData() throws InvalidMessageException {

        byte[] txn = new byte[8];
        byte[] total = new byte[4];
        byte[] aval = new byte[4];
        byte[] msglen = new byte[4];

        System.arraycopy(this.data, 0, txn, 0, 8);

        System.arraycopy(this.data, 8, total, 0, 4);

        System.arraycopy(this.data, 12, aval, 0, 4);

        System.arraycopy(this.data, 16, msglen, 0, 4);

        this.setTxnId(Tools.bcd2long(txn));

        this.setTotalPoints(Tools.bcd2int(total));//ByteUtilities.makeIntFromByte4(total));
        this.setAvaiablePoints(Tools.bcd2int(aval));//ByteUtilities.makeIntFromByte4(aval));
        int slen = ByteUtilities.makeIntFromByte4(msglen);

        try {

            this.setCustomerMsg(new String(this.data, 20, slen, "UTF-8"));

        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(SubmitResponse.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @Override
    protected void packData() throws InvalidMessageException {

        this.data = new byte[20 + this.getCustomerMsg().length()];
        byte[] total = Tools.int2bcd(totalPoints, 4);//IoBuffer.allocate(4).putInt(this.totalPoints).array();
        byte[] aval = Tools.int2bcd(this.avaiablePoints, 4); // totalPoints)IoBuffer.allocate(4).putInt(this.avaiablePoints).array();
        byte[] msglen = IoBuffer.allocate(4).putInt(this.getCustomerMsg().length()).array();

        byte[] txn = Tools.long2bcd(this.getTxnId(), 8);

        System.arraycopy(txn, 0, this.data, 0, 8);

        System.arraycopy(total, 0, this.data, 8, 4);
        System.arraycopy(aval, 0, this.data, 12, 4);
        System.arraycopy(msglen, 0, this.data, 16, 4);
        try {
            System.arraycopy(this.getCustomerMsg().getBytes("UTF-8"), 0, this.data, 20, this.getCustomerMsg().length());
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(SubmitResponse.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     * @return the customerMsg
     */
    public String getCustomerMsg() {
        return customerMsg;
    }

    /**
     * @param customerMsg the customerMsg to set
     */
    public void setCustomerMsg(String customerMsg) {
        this.customerMsg = customerMsg;
    }

    /**
     * @return the avaiablePoints
     */
    public int getAvaiablePoints() {
        return avaiablePoints;
    }

    /**
     * @param avaiablePoints the avaiablePoints to set
     */
    public void setAvaiablePoints(int avaiablePoints) {
        this.avaiablePoints = avaiablePoints;
    }

    /**
     * @return the totalPoints
     */
    public int getTotalPoints() {
        return totalPoints;
    }

    /**
     * @param totalPoints the totalPoints to set
     */
    public void setTotalPoints(int totalPoints) {
        this.totalPoints = totalPoints;
    }

    /**
     * @return the txnId
     */
    public Long getTxnId() {
        return txnId;
    }

    /**
     * @param txnId the txnId to set
     */
    public void setTxnId(Long txnId) {
        this.txnId = txnId;
    }
}
