import Ember from 'ember';

export default Ember.Controller.extend({

    statusFilterOptions: [  
        { status: 'Enabled' }, 
        { status: 'Disabled' },
        { status: 'Pending' }
    ],

    queryParams: ['status'],
    status: null,

    isList: true,

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
        },

            viewUserDetail() {
      console.log('viewUserDetail');

      let controller = this.ControllerFor('users');
      controller.set("isList", false);
    },
    }
});
