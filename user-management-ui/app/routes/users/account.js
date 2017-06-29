import Ember from 'ember';

export default Ember.Route.extend({

	model () {
		this.store.adapterFor('application').set('namespace', "user/api/v01");
        return this.store.createRecord('user');
    },
 
    deactivate () { 
       let model = this.controllerFor('users.account').get('model'); 
        // TODO: Create a mixin to override `rollbackAttributes` and
        // apply `rollbackAttributes` to any dirty relationship as well.
        model.rollbackAttributes();   
    }
});
