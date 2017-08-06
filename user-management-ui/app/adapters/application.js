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








 
    
    createRecord(store, type, snapshot) {
      let saving = this._super(store, type, snapshot);
      return new RSVP.Promise(
        (resolve, reject) => {
          saving.then(
            (payload) => {
              console.log("payload : " + payload.data.id);

              let id = payload.data.id;
              let existing = store.peekRecord('user', id);
              
              if (existing) {
                console.log("existing : " + existing.id);
                store.unloadRecord(existing); 
                run.next(() => { 
                  resolve(payload); 
                });
              } else {
                console.log("no existing" );
                resolve(payload);
              }
            },
              (error) => {
                reject(error);
              }
            );
        }
      );
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