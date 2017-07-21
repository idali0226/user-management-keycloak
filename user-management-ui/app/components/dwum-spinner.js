import Ember from 'ember';

export default Ember.Component.extend({
    classNames: ['dwum-spinner'],
    classNameBindings: ['button:button'],

    /** Text to display together with spinner. */
    loadingText: '',
});
