/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.nrm.dina.keycloak.management;
 
import se.nrm.dina.keycloak.management.logic.RealmManagement;

/**
 *
 * @author idali
 */
public class Main {

    public static void main(String... args) throws Exception {
        
        if(args != null) {
            System.out.println("args size = " + args.length);
        }
  
        RealmManagement rm = new RealmManagement();
        rm.createRealm("test");
    }

}
