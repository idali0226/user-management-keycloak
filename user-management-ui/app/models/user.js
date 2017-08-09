import DS from 'ember-data';
import Ember from 'ember';
import moment from 'moment';

import { buildValidations, validator } from 'ember-cp-validations';

const { attr } = DS;

const Validations = buildValidations({
	first_name: [
        validator('presence', {
     		presence: true, 
    		descriptionKey: 'fields.labels.user.first-name'
        }), 
        validator('length', {
            min: 2,
            descriptionKey: 'fields.labels.user.first-name'
        }),
    ],
	last_name: [
        validator('presence', {
            presence: true,
            descriptionKey: 'fields.labels.user.last-name'
        }), 
        validator('length', {
            min: 2,
            descriptionKey: 'fields.labels.user.last-name'
        }),
    ],
 	purpose: [
        validator('presence', { 
            presence: true,
            descriptionKey: 'fields.labels.user.purpose'
        }), validator('length', {
            min: 2,
            descriptionKey: 'fields.labels.user.purpose'
        }),
    ],
	email: [
        validator('presence', { 
            presence: true,
            descriptionKey: 'fields.labels.user.username-email'
        }),
		validator('format', { type: 'email' }),
 	    validator('username-available', {  
            disabled: Ember.computed.not('model.validationRequired'),
            debounce: 300 
        })
	],

  	password: [ 
        validator('format', { 
            disabled: Ember.computed.not('model.validationRequired'),
            regex: /^(?=.*?[#?!@$%^&*-]).{8,}$/,
            message: 'Password must has minimum 8 charaters and include at least one Special Characters',
  
        }), 
 	],

    passwordConfirmation: validator('confirmation', {
        on: 'password',
        message: 'Password do not match'
    }), 
});
 

export default DS.Model.extend(Validations, {

    username: attr('string'),
 	first_name: attr('string'),
 	last_name: attr('string'),
 	email: attr('string'),
 	purpose: attr('string'), 
 	password: attr('string'),
	timestamp_created: attr('number'),
// 	is_enabled: DS.attr('boolean'),
 	is_email_verified: DS.attr('boolean'),
 	status: DS.attr('string'),

    roles: DS.hasMany('role', {async: true}),
 
 	formatted_date: Ember.computed('timestamp_created', function () {
     	return moment(this.get('timestamp_created')).format('Do MMMM YYYY');
 	}),
 
 	full_name: Ember.computed('first_name', 'last_name', function() {
	 	return `${this.get('first_name')} ${this.get('last_name')}`;
 	}), 
    

    is_disabled_user: Ember.computed.equal('status', 'Disabled'),
    is_enabled_user: Ember.computed.equal('status', 'Enabled'),
    is_pending_user: Ember.computed.equal('status', 'Pending'), 
    is_super_admin:  Ember.computed.equal('purpose', 'Super admin'),

    realmRole: Ember.computed.filterBy('roles', 'is_client', false),
    clientRole: Ember.computed.filterBy('roles', 'is_client', true),
  
    view_color: Ember.computed('status', function() {

     //   let disabled = Ember.computed.equal('status', 'Disabled'); 
        if(this.get('status') === 'Disabled') {
            return 'user-view-disabled';
        } else if (this.get('status') === 'Pending') {
            return 'user-view-new';
        } else {
            return 'user-view';
        }
    }),

    validationRequired: Ember.computed('username', function() {  
        console.log("usrname ? " + this.get('username'))
        return this.get('username') ;
    }),

//    show_disable_button: Ember.computed('status', 'email', function() { 
//        return this.get('status') !== 'disabled'  & this.get('email') !== 'admin@nrm.se';
//    }),
  
 // show_enable_button: Ember.computed('status', function() { 
 //     return this.get('status') === 'disabled' | this.get('status') === 'new';
 //     }),



//    new_account: function() { 
//        return this.get('status') === 'New' || this.get('status') === 'Pending';
//    }.property('status'),

//    is_super_admin: function() {
//        return  this.get('purpose') === 'Super admin';
//    }.property('purpose')

});
 