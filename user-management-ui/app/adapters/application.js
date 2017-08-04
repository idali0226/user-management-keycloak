import Ember from 'ember';
import DS from 'ember-data';
import config from '../config/environment';

import DataAdapterMixin from 'ember-simple-auth/mixins/data-adapter-mixin';

const {RSVP, run} = Ember;

export default DS.JSONAPIAdapter.extend(DataAdapterMixin, {
    host: config.HOST,
 
    namespace: 'user/api/v01/secure', 

 //   namespace: 'user/api/v01', 
    
    headers: {
        "Accept": "application/json",
        "Content-type": "application/json"
    },

    authorizer: 'authorizer:oauth',


    
    // defaultSerializer: '-default',
    /**
     * Override method to be able to append eg. "search"
     * to the url.
     */
 



    urlForQuery (query, modelName) {
        var url = this._super(query, modelName);
        console.log(url);

        console.log("model name : " + modelName);
        console.log("query : " + query);

        if (query) {
            url = url + '/search';
            delete query.search; 
        }
        console.log("url for query : " + url);
        return url;
    },








 
    











  //  urlForCreateRecord(modelName/*, snapshot*/) {
  //      switch(modelName) {
  //        case 'user':
  //        case 'users':
  //          return this._super.apply(this, arguments).replace('users', 'register');
  //        default:
   //         return this._super(...arguments);
  //      }
 // } 
});