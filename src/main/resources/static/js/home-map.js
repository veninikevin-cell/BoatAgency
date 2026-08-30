// mappa home centrata sul lago di Como, marker agenzie da /api/agenzie
// (endpoint in HomeController, filtra solo quelle con coordinate settate)
var mappa = L.map('mappa').setView([46.0, 9.2], 11);

L.tileLayer('https://tile.openstreetmap.org/{z}/{x}/{y}.png', {
    attribution: '© OpenStreetMap'
}).addTo(mappa);

fetch('/api/agenzie')
    .then(function(r) { return r.json(); })
    .then(function(agenzie) {
        agenzie.forEach(function(ag) {
            var marker = L.marker([ag.latitude, ag.longitude]).addTo(mappa);
            marker.bindPopup(
                '<b>' + ag.name + '</b><br>' +
                '📍 ' + ag.zone + '<br>' +
                '<a href="/public/agency/' + ag.id + '">Vedi agenzia →</a>'
            );
        });
    })
    .catch(function(e) {
        console.log('errore caricamento agenzie:', e);
    });