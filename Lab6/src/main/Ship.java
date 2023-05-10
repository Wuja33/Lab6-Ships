package main;

import panels.MainFrameConsole;

import javax.swing.*;
import java.awt.*;
import java.net.*;
import java.io.*;
/**
 * Dawid Szeląg 264008
 *
 * Kompilacja (Windows):
 * dir /s /B *.java > sources.txt
 * javac @sources.txt
 *
 * Budowanie .jar:
 * jar cfm Ship.jar ./META-INF/Ship/MANIFEST.MF *
 *
 * Uruchamianie:
 * java -jar Ship.jar
 * (lub otworzyć z eksploratora)
 */

public class Ship {
    Object lockMove;
    public static int serverport = 2067;
    ServerSocket serverSocket;
    int x;
    int y;
    String id;
    MainFrameConsole frameConsole;
    String[] arrayShips;

    Ship()
    {
        lockMove = new Object();
        arrayShips = null;
    }

    public static void main(String[] args) {
        Ship ship = new Ship();
        ship.createFrame(ship);
        ship.initialize();
    }

    public void createFrame(Ship ship)
    {
        SwingUtilities.invokeLater(()->
        {
            frameConsole = new MainFrameConsole("Ship",new OptionPanel(),ship);
        });
    }

    public void move(int xMove,int yMove)
    {
        //sprawdzenie czy ruch jest dozwolony
        if (x+xMove<0||x+xMove>39)
        {
            frameConsole.moveError();
            return;
        }
        if (y+yMove<0||y+yMove>39)
        {
            frameConsole.moveError();
            return;
        }

        Thread threadShip = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    synchronized (lockMove) {
                        Socket socketToWorld = new Socket("127.0.0.1", 2002);
                        BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(socketToWorld.getOutputStream()));
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socketToWorld.getInputStream()));
                        bufferedWriter.write("move " + id + " " + (y + yMove) + " " + (x + xMove)); // "move ID y x"
                        bufferedWriter.newLine();
                        bufferedWriter.flush();

                        String str = bufferedReader.readLine();

                        if (str.startsWith("collision")) {
                            bufferedReader.close();
                            bufferedWriter.close();
                            socketToWorld.close();//zakończ połączenie
                            frameConsole.shipError();
                            return;
                        } else if (str.startsWith("good")) {
                            drawEmpty(x, y);
                            drawOwnShip(x + xMove, y + yMove);
                            x += xMove;
                            y += yMove;
                        }

                        bufferedWriter.close();
                        socketToWorld.close();//zakończ połączenie
                    }
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
                }
            }
        );
        threadShip.start();
    }
    public void scan()
    {
        Thread threadShip = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Socket socketToWorld = new Socket("127.0.0.1",2002);
                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(socketToWorld.getOutputStream()));
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socketToWorld.getInputStream()));
                    bufferedWriter.write("scan "+id); //przekaż komende scan do świata
                    bufferedWriter.newLine();
                    bufferedWriter.flush();

                    String str = bufferedReader.readLine(); //"y1 x1 y2 x2 ..."

                    if (arrayShips != null) { //jeśli to pierwszy scan, nie czyść planszy
                        for (int i = 0; i < arrayShips.length; i += 2) {
                            int yFunc = Integer.parseInt(arrayShips[i]);
                            int xFunc = Integer.parseInt(arrayShips[i + 1]);
                            drawEmpty(xFunc, yFunc);
                        }
                    }

                    String[] array = str.split(" ");

                    arrayShips = array; //zmień na aktualną tablice
                    for (int i = 0; i < array.length; i += 2) {
                        int yFunc = Integer.parseInt(array[i]);
                        int xFunc = Integer.parseInt(array[i + 1]);
                        drawShip(xFunc, yFunc);
                    }
                    bufferedWriter.close();
                    bufferedReader.close();
                    socketToWorld.close();//zakończ połączenie
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        });
        threadShip.start();
    }
    public void initialize()
    {
        Thread threadShip = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Socket socketToWorld = new Socket("127.0.0.1",2002);
                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(socketToWorld.getOutputStream()));
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socketToWorld.getInputStream()));
                    bufferedWriter.write("initialize");
                    bufferedWriter.newLine();
                    bufferedWriter.flush();

                    String str = bufferedReader.readLine();
                    String[] array = str.split(" ");
                    id = array[0];
                    y = Integer.parseInt(array[1]);
                    x = Integer.parseInt(array[2]);

                    drawOwnShip(x,y);
                    frameConsole.setTitle("Ship "+id);

                    bufferedWriter.close();
                    bufferedReader.close();
                    socketToWorld.close();//zakończ połączenie

                    scan();
                    waitForCollision();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        });
        threadShip.start();
    }
    public void waitForCollision()
    {
        Thread threadShip = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    serverSocket = new ServerSocket(serverport+Integer.parseInt(id));
                    while (!serverSocket.isClosed()) {
                        Socket world = serverSocket.accept(); //czekaj na połączenie ze światem

                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(world.getInputStream()));

                        String str = bufferedReader.readLine();

                        if (str.startsWith(id+" collision")) {
                            bufferedReader.close();
                            world.close();
                            serverSocket.close();
                            frameConsole.shipError();
                        }
                    }
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        });
        threadShip.start();
    }
    public void delete()
    {
        try {
            Socket socketToWorld = new Socket("127.0.0.1",2002);
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(socketToWorld.getOutputStream()));
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socketToWorld.getInputStream()));
            bufferedWriter.write("delete "+id);
            bufferedWriter.newLine();
            bufferedWriter.flush();

            String str = bufferedReader.readLine();

            bufferedWriter.close();
            bufferedReader.close();
            serverSocket.close();
            socketToWorld.close();//zakończ połączenie
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    public int countRegionY(int y)
    {
        return (int)Math.floor(y/5.0);
    }

    public int countRegionX(int x)
    {
        return (int)Math.floor(x/5.0);
    }

    public void drawShip(int x, int y)
    {
        frameConsole.getPanelConsole().getArrayOfPanelsMain()[countRegionY(y)][countRegionX(x)].getPanels()[y%5][x%5].setShape(1);
        frameConsole.getPanelConsole().getArrayOfPanelsMain()[countRegionY(y)][countRegionX(x)].getPanels()[y%5][x%5].repaint();
    }
    public void drawOwnShip(int x, int y)
    {
        frameConsole.getPanelConsole().getArrayOfPanelsMain()[countRegionY(y)][countRegionX(x)].getPanels()[y%5][x%5].setShape(2);
        frameConsole.getPanelConsole().getArrayOfPanelsMain()[countRegionY(y)][countRegionX(x)].getPanels()[y%5][x%5].repaint();
    }
    public void drawEmpty(int x, int y)
    {
        frameConsole.getPanelConsole().getArrayOfPanelsMain()[countRegionY(y)][countRegionX(x)].getPanels()[y%5][x%5].setShape(0);
        frameConsole.getPanelConsole().getArrayOfPanelsMain()[countRegionY(y)][countRegionX(x)].getPanels()[y%5][x%5].repaint();
    }


    class OptionPanel extends JPanel
    {
        JButton buttonMove0;
        JButton buttonMove1;
        JButton buttonMove2;
        JButton buttonMove3;
        JButton buttonMove4;
        JButton buttonMove5;
        JButton buttonMove6;
        JButton buttonMove7;
        JButton buttonMove8;

        public OptionPanel()
        {
            this.setPreferredSize(new Dimension(200,860));
            this.setLayout(new GridLayout(9,1,10,50));
            buttonMove0 = new JButton("SCAN");
            buttonMove0.setBackground(new Color(0x36B208));
            buttonMove0.setFocusPainted(false);
            buttonMove1 = new JButton("LEWY-GÓRNY");
            buttonMove2 = new JButton("GÓRA");
            buttonMove3 = new JButton("PRAWY-GÓRNY");
            buttonMove4 = new JButton("LEWO");
            buttonMove5 = new JButton("PRAWO");
            buttonMove6 = new JButton("LEWY-DOLNY");
            buttonMove7 = new JButton("DÓŁ");
            buttonMove8 = new JButton("PRAWY-DOLNY");
            this.add(buttonMove0);
            this.add(buttonMove1);
            this.add(buttonMove2);
            this.add(buttonMove3);
            this.add(buttonMove4);
            this.add(buttonMove5);
            this.add(buttonMove6);
            this.add(buttonMove7);
            this.add(buttonMove8);
            buttonMove0.addActionListener(e -> {
                Ship.this.scan();
            });
            buttonMove1.addActionListener(e -> {
                Ship.this.move(-1,-1);
            });
            buttonMove2.addActionListener(e -> {
                Ship.this.move(0,-1);
            });
            buttonMove3.addActionListener(e -> {
                Ship.this.move(1,-1);
            });
            buttonMove4.addActionListener(e -> {
                Ship.this.move(-1,0);
            });
            buttonMove5.addActionListener(e -> {
                Ship.this.move(1,0);
            });
            buttonMove6.addActionListener(e -> {
                Ship.this.move(-1,1);
            });
            buttonMove7.addActionListener(e -> {
                Ship.this.move(0,1);
            });
            buttonMove8.addActionListener(e -> {
                Ship.this.move(1,1);
            });
        }
    }
}
