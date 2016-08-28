package server;

import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.ExceptionListener;
import ecs100.UI;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.Field;

/**
 * Created by sanjay on 28/08/2016.
 */
public class Server {
    public static void main(String[] args) throws InterruptedException {
        Configuration config = new Configuration();
        config.setPort(9092);
        UI.println("Global Scoreboard:");

        try {
            Field f = UI.class.getDeclaredField("textPane");
            f.setAccessible(true);
            JTextArea area = (JTextArea) f.get(UI.theUI);
            Font font = new Font("Verdana", Font.BOLD, 30);
            area.setFont(font);
            area.setForeground(Color.BLUE);
        } catch (Exception e) {
            e.printStackTrace();
        }

        final SocketIOServer server = new SocketIOServer(config);
        server.addConnectListener(socketIOClient -> UI.println("Client connected!"));
        server.addEventListener("scoreGot", ScoreObject.class, (client, data, ackRequest) -> {
            server.getBroadcastOperations().sendEvent("scoreGot", data);
            UI.println(data);

        });

        server.start();
        Thread.sleep(Integer.MAX_VALUE);

        server.stop();
    }
}
