import Ember from 'ember';
import config from './config/environment';

const Router = Ember.Router.extend({
  location: config.locationType,
  rootURL: config.rootURL 
});

Router.map(function() {
  this.route('login');
  this.route('register');
 
  this.route('password-recover');


//  this.route('users', function() {
//    this.route('new');
//    this.route('view', {path: '/:id'}); 
//    this.route('edit', {path: ':id/edit'});
 //   this.route('status', {path: '/status'});
//  });

  this.route('admin', function() {
    this.route('users', function() {
      this.route('new');
      this.route('view', {path: '/:id'});
      this.route('edit', {path: '/:id/edit'});
    });
  });

  this.route('users', function() {
    this.route('profile', {path: '/:id'});
  });

 
  this.route('clients', function() {
    this.route('new');
    this.route('view', {path: ':id'});
  });

  this.route('users.account', {
    path: 'users/account',
  }); 
});

export default Router;
