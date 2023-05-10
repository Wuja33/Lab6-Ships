package main;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ScheduledThreadPoolExecutor;
/**
 * Dawid Szeląg 264008
 *
 * Kompilacja (Windows):
 * dir /s /B *.java > sources.txt
 * javac @sources.txt
 *
 * Budowanie .jar:
 * jar cfm MarkCreator.jar ./META-INF/MarkCreator/MANIFEST.MF *
 *
 * Uruchamianie:
 * java -jar MarkCreator.jar
 * (lub otworzyć z eksploratora)
 */
public class MarkCreator extends JFrame {

    MarkCreator()
    {
        this.setTitle("MarkCreator");
        this.setResizable(false);
        this.setVisible(true);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setLayout(new GridLayout(2,1,10,10));
        JButton openMarks = new JButton("START");
        openMarks.setBackground(new Color(0x11B406));
        JButton closeMarks = new JButton("STOP");
        closeMarks.setBackground(Color.gray);
        closeMarks.setEnabled(false);

        openMarks.addActionListener(e->
        {
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    String[] array = new String[2];
                    array[0] = String.valueOf(i)+String.valueOf(j);
                    array[1] = "2001";
//                Mark.main(array);
                    Thread thread = new Thread(this.startMark(array));
                    thread.start();
                }
            }
            openMarks.setEnabled(false);
            openMarks.setBackground(Color.gray);
            closeMarks.setEnabled(true);
            closeMarks.setBackground(new Color(0xD00000));
        });
        closeMarks.addActionListener(e -> {
            Mark.serverSockets.forEach(serverSocket -> {
                //close serverSockets
                try {
                    serverSocket.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            });
            //close invokeSenders
            Mark.threadPool.shutdown();
            try {
                Thread.sleep(1000);
                Mark.sockets.forEach(socket -> {
                    try {
                        socket.close();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                });
            }
            catch (InterruptedException exception)
            {
                exception.printStackTrace();
            }
            Mark.threadPool = new ScheduledThreadPoolExecutor(64);
            Mark.sockets = new ArrayList<>();
            closeMarks.setEnabled(false);
            closeMarks.setBackground(Color.gray);
            openMarks.setEnabled(true);
            openMarks.setBackground(new Color(0x11B406));
        });
        this.add(openMarks);
        this.add(closeMarks);
        this.setPreferredSize(new Dimension(300,150));
        this.pack();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(()->
        {
            MarkCreator markCreator = new MarkCreator();
        });
    }
    public Runnable startMark(String[] str)
    {
        Runnable runnable = () -> Mark.main(str);
        return runnable;
    }
}
