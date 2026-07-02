package com.ratelimiter.exception;

import com.ratelimiter.model.ErrorResponse;
import io.javalin.Javalin;

public final class GlobalExceptionHandler {

    private GlobalExceptionHandler() {}

    public static void register(Javalin app) {

        app.exception(ClientNotFoundException.class, (e, ctx) -> {
            ctx.status(404);
            ctx.json(new ErrorResponse(
                    404,
                    e.getMessage(),
                    System.currentTimeMillis()
            ));
        });

        app.exception(InvalidRequestException.class, (e, ctx) -> {
            ctx.status(400);
            ctx.json(new ErrorResponse(
                    400,
                    e.getMessage(),
                    System.currentTimeMillis()
            ));
        });

        app.exception(ConfigurationException.class, (e, ctx) -> {
            ctx.status(500);
            ctx.json(new ErrorResponse(
                    500,
                    e.getMessage(),
                    System.currentTimeMillis()
            ));
        });

        app.exception(BucketPersistenceException.class, (e, ctx) -> {
            ctx.status(500);
            ctx.json(new ErrorResponse(
                    500,
                    e.getMessage(),
                    System.currentTimeMillis()
            ));
        });

        app.exception(Exception.class, (e, ctx) -> {
            ctx.status(500);
            ctx.json(new ErrorResponse(
                    500,
                    "Internal Server Error",
                    System.currentTimeMillis()
            ));
        });
    }
}