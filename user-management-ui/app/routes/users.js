import Ember from 'ember';


import AuthenticatedRouteMixin from 'ember-simple-auth/mixins/authenticated-route-mixin';

export default Ember.Route.extend(AuthenticatedRouteMixin,{
  
  queryParams:{
    status: {
      refreshModel:true
    }
  },
  status: null,

  model(params) {
    console.log("params : " + params); 
    return  this.store.query('user', { filter: { status: params.status} });
  },
 
  beforeModel () {   
    console.log("beforeModel");
    this.store.adapterFor('application').set('namespace', "user/api/v01/secure");   
  }, 
 


 //   actions: { 
 //      changeStatus(status) {
  //          console.log("changeStatus: " + status);
 //           this.refresh();
 //         this.transitionTo('users.status', {
  //              queryParams: {
 //                 status: status  
 //               }
 //          });
 //        this.transitionTo('users.status', {status: status });     
 //       }
 //   }  
});
