import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';

moduleForComponent('user-account-form', 'Integration | Component | user account form', {
  integration: true
});

test('it renders', function(assert) {

  // Set any properties with this.set('myProperty', 'value');
  // Handle any actions with this.on('myAction', function(val) { ... });

  this.render(hbs`{{user-account-form}}`);

  assert.equal(this.$().text().trim(), 'Sign up');


  // Template block usage:
 // this.render(hbs`
 //   {{#user-account-form}} 
 //     Sign up
 //   {{/user-account-form}}
 // `);

 // assert.equal(this.$().text().trim(), null);
});
