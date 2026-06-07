"use strict";

// Aplica e persiste o tema visual escolhido pelo usuario
(function () {

    var themeKey = "skillshare.theme";

    var legacyThemeKey = "skillshare.homeTheme";

    function temaSalvo() {

        var theme = localStorage.getItem(themeKey);

        if (!theme) {

            theme = localStorage.getItem(legacyThemeKey);

            if (theme) localStorage.setItem(themeKey, theme);
        }

        return theme === "dark" ? "dark" : "light";
    }

    function aplicarTema(theme) {

        var dark = theme === "dark";

        document.body.classList.toggle("dark-theme", dark);

        document.documentElement.dataset.theme = dark ? "dark" : "light";
    }

    window.SkillShareTheme = {

        key: themeKey,

        get: temaSalvo,

        apply: function (theme) {

            var nextTheme = theme === "dark" ? "dark" : "light";

            localStorage.setItem(themeKey, nextTheme);

            localStorage.setItem(legacyThemeKey, nextTheme);

            aplicarTema(nextTheme);

            window.dispatchEvent(new CustomEvent("skillshare:themechange", {

                detail: { theme: nextTheme }
            }));
        }
    };

    if (document.readyState === "loading") {

        document.addEventListener("DOMContentLoaded", function () {

            aplicarTema(temaSalvo());
        });
    } else {

        aplicarTema(temaSalvo());
    }
})();
