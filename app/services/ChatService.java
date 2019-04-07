package services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pubnub.api.PNConfiguration;
import com.pubnub.api.PubNub;
import com.pubnub.api.callbacks.PNCallback;
import com.pubnub.api.callbacks.SubscribeCallback;
import com.pubnub.api.models.consumer.PNPublishResult;
import com.pubnub.api.models.consumer.PNStatus;
import com.pubnub.api.models.consumer.pubsub.PNMessageResult;
import com.pubnub.api.models.consumer.pubsub.PNPresenceEventResult;
import com.typesafe.config.Config;
import dao.DataAccessObject;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Collections;
import java.util.List;

@Singleton
public class ChatService {

    private ObjectMapper mapper = new ObjectMapper();
    private DataAccessObject dataAccessObject;
    private Config config;
    PubNub pubnub;

    @Inject
    public ChatService(DataAccessObject dataAccessObject, Config config) {
        this.dataAccessObject = dataAccessObject;
        this.config = config;
        PNConfiguration pnConfiguration = new PNConfiguration();
        pnConfiguration.setSubscribeKey(this.config.getString("pubnub.subscribe.key"));
        pnConfiguration.setPublishKey(this.config.getString("pubnub.publish.key"));
        pubnub = new PubNub(pnConfiguration);
        // for demo purpose only
        pubnub.addListener(new SubscribeCallback() {
            @Override
            public void status(PubNub pubnub, PNStatus status) {
                if (status.getOperation() != null) {
                    switch (status.getOperation()) {
                        // let's combine unsubscribe and subscribe handling for ease of use
                        case PNSubscribeOperation:
                        case PNUnsubscribeOperation:
                            // note: subscribe statuses never have traditional
                            // errors, they just have categories to represent the
                            // different issues or successes that occur as part of subscribe
                            switch (status.getCategory()) {
                                case PNConnectedCategory:
                                    // this is expected for a subscribe, this means there is no error or issue whatsoever
                                case PNReconnectedCategory:
                                    // this usually occurs if subscribe temporarily fails but reconnects. This means
                                    // there was an error but there is no longer any issue
                                case PNDisconnectedCategory:
                                    // this is the expected category for an unsubscribe. This means there
                                    // was no error in unsubscribing from everything
                                case PNUnexpectedDisconnectCategory:
                                    // this is usually an issue with the internet connection, this is an error, handle appropriately
                                case PNAccessDeniedCategory:
                                    // this means that PAM does allow this client to subscribe to this
                                    // channel and channel group configuration. This is another explicit error
                                default:
                                    // More errors can be directly specified by creating explicit cases for other
                                    // error categories of `PNStatusCategory` such as `PNTimeoutCategory` or `PNMalformedFilterExpressionCategory` or `PNDecryptionErrorCategory`
                            }

                        case PNHeartbeatOperation:
                            // heartbeat operations can in fact have errors, so it is important to check first for an error.
                            // For more information on how to configure heartbeat notifications through the status
                            // PNObjectEventListener callback, consult <link to the PNCONFIGURATION heartbeart config>
                            if (status.isError()) {
                                // There was an error with the heartbeat operation, handle here
                            } else {
                                // heartbeat operation was successful
                            }
                        default: {
                            // Encountered unknown status type
                        }
                    }
                } else {
                    // After a reconnection see status.getCategory()
                }
            }

            @Override
            public void message(PubNub pubnub, PNMessageResult message) {
                String messagePublisher = message.getPublisher();
                System.out.println("Message publisher: " + messagePublisher);
                System.out.println("Message Payload: " + message.getMessage());
                System.out.println("Message Subscription: " + message.getSubscription());
                System.out.println("Message Channel: " + message.getChannel());
                System.out.println("Message timetoken: " + message.getTimetoken());
            }

            @Override
            public void presence(PubNub pubnub, PNPresenceEventResult presence) {

            }
        });

    }

    public void createGroup(String groupName, List<String> users) {
        JsonNode payload = mapper.createObjectNode()
                .put("eventType", "join_group")
                .put("channel", "grp:" + groupName);
        dataAccessObject.addGroup(groupName, users);
        for (String user : users) {
            // send join event to channel user:user
            pubnub.publish()
                    .message(payload)
                    .channel("user:" + user)
                    .async(new PNCallback<PNPublishResult>() {
                        @Override
                        public void onResponse(PNPublishResult result, PNStatus status) {
                            // handle publish result, status always present, result if successful
                            // status.isError() to see if error happened
                            if (!status.isError()) {
                                System.out.println("pub timetoken: " + result.getTimetoken());
                            }
                            System.out.println("pub status code: " + status.getStatusCode());
                        }
                    });
        }

    }

    public List<String> listGroups(String userId) {
        return dataAccessObject.getUsersGroups(userId);
    }

    public void subscribe(String userId) {
        System.out.println("subscribing to " + userId);
        pubnub.subscribe()
                .channels(Collections.singletonList("user:" + userId)) // subscribe to channels information
                .execute();
    }
}
