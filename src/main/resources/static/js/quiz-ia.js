"use strict";

// Gerencia todo o fluxo do quiz com IA: abre o modal, conversa com o Botpress,
// captura o JSON gerado, mostra uma previa e publica o quiz no mural do grupo.

(function () {

    var modal       = null;

    var previewArea = null;

    var publishBtn  = null;

    var statusTxt   = null;

    var lottieInst  = null;

    var quizJson      = null;

    var grupoId       = 0;

    var bpReady       = false;

    var eventosReg    = false;

    var LOG = function () {

        var a = Array.prototype.slice.call(arguments); a.unshift("[QuizIA]");

        console.log.apply(console, a);

    };

    // API global usada por outros scripts ou pelo Botpress para entregar um quiz pronto.
    // Tambem permite diagnosticar o estado atual e disparar a publicacao manualmente.

    window.QuizIA = {

        
        receberDoBot: function (texto, json) {

            LOG("receberDoBot() chamado. json:", json ? (json.titulo || typeof json) : "null");

            if (typeof json === "string") {

                var parsedJson = tentarParsearJson(json);

                if (parsedJson) setQuizJson(parsedJson);

            } else if (json && Array.isArray(json.perguntas)) {

                setQuizJson(json);

            } else if (texto) {

                var parsed = tentarParsearJson(texto);

                if (parsed) setQuizJson(parsed);

            }

        },

        publicar:    function () { publicarQuiz(); },

        setJson:     function (json) { setQuizJson(json); },

        diagnostico: function () {

            LOG("botpress:", typeof window.botpress,

                "| grupoId:", grupoId,

                "| quizJson:", quizJson ? quizJson.titulo : null,

                "| btn disabled:", publishBtn ? publishBtn.disabled : "n/a");

        }

    };

    // Prepara a tela quando o HTML termina de carregar: encontra os elementos do modal,
    // registra cliques dos botoes, le o ID do grupo e inicia a integracao com o Botpress.

    document.addEventListener("DOMContentLoaded", function () {

        modal       = document.getElementById("quizIaModal");

        previewArea = document.getElementById("quizIaPreview");

        publishBtn  = document.getElementById("quizIaPublicar");

        statusTxt   = document.getElementById("qia-status-txt");

        var dataEl = document.getElementById("quizIaData");

        if (dataEl) grupoId = parseInt(dataEl.dataset.grupoId, 10) || 0;

        if (!modal) return;

        LOG("init. grupoId=" + grupoId);

        document.querySelectorAll(".btn-criar-quiz-ia").forEach(function (b) {

            b.addEventListener("click", abrirModal);

        });

        modal.addEventListener("click", function (e) { if (e.target === modal) fecharModal(); });

        var closeBtn = document.getElementById("quizIaFechar");

        if (closeBtn) closeBtn.addEventListener("click", fecharModal);

        if (publishBtn) publishBtn.addEventListener("click", publicarQuiz);

        var btnCarregar = document.getElementById("qia-btn-carregar-json");

        if (btnCarregar) {

            btnCarregar.addEventListener("click", function () {

                var ta = document.getElementById("quizIaJsonPaste");

                if (!ta || !ta.value.trim()) return;

                var json = tentarParsearJson(ta.value.trim());

                if (json) {

                    setQuizJson(json);

                    ta.value = "";

                    var details = document.getElementById("qia-json-details");

                    if (details) details.removeAttribute("open");

                    LOG("JSON carregado manualmente ✓");

                } else {

                    ta.style.borderColor = "#c62828";

                    setTimeout(function () { ta.style.borderColor = ""; }, 2000);

                    LOG("JSON inválido no campo manual.");

                }

            });

        }

        detectarBotpress(function (ok) {

            bpReady = ok;

            if (ok) {

                registrarEventos();

                LOG("Botpress OK. Keys:", Object.keys(window.botpress).join(", "));

            } else {

                LOG("Botpress não detectado.");

            }

        });

    });

    // Verifica repetidamente se o objeto do Botpress ja foi carregado na pagina.
    // Quando encontra o Botpress, chama o callback para continuar a configuracao do chat.

    function detectarBotpress(callback) {

        var n = 0;

        var t = setInterval(function () {

            n++;

            var bp = window.botpress;

            if (bp !== null && bp !== undefined && typeof bp === "object") {

                clearInterval(t);

                setTimeout(function () { callback(true); }, 300);

                return;

            }

            if (n >= 150) { clearInterval(t); callback(false); }

        }, 100);

    }

    // Registra varios nomes possiveis de eventos do Botpress, porque a API pode variar.
    // Tambem ativa um observador do DOM como plano B para capturar respostas exibidas no chat.

    function registrarEventos() {

        var bp = window.botpress;

        if (eventosReg) { LOG("Eventos já registrados — ignorando."); return; }

        eventosReg = true;

        ativarObserverDom();

        if (!bp || typeof bp.on !== "function") return;

        var nomesEvento = [

            "MESSAGE_RECEIVED",

            "message_received",

            "messageReceived",

            "WEBCHAT.MESSAGE.RECEIVED",

            "webchat.message.received",

            "BOT_MESSAGE",

            "bot_message",

            "message",

            "MESSAGE",

            "CUSTOM_EVENT",

            "customEvent"

        ];

        nomesEvento.forEach(function (nome) {

            try {

                bp.on(nome, function (data) {

                    LOG("EVENTO '" + nome + "' disparou:", JSON.stringify(data).slice(0, 200));

                    processarMensagemBot(data, nome);

                });

            } catch (e) {  }

        });

        LOG("Escutando", nomesEvento.length, "eventos diferentes do Botpress.");

    }

    // Recebe uma mensagem do bot, ignora mensagens do proprio usuario e procura textos
    // que possam conter o JSON do quiz. Se encontrar um quiz valido, salva e mostra a previa.

    function processarMensagemBot(data, nomeEvento) {

            if (data && data.authorId && String(data.authorId).indexOf("user_") === 0) {

                LOG("Ignorando mensagem do usuário (authorId=" + data.authorId + ")");

                return;

            }

            LOG("=== MENSAGEM DO BOT via '" + nomeEvento + "' ===");

            LOG("authorId:", data ? data.authorId : "n/a");

            LOG("Payload completo:", JSON.stringify(data).slice(0, 600));

            var textos = [];

            function coletarTextos(obj) {

                if (!obj) return;

                if (typeof obj === "string") { textos.push(obj); return; }

                if (typeof obj === "object") {

                    ["text","preview","markdown","content","body","message","value","label"].forEach(function (k) {

                        if (typeof obj[k] === "string") textos.push(obj[k]);

                    });

                    if (obj.payload) coletarTextos(obj.payload);

                    if (Array.isArray(obj.responses)) obj.responses.forEach(coletarTextos);

                    if (Array.isArray(obj.choices))   obj.choices.forEach(function (c) { if (typeof c === "string") textos.push(c); });

                    try { textos.push(JSON.stringify(obj)); } catch(e) {}

                }

            }

            coletarTextos(data);

            var resumo = textos.map(function (t) { return t.slice(0, 120); }).join(" | ");

            if (resumo) LOG("MESSAGE_RECEIVED:", resumo);

            for (var i = 0; i < textos.length; i++) {

                var json = tentarParsearJson(textos[i]);

                if (json) {

                    LOG("✅ JSON detectado! titulo:", json.titulo, "| perguntas:", (json.perguntas || []).length);

                    setQuizJson(json);

                    return;

                }

            }

            if (data && data.payload && typeof data.payload === "object") {

                try {

                    var p = data.payload;

                    if (p.titulo && Array.isArray(p.perguntas)) {

                        LOG("✅ Payload direto é um quiz JSON.");

                        setQuizJson(p);

                    }

                } catch (e) {}

            }

    }

    // Recebe um objeto de quiz, valida seu formato e guarda em quizJson.
    // Depois atualiza a previa e libera o botao para publicar no grupo.

    function setQuizJson(json) {

        var quizNormalizado = normalizarQuiz(json);

        if (!quizNormalizado) {

            LOG("JSON encontrado, mas formato do quiz é inválido:", json);

            return;

        }

        quizJson = quizNormalizado;

        renderizarPreview(quizJson);

        if (publishBtn) {

            publishBtn.textContent = "🚀 Publicar no Grupo";

            publishBtn.style.background = "linear-gradient(135deg,#4169dc,#7c3aed)";

        }

        LOG("quizJson definido:", quizJson.titulo);

    }

    // Exibe o modal do quiz, bloqueia a rolagem do fundo, inicia a animacao Lottie
    // e tenta abrir o chat do Botpress para o usuario conversar com a IA.

    function abrirModal() {

        if (!modal) return;

        modal.style.display = "flex";

        document.body.style.overflow = "hidden";

        document.body.classList.add("qia-aberto");

        if (!lottieInst) {

            var c = document.getElementById("quizIaBotLottie");

            if (c && typeof lottie !== "undefined") {

                lottieInst = lottie.loadAnimation({

                    container: c, renderer: "svg", loop: true, autoplay: true,

                    path: "/animations/Chat%20Bot.json"

                });

            }

        }

        if (statusTxt) statusTxt.textContent = bpReady ? "IA pronta ✓" : "Carregando…";

        var bp = window.botpress;

        if (bp) {

            if      (typeof bp.open      === "function") bp.open();

            else if (typeof bp.show      === "function") bp.show();

            else if (typeof bp.toggle    === "function") bp.toggle();

            else if (typeof bp.sendEvent === "function") bp.sendEvent({ type: "show" });

        }

    }

    // Fecha o modal e devolve a rolagem normal da pagina.
    // O quiz gerado permanece em memoria para nao perder a previa por acidente.

    function fecharModal() {

        if (!modal) return;

        modal.style.display = "none";

        document.body.style.overflow = "";

        document.body.classList.remove("qia-aberto");

    }

    // Procura o container do chat do Botpress usando seletores conhecidos.
    // Isso e usado pelo observer para ler mensagens quando eventos do Botpress nao disparam.

    function getBotpressChatEl() {

        return document.getElementById("webchat-root") ||

               document.querySelector(".bpEmbeddedWebchat") ||

               document.getElementById("bp-embedded-webchat");

    }

    // Le o texto visivel dentro do chat e tenta extrair dele o JSON do quiz.
    // Funciona como fallback quando a resposta da IA aparece na tela mas nao chega via evento.

    function extrairJsonDoDomChat() {

        var raiz = getBotpressChatEl();

        if (!raiz) { LOG("Elemento do chat não encontrado no DOM."); return null; }

        var textoCompleto = coletarTextoProfundo(raiz);

        LOG("Textos coletados do DOM:", textoCompleto.length, "chars | raiz:", raiz.id || raiz.className.slice(0, 30));

        if (textoCompleto.length < 10) return null;

        var json = tentarParsearJson(textoCompleto);

        if (json) return json;

        var matches = extrairObjetosJson(textoCompleto);

        for (var i = matches.length - 1; i >= 0; i--) {

            var j = tentarParsearJson(matches[i]);

            if (j) return j;

        }

        return null;

    }

    // Percorre um elemento e seus filhos para juntar todo o texto encontrado.
    // Tambem entra em shadow DOM, porque alguns widgets de chat renderizam conteudo ali.

    function coletarTextoProfundo(el) {

        var partes = [];

        function visitar(node) {

            if (!node) return;

            if (node.nodeType === Node.TEXT_NODE) {

                if (node.nodeValue) partes.push(node.nodeValue);

                return;

            }

            if (node.nodeType !== Node.ELEMENT_NODE && node.nodeType !== Node.DOCUMENT_FRAGMENT_NODE) return;

            if (node.innerText) partes.push(node.innerText);

            if (node.shadowRoot) visitar(node.shadowRoot);

            Array.prototype.forEach.call(node.children || [], visitar);

        }

        visitar(el);

        return partes.join("\n");

    }

    // Cria um MutationObserver no chat do Botpress para perceber novas mensagens.
    // Quando o DOM muda, tenta ler a conversa e encontrar o JSON do quiz gerado.

    function ativarObserverDom() {

        var n = 0;

        var t = setInterval(function () {

            n++;

            var raiz = getBotpressChatEl();

            if (raiz) {

                clearInterval(t);

                LOG("MutationObserver ativo em:", raiz.id || raiz.className.slice(0, 30));

                new MutationObserver(function () {

                    clearTimeout(window._quizIaObsTimer);

                    window._quizIaObsTimer = setTimeout(function () {

                        if (quizJson) return;

                        var json = extrairJsonDoDomChat();

                        if (json) {

                            LOG("✅ MutationObserver detectou JSON:", json.titulo);

                            setQuizJson(json);

                        }

                    }, 400);

                }).observe(raiz, { childList: true, subtree: true, characterData: true });

            } else if (n >= 100) {

                clearInterval(t);

                LOG("MutationObserver: elemento do chat não encontrado após 10s.");

            }

        }, 100);

    }

    // Tenta converter texto em um quiz valido. Primeiro normaliza caracteres,
    // depois procura blocos JSON e objetos dentro de textos maiores antes de validar.

    function tentarParsearJson(texto) {

        if (!texto) return null;

        texto = String(texto)

            .replace(/​/g, "")

            .replace(/[""]/g, '"')

            .replace(/['']/g, "'");

        var match = texto.match(/```(?:json)?\s*([\s\S]+?)\s*```/);

        var candidatos = match ? [match[1], texto] : [texto];

        extrairObjetosJson(texto).forEach(function (objeto) {

            candidatos.push(objeto);

        });

        for (var i = 0; i < candidatos.length; i++) {

            var c = candidatos[i].trim();

            try {

                var obj = JSON.parse(c);

                var quiz = normalizarQuiz(obj);

                if (quiz) return quiz;

            } catch (e) {}

            var idx = c.indexOf("{");

            if (idx > 0) {

                try {

                    var obj2 = JSON.parse(c.slice(idx));

                    var quiz2 = normalizarQuiz(obj2);

                    if (quiz2) return quiz2;

                } catch (e) {}

            }

        }

        return null;

    }

    // Varre o texto procurando objetos entre chaves { ... }, respeitando strings.
    // Retorna apenas candidatos que parecem conter a chave "perguntas".

    function extrairObjetosJson(texto) {

        var objetos = [];

        var inicio = -1;

        var profundidade = 0;

        var emString = false;

        var escape = false;

        for (var i = 0; i < texto.length; i++) {

            var ch = texto.charAt(i);

            if (emString) {

                if (escape) {

                    escape = false;

                } else if (ch === "\\") {

                    escape = true;

                } else if (ch === '"') {

                    emString = false;

                }

                continue;

            }

            if (ch === '"') {

                emString = true;

            } else if (ch === "{") {

                if (profundidade === 0) inicio = i;

                profundidade++;

            } else if (ch === "}" && profundidade > 0) {

                profundidade--;

                if (profundidade === 0 && inicio >= 0) {

                    objetos.push(texto.slice(inicio, i + 1));

                    inicio = -1;

                }

            }

        }

        return objetos.filter(function (objeto) {

            return objeto.indexOf('"perguntas"') !== -1 || objeto.indexOf("'perguntas'") !== -1;

        });

    }

    // Confere se o objeto tem perguntas e alternativas validas.
    // Tambem transforma valores em strings e garante uma alternativa correta numerica.

    function normalizarQuiz(obj) {

        if (!obj || !Array.isArray(obj.perguntas) || obj.perguntas.length === 0) return null;

        var perguntas = obj.perguntas.map(function (pergunta) {

            if (!pergunta || !Array.isArray(pergunta.alternativas) || pergunta.alternativas.length === 0) return null;

            var correta = Number(pergunta.correta);

            if (!Number.isFinite(correta)) correta = 0;

            return {

                enunciado: String(pergunta.enunciado || "Pergunta"),

                alternativas: pergunta.alternativas.map(function (alt) { return String(alt); }),

                correta: correta

            };

        }).filter(Boolean);

        if (perguntas.length === 0) return null;

        return {

            titulo: String(obj.titulo || "Quiz gerado"),

            perguntas: perguntas

        };

    }

    // Busca o token CSRF em meta tag, input escondido ou cookie.
    // Esse token e necessario para o POST que publica o quiz no mural.

    function getCsrfToken() {

        var meta = document.querySelector('meta[name="_csrf"]');

        if (meta && meta.content) return meta.content;

        var input = document.querySelector('input[name="_csrf"]');

        if (input && input.value) return input.value;

        var m = document.cookie.match(/XSRF-TOKEN=([^;]+)/);

        return m ? decodeURIComponent(m[1]) : "";

    }

    // Mostra uma previa simples do quiz gerado, com titulo e quantidade de perguntas.
    // Essa previa confirma para o usuario que a IA entregou um formato publicavel.

    function renderizarPreview(quiz) {

        if (!previewArea) return;

        previewArea.style.display = "block";

        previewArea.innerHTML = "";

        var box = document.createElement("div");

        box.style.cssText = "padding:12px 16px;background:#e8f5e9;border-radius:12px;margin:0 0 4px;";

        var titulo = document.createElement("strong");

        titulo.style.cssText = "color:#1b5e20;font-size:14px;";

        titulo.textContent = "✅ " + (quiz.titulo || "Quiz gerado");

        var resumo = document.createElement("span");

        resumo.style.cssText = "display:block;font-size:12px;color:#388e3c;margin-top:2px;";

        resumo.textContent = (quiz.perguntas || []).length + " pergunta(s) — clique em Publicar ↓";

        box.appendChild(titulo);

        box.appendChild(resumo);

        previewArea.appendChild(box);

    }

    // Envia o quiz para a rota do mural do grupo. Se ainda nao houver quiz em memoria,
    // tenta extrair o JSON diretamente do chat antes de desistir da publicacao.

    function publicarQuiz() {

        LOG("publicarQuiz() chamado. quizJson:", quizJson ? quizJson.titulo : "NULL", "| grupoId:", grupoId);

        if (!quizJson) {

            LOG("quizJson null — tentando extrair do DOM do chat...");

            quizJson = extrairJsonDoDomChat();

            if (quizJson) {

                LOG("✅ JSON extraído do DOM! titulo:", quizJson.titulo);

                renderizarPreview(quizJson);

            } else {

                if (publishBtn) {

                    publishBtn.textContent = "⏳ Conclua a conversa com a IA primeiro";

                    publishBtn.style.background = "#7b7b7b";

                    setTimeout(function () {

                        publishBtn.textContent = "🚀 Publicar no Grupo";

                        publishBtn.style.background = "";

                    }, 3000);

                }

                LOG("Nenhum JSON encontrado. Botpress ainda não enviou.");

                return;

            }

        }

        if (!grupoId) {

            LOG("ERRO: grupoId=0. Verifique #quizIaData no HTML.");

            return;

        }

        var csrf = getCsrfToken();

        LOG("Enviando quiz para /grupos/" + grupoId + "/mural/quiz | CSRF:", csrf ? "OK" : "VAZIO");

        if (publishBtn) { publishBtn.disabled = true; publishBtn.textContent = "⏳ Publicando…"; publishBtn.style.background = "#7b7b7b"; }

        var body = new URLSearchParams();

        body.append("conteudo", JSON.stringify(quizJson));

        fetch("/grupos/" + grupoId + "/mural/quiz", {

            method: "POST",

            credentials: "same-origin",

            headers: {

                "Content-Type": "application/x-www-form-urlencoded",

                "X-XSRF-TOKEN": csrf

            },

            body: body.toString()

        }).then(function (r) {

            LOG("Resposta HTTP:", r.status, "| redirected:", r.redirected);

            if (r.ok || r.redirected) {

                if (publishBtn) { publishBtn.textContent = "✓ Publicado!"; publishBtn.style.background = "#4caf50"; }

                setTimeout(function () { window.location.reload(); }, 1200);

            } else {

                return r.text().then(function (txt) {

                    throw new Error("HTTP " + r.status + ": " + txt.slice(0, 100));

                });

            }

        }).catch(function (err) {

            LOG("❌ Erro ao publicar:", err.message);

            if (publishBtn) {

                publishBtn.textContent = "❌ Erro — tente novamente";

                publishBtn.style.background = "#c62828";

                setTimeout(function () {

                    publishBtn.textContent = "🚀 Publicar no Grupo";

                    publishBtn.style.background = "";

                }, 4000);

            }

        });

    }

})();
