import Ember from 'ember';
import AjaxService from 'ember-ajax/services/ajax';
import config from '../config/environment';

export default AjaxService.extend({
	host: config.HOST,
	namespace: 'user/api/v01',

  session: Ember.inject.service(),

  headers: Ember.computed('data.authenticated.access_token', {
    get() {
      let headers = {};
      const authToken = this.get('session.data.authenticated.access_token');
      console.log("authToken: " + authToken);
      if (authToken) { 
        headers['Authorization'] = `bearer ${authToken}`;
      }
      return headers;
    }
  })
});