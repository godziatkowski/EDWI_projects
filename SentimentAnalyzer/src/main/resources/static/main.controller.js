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

        function search() {
            if ($scope.query) {
                var searchQuery = {};
                searchQuery.searchQuery = angular.copy($scope.query);
                searchQuery.page = 0;
                SearchQuery.search(searchQuery).$promise
                        .then(function (result) {
                            $scope.sites = result;
                            for (var i = 0; i < $scope.sites.length; i++) {
                                $scope.sites[0].details = false;
                            }
                        }, function (reason) {
                            $scope.error = true;
                        });
            }
        }

        function isPositive(site) {
            console.log(site)
            return site.sentiment && site.sentiment.label === 'pos';
        }

        function isNeutral(site) {
            console.log(site)
            return site.sentiment && site.sentiment.label === 'neutral';
        }

        function isNegative(site) {
            console.log(site)
            return site.sentiment && site.sentiment.label === 'neg';
        }
    }


})();