// calendario mensile navigabile per scegliere la data di prenotazione -
// disabilita date già occupate e giorni della settimana non previsti dal tour.

var mappaGiorni = ["DOM", "LUN", "MAR", "MER", "GIO", "VEN", "SAB"];
var arrayGiorni = availableDays ? availableDays.split(',') : [];

var oggi = new Date();
var anno = oggi.getFullYear();
var mese = oggi.getMonth();
var nomiMesi = ['Gennaio', 'Febbraio', 'Marzo', 'Aprile', 'Maggio', 'Giugno',
                'Luglio', 'Agosto', 'Settembre', 'Ottobre', 'Novembre', 'Dicembre'];

function cambioMese(delta) {
    mese += delta;
    if (mese < 0) { mese = 11; anno--; }
    if (mese > 11) { mese = 0; anno++; }
    disegnaCalendario();
}

function disegnaCalendario() {
    document.getElementById('meseAnno').textContent = nomiMesi[mese] + ' ' + anno;

    var cal = document.getElementById('calendario');
    cal.innerHTML = '';

    // offset per far partire la griglia da lunedì invece che domenica
    var primoGiorno = new Date(anno, mese, 1).getDay();
    var offset = (primoGiorno === 0) ? 6 : primoGiorno - 1;

    for (var i = 0; i < offset; i++) {
        var vuoto = document.createElement('span');
        vuoto.className = 'giorno';
        vuoto.innerHTML = '&nbsp;';
        cal.appendChild(vuoto);
    }

    var giorniMese = new Date(anno, mese + 1, 0).getDate();
    for (var g = 1; g <= giorniMese; g++) {
        var data = new Date(anno, mese, g);
        var dataStr = anno + '-' +
            String(mese + 1).padStart(2, '0') + '-' +
            String(g).padStart(2, '0');

        var span = document.createElement('span');
        span.className = 'giorno';
        span.textContent = g;

        // startsWith perché dateOccupate a volte arriva con timestamp appeso
        var occupata = dateOccupate.some(function(d) {
            return d === dataStr || d.startsWith(dataStr);
        });

        var nomeGiorno = mappaGiorni[data.getDay()];
        var nonDisponibile = arrayGiorni.length > 0 && !arrayGiorni.includes(nomeGiorno);
        var passata = data < oggi && data.toDateString() !== oggi.toDateString();

        if (occupata || nonDisponibile) {
            span.classList.add('occupato');
            span.title = occupata ? 'Data già prenotata' : 'Giorno non disponibile per questo tour';
        } else if (passata) {
            span.style.opacity = '0.3';
            span.style.cursor = 'not-allowed';
        } else {
            span.onclick = (function(ds, el) {
                return function() {
                    document.querySelectorAll('.giorno.selezionato')
                        .forEach(function(e) { e.classList.remove('selezionato'); });
                    el.classList.add('selezionato');
                    document.getElementById('dataScelta').value = ds;
                    document.getElementById('dataVisibile').textContent = ds;
                };
            })(dataStr, span);
        }

        if (data.toDateString() === oggi.toDateString()) {
            span.classList.add('oggi');
        }

        cal.appendChild(span);
    }
}
disegnaCalendario();