/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.nrm.dina.user.management;
  
import org.wildfly.swarm.Swarm;
import org.wildfly.swarm.management.ManagementFraction;
import se.nrm.dina.user.management.utils.CommonString; 

/**
 *
 * @author idali
 */
public class Main {

//    public static void main(String... args) throws Exception {
//        System.out.println("Running " + Main.class.getCanonicalName() + ".main");
//   
//        try {
//            Swarm swarm = new Swarm();
//
//            swarm.fraction(getManagementFraction());
//            swarm.start().deploy();
//        } catch (Exception ex) {
//        }
//    }
// 
//    private static ManagementFraction getManagementFraction() {
//        return ManagementFraction.createDefaultFraction()
//                        .httpInterfaceManagementInterface((iface) -> { 
//                            iface.securityRealm(CommonString.getInstance().getManagementRealm());
//                            iface.consoleEnabled(Boolean.TRUE); 
//                        })  
//                        .securityRealm(CommonString.getInstance().getManagementRealm(), (realm) -> {
//                            realm.inMemoryAuthentication((authn) -> {
//                                authn.add(CommonString.getInstance().getMasterAdminUsrname(),
//                                            CommonString.getInstance().getMasterAdminPassword(), 
//                                            true);
//                            });
//                            realm.inMemoryAuthorization((authz) -> {
//                                authz.add(CommonString.getInstance().getMasterAdminUsrname(),
//                                            CommonString.getInstance().getMasterAdminPassword());
//                            });
//                        });
//    }

}
