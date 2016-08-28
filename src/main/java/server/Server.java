package server;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.DataListener;
import ecs100.UI;

/**
 * Created by sanjay on 28/08/2016.
 */
public class Server {
    public static void main(String[] args) throws InterruptedException {
        Configuration config = new Configuration();
        config.setPort(9092);
        UI.println("Global Scoreboard:");
        final SocketIOServer server = new SocketIOServer(config);
        server.start();
        Thread.sleep(Integer.MAX_VALUE);

        server.stop();
    }
}
