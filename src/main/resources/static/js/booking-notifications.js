// notifiche websocket per conferma/rifiuto prenotazione

var socket = new SockJS('/ws');
var stomp = Stomp.over(socket);

stomp.connect({}, function() {
    stomp.subscribe('/user/queue/bookings', function(msg) {
        var dati = JSON.parse(msg.body);
        var testo = dati.status === 'CONFIRMED'
            ? 'La tua prenotazione è stata accettata!'
            : 'La tua prenotazione è stata rifiutata.';
        mostraNotifica(testo);
        setTimeout(function() { location.reload(); }, 2000);
    });
});

function mostraNotifica(testo) {
    var div = document.getElementById('notifica');
    div.innerHTML = '<div class="toast show">' +
        '<div class="toast-header bg-primary text-white">' +
        '<strong> Aggiornamento</strong>' +
        '<button type="button" class="btn-close btn-close-white ms-auto" ' +
        'onclick="this.closest(\'.toast\').remove()"></button>' +
        '</div>' +
        '<div class="toast-body">' + testo + '</div>' +
        '</div>';
    // pulizia automatica
    setTimeout(function() { div.innerHTML = ''; }, 4000);
}