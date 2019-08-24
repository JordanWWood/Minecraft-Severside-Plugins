package network.marble.dataaccesslayer.managers;

import com.microsoft.aspnet.signalr.HubConnection;
import com.microsoft.aspnet.signalr.HubConnectionBuilder;
import com.microsoft.aspnet.signalr.LogLevel;

/**
 * Provides access to the Redis layer of the Marble.
 */
public class SocketManager {

    public HubConnection getNewSignalRConnection(String url) throws Exception {
        return new HubConnectionBuilder().withUrl(url).configureLogging(LogLevel.Information).build();
    }
}
