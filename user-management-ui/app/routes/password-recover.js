import Ember from 'ember';

export default Ember.Route.extend({

	actions: {
        send(email) { 
            console.log("send " + email); 
   
            this.transitionTo('login'); 
        } 
    }
});
