package tools;

import main.Console;
import main.Mark;
import main.World;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MarkWorker implements Runnable{
    public static List<MarkWorker> markWorkerList = new ArrayList<>();
    Console console;
    Socket socket;
    BufferedWriter writer;
    BufferedReader reader;
    Integer id;

    public MarkWorker(Socket socket, Console console)
    {
        this.console = console;
        this.socket = socket;
        try {
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (!socket.isClosed()) {
            try {
                String str = reader.readLine();
                if (str==null)
                    System.out.println(socket.isClosed());
                //System.out.println("str:"+str);
                String[] array = str.split(" ");
                synchronized (console.getFrameConsole().getPanelConsole().getArrayOfPanelsMain()[Integer.parseInt(String.valueOf(array[0].charAt(0)))][Integer.parseInt(String.valueOf(array[0].charAt(1)))])
                {
                    int k=1;
                    for (int i = 0; i < 5; i++) {
                        for (int j = 0; j < 5; j++) {
                            console.getFrameConsole().getPanelConsole().getArrayOfPanelsMain()[Integer.parseInt(String.valueOf(array[0].charAt(0)))][Integer.parseInt(String.valueOf(array[0].charAt(1)))].getPanels()[i][j].setNumber(Integer.parseInt(array[k]));
                            console.getFrameConsole().getPanelConsole().getArrayOfPanelsMain()[Integer.parseInt(String.valueOf(array[0].charAt(0)))][Integer.parseInt(String.valueOf(array[0].charAt(1)))].getPanels()[i][j].setShape(3);
                            console.getFrameConsole().getPanelConsole().getArrayOfPanelsMain()[Integer.parseInt(String.valueOf(array[0].charAt(0)))][Integer.parseInt(String.valueOf(array[0].charAt(1)))].getPanels()[i][j].repaint();
                            k++;
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
    public void close()
    {
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
