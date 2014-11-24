var itemsmodule = angular.module('itemsModule', ['sharedDirectives', 'sharedServices']);

itemsmodule.controller('ctrlRead', function (httpService, $scope, $filter, $http) {

	$scope.sort = {
		sortingOrder : 'id',
		reverse : false
	};

	$scope.gap = 3;
	$scope.itemsPerPage = 5;
	$scope.currentPage = 0;
	$scope.items = [];
	$scope.departments = [];
	$scope.limits = [5,10,25,50,100];
	$scope.Math = window.Math;
	$scope.hideEditItem = true;

	$scope.edit = function(item) {
		$scope.editItem = item;
		$scope.retireReason = "";
		$scope.hideEditItem = false;
	};

	$http.get('/openmrs/ws/rest/v2/inventory/item?startIndex=1&limit=' + $scope.itemsPerPage)
	.success(function(resp) {
		$scope.items = resp.results;
		$scope.pages = Math.ceil(resp.length/5);
	});

	$http.get('/openmrs/ws/rest/v2/inventory/department')
	.success(function(resp) {
		$scope.departments = resp.results;
	});

	$scope.searchItem = function() {
		var url = '/openmrs/ws/rest/v2/inventory/item';
		var uuid = angular.isUndefined($scope.departmentSelected) || $scope.departmentSelected == null ? "" : $scope.departmentSelected.uuid;
		if ($scope.query == null || $scope.query =="") {
			url += '?startIndex=1&limit=' + $scope.itemsPerPage;
		} else {
			url += '?department_uuid='+ uuid+'&category_uuid=&q='+ $scope.query + '&startIndex=1&limit=' + $scope.itemsPerPage;
		}
		if ($scope.retiredChecked) {
			url += '&includeAll=true';
		}
		$http.get(url)
		.success(function(resp) {
			$scope.items = resp.results;
			$scope.pages = Math.ceil(resp.length/5);
		});
	};

	$scope.retire = function(item, reason) {
		httpService.retire('item', item.uuid, reason).then(
			function(success) {
				$scope.hideEditItem = true;
				$scope.searchItem();
			},
			function(error) {
				console.log(error);
			}
		);
	}

	$scope.purge = function(item) {
		httpService.purge('item', item.uuid).then(
			function(success) {
				$scope.hideEditItem = true;
				$scope.searchItem();
			},
			function(error) {
				console.log(error);
			}
		);
	}

	$scope.range = function (size,start, end) {
		var ret = [];
		if (size < end) {
			end = size;
			start = size-$scope.gap;
		}
		for (var i = start; i < end; i++) {
			ret.push(i);
		}
		return ret;
	};

});

itemsmodule.$inject = ['$scope', '$filter', '$http', 'httpServices'];

