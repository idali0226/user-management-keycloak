import Ember from 'ember';

export default Ember.Route.extend({

	model () {
		this.store.adapterFor('application').set('namespace', "user/api/v01");
        return this.store.createRecord('user');
    },

    beforeModel () {   
    //  let model = this.controllerFor('users.account').get('model'); 
    //  model.set('isNew', true);
    },

    activate: function() {
        console.log('----- activate hook called -----');
    },
 
    deactivate () { 
       let model = this.controllerFor('users.account').get('model'); 
        // TODO: Create a mixin to override `rollbackAttributes` and
        // apply `rollbackAttributes` to any dirty relationship as well.
        model.rollbackAttributes();   
    },
 
    actions: { 
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
                        user.save()
                            .then((record) => {   
                                console.log("record : " + record);
                                this.set('showSaved', true); 
                             //   this.sendInvitation(record);
                             //   this.transitionTo('index');
                                this.controller.sendEmail(record);
                                this.controller.set('responseMessage', true);
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
