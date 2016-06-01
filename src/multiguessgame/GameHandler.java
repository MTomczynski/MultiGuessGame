/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package multiguessgame;

import java.io.*;
import java.net.*;
import java.util.*;

/**
 *
 * @author M
 */
public class GameHandler extends Thread
{

    //czaty
    static Vector<GameHandler> games = new Vector<GameHandler>();

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private String nick;
    private int numb;

    public GameHandler(Socket socket, int numb)
    {
        this.socket = socket;
        this.numb = numb;
    }

    private void sendToEveryone(String tekst)
    {
        for (GameHandler game : games)
        {
            synchronized (games)
            {
                if (game != this)
                {
                    game.out.println("<" + nick + "> " + tekst);
                }
            }
        }
    }

    public void info()
    {
        out.print("Witaj " + nick + ", aktualnie graja: ");
        for (GameHandler game : games)
        {
            synchronized (games)
            {
                if (game != this)
                {
                    out.print(game.nick + " ");
                }
            }
        }
        out.println();
    }

    public boolean numbCheck(String linia)
    {
        sendToEveryone(linia);
        int q = Integer.parseInt(linia);
        if (q != numb)
        {
            if (q > numb)
            {
                out.println("Little lower");
            } else if (q < numb)
            {
                out.println("Little higher");
            }
            return false;
        } else
        {
            out.println("You guessed, congratz!");
            return true;
        }
    }

    public void run()
    {
        String linia;
        synchronized (games)
        {
            games.add(this);
        }
        try
        {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
            out.println("Polaczony z serwerem. Komenda /end konczy polaczenie.");
            out.print("Podaj swoj nick: ");
            nick = in.readLine();
            System.out.println("Do czatu dolaczyl: " + nick);
            sendToEveryone("Pojawil sie na czacie");
            info();
            while (!(linia = in.readLine()).equalsIgnoreCase("/end"))
            {
                if (numbCheck(linia))
                {
                    sendToEveryone(nick + " wygral gre");
                    try
                    {
                        in.close();
                        out.close();
                        socket.close();
                    } catch (IOException e)
                    {
                    } finally
                    {
                        synchronized (games)
                        {
                            games.removeElement(this);
                        }
                    }
                }
            }
            sendToEveryone("Opuscil czat");
            System.out.println("Czat opuscil: " + nick);
        } catch (IOException e)
        {
            System.out.println(e);
        } finally
        {
            try
            {
                in.close();
                out.close();
                socket.close();
            } catch (IOException e)
            {
            } finally
            {
                synchronized (games)
                {
                    games.removeElement(this);
                }
            }
        }
    }

}
