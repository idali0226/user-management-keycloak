import Ember from 'ember';

import ApplicationRouteMixin from 'ember-simple-auth/mixins/application-route-mixin';

export default Ember.Route.extend(ApplicationRouteMixin, {

    /** Inject services. */
    session: Ember.inject.service(),  
    i18n: Ember.inject.service(),
    moment: Ember.inject.service(),
    ajax: Ember.inject.service(),

    // Don't know if this will resolve refresh token.
     _networkBecomesAvailable() {
        if (this.get('session.isAuthenticated')) {
          let authenticator = this.get('session.authenticator');
         authenticator._refreshAccessToken(); 
        }
      },
  
    /** Override before model and setup localization. */
    beforeModel () {
        const result = this._super(...arguments); 

      	const language = this.get('session.data.locale');

    //    this.store.adapterFor('application').set('namespace', "user/api/v01/secure");

        if (language) {
            this.set('i18n.locale', language);
        }

        this.get('moment').changeLocale(
            this.get('i18n.locale')
        );
 
        return result;
    },

    actions: { 


        signout (id) { 
            console.log("signout " + id); 

            let session = this.get('session');

            const ajax = this.get('ajax');   
            ajax.request('/secure/logout?id=' + id, {
                method: 'PUT' 
            }).then(function(response) {
                console.log("response : " + response);
                session.invalidate();
            }).finally(()=>{  
                 session.invalidate();
            });

          //  this.get('session').invalidate();
            this.transitionTo('index'); 
        } 
    }
});