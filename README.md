# Nobre API

Este repositório contém o back-end do e-commerce Nobre. Ele oferece uma API
REST para cadastro, login, catálogo de produtos, pedidos e administração dos
produtos.

O projeto usa Java 21, Spring Boot, PostgreSQL e Flyway. O Flyway cria as
tabelas e insere os 16 produtos iniciais automaticamente quando a API é
iniciada pela primeira vez.

O front-end fica em outro repositório, chamado `nobre-ecommerce`.

## Pré-requisito

Instale e abra o Docker Desktop. Antes de continuar, confirme que ele mostra
**Engine running**.

Não é necessário instalar PostgreSQL ou Maven para usar o modo Docker.

## Iniciar a API e o banco de dados

Abra o PowerShell dentro da pasta `nobre-api`.

Na primeira execução, crie o arquivo local de configuração:

```powershell
Copy-Item .env.example .env
```

O `.env` é ignorado pelo Git. Edite-o se quiser escolher outro e-mail para o
administrador local.

Depois, inicie os serviços:

```powershell
docker compose --env-file .env up -d --build
```

O comando cria e mantém ativos estes serviços:

- `database`: PostgreSQL 17, disponível em `localhost:5432`;
- `api`: aplicação Spring Boot, disponível em `http://localhost:8080`.

Confira o estado:

```powershell
docker compose ps
```

O banco deve aparecer como `healthy`, e a API como `Up`.

Teste o catálogo:

```powershell
(Invoke-RestMethod http://localhost:8080/api/products).Count
```

O resultado esperado é `16`.

## Parar e iniciar novamente

Para parar os serviços sem apagar o banco:

```powershell
docker compose down
```

Para iniciar novamente:

```powershell
docker compose --env-file .env up -d
```

O volume `nobre_postgres_data` preserva usuários, produtos e pedidos entre as
execuções.

O comando abaixo também apaga o volume e todos os dados locais. Use somente
quando realmente quiser recriar o banco do zero:

```powershell
docker compose down -v
```

## Como conectar com o front-end

Com a API ativa, abra outro PowerShell na pasta do repositório
`nobre-ecommerce` e execute:

```powershell
docker compose up -d --build
```

Depois, abra `http://localhost:3000` no navegador.

Em desenvolvimento, o arquivo `js/api.js` do front detecta `localhost` e usa
automaticamente `http://localhost:8080`. A API aceita as origens
`http://localhost:3000` e `http://127.0.0.1:3000` configuradas em
`FRONTEND_URL`.

## Testar o sistema pelo navegador

1. Abra `http://localhost:3000` e confira os 16 produtos.
2. Acesse **Minha conta** e crie um cadastro com um e-mail ainda não usado.
3. Saia e entre novamente para testar o login.
4. Abra um produto, escolha o tamanho e adicione-o ao carrinho.
5. Finalize o checkout simulado preenchendo rua, cidade e forma de pagamento.
6. Abra **Minha conta > Meus pedidos** e confirme que o pedido aparece.

O pagamento é apenas uma simulação acadêmica. Nenhum cartão ou Pix real é
processado.

## Criar e testar a conta administrativa

O valor padrão de desenvolvimento é:

```text
ADMIN_EMAIL=admin@nobre.local
```

Cadastre esse e-mail pelo front. A API atribuirá o papel `ADMIN` à conta. Os
demais e-mails recebem o papel `CUSTOMER`.

Depois do login administrativo, abra:

```text
http://localhost:3000/admin.html
```

No painel, teste criar, editar e excluir um produto temporário. Atualmente o
painel administra produtos; ele não possui gerenciamento de usuários.

## Executar a API pelo IntelliJ

Se quiser depurar o Java no IntelliJ, mantenha apenas o banco no Docker:

```powershell
docker compose stop api
docker compose up -d database
```

No IntelliJ, abra o `pom.xml`, selecione o JDK 21 e crie uma configuração para
`br.com.nobre.api.NobreApiApplication`.

O Spring Boot não lê o arquivo `.env` automaticamente. Copie as variáveis de
`.env.example` para **Run > Edit Configurations > Environment variables**.
Nesse modo, `DATABASE_URL` deve continuar como
`jdbc:postgresql://localhost:5432/nobre`.

## Testes automatizados

Com Java 21 instalado:

```powershell
.\mvnw.cmd test
```

Os testes padrão cobrem catálogo, autenticação, cookie de sessão, pedidos e
autorização administrativa em um banco isolado.

O teste opcional abaixo também inicia um PostgreSQL temporário e valida as
migrações e o catálogo inicial:

```powershell
.\mvnw.cmd -Dnobre.postgres.tests=true test
```

## Endpoints principais

| Método | Rota | Acesso |
|---|---|---|
| `POST` | `/api/auth/register` | Público |
| `POST` | `/api/auth/login` | Público |
| `GET` | `/api/auth/me` | Autenticado |
| `POST` | `/api/auth/logout` | Público |
| `GET` | `/api/products` | Público |
| `POST` | `/api/products` | Administrador |
| `PUT` | `/api/products/{id}` | Administrador |
| `DELETE` | `/api/products/{id}` | Administrador |
| `GET` | `/api/orders` | Autenticado |
| `POST` | `/api/orders` | Autenticado |

## Configuração de produção

Os valores de `.env.example`, como senha `postgres`, e-mail administrativo e
segredo JWT, existem somente para desenvolvimento. Antes de publicar:

- use uma senha forte no PostgreSQL;
- gere um `JWT_SECRET` aleatório com pelo menos 32 caracteres;
- altere `ADMIN_EMAIL` para um endereço controlado por você;
- configure `FRONTEND_URL` com a origem HTTPS exata do front;
- quando front e API estiverem em sites diferentes, use
  `COOKIE_SECURE=true` e `COOKIE_SAME_SITE=None`.

Nunca envie o arquivo `.env` para o GitHub.
