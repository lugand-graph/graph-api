package com.graph.model;

import java.util.List;

/**
 * Payload completo do grafo enviado ao frontend.
 */
public record GraphData(
    List<GraphNode> nodes,
    List<GraphEdge> edges
) {}
