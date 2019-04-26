package army.helpful.prosoha.message.publisher;


import army.helpful.prosoha.message.RestClient;
import army.helpful.prosoha.message.model.Transaction;

import army.helpful.prosoha.message.model.User;
import io.swagger.annotations.*;
import org.keycloak.KeycloakPrincipal;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.keycloak.representations.AccessToken;
import org.keycloak.representations.IDToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.http.MediaType;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

import static org.springframework.http.HttpStatus.OK;


@EnableBinding(Source.class)
@RestController
@RequestMapping("/transaction")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class TransactionController
{
    @Autowired
    private Source source;

    @Autowired
    private RestClient restClient;
    private static final Logger logger = LoggerFactory.getLogger(TransactionController.class);

    @PostMapping(value = "/sendThankCoin", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "Is blocked in NAR.")
    @ApiImplicitParams(@ApiImplicitParam(name = "Authorization", value = "JWT authorization token", required = true, dataType = "string", paramType = "header"))
    @ApiResponses({@ApiResponse(code = 200, message = "NAR status.")})
    public void sendThankCoin(KeycloakAuthenticationToken kat, @RequestBody Transaction transaction) {

        KeycloakPrincipal keycloakPrincipal= (KeycloakPrincipal) kat.getPrincipal();
        AccessToken token= keycloakPrincipal.getKeycloakSecurityContext().getToken();
        String senderUsername= token.getPreferredUsername();
        User sender= new User();
        sender.setUsername(senderUsername);

        transaction.setSender(sender);


        String receiverUsername= transaction.getReceiver().getUsername();
        User receiver= new User();
        receiver.setUsername(receiverUsername);
        transaction.setReceiver(receiver);

        Message resultMessage =  MessageBuilder
                .withPayload(transaction)
                .setHeader("action"
                        , "sendThankCoin")
                .build();
        source.output().send(resultMessage);



        logger.info("changeProfilePhotoUrl", transaction);

    }

}