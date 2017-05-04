/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.nrm.dina.user.management.utils;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 *
 * @author idali
 */
public class Util {
    
   private static Util instance = null;

    private final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("MMM dd, yyyy 'at' HH.mm.ss");
    private final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("MMM dd, yyyy");

    public static synchronized Util getInstance() {
        if (instance == null) {
            instance = new Util();
        }
        return instance;
    }
    
    public String dateToString(Date date) {
        if (date != null) {
            return SIMPLE_DATE_FORMAT.format(date);
        } else {
            return null;
        }
    }

    
}
