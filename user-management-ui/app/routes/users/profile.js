import Ember from 'ember';
import AuthenticatedRouteMixin from 'ember-simple-auth/mixins/authenticated-route-mixin';

export default Ember.Route.extend(AuthenticatedRouteMixin, {

	model(params) { 
		return this.store.findRecord('user', params.user_id);
	},

 	beforeModel () {   
        this.store.adapterFor('application').set('namespace', "user/api/v01/secure");  
    },
 
 	actions: {
		editUser(user) {
			console.log("editUser: " + user);
			user.set('isEditing', true);
	 	},

	 	updateUser(user) {  
	 		console.log("update user : " + user.id);

             user.save().then((record) => {   
                console.log("record : " + record);
                this.refresh(); 
                this.set('showSaved', true);   
            }).catch((msg) => { 
                console.log("error : " + msg);
            }).finally(()=>{ 
                                 
            });
 
 /**
			user.validate() 
                .then(({ validations }) => {
                    if (validations.get('isValid')) { 
                        user.save()
                            .then((record) => {   
                                console.log("record : " + record);
                                this.set('showSaved', true);   
                            }).finally(()=>{ 
                                
                            });
                    } else {
                        console.log('invalid');  
                	} 
            	});    */
	 		user.set('isEditing', false);
	 	},

        changePassword(user) {  
        	console.log("changePassword: " + user);
            user.set('changePassword', true);
        },

        cancel(user) {
        	console.log("cancel: " + user);
        	user.set('changePassword', false);
        },

        updatePassword(user) {
        	console.log("uppdatePassword: " + user);
        	user.set('changePassword', false);
        	
        }
 	}

});
