package com.ratelimiter.controller;

import com.ratelimiter.model.ClientConfig;
import com.ratelimiter.service.ClientService;
import io.javalin.Javalin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientController {
    private static final Logger logger =
            LoggerFactory.getLogger(ClientController.class);

    private final ClientService clientService;

    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    public void registerRoutes(Javalin app) {

        app.post("/clients", ctx -> {
            ClientConfig clientConfig =
                    ctx.bodyAsClass(ClientConfig.class);
            logger.info(
                    "Received request to create client '{}'.",
                    clientConfig.clientId()
            );
            clientService.createClient(clientConfig);
            ctx.status(201).json(clientConfig);
        });

        app.get("/clients/{clientId}", ctx -> {
            String clientId = ctx.pathParam("clientId");
            logger.info(
                    "Received request to fetch client '{}'.",
                    clientId
            );
            ClientConfig client =
                    clientService.getClient(clientId);
            ctx.json(client);
        });

        app.get("/clients", ctx -> {
            logger.info(
                    "Received request to fetch all clients."
            );
            ctx.json(clientService.getAllClients());
        });

        app.put("/clients/{clientId}", ctx -> {
            String clientId = ctx.pathParam("clientId");
            ClientConfig request =
                    ctx.bodyAsClass(ClientConfig.class);
            ClientConfig updatedClient =
                    new ClientConfig(
                            clientId,
                            request.algorithmType(),
                            request.capacity(),
                            request.refillTokensPerSecond(),
                            request.windowSizeSeconds(),
                            request.enabled()
                    );
            logger.info(
                    "Received request to update client '{}'.",
                    clientId
            );
            clientService.updateClient(updatedClient);
            ctx.json(updatedClient);
        });

        app.delete("/clients/{clientId}", ctx -> {
            String clientId = ctx.pathParam("clientId");
            logger.info(
                    "Received request to delete client '{}'.",
                    clientId
            );
            clientService.deleteClient(clientId);
            ctx.status(204);
        });

        app.post("/clients/{clientId}/reset", ctx -> {
            String clientId = ctx.pathParam("clientId");
            logger.info(
                    "Received request to reset bucket for client '{}'.",
                    clientId
            );
            clientService.resetBucket(clientId);
            ctx.status(204);
        });
    }
}