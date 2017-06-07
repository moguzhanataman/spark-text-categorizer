angular.module('test', ['tangcloud'])
    .controller('TestCtrl', ['$scope', '$timeout', function ($scope, $timeout) {
        $timeout(function(){
            $scope.words = [
                {id: 1, word: "Big Data", size: 1},
                {id: 2, word: "oguzhan", size: 6},
                {id: 3, word: "test", size: 7},
                {id: 4, word: "kursat", size: 2},
                {id: 5, word: "yavuz", size: 10},
                {id: 6, word: "word3", size: 3},
                {id: 7, word: "cse 458", size: 4},
                {id: 8, word: "word5", size: 5},
                {id: 9, word: "word8", size: 8},
                {id: 10, word: "word9", size: 9},
                {id: 1, word: "Big Data", size: 1},
                {id: 2, word: "oguzhan", size: 6},
                {id: 3, word: "test", size: 7},
                {id: 4, word: "kursat", size: 2},
                {id: 5, word: "yavuz", size: 10},
                {id: 6, word: "word3", size: 3},
                {id: 7, word: "cse 458", size: 4},
                {id: 8, word: "word5", size: 5},
                {id: 9, word: "word8", size: 8},
                {id: 10, word: "word9", size: 9},
                {id: 1, word: "Big Data", size: 1},
                {id: 2, word: "oguzhan", size: 6},
                {id: 3, word: "test", size: 7},
                {id: 4, word: "kursat", size: 2},
                {id: 5, word: "yavuz", size: 10},
                {id: 6, word: "word3", size: 3},
                {id: 7, word: "cse 458", size: 4},
                {id: 8, word: "word5", size: 5},
                {id: 9, word: "word8", size: 8},
                {id: 10, word: "word9", size: 9},
                {id: 1, word: "Big Data", size: 1},
                {id: 2, word: "oguzhan", size: 6},
                {id: 3, word: "test", size: 7},
                {id: 4, word: "kursat", size: 2},
                {id: 5, word: "yavuz", size: 10},
                {id: 6, word: "word3", size: 3},
                {id: 7, word: "cse 458", size: 4},
                {id: 8, word: "word5", size: 5},
                {id: 9, word: "word8", size: 8},
                {id: 10, word: "word9", size: 9},
                {id: 1, word: "Big Data", size: 1},
                {id: 2, word: "oguzhan", size: 6},
                {id: 3, word: "test", size: 7},
                {id: 4, word: "kursat", size: 2},
                {id: 5, word: "yavuz", size: 10},
                {id: 6, word: "word3", size: 3},
                {id: 7, word: "cse 458", size: 4},
                {id: 8, word: "word5", size: 5},
                {id: 9, word: "word8", size: 8},
                {id: 10, word: "word9", size: 9}
            ];
        }, 1000);

        $scope.test = function(word) {
            alert("clicked on " + word);
        }
    }]);