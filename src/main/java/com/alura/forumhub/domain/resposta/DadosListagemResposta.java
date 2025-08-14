package com.alura.forumhub.domain.resposta;

public record DadosListagemResposta(
        Long id,
        String mensagem,
        String autor,
        Boolean solucao
) {}

