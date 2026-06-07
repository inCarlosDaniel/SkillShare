CREATE TABLE IF NOT EXISTS usuario (
    idUsuario   INT          NOT NULL AUTO_INCREMENT,
    nome        VARCHAR(100) DEFAULT NULL,
    email       VARCHAR(100) DEFAULT NULL,
    senha       VARCHAR(255) DEFAULT NULL,
    habilidades TEXT         DEFAULT NULL,
    interesses  TEXT         DEFAULT NULL,
    dificuldades TEXT        DEFAULT NULL,
    PRIMARY KEY (idUsuario)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE IF NOT EXISTS grupoestudo (
    idGrupo   INT          NOT NULL AUTO_INCREMENT,
    nome      VARCHAR(100) DEFAULT NULL,
    materia   VARCHAR(100) DEFAULT NULL,
    exameAlvo VARCHAR(100) DEFAULT NULL,
    idCriador INT          DEFAULT NULL,
    PRIMARY KEY (idGrupo),
    CONSTRAINT fk_grupo_criador FOREIGN KEY (idCriador) REFERENCES usuario (idUsuario)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE IF NOT EXISTS grupo_membro (
    idGrupo   INT NOT NULL,
    idUsuario INT NOT NULL,
    PRIMARY KEY (idGrupo, idUsuario),
    CONSTRAINT grupo_membro_ibfk_1 FOREIGN KEY (idGrupo)   REFERENCES grupoestudo (idGrupo),
    CONSTRAINT grupo_membro_ibfk_2 FOREIGN KEY (idUsuario) REFERENCES usuario     (idUsuario)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE IF NOT EXISTS postagem (
    idPostagem     INT          NOT NULL AUTO_INCREMENT,
    conteudo       TEXT         DEFAULT NULL,
    dataPublicacao DATETIME     DEFAULT NULL,
    idUsuario      INT          DEFAULT NULL,
    tipo           VARCHAR(20)  NOT NULL DEFAULT 'POST',
    idReferencia   INT          DEFAULT NULL,
    upvoteCount    INT          NOT NULL DEFAULT 0,
    PRIMARY KEY (idPostagem),
    CONSTRAINT postagem_ibfk_1 FOREIGN KEY (idUsuario) REFERENCES usuario (idUsuario)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE IF NOT EXISTS anexo (
    idAnexo     INT          NOT NULL AUTO_INCREMENT,
    nomeArquivo VARCHAR(100) DEFAULT NULL,
    tipo        VARCHAR(20)  DEFAULT NULL,
    url         VARCHAR(255) DEFAULT NULL,
    idPostagem  INT          DEFAULT NULL,
    PRIMARY KEY (idAnexo),
    CONSTRAINT anexo_ibfk_1 FOREIGN KEY (idPostagem) REFERENCES postagem (idPostagem)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE IF NOT EXISTS mensagem (
    idMensagem    INT         NOT NULL AUTO_INCREMENT,
    texto         TEXT        DEFAULT NULL,
    dataEnvio     DATETIME    DEFAULT NULL,
    lida          TINYINT(1)  DEFAULT NULL,
    idRemetente   INT         DEFAULT NULL,
    idDestinatario INT        DEFAULT NULL,
    tipo          VARCHAR(20) NOT NULL DEFAULT 'MENSAGEM',
    PRIMARY KEY (idMensagem),
    CONSTRAINT mensagem_ibfk_1 FOREIGN KEY (idRemetente)    REFERENCES usuario (idUsuario),
    CONSTRAINT mensagem_ibfk_2 FOREIGN KEY (idDestinatario) REFERENCES usuario (idUsuario)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE IF NOT EXISTS material (
    idMaterial INT          NOT NULL AUTO_INCREMENT,
    titulo     VARCHAR(150) DEFAULT NULL,
    categoria  VARCHAR(50)  DEFAULT NULL,
    arquivo    LONGBLOB     DEFAULT NULL,
    idUsuario  INT          DEFAULT NULL,
    PRIMARY KEY (idMaterial),
    CONSTRAINT material_ibfk_1 FOREIGN KEY (idUsuario) REFERENCES usuario (idUsuario)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
