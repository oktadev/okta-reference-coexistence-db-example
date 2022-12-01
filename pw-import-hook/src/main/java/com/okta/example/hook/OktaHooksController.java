package com.okta.example.hook;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class OktaHooksController {

    private final PasswordValidator passwordValidator;

    public OktaHooksController(PasswordValidator passwordValidator) { // <.>
        this.passwordValidator = passwordValidator;
    }

    @PostMapping("/pwhook")
    HookResponse passwordImportHook(@RequestBody JsonNode node) {

        // traverse the payload body and read `data.context.credential`
        JsonNode credentials =  node.get("data").get("context").get("credential");
        String username = credentials.get("username").asText(); // <.>
        String password = credentials.get("password").asText(); // <.>

        // validate the password
        boolean result = passwordValidator.isPasswordValid(username, password);
        String status = result ? "VERIFIED" : "UNVERIFIED"; // <.>

        return new HookResponse(List.of(
                new Command("com.okta.action.update",
                        Map.of("credential", status))));
    }

    // Define response object as records <.>
    public record HookResponse(List<Command> commands) {}
    public record Command(String type, Map<String, Object> value) {}
}
