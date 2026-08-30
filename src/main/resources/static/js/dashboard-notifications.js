// notifica websocket al fornitore per nuove prenotazioni in arrivo -

var socket = new SockJS('/ws');
var stomp = Stomp.over(socket);

stomp.connect({}, function() {
    stomp.subscribe('/user/queue/notifications', function(msg) {
        var dati = JSON.parse(msg.body);
        mostraNotifica('Nuova prenotazione da ' + dati.userName + ' per ' + dati.tourName);
    });
});

function mostraNotifica(testo) {
    var div = document.getElementById('notifica');
    div.innerHTML = '<div class="toast show">' +
        '<div class="toast-header bg-primary text-white">' +
        '<strong> Nuova prenotazione</strong>' +
        '<button type="button" class="btn-close btn-close-white ms-auto" onclick="this.closest(\'.toast\').remove()"></button>' +
        '</div>' +
        '<div class="toast-body">' + testo + '</div>' +
        '</div>';
    setTimeout(function() { div.innerHTML = ''; }, 5000);
}