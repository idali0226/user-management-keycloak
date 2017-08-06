import Ember from 'ember';

export default Ember.Controller.extend({

    statusFilterOptions: [  
        { status: 'Enabled' }, 
        { status: 'Disabled' },
        { status: 'Pending' }
    ],

    queryParams:{
        status: {
            refreshModel:true
        }
    },
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

    actions: {  
        statusChange(filter) {
            console.log("statusChange : "  + filter);
            
            let status = null;
            this.set('selectedFilter', filter); 
 
            if(filter !== null) {
                status = filter.status;  
            } 
  
            this.set('status', status);   

          //  let controller = this.controllerFor('user.status');
          //  controller.set("status", status);
         //   this.send("statusChange");
          //  this.transitionToRoute('users.status');
        },  
    }
});
