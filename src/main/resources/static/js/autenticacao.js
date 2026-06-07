// Controla cadastro, login e confirmacao de codigo nas telas de autenticacao
function getCsrfToken() {

    var match = document.cookie.split("; ").find(function (c) { return c.startsWith("XSRF-TOKEN="); });

    return match ? decodeURIComponent(match.split("=")[1]) : "";
}

document.addEventListener("DOMContentLoaded", function () {

    var registerForm = document.getElementById("registerForm");

    var verificationForm = document.getElementById("verificationForm");

    var pendingRegister = null;

    function setMessage(elementId, text, isError) {

        var message = document.getElementById(elementId);

        if (!message) {

            return;
        }

        message.textContent = text;

        message.classList.toggle("error", Boolean(isError));
    }

    document.querySelectorAll(".toggle-password").forEach(function (button) {

        button.addEventListener("click", function () {

            var input = button.parentElement.querySelector("input");

            input.type = input.type === "password" ? "text" : "password";
        });
    });

    if (registerForm) {

        var registerPending = false;

        registerForm.addEventListener("submit", function (event) {

            event.preventDefault();

            if (registerPending) return;

            var nome = document.getElementById("registerName").value.trim();

            var email = document.getElementById("registerEmail").value.trim();

            var senha = document.getElementById("registerPassword").value;

            var confirmarSenha = document.getElementById("registerConfirmPassword").value;

            if (senha.length < 6) {

                setMessage("registerMessage", "A senha deve ter pelo menos 6 caracteres.", true);

                return;
            }

            if (senha !== confirmarSenha) {

                setMessage("registerMessage", "As senhas nao conferem.", true);

                return;
            }

            registerPending = true;

            var submitButton = registerForm.querySelector("button[type='submit']");

            if (submitButton) submitButton.disabled = true;

            setMessage("registerMessage", "Criando conta...", false);

            pendingRegister = {

                nome: nome,
                email: email,
                senha: senha
            };

            fetch("/usuarios", {

                method: "POST",
                headers: {

                    "Content-Type": "application/json",
                    "X-XSRF-TOKEN": getCsrfToken()
                },
                body: JSON.stringify({

                    nome: nome,
                    email: email,
                    senha: senha,
                    habilidades: "",
                    interesses: "",
                    dificuldades: ""
                    
                })
            })

                .then(function (response) {

                    if (response.status === 409) {

                        throw new Error("Este email ja esta cadastrado.");
                    }
                    if (!response.ok) {

                        throw new Error("Erro ao cadastrar.");
                    }
                    registerForm.hidden = true;

                    verificationForm.hidden = false;

                    setMessage("registerMessage", "Conta criada. Digite o codigo enviado para seu email.", false);
                })

                .catch(function (err) {

                    registerPending = false;

                    if (submitButton) submitButton.disabled = false;

                    setMessage("registerMessage", err.message || "Nao foi possivel criar a conta.", true);
                });
        });
    }

    if (verificationForm) {

        verificationForm.addEventListener("submit", function (event) {

            event.preventDefault();

            if (!pendingRegister) {

                setMessage("registerMessage", "Refaca o cadastro para gerar um novo codigo.", true);

                return;
            }

            var codigo = document.getElementById("verificationCode").value.trim();

            setMessage("registerMessage", "Verificando codigo...", false);

            fetch("/usuarios/verificacao/confirmar", {

                method: "POST",
                headers: {

                    "Content-Type": "application/json",
                    "X-XSRF-TOKEN": getCsrfToken()
                },
                body: JSON.stringify({

                    email: pendingRegister.email,
                    codigo: codigo
                })
            })

                .then(function (response) {

                    if (response.status === 400) {

                        throw new Error("Codigo invalido.");
                    }

                    if (!response.ok) {

                        throw new Error("Conta criada mas nao foi possivel entrar automaticamente. Faca login manualmente.");
                    }

                    sessionStorage.setItem("skillshare.showProfileOnboarding", "true");

                    window.location.href = "/perfil-aprendizado";
                })

                .catch(function (err) {

                    setMessage("registerMessage", err.message || "Nao foi possivel entrar.", true);
                });
        });
    }
});
