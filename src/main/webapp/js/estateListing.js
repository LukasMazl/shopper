'use strict';

var estateListingModule = angular.module('estateListingApp', ['ui.bootstrap']);

estateListingModule.controller('estateListingController', function ($scope, $http) {
  $scope.estates = [];
  $scope.totalEstates = 0;
  $scope.pageNumber = 1;
  $scope.pageSize = 20;
  $scope.sort = 'dateSort,desc';

  $scope.maxNumberOfPageNumbers = 20;
  $http.defaults.headers.post["Content-Type"] = "application/json";

  function fetchEstates() {
    var requestUri = '/api/v1/estates?page=' + ($scope.pageNumber - 1) + '&size=' + $scope.pageSize + '&sort=' + $scope.sort;
    $http.get(requestUri).success(function(data) {
      if (data._embedded != undefined) {
        $scope.estates = data._embedded.estates;
      }
      $scope.totalEstates = data.page.totalElements;
    }).error(function(error) {
      $scope.fetchEstatesError = error;
    });
  };

  $scope.$watch('pageNumber + pageSize', function() {
      fetchEstates();
  });

  $scope.timestampToLocaleDateString = function(timestamp) {
    return new Date(timestamp).toLocaleFormat('%d.%m.%Y');
  }

  $scope.smallImageUrl = function(bigImageUrl) {
    return bigImageUrl.replace("img.sreality.cz/big/", "img.sreality.cz/middle/");
  }

  $scope.numberWithSpaces = function(number) {
    return (number == null) ? null : number.toString().replace(/\B(?=(\d{3})+(?!\d))/g, " ");
  }

  $scope.constructGoogleMapsUrl = function(address) {
    return 'https://www.google.com/maps/embed/v1/place?key=AIzaSyD3cmQ1Y2ZxnegHDg2Q2CmrOWqxn2ot6B4&q=' + encodeURIComponent(address);
  }
});
