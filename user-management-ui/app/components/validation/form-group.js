import Ember from 'ember';

export default Ember.Component.extend({
    classNames: ['form-group', 'form-group-sm'],
    classNameBindings: ['hasError:has-error', 'hasSuccess:has-success'],
    
    /** Inject validation service. */
    validation: Ember.inject.service(),
    
    /** Set to only display errors. */
    onlyError: true,
    
    /** Set to enable instant validation regardless if isHidden. */
    instant: false,
    
    /** Inverse of instant. */
    notInstant: Ember.computed.not('instant'),
    
    /** Should the validation be hidden. */
    isHidden: Ember.computed.and('validation.isHidden', 'notInstant'),
    
    /** Return if validation has errors. */
    hasError: Ember.computed('isValid', 'isHidden', function () {
        return this.get('isValid') === false && !this.get('isHidden');
    }),

    /** Return if validation is successful. */
    hasSuccess: Ember.computed('hasError', 'onlyError', function () {
        return !this.get('onlyError') && this.get('isValid');
    }),
    
    /** Return if help block text shold be displayed. */
    displayHelpBlock: Ember.computed('isValid', 'hideOnValid', 'message', 'isHidden', function () {
        return !this.get('isValid') && this.get('message') && !this.get('isHidden');
    })
});
