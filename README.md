# 📚 ForumHub

ForumHub é uma API REST desenvolvida com **Spring Boot** para gerenciar tópicos de discussão em um fórum técnico.
A aplicação permite **criar, listar, detalhar, atualizar e excluir tópicos** com **autenticação via JWT**, além de gerenciar **respostas**, **curtidas** e **perfis de usuário**. Possui também **busca avançada** e documentação integrada via Swagger.

---

## 🚀 Tecnologias utilizadas

* Java 17
* Spring Boot
* Spring Security
* Spring Data JPA
* Flyway
* MySQL
* H2 (para testes)
* JWT (JSON Web Token)
* Swagger (OpenAPI)
* Maven

---

## ✨ Novas funcionalidades

* **Sistema de Respostas**: usuários podem responder tópicos existentes.
* **Curtidas**: usuários podem curtir/descurtir tópicos e respostas.
* **Perfil de Usuário**: cada usuário possui um perfil com nome, e-mail e lista de tópicos e respostas criados.
* **Busca Avançada**: pesquisa por título, autor ou conteúdo do tópico.
* **Exclusão lógica**: tópicos e respostas não são apagados fisicamente, apenas marcados como inativos.
* **Paginação e ordenação**: listagens retornam resultados paginados.

---

## ⚙️ Configuração do ambiente

### 🔧 Banco de dados

A aplicação utiliza **MySQL** em produção e **H2** para testes.

Configure suas variáveis de ambiente no `application.properties`:

```properties
spring.datasource.username=${DB_MYSQL_USER}
spring.datasource.password=${DB_MYSQL_PASSWORD}
api.security.token.secret=${JWT_SECRET}
```

---

### 📦 Migrações

As migrações são gerenciadas com **Flyway**. Scripts devem ser colocados em:

```
src/main/resources/db/migration
```

---

## 📌 Endpoints principais

| Método | URI                       | Descrição                                    |
| ------ | ------------------------- | -------------------------------------------- |
| POST   | `/login`                  | Autenticação e geração de token JWT          |
| POST   | `/topicos`                | Cadastro de novo tópico                      |
| GET    | `/topicos`                | Listagem paginada de tópicos                 |
| GET    | `/topicos/{id}`           | Detalhamento de tópico                       |
| PUT    | `/topicos/{id}`           | Atualização de tópico                        |
| DELETE | `/topicos/{id}`           | Exclusão lógica de tópico                    |
| POST   | `/topicos/{id}/respostas` | Adicionar resposta a um tópico               |
| POST   | `/topicos/{id}/curtir`    | Curtir um tópico                             |
| DELETE | `/topicos/{id}/curtir`    | Remover curtida de um tópico                 |
| POST   | `/respostas/{id}/curtir`  | Curtir uma resposta                          |
| DELETE | `/respostas/{id}/curtir`  | Remover curtida de uma resposta              |
| GET    | `/usuarios/{id}`          | Consultar perfil de usuário                  |
| GET    | `/busca`                  | Buscar tópicos por título, autor ou conteúdo |

> Todos os endpoints (exceto `/login` e GET `/usuarios`) exigem autenticação via JWT.

---

## 🔐 Autenticação

Após o login, envie o token JWT no header das requisições:

```
Authorization: Bearer <seu_token>
```

---

## 🧪 Testes

Os testes são escritos com **SpringBootTest** e **MockMvc**, utilizando banco **H2** em memória.

Para rodar os testes:

```bash
./mvnw test
```

---

## 📖 Documentação Swagger

A documentação interativa está disponível em:

```
http://localhost:8081/swagger-ui.html
```

---

## 🛠️ Como rodar localmente

1. Clone o repositório:

   ```bash
   git clone https://github.com/seu-usuario/forumhub.git
   ```
2. Configure o banco MySQL e as variáveis de ambiente
3. Execute a aplicação:

   ```bash
   ./mvnw spring-boot:run
   ```

---

## 👩‍💻 Autora

**Priscila**
Desenvolvedora **Java** apaixonada por backend, APIs REST e arquitetura limpa.



