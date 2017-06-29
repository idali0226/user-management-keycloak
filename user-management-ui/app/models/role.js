import DS from 'ember-data';

export default DS.Model.extend({
	role_name: DS.attr('string'), 

	client: DS.belongsTo('client')
});
