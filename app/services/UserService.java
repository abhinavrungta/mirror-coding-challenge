package services;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.typesafe.config.Config;
import dao.DataAccessObject;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;

@Singleton
public class UserService {

    private ObjectMapper mapper;
    private DataAccessObject dataAccessObject;
    private Config config;

    @Inject
    public UserService(DataAccessObject dataAccessObject, Config config) {
        this.dataAccessObject = dataAccessObject;
        this.config = config;
        this.mapper = new ObjectMapper();
    }

    public JsonNode login(String username, String password) {
        if (this.dataAccessObject.getUserPasswd(username) != null && !this.dataAccessObject.getUserPasswd(username).equals(password)) {
            return mapper.createObjectNode();
        }
        if (this.dataAccessObject.getUserPasswd(username) == null) {
            this.dataAccessObject.setUserPasswd(username, password);
        }
        ObjectNode result = mapper.createObjectNode();
        result.put("access_token", getSignedToken(username));
        result.put("publishkey", config.getString("pubnub.publish.key"));
        result.put("subscribekey", config.getString("pubnub.subscribe.key"));
        result.put("uuid", username);
        result.put("channel", "user:" + username);
        result.put("nextstep", "in the client, subscribe to channel user:uuid\n Listen for join events to subscribe new channels or listen for incoming direct messages and display it accordingly based on room id.");
        return result;
    }

    private String getSignedToken(String id) {
        String secret = config.getString("play.http.secret.key");

        Algorithm algorithm = Algorithm.HMAC256(secret);
        return JWT.create()
                .withIssuer("Mirror")
                .withClaim("id", id)
                .withExpiresAt(Date.from(ZonedDateTime.now(ZoneId.systemDefault()).plusMinutes(60).toInstant()))
                .sign(algorithm);
    }

    public List<String> listUsers() {
        return dataAccessObject.getUsersList();
    }
}
