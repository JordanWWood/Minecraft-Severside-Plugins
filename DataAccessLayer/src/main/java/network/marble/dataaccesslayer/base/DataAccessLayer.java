package network.marble.dataaccesslayer.base;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import network.marble.dataaccesslayer.exceptions.APIException;
import network.marble.dataaccesslayer.managers.CacheManager;
import network.marble.dataaccesslayer.models.GlobalVariable;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeoutException;
import java.util.logging.Logger;

public class DataAccessLayer
{
    public static DataAccessLayer instance;

    private static Connection rabbitMQConnection;
    private static String rabbitMQUrl;

    public Logger logger;

    public DataAccessLayer(Logger logger) {
        this.logger = logger;
    }

    public Connection getRabbitMQConnection() {
        if(rabbitMQConnection != null) return rabbitMQConnection;
        try {
            ConnectionFactory connectionFactory = new ConnectionFactory();
            if (rabbitMQUrl == null || rabbitMQUrl.isEmpty())
            {
                try {
                    GlobalVariable gb = new GlobalVariable().getByName("rabbitmqurl");
                    if (gb != null) rabbitMQUrl = gb.value;
                    else {
                        logger.severe("Failed to retrieve RabbitMQ Connection URL");
                        return rabbitMQConnection = null;
                    }
                } catch (APIException e) {
                    logger.severe("RabbitMQ Connection URL retrieval caused an exception: " + e.getMessage());
                    return rabbitMQConnection = null;
                }
            }
            connectionFactory.setUri(rabbitMQUrl);
            rabbitMQConnection = connectionFactory.newConnection();
        } catch (URISyntaxException | NoSuchAlgorithmException | KeyManagementException | TimeoutException | IOException e) {
            e.printStackTrace();
            rabbitMQConnection = null;
        }
        return rabbitMQConnection;
    }

    public void onEnable() {
        instance = this;
        logger.info("Data Access Layer successfully loaded.");
    }

    public void onDisable() {
        if (rabbitMQConnection != null && rabbitMQConnection.isOpen()) {
            try {
                rabbitMQConnection.close();
            } catch (IOException e) {
                logger.severe("Failed to close RabbitMQ Connection");
                e.printStackTrace();
            }
        }
        CacheManager.getInstance().cleanUp();
        instance = null;
        logger.info("Data Access Layer has been disabled.");
    }
}
