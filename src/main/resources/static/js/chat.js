// connessione websocket della chat: invio, ricezione in tempo reale
// e rendering bolle. bookingId/myEmail/myName/receiverEmail arrivano da Thymeleaf


var stomp = null;

var socket = new SockJS('/ws');
stomp = Stomp.over(socket);
stomp.debug = null; // zittisce i log STOMP in console

stomp.connect({}, function() {
    stomp.subscribe('/user/queue/messages', function(msg) {
        var dati = JSON.parse(msg.body);
        aggiungiMessaggio(dati.content, dati.senderName, false);
    });
    scrollFondo();
});

function inviaMessaggio() {
    var input = document.getElementById('inputMsg');
    var testo = input.value.trim();
    if (!testo || !stomp) return;

    stomp.send('/app/chat.send', {}, JSON.stringify({
        content: testo,
        receiverEmail: receiverEmail,
        bookingId: bookingId
    }));

    // mostro subito il messaggio lato mittente invece di aspettare l'eco dal server
    aggiungiMessaggio(testo, 'Tu', true);
    input.value = '';
}

function aggiungiMessaggio(testo, mittente, mio) {
    var div = document.createElement('div');
    div.className = mio ? 'msg-mio' : 'msg-altro';

    var ora = new Date().toTimeString().substring(0, 5);

    if (mio) {
        div.innerHTML =
            '<div><div class="bolla">' + testo +
            '<div class="ora-msg text-end">' + ora + '</div>' +
            '</div></div>';
    } else {
        div.innerHTML =
            '<div><div class="nome-mittente">' + mittente + '</div>' +
            '<div class="bolla">' + testo +
            '<div class="ora-msg">' + ora + '</div>' +
            '</div></div>';
    }

    var chat = document.getElementById('chatMessages');
    chat.appendChild(div);
    scrollFondo();
}

function scrollFondo() {
    var chat = document.getElementById('chatMessages');
    chat.scrollTop = chat.scrollHeight;
}