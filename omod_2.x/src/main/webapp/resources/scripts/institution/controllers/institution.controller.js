(function() {
  'use strict';

  var base = angular.module('app.genericEntityController');
  base.controller("InstitutionController", InstitutionController);
  InstitutionController.$inject = ['$stateParams', '$injector', '$scope', '$filter', 'EntityRestFactory',
      'InstitutionModel'];

  function InstitutionController($stateParams, $injector, $scope, $filter, EntityRestFactory, InstitutionModel) {

    var self = this;

    var module_name = 'inventory';
    var entity_name = emr.message("openhmis.inventory.institution.name");
    var cancel_page = 'institutions.page';
<<<<<<< HEAD
    var rest_entity_name = emr.message("openhmis.inventory.institution.name_rest");
=======
	var rest_entity_name = emr.message("openhmis.inventory.institution.name_rest")
>>>>>>> 083d37350209b67586ea7261028842109cc5b7f1

    // @Override
    self.setRequiredInitParameters = self.setRequiredInitParameters || function() {
      self.bindBaseParameters(module_name, rest_entity_name, entity_name, cancel_page);
    }

    /* ENTRY POINT: Instantiate the base controller which loads the page */
    $injector.invoke(base.GenericEntityController, self, {
      $scope: $scope,
      $filter: $filter,
      $stateParams: $stateParams,
      EntityRestFactory: EntityRestFactory,
      GenericMetadataModel: InstitutionModel
    });
  }
})();
