import DS from 'ember-data';
import Ember from 'ember';
import moment from 'moment';

import { buildValidations, validator } from 'ember-cp-validations';

const Validations = buildValidations({
	first_name: validator('presence', true), 
	last_name: validator('presence', true), 
	purpose: validator('presence', true),

	email: [
		validator('presence', true),
		validator('format', { type: 'email' }),
	 	validator('username-available', { debounce: 300 })
	]  
});
 

export default DS.Model.extend( 
	Validations, {   
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
 		}.property('person'),
  
 		formattedDate: Ember.computed('timestamp_created', function () {
        	return moment(this.get('timestamp_created')).format('Do MMMM YYYY');
   	 	}),

   	 	fullName: function() {
  			return this.get('first_name') + ' ' + this.get('last_name');
		}.property('first_name', 'last_name')
});
