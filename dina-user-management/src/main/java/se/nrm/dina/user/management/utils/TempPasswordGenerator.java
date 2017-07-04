/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.nrm.dina.user.management.utils;
 
import org.apache.commons.lang.RandomStringUtils;

/**
 *
 * @author idali
 */
public class TempPasswordGenerator {
     
    public static final int PASSWORD_LENGTH =12;
    
    public static String generateRandomPassword() { 
        char[] chars = {'!', '@', '#', '$', '%', '&', '*', '=', '+', '?' }; 
        return RandomStringUtils.random(0, 11, PASSWORD_LENGTH, true, true, chars);
    }
    
}
