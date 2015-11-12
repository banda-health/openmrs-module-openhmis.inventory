/* initialize and bootstrap application */
requirejs([
	'/openmrs/ms/uiframework/resource/openhmis.inventory/scripts/institution/institutions.module.js',
], function() {
		requirejs(['/openmrs/ms/uiframework/resource/openhmis.inventory/scripts/lib/domReady.js'], function (domReady) {
			domReady(function(){
				angular.bootstrap(domReady, [ 'institutionsApp' ]);
			});
		});
});
