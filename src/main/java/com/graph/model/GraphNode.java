package com.graph.model;

import java.time.Instant;
import java.util.Map;

/**
 * Representa um nó do grafo.
 * Mapeado para nós Neo4j com label dinâmico.
 */
public record GraphNode(
    String id,
    String label,
    String name,
    String context,
    Map<String, Object> properties,
    Instant createdAt
) {}
