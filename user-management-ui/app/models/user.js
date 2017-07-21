import DS from 'ember-data';
import Ember from 'ember';
import moment from 'moment';

import { buildValidations, validator } from 'ember-cp-validations';

const Validations = buildValidations({
	first_name: validator('presence', {
 		presence: true,
		description: 'Username'}), 
	last_name: validator('presence', true), 
 	purpose: validator('presence', true), 

	email: [
		validator('presence', true),
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
 	is_enabled: DS.attr('boolean'),
 	is_email_verified: DS.attr('boolean'),
 	status: DS.attr('string'),

 	realms: DS.hasMany('realm', {async: true}),

 	new_account: function() { 
 	 	return this.get('status') === 'new';
  	}.property('status'),

  
 	formatted_date: Ember.computed('timestamp_created', function () {
     	return moment(this.get('timestamp_created')).format('Do MMMM YYYY');
 	}),


 	full_name: Ember.computed('first_name', 'last_name', function() {
	 	return `${this.get('first_name')} ${this.get('last_name')}`;
 	}),

    show_disable_button: Ember.computed('status', 'email', function() {
        return this.get('status') !== 'disabled'  & this.get('email') !== 'admin@nrm.se';
    }),
  
 	show_enable_button: Ember.computed('status', function() { 
 	 	return this.get('status') === 'disabled' | this.get('status') === 'new';
  	}),

    is_super_admin: function() {
        return  this.get('email') === 'admin@nrm.se';
    }.property('email')

});
 