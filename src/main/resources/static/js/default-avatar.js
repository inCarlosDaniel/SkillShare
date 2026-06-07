// Renderiza o avatar padrao animado quando nao existe foto de perfil
(function () {

    var animationPath = "/animations/profile.json";

    var lottieScriptPath = "/js/libs/lottie.js";

    var lottieLoading = false;

    var lottieReadyCallbacks = [];

    var selector = "[data-default-profile-avatar]";

    function ensureLottie(callback) {

        if (window.lottie) {

            callback();

            return;
        }

        lottieReadyCallbacks.push(callback);

        if (lottieLoading) return;

        lottieLoading = true;

        var script = document.createElement("script");

        script.src = lottieScriptPath;

        script.defer = true;

        script.onload = function () {

            lottieLoading = false;

            lottieReadyCallbacks.splice(0).forEach(function (readyCallback) {

                readyCallback();
            });
        };

        document.head.appendChild(script);
    }

    function renderDefaultAvatar(element) {

        if (!element || element.dataset.defaultAvatarRendered === "true") return;

        element.dataset.defaultAvatarRendered = "true";

        element.dataset.defaultProfileAvatar = "true";

        element.textContent = "";

        element.style.display = element.tagName === "SPAN" ? "grid" : "";

        element.style.placeItems = "center";

        element.style.overflow = "hidden";

        ensureLottie(function () {

            if (!window.lottie) return;

            element.innerHTML = "";

            window.lottie.loadAnimation({
                container: element,
                renderer: "svg",
                loop: true,
                autoplay: true,
                path: animationPath,
                rendererSettings: {
                    preserveAspectRatio: "xMidYMid slice"
                }
            });
        });
    }

    function showDefaultAvatar(idOrElement) {

        var element = typeof idOrElement === "string"
            ? document.getElementById(idOrElement)
            : idOrElement;

        if (!element) return;

        element.style.display = element.classList.contains("profile-cover-placeholder") ? "grid" : "";

        renderDefaultAvatar(element);
    }

    function initializeVisibleFallbacks() {

        Array.prototype.slice.call(document.querySelectorAll(selector)).forEach(function (element) {

            if (element.tagName === "IMG") return;

            var style = window.getComputedStyle(element);

            if (style.display !== "none") {

                renderDefaultAvatar(element);
            }
        });
    }

    window.SkillShareAvatars = window.SkillShareAvatars || {};

    window.SkillShareAvatars.showDefault = showDefaultAvatar;

    if (document.readyState === "loading") {

        document.addEventListener("DOMContentLoaded", initializeVisibleFallbacks);
    } else {

        initializeVisibleFallbacks();
    }
})();
