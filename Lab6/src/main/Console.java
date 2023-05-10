package main;

import panels.MainFrameConsole;
import panels.PanelInside;
import tools.MarkWorker;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Dawid Szeląg 264008
 *
 * Kompilacja (Windows):
 * dir /s /B *.java > sources.txt
 * javac @sources.txt
 *
 * Budowanie .jar:
  jar cfm Console.jar ./META-INF/Console/MANIFEST.MF *
  jar cfm Ship.jar ./META-INF/Ship/MANIFEST.MF *
  jar cfm World.jar ./META-INF/World/MANIFEST.MF *
  jar cfm MarkCreator.jar ./META-INF/MarkCreator/MANIFEST.MF *
 *
 * Uruchamianie:
 * java -jar Console.jar
 * java -jar Ship.jar
 * java -jar World.jar
 * java -jar MarkCreator.jar
 * (lub otworzyć z eksploratora)
 */

public class Console {
    ServerSocket serverSocket;
    Thread threadConsole;
    MainFrameConsole frameConsole;
    boolean open =true;

    public static void main(String[] args) {
        Console console = new Console();
        console.createFrame(console);
        console.startConsole(console);
    }
    public void createFrame(Console console)
    {
        SwingUtilities.invokeLater(()->
        {
            frameConsole = new MainFrameConsole("Console",new OptionPanel(),console);
        });
    }
    public void startConsole(Console console)
    {
        threadConsole = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    serverSocket = new ServerSocket(11000);
                    serverSocket.setReuseAddress(true);
                    while (!serverSocket.isClosed())
                    {
                        console.open = false;
                        Socket socket = serverSocket.accept();
                        MarkWorker markWorker = new MarkWorker(socket,console);
                        Thread thread = new Thread(markWorker);
                        thread.start();
                    }
                } catch (IOException e) {
                    System.out.println("Server closed");
                }

            }
        });
        threadConsole.setName("CONSOLE 1");
        threadConsole.start();
    }
    public void close()
    {
        try {
            serverSocket.close();
            MarkWorker.markWorkerList.forEach(markWorker -> markWorker.close());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized MainFrameConsole getFrameConsole() {
        return frameConsole;
    }

    public class OptionPanel extends JPanel
    {
        JButton buttonColor;
        JButton buttonNumbers;
        public OptionPanel()
        {
            this.setPreferredSize(new Dimension(200,860));
            this.setLayout(new GridLayout(4,1,10,200));
            buttonColor = new JButton("KOLOR");
            buttonNumbers= new JButton("LICZBY");
            this.add(new JPanel());
            this.add(buttonColor);
            this.add(buttonNumbers);
            this.add(new JPanel());
            buttonColor.addActionListener(e -> {
                PanelInside.color = true;
                for (int i = 0; i < 8; i++) {
                    for (int j = 0; j < 8; j++) {
                        for (int k = 0; k < 5; k++) {
                            for (int l = 0; l < 5; l++) {
                                Console.this.getFrameConsole().getPanelConsole().getArrayOfPanelsMain()[i][j].getPanels()[k][l].drawColor();
                            }
                        }
                    }
                }
            });
            buttonNumbers.addActionListener(e -> {
                PanelInside.color = false;
                for (int i = 0; i < 8; i++) {
                    for (int j = 0; j < 8; j++) {
                        for (int k = 0; k < 5; k++) {
                            for (int l = 0; l < 5; l++) {
                                Console.this.getFrameConsole().getPanelConsole().getArrayOfPanelsMain()[i][j].getPanels()[k][l].setBackground(UIManager.getColor( "Panel.background"));
                            }
                        }
                    }
                }
            });
        }
    }
}
