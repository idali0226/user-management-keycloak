/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.nrm.dina.user.management.utils;

import java.io.Console;

/**
 *
 * @author idali
 */
public class HelpClass {

    private static HelpClass instance = null;

    public static synchronized HelpClass getInstance() {
        if (instance == null) {
            instance = new HelpClass();
        }
        return instance;
    }
    
    public String setupInitialDataInput(Console console) {
        System.out.println("*-------------------------------------------------------------------------------------------------------*");
        System.out.print("* Do you want to setup initial data in Keycloak (y/n)? ");
        return console.readLine().trim();
    }
    
    public void initRealmVariables() {
        System.out.println();
        System.out.println("*------------------------------------------------------------------------------------------------------*");
        System.out.println("* The following steps setups initial data in keycloak for running user-management                      *");  
        System.out.println("*------------------------------------------------------------------------------------------------------*");
        System.out.println();
    }

    public String keycloakAuthUrlInput(Console console) {
        System.out.println("*-------------------------------------------------------------------------------------------------------*");
        System.out.print("* Enter keycloak authentication URL: ");
        return console.readLine().trim();
    }
    
//    public String realmNameInput(Console console) {
//        System.out.println("*------------------------------------------------------------------*");
//        System.out.print("* Enter realm name: ");
//        return console.readLine().trim();
//    }
     
    public String mailServerHostInput(Console console) {
        System.out.println("*------------------------------------------------------------------*");
        System.out.print("* Setup mail server: ");
        System.out.print("* Enter mail server host: ");
        return console.readLine().trim();
    }
     
    public String mailFromInput(Console console) {
        System.out.println("*------------------------------------------------------------------*"); 
        System.out.print("* Enter mail from: ");
        return console.readLine().trim();
    }
        
    public String mailServerPortInput(Console console) {
        System.out.println("*------------------------------------------------------------------*"); 
        System.out.print("* Enter mail server port: ");
        return console.readLine().trim();
    }
     
    public String emailUsernameInput(Console console) {
        System.out.println("*------------------------------------------------------------------*"); 
        System.out.print("* Enter email username: ");
        return console.readLine().trim();
    }
     
    public String emailPasswordInput(Console console) {
        System.out.println("*------------------------------------------------------------------*");
        System.out.print("* Enter email password: ");
        char passwordArray[] = console.readPassword();
        return new String(passwordArray).trim();
    }
    
    public String superUsernameInput(Console console) {
        System.out.println("*------------------------------------------------------------------*");
        System.out.print("* Enter super username (username must be a valid email address): ");
        return console.readLine().trim();
    }
    
    public String superPasswordInput(Console console) {
        System.out.println("*------------------------------------------------------------------*");
        System.out.print("* Enter super password: ");
        char passwordArray[] = console.readPassword();
        return new String(passwordArray).trim();
    }
}
