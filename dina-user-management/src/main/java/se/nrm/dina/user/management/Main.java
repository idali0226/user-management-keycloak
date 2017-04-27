/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.nrm.dina.user.management;
  
import org.wildfly.swarm.Swarm; 
import org.wildfly.swarm.management.ManagementFraction;  

/**
 *
 * @author idali
 */
public class Main {
    
    public static void main(String... args) throws Exception {
       System.out.println("Running " + Main.class.getCanonicalName() + ".main");

        Swarm swarm = new Swarm();
        swarm.fraction(getManagementFraction());
        swarm.start().deploy(); 
//        swarm.fraction(UndertowFraction.createDefaultFraction().)    

 
 

//        JAXRSArchive deployment = ShrinkWrap.create(JAXRSArchive.class);
//        deployment.as(Secured.class);
//    
//         
//        swarm.start().deploy(deployment); 
    }
   
    private static ManagementFraction getManagementFraction() { 
        return ManagementFraction.createDefaultFraction()
                        .httpInterfaceManagementInterface((iface) -> {
                            iface.allowedOrigin("http://localhost:8181");
                            iface.securityRealm("ManagementRealm");
                        })
                        .securityRealm("ManagementRealm", (realm) -> {
                            realm.inMemoryAuthentication((authn) -> {
                                authn.add("admin", "dina", true);
                            });
                            realm.inMemoryAuthorization((authz) -> {
                                authz.add("admin", "dina");
                            });
                        });
    }

}
