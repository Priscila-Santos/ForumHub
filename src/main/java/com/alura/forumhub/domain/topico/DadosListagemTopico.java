package com.alura.forumhub.domain.topico;

import java.time.LocalDateTime;

public record DadosListagemTopico(
        String titulo,
        String mensagem,
        LocalDateTime dataCriacao,
        String status,
        String autor,
        String curso
) {
    public DadosListagemTopico(Topico topico) {
        this(
                topico.getTitulo(),
                topico.getMensagem(),
                topico.getDataCriacao(),
                topico.getStatus().toString(),
                topico.getAutor().getNome(),
                topico.getCurso().getCategoria()
        );
    }
}
