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
            user.validate({ on: ['first_name', 'last_name', 'purpose' ] }) 
                .then(({  validations }) => {
                    console.log("is valid !" + validations.get('isValid'));
                    if (validations.get('isValid')) { 
                        console.log("valid"); 
                        user.save()
                            .then((record) => {   
                                console.log("record : " + record);
                                this.set('showSaved', true);   
                            }).catch((msg) => {
                                 console.log("error : " + msg.toString());
                                 if(msg.toString() === 'Error: The adapter operation was aborted') { 
                                    this.controller.get('model').rollbackAttributes();
                                 }
                            }).finally((response) => {  
                                console.log("finally response : " + response);
                            });
                    } else {
                        console.log('invalid');  
                        user.set('isEditing', true); 
                        this.controller.get('model').rollbackAttributes();
                      // this.controller.get('model').rollbackAttributes();
                    } 
                });  
            user.set('isEditing', false);
        }, 
    }
});
