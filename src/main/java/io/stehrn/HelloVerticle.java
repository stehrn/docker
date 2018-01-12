package io.stehrn;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.CompositeFuture;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class HelloVerticle extends AbstractVerticle {

    private static final Logger logger = LoggerFactory.getLogger(HelloVerticle.class);

    @Override
    public void start(Future<Void> startFuture) {
        CounterVerticle counterVerticle = deployCounterVerticle();

        vertx
                .createHttpServer()
                .requestHandler(r -> CompositeFuture.all(hostname(), counterVerticle.count()).setHandler(h -> {
                    CompositeFuture result = h.result();
                    String hostname = result.resultAt(0);
                    String count = result.resultAt(1);
                    r.response()
                            .putHeader("content-type", "text/html")
                            .end("Hello, hostname: " + hostname + ", count: " + count);
                }))
                .listen(port(), result -> {
                    if (result.succeeded()) {
                        startFuture.complete();
                    } else {
                        startFuture.fail(result.cause());
                    }
                });
    }

    private int port() {
        int port = config().getInteger("http.port", 8080);
        logger.info("Listening on 'http.port': " + port);
        return port;
    }

    private CounterVerticle deployCounterVerticle() {
        CounterVerticle counterVerticle = new CounterVerticle();
        vertx.deployVerticle(counterVerticle, new DeploymentOptions().setConfig(config()));
        return counterVerticle;
    }

    private Future<String> hostname() {
        Future<String> future = Future.future();
        try {
            InetAddress ip = InetAddress.getLocalHost();
            future.complete(ip.getHostName());
        } catch (UnknownHostException e) {
            future.fail(e);
        }
        return future;
    }
}
