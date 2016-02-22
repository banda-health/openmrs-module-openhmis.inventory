(function() {
  'use strict';

  var base = angular.module('app.genericManageController');
  base.controller("ManageInstitutionController", ManageInstitutionController);
  ManageInstitutionController.$inject = ['$injector', '$scope', '$filter', 'EntityRestFactory', 'CssStylesFactory',
      'PaginationService', 'InstitutionModel', 'CookiesService'];

  function ManageInstitutionController($injector, $scope, $filter, EntityRestFactory, CssStylesFactory, PaginationService,
          InstitutionModel, CookiesService) {

    var self = this;

    var module_name = 'inventory';
    var entity_name = emr.message("openhmis.inventory.institution.name");
<<<<<<< HEAD
    var rest_entity_name = emr.message("openhmis.inventory.institution.name_rest");
=======
	var rest_entity_name = emr.message("openhmis.inventory.institution.name_rest");
>>>>>>> 083d37350209b67586ea7261028842109cc5b7f1

    // @Override
    self.getModelAndEntityName = self.getModelAndEntityName || function() {
      self.bindBaseParameters(module_name, rest_entity_name, entity_name);
    }

    /* ENTRY POINT: Instantiate the base controller which loads the page */
    $injector.invoke(base.GenericManageController, self, {
      $scope: $scope,
      $filter: $filter,
      EntityRestFactory: EntityRestFactory,
      PaginationService: PaginationService,
      CssStylesFactory: CssStylesFactory,
      GenericMetadataModel: InstitutionModel,
      CookiesService: CookiesService
    });
  }
})();
