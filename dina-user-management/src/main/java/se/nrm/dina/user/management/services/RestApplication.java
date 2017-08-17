/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.nrm.dina.user.management.services;

import java.util.HashSet;
import java.util.Set;
import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import org.jboss.resteasy.plugins.interceptors.CorsFilter;

/**
 *
 * @author idali
 */
@ApplicationScoped
@ApplicationPath("/")
public class RestApplication extends Application {

    private Set<Object> singletons = new HashSet<>();
    private HashSet<Class<?>> classes = new HashSet<>();

    public RestApplication() {
        CorsFilter corsFilter = new CorsFilter();
        corsFilter.getAllowedOrigins().add("*");
        corsFilter.setAllowedMethods("OPTIONS, GET, POST, DELETE, PUT, PATCH");
        singletons.add(corsFilter);

        classes.add(UserManagementServices.class);
        classes.add(UserManagementUnsecureService.class);
    }

    @Override
    public Set<Object> getSingletons() {
        return singletons;
    }

    @Override
    public HashSet<Class<?>> getClasses() {
        return classes;
    }

//    @Override
//    public Set<Class<?>> getClasses() {
//        Set<Class<?>> classes = new HashSet<>();
//        classes.add(UserManagementServices.class);
//        classes.add(UserManagementUnsecureService.class);
//        return classes;
//    }
}
