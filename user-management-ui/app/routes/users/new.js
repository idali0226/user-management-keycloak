import Ember from 'ember';
  
import AuthenticatedRouteMixin from 'ember-simple-auth/mixins/authenticated-route-mixin';

export default Ember.Route.extend(AuthenticatedRouteMixin, {
    model () {
        return this.store.createRecord('user');
    },

    deactivate () {
        let model = this.controllerFor('user.new').get('model');

        // TODO: Create a mixin to override `rollbackAttributes` and
        // apply `rollbackAttributes` to any dirty relationship as well.
        model.rollbackAttributes();
    }
});
