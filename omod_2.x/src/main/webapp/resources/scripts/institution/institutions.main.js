/* initialize and bootstrap application */
requirejs(['institution/institutions.module',
], function() {
		requirejs(['lib/domReady'], function (domReady) {
			domReady(function(){
				console.log('bootstrap');
				angular.bootstrap(domReady, [ 'institutionsApp' ]);
			});
		});
});
