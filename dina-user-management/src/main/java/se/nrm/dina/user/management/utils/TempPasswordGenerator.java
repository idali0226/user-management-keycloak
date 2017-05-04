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
     
    public static final int PASSWORD_LENGTH = 8;
    
    public static String generateRandomPassword() { 
        return RandomStringUtils.random(PASSWORD_LENGTH, true, true);
    }
    
}
