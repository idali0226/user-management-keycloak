import Ember from 'ember';
import AuthenticatedRouteMixin from 'ember-simple-auth/mixins/authenticated-route-mixin';

export default Ember.Route.extend(AuthenticatedRouteMixin, { 
    model(params) {
        console.log("id .. " + params.id);
        return this.store.findRecord('user', params.id);
    },

    actions: {  
        updateUser(user) {
            console.log("update user : " + user.id);

            let controller = this.controller;
 

            if (controller.get('isSaving')) {
                return;
            }   


            user.validate() 
                .then(({ validations }) => {
                    if (validations.get('isValid')) { 
                        user.save()
                            .then((record) => {   
                                console.log("record : " + record);
                                this.set('showSaved', true);   
                            }).finally(()=>{ 
                                
                            });
                    } else {
                        console.log('invalid');  
                    } 
                });     
/**
            user.validate() 
                .then(({ validations }) => {
                    if (validations.get('isValid')) {   
                        controller.set('isSaving', true); 
                     
                        user.save()
                            .then((record) => {   
                                console.log(record.id);
                                this.set('showSaved', true);   
                                this.refresh();   
                            }).finally(()=>{
                                controller.set('isSaving', false); 
                            });
                    } else {
                        console.log('invalid');   
                    } 
            }); 

*/
 
        }
    }
});
