/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zhuc.nupay.mcm;

import com.zhuc.nupay.mcm.codec.PosMcAppProtocolCodecFactory;
import com.zhuc.nupay.mcm.handler.PosMcAppServerHandler;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.logging.Level;
import org.apache.log4j.Logger;

import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

/**
 * <h4>客户端与服务端通信协议.</h4>
 * <ul>
 * <li>采用不定长包结构:<br/>
 * 请求消息包结构:<br>
 * <table border="1" cellpadding="2" cellspacing="0" >
 * <tr>
 * <td>包长(len, 2 byte,整数)</td>
 * <td>指令(cmd, 1 byte,整数)</td>
 * <td>数据(data, N byte,根据指令类型决定)</td>
 * <td>CRC校验(crc, 2 byte,CRC16)</td></tr></table>
 * <br/>
 * 响应消息包结构:</br>
 * <table border="1" cellpadding="2" cellspacing="0" >
 * <tr>
 * <td>包长(len, 2 byte,整数)</td>
 * <td>指令(cmd, 1 byte,整数)</td>
 * <td>状态(status,1 byte,整数)</td>
 * <td>数据(data, N byte,根据指令类型决定)</td>
 * <td>CRC校验(crc, 2 byte,CRC16)</td></tr></table>
 * </li>
 * <ul>
 * <li>包长(len) 是指除去len以外，其他所有部分的字节长度</li>
 * <li>指令(cmd)
 * 现在支持的指令有查询积分(Inquiry,0x01),提交消费记录(Submit,0x02),撤销消费记录(Refund,0x03),批量操作(BatchOp,0x04).</li>
 * <li>状态(status)
 * 只用在返回消息中.表示该条请求的执行情况,具体状态值参见{@link com.zhuc.nupay.mcm.command.PosMcResponseStatus}
 * <li>数据(data) 根据指令的不同决定实际长度</li>
 * <li>CRC校验(crc) CRC16 算法,计算内容是是除crc本身以外的所有部分. 具体算法往下看</li>
 * <li>对于字符串,不足位，前面用0填充.</li>
 * <li>时间戳采用BCD编码，格式为:2013121619052301, 2013年12月16日19点05分23秒 星期1
 * </ul>
 *
 * </ul>
 * <ul>
 * <li>查询积分(Inquiry,0x01)</li>
 * <ul>
 * <li>请求消息 Data节结构:<br/>
 * <table border="1" cellpadding="2" cellspacing="0" >
 * <tr>
 * <td>商户ID(MerchantId,15 byte,字符串)</td>
 * <td>终端ID(TerminalId,8 byte,字符串)</td>
 * <td>卡号(CardNo,24 byte,字符串)</td>
 * </tr>
 * </table>
 * </li>
 * <br/>
 * <li>响应消息 Data 节结构:
 * <table border="1" cellpadding="2" cellspacing="0" ><tr>
 * <td>总积分(TotalPoints,4 byte,整数,BCD编码)</td>
 * <td>可用积分(AvailablePoints,4 byte,整数,BCD编码)</td>
 * <td>自定义消息长度(customerMsgLen,4 byte,整数)</td>
 * <td>自定义消息(customMessage,不定长,字符串,UTF-8编码)</td>
 *
 * </tr></table>
 * </li>
 *
 * </ul>
 * <br/>
 * <li>提交消费记录(Submit,0x02)</li>
 * <ul>
 * <li>请求消息 Data节结构:<br/>
 * <table border="1" cellpadding="2" cellspacing="0" >
 * <tr>
 * <td>商户ID(MerchantId,15 byte,字符串)</td>
 * <td>终端ID(TerminalId,8 byte,字符串)</td>
 * <td>卡号(CardNo,24 byte,字符串)</td>
 * <td>交易金额(Amount,4 byte,整数,BCD编码)</td>
 * <td>时间戳(timestamp,8 byte,长整型,BCD编码)</td>
 * </tr>
 * </table>
 * </li>
 * <li>响应消息 Data节结构:<br/>
 * <table border="1" cellpadding="2" cellspacing="0" ><tr>
 * <td>交易号(TxnId,8byte,整数, BCD编码</td>
 * <td>返现金额(reBates,4 byte,整数,BCD 编码</td>
 * <td>总积分(TotalPoints,4 byte,整数,BCD编码)</td>
 * <td>可用积分(AvailablePoints,4 byte,整数,BCD编码)</td>
 * <td>收款卡号(receivableCardNo,24 byte,数字字符串</td>
 * <td>自定义消息长度(customerMsgLen,4 byte,整数)</td>
 * <td>自定义消息(customMessage,不定长,字符串,UTF-8编码)</td>
 * </tr></table>
 *
 * </li>
 *
 * </ul>
 * <li>撤销/退款记录(Refund,0x03)</li>
 * <ul> <li>撤销消费记录请求和提交消费记录一样,唯一不同的是将指令节改为撤销/退款(0x03)</li>
 *         <li>退款响应消息 在提交消费的基础上，删除返利金额和收款卡号两个字段.</li>
 * </ul>
 * <li>批量操作（BatchOp,0x04)</li>
 * <ul>
 * <li>请求消息 Data节结构:<br/>
 * <table border="1" cellpadding="2" cellspacing="0" >
 * <tr>
 * <td>商户ID(MerchantId,15 byte,字符串)</td>
 * <td>终端ID(TerminalId,8 byte,字符串)</td>
 * <td>交易记录数(TxCnt,4 byte,整数)</td>
 * <td>交易类型(TxType,1
 * byte,整数){@link com.zhuc.nupay.mcm.command.PosMcCommand}</td>
 * <td>卡号(CardNo,24 byte,整数)</td>
 * <td>交易金额(Amount,4 byte,整数,BCD编码)</td>
 * <td>时间戳(timestamp,8 byte,长整型,BCD编码)</td>
 *  <td>返利金额(Rebates,4 byte,整数,BCD编码)</td>
 * <td>...根据记录数重复txType->>timestamp之间的内容</td>
 * </tr>
 * </table>
 * </li>
 * <li>响应消息 Data节结构:<br/>
 * <ul><li>响应消息暂时没有任何值,只在status里返回状态即可.</li></ul>
 * </li>
 * </ul>
 * 
 * <li>更新数据（UpdateData,0x05)</li>
 * <ul>
 *  <li>请求消息 Data节结构:<br/>
 * <table border="1" cellpadding="2" cellspacing="0" >
 * <tr>
 * <td>商户ID(MerchantId,15 byte,字符串)</td>
 * <td>终端ID(TerminalId,8 byte,字符串)</td>
 * </tr>
 * 
 * </table>
 * <li>响应消息 Data节结构:<br/>
 * <table border="1" cellpadding="2" cellspacing="0" ><tr>
 * <td>返利基数(Rebates Base,    3 byte,整数,BCD 编码</td>
 * <td>返利比例(Rebates Percent, 1byte,整数, BCD编码</td>
 * <td>服务器IP(Server Address, 64byte,字符串, </td>
 * <td>服务器端口(Server port,4byte,整数</td>
 * </tr></table>
 * </li>
 * 
 * </ul>
 * <li> 样例 </li>
 * <ul>
 * <li>封包</br>
 * <table border="1" cellpadding="2" cellspacing="0" >
 * <tr> <td rowSpan="3">查询积分(Inquiry,0x01)</td>
 * <td>卡号:123456789012345678901234,商户ID: 123456789012345 ,终端ID: 12345678,返回:
 * 总积分:9876,可用积分:1234,定制消息:Welcome Aron Lau</td>
 * </tr>
 * <tr><td>请求:00,32,01,31,32,33,34,35,36,37,38,39,30,31,32,33,34,35,31,32,33,34,35,36,37,38,31,32,33,34,35,36,37,38,39,30,31,32,33,34,35,36,37,38,39,30,31,32,33,34,d7,c0</td></tr>
 * <tr><td>响应:00,20,01,00,00,00,98,76,00,00,12,34,00,00,00,10,57,65,6c,63,6f,6d,65,20,41,72,6f,6e,20,4c,61,75,6a,05</td></tr>
 * <tr> <td rowSpan="3">提交消费记录(Submit,0x02)</td>
 * <td>卡号:123456789012345678901234,商户ID: 123456789012345 ,终端ID:12345678,消费:1000,返回:
 * 交易号:9999999, 总积分20000,可用积分:1999,返利金额: 200 (分) ,收款卡号:2890****************9111,定制消息:Hello,Ad from Server,Submit Ok</td>
 * </tr>
 * <tr><td>请求:00,3e,02,31,32,33,34,35,36,37,38,39,30,31,32,33,34,35,31,32,33,34,35,36,37,38,31,32,33,34,35,36,37,38,39,30,31,32,33,34,35,36,37,38,39,30,31,32,33,34,00,00,10,00,20,14,01,18,11,59,10,06,86,0b</td></tr>
 * <tr><td>响应:00,52,02,00,00,00,00,00,09,99,99,99,00,00,02,00,00,02,00,00,00,00,19,99,32,38,39,30,2a,2a,2a,2a,2a,2a,2a,2a,2a,2a,2a,2a,2a,2a,2a,2a,39,31,31,31,00,00,00,1e,48,65,6c,6c,6f,2c,41,64,20,66,72,6f,6d,20,53,65,72,76,65,72,2c,53,75,62,6d,69,74,20,4f,6b,39,4c</td></tr>
 *
 * <tr> <td rowSpan="3">退款/撤销(ReFund,0x03)</td>
 * <td>卡号:123456789012345678901234,商户ID: 123456789012345 ,终端ID:12345678,退款:850,返回:
 * 总积分20000,可用积分:1923,定制消息:Hello,ReFund Ok!!!</td>
 * </tr>
 * <tr><td>请求:00,3e,03,31,32,33,34,35,36,37,38,39,30,31,32,33,34,35,31,32,33,34,35,36,37,38,31,32,33,34,35,36,37,38,39,30,31,32,33,34,35,36,37,38,39,30,31,32,33,34,00,00,08,50,20,14,01,18,12,03,02,06,4b,c1</td></tr>
 * <tr><td>响应:00,2a,03,00,00,00,00,00,09,99,99,99,00,02,00,00,00,00,19,23,00,00,00,12,48,65,6c,6c,6f,2c,52,65,46,75,6e,64,20,4f,6b,21,21,21,02,44</td></tr>
 *
 * <tr> <td rowSpan="3">批量操作(BatchSubmit,0x04)</td>
 * <td>商户ID: 123456789012345 ,终端ID:12345678</br>
 *  第一笔 ->卡号:888888888888888888888881,交易金额:110,返利金额: 10</br>
 *  第二笔->卡号:888888888888888888888882, 交易金额;120,返利金额: 20</br>
 *  第三笔->卡号:888888888888888888888883, 交易金额;130,返利金额: 30</br>
 *  第四笔->卡号:888888888888888888888884, 交易金额;140,返利金额: 40</br>返回:
 * 成功! </td>
 * </tr>
 * 
 * <tr><td>请求:00,c2,04,31,32,33,34,35,36,37,38,39,30,31,32,33,34,35,31,32,33,34,35,36,37,38,00,</br>
 * 00,00,04,02,38,38,38,38,38,38,38,38,38,38,38,38,38,38,38,38,38,38,38,38,38,38,38,31,00,00,01,10,20,14,</br>
 * 01,22,12,10,59,03,00,00,00,10,02,38,38,38,38,38,38,38,38,38,38,38,38,38,38,38,38,38,38,38,38,38,38,38,32,</br>
 * 00,00,01,20,20,14,01,22,12,10,59,03,00,00,00,20,03,38,38,38,38,38,38,38,38,38,38,38,38,38,38,38,38,38,38,38,38,</br>
 * 38,38,38,33,00,00,01,30,20,14,01,22,12,10,59,03,00,00,00,30,03,38,38,38,38,38,38,38,38,38,38,38,38,38,38,38,38,38,</br>
 * 38,38,38,38,38,38,34,00,00,01,40,20,14,01,22,12,10,59,03,00,00,00,40,58,35</td></tr>
 * <tr><td>响应:00,05,04,00,00,be,3d</td></tr>
 *
 * <tr> <td rowSpan="3">更新数据(UpdateData,0x05)</td>
 * <td>商户ID: 200000000005 ,终端ID: 30009<br>
 *      返回：返利比例 [5%],返利基数:100,服务器:meme.zhuc.net,端口:5100)</br></td>
 * </tr>
 * <tr><td>请求:00,1a,05,31,32,33,34,35,36,37,38,39,30,31,32,33,34,35,31,32,33,34,35,36,37,38,40,70</td></tr>
 * <tr><td>响应:00,4c,05,00,00,01,00,05,6d,65,6d,65,2e,7a,68,75,63,2e,6e,65,74,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,13,ec,00,0c</td></tr>
 * </table>
 * 
 * 
 *
 * </li>
 * </ul>
 * * <li><b> NFC卡 (MIFARE S50)</b></li>
 * <ul><li>卡号写入位置:</br>
 *<table border="1" cellpadding="2" cellspacing="0" >
 * <tr>
 *  <td rowSpan="4">0扇区</td><td>块0(16byte) : 卡序列号 readonly<td></td>
 * </tr>
 * <tr><td>块1(16 byte): MSP 卡号前16byte </td></tr>
 * <tr><td>块2(16 byte): MSP 卡号后8byte </td></tr>
 * <tr><td>块3(16 byte): 扇区控制(KEYA+ACCESS_BIT+KEYB)</td></tr> 
 * </table>
 * </li>
 * </ul>
 * <li><b>update 2014/01/15</b></li>
 *  <ul>
 * <li> 增加更新数据(UpdateData)请求,POSapp在每次启动时更新,返回返利比例，与服务器地址,端口, 做离线交易时,返利金额 = 交易金额*返利比例</li>
 *  <li>实时消费交易(Submit) 返回消息增加返利金额,收款卡号</li>
 *  <li>批量交易单笔增加返利金额字段,对批量里的退款也一样增加返利金额,只是永远为0.</li>
 *  
 * </ul>
 * <li><b>update 2014/01/18</b></li>
 * <ul>
 * <li> 返利,修改为3字节返利基数，1字节返利比例，BCD 编码, </li>
 * <li>交易卡号，收款卡号都改为24字节.</li>
 *
 * </ul>
 * 
 * <li><b>update 2014/01/22</b></li>
 * <ul>
 * <li>修改批量交易中返利金额封包顺序,将返利金额放在交易时间戳后面</li>
 * <li>增加NFC卡 MSP卡号写入约定</li>
 *
 * </ul>
 * 
 * 
 * </ul>
 *
 * @author Aron llc_xyz@zhuc.net
 */
public class PosMcAppServer implements Runnable {

    private static final Logger log = Logger.getLogger(PosMcAppServer.class.getName());

    //服务器端口.
    private int port = 9990;
    
    
    private final PosMcAppServerHandler handler;

    public PosMcAppServer() {
        handler = new PosMcAppServerHandler();
    }

    public PosMcAppServer(int port) {
        handler = new PosMcAppServerHandler();
        this.port = port;

    }

    public void setApp(PosMcApp app) {

        if (handler != null) {
            log.info("Using App ["+app.getClass().getName()+"]");
            handler.setApp(app);
        }

    }

    public void run() {
        //PropertyConfigurator.configure("log4j.properties");

        IoAcceptor acceptor = new NioSocketAcceptor();

        acceptor.getFilterChain().addLast("logger", new LoggingFilter());

        acceptor.getFilterChain().addLast("codec", new ProtocolCodecFilter(new PosMcAppProtocolCodecFactory(false)));

        acceptor.setHandler(handler);

        acceptor.getSessionConfig().setReadBufferSize(2048);

        acceptor.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, 10);

        log.info(" App["+handler.getApp().getClass().getName()+"] Listen on " + this.getPort());
        try {
            acceptor.bind(new InetSocketAddress(getPort()));

        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(PosMcAppServer.class.getName()).log(Level.SEVERE, null, ex);
            log.info("Listen failed");
        }

    }

    /**
     * @return the port
     */
    public int getPort() {
        return port;
    }

    /**
     * @param port the port to set
     */
    public void setPort(int port) {
        this.port = port;
    }
}
