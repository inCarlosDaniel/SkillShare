"use strict";

// Controla busca, criacao e confirmacoes da pagina de grupos
document.addEventListener("DOMContentLoaded", function () {










    var campoBusca = document.getElementById("campoBuscaGrupos");

    var cards      = Array.prototype.slice.call(
        document.querySelectorAll(".descobrir-card")

    );

    var secaoDescobrir = document.getElementById("secaoDescobrir");

    if (campoBusca && cards.length > 0) {

        campoBusca.addEventListener("input", function () {

            var termo = campoBusca.value.trim().toLowerCase();

            var visiveis = 0;

            cards.forEach(function (card) {

                var nome = (card.querySelector("h3") || {}).textContent || "";

                var materia = (card.querySelector(".grupo-tag") || {}).textContent || "";

                var bate = termo === "" || nome.toLowerCase().includes(termo)

                                       || materia.toLowerCase().includes(termo);

                card.style.display = bate ? "" : "none";

                if (bate) visiveis++;


                destacarTexto(card.querySelector("h3"), termo);
            });


            var contador = document.getElementById("contadorDescobrir");

            if (contador) {

                contador.textContent = termo === ""
                    ? cards.length + " grupo(s)"
                    : visiveis + " resultado(s) para " + campoBusca.value.trim();
            }


            if (secaoDescobrir) {

                secaoDescobrir.style.display =
                    (visiveis === 0 && termo !== "") ? "none" : "";
            }
        });
    }


    function destacarTexto(el, termo) {

        if (!el) return;


        var original = el.dataset.textoOriginal || el.textContent;

        el.dataset.textoOriginal = original;

        if (!termo) {

            el.textContent = original;

            return;
        }

        var idx = original.toLowerCase().indexOf(termo);

        if (idx === -1) {

            el.textContent = original;

            return;
        }

        el.innerHTML =
            escapeHtml(original.substring(0, idx)) +
            '<mark style="background:#fef08a;border-radius:3px;padding:0 1px;">' +
            escapeHtml(original.substring(idx, idx + termo.length)) +
            "</mark>" +
            escapeHtml(original.substring(idx + termo.length));
    }

    function escapeHtml(str) {

        return str
            .replace(/&/g, "&amp;")

            .replace(/</g, "&lt;")

            .replace(/>/g, "&gt;");
    }








    var formCriar = document.getElementById("formCriarGrupo");

    if (formCriar) {

        formCriar.addEventListener("submit", function (e) {

            limparErros(formCriar);

            var valido = true;

            var campos = [
                { id: "inputNomeGrupo",    msg: "Informe um nome para o grupo."  },
                { id: "inputMateriaGrupo", msg: "Informe a matéria do grupo."    },
                { id: "inputObjetivoGrupo",msg: "Informe o objetivo do grupo."   }
            ];

            campos.forEach(function (c) {

                var el = document.getElementById(c.id);

                if (!el) return;

                if (el.value.trim() === "") {

                    mostrarErro(el, c.msg);

                    valido = false;

                } else if (el.maxLength > 0 && el.value.length >= el.maxLength) {

                    mostrarErro(el, "Máximo de " + el.maxLength + " caracteres atingido.");

                    valido = false;
                }
            });

            if (!valido) e.preventDefault();
        });


        formCriar.querySelectorAll("input[type='text']").forEach(function (input) {

            input.addEventListener("input", function () {

                var msg = input.parentElement.querySelector(".erro-campo");

                if (msg) msg.remove();

                input.classList.remove("input-erro");

                if (input.maxLength > 0 && input.value.length >= input.maxLength) {

                    mostrarErro(input, "Máximo de " + input.maxLength + " caracteres atingido.");
                }
            });
        });
    }

    function mostrarErro(input, mensagem) {

        input.classList.add("input-erro");

        var span = document.createElement("span");

        span.className = "erro-campo";

        span.textContent = mensagem;

        input.insertAdjacentElement("afterend", span);
    }

    function limparErros(form) {

        form.querySelectorAll(".erro-campo").forEach(function (el) { el.remove(); });

        form.querySelectorAll(".input-erro").forEach(function (el) {

            el.classList.remove("input-erro");
        });
    }








    document.querySelectorAll(".btn-apagar").forEach(function (btn) {

        btn.addEventListener("click", function (e) {

            e.preventDefault();

            var item = btn.closest(".grupo-item");

            if (!item) return;


            if (item.querySelector(".confirm-dialog")) return;

            var nomeGrupo = (item.querySelector(".grupo-info h3") || {}).textContent || "este grupo";

            var form = btn.closest("form");

            var dialog = document.createElement("div");

            dialog.className = "confirm-dialog";

            dialog.innerHTML =
                '<p>Apagar <strong>' + escapeHtml(nomeGrupo) + '</strong>? Esta ação não pode ser desfeita.</p>' +
                '<div class="confirm-dialog-acoes">' +
                '  <button type="button" class="confirm-sim">Sim, apagar</button>' +
                '  <button type="button" class="confirm-nao">Cancelar</button>' +
                '</div>';

            item.appendChild(dialog);

            dialog.querySelector(".confirm-sim").addEventListener("click", function () {

                form.submit();
            });

            dialog.querySelector(".confirm-nao").addEventListener("click", function () {

                dialog.remove();
            });
        });
    });

});
