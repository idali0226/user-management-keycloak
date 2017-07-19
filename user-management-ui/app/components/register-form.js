import Ember from 'ember';

export default Ember.Component.extend({

    actions: {
        submitForm(user) {
            console.log("user: " + user);
        }
    }
});
