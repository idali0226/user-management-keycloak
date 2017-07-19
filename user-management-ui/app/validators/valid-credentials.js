import PresenceValidator from 'ember-cp-validations/validators/presence';

export default PresenceValidator.extend({
  validate(value, options) {
    let response = this._super(...arguments);

    console.log("test 1: " + value + " " + options);

    // Validation message returned.
    if (response !== true) {
        let errorMsg = this.createErrorMessage('invalid', value, options);

        console.log("test 2: " + errorMsg);
        return errorMsg;
    }

    return response;
  }
});
