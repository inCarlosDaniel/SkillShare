// Aplica o fundo global escolhido pelo usuario
(function () {
    var backgroundKey = "skillshare.background";
    var legacyBackgroundKey = "skillshare.homeBackground";

    var validBackgrounds = {
        animated: true,
        fish: true
    };

    function getSavedBackground() {
        try {
            var background = localStorage.getItem(backgroundKey);

            if (!background) {
                background = localStorage.getItem(legacyBackgroundKey);
                if (background) localStorage.setItem(backgroundKey, background);
            }

            return background || "normal";
        } catch (error) {
            return "normal";
        }
    }

    function loadScript(src, onload) {
        var existing = document.querySelector('script[src="' + src + '"]');

        if (existing) {
            if (window.lottie) onload();
            else existing.addEventListener("load", onload, { once: true });
            return;
        }

        var script = document.createElement("script");
        script.src = src;
        script.defer = true;
        script.addEventListener("load", onload, { once: true });
        document.head.appendChild(script);
    }

    function ensureLayer() {
        var layer = document.getElementById("globalBackgroundLayer");

        if (!layer) {
            layer = document.createElement("div");
            layer.id = "globalBackgroundLayer";
            layer.setAttribute("aria-hidden", "true");
            document.body.insertBefore(layer, document.body.firstChild);
        }

        return layer;
    }

    function startFishBackground(layer) {
        layer.innerHTML = "";
        layer.className = "global-background-layer global-background-fish";

        function startAnim() {
            if (!window.lottie) return;
            window.lottie.loadAnimation({
                container: layer,
                renderer: "svg",
                loop: true,
                autoplay: true,
                path: "/animations/glowing-fish.json",
                rendererSettings: {
                    progressiveLoad: false,
                    hideOnTransparent: false,
                    preserveAspectRatio: "xMidYMid slice",
                    viewBoxSize: null
                }
            });
        }

        if (window.lottie) {
            startAnim();
        } else {
            var script = document.querySelector('script[src="/js/libs/lottie.js"]');
            if (script) {
                script.addEventListener("load", startAnim, { once: true });
            } else {
                loadScript("/js/libs/lottie.js", startAnim);
            }
        }
    }

    function applyGlobalBackground() {
        var savedBackground = getSavedBackground();

        if (!validBackgrounds[savedBackground]) {
            document.body.classList.remove(
                "skillshare-global-background-active",
                "skillshare-global-background-animated",
                "skillshare-global-background-fish"
            );

            var existingLayer = document.getElementById("globalBackgroundLayer");
            if (existingLayer) existingLayer.remove();

            return;
        }

        if (document.getElementById("lottieBackgroundContainer")) return;

        document.body.classList.add("skillshare-global-background-active");
        document.body.classList.toggle("skillshare-global-background-animated", savedBackground === "animated");
        document.body.classList.toggle("skillshare-global-background-fish",     savedBackground === "fish");

        var layer = ensureLayer();

        if (savedBackground === "fish") {
            startFishBackground(layer);
        } else {
            layer.innerHTML = "";
            layer.className = "global-background-layer global-background-animated";
        }
    }

    if (document.readyState === "loading") {
        document.addEventListener("DOMContentLoaded", applyGlobalBackground);
    } else {
        applyGlobalBackground();
    }
})();
