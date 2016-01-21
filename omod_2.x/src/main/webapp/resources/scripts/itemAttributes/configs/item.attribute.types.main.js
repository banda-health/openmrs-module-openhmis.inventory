/* initialize and bootstrap application */
requirejs(['itemAttributes/configs/item.attribute.types.module'], function() {
    requirejs(['reusable-components/lib/domReady'], function(domReady) {
        domReady(function() {
            angular.bootstrap(domReady, ['itemAttributeTypesApp']);
        });
    });
});
