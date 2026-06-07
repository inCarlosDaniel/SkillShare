"use strict";

// Controla abas, leitura e contador da pagina de notificacoes
document.addEventListener("DOMContentLoaded", function () {

    var rows = Array.prototype.slice.call(document.querySelectorAll(".notification-row:not(.empty-notification-row)"));


    var btnTodas    = document.getElementById("abaTodas");

    var btnNaoLidas = document.getElementById("abaNaoLidas");

    function filtrar(somenteNaoLidas) {

        rows.forEach(function (row) {

            var naoLida = row.classList.contains("unread");

            row.style.display = (!somenteNaoLidas || naoLida) ? "" : "none";
        });

        if (btnTodas && btnNaoLidas) {

            btnTodas.classList.toggle("aba-ativa",    !somenteNaoLidas);

            btnNaoLidas.classList.toggle("aba-ativa",  somenteNaoLidas);
        }

        atualizarVazio();
    }

    if (btnTodas)    btnTodas.addEventListener("click",    function () { filtrar(false); });

    if (btnNaoLidas) btnNaoLidas.addEventListener("click", function () { filtrar(true);  });


    rows.forEach(function (row) {

        row.addEventListener("click", function () {

            if (row.classList.contains("unread")) {

                row.classList.remove("unread");

                atualizarBadge();
            }
        });


        var timerLeitura;

        row.addEventListener("mouseenter", function () {

            timerLeitura = setTimeout(function () {

                row.classList.remove("unread");

                atualizarBadge();
            }, 2000);
        });

        row.addEventListener("mouseleave", function () {

            clearTimeout(timerLeitura);
        });
    });


    function atualizarBadge() {

        var naoLidas = document.querySelectorAll(".notification-row.unread").length;

        var badge = document.getElementById("badgeNaoLidas");

        if (badge) {

            badge.textContent = naoLidas > 0 ? naoLidas : "";

            badge.style.display = naoLidas > 0 ? "" : "none";
        }
        atualizarVazio();
    }


    function atualizarVazio() {

        var vazio = document.getElementById("notificacaoVazia");

        if (!vazio) return;

        var algumVisivel = rows.some(function (r) { return r.style.display !== "none"; });

        vazio.style.display = algumVisivel ? "none" : "";
    }

    atualizarBadge();
});
