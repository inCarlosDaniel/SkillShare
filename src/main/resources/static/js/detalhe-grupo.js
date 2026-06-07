"use strict";

// Controla interacoes do detalhe do grupo, incluindo quiz, comentarios e ordenacao
document.addEventListener("DOMContentLoaded", function () {


    document.querySelectorAll(".mural-tab").forEach(function (tab) {

        tab.addEventListener("click", function () {

            var tabId = tab.dataset.tab;

            if (!tabId) return;

            document.querySelectorAll(".mural-tab-content").forEach(function (c) {

                c.style.display = "none";
            });

            document.querySelectorAll(".mural-tab").forEach(function (t) {

                t.classList.remove("active");
            });

            var alvo = document.getElementById(tabId);

            if (alvo) alvo.style.display = "block";

            tab.classList.add("active");
        });
    });


    var textarea = document.querySelector(".mural-textarea");

    if (textarea && textarea.tagName === "TEXTAREA") {

        var LIMITE = 1000;

        var contador = document.createElement("span");

        contador.className = "mural-contador";

        contador.textContent = "0 / " + LIMITE;

        textarea.insertAdjacentElement("afterend", contador);

        textarea.addEventListener("input", function () {

            var len = textarea.value.length;

            contador.textContent = len + " / " + LIMITE;

            contador.classList.toggle("contador-aviso",  len > LIMITE * 0.85);

            contador.classList.toggle("contador-limite", len >= LIMITE);
        });
    }

    function getCsrf() {
        var m = document.cookie.match(/XSRF-TOKEN=([^;]+)/);
        return m ? decodeURIComponent(m[1]) : "";
    }

    function criarElemento(tag, className, texto) {
        var el = document.createElement(tag);
        if (className) el.className = className;
        if (texto !== undefined && texto !== null) el.textContent = texto;
        return el;
    }

    function parseQuiz(container) {
        try {
            var quiz = JSON.parse(container.dataset.quizJson || "{}");
            if (!quiz || !Array.isArray(quiz.perguntas)) return null;
            return quiz;
        } catch (e) {
            return null;
        }
    }

    function renderizarQuiz(container) {
        var quiz = parseQuiz(container);
        var titulo = container.querySelector(".quiz-title");
        if (titulo && quiz && quiz.titulo) titulo.textContent = quiz.titulo;

        var area = container.querySelector(".quiz-form-area");
        if (!area || area.dataset.renderizado === "true") return;

        area.innerHTML = "";

        if (!quiz) {
            area.appendChild(criarElemento("p", "quiz-erro", "Não foi possível carregar este quiz."));
            area.dataset.renderizado = "true";
            return;
        }

        var form = criarElemento("form", "quiz-form");

        quiz.perguntas.forEach(function (pergunta, perguntaIndex) {
            var bloco = criarElemento("fieldset", "quiz-pergunta");
            bloco.appendChild(criarElemento("legend", null, (perguntaIndex + 1) + ". " + (pergunta.enunciado || "Pergunta")));

            (pergunta.alternativas || []).forEach(function (alternativa, alternativaIndex) {
                var id = "quiz-" + Math.random().toString(36).slice(2) + "-" + perguntaIndex + "-" + alternativaIndex;
                var label = criarElemento("label", "quiz-alternativa");
                var input = document.createElement("input");
                input.type = "radio";
                input.name = "pergunta-" + perguntaIndex;
                input.value = String(alternativaIndex);
                input.id = id;
                label.setAttribute("for", id);
                label.appendChild(input);
                label.appendChild(criarElemento("span", null, alternativa));
                bloco.appendChild(label);
            });

            form.appendChild(bloco);
        });

        var resultado = criarElemento("p", "quiz-resultado");
        var botao = criarElemento("button", "quiz-corrigir-btn", "Corrigir");
        botao.type = "submit";

        form.appendChild(botao);
        form.appendChild(resultado);

        form.addEventListener("submit", function (event) {
            event.preventDefault();

            var pontos = 0;
            var total = quiz.perguntas.length;

            quiz.perguntas.forEach(function (pergunta, perguntaIndex) {
                var marcada = form.querySelector("input[name='pergunta-" + perguntaIndex + "']:checked");
                if (marcada && Number(marcada.value) === Number(pergunta.correta)) pontos++;
            });

            resultado.textContent = "Você acertou " + pontos + " de " + total + " pergunta" + (total === 1 ? "." : "s.");
            resultado.classList.add("visivel");
        });

        area.appendChild(form);
        area.dataset.renderizado = "true";
    }

    document.querySelectorAll(".quiz-post").forEach(function (container) {
        var quiz = parseQuiz(container);
        var titulo = container.querySelector(".quiz-title");
        if (titulo && quiz && quiz.titulo) titulo.textContent = quiz.titulo;

        var btn = container.querySelector(".quiz-toggle-btn");
        var area = container.querySelector(".quiz-form-area");
        if (!btn || !area) return;

        btn.addEventListener("click", function () {
            var abrir = area.hidden;
            if (abrir) renderizarQuiz(container);
            area.hidden = !abrir;
            btn.setAttribute("aria-expanded", String(abrir));
            btn.textContent = abrir ? "Ocultar Quiz" : "Responder Quiz";
        });
    });


    function sortarCards(modo) {
        var lista = document.querySelector(".mural-posts-lista");
        if (!lista) return;
        var cards = Array.prototype.slice.call(lista.querySelectorAll(".mural-post-card"));
        if (modo === "relevante") {
            cards.sort(function (a, b) {
                return (parseInt(b.dataset.upvoteCount, 10) || 0) - (parseInt(a.dataset.upvoteCount, 10) || 0);
            });
        } else {
            cards.sort(function (a, b) {
                return new Date(b.dataset.dataPub) - new Date(a.dataset.dataPub);
            });
        }
        cards.forEach(function (c) { lista.appendChild(c); });
    }

    document.querySelectorAll(".mural-sort-btn").forEach(function (btn) {
        btn.addEventListener("click", function () {
            document.querySelectorAll(".mural-sort-btn").forEach(function (b) { b.classList.remove("active"); });
            btn.classList.add("active");
            sortarCards(btn.dataset.sort);
        });
    });


    document.querySelectorAll(".mural-post-card").forEach(function (card) {

        var postId = card.dataset.postId;
        if (!postId) return;


        fetch("/postagens/" + postId + "/upvotes", { credentials: "same-origin" })
            .then(function (r) { return r.json(); })
            .then(function (data) {
                var span = card.querySelector(".upvote-count");
                if (span) span.textContent = data.upvotes || 0;
                card.dataset.upvoteCount = data.upvotes || 0;
                var btn = card.querySelector(".mural-upvote-btn");
                if (data.upvotou && btn) btn.classList.add("ativo");
            }).catch(function () {});


        fetch("/postagens/" + postId + "/comentarios", { credentials: "same-origin" })
            .then(function (r) { return r.json(); })
            .then(function (lista) {
                var countEl = card.querySelector(".comment-count");
                if (countEl) countEl.textContent = lista.length;
                var listEl = card.querySelector(".comentarios-lista");
                if (!listEl) return;
                if (lista.length > 0) {
                    listEl.innerHTML = "";
                    lista.forEach(function (c) { listEl.appendChild(criarComentarioEl(c.autor, c.texto)); });
                }
            }).catch(function () {});
    });


    document.querySelectorAll(".mural-upvote-btn").forEach(function (btn) {

        btn.addEventListener("click", function () {

            var card   = btn.closest(".mural-post-card");
            var postId = card ? card.dataset.postId : null;
            if (!postId) return;

            var ativo  = btn.classList.contains("ativo");
            var metodo = ativo ? "DELETE" : "POST";

            fetch("/postagens/" + postId + "/upvote", {
                method: metodo,
                credentials: "same-origin",
                headers: { "X-XSRF-TOKEN": getCsrf() }
            }).then(function (r) { return r.json(); })
              .then(function (data) {
                  btn.classList.toggle("ativo", data.upvotou);
                  var span = btn.querySelector(".upvote-count");
                  if (span) span.textContent = data.upvotes || 0;
                  if (card) card.dataset.upvoteCount = data.upvotes || 0;
              }).catch(function () {});
        });
    });


    document.querySelectorAll(".mural-comment-toggle-btn").forEach(function (btn) {

        btn.addEventListener("click", function () {

            var card = btn.closest(".mural-post-card");
            var area = card ? card.querySelector(".mural-comentarios") : null;
            if (!area) return;

            var aberto = area.hidden;
            area.hidden = !aberto;
            btn.setAttribute("aria-expanded", String(aberto));

            if (aberto) {
                var input = area.querySelector(".comentario-input");
                if (input) input.focus();
            }
        });
    });


    document.querySelectorAll(".comentario-enviar-btn").forEach(function (btn) {
        btn.addEventListener("click", function () { enviarComentario(btn); });
    });

    document.querySelectorAll(".comentario-input").forEach(function (input) {
        input.addEventListener("keydown", function (e) {
            if (e.key === "Enter" && !e.shiftKey) {
                e.preventDefault();
                enviarComentario(input.nextElementSibling);
            }
        });
    });

    document.querySelectorAll("[data-material-picker]").forEach(function (picker) {
        var input = picker.querySelector(".material-search-input");
        var hidden = picker.querySelector("[data-material-value]");
        var options = Array.prototype.slice.call(picker.querySelectorAll(".material-option"));
        var empty = picker.querySelector(".material-empty");
        var form = picker.closest("form");
        var card = picker.closest(".group-actions-card");

        function openPicker() {
            picker.classList.add("is-open");
            if (card) card.classList.add("has-open-dropdown");
            input.setAttribute("aria-expanded", "true");
        }

        function closePicker() {
            picker.classList.remove("is-open");
            if (card) card.classList.remove("has-open-dropdown");
            input.setAttribute("aria-expanded", "false");
        }

        function normalize(text) {
            return (text || "")
                .toLowerCase()
                .normalize("NFD")
                .replace(/[\u0300-\u036f]/g, "");
        }

        function filterOptions() {
            var query = normalize(input.value);
            var visibleCount = 0;

            options.forEach(function (option) {
                var matches = normalize(option.textContent).indexOf(query) !== -1;
                option.hidden = !matches;
                if (matches) visibleCount += 1;
            });

            if (empty) empty.hidden = visibleCount > 0;
        }

        function selectOption(option) {
            options.forEach(function (item) {
                item.classList.toggle("is-selected", item === option);
                item.setAttribute("aria-selected", String(item === option));
            });

            hidden.value = option.dataset.materialId || "";
            input.value = option.textContent.trim();
            input.setCustomValidity("");
            filterOptions();
            closePicker();
        }

        if (!input || !hidden) return;

        options.forEach(function (option) {
            option.addEventListener("click", function () {
                selectOption(option);
            });
        });

        input.addEventListener("focus", function () {
            openPicker();
            filterOptions();
        });

        input.addEventListener("click", function () {
            openPicker();
            filterOptions();
        });

        input.addEventListener("input", function () {
            openPicker();
            hidden.value = "";
            options.forEach(function (option) {
                option.classList.remove("is-selected");
                option.setAttribute("aria-selected", "false");
            });
            input.setCustomValidity("");
            filterOptions();
        });

        input.addEventListener("keydown", function (event) {
            if (event.key === "Escape") {
                closePicker();
                return;
            }

            if (event.key !== "Enter") return;

            var firstVisible = options.find(function (option) {
                return !option.hidden;
            });

            if (firstVisible) {
                event.preventDefault();
                selectOption(firstVisible);
            }
        });

        if (form) {
            form.addEventListener("submit", function (event) {
                if (hidden.value) return;

                event.preventDefault();
                input.setCustomValidity("Selecione um material da lista.");
                input.reportValidity();
            });
        }

        filterOptions();
    });

    document.addEventListener("click", function (event) {
        document.querySelectorAll("[data-material-picker].is-open").forEach(function (picker) {
            if (!picker.contains(event.target)) {
                picker.classList.remove("is-open");
                var card = picker.closest(".group-actions-card");
                if (card) card.classList.remove("has-open-dropdown");
                var input = picker.querySelector(".material-search-input");
                if (input) input.setAttribute("aria-expanded", "false");
            }
        });
    });

    function enviarComentario(btn) {

        var area   = btn ? btn.closest(".comentario-form") : null;
        var input  = area ? area.querySelector(".comentario-input") : null;
        var card   = btn ? btn.closest(".mural-post-card") : null;
        var postId = card ? card.dataset.postId : null;

        if (!input || !input.value.trim() || !postId) return;

        var texto = input.value.trim();

        fetch("/postagens/" + postId + "/comentar", {
            method: "POST",
            credentials: "same-origin",
            headers: { "Content-Type": "application/json", "X-XSRF-TOKEN": getCsrf() },
            body: JSON.stringify({ texto: texto })
        }).then(function (r) { return r.json(); })
          .then(function (data) {
              var lista  = card.querySelector(".comentarios-lista");
              var vazio  = lista ? lista.querySelector(".comentarios-vazio") : null;
              if (vazio) vazio.remove();
              if (lista) lista.appendChild(criarComentarioEl(data.autor, texto));
              var countEl = card.querySelector(".comment-count");
              if (countEl) countEl.textContent = data.total || 0;
              input.value = "";
              input.focus();
          }).catch(function () {});
    }

    function criarComentarioEl(autor, texto) {
        var item = document.createElement("div");
        item.className = "comentario-item";
        item.innerHTML = "<strong>" + (autor || "Usuário") + ":</strong> " +
                         document.createElement("span").appendChild(document.createTextNode(texto)).parentNode.innerHTML;
        return item;
    }


    document.querySelectorAll(".mural-tab-content form").forEach(function (form) {
        form.addEventListener("submit", function (event) {
            if (event.defaultPrevented) return;

            var btn = form.querySelector('[type="submit"]');
            if (btn) btn.disabled = true;
        });
    });


    if (window.location.hash === "#mural") {

        var mural = document.querySelector(".mural-section");

        if (mural) setTimeout(function () {

            mural.scrollIntoView({ behavior: "smooth", block: "start" });
        }, 200);
    }
});
