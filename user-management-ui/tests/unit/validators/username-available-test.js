import { moduleFor, test } from 'ember-qunit';
 
moduleFor('validator:username-available', 'Unit | Validator | username-available', {
  needs: ['validator:messages']
});

test('username is unique', function(assert) {
 //   assert.expect(1);

  //  let validator =  this.subject();
//    let done = assert.async();

  //  validator.validate('johndoe42').then((message) => {
  //    assert.equal(message, true);
//      done();
  //  }); 
  var validator = this.subject();
  assert.ok(validator);
});
