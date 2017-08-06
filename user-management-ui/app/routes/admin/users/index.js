import Ember from 'ember';

import AuthenticatedRouteMixin from 'ember-simple-auth/mixins/authenticated-route-mixin';

export default Ember.Route.extend(AuthenticatedRouteMixin, { 
    actions: {
        viewUsrDetail(user) {
            console.log("viewUserDetail: " + user.id);
            this.transitionTo("admin.users.view", user.id);
        }
    }
});