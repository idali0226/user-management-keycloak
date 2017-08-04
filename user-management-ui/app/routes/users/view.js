import Ember from 'ember';

import AuthenticatedRouteMixin from 'ember-simple-auth/mixins/authenticated-route-mixin';
import SweetAlertMixin from 'ember-sweetalert/mixins/sweetalert-mixin';

//import sweetAlert from 'ember-sweetalert';


export default Ember.Route.extend(AuthenticatedRouteMixin, SweetAlertMixin,  {

    ajax: Ember.inject.service(), 
    i18n: Ember.inject.service(),
 
 	model(params) { 
        console.log("model");
 		return this.store.findRecord('user', params.id );
  	},

    afterModel: function(){
        console.log("after model");
     //   return this.store.findAll('client');
    },

 //   deactivate () { 
 //       console.log("deactivate");
 //       let model = this.controllerFor('users.view').get('model'); 
        // TODO: Create a mixin to override `rollbackAttributes` and
        // apply `rollbackAttributes` to any dirty relationship as well.
 //       model.rollbackAttributes();   
 //   },
    activate () { 
        console.log("activate");
        this.controllerFor('users').set('isList', false); 
    },

    deactivate () { 
        console.log("deactivate");
        this.controllerFor('users').set('isList', true); 
    },

    transitionToUser () {
        console.log('transitionToUser');
        this.transitionTo('users');
    },
  
    
    sendInvitation(user) {
        console.log("sendInvitation: " + user.id);
    
        const ajax = this.get('ajax'); 
        return ajax.request('/secure/sendemail?id=' + user.id, {
            method: 'POST' 
        });
    },
 
    actions: { 
        userAction(user, action) {
            console.log("userAction : " + user + " " + action);

            const ajax = this.get('ajax');

            ajax.request('/secure/enableDisableUser?id=' + user.id + '&action=' + action, {
                method: 'PUT' 
            }).then((response) => {
                console.log('response: ' + response);
                this.refresh();  
             //   this.transitionTo('users');
            }); 
        },

        rejectUser(user) {
            console.log('rejectUser');
        
        //    let areYouSure = this.get('i18n').t('sweetalert.are-you-sure'); 
        //    let text = this.get('i18n').t('sweetalert.reject-text');
            let sweetAlert = this.get('sweetAlert');
            sweetAlert({ 
                title: "Are you sure?", 
                text: "Are you sure that you want to reject this user?", 
                type: "warning",
                showCancelButton: true,
                confirmButtonText: 'Yes',
                confirmButtonColor: '#04B404', 
                allowOutsideClick: false
            }).then((confirm)=> {
                console.log(confirm);
                user.destroyRecord(); 
                sweetAlert("Rejected!", "User was successfully rejected!", "success");
                this.transitionTo('users');    
            }).catch(e => {
                console.log(e);
            });
        },

        sendVerifyEmail(user) {
            console.log("sendVerifyEmail" + user.id); 

            let sweetAlert = this.get('sweetAlert');
            sweetAlert({
                title: "Are you sure?", 
                text: "Are you sure that you want to send verification email to this user?", 
                type: "warning",
                showCancelButton: true,
                confirmButtonText: 'Yes',
                confirmButtonColor: '#04B404', 
                allowOutsideClick: false
            }).then((confirm)=> {
                console.log(confirm);
                this.sendInvitation(user);
                sweetAlert("Email sent!", "Verification email has been sent to user!", "success");
                this.refresh(); 
            }).catch(e => {
                console.log(e);
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
