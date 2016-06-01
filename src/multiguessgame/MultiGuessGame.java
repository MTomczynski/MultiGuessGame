package multiguessgame;

import java.io.*;
import java.net.*;
import java.util.Random;

public class MultiGuessGame
{

    private static ServerSocket server;
    private static final int PORT = 2345;

    public static void main(String[] args)
    {
        Random rand = new Random();
        int r = rand.nextInt(100) + 1;

        try
        {
            server = new ServerSocket(PORT);
            System.out.println("Gra Serwer uruchomiony na porcie: " + PORT);
            while (true)
            {
                Socket socket = server.accept();
                InetAddress addr = socket.getInetAddress();
                System.out.println("Polaczenie z adresu: " + addr.getHostName()
                        + " [" + addr.getHostAddress() + "]");
                new GameHandler(socket, r).start();
            }
        } catch (IOException e)
        {
            System.out.println(e);
        }
    }
}
