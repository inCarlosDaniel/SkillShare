// Exibe a animacao de validacao e redireciona para o proximo passo
document.addEventListener("DOMContentLoaded", function () {
    var animationContainer = document.getElementById("validationAnimation");

    if (animationContainer && window.lottie) {
        window.lottie.loadAnimation({
            container: animationContainer,
            renderer: "svg",
            loop: true,
            autoplay: true,
            path: "/animations/0440d3b2-5273-11f0-b93e-e315c27baf59.json",
            rendererSettings: {
                progressiveLoad: false,
                hideOnTransparent: false,
                preserveAspectRatio: "xMidYMid meet"
            }
        });
    }

    window.setTimeout(function () {
        window.location.href = "/";
    }, 3600);
});
