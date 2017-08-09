import DS from 'ember-data';

export default DS.Model.extend({
	role_name: DS.attr('string'), 
    description: DS.attr('string'),
    is_client: DS.attr('boolean'),
    role_belong_to: DS.attr('String'),  
});
