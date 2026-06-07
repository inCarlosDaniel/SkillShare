"use strict";

// Controla abas, upload, filtro e exclusao de materiais
document.addEventListener("DOMContentLoaded", function () {


    function getCsrf() {

        var match = document.cookie.split("; ")

            .find(function (c) { return c.startsWith("XSRF-TOKEN="); });

        return match ? decodeURIComponent(match.split("=")[1]) : "";
    }


    var tabBtns   = Array.prototype.slice.call(document.querySelectorAll(".repo-tab-btn"));

    var tabPanels = Array.prototype.slice.call(document.querySelectorAll(".repo-tab-panel"));

    tabBtns.forEach(function (btn) {

        btn.addEventListener("click", function () {

            tabBtns.forEach(function (b)   { b.classList.remove("active"); });

            tabPanels.forEach(function (p) { p.classList.remove("active"); });

            btn.classList.add("active");

            var alvo = document.getElementById("tab-" + btn.dataset.tab);

            if (alvo) alvo.classList.add("active");
        });
    });


    var formUpload  = document.querySelector(".materials-heading");

    var inputArq    = formUpload ? formUpload.querySelector("input[type='file']") : null;

    var btnUpload   = formUpload ? formUpload.querySelector("button[type='submit']") : null;

    var nomePreview = document.getElementById("nomeArquivoPreview");

    if (inputArq && nomePreview) {

        inputArq.addEventListener("change", function () {

            nomePreview.textContent = inputArq.files[0] ? inputArq.files[0].name : "";
        });
    }

    if (formUpload && btnUpload) {

        formUpload.addEventListener("submit", function (e) {


            if (formUpload.dataset.enviando === "true") {

                e.preventDefault();

                return;
            }


            var titulo    = formUpload.querySelector("input[name='titulo']");

            var categoria = formUpload.querySelector("input[name='categoria']");

            var arquivo   = formUpload.querySelector("input[name='arquivo']");

            var valido    = true;

            [titulo, categoria, arquivo].forEach(function (campo) {

                if (!campo) return;

                if (!campo.value.trim() && campo.type !== "file") {

                    campo.classList.add("input-erro");

                    valido = false;
                } else if (campo.type === "file" && !campo.files.length) {

                    campo.classList.add("input-erro");

                    valido = false;
                } else {

                    campo.classList.remove("input-erro");
                }
            });

            if (!valido) {

                e.preventDefault();

                return;
            }

            formUpload.dataset.enviando = "true";

            btnUpload.disabled = true;

            btnUpload.textContent = "Enviando...";
        });


        formUpload.querySelectorAll("input").forEach(function (input) {

            input.addEventListener("input", function () {

                input.classList.remove("input-erro");
            });
        });
    }


    var fotoCard   = document.getElementById("fotoPerfilCard");

    var fotoImg    = document.getElementById("fotoPerfilImg");

    var btnExcluir = document.getElementById("btnExcluirFoto");

    if (fotoCard && fotoImg) {

        var testImg = new Image();

        testImg.onload = function () {

            fotoImg.src = "/usuarios/foto";

            fotoCard.style.display = "";
        };

        testImg.src = "/usuarios/foto";
    }

    if (btnExcluir) {

        btnExcluir.addEventListener("click", function () {

            if (!confirm("Excluir sua foto de perfil?")) return;

            fetch("/usuarios/foto/excluir", {

                method: "POST",
                headers: { "X-XSRF-TOKEN": getCsrf() }
            }).then(function (r) {

                if (r.ok) {

                    localStorage.removeItem("skillshare.profilePhoto");

                    if (fotoCard) fotoCard.style.display = "none";
                }
            });
        });
    }


    document.querySelectorAll(".delete-material-button").forEach(function (btn) {

        btn.addEventListener("click", function (e) {

            e.preventDefault();

            var form = btn.closest("form");

            if (!form) return;


            if (form.querySelector(".confirm-material")) return;

            var dialog = document.createElement("div");

            dialog.className = "confirm-material";

            dialog.innerHTML =
                '<p>Excluir este material?</p>' +
                '<div class="confirm-material-acoes">' +
                '  <button type="button" class="confirm-sim-mat">Excluir</button>' +
                '  <button type="button" class="confirm-nao-mat">Cancelar</button>' +
                '</div>';

            btn.insertAdjacentElement("afterend", dialog);

            dialog.querySelector(".confirm-sim-mat").addEventListener("click", function () {

                form.submit();
            });

            dialog.querySelector(".confirm-nao-mat").addEventListener("click", function () {

                dialog.remove();
            });
        });
    });


    var inputFiltro = document.getElementById("filtroMateriais");

    if (inputFiltro) {

        inputFiltro.addEventListener("input", function () {

            var termo = inputFiltro.value.trim().toLowerCase();

            document.querySelectorAll(".material-card").forEach(function (card) {

                var titulo = (card.querySelector("h2") || {}).textContent || "";

                var cat    = (card.querySelector("p")  || {}).textContent || "";

                var bate   = !termo || titulo.toLowerCase().includes(termo)

                                    || cat.toLowerCase().includes(termo);

                card.style.display = bate ? "" : "none";
            });
        });
    }
});
