// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

var geocoder;
var map;
var markers;
var directionsService;

/** Creates a map and adds it to the page. */
function createMap() {
  geocoder = new google.maps.Geocoder();
  directionsService = new google.maps.DirectionsService();
  markers = [];
  map = new google.maps.Map(
    document.getElementById('map'), {
      center: {
        lat: 0,
        lng: 0
      },
      zoom: 4
    });
}

function addMarker(address) {
  //works by geocoding, another google API
  geocoder.geocode({
    'address': address
  }, function(results, status) {
    if (status == 'OK') {
      map.setCenter(results[0].geometry.location);
      var marker = new google.maps.Marker({
        map: map,
        position: results[0].geometry.location
      });
      markers.push(marker); //global tracker for markers, no easy way to access them
    } else {
      alert('Geocode was not successful for the following reason: ' + status);
    }
  });
}

async function triggerTSP(key) {
  //append the herokuapp before so that we can do cors
  var baseURL = "https://cors-anywhere.herokuapp.com/https://maps.googleapis.com/maps/api/distancematrix/json?";

  var params = new URLSearchParams();
  //in this block, we are going to build the search string
  var or_dest = "";
  var lines = document.getElementById('addresses').value.split('\n');
  for (var i = 0; i < lines.length; i++) {
    or_dest += lines[i]
    or_dest += "|"
    addMarker(lines[i]) //add a marker to the map
  }
  params.append('origins', or_dest);
  params.append('destinations', or_dest);
  params.append('key', key);

  //get the response and parse as json
  let response = await fetch(baseURL.concat(params.toString()), {
    mode: 'cors'
  });
  let data = await response.json();

  //unpack json and extract make it into a matrix
  cities = data["destination_addresses"]
  var distmat = [];
  for (var i = 0; i < cities.length; i++) {
    distmat[i] = new Array(cities.length);
  }
  for (var i = 0; i < data["rows"].length; i++) {
    for (var j = 0; j < data["rows"].length; j++) {
      distmat[i][j] = data["rows"][i]["elements"][j]["distance"]["value"]
    }
  }

  // run the 2-opt algorithm
  best_rte = twoopt(distmat, 1800000000)
  //this is the iteration limit cuz that takes roughly 1 second on a normal computer

  //add everything to a list element so taht it displays nicely
  const adressListElement = document.getElementById('address-ul');
  adressListElement.innerHTML = '';
  for (i = 0; i < best_rte.length - 1; i++) {
    adressListElement.appendChild(
      createListElementAddress(cities[best_rte[i]]));
    //this will draw the route in the map
    calcRoute(cities[best_rte[i]], cities[best_rte[i + 1]]);
  }
  adressListElement.appendChild(
    createListElementAddress("And finally, back to ".concat(cities[0])));

  //center the markers by using the fitBounds method
  var bounds = new google.maps.LatLngBounds();
  for (var i = 0; i < markers.length; i++) {
    bounds.extend(markers[i].getPosition());
  }
  map.setCenter(bounds.getCenter());
  map.fitBounds(bounds);
  if (map.getZoom() > 15) {
    map.setZoom(15);
  }
}

function createListElementAddress(city) {
  const liElement = document.createElement('li');
  liElement.innerHTML = "<p>" + city + "</p>";
  return liElement;
}

function calcRoute(start, end) {
  var directionsRenderer = new google.maps.DirectionsRenderer();
  directionsRenderer.setMap(map);
  var request = {
    origin: start,
    destination: end,
    travelMode: 'DRIVING'
  };
  directionsService.route(request, function(result, status) {
    if (status == 'OK') {
      directionsRenderer.setDirections(result);
    }
  });
}
