package io.stehrn;


import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.redis.RedisClient;
import io.vertx.redis.RedisOptions;

public class CounterVerticle extends AbstractVerticle {

    private static final String DEFAULT_HOST = "localhost";// "127.0.0.1";

    private RedisClient client;

    @Override
    public void start() throws Exception {
        // If a config file is set, read the host and port.
        String host = Vertx.currentContext().config().getString("host");
        if (host == null) {
            host = DEFAULT_HOST;
        }

        // Create the redis client
        client = RedisClient.create(vertx, new RedisOptions().setHost(host));

        // add handler for counter increment
        MessageConsumer<String> consumer = vertx.eventBus().consumer("redis.counter");
        consumer.handler(message -> {
            client.incr(message.body(), count -> {
                if (count.succeeded()) {
                    message.reply(count.result());
                } else {
                    message.fail(-1, count.cause().getMessage());
                }
            });
        });
    }

    Future<String> count() {
        return next().compose(c -> Future.succeededFuture(Long.toString(c))).otherwise(throwable -> "Failed to get count: " + throwable.getMessage());
    }

    Future<Long> next() {
        Future<Long> future = Future.future();
        DeliveryOptions options = new DeliveryOptions();
        options.setSendTimeout(500L);

        // Message<String> reply = awaitResult(h -> eb.send("someaddress", "ping", h));

        vertx.eventBus().send("redis.counter", "counter", options, r -> {
            if (r.succeeded()) {
                long count = (Long) r.result().body();
                future.complete(count);
            } else {
                future.fail(r.cause());
            }
        });
        return future;
    }

    @Override
    public void stop() throws Exception {
        if (client != null) {
            client.close(r -> {
                if (r.failed()) {
                    System.out.println("Close operation failed: " + r.cause().getMessage());
                }
            });
        }
    }

    public static void main(String[] args) {

    }
}
