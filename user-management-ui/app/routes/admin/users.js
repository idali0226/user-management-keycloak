import Ember from 'ember';
import AuthenticatedRouteMixin from 'ember-simple-auth/mixins/authenticated-route-mixin';

export default Ember.Route.extend(AuthenticatedRouteMixin,{
  
    session: Ember.inject.service(),  

    queryParams:{
        status: {
            refreshModel:true,
            scope: 'controller',
        }
    },
    status: null,
    isList: true,

    model(params) {
        console.log("params : " + params); 
//    return this.store.findAll('user');
        if(params === null) {
        return this.store.findAll('user');
        }
        return  this.store.query('user', { filter: { status: params.status}, reload: true });
    },
 
    activate () { 
        console.log("activate");
        this.controllerFor('users').set('isList', true); 
    },

    beforeModel () {   
        console.log("beforeModel"); 
        this.store.adapterFor('application').set('namespace', "user/api/v01/secure");    
        const isAdmin = this.get('session.user_profile.isAdmin'); 
        if(!isAdmin) {
            this.transitionTo('index');
        }
    },   
});
