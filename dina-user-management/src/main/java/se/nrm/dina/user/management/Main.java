/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.nrm.dina.user.management;
 
import java.util.Map;
import org.wildfly.swarm.Swarm;
import org.wildfly.swarm.management.ManagementFraction;
import se.nrm.dina.user.management.utils.CommonString; 

/**
 *
 * @author idali
 */
public class Main {

    public static void main(String... args) {
        System.out.println("Running " + Main.class.getCanonicalName() + ".main");
        
        Map<String, String> env = System.getenv();
        for (String envName : env.keySet()) {
            System.out.format("%s=%s%n",
                              envName,
                              env.get(envName));
        }
                             
//        Console console = System.console();
//        String setupInitialData = HelpClass.getInstance().setupInitialDataInput(console).trim().toLowerCase();
//        readInInitData(console, setupInitialData); 
        try {
            Swarm swarm = new Swarm();

            swarm.fraction(getManagementFraction());
            swarm.start().deploy();
        } catch (Exception ex) {
        }
    }

//    private static void readInInitData(Console console, String initialSetup) {
//        Properties prop = new Properties();
//        prop.setProperty(CommonString.getInstance().getInitialSetup(), initialSetup);
//        prop.setProperty(CommonString.getInstance().getKeycloakAuthURL(), HelpClass.getInstance().keycloakAuthUrlInput(console));
//        if (initialSetup.equals("yes") || initialSetup.equals("y")) { 
//            HelpClass.getInstance().initRealmVariables();
////            prop.setProperty(CommonString.getInstance().getKeycloakAuthURL(), HelpClass.getInstance().keycloakAuthUrlInput(console));
//            //        prop.setProperty(CommonString.getInstance().getRealmName(), HelpClass.getInstance().realmNameInput(console));
//            prop.setProperty(CommonString.getInstance().getMailServerHost(), HelpClass.getInstance().mailServerHostInput(console));
//            prop.setProperty(CommonString.getInstance().getMailServerPort(), HelpClass.getInstance().mailServerPortInput(console));
//            prop.setProperty(CommonString.getInstance().getFrom(), HelpClass.getInstance().mailFromInput(console));
//            prop.setProperty(CommonString.getInstance().getMailServerUsername(), HelpClass.getInstance().emailUsernameInput(console));
//            prop.setProperty(CommonString.getInstance().getMailServerPassword(), HelpClass.getInstance().emailPasswordInput(console));
//            prop.setProperty(CommonString.getInstance().getSuperUsername(), HelpClass.getInstance().superUsernameInput(console));
//            prop.setProperty(CommonString.getInstance().getSuperPassword(), HelpClass.getInstance().superPasswordInput(console));
//        }
//
//        OutputStream output = null;
//        try {
//            output = new FileOutputStream(CommonString.getInstance().getConfigProperties());
//            prop.store(output, null);
//        } catch (IOException ex) {
//        } finally {
//            try {
//                if (output != null) {
//                    output.close();
//                }
//            } catch (IOException ex) {
//            }
//        }
//    }

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
