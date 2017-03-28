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

package com.zhuc.nupay.mcm.app;

import com.zhuc.nupay.mcm.PosMcApp;
import com.zhuc.nupay.mcm.app.nupay.CardDao;
import com.zhuc.nupay.mcm.app.nupay.PosTerminalDao;
import com.zhuc.nupay.mcm.app.nupay.TransactionDao;
import com.zhuc.nupay.mcm.messages.BatchOpRequest;
import com.zhuc.nupay.mcm.messages.BatchOpResponse;
import com.zhuc.nupay.mcm.messages.InquiryRequest;
import com.zhuc.nupay.mcm.messages.InquiryResponse;
import com.zhuc.nupay.mcm.messages.ReFundRequest;
import com.zhuc.nupay.mcm.messages.ReFundResponse;
import com.zhuc.nupay.mcm.messages.SubmitRequest;
import com.zhuc.nupay.mcm.messages.SubmitResponse;
import com.zhuc.nupay.mcm.messages.UpdateDataRequest;
import com.zhuc.nupay.mcm.messages.UpdateDataResponse;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author Aron llc_xyz@qq.com
 */
public class NuaPayApp implements PosMcApp {
    
    private CardDao carddao;
    
    private PosTerminalDao termialDao;
    
    private TransactionDao trDao;
    
    
    public InquiryResponse Inquiry(InquiryRequest inqury) {
        
        InquiryResponse response = new InquiryResponse();
        
        String cardNo = inqury.getCardNo(); //To change body of generated methods, choose Tools | Templates.
        
        return response;
        
        
    }

    public SubmitResponse Submit(SubmitRequest submit) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public ReFundResponse Refund(ReFundRequest refund) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public BatchOpResponse BatchOp(BatchOpRequest batop) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public UpdateDataResponse Update(UpdateDataRequest req) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    
}
