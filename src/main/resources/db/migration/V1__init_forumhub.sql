-- Tabela de cursos
CREATE TABLE cursos (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        nome VARCHAR(255) NOT NULL,
                        categoria VARCHAR(255) NOT NULL
);

-- Tabela de perfis
CREATE TABLE perfis (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        nome VARCHAR(255) NOT NULL
);

-- Tabela de usuários
CREATE TABLE usuarios (
                          id BIGINT AUTO_INCREMENT PRIMARY KEY,
                          nome VARCHAR(255) NOT NULL,
                          email VARCHAR(255) NOT NULL UNIQUE,
                          senha VARCHAR(255) NOT NULL
);

-- Tabela de tópicos
CREATE TABLE topicos (
                         id BIGINT AUTO_INCREMENT PRIMARY KEY,
                         titulo VARCHAR(255) NOT NULL,
                         mensagem TEXT NOT NULL,
                         data_criacao DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                         status ENUM('NAO_RESPONDIDO','NAO_SOLUCIONADO','SOLUCIONADO')
    NOT NULL DEFAULT 'NAO_RESPONDIDO',
                         autor_id BIGINT NOT NULL,
                         curso_id BIGINT NOT NULL,
                         CONSTRAINT fk_topico_autor
                             FOREIGN KEY (autor_id)
                                 REFERENCES usuarios(id)
                                 ON UPDATE CASCADE ON DELETE CASCADE,
                         CONSTRAINT fk_topico_curso
                             FOREIGN KEY (curso_id)
                                 REFERENCES cursos(id)
                                 ON UPDATE CASCADE ON DELETE CASCADE
);

-- Tabela de respostas
CREATE TABLE respostas (
                           id BIGINT AUTO_INCREMENT PRIMARY KEY,
                           mensagem TEXT NOT NULL,
                           data_criacao DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                           solucao BOOLEAN NOT NULL DEFAULT FALSE,
                           topico_id BIGINT NOT NULL,
                           autor_id BIGINT NOT NULL,
                           CONSTRAINT fk_resposta_topico
                               FOREIGN KEY (topico_id)
                                   REFERENCES topicos(id)
                                   ON UPDATE CASCADE ON DELETE CASCADE,
                           CONSTRAINT fk_resposta_autor
                               FOREIGN KEY (autor_id)
                                   REFERENCES usuarios(id)
                                   ON UPDATE CASCADE ON DELETE CASCADE
);

-- Tabela de ligação usuário-perfil
CREATE TABLE usuario_perfis (
                                usuario_id BIGINT NOT NULL,
                                perfil_id BIGINT NOT NULL,
                                CONSTRAINT fk_up_usuario
                                    FOREIGN KEY (usuario_id)
                                        REFERENCES usuarios(id)
                                        ON UPDATE CASCADE ON DELETE CASCADE,
                                CONSTRAINT fk_up_perfil
                                    FOREIGN KEY (perfil_id)
                                        REFERENCES perfis(id)
                                        ON UPDATE CASCADE ON DELETE CASCADE,
                                PRIMARY KEY (usuario_id, perfil_id)
);