import Ember from 'ember';

import ApplicationRouteMixin from 'ember-simple-auth/mixins/application-route-mixin';

export default Ember.Route.extend(ApplicationRouteMixin, {

    /** Inject services. */
    session: Ember.inject.service(),  
    i18n: Ember.inject.service(),
    moment: Ember.inject.service(),
  
    /** Override before model and setup localization. */
    beforeModel () {
        const result = this._super(...arguments); 

      	const language = this.get('session.data.locale');

        this.store.adapterFor('application').set('namespace', "user/api/v01/secure");

        if (language) {
            this.set('i18n.locale', language);
        }

        this.get('moment').changeLocale(
            this.get('i18n.locale')
        );
 
        return result;
    }
});