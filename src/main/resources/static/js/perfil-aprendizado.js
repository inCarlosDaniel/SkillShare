// Controla o onboarding de perfil de aprendizado e o envio das tags
function getCsrfToken() {
    var meta = document.querySelector('meta[name="_csrf"]');

    if (meta && meta.content) {
        return meta.content;
    }

    var match = document.cookie.split("; ").find(function (c) { return c.startsWith("XSRF-TOKEN="); });

    return match ? decodeURIComponent(match.split("=")[1]) : "";
}

document.addEventListener("DOMContentLoaded", function () {

    var animationContainer = document.getElementById("learningOnboardingAnimation");
    var form               = document.getElementById("learningOnboardingForm");
    var message            = document.getElementById("learningOnboardingMessage");
    var habilidadesInput   = document.getElementById("habilidadesInput");
    var interessesInput    = document.getElementById("interessesInput");
    var dificuldadesInput  = document.getElementById("dificuldadesInput");

    var MAX_TAGS = 5;


    var estado = {
        habilidades:  splitLocal(document.getElementById("currentHabilidades")  ? document.getElementById("currentHabilidades").value  : ""),
        interesses:   splitLocal(document.getElementById("currentInteresses")   ? document.getElementById("currentInteresses").value   : ""),
        dificuldades: splitLocal(document.getElementById("currentDificuldades") ? document.getElementById("currentDificuldades").value : "")
    };

    function splitLocal(value) {
        if (!value) return [];
        return value.split(/[,;\n]/).map(function (s) { return s.trim(); }).filter(Boolean);
    }

    if (animationContainer && window.lottie) {
        window.lottie.loadAnimation({
            container: animationContainer,
            renderer: "svg",
            loop: true,
            autoplay: true,
            path: "/animations/Blue%20Working%20Cat%20Animation.json",
            rendererSettings: {
                progressiveLoad: false,
                hideOnTransparent: false,
                preserveAspectRatio: "xMidYMid meet"
            }
        });
    }

    function setMessage(text, isError) {
        if (!message) return;
        message.textContent = text;
        message.classList.toggle("error", Boolean(isError));
    }

    if (!form) return;

    form.addEventListener("submit", function (event) {

        event.preventDefault();

        var novas = {
            habilidades:  splitLocal(habilidadesInput  ? habilidadesInput.value  : ""),
            interesses:   splitLocal(interessesInput   ? interessesInput.value   : ""),
            dificuldades: splitLocal(dificuldadesInput ? dificuldadesInput.value : "")
        };

        var adicionado   = false;
        var atingiuLimite = false;

        ["habilidades", "interesses", "dificuldades"].forEach(function (campo) {
            novas[campo].forEach(function (tag) {
                if (estado[campo].indexOf(tag) !== -1) return;
                if (estado[campo].length >= MAX_TAGS) { atingiuLimite = true; return; }
                estado[campo].push(tag);
                adicionado = true;
            });
        });

        if (!adicionado && atingiuLimite) {
            setMessage("Máximo de " + MAX_TAGS + " tags por campo já atingido.", true);
            return;
        }

        if (!adicionado) {
            setMessage("Nenhuma tag nova digitada.", false);
            return;
        }

        var submitButton = form.querySelector("button[type='submit']");
        if (submitButton) submitButton.disabled = true;
        setMessage("Salvando seu perfil...", false);

        fetch("/usuarios/perfil-aprendizado", {
            method: "PATCH",
            headers: {
                "Content-Type": "application/json",
                "X-XSRF-TOKEN": getCsrfToken()
            },
            body: JSON.stringify({
                habilidades:  estado.habilidades.join(", "),
                interesses:   estado.interesses.join(", "),
                dificuldades: estado.dificuldades.join(", ")
            })
        })
        .then(function (response) {
            if (!response.ok) throw new Error("Nao foi possivel salvar seu perfil.");
            return response.json();
        })
            .then(function () {
                setMessage("Perfil salvo. Redirecionando...", false);
                window.location.href = "/validando-informacoes";
            })
        .catch(function (error) {
            if (submitButton) submitButton.disabled = false;
            setMessage(error.message || "Nao foi possivel salvar seu perfil.", true);
        });
    });
});
