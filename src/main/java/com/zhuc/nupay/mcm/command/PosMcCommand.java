/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.zhuc.nupay.mcm.command;

/**
 *
 * @author Aron
 */
public class PosMcCommand {
    
    /**
     * 查询积分指令
     */
    public static final  byte INQUERY = 0x01;
    /**
     * 提交消费记录
     */
    public static final byte SUBMIT  = 0x02;
    /**
     * 退款/撤销消费
     */
    public static final byte REFUND = 0x03;
    
    /**
     * 批量操作消费和退款/撤销.
     */
    public static final byte BATCH_OP = 0x04;
    
    /**
     * 更新服务端数据.
     */
    public static final byte UPDATE = 0x05;
    
    
    
    
    
}
