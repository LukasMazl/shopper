'use strict';

var favouritesModule = angular.module('favouritesApp', ['ui.bootstrap']);

favouritesModule.controller('favouritesController', function ($scope, $http, $location) {
  $scope.initialLoad = true;
  $scope.estates = [];
  $scope.sort = 'visible,active,stars,dateSort,desc';

  $http.defaults.headers.post["Content-Type"] = "application/json";

  function fetchEstates() {
	var url = '/api/v1/estates/search/findAllByStarsGreaterThanAndAddressLike';
    var params = {
      'stars': 0,
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

  $scope.init = function () {
    fetchEstates();
  }

  $scope.searchSubmit = function() {
    console.log("searchSubmit " + $scope.searchText);
    fetchEstates();
  }

  $scope.historyAll = function(estate) {
	var historyFormated = "";
	for (var history of estate.histories) {
		historyFormated += $scope.timestampToLocaleDateString(history.createdAt) + " " + history.message + "\n";
	}
    return historyFormated;
  }

  $scope.historyPrice = function(estate) {
	var historyFormated = "";
	for (var history of estate.histories) {
		if (history.historyType == "PRICE" || history.historyType == "DUPLICITY")
			historyFormated += $scope.timestampToLocaleDateString(history.createdAt) + " " + history.message + "\n";
	}
    return historyFormated;
  }

  $scope.historyPriceDifference = function(estate) {
	var historyPriceFirst = null
	for (var history of estate.histories) {
		if (history.historyType == "PRICE"){
			if (historyPriceFirst == null)
				historyPriceFirst = history;
			else if (history.createdAt < historyPriceFirst.createdAt)
				historyPriceFirst = history;
		}
	}
	if (historyPriceFirst == null)
		return "";
	
    var price = historyPriceFirst.message.match(/Cena: (.*) -> .*/)[1];
    var rozdil = estate.price - price;
    return rozdil;
  }

  $scope.addressShort = function(estate) {
    return estate.address.replace(/Praha (.*)- /, '$1- ');
  }

  $scope.timestampToLocaleDateString = function(timestamp) {
    return new Date(timestamp).toLocaleFormat('%d.%m.%Y');
  }

  $scope.smallImageUrl = function(bigImageUrl) {
    return bigImageUrl.replace("img.sreality.cz/big/", "img.sreality.cz/middle/");
  }

  $scope.numberWithSpacesAndSign = function(number) {
	if (number == null)
		return null;
	return (number > 0 ? "+" : "") + $scope.numberWithSpaces(number);
  }

  $scope.numberWithSpaces = function(number) {
    return (number == null) ? null : number.toString().replace(/\B(?=(\d{3})+(?!\d))/g, " ");
  }

  $scope.constructGoogleMapsUrl = function(address) {
    return 'https://www.google.com/maps/place/' + encodeURIComponent(address);
  }
});

favouritesModule.controller('estateRatingController', function ($scope, $http) {
  $scope.stars = undefined;
  $scope.starsOld = undefined;
  $scope.maxStars = 3;
  $scope.srealityId = undefined;

  $scope.setStars = function() {
		if ($scope.stars == $scope.starsOld)
			$scope.stars = 0;
		var url = '/api/v1/vote';
	    var params = {
	      'srealityId': $scope.srealityId,
	      'stars': $scope.stars,
	    };
	    $http({ method: 'GET', url: url, params: params });
	    $scope.starsOld = $scope.stars;
  };
});

favouritesModule.controller('estateNoteController', function ($scope, $http) {

  $scope.setNote = function() {
		var url = '/api/v1/setNote';
	    var params = {
	      'srealityId': $scope.estate.srealityId,
	      'note': $scope.estate.note,
	    };
	    $http({ method: 'GET', url: url, params: params });
  };
});

favouritesModule.controller('estateVisibleController', function ($scope, $http) {

  $scope.setVisible = function() {
		var url = '/api/v1/setVisible';
	    var params = {
	      'srealityId': $scope.estate.srealityId,
	      'visible': $scope.estate.visible,
	    };
	    $http({ method: 'GET', url: url, params: params });
  };
});
