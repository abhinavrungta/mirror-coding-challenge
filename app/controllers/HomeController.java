package controllers;

import auth.VerifiedJwt;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.typesafe.config.Config;
import filters.Attrs;
import play.mvc.Controller;
import play.mvc.Result;
import services.ChatService;
import services.UserService;

import javax.inject.Inject;
import java.util.List;
import java.util.Optional;

public class HomeController extends Controller {
    @Inject
    private Config config;
    @Inject
    private UserService userService;
    @Inject
    private ChatService chatService;
    private ObjectMapper mapper = new ObjectMapper();

    public Result index() {
        return ok("App is ready");
    }

    public Result login() {
        JsonNode body = request().body().asJson();

        if (body == null) {
            return forbidden();
        }

        if (body.hasNonNull("username") && body.hasNonNull("password")) {
            JsonNode response = userService.login(body.get("username").asText(), body.get("password").asText());
            if (response.size() == 0) {
                return forbidden();
            }
            chatService.subscribe(body.get("username").asText()); // For demo purpose only
            return ok(response);
        } else {
            System.out.println("json body not as expected: " + body.toString());
        }

        return forbidden();
    }

    public Result listUsers() {
        JsonNode response = mapper.valueToTree(userService.listUsers());
        return ok(response);
    }

    public Result createGroup(String userId) {
        if (!isJwtMatchingUser(userId)) {
            return forbidden();
        }
        JsonNode body = request().body().asJson();

        if (body == null) {
            return forbidden();
        }

        if (body.hasNonNull("groupName") && body.hasNonNull("users")) {
            String groupName = body.get("groupName").asText();
            List<String> users = mapper.convertValue(body.get("users"), new TypeReference<List<String>>() {
            });
            chatService.createGroup(groupName, users);
            return ok();
        } else {
            System.out.println("json body not as expected: " + body.toString());
        }
        return forbidden();
    }

    public Result listGroups(String userId) {
        if (isJwtMatchingUser(userId)) {
            JsonNode response = mapper.valueToTree(chatService.listGroups(userId));
            return ok(response);
        }
        return forbidden();
    }

    private boolean isJwtMatchingUser(String userId) {
        Optional<VerifiedJwt> oVerifiedJwt = request().attrs().getOptional(Attrs.VERIFIED_JWT);
        return oVerifiedJwt.map(jwt -> jwt.getId().equalsIgnoreCase(userId)).orElse(false);
    }
}