import Ember from 'ember';

import ApplicationRouteMixin from 'ember-simple-auth/mixins/application-route-mixin';

export default Ember.Route.extend(ApplicationRouteMixin, {

    /** Inject services. */
    session: Ember.inject.service(), 
 

    /** Override before model and setup localization. */
    beforeModel () {
        const result = this._super(...arguments); 
 
        return result;
    }
});