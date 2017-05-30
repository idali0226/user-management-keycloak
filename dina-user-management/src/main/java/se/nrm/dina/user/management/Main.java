/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.nrm.dina.user.management;

import java.io.Console;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties; 
import org.wildfly.swarm.Swarm;
import org.wildfly.swarm.management.ManagementFraction;  
import se.nrm.dina.user.management.utils.CommonString;
import se.nrm.dina.user.management.utils.HelpClass;

/**
 *
 * @author idali
 */
public class Main {

    public static void main(String... args) { 
        System.out.println("Running " + Main.class.getCanonicalName() + ".main"); 
        Properties prop = new Properties();
        
        Console console = System.console();
        
        HelpClass.getInstance().initRealmVariables(); 
        
        prop.setProperty(CommonString.getInstance().getKeycloakAuthURL(), HelpClass.getInstance().keycloakAuthUrlInput(console));
//        prop.setProperty(CommonString.getInstance().getRealmName(), HelpClass.getInstance().realmNameInput(console));
        prop.setProperty(CommonString.getInstance().getMailServerHost(), HelpClass.getInstance().mailServerHostInput(console));
        prop.setProperty(CommonString.getInstance().getMailServerPort(), HelpClass.getInstance().mailServerPortInput(console));
        prop.setProperty(CommonString.getInstance().getFrom(), HelpClass.getInstance().mailFromInput(console));
        prop.setProperty(CommonString.getInstance().getMailServerUsername(), HelpClass.getInstance().emailUsernameInput(console));
        prop.setProperty(CommonString.getInstance().getMailServerPassword(), HelpClass.getInstance().emailPasswordInput(console));
        prop.setProperty(CommonString.getInstance().getSuperUsername(), HelpClass.getInstance().superUsernameInput(console));
        prop.setProperty(CommonString.getInstance().getSuperPassword(), HelpClass.getInstance().superPasswordInput(console));
  
        OutputStream output = null;
        try { 
            output = new FileOutputStream(CommonString.getInstance().getConfigProperties()); 
            prop.store(output, null);

            Swarm swarm = new Swarm(); 
            swarm.fraction(getManagementFraction());  
            swarm.start().deploy();  
        } catch (FileNotFoundException ex) { 
        } catch (IOException ex) { 
        } catch (Exception ex) { 
        } finally {
            try {
                if(output != null) {
                    output.close();
                } 
            } catch (IOException ex) { 
            }
        }
    }
  
    private static ManagementFraction getManagementFraction() { 
        return ManagementFraction.createDefaultFraction()
                        .httpInterfaceManagementInterface((iface) -> { 
                            iface.securityRealm(CommonString.getInstance().getManagementRealm());
                            iface.consoleEnabled(Boolean.TRUE); 
                        })  
                        .securityRealm(CommonString.getInstance().getManagementRealm(), (realm) -> {
                            realm.inMemoryAuthentication((authn) -> {
                                authn.add(CommonString.getInstance().getMasterAdminUsrname(),
                                            CommonString.getInstance().getMasterAdminPassword(), 
                                            true);
                            });
                            realm.inMemoryAuthorization((authz) -> {
                                authz.add(CommonString.getInstance().getMasterAdminUsrname(),
                                            CommonString.getInstance().getMasterAdminPassword());
                            });
                        });
    }

}
