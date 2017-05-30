import Ember from 'ember';
import config from './config/environment';

const Router = Ember.Router.extend({
  location: config.locationType,
  rootURL: config.rootURL
});

Router.map(function() {
  this.route('login');
  this.route('users', function() {
    this.route('new');
    this.route('view');
  });
  this.route('clients', function() {
    this.route('new');
    this.route('view');
  });
  this.route('users.view', {
    path: 'users/:user_id',
  });
  this.route('users.new', {
    path: 'users/new',
  });

});

export default Router;
