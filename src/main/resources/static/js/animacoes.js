"use strict";

// Disponibiliza animacoes globais com GSAP usadas em varias telas
(function () {

    if (typeof gsap === "undefined") return;

    var reduced = window.matchMedia("(prefers-reduced-motion: reduce)").matches;



    window.gsapBounce = function (el) {
        if (reduced || typeof gsap === "undefined") return;
        gsap.fromTo(el,
            { scale: 1.28 },
            { scale: 1, duration: 0.35, ease: "elastic.out(1.1, 0.5)" }
        );
    };

    window.gsapEntrada = function (el, direcao) {
        if (reduced || typeof gsap === "undefined") return;
        var x = direcao === "esquerda" ? -18 : direcao === "direita" ? 18 : 0;
        var y = x === 0 ? -10 : 0;
        gsap.from(el, { opacity: 0, x: x, y: y, duration: 0.3, ease: "power2.out" });
    };

    if (reduced) return;


    document.addEventListener("DOMContentLoaded", function () {

        if (typeof ScrollTrigger === "undefined") return;

        gsap.registerPlugin(ScrollTrigger);

        var alturaViewport = window.innerHeight || document.documentElement.clientHeight;

        [".post-card", ".group-card", ".suggestion-card", ".notification-row", ".material-card"]
            .forEach(function (seletor) {

                document.querySelectorAll(seletor).forEach(function (el) {

                    if (el.getBoundingClientRect().top >= alturaViewport) {

                        gsap.set(el, { opacity: 0, y: 22 });

                        ScrollTrigger.create({
                            trigger: el,
                            start: "top 92%",
                            once: true,
                            onEnter: function () {
                                gsap.to(el, { opacity: 1, y: 0, duration: 0.42, ease: "power2.out" });
                            }
                        });
                    }
                });
            });
    });

})();
