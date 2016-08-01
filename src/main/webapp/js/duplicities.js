'use strict';

var duplicitiesModule = angular.module('shopperApp', ['ui.bootstrap', 'angular.filter']);

duplicitiesModule.controller('duplicitiesController', function ($scope, $http, $location) {
  $scope.estates = [];
  $scope.sort = 'duplicityId,dateSort,desc';

  $http.defaults.headers.post["Content-Type"] = "application/json";

  function fetchEstates() {
	var url = '/api/v1/estates/search/findAllByDuplicityIdNotAndAddressLike';
    var params = {
      'duplicityId': 0,
      'address': $scope.searchText || "%",
      'sort': $scope.sort,
    };
    console.log("Fetch estates: url = " + url + " , params = " + JSON.stringify(params));
    $http({ method: 'GET', url: url, params: params }).success(function(data) {
      if (data._embedded != undefined) {
        $scope.estates = data._embedded.estates;
      }
    }).error(function(error) {
      $scope.fetchEstatesError = error;
    });
  };

  $scope.searchSubmit = function() {
    console.log("searchSubmit " + $scope.searchText);
    fetchEstates();
  }
});