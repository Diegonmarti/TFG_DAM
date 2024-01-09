var point1, point2, mymap, marker;
// Obtiene la ubicación del usuario y centra el mapa en esa ubicación
if ("geolocation" in navigator) {
navigator.geolocation.getCurrentPosition(position => {
var userLat = position.coords.latitude;
var userLon = position.coords.longitude;
mymap.setView([userLat, userLon], 13);
});
}

var defaultStreet = "C. de Jorge Juan, 828001 Madrid";
document.getElementById("street").value = defaultStreet;

function showMap() {
var street = document.getElementById("street").value;
if (street === "") {
alert("Introduce una calle válida");
return;
}
var url = "https://nominatim.openstreetmap.org/search?format=json&q=" + street;
fetch(url)
.then(response => response.json())
.then(data => {
  if (data.length === 0) {
    alert("No se encontró una ubicación correspondiente a la calle");
    return;
  }
  var lat = data[0].lat;
  var lon = data[0].lon;

point1 = L.latLng(lat, lon);
if (!mymap) {
mymap = L.map('mapContainer').setView([lat, lon], 13);
L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
maxZoom: 18,
attribution: 'Map data © <a href="https://openstreetmap.org">OpenStreetMap</a> contributors'
}).addTo(mymap);
} else {
mymap.setView([lat, lon], 13);
if (marker) {
marker.remove();
}
}
marker = L.marker([lat, lon]).addTo(mymap);
document.getElementById("resetButton").disabled = false;
document.getElementById("resetButton").style.display = "inline-block";
document.getElementById("calculateDistanceButton").disabled = false;
document.getElementById("coordinates").innerHTML = "Coordenadas: " + lat + ", " + lon;
});
}

document.getElementById("showMapButton").addEventListener("click", showMap);

function showMyLocation() {
if (!mymap) {
alert("Primero debes buscar una calle");
return;
}

if ("geolocation" in navigator) {
navigator.geolocation.getCurrentPosition(position => {
var userLat = position.coords.latitude;
var userLon = position.coords.longitude;
point2 = L.latLng(userLat, userLon);
L.marker([userLat, userLon]).addTo(mymap);
L.polyline([point1, point2], {color: 'red'}).addTo(mymap);

var distance = point1.distanceTo(point2);
document.getElementById("distance").innerHTML = "Distancia: " + distance.toFixed(2) + " metros";
});
} else {
alert("Geolocalización no disponible");
}
}

document.getElementById("myLocationButton").addEventListener("click", showMyLocation);
function calculateDistance() {
if (!point2) {
alert("Primero debes presionar el botón 'Mi ubicación'");
return;
}

var distance = point1.distanceTo(point2);
document.getElementById("distance").innerHTML = "Distancia: " + distance.toFixed(2) + " metros";
}
function resetMap() {
if (marker) {
marker.remove();
}
if (mymap) {
mymap.remove();
mymap = null;
}
point1 = null;
point2 = null;
document.getElementById("resetButton").disabled = true;
document.getElementById("resetButton").style.display = "none";
document.getElementById("coordinates").innerHTML = "";
document.getElementById("distance").innerHTML = "";

}

function resetPage() {
location.reload();
}
document.getElementById("resetPageButton").addEventListener("click", resetPage);
