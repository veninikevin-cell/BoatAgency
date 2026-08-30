// checkbox giorni settimana -> stringa CSV nel campo nascosto availableDays
// (es "LUN,MER,VEN"), formato atteso da TourDTO lato server
var checkboxes = document.querySelectorAll('input[name="giorni"]');
checkboxes.forEach(function(cb) {
    cb.addEventListener('change', function() {
        var selezionati = [];
        checkboxes.forEach(function(c) {
            if (c.checked) selezionati.push(c.value);
        });
        document.getElementById('availableDaysInput').value = selezionati.join(',');
    });
});