package il.cshaifasweng.OCSFMediatorExample.server;

import il.cshaifasweng.OCSFMediatorExample.entities.Message;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.AbstractServer;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.ConnectionToClient;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.SubscribedClient;

import java.io.IOException;
import java.util.ArrayList;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;

public class SimpleServer extends AbstractServer {
    private static ArrayList<SubscribedClient> SubscribersList = new ArrayList<>();

    public SimpleServer(int port) {
        super(port);

    }

    @Override
    protected void handleMessageFromClient(Object msg, ConnectionToClient client) {
        Message message = (Message) msg;
        String request = message.getMessage();
        try {
            //we got an empty message, so we will send back an error message with the error details.
            if (request.isBlank()) {
                message.setMessage("Error! we got an empty message");
                client.sendToClient(message);
            }
            //we got a request to change submitters IDs with the updated IDs at the end of the string, so we save
            // the IDs at data field in Message entity and send back to all subscribed clients a request to update
            //their IDs text fields. An example of use of observer design pattern.
            //message format: "change submitters IDs: 123456789, 987654321"
            else if (request.startsWith("change submitters IDs:")) {
                message.setData(request.substring(23));
                message.setMessage("update submitters IDs");
                sendToAllClients(message);
            }
            //we got a request to add a new client as a subscriber.
            else if (request.equals("add client")) {
                SubscribedClient connection = new SubscribedClient(client);
                SubscribersList.add(connection);
                message.setMessage("client added successfully");
                client.sendToClient(message);
            }
            //we got a message from client requesting to echo Hello, so we will send back to client Hello world!
            else if (request.startsWith("echo Hello")) {
                message.setMessage("Hello World!");
                client.sendToClient(message);
            } else if (request.startsWith("send Submitters IDs")) {
                message.setMessage("308571447, 206130635");
                client.sendToClient(message);
            } else if (request.startsWith("send Submitters")) {
                message.setMessage("Ohad, Adam");
                client.sendToClient(message);
            } else if (request.equals("what day it is?")) {
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd");
                LocalDateTime today = LocalDateTime.now();
                message.setMessage(dtf.format(today));
                client.sendToClient(message);
            } else if (request.startsWith("add")) {
                String num1 = request.substring(4, 5); //here problem need split**********************
                String num2 = request.substring(6);
                int n1 = Integer.parseInt(num1);
                int n2 = Integer.parseInt(num2);
                int ans = n1 + n2;
                String outcome = Integer.toString(ans);
                message.setMessage(outcome);
                client.sendToClient(message);
            } else {
                message.setMessage(request);
                client.sendToClient(message);
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    public void sendToAllClients(Message message) {
        try {
            for (SubscribedClient SubscribedClient : SubscribersList) {
                SubscribedClient.getClient().sendToClient(message);
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

}
