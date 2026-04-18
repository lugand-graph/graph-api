package com.graph.resource;

import com.graph.model.GraphData;
import com.graph.model.GraphEdge;
import com.graph.model.GraphNode;
import com.graph.repository.GraphRepository;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

/**
 * REST API do grafo.
 * GET  /api/graph          — retorna todos os nós e arestas
 * POST /api/graph/nodes    — cria um nó
 * POST /api/graph/edges    — cria uma aresta
 */
@Path("/api/graph")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class GraphResource {

    @Inject
    GraphRepository repository;

    @GET
    public GraphData getGraph() {
        return repository.findAll();
    }

    @POST
    @Path("/nodes")
    public GraphNode createNode(CreateNodeRequest req) {
        return repository.createNode(req.label(), req.name(), req.context());
    }

    @POST
    @Path("/edges")
    public GraphEdge createEdge(CreateEdgeRequest req) {
        return repository.createEdge(req.sourceId(), req.targetId(), req.type(), req.weight());
    }

    public record CreateNodeRequest(String label, String name, String context) {}
    public record CreateEdgeRequest(String sourceId, String targetId, String type, double weight) {}
}
