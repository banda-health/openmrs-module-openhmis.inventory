/* initialize and bootstrap application */
requirejs(['item/configs/items.module'], function() {
    requirejs(['reusable-components/lib/domReady'], function(domReady) {
        domReady(function() {
            angular.bootstrap(domReady, ['itemsApp']);
        });
    });
});
