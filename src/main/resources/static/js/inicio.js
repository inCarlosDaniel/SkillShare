// Desenha o fundo low-poly animado da pagina inicial
(function () {

    var canvas = document.querySelector("[data-low-poly-background]");

    if (!canvas) return;

    var context = canvas.getContext("2d");

    var points = [];

    var cells = [];

    var spacing = 76;

    var pixelRatio = 1;

    // Ajusta o canvas ao tamanho da tela
    function resizeCanvas() {

        pixelRatio = Math.min(window.devicePixelRatio || 1, 2);

        canvas.width = Math.floor(window.innerWidth * pixelRatio);

        canvas.height = Math.floor(window.innerHeight * pixelRatio);

        canvas.style.width = window.innerWidth + "px";

        canvas.style.height = window.innerHeight + "px";

        context.setTransform(pixelRatio, 0, 0, pixelRatio, 0, 0);

        buildMesh();
    }

    // Cria a malha de pontos e triangulos do fundo
    function buildMesh() {

        var columns = Math.ceil(window.innerWidth / spacing) + 2;

        var rows = Math.ceil(window.innerHeight / spacing) + 2;

        points = [];

        cells = [];

        for (var y = 0; y <= rows; y += 1) {

            var row = [];

            for (var x = 0; x <= columns; x += 1) {

                row.push({

                    x: x * spacing - spacing,
                    y: y * spacing - spacing,
                    jitterX: (Math.random() - .5) * 46,
                    jitterY: (Math.random() - .5) * 46,
                    speed: .95 + Math.random() * 1.15,
                    phase: Math.random() * Math.PI * 2,
                    shade: Math.random()
                });
            }
            points.push(row);
        }

        for (var rowIndex = 0; rowIndex < rows; rowIndex += 1) {

            for (var columnIndex = 0; columnIndex < columns; columnIndex += 1) {

                var a = points[rowIndex][columnIndex];

                var b = points[rowIndex][columnIndex + 1];

                var c = points[rowIndex + 1][columnIndex];

                var d = points[rowIndex + 1][columnIndex + 1];

                if ((rowIndex + columnIndex) % 2 === 0) {

                    cells.push([a, b, d], [a, d, c]);
                } else {

                    cells.push([a, b, c], [b, d, c]);
                }
            }
        }
    }

    function pointPosition(point, time) {

        var x = point.x + point.jitterX + Math.sin(time * point.speed + point.phase) * 18;

        var y = point.y + point.jitterY + Math.cos(time * point.speed * .88 + point.phase) * 18;

        return { x: x, y: y };
    }

    function triangleColor(triangle, index, time) {

        var dark = document.body.classList.contains("dark-theme");

        var wave = Math.sin(time * 1.25 + index * .22) * .5 + .5;

        var shade = (triangle[0].shade + triangle[1].shade + triangle[2].shade) / 3;

        var alpha = dark ? .3 + shade * .18 : .48 + shade * .28;

        var blue = dark ? 78 + wave * 48 : 196 + wave * 34;

        var base = dark ? 28 + shade * 28 : 218 + shade * 26;

        return "rgba(" + Math.round(base) + ", " + Math.round(base + (dark ? 8 : 3)) + ", " + Math.round(blue) + ", " + alpha.toFixed(3) + ")";
    }

    // Renderiza a animacao do fundo a cada frame
    function draw(timeStamp) {

        var time = timeStamp / 1000;

        context.clearRect(0, 0, window.innerWidth, window.innerHeight);

        context.fillStyle = document.body.classList.contains("dark-theme") ? "#171b26" : "#d2d8e2";

        context.fillRect(0, 0, window.innerWidth, window.innerHeight);

        var sweep = (time * 90) % (window.innerWidth + 420) - 210;

        var gradient = context.createLinearGradient(sweep - 220, 0, sweep + 220, window.innerHeight);

        gradient.addColorStop(0, "rgba(255,255,255,0)");

        gradient.addColorStop(.5, document.body.classList.contains("dark-theme") ? "rgba(95,135,210,.12)" : "rgba(255,255,255,.28)");

        gradient.addColorStop(1, "rgba(255,255,255,0)");

        context.fillStyle = gradient;

        context.fillRect(0, 0, window.innerWidth, window.innerHeight);

        cells.forEach(function (triangle, index) {

            var p1 = pointPosition(triangle[0], time);

            var p2 = pointPosition(triangle[1], time);

            var p3 = pointPosition(triangle[2], time);

            context.beginPath();

            context.moveTo(p1.x, p1.y);

            context.lineTo(p2.x, p2.y);

            context.lineTo(p3.x, p3.y);

            context.closePath();

            context.fillStyle = triangleColor(triangle, index, time);

            context.fill();

            context.strokeStyle = document.body.classList.contains("dark-theme")

                ? "rgba(255,255,255,.05)"
                : "rgba(255,255,255,.36)";

            context.lineWidth = 1;

            context.stroke();
        });

        requestAnimationFrame(draw);
    }

    window.addEventListener("resize", resizeCanvas);

    resizeCanvas();

    requestAnimationFrame(draw);
})();

// Controla os filtros dos grupos destacados na tela inicial
(function () {

    var filterButtons = Array.prototype.slice.call(document.querySelectorAll("[data-home-filter]"));

    var groupCards = Array.prototype.slice.call(document.querySelectorAll("[data-group-card]"));

    var projectsTitle = document.querySelector("[data-projects-title]");

    if (!filterButtons.length) return;

    var defaultTitle = projectsTitle ? projectsTitle.textContent : "";

    function setActiveFilter(filter) {

        filterButtons.forEach(function (button) {

            var active = button.dataset.homeFilter === filter;

            button.classList.toggle("active", active);

            button.setAttribute("aria-pressed", active ? "true" : "false");
        });
    }

    function showAllGroups() {

        groupCards.forEach(function (card) {

            card.classList.remove("course-card-hidden");
        });

        if (projectsTitle) projectsTitle.textContent = defaultTitle;

        setActiveFilter("popular");
    }

    function showMostRecentGroup() {

        if (!groupCards.length) {

            if (projectsTitle) projectsTitle.textContent = "Nenhum grupo criado ainda";

            setActiveFilter("recent");

            return;
        }

        var mostRecentCard = groupCards.reduce(function (latest, card) {

            var latestId = parseInt(latest.dataset.groupId || "0", 10);

            var cardId = parseInt(card.dataset.groupId || "0", 10);

            return cardId > latestId ? card : latest;
        }, groupCards[0]);

        groupCards.forEach(function (card) {

            card.classList.toggle("course-card-hidden", card !== mostRecentCard);
        });

        if (projectsTitle) projectsTitle.textContent = "Grupo mais recente";

        setActiveFilter("recent");
    }

    filterButtons.forEach(function (button) {

        button.setAttribute("aria-pressed", "false");

        button.addEventListener("click", function () {

            var filter = button.dataset.homeFilter;

            if (filter === "groups") {

                window.location.href = "/grupos";

                return;
            }

            if (filter === "recent") {

                showMostRecentGroup();

                return;
            }

            showAllGroups();
        });
    });
})();

// Exibe o convite para completar o perfil de aprendizado
(function () {

    var onboardingKey = "skillshare.showProfileOnboarding";

    if (sessionStorage.getItem(onboardingKey) !== "true") return;

    function createCallout(className, title, text, actionText, actionHref) {

        var callout = document.createElement("section");

        callout.className = "profile-onboarding-callout " + className;

        callout.setAttribute("aria-label", title);

        var closeButton = document.createElement("button");

        closeButton.type = "button";

        closeButton.className = "profile-onboarding-close";

        closeButton.setAttribute("aria-label", "Fechar aviso");

        closeButton.textContent = "×";

        closeButton.addEventListener("click", dismiss);

        var heading = document.createElement("strong");

        heading.textContent = title;

        var paragraph = document.createElement("p");

        paragraph.textContent = text;

        callout.appendChild(closeButton);

        callout.appendChild(heading);

        callout.appendChild(paragraph);

        if (actionText && actionHref) {

            var action = document.createElement("a");

            action.className = "profile-onboarding-action";

            action.href = actionHref;

            action.textContent = actionText;

            action.addEventListener("click", function () {

                sessionStorage.removeItem(onboardingKey);
            });

            callout.appendChild(action);
        }

        return callout;
    }

    function dismiss() {

        sessionStorage.removeItem(onboardingKey);

        document.body.classList.remove("profile-onboarding-active");

        document.querySelectorAll(".profile-onboarding-callout").forEach(function (callout) {

            callout.remove();
        });
    }

    // Mostra o onboarding quando o usuario ainda precisa completar o perfil
    function showProfileOnboarding() {

        var profileCard = document.querySelector(".profile-card");

        var topAvatar = document.getElementById("topAvatarPlaceholder") || document.getElementById("topAvatarImg");

        var customizeButton = document.querySelector("[data-customize-toggle]");

        if (!profileCard || !topAvatar || !customizeButton) return;

        document.body.classList.add("profile-onboarding-active");

        profileCard.appendChild(createCallout(
            "profile-photo-callout",
            "Adicione uma foto ao seu perfil",
            "Uma foto ajuda seus colegas a reconhecerem você nas conexões, mensagens e postagens.",
            "Personalize seu perfil",
            "/perfil"
        ));

        customizeButton.insertAdjacentElement("afterend", createCallout(
            "profile-customize-callout",
            "Personalize sua página inicial",
            "Ajuste tema, fundo e acessibilidade para deixar o SkillShare do seu jeito.",
            null,
            null
        ));

        window.setTimeout(function () {

            document.body.classList.add("profile-onboarding-visible");
        }, 80);
    }

    if (document.readyState === "loading") {

        document.addEventListener("DOMContentLoaded", showProfileOnboarding);
    } else {

        showProfileOnboarding();
    }
})();

// Controla personalizacao da pagina inicial, tema, fundo e cards arrastaveis
(function () {

    var toggleButton = document.querySelector("[data-customize-toggle]");

    var resetButton = document.querySelector("[data-reset-widget]");

    if (!toggleButton) return;

    var storageKey = "skillshare.homeLayoutPositions";

    var themeStorageKey = "skillshare.theme";

    var legacyThemeStorageKey = "skillshare.homeTheme";

    var backgroundStorageKey = "skillshare.background";

    var legacyBackgroundStorageKey = "skillshare.homeBackground";

    var themeButtons = Array.prototype.slice.call(document.querySelectorAll("[data-theme-option]"));

    var backgroundButtons = Array.prototype.slice.call(document.querySelectorAll("[data-background-option]"));

    var draggableSelectors = [
        ".profile-card",
        ".main-menu",
        ".explore-menu",
        ".search-card",
        ".filter-row",
        ".projects-panel",
        ".notifications-panel",
        ".suggestions-panel",
        ".floating-chat"
    ];

    var items = Array.prototype.slice.call(document.querySelectorAll(draggableSelectors.join(",")));

    var positions = loadPositions();

    var dragState = null;

    var fishLottieAnim = null;

    var fishLottieTimer = null;

    items.forEach(function (item, index) {

        var id = item.dataset.layoutId || item.className.split(/\s+/)[0] || "item-" + index;

        item.dataset.layoutId = id;

        item.dataset.layoutDraggable = "true";

        applyItemPosition(item, positions[id] || { x: 0, y: 0 });
    });

    // Carrega posicoes salvas dos cards da pagina inicial
    function loadPositions() {

        try {

            return JSON.parse(localStorage.getItem(storageKey)) || {};
        } catch (error) {

            localStorage.removeItem(storageKey);

            return {};
        }
    }

    // Salva posicoes personalizadas dos cards
    function savePositions() {

        localStorage.setItem(storageKey, JSON.stringify(positions));
    }

    function applyItemPosition(item, position) {

        item.style.setProperty("--layout-x", (position.x || 0) + "px");

        item.style.setProperty("--layout-y", (position.y || 0) + "px");
    }

    function isInteractive(target) {

        if (target.closest(".floating-chat")) {

            return false;
        }
        return Boolean(target.closest("a, button, input, textarea, select, label"));
    }

    // Aplica o tema escolhido na pagina inicial
    function applyTheme(theme) {

        var useDarkTheme = theme === "dark";

        document.body.classList.toggle("dark-theme", useDarkTheme);

        document.documentElement.dataset.theme = useDarkTheme ? "dark" : "light";

        themeButtons.forEach(function (button) {

            button.setAttribute("aria-pressed", button.dataset.themeOption === theme ? "true" : "false");
        });

        if (window.SkillShareTheme && typeof window.SkillShareTheme.apply === "function") {

            window.SkillShareTheme.apply(useDarkTheme ? "dark" : "light");
        } else {

            localStorage.setItem(themeStorageKey, useDarkTheme ? "dark" : "light");

            localStorage.setItem(legacyThemeStorageKey, useDarkTheme ? "dark" : "light");
        }
    }

    function iniciarFundoPeixes() {

        var container = document.getElementById("lottieBackgroundContainer");

        if (!container) return;


        container.style.display = "block";


        void container.offsetWidth;

        if (typeof lottie === "undefined") {

            clearTimeout(fishLottieTimer);

            fishLottieTimer = setTimeout(function () {

                if (document.body.classList.contains("fish-background")) iniciarFundoPeixes();
            }, 250);

            return;
        }

        if (!fishLottieAnim) {

            container.innerHTML = "";

            fishLottieAnim = lottie.loadAnimation({
                container: container,
                renderer:  "svg",
                loop:      true,
                autoplay:  true,
                path:      "/animations/glowing-fish.json",
                rendererSettings: {
                    progressiveLoad: false,
                    hideOnTransparent: false,
                    preserveAspectRatio: "xMidYMid slice",
                    viewBoxSize: null
                }
            });

        } else {

            fishLottieAnim.play();
        }
    }

    function pararFundoPeixes() {

        var container = document.getElementById("lottieBackgroundContainer");

        clearTimeout(fishLottieTimer);

        if (fishLottieAnim) fishLottieAnim.stop();

        if (container) container.style.display = "none";
    }

// Aplica o fundo escolhido pelo usuario
    function applyBackground(background) {

        var useLowPoly = background === "animated";

        var useFish = background === "fish";

        document.body.classList.toggle("low-poly-theme",  useLowPoly);

        document.body.classList.remove("cat-background", "dog-background");

        document.body.classList.toggle("fish-background", useFish);

        if (useFish) iniciarFundoPeixes();
        else         pararFundoPeixes();

        backgroundButtons.forEach(function (button) {

            button.setAttribute("aria-pressed", button.dataset.backgroundOption === background ? "true" : "false");
        });

        localStorage.setItem(backgroundStorageKey, useLowPoly || useFish ? background : "normal");

        localStorage.setItem(legacyBackgroundStorageKey, useLowPoly || useFish ? background : "normal");
    }

    toggleButton.addEventListener("click", function () {

        var editing = document.body.classList.toggle("layout-editing");

        toggleButton.textContent = editing ? "Salvar" : "Personalizar";
    });

    themeButtons.forEach(function (button) {

        button.addEventListener("click", function () {

            var novoTema = button.dataset.themeOption === "dark" ? "dark" : "light";

            if (typeof gsap !== "undefined") {

                gsap.to("body", {
                    opacity: 0.88, duration: 0.12, overwrite: true,
                    onComplete: function () {
                        applyTheme(novoTema);
                        gsap.to("body", { opacity: 1, duration: 0.2 });
                    }
                });
            } else {

                applyTheme(novoTema);
            }
        });
    });

    backgroundButtons.forEach(function (button) {

        button.addEventListener("click", function () {

            var option = button.dataset.backgroundOption;

            applyBackground(option === "animated" || option === "fish" ? option : "normal");
        });
    });

    document.addEventListener("pointerdown", function (event) {

        if (!document.body.classList.contains("layout-editing") || isInteractive(event.target)) return;

        var item = event.target.closest("[data-layout-draggable]");

        if (!item) return;

        var id = item.dataset.layoutId;

        var currentPosition = positions[id] || { x: 0, y: 0 };

        dragState = {

            item: item,
            id: id,
            startX: event.clientX,
            startY: event.clientY,
            startItemX: currentPosition.x || 0,
            startItemY: currentPosition.y || 0
        };

        item.classList.add("is-dragging");

        item.setPointerCapture(event.pointerId);
    });

    document.addEventListener("pointermove", function (event) {

        if (!dragState) return;

        positions[dragState.id] = {

            x: dragState.startItemX + event.clientX - dragState.startX,
            y: dragState.startItemY + event.clientY - dragState.startY
        };

        applyItemPosition(dragState.item, positions[dragState.id]);
    });

    document.addEventListener("pointerup", function (event) {

        if (!dragState) return;

        dragState.item.classList.remove("is-dragging");

        dragState.item.releasePointerCapture(event.pointerId);

        dragState = null;

        savePositions();
    });

    document.addEventListener("pointercancel", function () {

        if (!dragState) return;

        dragState.item.classList.remove("is-dragging");

        dragState = null;
    });

    if (resetButton) {

        resetButton.addEventListener("click", function () {

            positions = {};

            localStorage.removeItem(storageKey);

            applyTheme("light");

            applyBackground("normal");

            items.forEach(function (item) {

                applyItemPosition(item, { x: 0, y: 0 });
            });
        });
    }

    var savedTheme = localStorage.getItem(themeStorageKey) || localStorage.getItem(legacyThemeStorageKey);

    applyTheme(savedTheme === "dark" ? "dark" : "light");

    var savedBackground = localStorage.getItem(backgroundStorageKey) || localStorage.getItem(legacyBackgroundStorageKey);

    applyBackground(savedBackground === "animated" || savedBackground === "fish" ? savedBackground : "normal");
})();


// Inicializa animacoes de entrada da pagina inicial
document.addEventListener("DOMContentLoaded", function () {

    document.querySelectorAll(".connect-button[data-usuario-id]").forEach(function (btn) {

        btn.addEventListener("click", function () {

            var id = btn.dataset.usuarioId;

            if (!id) return;

            var match = document.cookie.match(/XSRF-TOKEN=([^;]+)/);

            var token = match ? decodeURIComponent(match[1]) : "";

            btn.textContent = "Conectando...";

            btn.disabled = true;

            fetch("/match/conectar/" + id, {

                method: "POST",

                headers: { "X-XSRF-TOKEN": token }

            }).then(function () {

                btn.textContent = "✓ Conectado";

                btn.style.opacity = "0.6";

            }).catch(function () {

                btn.textContent = "Conectar";

                btn.disabled = false;
            });
        });
    });
});

      
      var idxFile = document.getElementById("indexFileInput");
      var idxName = document.getElementById("indexFileName");
      if (idxFile && idxName) {
        idxFile.addEventListener("change", function() {
          idxName.textContent = idxFile.files[0] ? idxFile.files[0].name : "";
        });
      }
