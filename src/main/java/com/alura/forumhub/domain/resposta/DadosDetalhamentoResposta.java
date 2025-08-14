package com.alura.forumhub.domain.resposta;

public record DadosDetalhamentoResposta(
        Long id,
        String mensagem,
        String autor,
        String topico,
        Boolean solucao,
        String dataCriacao
) {}

