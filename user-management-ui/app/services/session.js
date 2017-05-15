import Ember from 'ember';

import SessionService from 'ember-simple-auth/services/session';

import config from '../config/environment';

export default SessionService.extend({

    
    tokenObserver: Ember.observer('data.authenticated.access_token', function() {

        console.log("access_token: " + 'data.authenticated.access_token');
        if (this.get('data.authenticated.access_token')) {
            const url = `${config.AUTHENTICATION_HOST}/userinfo`;
            console.log(url); 
            Ember.$.ajax({
                dataType: 'json',
                url: url,
                success: (responseData) => {
                    this.set('userProfile', {
                        user_id: responseData.sub,
                        name: responseData.name,
                        fullName: responseData.preferred_username,
                        agentId: responseData.agentId,
                        email: responseData.email,
                        realm_role: responseData.realm_role,
                        isFocusGroupMember: responseData.agentId !== '1016',
                        isAdmin: 'admin' === responseData.realm_role.toString(),
                    });  
                },

                error: (responseData) => {
                    console.log(responseData);
                    // If fetching the user profile errors, sign out to
                    // retrieve a new token.
                    this.invalidate();
                },
                
                headers: {
                    Authorization: `bearer ${this.get('data.authenticated.access_token')}`,
                },  

                
            }); 
        } else {
            this.set('userProfile', undefined);
        } 
    }),
});
