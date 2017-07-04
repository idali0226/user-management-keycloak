import Ember from 'ember';

export default Ember.Controller.extend({

 	ajax: Ember.inject.service(),

    sendEmail(user) {
        console.log("sendEmail: " + user.id);
    
        const ajax = this.get('ajax'); 
        return ajax.request('/sendemail?id=' + user.id, {
            method: 'POST' 
        });
    },


/**
	actions: {  
 
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
                                this.transitionToRoute('index');
                            }).finally(()=>{
                                controller.set('isSaving', false);
                            });
                    } else {
                        console.log('invalid');  
                } 
            }); 
        }
	} */
});
