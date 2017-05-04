import Ember from 'ember';

export default Ember.Component.extend({
    classNames: ['panel-footer', 'field-group__toolbar'],
    classNameBindings: ['collapsed:hidden']
});
