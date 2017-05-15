import Ember from 'ember';

/**
 * Mixin that overrides the save method on a model.
 *
 * When save is called all hasMany relationships are marked
 * for deletion and removed when promise is resolved.
 *
 * This is done due to issue: https://github.com/emberjs/data/issues/1829
*/
export default Ember.Mixin.create({
    save: function() {
        var recordsToDelete = [];
        this.eachRelationship((name, descriptor) => {
            if (descriptor.kind === 'hasMany') {
                this.get(name).forEach((object) => {
                    if (object.get('isNew')) {
                        recordsToDelete.push(object);
                    }
                });
            }
        });

        var promise = this._super(...arguments);
        promise.then(() => {
            recordsToDelete.forEach((record) => {
                record.deleteRecord();
            });
        });

        return promise;
    }
});