/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.zhuc.nupay.mcm.messages;

import com.zhuc.nupay.mcm.command.PosMcCommand;
import com.zhuc.nupay.mcm.entity.TradeEntity;
import com.zhuc.nupay.mcm.exceptions.InvalidMessageException;
import com.zhuc.nupay.mcm.messages.base.PosMcRequest;
import com.zhuc.nupay.mcm.misc.Tools;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.proxy.utils.ByteUtilities;

/**
 * data节封包结构.
 * ..|15字节商户ID | 8字节终端ID |4字节交易记录条数| 1字节交易类型| 24字节卡号|4字节交易金额|4字节返利金额|8字节时间戳|....依次类推.
 *   如果是退单,返利金额始终为0.
 * 
 * 
 * 
 * @author Aron
 */
public class BatchOpRequest extends PosMcRequest {
    
    private String merchantId;
    
    private String terminalId;
    
    /**
     * 交易记录.
     */
    private List<TradeEntity> trades;
    
    
    private static int SINGLE_TRANS_LEN = 41;
    
    public BatchOpRequest(){
        this.cmd = PosMcCommand.BATCH_OP;
        trades = new ArrayList<TradeEntity>();
        
    }
    
    @Override
    protected void parseData() throws InvalidMessageException {
        try {
            
            this.setMerchantId(new String(this.data, 0, 15, "UTF-8"));
            this.setTerminalId(new String(this.data, 15, 8, "UTF-8"));
            //TODO
            byte[] txcnt = new byte[4];
            
            System.arraycopy(this.data, 23, txcnt, 0, 4);
            
            //交易记录条数.
            int cnt = ByteUtilities.makeIntFromByte4(txcnt);
            //每条交易33个字节.
            byte[] txbytes = new byte[cnt*SINGLE_TRANS_LEN];
            System.arraycopy(this.data, 27, txbytes, 0, cnt*SINGLE_TRANS_LEN);
            
            this.parseTrades(txbytes);
            
            
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(SubmitRequest.class.getName()).log(Level.SEVERE, null, ex);
            throw new InvalidMessageException(ex.getMessage());
        }

    }

    @Override
    protected void packData() throws InvalidMessageException {
        
        
        this.data = new byte[27+this.getTrades().size()*SINGLE_TRANS_LEN];
        
        //mid,tid 都是数字直接getbytes
        System.arraycopy(Tools.fixLength(this.getMerchantId().getBytes(),15), 0, this.data, 0, 15);
        System.arraycopy(Tools.fixLength(this.getTerminalId().getBytes(),8), 0, this.data, 15, 8);
        
        System.arraycopy(IoBuffer.allocate(4)
                .putInt(this.getTrades().size()).array(), 0, this.data, 23, 4);
        
        
        int offset = 27;
        
        for(int i=0;i<this.getTrades().size();i++){
            byte[] type = new byte[1];
                   type[0] = (byte)this.getTrades().get(i).getTradeType();
            byte[] cardno = Tools.fixLength(this.getTrades()
                    .get(i).getCardNo().getBytes(),24);
            
            byte[] amt = Tools.int2bcd(this.getTrades().get(i).getAmount(), 4);//IoBuffer.allocate(4)
                                        //.putInt(this.getTrades().get(i).getAmount()).array();
            byte[] tsp = Tools.timestamp2bcd(this.getTrades().get(i).getTimestamp(), 8);//IoBuffer.allocate(8)
                    //.putLong(this.getTrades().get(i).getTimestamp()).array();
            
            byte[] rebate =Tools.int2bcd(this.getTrades().get(i).getRebate(),4);
            
            System.arraycopy(type, 0, this.data, offset+i*SINGLE_TRANS_LEN, 1);
            
            System.arraycopy(cardno, 0, this.data, offset + i * SINGLE_TRANS_LEN + 1, 24);
            
            System.arraycopy(amt, 0, this.data, offset + i * SINGLE_TRANS_LEN + 25, 4);
            //update 2014/01/22 调换顺序.
            System.arraycopy(tsp, 0, this.data, offset + i * SINGLE_TRANS_LEN + 29, 8);
            
            System.arraycopy(rebate, 0, this.data, offset + i * SINGLE_TRANS_LEN + 37, 4);
            
            
        }
        
    }

    protected void parseTrades(byte[] bytes){
        
        for(int i=0;i<bytes.length/SINGLE_TRANS_LEN;i++){
            byte[] txtype = new byte[1];
            byte[] cardno = new byte[24];
            byte[] amt = new byte[4];
            byte[] rebate = new byte[4];
            byte[] tsp = new byte[8];
            
            System.arraycopy(bytes, i*SINGLE_TRANS_LEN, txtype, 0, 1);
            System.arraycopy(bytes, i*SINGLE_TRANS_LEN+1, cardno, 0, 24);
            System.arraycopy(bytes, i*SINGLE_TRANS_LEN+25, amt, 0,4 );
            
            
            
            System.arraycopy(bytes, i * SINGLE_TRANS_LEN + 29, tsp, 0, 8);
            
            System.arraycopy(bytes, i * SINGLE_TRANS_LEN + 37, rebate, 0, 4);
            
            TradeEntity entity = new TradeEntity();
            entity.setTradeType(txtype[0]&0xff);
            entity.setAmount(Tools.bcd2int(amt));//ByteUtilities.makeIntFromByte4(amt));
            entity.setTimestamp(Tools.bcd2timestamp(tsp));//Tools.bytes2long(tsp));
            
            entity.setRebate(Tools.bcd2int(rebate));
            
            try {
                entity.setCardNo(new String(cardno,0,24,"UTF-8"));
            } catch (UnsupportedEncodingException ex) {
                Logger.getLogger(BatchOpRequest.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            this.getTrades().add(entity);
            
            
        }
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

    /**
     * @return the trades
     */
    public List<TradeEntity> getTrades() {
        return trades;
    }

    /**
     * @param trades the trades to set
     */
    public void setTrades(List<TradeEntity> trades) {
        this.trades = trades;
    }
}
