import Ember from 'ember';

import AuthenticatedRouteMixin from 'ember-simple-auth/mixins/authenticated-route-mixin';

export default Ember.Route.extend(AuthenticatedRouteMixin, {

//	model(params) {
//		return Ember.RSVP.hash({
//			user: this.get('store').findRecord('user', params.id),
//			clients: this.get('store').findAll('client')
//		});
//	},

// 	setupController(controller, models) {
// 	    controller.set('user', models.user);
// 	    controller.set('clients', models.clients);
// 	},

// 	renderTemplate() {
// 		console.log("render template");
//        this.render('users.view');
//    },







 	model(params) { 
 		return this.store.findRecord('user', params.id);
 	},

// 	afterModel: function(){
//        return Ember.RSVP.all([
//            this.store.findAll('client')
//        ]);
//    }
});
