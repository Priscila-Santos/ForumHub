package com.alura.forumhub.domain.topico;

import java.time.LocalDateTime;

public record DadosDetalhamentoTopico(
        Long id,
        String titulo,
        String mensagem,
        String nomeAutor,
        String nomeCurso,
        LocalDateTime dataCriacao,
        String status
) {
    public DadosDetalhamentoTopico(Topico topico) {
        this(
                topico.getId(),
                topico.getTitulo(),
                topico.getMensagem(),
                topico.getAutor().getNome(),
                topico.getCurso().getNome(),
                topico.getDataCriacao(),
                topico.getStatus().name()
        );
    }

}


