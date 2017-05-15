(function () {
    'use strict';

    angular
            .module('AuctionHelper')
            .factory('SearchQuery', SearchQuery);

    SearchQuery.$inject = ['$resource'];

    function SearchQuery($resource) {
        return $resource('/api/search', {}, {
            search: {
                method: 'GET',
                isArray: true
            }
        });

    }

})();