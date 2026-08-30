// mappa per scegliere posizione agenzia. marker sulle coordinate salvate
// (se ci sono) e si sposta al click. latVal/lngVal arrivano da Thymeleaf
var mappa = L.map('mappaForm').setView([46.0, 9.2], 11);
 
L.tileLayer('https://tile.openstreetmap.org/{z}/{x}/{y}.png', {
    attribution: '© OpenStreetMap'
}).addTo(mappa);
 
var marker = null;
 
if (latVal && lngVal) {
    marker = L.marker([latVal, lngVal]).addTo(mappa);
    mappa.setView([latVal, lngVal], 13);
}
 
mappa.on('click', function(e) {
    var lat = e.latlng.lat.toFixed(6);
    var lng = e.latlng.lng.toFixed(6);
 
    document.getElementById('latInput').value = lat;
    document.getElementById('lngInput').value = lng;
 
    if (marker) {
        marker.setLatLng([lat, lng]);
    } else {
        marker = L.marker([lat, lng]).addTo(mappa);
    }});