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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 批量消费.
 * @author Aron llc_xyz@qq.com
 */
public class JBeiBatchSubmitResponse extends JBeiResponse {
    
    private int count;
    
    private int recCount;
    
    private List<String> ridList = new ArrayList<String>();
    
    
    
    @Override
    public void unpack() {
        if (this.getResult().containsKey("count")) {
            this.setCount(Integer.parseInt(this.getResult().get("count")));
        }
        if (this.getResult().containsKey("reccount")) {
            this.setRecCount(Integer.parseInt(this.getResult().get("reccount")));
        }
        if (this.getResult().containsKey("ridlist")) {
            
            this.setRidList(Arrays.asList(this.getResult().get("ridlist").split(",")));
        }
        
    }

    /**
     * @return the count
     */
    public int getCount() {
        return count;
    }

    /**
     * @param count the count to set
     */
    public void setCount(int count) {
        this.count = count;
    }

    /**
     * @return the recCount
     */
    public int getRecCount() {
        return recCount;
    }

    /**
     * @param recCount the recCount to set
     */
    public void setRecCount(int recCount) {
        this.recCount = recCount;
    }

    /**
     * @return the ridList
     */
    public List<String> getRidList() {
        return ridList;
    }

    /**
     * @param ridList the ridList to set
     */
    public void setRidList(List<String> ridList) {
        this.ridList = ridList;
    }
    
}
