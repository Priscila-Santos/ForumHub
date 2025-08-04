package com.alura.forumhub.domain.topico;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface TopicoRepository extends JpaRepository<Topico, Long> {
    boolean existsByTituloAndMensagem(String titulo, String mensagem);

    Page<Topico> findByCursoNomeAndDataCriacaoBetween(
            String cursoNome,
            LocalDateTime inicio,
            LocalDateTime fim,
            Pageable pageable
    );

}


