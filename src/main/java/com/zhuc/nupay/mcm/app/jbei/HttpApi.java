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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
/**
 *
 * @author Aron llc_xyz@qq.com
 */
public class HttpApi {
    
    public String post(String url,List<NameValuePair> params) throws HttpApiException {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        
        HttpPost httpPost = new HttpPost(url);
//        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
//        nvps.add(new BasicNameValuePair("username", "admin"));
//        nvps.add(new BasicNameValuePair("password", "admin"));
//        // 添加post参数
       
        CloseableHttpResponse response = null;
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(params));
            response = httpclient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                // 获得响应字符集编码
                ContentType contentType = ContentType.getOrDefault(entity);
                Charset charset = contentType.getCharset();
                InputStream is = entity.getContent();
                // 将inputstream转化为reader，并使用缓冲读取，还可按行读取内容
                BufferedReader br = new BufferedReader(
                        new InputStreamReader(is, charset));
                StringBuilder sb = new StringBuilder();
                String line=null;
                while ((line = br.readLine()) != null) {
                    sb.append(line+"\n");
                }
                
                is.close();
                //System.out.println("返回:->"+sb.toString());
                
                return sb.toString();
            }
            else{
                throw new HttpApiException("Empty Response from server");
            }
            
            
        } catch (IOException ex) {
            Logger.getLogger(HttpApi.class.getName()).log(Level.SEVERE, null, ex);
            
            throw new HttpApiException("Network Error",ex);
            
            
        }  finally {
                try {
                    if(response!=null) response.close();
                } catch (IOException ex) {
                    Logger.getLogger(HttpApi.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
    
    }
}
