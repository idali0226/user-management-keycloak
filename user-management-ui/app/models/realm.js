import DS from 'ember-data';

export default DS.Model.extend({
	realm_name: DS.attr('string'),
	description: DS.attr('string'),

    users: DS.hasMany('user', {async: true}),
	clients: DS.hasMany('client', {async: true}),
	roles: DS.hasMany('role', {async: true}),

});
