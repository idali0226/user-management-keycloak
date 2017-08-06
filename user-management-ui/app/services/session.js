import Ember from 'ember';

import SessionService from 'ember-simple-auth/services/session';

import config from '../config/environment';

export default SessionService.extend({

    
    tokenObserver: Ember.observer('data.authenticated.access_token', function() {
 
        if (this.get('data.authenticated.access_token')) {
            const url = `${config.AUTHENTICATION_HOST}/userinfo`;
            console.log(url); 
            Ember.$.ajax({
                dataType: 'json',
                url: url,
                success: (response_data) => {
                    this.set('user_profile', {
                        user_id: response_data.sub,
                        name: response_data.given_name + " " + response_data.family_name,
                        full_name: response_data.preferred_username, 
                        email: response_data.email,
                        realm_role: response_data.realm_role, 
                        isAdmin: 'admin' === response_data.realm_role.toString(),
                        isLoggedIn: true
                    });  
                },

                error: (responseData) => {
                    console.log("responseData: " + responseData);
                    // If fetching the user profile errors, sign out to
                    // retrieve a new token.
                    this.invalidate();
                },
                
                headers: {
                    Authorization: `bearer ${this.get('data.authenticated.access_token')}`,
                },  

                
            }); 
        } else {  
          //  console.log("inspect " + Ember.inspect(this ));
            
            this.set('userProfile', undefined);
        } 
    }),
});
