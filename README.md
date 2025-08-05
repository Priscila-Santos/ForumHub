# 📚 ForumHub

ForumHub é uma API REST desenvolvida com Spring Boot para gerenciar tópicos de discussão em um fórum técnico. A aplicação permite o cadastro, listagem, detalhamento, atualização e exclusão lógica de tópicos, com autenticação via JWT e documentação integrada via Swagger.

---

## 🚀 Tecnologias utilizadas

- Java 17
- Spring Boot
- Spring Security
- Spring Data JPA
- Flyway
- MySQL
- H2 (para testes)
- JWT (JSON Web Token)
- Swagger (OpenAPI)
- Maven

---

## ⚙️ Configuração do ambiente

### 🔧 Banco de dados

A aplicação utiliza MySQL em produção e H2 em testes.

Configure suas variáveis de ambiente no `application.properties`:

```properties
spring.datasource.username=${DB_MYSQL_USER}
spring.datasource.password=${DB_MYSQL_PASSWORD}
api.security.token.secret=${JWT_SECRET:12345678}
```

### 📦 Migrações

As migrações são gerenciadas com Flyway. Scripts devem ser colocados em:

```
src/main/resources/db/migration
```

---

## 📌 Endpoints principais

| Método | URI               | Descrição                          |
|--------|-------------------|-------------------------------------|
| POST   | `/login`          | Autenticação e geração de token JWT |
| POST   | `/topicos`        | Cadastro de novo tópico             |
| GET    | `/topicos`        | Listagem paginada de tópicos        |
| GET    | `/topicos/{id}`   | Detalhamento de tópico              |
| PUT    | `/topicos/{id}`   | Atualização de tópico               |
| DELETE | `/topicos/{id}`   | Exclusão lógica de tópico           |

> Todos os endpoints (exceto `/login`) exigem autenticação via JWT.

---

## 🔐 Autenticação

Após o login, envie o token JWT no header das requisições:

```
Authorization: Bearer <seu_token>
```

---

## 🧪 Testes

Os testes são escritos com `SpringBootTest` e `MockMvc`, utilizando banco H2 em memória.

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
Desenvolvedora Java apaixonada por backend, APIs REST e arquitetura limpa.

