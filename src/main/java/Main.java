import io.vertx.core.Vertx;
import pl.beda.reactive.rest.api.MainVerticle;

public class Main {

    public static void main(String[] args) {
        final Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(MainVerticle.class.getName()).onFailure(throwable -> System.exit(-1));
    }

}
