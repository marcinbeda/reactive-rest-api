package pl.beda.reactive.rest.api.utils;

import io.vertx.ext.auth.JWTOptions;
import io.vertx.ext.auth.PubSecKeyOptions;
import io.vertx.ext.auth.jwt.JWTAuthOptions;

import java.util.Properties;

public class JwtUtils {

    public static JWTAuthOptions getConfiguration() {
        final Properties properties = ConfigUtils.getInstance().getProperties();
        JWTAuthOptions jwtAuthOptions = new JWTAuthOptions();
        jwtAuthOptions.addPubSecKey(
                new PubSecKeyOptions()
                        .setAlgorithm(properties.getProperty("jwt.algorithm"))
                        .setBuffer(properties.getProperty("jwt.secret")));
        jwtAuthOptions.setJWTOptions(
                new JWTOptions()
                        .setExpiresInSeconds(Integer.parseInt(properties.getProperty("jwt.expires.in.seconds"))));
        return jwtAuthOptions;
    }

}
