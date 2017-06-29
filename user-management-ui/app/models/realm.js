import DS from 'ember-data';

export default DS.Model.extend({
	realm: DS.attr('string'),
	realm_description: DS.attr('string'),

	clients: DS.hasMany('client', {async: true}),
	roles: DS.hasMany('role', {async: true})
});
