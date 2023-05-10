package main;

import java.net.*;
import java.io.*;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Mark {
    static ScheduledThreadPoolExecutor threadPool = new ScheduledThreadPoolExecutor(64);
    static List<ServerSocket> serverSockets = new ArrayList<>();
    static List<Socket> sockets = new ArrayList<>();
    ServerSocket serverSocket;
    Socket socketToConsole;
    BufferedWriter bufferedWriter;
    Thread threadConsole;
    String id; //yx -->> y wiersz, x kolumna
    int[][] region;

    Mark(String id,String port)
    {
        this.region = new int[5][5];
        this.id = id;
        while (true) {
            try {
                socketToConsole = new Socket();
                socketToConsole.connect(new InetSocketAddress("127.0.0.1", 11000), 0);
                sockets.add(socketToConsole);
                bufferedWriter = new BufferedWriter(new OutputStreamWriter(socketToConsole.getOutputStream()));
                break;
            } catch (IOException e) {
                try {
                    TimeUnit.SECONDS.sleep(2);
                    System.out.println("TIME HERE");
                }
                catch (InterruptedException exception){}
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        Mark mark = new Mark(args[0],args[1]);
        mark.startConsole();
        System.out.println(mark);
        threadPool.scheduleAtFixedRate(mark.startSender(), 3, 5, TimeUnit.SECONDS);
    }


    public void startConsole()
    {
        threadConsole = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    serverSocket = new ServerSocket(Integer.parseInt("70"+id));
                    serverSockets.add(serverSocket);
                    serverSocket.setReuseAddress(true);
                    while (!serverSocket.isClosed())
                    {
                        Socket socketFromWorld = serverSocket.accept();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(socketFromWorld.getInputStream()));
                        String str = reader.readLine();
                        String[] array = str.split(" ");
                        int k=0;
                        synchronized (region) {
                            for (int i = 0; i < 5; i++) {
                                for (int j = 0; j < 5; j++) {
                                    try {
                                        region[i][j] = Integer.parseInt(array[k]);
                                    }
                                    catch (NumberFormatException e2)
                                    {
                                        System.out.println("ERROR:"+str);
                                    }
                                    k++;
                                }
                            }
                        }
                        reader.close();
                    }
                }
                catch (IOException e)
                {
                    System.out.println("Mark disconnected!");
                }
            }
        });
        threadConsole.start();
    }
    public Runnable startSender()
    {
        Runnable runnable = ()->{
                    try {
                        if (socketToConsole.isConnected()) {
                            bufferedWriter.write(id + " "); //przekaż to do konsoli
                            synchronized (region)
                            {
                                for (int i = 0; i < 5; i++) {
                                    for (int j = 0; j < 5; j++) {
                                        bufferedWriter.write(region[i][j]+" ");
                                    }
                                }
                            }
                            bufferedWriter.newLine();
                            bufferedWriter.flush();
                        }
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
        };
        return runnable;
    }
}
