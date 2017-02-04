'use strict';

var shopperModule = angular.module("shopperApp")
	.component("estateSummary", {
		templateUrl: 'estateSummary.html',
		controller: EstateSummaryController,
		bindings: {
			estate: '='
		}
	});

function EstateSummaryController() {
	this.addressShort = function(estate) {
		return estate.address.replace(/Praha (.*)- /, '$1- ');
	}

	this.historyPrice = function(estate) {
		var historyFormated = "";
		for (var history of estate.histories) {
			if (history.historyType == "PRICE" || history.historyType == "DUPLICITY")
				historyFormated += this.timestampToLocaleDateString(history.createdAt) + " " + history.message + "\n";
		}
		return historyFormated;
	}

	this.historyPriceDifference = function(estate) {
		var historyPriceFirst = null;
		for (var history of estate.histories) {
			if (history.historyType == "PRICE") {
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

	this.historyAll = function(estate) {
		var historyFormated = "";
		for (var history of estate.histories) {
			historyFormated += this.timestampToLocaleDateString(history.createdAt) + " " + history.message + "\n";
		}
		return historyFormated;
	}

	this.dates = function(estate) {
		var dates = this.timestampToLocaleDateString(estate.createdAt);
		if (this.timestampToLocaleDateString(estate.createdAt) != this.timestampToLocaleDateString(estate.dateSort))
			dates += " (" + this.timestampToLocaleDateString(estate.dateSort) + ")";
		return dates;
	}

	this.timestampToLocaleDateString = function(timestamp) {
		return new Date(timestamp).toLocaleFormat('%d.%m.%Y');
	}

	this.smallImageUrl = function(bigImageUrl) {
		return bigImageUrl.replace("img.sreality.cz/big/", "img.sreality.cz/middle/");
	}

	this.numberWithSpacesAndSign = function(number) {
		if (number == null)
			return null;
		return (number > 0 ? "+" : "") + this.numberWithSpaces(number);
	}

	this.numberWithSpaces = function(number) {
		return (number == null) ? null : number.toString().replace(/\B(?=(\d{3})+(?!\d))/g, " ");
	}

	this.constructGoogleMapsUrl = function(address) {
		return 'https://www.google.com/maps/place/' + encodeURIComponent(address);
	}
}

shopperModule.controller('estateRatingController', function ($scope, $http) {
	$scope.estate = undefined;
	$scope.starsOld = undefined;
	$scope.maxStars = 3;

	$scope.setStars = function() {
		if ($scope.estate.stars == $scope.starsOld)
			$scope.estate.stars = 0;
		var url = '/api/v1/vote';
		var params = {
			'srealityId': $scope.estate.srealityId,
			'stars': $scope.estate.stars,
		};
		$http({ method: 'GET', url: url, params: params });
		$scope.starsOld = $scope.estate.stars;
	};
});

shopperModule.controller('estateVisibleController', function ($scope, $http) {
	$scope.estate = undefined;
	
	$scope.setVisible = function() {
		var url = '/api/v1/setVisible';
		var params = {
			'srealityId': $scope.estate.srealityId,
			'visible': $scope.estate.visible,
		};
		$http({ method: 'GET', url: url, params: params });
	};
});

shopperModule.controller('estateNoteController', function ($scope, $http) {
	$scope.estate = undefined;
	
	$scope.setNote = function() {
		var url = '/api/v1/setNote';
		var params = {
			'srealityId': $scope.estate.srealityId,
			'note': $scope.estate.note,
		};
		$http({ method: 'GET', url: url, params: params });
	};
});