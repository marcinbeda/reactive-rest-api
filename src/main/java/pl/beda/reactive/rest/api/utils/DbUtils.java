package pl.beda.reactive.rest.api.utils;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;

import java.util.Properties;

public class DbUtils {

    private static final String HOST_CONFIG = "db.host";
    private static final String PORT_CONFIG = "db.port";
    private static final String DATABASE_NAME = "db.name";

    public static MongoClient buildDbClient(Vertx vertx) {
        final Properties properties = ConfigUtils.getInstance().getProperties();
        return MongoClient.create(vertx, getDbProperties(properties));
    }

    private static JsonObject getDbProperties(Properties properties) {
        JsonObject dbConfig = new JsonObject();
        dbConfig.put("host", properties.getProperty(HOST_CONFIG));
        dbConfig.put("port", Integer.parseInt(properties.getProperty(PORT_CONFIG)));
        dbConfig.put("db_name", properties.getProperty(DATABASE_NAME));
        return dbConfig;
    }

}
