"use strict";

// Controla postagens, anexos, upvotes e comentarios do feed
function getCsrfToken() {

    var m = document.cookie.match(/XSRF-TOKEN=([^;]+)/);

    return m ? decodeURIComponent(m[1]) : "";
}

document.addEventListener("DOMContentLoaded", function () {


    var inputArquivo = document.getElementById("feedFileInput");

    var nomeArquivo  = document.getElementById("feedFileName");

    if (inputArquivo && nomeArquivo) {

        inputArquivo.addEventListener("change", function () {

            nomeArquivo.textContent = inputArquivo.files[0] ? inputArquivo.files[0].name : "";
        });
    }

    var textarea = document.querySelector(".composer-input");

    if (textarea) {

        textarea.addEventListener("input", function () {

            textarea.style.height = "auto";

            textarea.style.height = (textarea.scrollHeight + 2) + "px";
        });

        var LIMITE = 500;

        var contador = document.createElement("span");

        contador.className = "composer-contador";

        contador.textContent = "0 / " + LIMITE;

        textarea.insertAdjacentElement("afterend", contador);

        textarea.addEventListener("input", function () {

            var len = textarea.value.length;

            contador.textContent = len + " / " + LIMITE;

            contador.classList.toggle("contador-aviso",  len > LIMITE * 0.85);

            contador.classList.toggle("contador-limite", len >= LIMITE);
        });

        var form = textarea.closest("form");

        if (form) {

            form.addEventListener("submit", function (e) {

                if (textarea.value.length > LIMITE) {

                    e.preventDefault();

                    contador.classList.add("contador-limite");

                    textarea.focus();
                }
            });
        }
    }


    document.querySelectorAll(".post-card[data-post-id]").forEach(function (card) {

        var postId = card.dataset.postId;

        if (!postId) return;

        var upvoteBtn       = card.querySelector(".upvote-btn");

        var upvoteIcon      = card.querySelector(".upvote-icon");

        var upvoteCount     = card.querySelector(".upvote-count");

        var comentariosBtn  = card.querySelector(".comentarios-btn");

        var comentariosCount = card.querySelector(".comentarios-count");

        var comentariosSection = card.querySelector(".comentarios-section");

        var comentarioForm  = card.querySelector(".comentario-form");

        var comentariosLista = card.querySelector(".comentarios-lista");

        var editBtn         = card.querySelector(".post-edit-btn");

        var deleteBtn       = card.querySelector(".post-delete-btn");

        var conteudoEl      = card.querySelector(".post-conteudo");


        fetch("/postagens/" + postId + "/upvotes")

            .then(function (r) { return r.ok ? r.json() : null; })

            .then(function (data) {

                if (!data) return;

                upvoteCount.textContent = data.upvotes || 0;

                atualizarUpvote(upvoteBtn, upvoteIcon, upvoteCount, data.upvotes, data.upvotou);
            });


        if (upvoteBtn) {

            upvoteBtn.addEventListener("click", function () {

                if (typeof window.gsapBounce === "function") window.gsapBounce(upvoteBtn);

                var jaUpvotou = upvoteBtn.classList.contains("curtido");

                var metodo = jaUpvotou ? "DELETE" : "POST";

                fetch("/postagens/" + postId + "/upvote", {

                    method: metodo,
                    headers: { "X-XSRF-TOKEN": getCsrfToken() }
                })

                .then(function (r) { return r.ok ? r.json() : null; })

                .then(function (data) {

                    if (!data) return;

                    atualizarUpvote(upvoteBtn, upvoteIcon, upvoteCount, data.upvotes, data.upvotou);
                });
            });
        }


        if (comentariosBtn && comentariosSection) {

            comentariosBtn.addEventListener("click", function () {

                var visivel = !comentariosSection.hidden;

                comentariosSection.hidden = visivel;

                if (!visivel) {

                    carregarComentarios(postId, comentariosLista, comentariosCount);
                }
            });
        }


        if (comentarioForm) {

            comentarioForm.addEventListener("submit", function (e) {

                e.preventDefault();

                var input = comentarioForm.querySelector(".comentario-input");

                var texto = input ? input.value.trim() : "";

                if (!texto) return;

                fetch("/postagens/" + postId + "/comentar", {

                    method: "POST",
                    headers: {

                        "Content-Type": "application/json",
                        "X-XSRF-TOKEN": getCsrfToken()
                    },
                    body: JSON.stringify({ texto: texto })
                })

                .then(function (r) { return r.ok ? r.json() : null; })

                .then(function (data) {

                    if (!data) return;

                    if (input) input.value = "";

                    comentariosCount.textContent = data.total || 0;

                    adicionarComentarioDom(comentariosLista, data.autor, data.texto);
                });
            });
        }


        if (deleteBtn) {

            deleteBtn.addEventListener("click", function () {

                if (!confirm("Excluir esta postagem?")) return;

                fetch("/postagens/" + postId, {

                    method: "DELETE",
                    headers: { "X-XSRF-TOKEN": getCsrfToken() }
                })

                .then(function (r) {

                    if (r.ok) card.remove();
                });
            });
        }


        if (editBtn && conteudoEl) {

            editBtn.addEventListener("click", function () {

                var textoAtual = conteudoEl.textContent.trim();

                var input = document.createElement("textarea");

                input.className = "post-edit-textarea";

                input.value = textoAtual;

                input.style.cssText = "width:100%;min-height:80px;padding:10px;border:1.5px solid #4169dc;border-radius:10px;font:inherit;resize:vertical;";

                var btnSalvar = document.createElement("button");

                btnSalvar.textContent = "Salvar";

                btnSalvar.className = "post-edit-salvar";

                btnSalvar.style.cssText = "margin-top:8px;padding:6px 16px;background:#4169dc;color:#fff;border:0;border-radius:999px;cursor:pointer;font-weight:700;";

                var btnCancelar = document.createElement("button");

                btnCancelar.textContent = "Cancelar";

                btnCancelar.style.cssText = "margin-top:8px;margin-left:8px;padding:6px 16px;border:1.5px solid #dde1ee;border-radius:999px;cursor:pointer;font-weight:700;background:transparent;";

                conteudoEl.style.display = "none";

                conteudoEl.insertAdjacentElement("afterend", btnCancelar);

                conteudoEl.insertAdjacentElement("afterend", btnSalvar);

                conteudoEl.insertAdjacentElement("afterend", input);

                btnCancelar.addEventListener("click", function () {

                    input.remove(); btnSalvar.remove(); btnCancelar.remove();

                    conteudoEl.style.display = "";
                });

                btnSalvar.addEventListener("click", function () {

                    var novoTexto = input.value.trim();

                    if (!novoTexto) return;

                    fetch("/postagens/" + postId, {

                        method: "PUT",
                        headers: {

                            "Content-Type": "application/json",
                            "X-XSRF-TOKEN": getCsrfToken()
                        },
                        body: JSON.stringify({ conteudo: novoTexto })
                    })

                    .then(function (r) {

                        if (r.ok) {

                            conteudoEl.textContent = novoTexto;

                            conteudoEl.style.display = "";

                            input.remove(); btnSalvar.remove(); btnCancelar.remove();
                        }
                    });
                });
            });
        }
    });



    function atualizarUpvote(btn, icon, countEl, total, ativo) {

        if (ativo) {

            btn.classList.add("upvote-ativo", "curtido");
        } else {

            btn.classList.remove("upvote-ativo", "curtido");
        }
        countEl.textContent = total > 0 ? total : "Upvote";
    }

    function carregarComentarios(postId, lista, countEl) {

        fetch("/postagens/" + postId + "/comentarios")

            .then(function (r) { return r.ok ? r.json() : []; })

            .then(function (comentarios) {

                lista.innerHTML = "";

                comentarios.forEach(function (c) {

                    adicionarComentarioDom(lista, c.autor, c.texto);
                });

                if (countEl) countEl.textContent = comentarios.length;
            });
    }

    function adicionarComentarioDom(lista, autor, texto) {

        var div = document.createElement("div");

        div.className = "comentario-item";

        div.innerHTML =
            "<strong class='comentario-autor'>" + escapeHtml(autor) + ":</strong> " +
            "<span class='comentario-texto'>" + escapeHtml(texto) + "</span>";

        lista.appendChild(div);

        if (typeof window.gsapEntrada === "function") window.gsapEntrada(div, "cima");

        lista.scrollTop = lista.scrollHeight;
    }

    function escapeHtml(str) {

        return String(str)

            .replace(/&/g, "&amp;")

            .replace(/</g, "&lt;")

            .replace(/>/g, "&gt;")

            .replace(/"/g, "&quot;");
    }
});
