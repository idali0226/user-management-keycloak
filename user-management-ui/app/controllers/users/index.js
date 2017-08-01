import Ember from 'ember';

export default Ember.Controller.extend({

    actions: {
        filterByUsername(param) {
          if (param !== '') {
            return this.get('store').query('user', { email: param });
          } else {
            return this.get('store').findAll('user');
          }
        }
    }
});
