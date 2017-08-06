import Ember from 'ember';
  
import AuthenticatedRouteMixin from 'ember-simple-auth/mixins/authenticated-route-mixin';

export default Ember.Route.extend(AuthenticatedRouteMixin, {
    i18n: Ember.inject.service(),
    notifications: Ember.inject.service('notification-messages'), 

    model () { 
        return this.store.createRecord('user');
    },

    activate () { 
        console.log("activate");
        this.controllerFor('users').set('isList', false); 
        this.controllerFor('users').set('status', null); 
    },
  
    deactivate () { 
        console.log("deactivate");
        let model = this.controllerFor('users.new').get('model'); 
        // TODO: Create a mixin to override `rollbackAttributes` and
        // apply `rollbackAttributes` to any dirty relationship as well.
        model.rollbackAttributes();   
        this.controllerFor('users').set('isList', true); 
    },
 
 
 
    ajax: Ember.inject.service(), 
    sendEmail(user) {
        console.log("sendInvitation: " + user.id);
    
        const ajax = this.get('ajax'); 
        return ajax.request('/secure/sendemail?id=' + user.id + '&isPendingUser=' + false, { 
            method: 'POST' 
        });
    },


    /** Transition to users View route. */
    transitionToUser () {
        console.log('transitionToUser' );
        this.transitionTo('users', {
            queryParams: {
              status, null
            }
        });
       // this.transitionTo('users.view', user);
    },


    actions: {   

        /** Handle form submit and validation. */
        submitForm () { 
            let controller = this.controller;

            console.log('submitForm');
            if (controller.get('isSaving')) {
                return;
            }   
  
         //   var user = this.controller.get('model');   
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
                                console.log(record.id);
                                this.set('showSaved', true);  
                                this.sendEmail(record);
                                this.refresh();  
                                this.transitionToUser();   
                            }).finally(()=>{
                                controller.set('isSaving', false); 
                            });
                    } else {
                        console.log('invalid');   
                    } 
            }); 
        }, 
    }
});
