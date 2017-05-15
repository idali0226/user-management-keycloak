import Ember from 'ember';

import AuthenticatedRouteMixin from 'ember-simple-auth/mixins/authenticated-route-mixin';

export default Ember.Route.extend(AuthenticatedRouteMixin, {
 	renderTemplate() {
        this.render('users.view');
    },
    afterModel: function(model){
        return Ember.RSVP.all([
            model.get('cliets'), model.get('client') 
        ]);
    }
});
