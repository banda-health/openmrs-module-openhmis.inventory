/* initialize and bootstrap application */
requirejs(['institution/configs/institutions.module'], function() {
  requirejs(['reusable-components/lib/domReady'], function(domReady) {
    domReady(function() {
      angular.bootstrap(domReady, ['institutionsApp']);
    });
  });
});
