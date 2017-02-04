'use strict';

var estateListingModule = angular.module('estateListingApp', ['ui.bootstrap']);

estateListingModule.controller('estateListingController', function ($scope, $http, $location) {
  $scope.initialLoad = true;
  $scope.estates = [];
  $scope.totalEstates = 0;
  $scope.pageNumber = 123456789;
//  $scope.pageNumber = parseInt($location.search()['pageNumber']) || 1;
  $scope.pageSize = 20;
  $scope.sort = 'dateSort,desc';

  $scope.maxNumberOfPageNumbers = 20;
  $http.defaults.headers.post["Content-Type"] = "application/json";

  function fetchEstates() {
	var url = '/api/v1/estates/search/findAllByActiveAndVisibleAndAddressLike';
    var params = {
      'active': true,
      'visible': true,
      'address': $scope.searchText || "%",
      'page': $scope.pageNumber - 1,
      'size': $scope.pageSize,
      'sort': $scope.sort,
    };
    console.log("Fetch estates: url = " + url + " , params = " + JSON.stringify(params));
    $http({ method: 'GET', url: url, params: params }).success(function(data) {
      if (data._embedded != undefined) {
        $scope.estates = data._embedded.estates;
      }
      $scope.totalEstates = data.page.totalElements;
      $location.search({'pageNumber': $scope.pageNumber});
    }).error(function(error) {
      $scope.fetchEstatesError = error;
    });
  };

  $scope.searchSubmit = function() {
    console.log("searchSubmit " + $scope.searchText);
    fetchEstates();
  }

  $scope.pageChanged = function() {
    if ($scope.initialLoad) {
      $scope.initialLoad = false;
      $scope.pageNumber = parseInt($location.search()['pageNumber']) || 1;
    }
    fetchEstates();
  }

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

estateListingModule.controller('estateRatingController', function ($scope, $http) {
  $scope.stars = 0;
  $scope.maxStars = 3;
  $scope.srealityId = undefined;

  $scope.$watch('stars', function() {
    console.log("Set stars for " + $scope.srealityId + " to " + $scope.stars);
	var url = '/api/v1/vote';
    var params = {
      'srealityId': $scope.srealityId,
      'stars': $scope.stars,
    };
    $http({ method: 'GET', url: url, params: params });
  })
});
