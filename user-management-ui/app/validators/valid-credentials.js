import PresenceValidator from 'ember-cp-validations/validators/presence';

export default PresenceValidator.extend({
  validate(value, options) {
    let response = this._super(...arguments); 

    // Validation message returned.
    if (response !== true) {
        let errorMsg = this.createErrorMessage('invalid', value, options); 
        return errorMsg;
    }

    return response;
  }
});
