package io.stehrn;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class HelloVerticle extends AbstractVerticle {

    @Override
    public void start(Future<Void> fut) {
        CounterVerticle counterVerticle = new CounterVerticle();
        vertx.deployVerticle(counterVerticle);
        vertx
                .createHttpServer()
                .requestHandler(r -> {

                    CompositeFuture.all(hostname(), counterVerticle.count()).setHandler(h -> {
                        CompositeFuture result = h.result();
                        String hostname = result.resultAt(0);
                        String count = result.resultAt(1);
                        r.response()
                                .putHeader("content-type", "text/html")
                                .end("Hello, hostname: " + hostname + ", count: " + count);
                    });
                })
                .listen(8080, result -> {
                    if (result.succeeded()) {
                        fut.complete();
                    } else {
                        fut.fail(result.cause());
                    }
                });
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
