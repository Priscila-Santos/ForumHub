package com.alura.forumhub.domain.resposta;

import jakarta.validation.constraints.NotBlank;

public record DadosAtualizacaoResposta(
        @NotBlank
        String mensagem,

        Boolean solucao
) {}

