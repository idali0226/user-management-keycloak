import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import Ember from 'ember';

moduleForComponent('client-item', 'Integration | Component | client item', {
  integration: true
});

test('it renders', function(assert) {

  // Set any properties with this.set('myProperty', 'value');
  // Handle any actions with this.on('myAction', function(val) { ... });

 
  let model = Ember.Object.create({});

  model.set('client_id', 'collection');
  model.set('client_name', 'collection');

  this.set('model', model); 

  this.render(hbs`{{client-item item=model}}`); 
  assert.equal(this.$('.panel-title').text().trim(), 'collection [collection]');
 
});
