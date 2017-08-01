import DS from 'ember-data';
import Ember from 'ember';
import moment from 'moment';

import { buildValidations, validator } from 'ember-cp-validations';

const Validations = buildValidations({
	first_name: validator('presence', {
 		presence: true,
		descriptionKey: 'fields.labels.user.first-name'
    }), 
	last_name: validator('presence', {
        presence: true,
        descriptionKey: 'fields.labels.user.last-name'
    }), 
 	purpose: validator('presence', {
        presence: true,
        descriptionKey: 'fields.labels.user.purpose'
    }), 

	email: [
		validator('presence', {
            presence: true,
            descriptionKey: 'fields.labels.user.username-email'
        }),
		validator('format', { type: 'email' }),
	 	validator('username-available', { debounce: 300 })
	],

//	password: [
//	 	validator('presence', true),
//		validator('length', {
//			min: 4
//		})
//	],
//	passwordConfirmation: [
//		validator('presence', true),
//		validator('confirmation', {
//			on: 'password',
//			message: '{description} do not match',
//			description: 'Password'
//		})
//	]
});
 

export default DS.Model.extend(Validations, {

 	first_name: DS.attr('string'),
 	last_name:  DS.attr('string'),
 	email: DS.attr('string'),
 	purpose: DS.attr('string'), 
 	password: DS.attr('string'),
	timestamp_created: DS.attr('number'),
// 	is_enabled: DS.attr('boolean'),
 	is_email_verified: DS.attr('boolean'),
 	status: DS.attr('string'),

 	realms: DS.hasMany('realm', {async: true}),
 
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




 //   is_disabled: Ember.computed.equal('status', 'Disabled'),
 //   is_enabled: Ember.computed.equal('status', 'Enabled'),
 //   is_new_accoun: Ember.computed.equal('status', 'Pending'), 
 //   is_super_admin:  Ember.computed.equal('purpose', 'Super admin'),
 //   is_email_not_verified: Ember.computed.not('is_email_verified'),

 //   is_new_and_email_verified: Ember.computed.and('is_new_accoun', 'is_email_verified'),
 //   show_enable_button: Ember.computed.or('is_disabled', 'is_new_and_email_verified'),
 //   show_disable_button: Ember.computed.equal('status', 'Enabled'),
 //   show_resending_email_button: Ember.computed.and('is_new_accoun', 'is_email_not_verified'),


  //  show_reject_button: Ember.computed.equal('status', 'Pending'),


    view_color: Ember.computed('status', function() {

        let disabled = Ember.computed.equal('status', 'Disabled');
        console.log("color : " + disabled);
        if(this.get('status') === 'Disabled') {
            return 'user-view-disabled';
        } else if (this.get('status') === 'Pending') {
            return 'user-view-new';
        } else {
            return 'user-view';
        }
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
 