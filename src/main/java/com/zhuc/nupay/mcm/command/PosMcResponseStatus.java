/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.zhuc.nupay.mcm.command;

/**
 *
 * @author Aron
 */
public class PosMcResponseStatus {
    public static byte SUCCESS = 0x00;       
    public static byte FAILED = 0x01;
    
    public static byte NO_DATA_PROCESSED =(byte)201;
    public static byte SERVER_API_ERROR = (byte)200;
    
    
}
