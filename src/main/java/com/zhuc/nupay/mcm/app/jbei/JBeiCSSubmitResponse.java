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

package com.zhuc.nupay.mcm.app.jbei;

import java.math.BigDecimal;
import java.text.DecimalFormat;

/**
 * 会员消费单笔提交接口.
 * @author Aron llc_xyz@qq.com
 */
public class JBeiCSSubmitResponse extends JBeiPointsDetailResponse{
    
    private String info;
    
    private Long txnId;
    
    private String rid;
    
    private int rebates;
    
    
    @Override
    public void unpack() {
        super.unpack();
        if (this.getResult().containsKey("info")) {
            this.setInfo(this.getResult().get("info"));
        }
        if (this.getResult().containsKey("rid")) {
            this.setRid(this.getResult().get("rid"));
        }
        if (this.getResult().containsKey("sid")) {
            this.setTxnId(Long.parseLong(this.getResult().get("sid")));
        }
        if(this.getResult().containsKey("rebate")){
            //reabtes为小数.
            float f = Float.parseFloat(this.getResult().get("rebate"));
            BigDecimal b = new BigDecimal(f);
            float rf = b.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
            
            this.setRebates(Integer.parseInt(String.valueOf(rf*100)));
            
        }
        
        
        
    }

    /**
     * @return the info
     */
    public String getInfo() {
        return info;
    }

    /**
     * @param info the info to set
     */
    public void setInfo(String info) {
        this.info = info;
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

    /**
     * @return the rid
     */
    public String getRid() {
        return rid;
    }

    /**
     * @param rid the rid to set
     */
    public void setRid(String rid) {
        this.rid = rid;
    }

    /**
     * @return the rebates
     */
    public int getRebates() {
        return rebates;
    }

    /**
     * @param rebates the rebates to set
     */
    public void setRebates(int rebates) {
        this.rebates = rebates;
    }
}
