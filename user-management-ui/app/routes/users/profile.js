import Ember from 'ember';

export default Ember.Route.extend({

	model(params) {
		console.log("id .. " + params.user_id);
		return this.store.findRecord('user', params.user_id);
	},
 
 	actions: {
		editUser(user) {
			console.log("user: " + user);
			user.set('isEditing', true);
	 	},

	 	updateUser(user) {
	 		console.log("user: " + user);
	 		user.set('isEditing', false);
	 	},

        changePassword(user) {  
            user.set('changePassword', true);
        },

        cancel(user) {
        	user.set('changePassword', false);
        },

        updatePassword(user) {
        	user.set('changePassword', false);
        	
        }
 	}

});
