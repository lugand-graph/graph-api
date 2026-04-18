# graph-api

Backend REST da plataforma Graph — Quarkus 3 / Java 21.

Expõe os dados do Neo4j como API REST e aplica o schema do PostgreSQL via Flyway.

## Stack

| | |
|---|---|
| Framework | Quarkus 3.34.5 |
| Java | 21 |
| Banco de grafo | Neo4j 5.26 (driver `quarkus-neo4j`) |
| Banco relacional | PostgreSQL 16 (`quarkus-jdbc-postgresql`) |
| Migrations | Flyway (`quarkus-flyway`) |

## Pré-requisitos

- Java 21
- Neo4j e PostgreSQL rodando (via `graph-workspace/docker-compose.yml`)
- Variáveis de ambiente definidas (ver abaixo)

## Variáveis de Ambiente

Carregadas automaticamente de `src/main/resources/application.yml`.  
Em dev mode, o Quarkus lê o arquivo `.env` na raiz de `graph-workspace`.

| Variável | Padrão | Descrição |
|---|---|---|
| `NEO4J_URI` | `bolt://localhost:7687` | URI do Neo4j |
| `NEO4J_USERNAME` | `neo4j` | Usuário Neo4j |
| `NEO4J_PASSWORD` | `lugand00` | Senha Neo4j |
| `PG_HOST` | `localhost` | Host PostgreSQL |
| `PG_PORT` | `5433` | Porta PostgreSQL |
| `PG_DB` | `graph_meta` | Nome do banco |
| `PG_USER` | `graph_user` | Usuário PostgreSQL |
| `PG_PASSWORD` | `lugand00` | Senha PostgreSQL |

## Rodar em modo dev

```bash
./mvnw quarkus:dev
```

- API REST: http://localhost:8080/api/graph
- Dev UI: http://localhost:8080/q/dev/
- GraphQL UI: http://localhost:8080/q/graphql-ui

O Flyway aplica as migrations automaticamente ao iniciar.

## Endpoints

| Método | Path | Descrição |
|---|---|---|
| `GET` | `/api/graph` | Todos os nós e arestas |
| `GET` | `/api/graph/nodes` | Apenas nós |
| `GET` | `/api/graph/edges` | Apenas arestas |
| `POST` | `/api/graph/nodes` | Criar nó |
| `POST` | `/api/graph/edges` | Criar aresta |

### Exemplo

```bash
curl http://localhost:8080/api/graph
# { "nodes": [...], "edges": [...] }

curl -X POST http://localhost:8080/api/graph/nodes \
  -H "Content-Type: application/json" \
  -d '{"id":"abc-123","label":"Concept","name":"Grafo de Conhecimento"}'
```

## Estrutura

```
src/main/java/com/graph/
├── model/
│   ├── GraphData.java      ← { nodes, edges }
│   ├── GraphNode.java      ← { id, label, name, description, ... }
│   └── GraphEdge.java      ← { id, sourceId, targetId, type }
├── repository/
│   └── GraphRepository.java ← queries Cypher via Neo4j driver
└── resource/
    ├── GraphResource.java  ← endpoints JAX-RS
    └── CorsFilter.java     ← CORS para localhost:5173

src/main/resources/
├── application.yml         ← configuração Quarkus
└── db/migration/
    └── V1__schema_base.sql ← schema PostgreSQL (Flyway)
```

## Build

```bash
# Compilar
./mvnw compile -q

# Build completo (gera JAR)
./mvnw package -DskipTests

# Rodar o JAR gerado
java -jar target/quarkus-app/quarkus-run.jar
```

## ADRs Relacionadas

- [ADR-003 — Quarkus como backend](../graph-docs/adr/ADR-003-quarkus-backend.md)
- [ADR-002 — Neo4j como banco principal](../graph-docs/adr/ADR-002-neo4j-graph-database.md)
