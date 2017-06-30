import DS from 'ember-data';

import DependentRelationships from '../mixins/dependent-relationships';

export default DS.Model.extend(DependentRelationships, {
	client_id: DS.attr('string'),
	client_name: DS.attr('string'),
	descriptions: DS.attr('string'),

	roles: DS.hasMany('role', {async: true}),
});
