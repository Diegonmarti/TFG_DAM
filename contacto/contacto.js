// Función de inicialización del mapa
function initMap() {
  // Crear mapa
  var map = new google.maps.Map(document.getElementById('mapContainer'), {
    center: {lat: 0, lng: 0}, // Coordenadas iniciales del mapa
    zoom: 12 
  });

  if (navigator.geolocation) {
    navigator.geolocation.getCurrentPosition(function(position) {
      var pos = {
        lat: position.coords.latitude,
        lng: position.coords.longitude
      };

      map.setCenter(pos);

      var marker = new google.maps.Marker({
        position: pos,
        map: map
      });
    });
  }
}


function initMap() {
  var addressCoords = [40.426901, -3.686825];
  var map = L.map('map').setView(addressCoords, 15);

  L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
    attribution: 'Map data © <a href="https://openstreetmap.org">OpenStreetMap</a> contributors'
  }).addTo(map);
  L.marker(addressCoords).addTo(map)
    .bindPopup('C. de Jorge Juan, 8, 28001 Madrid')
    .openPopup();
}
initMap();

function enviarFormulario() {
  var nombre = document.getElementById("nombre").value;
  var email = document.getElementById("email").value;
  var mensaje = document.getElementById("mensaje").value;

  var correoDestino = 'diegogmarti@gmail.com';
  var asunto = 'Datos del formulario';

  var mailtoLink = 'mailto:' + correoDestino +
    '?subject=' + encodeURIComponent(asunto) +
    '&body=' + encodeURIComponent('Nombre: ' + nombre + '\nEmail: ' + email + '\nMensaje: ' + mensaje);

  window.location.href = mailtoLink;
}