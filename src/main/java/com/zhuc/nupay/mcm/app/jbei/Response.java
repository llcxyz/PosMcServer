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

import java.util.Map;

/**
 *
 * @author Aron llc_xyz@qq.com
 */
public interface Response {
    public void unpack();
    /**
     * @return the success
     */
    public boolean isSuccess();

    /**
     * @param success the success to set
     */
    public void setSuccess(boolean success);

    /**
     * @return the errCode
     */
    public int getErrCode();

    /**
     * @param errCode the errCode to set
     */
    public void setErrCode(int errCode);

    /**
     * @return the errMsg
     */
    public String getErrMsg();

    /**
     * @param errMsg the errMsg to set
     */
    public void setErrMsg(String errMsg);

    /**
     * @return the content
     */
    public String getContent();

    /**
     * @param content the content to set
     */
    public void setContent(String content);

    /**
     * @return the result
     */
    public Map<String, String> getResult();

    /**
     * @param result the result to set
     */
    public void setResult(Map<String, String> result);

}
