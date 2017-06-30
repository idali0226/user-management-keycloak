import Ember from 'ember';

export default Ember.Controller.extend({

  	session: Ember.inject.service(),

    actions: {

        /** Invalidate current session and logout user. */
        invalidateSession () {
            this.get('session').invalidate();
        }
    }
});
