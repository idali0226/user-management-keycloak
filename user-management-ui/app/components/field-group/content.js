import Ember from 'ember';

export default Ember.Component.extend({
    classNames: ['panel-body', 'field-group__content'],
    classNameBindings: ['collapsed:hidden']
});
