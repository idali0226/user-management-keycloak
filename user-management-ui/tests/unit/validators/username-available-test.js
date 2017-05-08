import { moduleFor, test } from 'ember-qunit';
  

moduleFor('validator:username-available', 'Unit | Validator | username-available', {
  needs: ['validator:messages']
});

test('it works', function(assert) {
  var validator = this.subject();
  assert.ok(validator);
});
