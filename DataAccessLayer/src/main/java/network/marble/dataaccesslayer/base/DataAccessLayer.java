package network.marble.dataaccesslayer.base;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import lombok.Getter;
import network.marble.dataaccesslayer.exceptions.APIException;
import network.marble.dataaccesslayer.managers.CacheManager;
import network.marble.dataaccesslayer.managers.RedisManager;
import network.marble.dataaccesslayer.managers.TimerManager;
import network.marble.dataaccesslayer.models.GlobalVariable;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.concurrent.TimeoutException;
import java.util.logging.Logger;

public class DataAccessLayer
{
    public static DataAccessLayer instance;

    private static ArrayList<Connection> rabbitMQConnection = new ArrayList<>();
    private static String rabbitMQUrl;

    @Getter
    private final static boolean devNet = System.getProperty("TESTNET") != null;

    public static String getEnvironmentalVariable(String name) {
        return DataAccessLayer.isDevNet() ? System.getProperty(name) : System.getenv(name);
    }

    public Logger logger;
    @Getter
    public File dataFolder;

    public TimerManager timerManager;
    public CacheManager cacheManager;

    public DataAccessLayer(Logger logger, File dataFolder) {
        this.logger = logger;
        this.dataFolder = dataFolder;
        instance = this;
        this.timerManager = TimerManager.getInstance();
        this.cacheManager = CacheManager.getInstance();
    }

    /**
     * Generates a new RabbitMQ connection and returns it
     * @return The new connection
     */
    public Connection getRabbitMQConnection() throws KeyManagementException, TimeoutException, NoSuchAlgorithmException, IOException, URISyntaxException {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        if (rabbitMQUrl == null || rabbitMQUrl.isEmpty())
        {
            try {
                GlobalVariable gb = new GlobalVariable().getByName("rabbitmqurl");
                if (gb != null) rabbitMQUrl = gb.value;
                else {
                    logger.severe("Failed to retrieve RabbitMQ Connection URL");
                    return null;
                }
            } catch (APIException e) {
                logger.severe("RabbitMQ Connection URL retrieval caused an exception: " + e.getMessage());
                return null;
            }
        }
        connectionFactory.setUri(rabbitMQUrl);

        Connection con = connectionFactory.newConnection();
        rabbitMQConnection.add(con);
        return con;
    }

    public void onEnable() {
        logger.info("Data Access Layer successfully loaded.");
    }

    public void onDisable() {
        for(Connection con : rabbitMQConnection){
            if (con != null && con.isOpen()) {
                try {
                    con.close();
                } catch (IOException e) {
                    logger.severe("Failed to close RabbitMQ Connection");
                    e.printStackTrace();
                }
            }
        }

        RedisManager.setShutdown(true);
        RedisManager.getPool().close();
        cacheManager.cleanUp();
        instance = null;
        logger.info("Data Access Layer has been disabled.");
    }
}
