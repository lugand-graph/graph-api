package com.graph.repository;

import com.graph.model.GraphData;
import com.graph.model.GraphEdge;
import com.graph.model.GraphNode;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Values;

import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * Repositório Neo4j — operações base de leitura e escrita no grafo.
 */
@ApplicationScoped
public class GraphRepository {

    @Inject
    Driver driver;

    public GraphData findAll() {
        try (var session = driver.session()) {
            var nodes = session.run("""
                MATCH (n)
                RETURN elementId(n) AS id,
                       labels(n)[0] AS label,
                       n.name AS name,
                       n.context AS context,
                       n.created_at AS createdAt
                LIMIT 500
                """)
                .list(r -> new GraphNode(
                    r.get("id").asString(),
                    r.get("label").asString("Node"),
                    r.get("name").asString(""),
                    r.get("context").asString("default"),
                    Map.of(),
                    r.get("createdAt").isNull() ? Instant.now()
                        : Instant.ofEpochMilli(r.get("createdAt").asLong())
                ));

            var edges = session.run("""
                MATCH (a)-[r]->(b)
                RETURN elementId(r) AS id,
                       elementId(a) AS sourceId,
                       elementId(b) AS targetId,
                       type(r) AS type,
                       coalesce(r.weight, 1.0) AS weight,
                       r.created_at AS createdAt
                LIMIT 1000
                """)
                .list(r -> new GraphEdge(
                    r.get("id").asString(),
                    r.get("sourceId").asString(),
                    r.get("targetId").asString(),
                    r.get("type").asString("CONNECTS"),
                    r.get("weight").asDouble(1.0),
                    Map.of(),
                    r.get("createdAt").isNull() ? Instant.now()
                        : Instant.ofEpochMilli(r.get("createdAt").asLong())
                ));

            return new GraphData(nodes, edges);
        }
    }

    public GraphNode createNode(String label, String name, String context) {
        try (var session = driver.session()) {
            var id = java.util.UUID.randomUUID().toString();
            var now = Instant.now().toEpochMilli();
            session.run("""
                CREATE (n:%s {id: $id, name: $name, context: $context, created_at: $createdAt})
                """.formatted(label),
                Values.parameters("id", id, "name", name, "context", context, "createdAt", now));
            return new GraphNode(id, label, name, context, Map.of(), Instant.ofEpochMilli(now));
        }
    }

    public GraphEdge createEdge(String sourceId, String targetId, String type, double weight) {
        try (var session = driver.session()) {
            var id = java.util.UUID.randomUUID().toString();
            var now = Instant.now().toEpochMilli();
            session.run("""
                MATCH (a {id: $sourceId}), (b {id: $targetId})
                CREATE (a)-[r:%s {id: $id, weight: $weight, created_at: $createdAt}]->(b)
                """.formatted(type),
                Values.parameters("sourceId", sourceId, "targetId", targetId,
                    "id", id, "weight", weight, "createdAt", now));
            return new GraphEdge(id, sourceId, targetId, type, weight, Map.of(),
                Instant.ofEpochMilli(now));
        }
    }
}
