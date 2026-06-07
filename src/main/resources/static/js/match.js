"use strict";

// Controla filtro, fotos e conexoes da pagina de match
document.addEventListener("DOMContentLoaded", function () {

    var cards = Array.prototype.slice.call(document.querySelectorAll(".match-card"));


    var inputFiltro = document.getElementById("matchFiltro");

    if (inputFiltro && cards.length > 0) {

        inputFiltro.addEventListener("input", function () {

            var termo = inputFiltro.value.trim().toLowerCase();

            var visiveis = 0;

            cards.forEach(function (card) {

                var nome = (card.querySelector("h2") || {}).textContent || "";

                var habilidade = (card.querySelector(".match-reason p") || {}).textContent || "";

                var bate = !termo || nome.toLowerCase().includes(termo)

                                  || habilidade.toLowerCase().includes(termo);

                card.style.display = bate ? "" : "none";

                if (bate) visiveis++;
            });


            var semResultado = document.getElementById("matchSemResultado");

            if (semResultado) {

                semResultado.style.display = (visiveis === 0 && termo) ? "" : "none";
            }
        });
    }



    cards.forEach(function (card) {

        var id = card.dataset.usuarioId;

        if (!id) return;

        var avatar = card.querySelector(".match-avatar");

        if (!avatar) return;

        var inicial = avatar.textContent.trim();


        var img = new Image();

        img.onload = function () {

            avatar.textContent = "";

            avatar.style.backgroundImage = "url(" + img.src + ")";

            avatar.style.backgroundSize  = "cover";

            avatar.style.backgroundPosition = "center";

            avatar.classList.add("match-avatar-foto");
        };

        img.src = "/usuarios/" + id + "/foto";
    });


    document.querySelectorAll(".connect-chat-button").forEach(function (btn) {

        btn.addEventListener("click", function (e) {

            e.preventDefault();

            var destId = btn.dataset.destId;

            var href   = btn.getAttribute("href") || ("/chat/" + destId);

            btn.textContent = "Conectando...";

            btn.style.opacity = "0.7";

            btn.style.pointerEvents = "none";


            var match  = document.cookie.match(/XSRF-TOKEN=([^;]+)/);

            var token  = match ? decodeURIComponent(match[1]) : "";


            fetch("/match/conectar/" + destId, {

                method: "POST",
                headers: { "X-XSRF-TOKEN": token }
            }).finally(function () {

                window.location.href = href;
            });
        });
    });


    if ("IntersectionObserver" in window) {

        var observer = new IntersectionObserver(function (entries) {

            entries.forEach(function (entry) {

                if (entry.isIntersecting) {

                    entry.target.classList.add("match-card-visivel");

                    observer.unobserve(entry.target);
                }
            });
        }, { threshold: 0.1 });

        cards.forEach(function (card) { observer.observe(card); });
    }
});
