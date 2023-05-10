package main;

import panels.MainFrameConsole;
import tools.ShipWorker;

import javax.swing.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Dawid Szeląg 264008
 *
 * Kompilacja (Windows):
 * dir /s /B *.java > sources.txt
 * javac @sources.txt
 *
 * Budowanie .jar:
 * jar cfm World.jar ./META-INF/World/MANIFEST.MF *
 *
 * Uruchamianie:
 * java -jar World.jar
 * (lub otworzyć z eksploratora)
 */

public class World {
    MainFrameConsole mainFrameConsole;
    ServerSocket serverSocket;
    Thread threadServer;
    int[][] arrayLevel;
    int[][] arrayShips;
    ConcurrentHashMap<Integer,String> listShips; // (ID,"y x")
    World()
    {
        listShips = new ConcurrentHashMap<>();
        arrayShips = new int[40][40];
        arrayLevel = new int[40][40];
    }

    public static void main(String[] args) {
        World world = new World();
        world.createFrame(world);
        world.startConsole(world);
    }

    public void createFrame(World world)
    {
        SwingUtilities.invokeLater(()->{
            mainFrameConsole = new MainFrameConsole("World",world);
        });
    }
    public void startConsole(World world)
    {
        threadServer = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    serverSocket = new ServerSocket(2002);
                    while (!serverSocket.isClosed())
                    {
                        Socket socketShip = serverSocket.accept();
                        ShipWorker worker = new ShipWorker(socketShip,world);
                        Thread thread = new Thread(worker);
                        thread.start();
                    }
                }
                catch (IOException e)
                {
                    System.out.println("World closed!");
                }
            }
        });
        threadServer.start();
    }

    public <K, V> K getKey(ConcurrentHashMap<K, V> map, V value) {
        for (Map.Entry<K, V> entry : map.entrySet()) {
            if (entry.getValue().equals(value)) {
                return entry.getKey();
            }
        }
        return null;
    }
    public void close()
    {
        try {
            serverSocket.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public static int countRegionY(int y)
    {
        return (int)Math.floor(y/5.0);
    }

    public static int countRegionX(int x)
    {
        return (int)Math.floor(x/5.0);
    }

    public synchronized int[][] getArrayShips() {
        return arrayShips;
    }

    public synchronized ConcurrentHashMap<Integer, String> getListShips() {
        return listShips;
    }

    public MainFrameConsole getMainFrameConsole() {
        return mainFrameConsole;
    }

    public synchronized int[][] getArrayLevel() {
        return arrayLevel;
    }
}
