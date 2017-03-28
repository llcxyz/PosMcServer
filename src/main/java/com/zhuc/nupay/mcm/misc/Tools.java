/*
 * Copyright (C) 2013 Aron/llc_xyz@qq.com
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

package com.zhuc.nupay.mcm.misc;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.mina.proxy.utils.ByteUtilities;

/**
 *
 * @author Aron/llc_xyz@qq.com
 */
public class Tools {
    
    public final static char[] BToA = "0123456789abcdef".toCharArray();
    
    
    public static byte[] long2bytes(long num) {
        byte[] b = new byte[8];
        for (int i = 0; i < 8; i++) {
            b[i] = (byte) (num >>> (56 - (i * 8)));
        }
        return b;
    }
    public static long bytes2long(byte[] b) {
        long temp = 0;
        long res = 0;
        for (int i = 0; i < 8; i++) {
            res <<= 8;
            temp = b[i] & 0xff;
            res |= temp;
        }
        return res;
    }
    public static  byte[] fixLength(byte[] b, int len) {

        if (b.length < len) {
            byte[] b2 = new byte[len];
            System.arraycopy(b, 0, b2, 0, b.length);
            return b2;
        } else {
            return b;
        }

    }
    /**
     * @param bytes
     * @return 
     * @函数功能: BCD码转为10进制串(阿拉伯数据)
     * @输入参数: BCD码
     * @输出结果: 10进制串
     */
//    public static String bcd2Str(byte[] bytes) {
//        StringBuffer temp = new StringBuffer(bytes.length * 2);
//
//        for (int i = 0; i < bytes.length; i++) {
//            temp.append((byte) ((bytes[i] & 0xf0) >>> 4));
//            temp.append((byte) (bytes[i] & 0x0f));
//        }
//        return temp.toString().substring(0, 1).equalsIgnoreCase("0") ? temp
//                .toString().substring(1) : temp.toString();
//    }

    /**
     * 10进制串转为BCD码
     *
     * @param asc 10进制串
     * @param len 期望转换成后的长度
     * @return BCD码(十位,个位;千位,百位;十万位,万位;....)
     * @throws NotExceptException
     */
    public static byte[] str2BcdByte(String asc, int len) {
        int xLen = asc.length();
       
        StringBuffer extAdd = new StringBuffer();
        for (int i = 0; i < 2 * len - xLen; i++) {
            extAdd.append("0");
        }
        String xAsc = extAdd.append(asc).toString();
        xLen = xAsc.length();
        byte abt[] = new byte[xLen];
        if (xLen >= 2) {
            xLen = xLen / 2;
        }
        byte bbt[] = new byte[len];
        abt = xAsc.getBytes();
        int j, k;

        for (int p = len - 1; p >= 0; p--) {
            if ((abt[2 * p] >= '0') && (abt[2 * p] <= '9')) {
                j = abt[2 * p] - '0';
            } else if ((abt[2 * p] >= 'a') && (abt[2 * p] <= 'z')) {
                j = abt[2 * p] - 'a' + 0x0a;
            } else {
                j = abt[2 * p] - 'A' + 0x0a;
            }

            if ((abt[2 * p + 1] >= '0') && (abt[2 * p + 1] <= '9')) {
                k = abt[2 * p + 1] - '0';
            } else if ((abt[2 * p + 1] >= 'a') && (abt[2 * p + 1] <= 'z')) {
                k = abt[2 * p + 1] - 'a' + 0x0a;
            } else {
                k = abt[2 * p + 1] - 'A' + 0x0a;
            }

            int a = (j << 4) + k;
            byte b = (byte) a;
            bbt[p] = b;
        }

        return bbt;
    }

    /**
     * BCD码转ASC码
     *
     * @param bytes BCD串(十位,个位;千位,百位;十万位,万位;....)
     * @return 10进制串
     */
    public static String BCD2ASCByte(byte[] bytes) {
        StringBuffer temp = new StringBuffer(bytes.length * 2);

        for (int i = bytes.length - 1; i >= 0; i--) {
            int h = ((bytes[i] & 0xf0) >>> 4);
            int l = (bytes[i] & 0x0f);
            temp.append(BToA[h]).append(BToA[l]);
        }
        return temp.toString();
    }

    /**
     * 将日月年BCD格式的数组转换为yyMMdd日期格式的字符串
     *
     * @param bytes
     * @return yyMMdd日期格式的字符串
     * @throws LenNotMatchException
     */
    public static String BCD2ASCDate(byte[] bytes) {
      
        StringBuffer temp = new StringBuffer();
        for (int i = 2; i >= 0; i--) {
            temp.append(BCD2ASCByte(new byte[]{bytes[i]}));
        }
        return temp.toString();
    }

    /**
     * @函数功能: 10进制串转为BCD码
     * @输入参数: 10进制串
     * @输出结果: BCD码
     */
    public static byte[] str2Bcd(String asc) {
        int len = asc.length();
        int mod = len % 2;

        if (mod != 0) {
            asc = "0" + asc;
            len = asc.length();
        }

        byte abt[] = new byte[len];
        if (len >= 2) {
            len = len / 2;
        }

        byte bbt[] = new byte[len];
        abt = asc.getBytes();
        int j, k;

        for (int p = 0; p < asc.length() / 2; p++) {
            if ((abt[2 * p] >= '0') && (abt[2 * p] <= '9')) {
                j = abt[2 * p] - '0';
            } else if ((abt[2 * p] >= 'a') && (abt[2 * p] <= 'z')) {
                j = abt[2 * p] - 'a' + 0x0a;
            } else {
                j = abt[2 * p] - 'A' + 0x0a;
            }

            if ((abt[2 * p + 1] >= '0') && (abt[2 * p + 1] <= '9')) {
                k = abt[2 * p + 1] - '0';
            } else if ((abt[2 * p + 1] >= 'a') && (abt[2 * p + 1] <= 'z')) {
                k = abt[2 * p + 1] - 'a' + 0x0a;
            } else {
                k = abt[2 * p + 1] - 'A' + 0x0a;
            }

            int a = (j << 4) + k;
            byte b = (byte) a;
            bbt[p] = b;
        }
        return bbt;
    }

    
    /**
     * @param bytes
     * @return 
     * @函数功能: BCD码转ASC码
     * @输入参数: BCD串
     * @输出结果: ASC码
     */
    public static String bcd2str(byte[] bytes) {
        StringBuilder temp = new StringBuilder(bytes.length * 2);

        for (int i = 0; i < bytes.length; i++) {
            int h = ((bytes[i] & 0xf0) >>> 4);
            int l = (bytes[i] & 0x0f);
            temp.append(BToA[h]).append(BToA[l]);
        }
        return temp.toString();
    }
    
    public static byte[] int2bcd(int value,int fixedlen) {
        String dst = String.valueOf(value);
        
        return Tools.str2BcdByte(dst, fixedlen);
        
    }
    public static int bcd2int(byte[] bytes){
        
        return Integer.parseInt(Tools.bcd2str(bytes));
    }
    public static byte[] long2bcd(long value, int fixedlen) {
        String dst = String.valueOf(value);

        return Tools.str2BcdByte(dst, fixedlen);

    }

    public static long bcd2long(byte[] bytes) {

        return Long.parseLong(Tools.bcd2str(bytes));
    }
    
    /**
     * timestamp 转成bcd.
     * 最后1个字节是星期.
     * @param timestamp
     * @param fixedlen
     * @return
     * @throws Exception 
     */
    public static byte[] timestamp2bcd(long timestamp,int fixedlen) {
        
        Calendar cl = Calendar.getInstance();
        Date d = new Date();

        d.setTime(timestamp);
        
        cl.setTime(d);
        
        String rs = String.valueOf(timestamp);
        
        String xq = "0"+(cl.get(Calendar.DAY_OF_WEEK)-1);
        
        rs.substring(rs.length()-2, rs.length());
        
        DateFormat format1 = new SimpleDateFormat(
                "yyyyMMddhhmmss");
        
        String s = format1.format(d)+xq;
        
        return Tools.str2BcdByte(s, fixedlen);
        
        
    }
    
    /**
     * bcd码转成timestamp,
     * 将最后1个字节的星期要去掉.
     * @param bcds
     * @return 
     */
    public static long bcd2timestamp(byte[] bcds){
        try {
            byte[] timebcd = new byte[bcds.length];
            
            System.arraycopy(bcds, 0, timebcd, 0, bcds.length-1);
            
            // timebcd[timebcd.length-2] = 0x00;
            
            timebcd[timebcd.length-1] = 0x00;
            String time = Tools.bcd2str(timebcd);
            time  = time.substring(0, time.length()-2);
            DateFormat format=  new SimpleDateFormat("yyyyMMddhhmmss");
            return format.parse(time).getTime();
        } catch (ParseException ex) {
            Logger.getLogger(Tools.class.getName()).log(Level.SEVERE, null, ex);
            return 0;
        }
       
    
    }
    
    public static String long2datestr(Long timestamp){
        
        Date d = new Date();
        d.setTime(timestamp);
        
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(d);
        
    }
    
    public static byte trErrCode(int errCode){
        if (errCode>=255)
            return (byte) (0xff&Math.abs(errCode-1000));
        
        return (byte)(0xff&errCode);
        
        
    }
    
    
    
    
    public static void main(String[] argv){
        System.out.println(" current mill:"+System.currentTimeMillis()+",,0xff="+(0xff&256));
        
        try {
            byte[] date = timestamp2bcd(System.currentTimeMillis(),8);
            System.out.println("date == " + ByteUtilities.asHex(date, ","));
            System.out.println(" real date -"+Tools.bcd2timestamp(date));
            Date dateobj = new Date();
            dateobj.setTime(Tools.bcd2timestamp(date));
            
            System.out.println(" date  obj = "+dateobj);
            
            byte[] mon = Tools.str2BcdByte("20130405123355",8);
            System.out.println("byte == "+ByteUtilities.asHex(mon,","));
            System.out.println(" str = "+Tools.bcd2str(mon)+",int = "+Long.parseLong(Tools.bcd2str(mon)));
            
        } catch (Exception ex) {
            Logger.getLogger(Tools.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
}
