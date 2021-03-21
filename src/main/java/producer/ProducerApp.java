package producer;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

public class ProducerApp {

    private static final String EXCHANGE_NAME = "topic_exchange";

    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("localhost");
        try (Connection connection = connectionFactory.newConnection();
                BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
            Channel channel = connection.createChannel();
            channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.TOPIC);

            String msg;
            int topic;

            while (true) {
                System.out.println("Choose your topic: 1 - Java, 2 - Python or 0 for exit");
                try {
                    topic = Integer.parseInt(reader.readLine());
                } catch (NumberFormatException e) {
                    continue;
                }
                switch (topic) {
                    case 1:
                        System.out.println("Write your message:");
                        msg = reader.readLine();
                        channel.basicPublish(EXCHANGE_NAME, "ru.pankov.java", null, msg.getBytes(StandardCharsets.UTF_8));
                        break;
                    case 2:
                        System.out.println("Write your message:");
                        msg = reader.readLine();
                        channel.basicPublish(EXCHANGE_NAME, "ru.pankov.python", null, msg.getBytes(StandardCharsets.UTF_8));
                        break;
                    case 0:
                        return;
                    default:
                        System.out.println("Incorrect topic");
                }
            }
        }
    }
}
