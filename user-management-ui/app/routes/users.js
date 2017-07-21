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


    ajax: Ember.inject.service(),

    actions: {
        disableUser(user) { 
            console.log("disableUser: " + user.id); 

            const ajax = this.get('ajax'); 
            ajax.request('/secure/disableUser?id=' + user.id, {
                method: 'PUT' 
            }).then((response) => {
                console.log('response: ' + response);
                this.refresh(); 
            });
            
            // this.transitionTo('users');
        } 
    }
});
