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

/**
 * 会员积分查询返回.
 * @author Aron llc_xyz@qq.com
 */
public class JBeiPointsDetailResponse extends JBeiResponse{

    private int totalPoints;
    
    private int validpoints;
    
    @Override
    public void unpack() {
       if(this.getResult().containsKey("totalpoints")){
            this.setTotalPoints(Integer.parseInt(this.getResult().get("totalpoints")));
       }
        if (this.getResult().containsKey("validpoints")) {
            this.setValidpoints(Integer.parseInt(this.getResult().get("validpoints")));
        }
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
     * @return the validpoints
     */
    public int getValidpoints() {
        return validpoints;
    }

    /**
     * @param validpoints the validpoints to set
     */
    public void setValidpoints(int validpoints) {
        this.validpoints = validpoints;
    }
    
}
