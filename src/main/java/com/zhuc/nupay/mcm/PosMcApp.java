/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.zhuc.nupay.mcm;

import com.zhuc.nupay.mcm.messages.BatchOpRequest;
import com.zhuc.nupay.mcm.messages.BatchOpResponse;
import com.zhuc.nupay.mcm.messages.InquiryRequest;
import com.zhuc.nupay.mcm.messages.InquiryResponse;
import com.zhuc.nupay.mcm.messages.ReFundResponse;
import com.zhuc.nupay.mcm.messages.ReFundRequest;
import com.zhuc.nupay.mcm.messages.SubmitRequest;
import com.zhuc.nupay.mcm.messages.SubmitResponse;
import com.zhuc.nupay.mcm.messages.UpdateDataRequest;
import com.zhuc.nupay.mcm.messages.UpdateDataResponse;
import java.util.Map;

/**
 *
 * @author Aron
 */
public interface PosMcApp {
    
    
    
    /**
     * 查询卡积分
     * @param inqury
     * @return InquiryResponse
     */
    public InquiryResponse Inquiry(InquiryRequest inqury);
    
    /**
     * 提交消费记录
     * @param submit
     * @return SubmitResponse
     */
    public SubmitResponse Submit(SubmitRequest submit);
    
    /**
     * 取消该笔记录.
     * @param refund
     * @return ReFundResponse
     */
    public ReFundResponse Refund(ReFundRequest refund);
    
    
    /**
     * 批量提交消费/撤销记录.
     * @param batop
     * @return BatchOpResponse
     */
    public BatchOpResponse BatchOp(BatchOpRequest batop);
    
    
    public UpdateDataResponse Update(UpdateDataRequest req);
            
}
