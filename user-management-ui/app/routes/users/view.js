import Ember from 'ember';

import AuthenticatedRouteMixin from 'ember-simple-auth/mixins/authenticated-route-mixin';

export default Ember.Route.extend(AuthenticatedRouteMixin, {

 // 	model(params) { 
 //		return Ember.RSVP.hash({
 //	      	user: this.store.findRecord('user', params.id),
 //	      	clients: this.store.findAll('client')
 //	    }); 
 //	},



 // 	setupController(controller, model) {
 //		controller.set('clients', model.clients);
 //		controller.set('user', model.user); 
 //	}

 	model(params) { 
 		return this.store.findRecord('user', params.id);
  	},

  	actions: {
 
  	}
 
});
