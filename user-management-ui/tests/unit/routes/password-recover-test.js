import { moduleFor, test } from 'ember-qunit';

moduleFor('route:password-recover', 'Unit | Route | password recover', {
  // Specify the other units that are required for this test.
  // needs: ['controller:foo']

  	needs: ['validator:presence',
    		'validator:format',
    		'validator:username-available', 
    ]
});

test('it exists', function(assert) {
  let route = this.subject();
  assert.ok(route);
});
