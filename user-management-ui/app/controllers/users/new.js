import Ember from 'ember';
 

export default Ember.Controller.extend({


	/** Inject services. */
 //   i18n: Ember.inject.service(),
 //   session: Ember.inject.service(), 
 //   validation: Ember.inject.service(),
 //   ajax: Ember.inject.service(),

 //   purpose: null,  
 //   purposes: ['admin', 'data entry', 'test'],

/*
    sendInvitation(user) {
        console.log("sendInvitation: " + user.id);
    
        const ajax = this.get('ajax'); 
        return ajax.request('/secure/sendemail?id=' + user.id, {
            method: 'POST' 
        });
    },

    */

    /** Transition to users View route. */
    /*
    transitionToUser () {
        console.log('transitionToUser');
        this.transitionToRoute('users');
    },
    */

/**
    actions: {  
   //     didMakeSelection(value) {
   //         console.log('didMakeSelection : ' + value   ); 
  
   //         if(value !== 'none') {
   //             this.set('model.purpose', value);  
   //         } else {
   //             this.set('model.purpose', null); 
   //         }
   //     },
 
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
                                console.log('save: ' + record.id);

                                this.sendInvitation(record);
                                this.transitionToUser(); 

                                console.log('done');
                            }).finally(()=>{
                                controller.set('isSaving', false);
                            });
                    } else {
                        console.log('invalid');  
                } 
            }); 
        }, 
    }*/
});
