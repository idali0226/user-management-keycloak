import { moduleFor, test } from 'ember-qunit';

moduleFor('validator:valid-credentials', 'Unit | Validator | valid-credentials', {
  needs: ['validator:messages']
});

test('it works', function(assert) {
  var validator = this.subject();
  assert.ok(validator);
});
