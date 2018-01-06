package io.stehrn;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;

import java.net.InetAddress;
import java.net.UnknownHostException;

import static io.vertx.core.Future.future;

public class HelloVerticle extends AbstractVerticle {

    @Override
    public void start(Future<Void> fut) {
        CounterVerticle counter = new CounterVerticle();
        vertx.deployVerticle(counter);
        vertx
                .createHttpServer()
                .requestHandler(r -> {

                    Future<Long> count = future(c -> {
                        c.result();
                    });

                    counter.next(count);

                    count.compose(v -> {
                        return Future.future(x -> {
                            r.response()
                                    .putHeader("content-type", "text/html")
                                    .end("Hello, hostname: " + hostname() + ", count: " + count.result());
                        });
                    });


                    // tosdo, give it 3 seconds to get count, then fail
//                    counter.next(future(count -> {
//                        r.response()
//                                .putHeader("content-type", "text/html")
//                                .end("Hello, hostname: " + hostname() + ", count: " + count.result());
//                    }));
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

    //http://vertx.io/docs/vertx-sync/java/
    public static void main(String[] args) {

        Future<Long> future = Future.<Long>future();

        future.compose(h -> {
            System.out.println("great!" + h.toString());
        }, Future.succeededFuture("what"));

//        future.otherwise(0L).setHandler(h -> {
//            System.out.printf("??" + h.result());
//        });


        future.complete(2L);
       // future.handle(Future.succeededFuture(3L));
        //future.handle(Future.failedFuture("broke"));



        // Future<Long> res = Future.succeededFuture(4L);
        // future.handle(res);

        //future.compose(aLong -> Future.succeededFuture("a result:" + aLong));

        // fail(future.completer());


    }

    static void success(Handler<AsyncResult<Long>> completer) {
        completer.handle(Future.succeededFuture(4L));
    }

    static void fail(Handler<AsyncResult<Long>> completer) {
        completer.handle(Future.failedFuture("Broke"));
    }
}
