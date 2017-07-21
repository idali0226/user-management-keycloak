import Ember from 'ember';
  
import AuthenticatedRouteMixin from 'ember-simple-auth/mixins/authenticated-route-mixin';

export default Ember.Route.extend(AuthenticatedRouteMixin, {

    model () {
        return this.store.createRecord('user');
    },
 
    deactivate () { 
        console.log("deactivate");
        let model = this.controllerFor('users.new').get('model'); 
        // TODO: Create a mixin to override `rollbackAttributes` and
        // apply `rollbackAttributes` to any dirty relationship as well.
        model.rollbackAttributes();   
    },
 
 
    ajax: Ember.inject.service(), 
    sendInvitation(user) {
        console.log("sendInvitation: " + user.id);
    
        const ajax = this.get('ajax'); 
        return ajax.request('/secure/sendemail?id=' + user.id, {
            method: 'POST' 
        });
    },


    /** Transition to users View route. */
    transitionToUser () {
        console.log('transitionToUser');
        this.transitionTo('users');
    },

    actions: {   

        /** Handle form submit and validation. */
        submitForm () { 
            let controller = this;

            console.log('submitForm');
            if (controller.get('isSaving')) {
                return;
            }   
 

            var user = this.controller.get('model');   
            user.validate() 
                .then(({ validations }) => {
                    if (validations.get('isValid')) {  
                        controller.set('isSaving', true);

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
    }
});
