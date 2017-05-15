import Ember from 'ember';
 

export default Ember.Controller.extend({

	/** Inject services. */
    i18n: Ember.inject.service(),
    session: Ember.inject.service(), 
    validation: Ember.inject.service(),
    ajax: Ember.inject.service(),
 

    sendInvitation(user) {
        console.log("sendInvitation: " + user.id);
    
        const ajax = this.get('ajax'); 
        return ajax.request('/sendemail?id=' + user.id, {
            method: 'POST' 
        });
    },

    /** Transition to users View route. */
    transitionToUser () {
        this.transitionToRoute('users');
    },

    actions: { 

        /** Handle form submit and validation. */
        submitForm () { 
            let controller = this;

            console.log('submitForm');
            if (controller.get('isSaving')) {
                return;
            }  

            var user = this.get('model');     

            user.validate() 
                .then(({ validations }) => {
                    if (validations.get('isValid')) { 
                        user.save()
                            .then((record) => {   
                                this.set('showSaved', true); 
                                this.sendInvitation(record);
                                this.transitionToUser(); 
                            }).finally(()=>{
                                controller.set('isSaving', false);
                            });
                    } else {
                        console.log('invalid');  
                } 
            }); 
        }
    }
});
