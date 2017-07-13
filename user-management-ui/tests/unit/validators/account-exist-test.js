import { moduleFor, test } from 'ember-qunit';

moduleFor('validator:account-exist', 'Unit | Validator | account-exist', {
  needs: ['validator:messages']
});

test('it works', function(assert) {
  var validator = this.subject();
  assert.ok(validator);
});
