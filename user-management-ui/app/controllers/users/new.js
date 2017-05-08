import Ember from 'ember';

export default Ember.Controller.extend({

	i18n: Ember.inject.service(),
    session: Ember.inject.service(), 
    validation: Ember.inject.service(),


    actions: {

        /** Handle navigation item click. */
        navigationClick(fieldGroupId) {
            Ember.$('html, body').animate({
                scrollTop: Ember.$('#field-group-' + fieldGroupId).offset().top - 50
            }, 300);
        },

   		/** Transition to Collection object View route. */
    	transitionToUsers(user) {
        	this.transitionToRoute(
	            'users.view', user
	        );
	    },

        /** Handle form submit and validation. */
        submitForm () {
            let controller = this;

            if (controller.get('isSaving')) {
                return;
            }

            this.model.validate({}, true).then(({model, validations}) => {
                this.set('validation.isHidden', false);

                if (validations.get('isValid')) {

                    this.set('validation.isHidden', true);
                    controller.set('isSaving', true);

                    this.model.save().then((record) => { 
                        controller.transitionToCollectionObject(record);
                    }).finally(()=>{
                        controller.set('isSaving', false);
                    });

                } else {
                    Ember.run.next(this, controller.scrollToValidation);
                }
            });
        }
 
    }
});
