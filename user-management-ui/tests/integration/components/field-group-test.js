import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';

moduleForComponent('field-group', 'Integration | Component | field group', {
  integration: true
});

test('it renders', function(assert) {
  // Set any properties with this.set('myProperty', 'value');
  // Handle any actions with this.on('myAction', function(val) { ... });"

  this.render(hbs`{{field-group title='definitions.name'}}`);

  assert.equal(this.$().text().trim(), 'Name');

  // Template block usage:"
  this.render(hbs`
    {{#field-group title='definitions.name' as |fg|}}
        {{#fg.content}}
            Content
        {{/fg.content}}
        {{#fg.toolbar}}
            Toolbar
        {{/fg.toolbar}}
    {{/field-group}}
  `);

  assert.equal(this.$('.field-group__content').text().trim(), 'Content');
  assert.equal(this.$('.field-group__toolbar').text().trim(), 'Toolbar');
});
