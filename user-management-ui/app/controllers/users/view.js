import Ember from 'ember';

export default Ember.Controller.extend({

 	/** Inject services. */
    i18n: Ember.inject.service(),
    session: Ember.inject.service(), 
    validation: Ember.inject.service(),
    ajax: Ember.inject.service(),

	actions: { 
		enableUser(user) { 

			console.log("enableUser: " + user.id);
    
        	const ajax = this.get('ajax');

	        ajax.request('/secure/enableUser?id=' + user.id, {
	            method: 'PUT' 
	        });

			this.transitionToRoute('users');
		}
	}
});
