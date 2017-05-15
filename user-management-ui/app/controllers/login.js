import Ember from 'ember';

import { validator, buildValidations } from 'ember-cp-validations';

const Validations = buildValidations({ 
    username: [
        validator('presence', {
            presence: true,
            descriptionKey: 'definitions.username'
        }),
        validator('format', { type: 'email'})
    ],
    password: validator('presence', {
        presence: true,
        descriptionKey: 'definitions.password'
    }),
    invalidCredentials: validator('valid-credentials', {
        presence: false,
        descriptionKey: 'definitions.usernameOrPassword'
    }),
});

      
export default Ember.Controller.extend(Validations, {

    session: Ember.inject.service('session'),
    validation: Ember.inject.service(), 
 

    actions: {

        /** Authenticate and sign in with currently selected agent. */
        authenticate () {
            this.set('validation.isHidden', true);
            this.set('invalidCredentials', null);
            this.set('isValidating', true);

            this.validate({}, true).then(({model, validations}) => {
                if (validations.get('isValid')) {
                    this.get('session').authenticate(
                        'authenticator:oauth',
                        this.get('username'), this.get('password')
                    ).catch(() => {
                        this.set('invalidCredentials', true);
                        this.set('validation.isHidden', false);
                        this.set('isValidating', false);
                    });   
                } else {
                    this.set('validation.isHidden', false);
                    this.set('isValidating', false);
                } 
            }); 
        }
    }
});