package com.alura.forumhub.domain.usuario;

import jakarta.validation.constraints.NotBlank;

public record DadosAtualizacaoUsuario(
        @NotBlank
        String nome,

        @NotBlank
        String senha
) {}

