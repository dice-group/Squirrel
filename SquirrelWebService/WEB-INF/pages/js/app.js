var app = angular.module('app', [ 'ngMaterial', 'ngMessages', 'material.svgAssetsCache','ui.router']);

app.config(['$stateProvider', function($stateProvider) {
  var homeState = {
    name: 'home',
    url: '/home',
    templateUrl: 'pages/templates/home.html'
  };

  var aboutState = {
    name: 'about',
    url: '/about',
    templateUrl: 'pages/templates/about.html'
  };

  var factCheckState = {
    name: 'fact-check',
    url: '/fact-check',
    templateUrl: 'pages/templates/fact-check.html'
  };

  var crawlerState = {
    name: 'crawler',
    url: '/crawler',
    templateUrl: 'pages/templates/crawler.html'
  };

  var squirrelState = {
    name: 'squirrel',
    url: '/squirrel',
    templateUrl: 'pages/templates/squirrel.html'
  };

  $stateProvider.state(homeState);
  $stateProvider.state(aboutState);
  $stateProvider.state(factCheckState);
  $stateProvider.state(crawlerState);
  $stateProvider.state(squirrelState);
}]);

app.config(['$urlRouterProvider', function($urlRouterProvider) {
  $urlRouterProvider.when('', '/home');
  $urlRouterProvider.otherwise('/home');
}]);