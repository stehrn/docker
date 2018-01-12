package io.stehrn;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.redis.RedisClient;
import io.vertx.redis.RedisOptions;

/**
 * Deployed from HelloVerticle
 */
public class CounterVerticle extends AbstractVerticle {

    private static final Logger logger = LoggerFactory.getLogger(CounterVerticle.class);

    private static final String COUNTER_ADDRESS = "redis.counter";
    private static final String REDIS_COUNTER_KEY = "counter";

    private RedisClient client;
    private DeliveryOptions deliveryOptions;

    @Override
    public void start() throws Exception {

        long timeout = config().getLong("counter.timeout.ms", 500L);
        logger.info("Redis 'counter.timeout.ms' set to " + timeout + "ms");
        deliveryOptions = new DeliveryOptions().setSendTimeout(timeout);

        String host = config().getString("redis.host", "localhost");
        logger.info("'redis.host' set to " + host);
        client = RedisClient.create(vertx, new RedisOptions().setHost(host));

        // add handler for counter increment
        MessageConsumer<String> consumer = vertx.eventBus().consumer(COUNTER_ADDRESS);
        consumer.handler(message -> client.incr(message.body(), count -> {
            if (count.succeeded()) {
                message.reply(count.result());
            } else {
                message.fail(-1, count.cause().getMessage());
            }
        }));
    }

    /**
     * @return Next counter, if we fail to load then relevant error message shown
     */
    Future<String> count() {
        return next().compose(c -> Future.succeededFuture(Long.toString(c))).otherwise(throwable -> "Failed to get count: " + throwable.getMessage());
    }

    /**
     * @return Send request to event bus for next counter value
     */
    private Future<Long> next() {
        Future<Long> future = Future.future();

        // Message<String> reply = awaitResult(h -> eb.send("someaddress", "ping", h));

        vertx.eventBus().send(COUNTER_ADDRESS, REDIS_COUNTER_KEY, deliveryOptions, r -> {
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
}
