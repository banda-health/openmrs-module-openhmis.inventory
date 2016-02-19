requirejs(['operationtypes/configs/operationTypes.module'], function() {
	requirejs(['reusable-components/lib/domReady'], function(domReady) {
		domReady(function() {
			angular.bootstrap(domReady, ['entitiesApp']);
		});
	});
});
