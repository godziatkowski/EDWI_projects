(function () {

    angular
            .module('AuctionHelper')
            .controller('MainController', MainController);

    MainController.$inject = ['$scope', 'SearchQuery'];

    function MainController($scope, SearchQuery) {

        $scope.query = '';

        $scope.search = search;

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
                            console.log(reason)
                        });
            }
        }
    }


})();