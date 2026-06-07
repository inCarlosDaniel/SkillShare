"use strict";

// Controla a lista de conversas, filtros, avatares e modal de nova mensagem
document.addEventListener("DOMContentLoaded", function () {

    var rows = Array.prototype.slice.call(document.querySelectorAll(".conversation-row"));

    prepararFotosPerfil(document);


    if (typeof SockJS !== "undefined" && typeof StompJs !== "undefined") {

        var listaData = document.getElementById("listaData");
        var userId    = listaData ? parseInt(listaData.dataset.userId, 10) : 0;

        if (userId > 0) {

            var sc = new StompJs.Client({
                webSocketFactory: function () { return new SockJS("/ws"); },
                reconnectDelay: 5000,

                onConnect: function () {

                    sc.subscribe("/user/queue/mensagens", function (frame) {

                        var msg      = JSON.parse(frame.body);
                        var remId    = msg.remetenteId || 0;
                        var parceiroId = msg.proprio ? 0 : remId;

                        var row = parceiroId
                            ? document.querySelector(".conversation-row[data-parceiro-id='" + parceiroId + "']")
                            : null;

                        if (row) {


                            var p = row.querySelector(".conv-content-link p");
                            if (p) p.textContent = msg.texto;


                            var lista = row.parentNode;
                            if (lista && lista.firstChild !== row) {
                                lista.insertBefore(row, lista.firstChild);
                            }


                            if (!msg.proprio) {
                                row.classList.add("unread");
                                if (!row.querySelector(".unread-dot")) {
                                    var dot = document.createElement("span");
                                    dot.className = "unread-dot";
                                    dot.setAttribute("aria-label", "Mensagem não lida");
                                    var meta = row.querySelector(".conversation-meta");
                                    if (meta) meta.appendChild(dot);
                                }
                            }
                        } else if (!msg.proprio) {

                            window.location.reload();
                        }

                        atualizarBadge();
                    });
                }
            });

            sc.activate();
        }
    }


    var inputFiltro = document.getElementById("filtroConversas");

    if (inputFiltro && rows.length > 0) {

        inputFiltro.addEventListener("input", function () {

            var termo = inputFiltro.value.trim().toLowerCase();

            rows.forEach(function (row) {

                var nome = (row.querySelector("h2") || {}).textContent || "";

                var msg  = (row.querySelector("p")  || {}).textContent || "";

                var bate = !termo || nome.toLowerCase().includes(termo)

                                  || msg.toLowerCase().includes(termo);

                row.style.display = bate ? "" : "none";
            });
        });
    }


    rows.forEach(function (row) {

        row.addEventListener("click", function () {

            row.classList.remove("unread");

            var dot = row.querySelector(".unread-dot");

            if (dot) dot.remove();

            atualizarBadge();
        });
    });

    function atualizarBadge() {

        var naoLidas = document.querySelectorAll(".conversation-row.unread").length;

        var badge = document.getElementById("badgeNaoLidas");

        if (badge) {

            badge.textContent = naoLidas > 0 ? naoLidas : "";

            badge.style.display = naoLidas > 0 ? "" : "none";
        }
    }

    function prepararFotosPerfil(raiz) {

        var fotos = Array.prototype.slice.call(raiz.querySelectorAll(".conv-foto"));

        fotos.forEach(function (foto) {

            if (foto.dataset.avatarReady === "true") return;

            foto.dataset.avatarReady = "true";

            foto.addEventListener("load", function () {

                foto.style.display = "block";

                var fallbackId = foto.dataset.placeholderId || foto.id.replace("avi-", "avp-");

                var fallback = document.getElementById(fallbackId);

                if (fallback) fallback.style.display = "none";
            });

            foto.addEventListener("error", function () {

                foto.style.display = "none";

                var fallbackId = foto.dataset.placeholderId || foto.id.replace("avi-", "avp-");

                var fallback = document.getElementById(fallbackId);

                if (window.SkillShareAvatars) {
                    window.SkillShareAvatars.showDefault(fallback);
                } else if (fallback) {
                    fallback.style.display = "grid";
                }
            });

            if (foto.complete && foto.naturalWidth > 0) {

                foto.dispatchEvent(new Event("load"));
            }
        });
    }


    var btnNova = document.querySelector(".new-message-button");

    var overlay = document.getElementById("novaMsgOverlay");

    if (btnNova && overlay) {

        btnNova.addEventListener("click", function () {

            overlay.style.display = "flex";
        });
    }

    atualizarBadge();
});


      var filtro = document.getElementById("filtroParceiros");
      if (filtro) {
        filtro.addEventListener("input", function () {
          var termo = filtro.value.trim().toLowerCase();
          document.querySelectorAll("#listaParceiros .nova-msg-item").forEach(function (li) {
            var nome = (li.querySelector("strong") || {}).textContent || "";
            li.style.display = (!termo || nome.toLowerCase().includes(termo)) ? "" : "none";
          });
        });
      }
