import Ember from 'ember';
 
export default Ember.Service.extend({

    /** Inject services. */
    session: Ember.inject.service('session'),
 

    /** Return type based on division. */
    type: Ember.computed.alias('configuration.type'),

    /** Return components based on division. */
    components: Ember.computed.alias('configuration.components'),

    /** Return component specific configurations based on division. */
    component: Ember.computed.alias('configuration.component'),

});
