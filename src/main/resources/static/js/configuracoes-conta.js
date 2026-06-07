"use strict";

// Controla validacoes e acoes da pagina de configuracao da conta
document.addEventListener("DOMContentLoaded", function () {


    document.querySelectorAll(".settings-link[href^='#']").forEach(function (link) {

        link.addEventListener("click", function (e) {

            e.preventDefault();

            var alvo = document.getElementById(link.getAttribute("href").slice(1));

            if (alvo) alvo.scrollIntoView({ behavior: "smooth", block: "start" });
        });
    });


    if (window.location.hash) {

        var alvo = document.getElementById(window.location.hash.slice(1));

        if (alvo) setTimeout(function () {

            alvo.scrollIntoView({ behavior: "smooth", block: "start" });
        }, 150);
    }


    document.querySelectorAll(".toggle-senha-btn").forEach(function (btn) {

        btn.addEventListener("click", function () {

            var campo = btn.previousElementSibling;

            if (!campo) return;

            var visivel = campo.type === "text";

            campo.type = visivel ? "password" : "text";

            btn.textContent = visivel ? "👁" : "🙈";

            btn.setAttribute("aria-label", visivel ? "Mostrar senha" : "Ocultar senha");
        });
    });


    var inputNova    = document.getElementById("novaSenha");

    var barra        = document.getElementById("forcaSenhaBarra");

    var textoForca   = document.getElementById("forcaSenhaTexto");

    if (inputNova && barra && textoForca) {

        var niveis = [
            { ate: 5,        label: "Muito fraca", cor: "#e74c3c", largura: "18%" },
            { ate: 7,        label: "Fraca",        cor: "#e67e22", largura: "38%" },
            { ate: 9,        label: "Razoável",     cor: "#f1c40f", largura: "58%" },
            { ate: 12,       label: "Boa",           cor: "#2ecc71", largura: "78%" },
            { ate: Infinity, label: "Forte",         cor: "#27ae60", largura: "100%" }
        ];

        inputNova.addEventListener("input", function () {

            var len = inputNova.value.length;

            if (len === 0) {

                barra.style.width = "0";

                textoForca.textContent = "";

                return;
            }
            var nivel = niveis.find(function (n) { return len <= n.ate; });

            barra.style.width      = nivel.largura;

            barra.style.background = nivel.cor;

            textoForca.textContent = nivel.label;

            textoForca.style.color = nivel.cor;
        });
    }


    var formDados = document.querySelector("#editar .settings-form");

    var formSenha = document.querySelector("#seguranca .settings-form");

    var alterado  = false;

    [formDados, formSenha].forEach(function (form) {

        if (!form) return;

        form.querySelectorAll("input").forEach(function (input) {

            input.addEventListener("input", function () { alterado = true; });
        });

        form.addEventListener("submit", function () { alterado = false; });
    });

    window.addEventListener("beforeunload", function (e) {

        if (alterado) {

            e.preventDefault();

            e.returnValue = "";
        }
    });


    var btnExcluir = document.querySelector(".settings-link.danger");

    if (btnExcluir) {

        btnExcluir.addEventListener("click", function (e) {

            e.preventDefault();

            if (document.getElementById("confirmExcluir")) return;

            var dialog = document.createElement("div");

            dialog.id = "confirmExcluir";

            dialog.className = "confirm-excluir-dialog";

            dialog.innerHTML =
                '<p>⚠️ <strong>Tem certeza?</strong> Sua conta será excluída permanentemente.</p>' +
                '<div class="confirm-excluir-acoes">' +
                '  <button type="button" id="confirmExcluirSim">Sim, excluir minha conta</button>' +
                '  <button type="button" id="confirmExcluirNao">Cancelar</button>' +
                '</div>';

            btnExcluir.insertAdjacentElement("afterend", dialog);

            document.getElementById("confirmExcluirSim").addEventListener("click", function () {

                var formExcluir = document.getElementById("formExcluirConta");

                if (formExcluir) formExcluir.submit();
            });

            document.getElementById("confirmExcluirNao").addEventListener("click", function () {

                dialog.remove();
            });
        });
    }
});
