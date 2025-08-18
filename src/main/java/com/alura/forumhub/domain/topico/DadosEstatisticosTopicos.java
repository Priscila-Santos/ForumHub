package com.alura.forumhub.domain.topico;

public record DadosEstatisticosTopicos(
        String curso,
        long totalTopicos,
        long topicosUltimaSemana,
        long totalRespostas

) {
}
