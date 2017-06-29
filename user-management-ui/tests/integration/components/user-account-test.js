import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import Ember from 'ember';

moduleForComponent('user-account', 'Integration | Component | user account', {
  integration: true
});

test('it renders', function(assert) {

  // Set any properties with this.set('myProperty', 'value');
  // Handle any actions with this.on('myAction', function(val) { ... });

  let model = Ember.Object.create({});
  model.set('full_name', 'Ida Li');
  model.set('formatted_date', '2017-06-21');

  this.set('model', model); 
  this.render(hbs`{{user-account account=model}}`); 
  assert.equal(this.$('.panel-title').text().trim(), 'Ida Li [2017-06-21]');
     
});
