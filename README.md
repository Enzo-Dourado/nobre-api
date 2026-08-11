# Nobre API

API REST do e-commerce Nobre em Java 21, Spring Boot e PostgreSQL. O Flyway
cria o schema e insere o catálogo inicial automaticamente na primeira execução.

## Início rápido com Docker

Pré-requisito: Docker Desktop em execução.

```powershell
Copy-Item .env.example .env
docker compose up --build
```

Edite `ADMIN_EMAIL` no `.env` antes de criar a conta administrativa. A API
ficará em `http://localhost:8080` e o PostgreSQL em `localhost:5432`.

Confirme a integração:

```powershell
Invoke-RestMethod http://localhost:8080/api/products
```

O volume `nobre_postgres_data` mantém os dados entre reinicializações. Para
parar sem apagar o banco, use `docker compose down`. Para recriar um banco
vazio, use conscientemente `docker compose down -v`.

## Execução sem Docker

Crie um PostgreSQL vazio e configure as variáveis descritas em `.env.example`.
Depois execute:

```powershell
.\mvnw.cmd spring-boot:run
```

O valor de `DATABASE_URL` é uma URL JDBC, por exemplo
`jdbc:postgresql://localhost:5432/nobre`. Não use diretamente uma URL
`postgres://`; provedores gerenciados normalmente exibem ambos os formatos.

## Front-end

O front local roda em `http://localhost:3000` e detecta automaticamente a API
em `http://localhost:8080`. `FRONTEND_URL` aceita origens separadas por vírgula:

```text
FRONTEND_URL=http://localhost:3000,http://127.0.0.1:3000
```

Em produção, configure a origem HTTPS exata do front. Para cookies entre sites
diferentes, configure também `COOKIE_SECURE=true` e `COOKIE_SAME_SITE=None`.

## Testes

Os testes padrão cobrem catálogo, autenticação, cookies, pedidos e autorização
administrativa em um banco isolado:

```powershell
.\mvnw.cmd test
```

Para validar também as migrações e o seed em um PostgreSQL real e temporário:

```powershell
.\mvnw.cmd -Dnobre.postgres.tests=true test
```

Esse segundo comando inicia um processo PostgreSQL local durante o teste; não
precisa de Docker nem altera o banco de desenvolvimento.

## Endpoints

| Método | Rota | Acesso |
|---|---|---|
| `POST` | `/api/auth/register` | Público |
| `POST` | `/api/auth/login` | Público |
| `GET` | `/api/auth/me` | Autenticado |
| `POST` | `/api/auth/logout` | Público |
| `GET` | `/api/products` | Público |
| `POST/PUT/DELETE` | `/api/products` | Administrador |
| `GET/POST` | `/api/orders` | Autenticado |
