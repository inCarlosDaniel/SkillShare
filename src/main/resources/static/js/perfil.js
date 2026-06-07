// Controla edicao do perfil, foto e tags de aprendizado
function getCsrfToken() {

    var meta = document.querySelector('meta[name="_csrf"]');

    if (meta && meta.content) {

        return meta.content;
    }

    var match = document.cookie.split("; ").find(function (c) { return c.startsWith("XSRF-TOKEN="); });

    return match ? decodeURIComponent(match.split("=")[1]) : "";
}

document.addEventListener("DOMContentLoaded", function () {

    var editButton = document.getElementById("editProfileButton");

    var editPanel = document.getElementById("editProfilePanel");

    var uploadForm = document.getElementById("profileUploadForm");

    var imageInput = document.getElementById("profileImageInput");

    var selectedFileName = document.getElementById("selectedFileName");

    var profilePhoto = document.getElementById("profilePhoto");

    var topAvatar = document.getElementById("topAvatarImg");

    var topUserName = document.getElementById("topUserName");

    var profileName = document.getElementById("profileName");

    var skillsTags = document.getElementById("skillsTags");

    var interestTags = document.getElementById("interestTags");

    var difficultyTags = document.getElementById("difficultyTags");

    var learningProfileForm = document.getElementById("learningProfileForm");

    var skillsInput = document.getElementById("skillsInput");

    var interestsInput = document.getElementById("interestsInput");

    var difficultiesInput = document.getElementById("difficultiesInput");

    var learningProfileMessage = document.getElementById("learningProfileMessage");

    var downloadLink = document.getElementById("downloadProfilePhoto");

    var message = document.getElementById("profileMessage");

    var profilePhotoKey = "skillshare.profilePhoto";

    var currentUserKey = "skillshare.currentUser";

    var MAX_TAGS = 5;

    var tagState = { habilidades: [], interesses: [], dificuldades: [] };

    var currentProfileImage = "";

    function showDefaultProfileAvatar() {

        if (profilePhoto) profilePhoto.style.display = "none";

        if (topAvatar) topAvatar.style.display = "none";

        if (window.SkillShareAvatars) {

            window.SkillShareAvatars.showDefault("profilePlaceholder");

            window.SkillShareAvatars.showDefault("topAvatarPlaceholder");
        }
    }

    function setMessage(text, isError) {

        message.textContent = text;

        message.classList.toggle("error", Boolean(isError));
    }

    function setLearningProfileMessage(text, isError) {

        learningProfileMessage.textContent = text;

        learningProfileMessage.classList.toggle("error", Boolean(isError));
    }

    function setCurrentProfileImage(imageUrl) {

        currentProfileImage = imageUrl;

        if (!imageUrl) {

            return;
        }

        profilePhoto.src = imageUrl;

        topAvatar.src = imageUrl;

        downloadLink.href = imageUrl;

        downloadLink.classList.remove("disabled");

        downloadLink.setAttribute("aria-disabled", "false");

        localStorage.setItem(profilePhotoKey, imageUrl);
    }

    function splitField(value) {

        if (!value) {

            return [];
        }

        return value
            .split(/[,;\n]/)

            .map(function (item) {

                return item.trim();
            })

            .filter(Boolean);
    }

    function renderTags(container, values, type, emptyText) {

        container.innerHTML = "";

        if (values.length === 0) {

            var emptyTag = document.createElement("span");

            emptyTag.className = "tag empty";

            emptyTag.textContent = emptyText;

            container.appendChild(emptyTag);

            return;
        }

        values.forEach(function (value) {

            var tag = document.createElement("span");

            tag.className = "tag " + type;

            tag.textContent = value;

            container.appendChild(tag);
        });
    }

    function renderTagsInterativos(container, tags, categoria, tipo, emptyText) {

        container.innerHTML = "";

        if (tags.length === 0) {

            var emptyTag = document.createElement("span");
            emptyTag.className = "tag empty";
            emptyTag.textContent = emptyText;
            container.appendChild(emptyTag);

        } else {

            tags.forEach(function (value, index) {

                var tag = document.createElement("span");
                tag.className = "tag " + tipo;
                tag.textContent = value;

                var btn = document.createElement("button");
                btn.type = "button";
                btn.className = "tag-remove";
                btn.innerHTML = "&times;";
                btn.setAttribute("aria-label", "Remover " + value);
                btn.addEventListener("click", function (e) {
                    e.stopPropagation();
                    removerTag(categoria, index);
                });

                tag.appendChild(btn);
                container.appendChild(tag);
            });
        }

        if (tags.length >= MAX_TAGS) {

            var maxSpan = document.createElement("span");
            maxSpan.className = "tag-max-message";
            maxSpan.textContent = "Máximo (" + MAX_TAGS + ") atingido";
            container.appendChild(maxSpan);
        }
    }

    function atualizarTagsNaTela() {

        renderTagsInterativos(skillsTags,    tagState.habilidades, "habilidades", "teach",      "Nenhuma habilidade cadastrada");
        renderTagsInterativos(interestTags,  tagState.interesses,  "interesses",  "interest",   "Nenhum interesse cadastrado");
        renderTagsInterativos(difficultyTags, tagState.dificuldades, "dificuldades", "difficulty", "Nenhuma dificuldade cadastrada");
    }

    function removerTag(categoria, index) {

        tagState[categoria].splice(index, 1);
        atualizarTagsNaTela();
        salvarTags().catch(function () {});
    }

    function salvarTags() {

        return fetch("/usuarios/perfil-aprendizado", {

            method: "PATCH",
            headers: {
                "Content-Type": "application/json",
                "X-XSRF-TOKEN": getCsrfToken()
            },
            body: JSON.stringify({
                habilidades:  tagState.habilidades.join(", "),
                interesses:   tagState.interesses.join(", "),
                dificuldades: tagState.dificuldades.join(", ")
            })
        })

        .then(function (r) {

            if (!r.ok) throw new Error("Status: " + r.status);
            return r.json();
        })

        .then(function (usuario) {

            if (topUserName) topUserName.textContent = usuario.nome || "";
            if (profileName) profileName.textContent = usuario.nome || "";
        });
    }

    function applyUser(usuario) {

        var name = usuario && usuario.nome ? usuario.nome : "Usuário";

        if (topUserName) topUserName.textContent = name;
        profileName.textContent = name;

        tagState.habilidades  = splitField(usuario && usuario.habilidades);
        tagState.interesses   = splitField(usuario && usuario.interesses);
        tagState.dificuldades = splitField(usuario && usuario.dificuldades);

        atualizarTagsNaTela();
    }

    function getStoredUser() {

        try {

            return JSON.parse(localStorage.getItem(currentUserKey));
        } catch (error) {

            return null;
        }
    }

    function loadCurrentUser() {

        fetch("/usuario")

            .then(function (response) {

                if (!response.ok) {

                    throw new Error("Erro ao carregar usuario.");
                }
                return response.json();
            })

            .then(function (usuario) {

                applyUser(usuario);

                localStorage.setItem(currentUserKey, JSON.stringify({

                    idUsuario: usuario.idUsuario,
                    nome: usuario.nome,
                    email: usuario.email
                }));
            })

            .catch(function () {

                applyUser(null);
            });
    }

    function loadLastUploadedImage() {

        var url = "/usuarios/foto?t=" + Date.now();

        var testImg = new Image();

        testImg.onload = function () {

            setCurrentProfileImage(url);
        };

        testImg.onerror = function () {

            showDefaultProfileAvatar();
        };

        testImg.src = url;
    }

    editButton.addEventListener("click", function () {

        editPanel.hidden = !editPanel.hidden;
    });

    imageInput.addEventListener("change", function () {

        var file = imageInput.files[0];

        selectedFileName.textContent = file ? file.name : "Escolher foto";
    });

    uploadForm.addEventListener("submit", function (event) {

        event.preventDefault();

        var file = imageInput.files[0];

        if (!file) {

            setMessage("Escolha uma imagem antes de enviar.", true);

            return;
        }

        if (!file.type.startsWith("image/")) {

            setMessage("Envie apenas arquivos de imagem.", true);

            return;
        }

        var formData = new FormData();

        formData.append("arquivo", file);

        setMessage("Enviando foto...", false);

        fetch("/usuarios/foto", {

            method: "POST",
            headers: { "X-XSRF-TOKEN": getCsrfToken() },
            body: formData
        })

            .then(function (response) {

                if (!response.ok) {

                    throw new Error("Erro ao enviar imagem.");
                }
            })

            .then(function () {


                var novaUrl = "/usuarios/foto?t=" + Date.now();

                setCurrentProfileImage(novaUrl);


                var topImg = document.getElementById("topAvatarImg");

                var topPlaceholder = document.getElementById("topAvatarPlaceholder");

                if (topImg) {

                    topImg.style.display = "none";

                    topImg.onload = function () {

                        topImg.style.display = "";

                        if (topPlaceholder) topPlaceholder.style.display = "none";
                    };

                    topImg.onerror = function () {

                        topImg.style.display = "none";

                        if (window.SkillShareAvatars) {

                            window.SkillShareAvatars.showDefault(topPlaceholder);
                        } else if (topPlaceholder) {

                            topPlaceholder.style.display = "";
                        }
                    };

                    topImg.src = novaUrl;
                }

                selectedFileName.textContent = "Escolher foto";

                imageInput.value = "";

                setMessage("Foto enviada com sucesso.", false);
            })

            .catch(function () {

                setMessage("Nao foi possivel enviar a foto.", true);
            });
    });

    learningProfileForm.addEventListener("submit", function (event) {

        event.preventDefault();

        var novas = {
            habilidades:  splitField(skillsInput    ? skillsInput.value    : ""),
            interesses:   splitField(interestsInput ? interestsInput.value : ""),
            dificuldades: splitField(difficultiesInput ? difficultiesInput.value : "")
        };

        var adicionado = false;
        var atingiuLimite = false;

        ["habilidades", "interesses", "dificuldades"].forEach(function (campo) {

            novas[campo].forEach(function (tag) {

                if (tagState[campo].indexOf(tag) !== -1) return;

                if (tagState[campo].length >= MAX_TAGS) { atingiuLimite = true; return; }

                tagState[campo].push(tag);
                adicionado = true;
            });
        });

        if (skillsInput)       skillsInput.value = "";
        if (interestsInput)    interestsInput.value = "";
        if (difficultiesInput) difficultiesInput.value = "";

        if (!adicionado && atingiuLimite) {

            setLearningProfileMessage("Máximo de " + MAX_TAGS + " tags por campo atingido.", true);
            return;
        }

        if (!adicionado) {

            setLearningProfileMessage("Nenhuma tag nova para adicionar.", false);
            return;
        }

        atualizarTagsNaTela();
        setLearningProfileMessage("Salvando...", false);

        var submitButton = learningProfileForm.querySelector("button[type='submit']");
        if (submitButton) submitButton.disabled = true;

        salvarTags()

            .then(function () {

                setLearningProfileMessage("Tags adicionadas com sucesso!", false);

                localStorage.setItem(currentUserKey, JSON.stringify({
                    habilidades:  tagState.habilidades.join(", "),
                    interesses:   tagState.interesses.join(", "),
                    dificuldades: tagState.dificuldades.join(", ")
                }));
            })

            .catch(function (error) {

                console.error(error);
                setLearningProfileMessage("Nao foi possivel salvar.", true);
            })

            .finally(function () {

                if (submitButton) submitButton.disabled = false;
            });
    });

    downloadLink.addEventListener("click", function (event) {

        if (!currentProfileImage) {

            event.preventDefault();
        }
    });

    loadCurrentUser();

    loadLastUploadedImage();
});
