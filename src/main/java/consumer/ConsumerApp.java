package consumer;

import com.rabbitmq.client.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

public class ConsumerApp {

    private static final String EXCHANGE_NAME = "topic_exchange";

    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("localhost");
        Connection connection = connectionFactory.newConnection();
        Channel channel = connection.createChannel();

        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.TOPIC);

        String queue = channel.queueDeclare().getQueue();
        String route = "ru.pankov.java";
        channel.queueBind(queue, EXCHANGE_NAME, route);

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String msg = new String(delivery.getBody(), StandardCharsets.UTF_8);
            System.out.println("Received: '" + msg + "'");
        };

        channel.basicConsume(queue, true, deliverCallback, consumerTag -> {});

        String anotherRoute;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
            while (true) {
                System.out.println("Write your route for change (current is [" + route + "])");
                anotherRoute = reader.readLine();
                channel.queueUnbind(queue, EXCHANGE_NAME, route);
                route = anotherRoute;
                channel.queueBind(queue, EXCHANGE_NAME, route);
            }
        }
    }
}
