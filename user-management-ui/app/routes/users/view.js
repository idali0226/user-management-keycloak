import Ember from 'ember';

import AuthenticatedRouteMixin from 'ember-simple-auth/mixins/authenticated-route-mixin';

export default Ember.Route.extend(AuthenticatedRouteMixin, {
 
 	model(params) { 
 		return this.store.findRecord('user', params.id );
  	},

    deactivate () { 
        console.log("deactivate");
        let model = this.controllerFor('users.view').get('model'); 
        // TODO: Create a mixin to override `rollbackAttributes` and
        // apply `rollbackAttributes` to any dirty relationship as well.
        model.rollbackAttributes();   
    },
  
 
    ajax: Ember.inject.service(), 
    actions: { 
        enableUser(user) { 

            console.log("enableUser: " + user.id);
    
            const ajax = this.get('ajax');

            ajax.request('/secure/enableUser?id=' + user.id, {
                method: 'PUT' 
            }).then((response) => {
                console.log('response: ' + response);
                this.refresh(); 
             //   this.transitionTo('users');
            }); 
        },

        disableUser(user) { 
            console.log("disableUser: " + user.id); 

            const ajax = this.get('ajax'); 
            ajax.request('/secure/disableUser?id=' + user.id, {
                method: 'PUT' 
            }).then((response) => {
                console.log('response: ' + response);
                this.refresh(); 
            });
        }, 

        edit(user) {
            console.log("edit: " + user.id); 
            user.set('isEditing', true);
        },

        cancelEdit(user) {
            user.set('isEditing', false);
        },

        updateUser(user) {  
            console.log("update user : " + user.id);

            user.save().then(() => {    
                this.set('showSaved', true);     
            }).catch((msg) => { 
                console.log("error : " + msg);
            }).finally(()=>{  
                user.set('isEditing', false);     
            });
        },
 
 /**
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
            user.set('isEditing', false);
        }, */
    } 
});
