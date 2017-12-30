package io.stehrn;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class Hello extends AbstractVerticle {

    @Override
    public void start(Future<Void> fut) {
        vertx
                .createHttpServer()
                .requestHandler(r -> {
                    r.response().end("Hello, hostname: " + hostname());
                })
                .listen(8080, result -> {
                    if (result.succeeded()) {
                        fut.complete();
                    } else {
                        fut.fail(result.cause());
                    }
                });
    }

    private String hostname() {
        try {
            InetAddress ip = InetAddress.getLocalHost();
            return ip.getHostName();
        } catch (UnknownHostException e) {
            throw new RuntimeException("Failed to get hostname", e);
        }
    }
}
