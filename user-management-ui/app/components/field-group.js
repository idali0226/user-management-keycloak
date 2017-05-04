import Ember from 'ember';

export default Ember.Component.extend({

    /** Set default css classes. */
    classNames: ['field-group', 'panel', 'panel-default'],

    /** Bind passed in *identifier* as id of element. */
    attributeBindings: ['_identifier:id'],

    /** Concatinate *identifier*. */
    _identifier: function () {
        return `field-group-${this.get('identifier')}`;
    }.property('identifier'),

    /** Set if the group is collapseable. */
    collapsable: true,

    /** Set if field group is collapsed. */
    collapsed: false,

    /** Set to display count in header*/
    counter: null,

    actions: {

        /** Toggle if group is collapsed. */
        toggleCollapse() {
            if (this.get('collapsable')) {
                this.toggleProperty('collapsed');
            }
        }
    }
});
