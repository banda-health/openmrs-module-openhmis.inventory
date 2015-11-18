(function() {
  'use strict';

  var base = angular.module('app.genericManageController');
  base.controller("ManageInstitutionController", ManageInstitutionController);
  ManageInstitutionController.$inject = ['$injector', '$scope', '$filter',
      'ManageEntityRestFactory', 'CssStylesFactory', 'PaginationService',
      'InstitutionModel', 'CookiesService'];

  function ManageInstitutionController($injector, $scope, $filter,
          ManageEntityRestFactory, CssStylesFactory, PaginationService,
          InstitutionModel, CookiesService) {

    var self = this;

    var module_name = 'inventory';
    var entity_name = emr.message("openhmis.inventory.institution.name");

    // @Override
    self.getModelAndEntityName = self.getModelAndEntityName || function() {
      self.bindBaseParameters(module_name, entity_name);
    }

    /* ENTRY POINT: Instantiate the base controller which loads the page */
    $injector.invoke(base.GenericManageController, self, {
      $scope: $scope,
      $filter: $filter,
      ManageEntityRestFactory: ManageEntityRestFactory,
      PaginationService: PaginationService,
      CssStylesFactory: CssStylesFactory,
      GenericMetadataModel: InstitutionModel,
      CookiesService: CookiesService
    });
  }
})();
