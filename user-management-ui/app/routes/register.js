import Ember from 'ember';
  
export default Ember.Route.extend({
     
    i18n: Ember.inject.service(),
    notifications: Ember.inject.service('notification-messages'),
    ajax: Ember.inject.service(), 

    model () {
        this.store.adapterFor('application').set('namespace', "user/api/v01"); 
        return this.store.createRecord('user');
    },
 
    deactivate () { 
        console.log("deactivate");
        this.controllerFor('register').set('responseMessage', false);
        let model = this.controllerFor('register').get('model');  
        model.rollbackAttributes();   
    },
 

    beforeModel () {   
      //  model.set('required', false)
    },
 
    
    sendEmail(user) {
        console.log("sendEmail: " + user.id);
    
        const ajax = this.get('ajax'); 
        return ajax.request('/sendemail?id=' + user.id, {
            method: 'POST' 
        });
    },
    
    actions: { 
        submitForm () {  
            let controller = this.controller;

            console.log('submitForm');
            if (controller.get('isSaving')) {
                console.log("now saving.....");
                return;
            }   
  
            var user = controller.get('model');     
            user.validate() 
                .then(({ validations }) => {
                    if (validations.get('isValid')) { 
                        controller.set('isSaving', true);   
                        this.get('notifications').warning(this.get('i18n').t('messages.saving-account-inprocess'), {
                          autoClear: true
                        }); 
                        user.save()
                            .then((record) => {   
                                console.log("record : " + record);
                                controller.set('showSaved', true); 
                             //   this.sendInvitation(record);
                             // this.transitionTo('index');
                                this.sendEmail(record); 
                                controller.set('responseMessage', true); 
                            }).finally(()=>{
                                controller.set('isSaving', false);
                            });
                    } else {
                        console.log('invalid');  
                    } 
                }); 
        },

     //   willTransition() {
          // rollbackAttributes() removes the record from the store
          // if the model 'isNew'
      //    this.controller.get('model').rollbackAttributes();
     //   }
    }
});
