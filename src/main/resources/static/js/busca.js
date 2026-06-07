"use strict";

// Controla filtros, busca, destaque de termo e avatares da pagina de busca
document.addEventListener("DOMContentLoaded", function () {

    var inputBusca  = document.getElementById("inputBusca");

    var btnLimpar   = document.getElementById("btnLimparBusca");

    var tipoHidden  = document.getElementById("tipoHidden");

    var formBusca   = document.getElementById("formBusca");

    prepararFotosBusca(document);




    document.querySelectorAll(".filter-pill[data-tipo]").forEach(function (pill) {

        pill.addEventListener("click", function (e) {

            e.preventDefault();

            if (tipoHidden) tipoHidden.value = pill.dataset.tipo;

            document.querySelectorAll(".filter-pill").forEach(function (p) {

                p.classList.toggle("active", p === pill);
            });

            if (formBusca) formBusca.submit();
        });
    });


    if (inputBusca && btnLimpar) {


        inputBusca.addEventListener("input", function () {

            btnLimpar.style.display = inputBusca.value ? "" : "none";
        });

        btnLimpar.addEventListener("click", function () {

            inputBusca.value = "";

            btnLimpar.style.display = "none";

            inputBusca.focus();


            if (formBusca) formBusca.submit();
        });


        btnLimpar.style.display = inputBusca.value ? "" : "none";
    }


    var termo = (inputBusca ? inputBusca.value : "").trim().toLowerCase();

    if (termo) {

        document.querySelectorAll(".result-row h1").forEach(function (h1) {

            var original = h1.textContent;

            var idx = original.toLowerCase().indexOf(termo);

            if (idx === -1) return;

            h1.innerHTML =
                escapeHtml(original.slice(0, idx)) +
                '<mark class="busca-destaque">' +
                escapeHtml(original.slice(idx, idx + termo.length)) +
                "</mark>" +
                escapeHtml(original.slice(idx + termo.length));
        });
    }

    function escapeHtml(str) {

        return str.replace(/&/g,"&amp;").replace(/</g,"&lt;").replace(/>/g,"&gt;");
    }

    function prepararFotosBusca(raiz) {

        var fotos = Array.prototype.slice.call(raiz.querySelectorAll(".js-busca-foto"));

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

            if (foto.complete && foto.naturalWidth > 0) {

                foto.dispatchEvent(new Event("load"));
            }
        });
    }


    if (inputBusca && !inputBusca.value) {

        inputBusca.focus();
    }
});
