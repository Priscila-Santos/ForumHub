package com.alura.forumhub.domain.resposta;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RespostaRepository extends JpaRepository<Resposta, Long> {
    // Conta respostas associadas a tópicos ativos de um dado curso
    long countByTopicoCursoCategoria(String categoriaCurso);

}
