import { moduleForModel, test } from 'ember-qunit';

moduleForModel('client', 'Unit | Model | client', {
  // Specify the other units that are required for this test.
    needs: [
      'model:role' 
  	]
});

test('it exists', function(assert) {
  let model = this.subject();
  // let store = this.store();
  assert.ok(!!model);
});
