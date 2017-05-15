import Ember from 'ember';
import DependentRealtionshipsMixin from 'user-management-ui/mixins/dependent-relationships';
import { module, test } from 'qunit';

module('Unit | Mixin | dependent realtionships');

// Replace this with your real tests.
test('it works', function(assert) {
  let DependentRealtionshipsObject = Ember.Object.extend(DependentRealtionshipsMixin);
  let subject = DependentRealtionshipsObject.create();
  assert.ok(subject);
});
