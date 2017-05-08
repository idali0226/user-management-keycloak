import { moduleFor, test } from 'ember-qunit';
import Ember from 'ember';
import DS from 'ember-data';


moduleFor('serializer:application', 'Unit | Serializer | application', {
  // Specify the other units that are required for this test.
});

test('it serializes records in JSON Api format', function(assert) {

  // create a dummy model for application
  let DummyModel = DS.Model.extend({
    first_name: DS.attr('string'),
    last_name: DS.attr('string')
  });
  this.registry.register('model:application', DummyModel);

  let store = Ember.getOwner(this).lookup('service:store');

  let basicModel = {
    first_name: 'John',
    last_name: 'Doe'
  };

  let expectedHash = {
    data: {
      attributes: {
        first_name: basicModel.first_name,
        last_name: basicModel.last_name
      },
      type: 'applications'
    }
  };

  Ember.run(function(){

    // Create an instance of DummyModel and serialize
    let serializedRecord = store.createRecord('application', basicModel).serialize();
    assert.deepEqual(serializedRecord, expectedHash);

  });

});









