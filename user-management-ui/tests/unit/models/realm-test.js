import { moduleForModel, test } from 'ember-qunit';

moduleForModel('realm', 'Unit | Model | realm', {
  // Specify the other units that are required for this test.
  needs: [
      'model:client' 
  	]
});

test('it exists', function(assert) {
  let model = this.subject();
  // let store = this.store();
  assert.ok(!!model);
});
