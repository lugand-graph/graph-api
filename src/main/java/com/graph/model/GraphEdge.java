package com.graph.model;

import java.time.Instant;
import java.util.Map;

/**
 * Representa uma aresta do grafo.
 * Mapeado para relacionamentos Neo4j com tipo dinâmico.
 */
public record GraphEdge(
    String id,
    String sourceId,
    String targetId,
    String type,
    double weight,
    Map<String, Object> properties,
    Instant createdAt
) {}
