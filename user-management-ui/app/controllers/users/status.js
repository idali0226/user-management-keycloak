import Ember from 'ember';

export default Ember.Controller.extend({

    queryParams: ['status'],
    status: null,

    filteredUsers: Ember.computed('status', 'model', function() {
        var status = this.get('status');
        var users = this.get('model');

        if (status) {
          return users.filterBy('status', status);
        } else {
          return users;
        }
    }),

});
