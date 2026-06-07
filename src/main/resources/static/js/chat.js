"use strict";

// Controla a tela principal de conversa em tempo real
document.addEventListener("DOMContentLoaded", function () {

    var meta        = document.getElementById("chatData");

    var thread      = document.getElementById("chatThread");

    var form        = document.getElementById("chatForm");

    var input       = document.getElementById("chatInput");

    var btnEnviar   = form ? form.querySelector("button[type='submit']") : null;

    var btnRolar    = document.getElementById("btnRolarFim");

    var onlineDot   = document.getElementById("onlineDot");

    var statusTexto = document.getElementById("statusTexto");

    var typingEl    = document.getElementById("typingIndicator");

    var contador    = document.getElementById("chatContador");

    var destId    = meta ? parseInt(meta.dataset.destId, 10) : 0;

    var destFotoUrl = destId > 0 ? "/usuarios/" + destId + "/foto" : "";

    var LIMITE         = 500;

    var typingTimer    = null;

    var typingDebounce = null;


    if (typingEl) typingEl.style.display = "none";

    prepararFotosPerfil(document);

    scrollFim(false);


    if (input && contador) {

        input.addEventListener("input", function () {

            var len = input.value.length;

            contador.textContent = len + " / " + LIMITE;

            contador.classList.toggle("contador-aviso",  len > LIMITE * 0.85);

            contador.classList.toggle("contador-limite", len >= LIMITE);
        });
    }


    if (input) {

        input.addEventListener("input", function () {

            input.style.height = "auto";

            input.style.height = Math.min(input.scrollHeight, 120) + "px";
        });
    }


    if (thread && btnRolar) {

        thread.addEventListener("scroll", function () {

            var dist = thread.scrollHeight - thread.scrollTop - thread.clientHeight;

            btnRolar.style.display = dist > 120 ? "" : "none";
        });

        btnRolar.addEventListener("click", function () { scrollFim(true); });
    }


    if (typeof StompJs === "undefined" || typeof SockJS === "undefined" || destId === 0) {

        setStatus(false, "Sem tempo real");

        return;
    }

    var stompClient = new StompJs.Client({

        webSocketFactory: function () { return new SockJS("/ws"); },
        reconnectDelay: 5000,

        onConnect: function () {


            var presencaTimer = setTimeout(function () {

                if (statusTexto && statusTexto.textContent === "Verificando...") {

                    setStatus(false, "Inativo");
                }
            }, 5000);


            stompClient.subscribe("/user/queue/mensagens", function (frame) {

                var msg = JSON.parse(frame.body);

                adicionarBolha(msg.nome, msg.texto, msg.hora, msg.proprio);

                if (msg.proprio) {

                    input.value = "";

                    input.style.height = "";

                    if (contador) contador.textContent = "";

                    if (btnEnviar) { btnEnviar.disabled = false; btnEnviar.textContent = "➜"; }
                }
            });


            stompClient.subscribe("/user/queue/digitando", function () {

                if (!typingEl) return;

                typingEl.style.display = "";

                scrollFim(false);

                clearTimeout(typingTimer);

                typingTimer = setTimeout(function () {

                    typingEl.style.display = "none";
                }, 2500);
            });


            stompClient.subscribe("/user/queue/presenca", function (frame) {

                clearTimeout(presencaTimer);

                var data = JSON.parse(frame.body);

                if (data.online) {

                    setStatus(true, "Ativo");
                } else {

                    setStatus(false, "Inativo");
                }
            });


            stompClient.publish({

                destination: "/app/chat.verificarPresenca",
                body: JSON.stringify({ idDestinatario: destId })
            });
        },

        onDisconnect: function () {

            setStatus(false, "Reconectando...");

            if (typingEl) typingEl.style.display = "none";
        },

        onStompError: function () {

            setStatus(false, "Erro de conexão");
        }
    });

    stompClient.activate();


    function enviarMensagemHttp(texto) {

        if (!form) return;

        var formData = new FormData(form);
        formData.set("texto", texto);

        if (btnEnviar) { btnEnviar.disabled = true; btnEnviar.textContent = "..."; }

        fetch(form.action, {
            method: "POST",
            credentials: "same-origin",
            body: formData
        }).then(function (response) {
            if (response.redirected) {
                window.location.href = response.url;
            } else {
                window.location.reload();
            }
        }).catch(function () {
            window.location.reload();
        });
    }

    if (form) {

        form.addEventListener("submit", function (e) {

            var texto = input ? input.value.trim() : "";

            if (!texto || texto.length > LIMITE) {
                e.preventDefault();
                return;
            }

            if (stompClient && stompClient.connected) {
                e.preventDefault();

                if (btnEnviar) { btnEnviar.disabled = true; btnEnviar.textContent = "..."; }

                stompClient.publish({
                    destination: "/app/chat.enviar",
                    body: JSON.stringify({ texto: texto, idDestinatario: destId })
                });
            } else {
                e.preventDefault();
                enviarMensagemHttp(texto);
            }
        });
    }


    if (input) {

        input.addEventListener("keydown", function (e) {

            if (e.key === "Enter" && !e.shiftKey) {

                e.preventDefault();

                if (form && input.value.trim()) form.requestSubmit();
            }
        });


        input.addEventListener("input", function () {

            if (!stompClient || !stompClient.connected || !input.value.trim()) return;

            clearTimeout(typingDebounce);

            typingDebounce = setTimeout(function () {

                stompClient.publish({

                    destination: "/app/chat.digitando",
                    body: JSON.stringify({ idDestinatario: destId })
                });
            }, 500);
        });
    }



    function adicionarBolha(nome, texto, hora, proprio) {

        if (typingEl) typingEl.style.display = "none";

        var row = document.createElement("div");

        row.className = "bubble-row " + (proprio ? "sent" : "received");

        if (!proprio) {

            var stack = document.createElement("span");

            stack.className = "chat-avatar-stack chat-avatar-stack-small";

            var av = document.createElement("span");

            av.className = "bubble-avatar";

            av.id = "live-avatar-" + Date.now() + "-" + Math.floor(Math.random() * 10000);

            av.textContent = nome ? nome.charAt(0).toUpperCase() : "U";

            stack.appendChild(av);

            if (destFotoUrl) {

                var foto = document.createElement("img");

                foto.className = "bubble-avatar bubble-foto js-profile-photo";

                foto.src = destFotoUrl;

                foto.alt = nome || "Usuario";

                foto.dataset.placeholderId = av.id;

                stack.appendChild(foto);
            }

            row.appendChild(stack);
        }

        var art = document.createElement("article");

        art.className = "chat-bubble " + (proprio ? "sent" : "received");

        var p = document.createElement("p");

        p.textContent = texto;

        var t = document.createElement("time");

        t.textContent = hora || agora();

        art.appendChild(p);

        art.appendChild(t);

        row.appendChild(art);

        if (typingEl && typingEl.parentNode === thread) {

            thread.insertBefore(row, typingEl);
        } else {

            thread.appendChild(row);
        }

        if (typeof window.gsapEntrada === "function") window.gsapEntrada(row, proprio ? "direita" : "esquerda");

        prepararFotosPerfil(row);

        var perto = thread.scrollHeight - thread.scrollTop - thread.clientHeight < 160;

        if (perto || proprio) scrollFim(true);
    }

    function scrollFim(suave) {

        if (!thread) return;

        thread.scrollTo({ top: thread.scrollHeight, behavior: suave ? "smooth" : "auto" });
    }

    function setStatus(online, texto) {

        if (statusTexto) statusTexto.textContent = texto;

        if (onlineDot) {

            onlineDot.classList.remove("ativo", "inativo");

            onlineDot.classList.add(online ? "ativo" : "inativo");
        }
    }

    function agora() {

        var d = new Date();

        return String(d.getHours()).padStart(2, "0") + ":" + String(d.getMinutes()).padStart(2, "0");
    }

    function prepararFotosPerfil(raiz) {

        var fotos = Array.prototype.slice.call(raiz.querySelectorAll(".js-profile-photo"));

        fotos.forEach(function (foto) {

            if (foto.dataset.avatarReady === "true") return;

            foto.dataset.avatarReady = "true";

            foto.addEventListener("load", function () {

                foto.classList.add("is-loaded");

                foto.style.display = "block";

                var fallback = document.getElementById(foto.dataset.placeholderId || "");

                if (fallback) fallback.style.display = "none";
            });

            foto.addEventListener("error", function () {

                foto.classList.remove("is-loaded");

                foto.style.display = "none";

                var fallback = document.getElementById(foto.dataset.placeholderId || "");

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
});
