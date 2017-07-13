/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.nrm.dina.user.management.exceptions;

import java.util.ArrayList;
import java.util.List;
import javax.ejb.ApplicationException;

/**
 *
 * @author idali
 */
@ApplicationException
public class UserManagementException extends RuntimeException {
    
    private final int errorCode;
    private final String errorType; 
    private List<String> errorMessages; 
    
    public UserManagementException(int errorCode, String errorType, String errorMessage) {
        super(errorMessage);
        this.errorCode = errorCode;
        this.errorType = errorType; 
    }
        
    public UserManagementException(int errorCode, String errorType, List<String> errorMessages) {
        this.errorCode = errorCode;
        this.errorType = errorType;
        this.errorMessages = errorMessages;
    }
    
    public int getErrorCode() {
        return errorCode;
    }

    public String getErrorType() {
        return errorType;
    }
  
    public List<String> getErrorMessages() {
        return errorMessages;
    }
    
    public List<String> getErrorMessageList() {
        if(errorMessages == null || errorMessages.isEmpty()) { 
            errorMessages = new ArrayList<>();
            errorMessages.add(getMessage());  
        }
        return errorMessages;
    }
    
}
