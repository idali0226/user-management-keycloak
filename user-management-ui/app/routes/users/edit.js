import Ember from 'ember';
import AuthenticatedRouteMixin from 'ember-simple-auth/mixins/authenticated-route-mixin';

export default Ember.Route.extend(AuthenticatedRouteMixin, {


	model(params) {
		console.log("id .. " + params.id);
		return this.store.findRecord('user', params.id);
	},

	actions: {
 
		updateUser(user) {
			console.log("update user : " + user.id);

			let controller = this;

			user.validate() 
                .then(({ validations }) => {
                    if (validations.get('isValid')) { 
                        user.save()
                            .then((record) => {   
                                console.log("record : " + record);
                                this.set('showSaved', true); 
                             //   this.sendInvitation(record);
                             //   this.transitionTo('index');
                             //   this.controller.sendEmail(record);
                                this.controller.set('responseMessage', true);
                            }).finally(()=>{
                                controller.set('isSaving', false);
                            });
                    } else {
                        console.log('invalid');  
                    } 
                });   
		}
	}
});
