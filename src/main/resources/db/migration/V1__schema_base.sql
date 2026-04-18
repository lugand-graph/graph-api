-- =============================================================================
-- V1: Schema base — metadados e auditoria do grafo
-- Managed by Flyway. Do NOT edit manually after applied.
-- =============================================================================

-- Extensões
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "pg_trgm";

-- Schema de metadados do grafo
CREATE SCHEMA IF NOT EXISTS graph_meta;

-- Tabela: contextos (espelha labels do Neo4j)
CREATE TABLE IF NOT EXISTS graph_meta.contexts (
    id          UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name        VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at  TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- Sequence para o id da tabela particionada
CREATE SEQUENCE IF NOT EXISTS graph_meta.events_id_seq;

-- Tabela: auditoria de eventos do grafo
-- PK composta inclui created_at (obrigatório em tabelas particionadas por range)
CREATE TABLE IF NOT EXISTS graph_meta.events (
    id          BIGINT NOT NULL DEFAULT nextval('graph_meta.events_id_seq'),
    event_type  VARCHAR(50) NOT NULL,
    context_id  UUID,
    entity_id   VARCHAR(255),
    payload     JSONB,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT now(),
    PRIMARY KEY (id, created_at)
) PARTITION BY RANGE (created_at);

-- Partições 2026
CREATE TABLE IF NOT EXISTS graph_meta.events_2026_q2
    PARTITION OF graph_meta.events
    FOR VALUES FROM ('2026-04-01') TO ('2026-07-01');

CREATE TABLE IF NOT EXISTS graph_meta.events_2026_q3
    PARTITION OF graph_meta.events
    FOR VALUES FROM ('2026-07-01') TO ('2026-10-01');

CREATE TABLE IF NOT EXISTS graph_meta.events_2026_q4
    PARTITION OF graph_meta.events
    FOR VALUES FROM ('2026-10-01') TO ('2027-01-01');

-- Índices
CREATE INDEX IF NOT EXISTS idx_events_type       ON graph_meta.events (event_type);
CREATE INDEX IF NOT EXISTS idx_events_created_at ON graph_meta.events (created_at);
CREATE INDEX IF NOT EXISTS idx_events_payload    ON graph_meta.events USING gin (payload);
