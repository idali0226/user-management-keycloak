import Ember from 'ember';
 
import { validator, buildValidations } from 'ember-cp-validations';

const Validations = buildValidations({ 
    email: [
        validator('presence', {
            presence: true,
            descriptionKey: 'definitions.email'
        }),
        validator('format', { type: 'email', debounce: 300}),
        validator('account-exist', { debounce: 300 })
    ]  
});

export default Ember.Controller.extend(Validations, {
 
    validation: Ember.inject.service(), 
    ajax: Ember.inject.service(),
   
    actions: {
   
        send () {
        	let controller = this;

            if (controller.get('isSending')) {
                return;
            }

        	const ajax = this.get('ajax'); 

            this.set('validation.isHidden', true); 
            this.set('isValidating', true); 

            this.validate({}, true).then(({model, validations}) => {  
                if (validations.get('isValid')) {

                    this.set('validation.isHidden', true);
                    controller.set('isSending', true);

                	ajax.request('/recover-password?email=' + this.get('email'), {
               			method: 'PUT' 
           			}).then(function(response) {
               			console.log("response : " + response); 
               			controller.set('responseMessage', true);
          			}).finally(() => {
                        controller.set('isSending', false);
                    }); 
                     
                } else {
                    this.set('validation.isHidden', false);
                    this.set('isValidating', false);
                } 
            }); 
        }
    } 

});