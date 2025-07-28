package org.example.githubfiles.utils;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

public class NetworkUtils {
    public static boolean isInternetAvailable() {
        try (Socket socket = new Socket()) {
            SocketAddress address = new InetSocketAddress("8.8.8.8", 53); // Google DNS
            socket.connect(address, 2000); // 2 saniye timeout
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}
