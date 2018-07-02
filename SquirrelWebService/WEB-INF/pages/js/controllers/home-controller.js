app.controller('homeCtrl', ['$scope', '$http', function ($scope, $http) {

  $scope.autoUpdate = true;
  $scope.slider = 100;

  $scope.getData = function () {
    if ($scope.autoUpdate && $scope.slider.valueOf() === 100) {
      $http.get("/observer")
        .then(function (response) {
          setContent(response)
        }, function myError(response) {
          console.log(response);
        });
    }
  };

  $scope.getData();

  $scope.onSliderChange = function () {
    if ($scope.autoUpdate) {
      $http.get("/observer?id=" + $scope.slider.valueOf() + "&percent=true")
        .then(function (response) {
          setContent(response)
        }, function myError(response) {
          console.log(response);
        });
    }
  };

  function setContent($response) {
    $scope.pendingURIs = $response.data.countOfPendingURIs;
    $scope.countOfCrawledURIs = $response.data.countOfCrawledURIs;
    $scope.countOfWorker = $response.data.countOfWorker;
    $scope.countOfDeadWorker = $response.data.countOfDeadWorker;
    $scope.runtimeInSeconds = $response.data.runtimeInSeconds;
    $scope.writeTime = $response.data.writeTime;
    $scope.readTime = $response.data.readTime;
    $scope.ipStringListMap = $response.data.ipStringListMap;
    $scope.PendingURIS = $response.data.pendingURIs;
    $scope.NextCrawledURIs = $response.data.nextCrawledURIs;
    $scope.totalUrls = $response.data.totalUrls;
  }

  setInterval($scope.getData, 5000);
}]);
