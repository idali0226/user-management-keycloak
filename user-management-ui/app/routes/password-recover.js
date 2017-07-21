import Ember from 'ember';
 
export default Ember.Route.extend({
  
    beforeModel () {   
        this.store.adapterFor('application').set('namespace', "user/api/v01"); 
    },
 
 
 	setupController(controller) {
		controller.set('responseMessage', false); 
	} 
});
