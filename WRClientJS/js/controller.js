/**
 * Created by hadrien on 20/06/14.
 */

var phonecatApp = angular.module('phonecatApp', []);
phonecatApp.controller('PhoneListCtrl', ['$scope', '$http',
    function ($scope, $http) {
        console.log("Server called ");
        $('#spin').spin();
        $http.get('http://localhost:8080/webradio/'+"red hot chili peppers_can't stop").success(function(data) {
            console.log("Got that data");
            $('#spin').spin(false);
            $scope.songs = data;
        });
        $scope.orderProp = 'artist';
        $scope.reverse = 'false';
    }]);