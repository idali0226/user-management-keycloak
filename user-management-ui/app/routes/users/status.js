import Ember from 'ember';

export default Ember.Route.extend({

    queryParams:{
        status: {
            refreshModel:true
        }
    },
    status: null,

    model(params) {
        console.log("params : " + params); 
     
        return  this.store.query('user', { filter: { status: params.status} });
    },
 
   
 
});
