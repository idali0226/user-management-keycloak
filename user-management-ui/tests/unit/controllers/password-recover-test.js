import { moduleFor, test } from 'ember-qunit';

moduleFor('controller:password-recover', 'Unit | Controller | password recover', {
  // Specify the other units that are required for this test.
  // needs: ['controller:foo']

 	needs: ['validator:presence',  
 			'validator:format',
    		'validator:account-exist',
  	] 
});

// Replace this with your real tests.
test('it exists', function(assert) {
  let controller = this.subject();
  assert.ok(controller);
});
