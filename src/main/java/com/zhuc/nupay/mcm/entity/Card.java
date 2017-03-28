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

package com.zhuc.nupay.mcm.entity;

import java.io.Serializable;
import javax.annotation.Generated;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 *
 * @author Aron llc_xyz@qq.com
 */
@Entity
@Table(name="card")
@NamedQueries({
    @NamedQuery(name = "card.findbyno", query = "select c from card c "
            + "where c.no = :no")
})

public class Card implements Serializable {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long Id;
    
    private String no;
    
    private int total_points;
    
    private int  avaliable_points;

    
    /**
     * @return the no
     */
    public String getNo() {
        return no;
    }

    /**
     * @param no the no to set
     */
    public void setNo(String no) {
        this.no = no;
    }

    /**
     * @return the total_points
     */
    public int getTotal_points() {
        return total_points;
    }

    /**
     * @param total_points the total_points to set
     */
    public void setTotal_points(int total_points) {
        this.total_points = total_points;
    }

    /**
     * @return the avaliable_points
     */
    public int getAvaliable_points() {
        return avaliable_points;
    }

    /**
     * @param avaliable_points the avaliable_points to set
     */
    public void setAvaliable_points(int avaliable_points) {
        this.avaliable_points = avaliable_points;
    }
    
}
