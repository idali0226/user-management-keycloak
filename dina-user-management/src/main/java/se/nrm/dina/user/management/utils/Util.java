/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.nrm.dina.user.management.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.time.Instant; 
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author idali
 */
public class Util {
    
    private String mailHost;
    private String mailPort;
    private String mailFrom;
    private String emailUserName;
    private String emailPassword;
    
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    
    private static Util instance = null;
    private Properties prop;

    private final DateTimeFormatter FORMATTER_WITH_TIMESTAMP = DateTimeFormatter.ofPattern("MMM dd, yyyy 'at' HH.mm.ss");
    private final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("MMM dd, yyyy");

    public static synchronized Util getInstance() {
        if (instance == null) { 
            instance = new Util();
            instance.uploadProperties();
        }
        return instance;
    }
    
    public Map<String, String> buildProtocolMap(String attribute, String claimName) {
        Map<String, String> protocolMap = new HashMap<>();
        protocolMap.put(CommonString.getInstance().getMultiValued(), Boolean.TRUE.toString());
        protocolMap.put(CommonString.getInstance().getUserInfoTokenClaim(), Boolean.TRUE.toString());
        protocolMap.put(CommonString.getInstance().getUserAttribute(), attribute);
        protocolMap.put(CommonString.getInstance().getIdTokenClaim(), Boolean.TRUE.toString());
        protocolMap.put(CommonString.getInstance().getAccessTokenClaim(), Boolean.TRUE.toString());
        protocolMap.put(CommonString.getInstance().getClientName(), claimName);
        protocolMap.put(CommonString.getInstance().getJsonTypeLabel(), CommonString.getInstance().getStringDataFormat());  
        
        return protocolMap;
    }
    
    public String dateToString(Date date) {
        if (date != null) { 
            return SIMPLE_DATE_FORMAT.format(date);
        } else {
            return null;
        }
    }
     
    
    public String dateLongToString(long dateLong) {
        LocalDateTime localDate = Instant.ofEpochMilli(dateLong)
                                         .atZone(ZoneId.systemDefault())
                                         .toLocalDateTime(); 
        String strDate = localDate.format(FORMATTER_WITH_TIMESTAMP); 
        return strDate;
    } 
    
    public boolean initialSetup() { 
        return Boolean.valueOf(prop.getProperty(CommonString.getInstance().getInitialSetup()));
    }
    
    public String getPropertyValue(String key) {
        return prop.getProperty(key);
    }
    
    private void uploadProperties() {
        
        logger.info("uploadProperties");
        
        prop = new Properties();
        InputStream input = null; 
        try {  
            input = new FileInputStream(CommonString.getInstance().getConfigProperties());
            prop.load(input); 
        } catch (IOException ex) { 
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) { 
                }
            }
        }
    }
}
