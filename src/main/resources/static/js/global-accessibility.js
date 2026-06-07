// Aplica preferencias globais de acessibilidade, como fonte e contraste
(function () {
    var fontKey = "skillshare.tamanhoFonte";
    var contrastKey = "skillshare.altoContraste";
    var fontSize = 1;

    function readFontSize() {
        var saved = parseFloat(localStorage.getItem(fontKey));
        return Number.isFinite(saved) ? saved : 1;
    }

    function applyFontSize(value) {
        fontSize = Math.min(1.5, Math.max(0.8, value));
        document.documentElement.style.fontSize = (fontSize * 16) + "px";
        localStorage.setItem(fontKey, String(fontSize));

        try {
            var scale = fontSize;
            if (window._skillshareOriginalFontSizes && window._skillshareOriginalFontSizes.length) {
                window._skillshareOriginalFontSizes.forEach(function (entry) {
                    try {
                        entry.el.style.fontSize = Math.round(entry.size * scale) + 'px';
                    } catch (e) {  }
                });
            }
        } catch (e) {  }
        updateAccessibilityStates();
    }

    function applyContrast(active) {
        document.body.classList.toggle("alto-contraste", active);
        localStorage.setItem(contrastKey, active ? "1" : "0");
        updateAccessibilityStates();
    }

    function openVLibras() {
        var button = document.querySelector("[vw-access-button]");
        if (button) button.click();
    }

    function resetAccessibility() {
        fontSize = 1;
        applyFontSize(fontSize);
        applyContrast(false);
    }

    function captureOriginalFontSizes() {
        if (window._skillshareOriginalFontSizes) return;
        var nodes = Array.prototype.slice.call(document.querySelectorAll('body *'));
        var list = [];
        nodes.forEach(function (el) {
            try {
                var cs = window.getComputedStyle(el);
                if (!cs) return;
                var fs = cs.fontSize;
                if (!fs || fs.indexOf('px') === -1) return;
                var num = parseFloat(fs);
                if (!isFinite(num) || num <= 0) return;
                list.push({ el: el, size: num });
            } catch (e) { }
        });
        window._skillshareOriginalFontSizes = list;
    }

    function updateAccessibilityStates() {
        var contrastActive = document.body.classList.contains("alto-contraste");
        document.querySelectorAll("[data-acesso=alto-contraste]").forEach(function (button) {
            button.setAttribute("aria-pressed", contrastActive ? "true" : "false");
        });
        document.querySelectorAll("[data-acesso=fonte-maior], [data-acesso=fonte-menor]").forEach(function (button) {
            button.setAttribute("aria-label", "Aumentar ou diminuir fonte. Tamanho atual: " + Math.round(fontSize * 100) + "%.");
        });
    }

    function handleAccessibilityAction(event) {
        var element = event.currentTarget;
        if (!element || !element.dataset) return;

        var reset = element.dataset.resetWidget !== undefined;
        var action = element.dataset.acesso;

        if (reset) {
            resetAccessibility();
            return;
        }

        if (!action) return;

        if (action === "fonte-maior") applyFontSize(fontSize + 0.1);
        if (action === "fonte-menor") applyFontSize(fontSize - 0.1);
        if (action === "alto-contraste") applyContrast(!document.body.classList.contains("alto-contraste"));
        if (action === "libras") openVLibras();
    }

    function registerAccessibilityControls() {
        document.querySelectorAll("[data-acesso], [data-reset-widget]").forEach(function (button) {
            button.removeEventListener("click", handleAccessibilityAction);
            button.addEventListener("click", handleAccessibilityAction);
        });
    }

    fontSize = readFontSize();
    if (document.readyState === "loading") {
        document.addEventListener('DOMContentLoaded', function () {
            captureOriginalFontSizes();
            applyFontSize(fontSize);
            applyContrast(localStorage.getItem(contrastKey) === "1");
            registerAccessibilityControls();
        });
    } else {
        captureOriginalFontSizes();
        applyFontSize(fontSize);
        applyContrast(localStorage.getItem(contrastKey) === "1");
        registerAccessibilityControls();
    }
})();
