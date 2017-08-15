/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.nrm.dina.user.management.utils;

/**
 *
 * @author idali
 */
public enum ResourceType {
    
    User,
    Realm,
    Client,
    Role;
    
    public String getText() {
        return this.name().toLowerCase();
    } 
}
