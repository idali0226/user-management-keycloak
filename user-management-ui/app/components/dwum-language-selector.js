import Ember from 'ember';
 

export default Ember.Component.extend({

    /** Inject services. */
    i18n: Ember.inject.service(),
    session: Ember.inject.service(), 
    moment: Ember.inject.service(),

    tagName: 'ul',

    classNames: ['dwum-component-language-selector'],

    actions: {

        /** Change application *language*. */
        changeLanguage: function (language) {
            this.set('i18n.locale', language);
            this.get('session').set('data.locale', language);

            this.get('moment').changeLocale(
                this.get('i18n.locale')
            );
        }
    }
});
