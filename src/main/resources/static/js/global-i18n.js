(function () {
    "use strict";

    var IDIOMA_KEY = "skillshare.idioma";
    var DEFAULT_LANG = "pt-br";
    var supported = { "pt-br": true, en: true, es: true };

    var dictionary = {
        "Inicio": { en: "Home", es: "Inicio" },
        "Início": { en: "Home", es: "Inicio" },
        "Feed": { en: "Feed", es: "Feed" },
        "Match": { en: "Match", es: "Match" },
        "Materiais": { en: "Materials", es: "Materiales" },
        "Explorar": { en: "Explore", es: "Explorar" },
        "Pesquisar": { en: "Search", es: "Buscar" },
        "Pesquisar grupo": { en: "Search group", es: "Buscar grupo" },
        "Meu Perfil": { en: "My Profile", es: "Mi Perfil" },
        "Configurações": { en: "Settings", es: "Configuración" },
        "Acessibilidade": { en: "Accessibility", es: "Accesibilidad" },
        "Sair": { en: "Log out", es: "Salir" },
        "Mensagens": { en: "Messages", es: "Mensajes" },
        "Nenhuma conversa ainda.": { en: "No conversations yet.", es: "Todavia no hay conversaciones." },
        "Ver todas": { en: "View all", es: "Ver todas" },
        "Mensagem...": { en: "Message...", es: "Mensaje..." },
        "Abrir ou fechar chat": { en: "Open or close chat", es: "Abrir o cerrar chat" },
        "Fechar": { en: "Close", es: "Cerrar" },
        "Usuário": { en: "User", es: "Usuario" },

        "Personalizar": { en: "Customize", es: "Personalizar" },
        "Salvar": { en: "Save", es: "Guardar" },
        "Tema": { en: "Theme", es: "Tema" },
        "Tema da pagina": { en: "Page theme", es: "Tema de la pagina" },
        "Claro": { en: "Light", es: "Claro" },
        "Escuro": { en: "Dark", es: "Oscuro" },
        "Fundo": { en: "Background", es: "Fondo" },
        "Fundo da pagina": { en: "Page background", es: "Fondo de la pagina" },
        "Normal": { en: "Normal", es: "Normal" },
        "Animado": { en: "Animated", es: "Animado" },
        "Idioma": { en: "Language", es: "Idioma" },
        "Idioma da interface": { en: "Interface language", es: "Idioma de la interfaz" },
        "Opções de acessibilidade": { en: "Accessibility options", es: "Opciones de accesibilidad" },
        "Fonte +": { en: "Font +", es: "Fuente +" },
        "Fonte −": { en: "Font -", es: "Fuente -" },
        "Alto contraste": { en: "High contrast", es: "Alto contraste" },
        "Libras": { en: "Sign language", es: "Lengua de señas" },
        "Resetar": { en: "Reset", es: "Restablecer" },
        "+ Criar Post": { en: "+ Create Post", es: "+ Crear Publicacion" },
        "Criar postagem": { en: "Create post", es: "Crear publicacion" },
        "Compartilhe uma dúvida ou material de estudo...": { en: "Share a question or study material...", es: "Comparte una duda o material de estudio..." },
        "Compartilhe uma dúvida ou material de estudo com a comunidade...": { en: "Share a question or study material with the community...", es: "Comparte una duda o material de estudio con la comunidad..." },
        "Adicionar Anexo": { en: "Add Attachment", es: "Agregar Archivo" },
        "Descubra Projetos de Habilidades": { en: "Discover Skill Projects", es: "Descubre Proyectos de Habilidades" },
        "Nenhum grupo criado ainda": { en: "No groups created yet", es: "Aun no hay grupos creados" },
        "Nenhuma notificacao recebida ainda.": { en: "No notifications received yet.", es: "Aun no hay notificaciones recibidas." },
        "Idiomas": { en: "Languages", es: "Idiomas" },

        "Todas as Postagens": { en: "All Posts", es: "Todas las Publicaciones" },
        "Minhas Postagens": { en: "My Posts", es: "Mis Publicaciones" },
        "Postagens": { en: "Posts", es: "Publicaciones" },
        "Adicionar comentário...": { en: "Add comment...", es: "Agregar comentario..." },
        "Enviar comentário": { en: "Send comment", es: "Enviar comentario" },
        "Opções do post": { en: "Post options", es: "Opciones de la publicacion" },
        "✏️ Editar": { en: "✏️ Edit", es: "✏️ Editar" },
        "🗑️ Excluir": { en: "🗑️ Delete", es: "🗑️ Eliminar" },
        "Agora": { en: "Now", es: "Ahora" },
        "Nenhuma postagem.": { en: "No posts.", es: "No hay publicaciones." },
        "Você ainda não publicou nenhuma postagem.": { en: "You have not published any posts yet.", es: "Aun no has publicado ninguna publicacion." },
        "Nenhuma postagem cadastrada ainda.": { en: "No posts registered yet.", es: "Aun no hay publicaciones registradas." },
        "Excluir esta postagem?": { en: "Delete this post?", es: "Eliminar esta publicacion?" },

        "Voltar para mensagens": { en: "Back to messages", es: "Volver a mensajes" },
        "Verificando...": { en: "Checking...", es: "Comprobando..." },
        "Conversa": { en: "Conversation", es: "Conversacion" },
        "Nenhuma mensagem ainda. Diga ola!": { en: "No messages yet. Say hello!", es: "Aun no hay mensajes. Di hola!" },
        "Aceitar": { en: "Accept", es: "Aceptar" },
        "Recusar": { en: "Decline", es: "Rechazar" },
        "Aceite a solicitacao acima para responder.": { en: "Accept the request above to reply.", es: "Acepta la solicitud de arriba para responder." },
        "Digite sua mensagem... (Enter para enviar)": { en: "Type your message... (Enter to send)", es: "Escribe tu mensaje... (Enter para enviar)" },
        "Digite sua mensagem": { en: "Type your message", es: "Escribe tu mensaje" },
        "Enviar mensagem": { en: "Send message", es: "Enviar mensaje" },

        "Resultados para": { en: "Results for", es: "Resultados para" },
        "Nenhum grupo encontrado.": { en: "No group found.", es: "No se encontro ningun grupo." },
        "Tente buscar por um nome diferente ou crie um novo grupo.": { en: "Try searching for a different name or create a new group.", es: "Intenta buscar otro nombre o crea un nuevo grupo." },
        "Abrir": { en: "Open", es: "Abrir" },
        "Entrar": { en: "Join", es: "Entrar" },
        "← Voltar para meus grupos": { en: "← Back to my groups", es: "← Volver a mis grupos" },
        "Meus Grupos": { en: "My Groups", es: "Mis Grupos" },
        "Você ainda não participa de nenhum grupo.": { en: "You are not in any groups yet.", es: "Aun no participas en ningun grupo." },
        "Grupos em que você já participa.": { en: "Groups you already participate in.", es: "Grupos en los que ya participas." },
        "Crie um novo grupo pelo formulário ao lado ou pesquise grupos existentes para entrar.": { en: "Create a new group using the form beside this list or search for existing groups to join.", es: "Crea un grupo nuevo con el formulario lateral o busca grupos existentes para entrar." },
        "★ Você é o criador": { en: "★ You are the creator", es: "★ Eres el creador" },
        "Apagar": { en: "Delete", es: "Eliminar" },
        "Descobrir Grupos": { en: "Discover Groups", es: "Descubrir Grupos" },
        "disponíveis para entrar.": { en: "available to join.", es: "disponibles para entrar." },
        "Por": { en: "By", es: "Por" },
        "Entrar no grupo": { en: "Join group", es: "Entrar al grupo" },
        "Criar Grupo": { en: "Create Group", es: "Crear Grupo" },
        "Nome do grupo": { en: "Group name", es: "Nombre del grupo" },
        "Matéria": { en: "Subject", es: "Materia" },
        "Objetivo": { en: "Goal", es: "Objetivo" },
        "Objetivo do grupo": { en: "Group goal", es: "Objetivo del grupo" },
        "Foto do grupo (opcional)": { en: "Group photo (optional)", es: "Foto del grupo (opcional)" },
        "+ Criar Grupo": { en: "+ Create Group", es: "+ Crear Grupo" },
        "Pesquisar Grupos": { en: "Search Groups", es: "Buscar Grupos" },
        "Buscar por nome...": { en: "Search by name...", es: "Buscar por nombre..." },
        "Nome do grupo para pesquisar": { en: "Group name to search", es: "Nombre del grupo para buscar" },
        "Apagar este grupo permanentemente?": { en: "Delete this group permanently?", es: "Eliminar este grupo permanentemente?" },

        "← Voltar": { en: "← Back", es: "← Volver" },
        "Você precisa entrar neste grupo para ver o conteúdo e participar.": { en: "You need to join this group to see the content and participate.", es: "Necesitas entrar en este grupo para ver el contenido y participar." },
        "Entrar no Grupo": { en: "Join Group", es: "Entrar al Grupo" },
        "Foto do grupo": { en: "Group photo", es: "Foto del grupo" },
        "Atualizar foto": { en: "Update photo", es: "Actualizar foto" },
        "Materiais do Grupo": { en: "Group Materials", es: "Materiales del Grupo" },
        "Excluir": { en: "Delete", es: "Eliminar" },
        "Mural": { en: "Wall", es: "Mural" },
        "▲ Relevante": { en: "▲ Relevant", es: "▲ Relevante" },
        "🕐 Recente": { en: "🕐 Recent", es: "🕐 Reciente" },
        "Nenhuma publicação ainda. O criador do grupo pode começar postando!": { en: "No posts yet. The group creator can start by posting!", es: "Aun no hay publicaciones. El creador del grupo puede empezar publicando!" },
        "Responder Quiz": { en: "Answer Quiz", es: "Responder Quiz" },
        "Dar upvote nesta publicação": { en: "Upvote this post", es: "Dar upvote a esta publicacion" },
        "Comentários": { en: "Comments", es: "Comentarios" },
        "Nenhum comentário ainda.": { en: "No comments yet.", es: "Aun no hay comentarios." },
        "Comentário": { en: "Comment", es: "Comentario" },
        "Escreva um comentário...": { en: "Write a comment...", es: "Escribe un comentario..." },
        "Enviar": { en: "Send", es: "Enviar" },
        "Membros Ativos": { en: "Active Members", es: "Miembros Activos" },
        "Criador": { en: "Creator", es: "Creador" },
        "Nenhum membro": { en: "No members", es: "Ningun miembro" },
        "Postar no Mural": { en: "Post on Wall", es: "Publicar en el Mural" },
        "Texto": { en: "Text", es: "Texto" },
        "Material": { en: "Material", es: "Material" },
        "Arquivo": { en: "File", es: "Archivo" },
        "Escreva algo no mural...": { en: "Write something on the wall...", es: "Escribe algo en el mural..." },
        "Texto para publicar no mural": { en: "Text to post on the wall", es: "Texto para publicar en el mural" },
        "Publicar": { en: "Publish", es: "Publicar" },
        "✨ Criar Quiz com IA": { en: "✨ Create Quiz with AI", es: "✨ Crear Quiz con IA" },
        "ou cole o JSON manualmente:": { en: "or paste the JSON manually:", es: "o pega el JSON manualmente:" },
        "Publicar Quiz": { en: "Publish Quiz", es: "Publicar Quiz" },
        "Selecione um material...": { en: "Select a material...", es: "Selecciona un material..." },
        "Selecione ou pesquise um material": { en: "Select or search for a material", es: "Selecciona o busca un material" },
        "Materiais encontrados": { en: "Materials found", es: "Materiales encontrados" },
        "Nenhum material encontrado.": { en: "No material found.", es: "No se encontro ningun material." },
        "Compartilhar": { en: "Share", es: "Compartir" },
        "Título do arquivo (opcional)": { en: "File title (optional)", es: "Titulo del archivo (opcional)" },
        "Arquivo para publicar": { en: "File to publish", es: "Archivo para publicar" },
        "Publicar Arquivo": { en: "Publish File", es: "Publicar Archivo" },
        "Sobre este grupo": { en: "About this group", es: "Sobre este grupo" },
        "Somente o criador do grupo pode publicar no mural.\n              Use os comentários em cada publicação para interagir.": { en: "Only the group creator can post on the wall.\n              Use comments on each post to interact.", es: "Solo el creador del grupo puede publicar en el mural.\n              Usa los comentarios de cada publicacion para interactuar." },
        "Buscar grupo...": { en: "Search group...", es: "Buscar grupo..." },
        "Criar Quiz com IA": { en: "Create Quiz with AI", es: "Crear Quiz con IA" },
        "Converse com a IA para criar seu quiz": { en: "Chat with AI to create your quiz", es: "Conversa con la IA para crear tu quiz" },
        "▸ Colar JSON do quiz manualmente": { en: "▸ Paste quiz JSON manually", es: "▸ Pegar JSON del quiz manualmente" },
        "Carregar": { en: "Load", es: "Cargar" },
        "🚀 Publicar no Grupo": { en: "🚀 Publish to Group", es: "🚀 Publicar en el Grupo" },
        "Remover este material do grupo?": { en: "Remove this material from the group?", es: "Eliminar este material del grupo?" },

        "Foto de perfil": { en: "Profile photo", es: "Foto de perfil" },
        "Online": { en: "Online", es: "En linea" },
        "Editar Perfil": { en: "Edit Profile", es: "Editar Perfil" },
        "Escolher foto": { en: "Choose photo", es: "Elegir foto" },
        "Habilidades": { en: "Skills", es: "Habilidades" },
        "(máx. 5)": { en: "(max. 5)", es: "(max. 5)" },
        "Ex: Java, HTML, desenho": { en: "Ex: Java, HTML, drawing", es: "Ej: Java, HTML, dibujo" },
        "Interesses": { en: "Interests", es: "Intereses" },
        "Ex: Spring Boot, inglês, Excel": { en: "Ex: Spring Boot, English, Excel", es: "Ej: Spring Boot, ingles, Excel" },
        "Dificuldades": { en: "Difficulties", es: "Dificultades" },
        "Ex: matemática, apresentação, lógica": { en: "Ex: math, presentation, logic", es: "Ej: matematicas, presentacion, logica" },
        "Adicionar": { en: "Add", es: "Agregar" },
        "Minhas Habilidades (O que posso ensinar)": { en: "My Skills (What I can teach)", es: "Mis Habilidades (Lo que puedo ensenar)" },
        "Nenhuma habilidade cadastrada": { en: "No skills registered", es: "Ninguna habilidad registrada" },
        "Meus Interesses (O que quero aprender)": { en: "My Interests (What I want to learn)", es: "Mis Intereses (Lo que quiero aprender)" },
        "Nenhum interesse cadastrado": { en: "No interests registered", es: "Ningun interes registrado" },
        "Minhas Dificuldades (Onde preciso de ajuda)": { en: "My Difficulties (Where I need help)", es: "Mis Dificultades (Donde necesito ayuda)" },
        "Nenhuma dificuldade cadastrada": { en: "No difficulties registered", es: "Ninguna dificultad registrada" },
        "Nenhuma tag nova para adicionar.": { en: "No new tag to add.", es: "No hay ninguna etiqueta nueva para agregar." }
    };

    function normalizarCodigo(codigo) {
        codigo = String(codigo || "").toLowerCase();
        if (codigo === "pt" || codigo === "pt-br") return "pt-br";
        if (codigo === "en" || codigo.indexOf("en-") === 0) return "en";
        if (codigo === "es" || codigo.indexOf("es-") === 0) return "es";
        return DEFAULT_LANG;
    }

    function idiomaAtual() {
        return normalizarCodigo(localStorage.getItem(IDIOMA_KEY) || DEFAULT_LANG);
    }

    function traduzirValor(valor, idioma) {
        if (!valor || idioma === DEFAULT_LANG) return null;

        var direto = dictionary[valor];
        if (direto && direto[idioma]) return direto[idioma];

        var aparado = valor.trim();
        var entrada = dictionary[aparado];
        if (!entrada || !entrada[idioma]) return null;

        return valor.replace(aparado, entrada[idioma]);
    }

    function textoOriginal(elemento, attr, valorAtual) {
        var dataAttr = "data-i18n-original-" + attr.replace(/[^a-z0-9]+/gi, "-").toLowerCase();
        if (!elemento.hasAttribute(dataAttr)) {
            elemento.setAttribute(dataAttr, valorAtual);
        }
        return elemento.getAttribute(dataAttr);
    }

    function traduzirAtributos(root, idioma) {
        ["placeholder", "aria-label", "title"].forEach(function (attr) {
            root.querySelectorAll("[" + attr + "]").forEach(function (elemento) {
                var original = textoOriginal(elemento, attr, elemento.getAttribute(attr));
                var traduzido = traduzirValor(original, idioma);
                elemento.setAttribute(attr, traduzido || original);
            });
        });

        root.querySelectorAll("[onclick]").forEach(function (elemento) {
            var original = textoOriginal(elemento, "onclick", elemento.getAttribute("onclick"));
            var atualizado = original;
            Object.keys(dictionary).forEach(function (texto) {
                var traduzido = traduzirValor(texto, idioma);
                if (traduzido) atualizado = atualizado.split(texto).join(traduzido);
            });
            elemento.setAttribute("onclick", atualizado);
        });

        root.querySelectorAll("img[alt]").forEach(function (elemento) {
            var original = textoOriginal(elemento, "alt", elemento.getAttribute("alt"));
            var traduzido = traduzirValor(original, idioma);
            elemento.setAttribute("alt", traduzido || original);
        });
    }

    function deveIgnorarNoTexto(node) {
        var parent = node.parentElement;
        if (!parent) return true;
        if (parent.closest("script, style, textarea, input, [data-no-i18n]")) return true;
        if (parent.hasAttribute("th:text") || parent.hasAttribute("th:utext")) return true;
        if (parent.closest("[th\\:text], [th\\:utext]")) return true;
        return false;
    }

    function traduzirTextos(root, idioma) {
        var walker = document.createTreeWalker(root, NodeFilter.SHOW_TEXT, {
            acceptNode: function (node) {
                if (deveIgnorarNoTexto(node)) return NodeFilter.FILTER_REJECT;
                return node.nodeValue.trim() ? NodeFilter.FILTER_ACCEPT : NodeFilter.FILTER_REJECT;
            }
        });

        var nodes = [];
        while (walker.nextNode()) nodes.push(walker.currentNode);

        nodes.forEach(function (node) {
            if (!node.__skillshareOriginalText) {
                node.__skillshareOriginalText = node.nodeValue;
            }
            var traduzido = traduzirValor(node.__skillshareOriginalText, idioma);
            node.nodeValue = traduzido || node.__skillshareOriginalText;
        });
    }

    function atualizarBotoesIdioma(idioma) {
        document.querySelectorAll("[data-idioma]").forEach(function (botao) {
            botao.classList.toggle("ativo-idioma", normalizarCodigo(botao.dataset.idioma) === idioma);
        });
    }

    function aplicarIdioma(codigo) {
        var idioma = normalizarCodigo(codigo);
        if (!supported[idioma]) idioma = DEFAULT_LANG;

        document.documentElement.lang = idioma;
        document.body.dataset.lang = idioma;
        localStorage.setItem(IDIOMA_KEY, idioma);

        traduzirTextos(document.body, idioma);
        traduzirAtributos(document, idioma);
        atualizarBotoesIdioma(idioma);

        document.dispatchEvent(new CustomEvent("skillshare:idioma", { detail: { idioma: idioma } }));
    }

    var confirmOriginal = window.confirm;
    window.confirm = function (mensagem) {
        var traduzida = traduzirValor(mensagem, idiomaAtual());
        return confirmOriginal.call(window, traduzida || mensagem);
    };

    document.addEventListener("click", function (event) {
        var botao = event.target.closest("[data-idioma]");
        if (botao) aplicarIdioma(botao.dataset.idioma);
    });

    var agendado = false;
    var observer = new MutationObserver(function () {
        if (agendado) return;
        agendado = true;
        window.requestAnimationFrame(function () {
            agendado = false;
            aplicarIdioma(idiomaAtual());
        });
    });

    function iniciar() {
        aplicarIdioma(idiomaAtual());
        observer.observe(document.body, {
            childList: true,
            subtree: true,
            characterData: true,
            attributes: true,
            attributeFilter: ["placeholder", "aria-label", "title"]
        });
    }

    window.SkillShareI18n = {
        aplicarIdioma: aplicarIdioma,
        idiomaAtual: idiomaAtual,
        traduzir: function (texto) {
            return traduzirValor(texto, idiomaAtual()) || texto;
        }
    };

    if (document.readyState === "loading") {
        document.addEventListener("DOMContentLoaded", iniciar);
    } else {
        iniciar();
    }
})();
