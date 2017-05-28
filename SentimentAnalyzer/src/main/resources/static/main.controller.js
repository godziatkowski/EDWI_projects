(function () {

    angular
            .module('AuctionHelper')
            .controller('MainController', MainController);

    MainController.$inject = ['$scope', 'SearchQuery'];

    function MainController($scope, SearchQuery) {

        $scope.query = '';
        $scope.search = search;
        $scope.isPositive = isPositive;
        $scope.isNeutral = isNeutral;
        $scope.isNegative = isNegative;
        $scope.getNextPage = getNextPage;

        var currentPage = 0;

        function search() {
            if ($scope.query) {
                $scope.statistics = undefined;
                var searchQuery = {};
                currentPage = 0;
                searchQuery.searchQuery = angular.copy($scope.query);
                searchQuery.page = currentPage;
                SearchQuery.search(searchQuery).$promise
                        .then(function (result) {
                            $scope.sites = result;
                            for (var i = 0; i < $scope.sites.length; i++) {
                                $scope.sites[0].details = false;
                            }
                            calculateStatistics();
                        }, function (reason) {
                            $scope.error = true;
                        });
            }
        }

        function isPositive(site) {
            return site.sentiment && site.sentiment.label === 'pos';
        }

        function isNeutral(site) {
            return site.sentiment && site.sentiment.label === 'neutral';
        }

        function isNegative(site) {
            return site.sentiment && site.sentiment.label === 'neg';
        }

        function getNextPage() {
            if ($scope.query) {
                var searchQuery = {};
                currentPage++;
                searchQuery.searchQuery = angular.copy($scope.query);
                searchQuery.page = currentPage;
                SearchQuery.search(searchQuery).$promise
                        .then(function (results) {
                            for (var resultIndex = 0; resultIndex < results.length; resultIndex++) {
                                $scope.sites.push(results[resultIndex]);
                            }
                            for (var siteIndex = 0; siteIndex < $scope.sites.length; siteIndex++) {
                                $scope.sites[siteIndex].details = false;
                            }
                            calculateStatistics();
                        }, function (reason) {
                            $scope.error = true;
                        });
            }

        }

        function calculateStatistics() {
            var statistics = {
                positive: 0,
                neutral: 0,
                negative: 0,
                unrecognized: 0
            };
            for (var i = 0; i < $scope.sites.length; i++) {
                var site = $scope.sites[i];
                if (isPositive(site)) {
                    statistics.positive+=1;
                } else if (isNeutral(site)) {
                    statistics.neutral+=1;
                } else if (isNegative(site)) {
                    statistics.negative+=1;
                } else {
                    statistics.unrecognized+=1;
                }
            }
            $scope.statistics = statistics;
        }
    }


})();