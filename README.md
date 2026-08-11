# Nobre API

API REST leve em Java 21 e Spring Boot para o front-end Nobre. Este diretório é
autônomo e pode ser movido para um repositório Git separado.

## Execução

1. Crie um banco PostgreSQL vazio.
2. Copie `.env.example` e exporte as variáveis no ambiente.
3. Execute `mvn spring-boot:run`.

O Flyway cria as tabelas e inclui o catálogo inicial. A primeira conta criada
com o endereço configurado em `ADMIN_EMAIL` recebe acesso administrativo.

Para conectar um front hospedado separadamente, configure a origem exata em
`FRONTEND_URL`; no front, defina `window.NOBRE_API_URL` antes de carregar
`js/api.js`. Consulte o README principal para os exemplos completos.
