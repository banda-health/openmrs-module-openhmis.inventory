/* initialize and bootstrap application */
requirejs(['institution/institutions.module'], function() {
		requirejs(['lib/domReady'], function (domReady) {
			domReady(function(){
				angular.bootstrap(domReady, [ 'institutionsApp' ]);
			});
		});
});
