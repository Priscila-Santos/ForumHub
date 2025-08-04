package com.alura.forumhub.domain.topico;

import jakarta.validation.constraints.NotBlank;

public record DadosAtualizacaoTopico(
        @NotBlank
        String titulo,

        @NotBlank
        String mensagem
) {
}
