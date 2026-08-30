// mappa posizione agenzia, sola visualizzazione (nessun click/edit qui,
// per quello vedi agency-form-map.js). lat/lng/agencyName arrivano da Thymeleaf

if (lat && lng) {
    var mappa = L.map('mappaAgenzia').setView([lat, lng], 14);
    L.tileLayer('https://tile.openstreetmap.org/{z}/{x}/{y}.png', {
        attribution: '© OpenStreetMap'
    }).addTo(mappa);
    L.marker([lat, lng])
        .addTo(mappa)
        .bindPopup('<b>' + agencyName + '</b>')
        .openPopup();
}