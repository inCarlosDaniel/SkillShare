"use strict";

// Controla o widget flutuante de chat e sua conexao WebSocket
(function () {

    var widgetParceiroId   = 0;
    var widgetParceiroNome = "";
    var stompClient        = null;
    var naoLidos           = 0;

    prepararFotosWidget(document);


    function iniciar() {

        if (typeof SockJS === "undefined" || typeof StompJs === "undefined") {
            setTimeout(iniciar, 200);
            return;
        }

        if (_widgetCurrentUserId === 0) return;

        stompClient = new StompJs.Client({
            webSocketFactory: function () { return new SockJS("/ws"); },
            reconnectDelay: 5000,

            onConnect: function () {

                stompClient.subscribe("/user/queue/mensagens", function (frame) {

                    var msg = JSON.parse(frame.body);
                    var parceiroId = msg.proprio ? msg.destinatarioId : msg.remetenteId;

                    if (parceiroId && parceiroId === widgetParceiroId && !msg.proprio) {
                        adicionarBolha(msg.nome, msg.texto, msg.hora, false);
                    }

                    atualizarPreview(parceiroId, msg.texto, msg.proprio);

                    if (!msg.proprio && parceiroId !== widgetParceiroId) {
                        naoLidos++;
                        atualizarBadge();
                    }
                });
            }
        });

        stompClient.activate();
    }


    window.widgetAbrirChat = function (parceiroId, parceiroNome) {

        widgetParceiroId   = parseInt(parceiroId, 10);
        widgetParceiroNome = parceiroNome || "Usuário";

        var nomEl = document.getElementById("widgetChatNome");
        if (nomEl) nomEl.textContent = widgetParceiroNome;

        var lista = document.getElementById("widgetLista");
        var chat  = document.getElementById("widgetChat");
        if (lista) lista.style.display = "none";
        if (chat)  chat.style.display  = "flex";


        var thread = document.getElementById("widgetThread");
        if (thread) thread.innerHTML = "";

        fetch("/api/chat-historico/" + widgetParceiroId, { credentials: "same-origin" })
            .then(function (r) { return r.json(); })
            .then(function (msgs) {

                msgs.forEach(function (m) {
                    adicionarBolha(m.nome, m.texto, m.hora, m.proprio);
                });
                rolarFim();
            })
            .catch(function () {});


        var inp = document.getElementById("widgetInput");
        if (inp) setTimeout(function () { inp.focus(); }, 100);
    };


    window.widgetVoltarLista = function () {

        widgetParceiroId = 0;

        var lista = document.getElementById("widgetLista");
        var chat  = document.getElementById("widgetChat");
        if (lista) lista.style.display = "";
        if (chat)  chat.style.display  = "none";
    };


    window.widgetEnviar = function () {

        var inp = document.getElementById("widgetInput");
        if (!inp) return;

        var texto = inp.value.trim();
        if (!texto || widgetParceiroId === 0) return;
        if (!stompClient || !stompClient.connected) return;

        stompClient.publish({
            destination: "/app/chat.enviar",
            body: JSON.stringify({ texto: texto, idDestinatario: widgetParceiroId })
        });

        inp.value = "";
    };


    function adicionarBolha(nome, texto, hora, proprio) {

        var thread = document.getElementById("widgetThread");
        if (!thread) return;

        var row = document.createElement("div");
        row.style.cssText = "display:flex;align-items:flex-end;gap:6px;" +
                            (proprio ? "flex-direction:row-reverse;" : "");

        if (!proprio) {
            var stack = document.createElement("span");
            stack.className = "widget-avatar-stack";
            stack.style.cssText = "width:24px;height:24px;border-radius:50%;background:#4169dc;" +
                                  "color:#fff;font-size:11px;font-weight:900;display:grid;place-items:center;flex-shrink:0;position:relative;overflow:hidden;";

            var av = document.createElement("span");
            av.className = "widget-avatar-fallback";
            av.id = "widgetLiveAvatarFallback-" + Date.now() + "-" + Math.floor(Math.random() * 10000);
            av.textContent = nome ? nome.charAt(0).toUpperCase() : "U";
            stack.appendChild(av);

            if (widgetParceiroId > 0) {
                var img = document.createElement("img");
                img.className = "widget-avatar-photo js-widget-photo";
                img.style.display = "none";
                img.alt = nome || "Usuario";
                img.dataset.placeholderId = av.id;
                img.src = "/usuarios/" + widgetParceiroId + "/foto";
                stack.appendChild(img);
            }

            row.appendChild(stack);
        }

        var bub = document.createElement("div");
        bub.style.cssText = "max-width:75%;padding:7px 10px;border-radius:12px;font-size:13px;line-height:1.4;" +
                            (proprio
                                ? "background:#4169dc;color:#fff;border-bottom-right-radius:3px;"
                                : "background:#f0f2f7;color:#202333;border-bottom-left-radius:3px;");

        var p = document.createElement("p");
        p.style.margin = "0 0 2px";
        p.textContent  = texto;

        var t = document.createElement("time");
        t.style.cssText = "font-size:10px;opacity:.65;display:block;text-align:" +
                          (proprio ? "right" : "left") + ";";
        t.textContent = hora || "";

        bub.appendChild(p);
        bub.appendChild(t);
        row.appendChild(bub);
        thread.appendChild(row);

        prepararFotosWidget(row);

        rolarFim();
    }

    function prepararFotosWidget(raiz) {

        var fotos = Array.prototype.slice.call(raiz.querySelectorAll(".js-widget-photo"));

        fotos.forEach(function (foto) {

            if (foto.dataset.avatarReady === "true") return;

            foto.dataset.avatarReady = "true";

            foto.addEventListener("load", function () {

                foto.style.display = "block";

                var fallback = document.getElementById(foto.dataset.placeholderId || "");

                if (fallback) fallback.style.display = "none";
            });

            foto.addEventListener("error", function () {

                foto.style.display = "none";

                var fallback = document.getElementById(foto.dataset.placeholderId || "");

                if (window.SkillShareAvatars) {
                    window.SkillShareAvatars.showDefault(fallback);
                } else if (fallback) {
                    fallback.style.display = "grid";
                }
            });

            if (foto.complete) {
                if (foto.naturalWidth > 0) {
                    foto.dispatchEvent(new Event("load"));
                } else {
                    foto.dispatchEvent(new Event("error"));
                }
            }
        });
    }

    function atualizarPreview(parceiroId, texto, proprio) {

        if (!parceiroId) return;

        var item = document.querySelector(".widget-conv-item[data-parceiro-id='" + parceiroId + "']");
        if (!item) return;

        var preview = item.querySelector("div > span:not(.widget-avatar-photo):not(.widget-avatar-fallback)");
        if (!preview) {
            preview = item.querySelector("div span");
        }

        if (preview) {
            preview.textContent = texto;
        }

        if (item.parentNode && item.parentNode.firstChild !== item) {
            item.parentNode.insertBefore(item, item.parentNode.firstChild);
        }
    }

    function rolarFim() {
        var thread = document.getElementById("widgetThread");
        if (thread) thread.scrollTop = thread.scrollHeight;
    }

    function atualizarBadge() {
        
        var badge = document.getElementById("widgetBadge");

        if (!badge) return;

        if (naoLidos > 0) {

            badge.textContent = naoLidos > 9 ? "9+" : naoLidos;

            badge.style.display = "";

        } else {

            badge.style.display = "none";

        }
    }


    var tab = document.getElementById("floatingChatTab");

    if (tab) {

        tab.addEventListener("click", function () {

            naoLidos = 0;

            atualizarBadge();

        });
    }


    var _origEnviar = window.widgetEnviar;
    window.widgetEnviar = function () {

        var inp   = document.getElementById("widgetInput");

        var texto = inp ? inp.value.trim() : "";

        _origEnviar();

        if (texto && widgetParceiroId !== 0) {

            var agora = new Date();
            var hora  = String(agora.getHours()).padStart(2, "0") + ":" +
                        String(agora.getMinutes()).padStart(2, "0");

            adicionarBolha("Eu", texto, hora, true);
        }
    };

    iniciar();
})();
