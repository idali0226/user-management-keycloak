import Ember from 'ember';


import AuthenticatedRouteMixin from 'ember-simple-auth/mixins/authenticated-route-mixin';

export default Ember.Route.extend(AuthenticatedRouteMixin,{
 

 //	model() {
 //	 	this.store.adapterFor('application').set('namespace', "user/api/v01/secure");
 //		return Ember.RSVP.hash({
 //			users: this.store.findAll('user'),
 //			clients: this.store.findAll('client')
 //		});
 //	}
 
    model() {
     return this.store.findAll('user');
    },



    beforeModel () {   
        this.store.adapterFor('application').set('namespace', "user/api/v01/secure");  
    },


 
});
