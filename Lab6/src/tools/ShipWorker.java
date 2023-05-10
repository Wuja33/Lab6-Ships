package tools;

import main.Ship;
import main.World;

import java.io.*;
import java.net.Socket;
import java.util.*;

import static main.World.countRegionX;
import static main.World.countRegionY;

public class ShipWorker implements Runnable {
    World world;
    Socket socket;
    BufferedWriter writer;
    BufferedReader reader;
    Integer id;
    static int idShips = 0;

    public ShipWorker(Socket socket, World world) {
        this.world = world;
        this.socket = socket;
        try {
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (!socket.isClosed()) {
            try {
                String str = reader.readLine();

                if (str.startsWith("initialize")) {
                    writer.write(initializeShip(str));
                    writer.flush();
                } else if (str.startsWith("move")) {
                    writer.write(moveShip(str));
                    writer.flush();
                } else if (str.startsWith("scan")) {
                    writer.write(scanShip(str));
                    writer.flush();
                } else if (str.startsWith("delete")) {
                    writer.write(deleteShip(str));
                    writer.flush();
                }

                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void messageMark(String id) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Socket socket = new Socket("127.0.0.1", Integer.parseInt("70" + id));
                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                    synchronized (world.getArrayLevel()) {
                        int i1 = Integer.parseInt(Character.toString(id.charAt(0)));
                        int i2 = Integer.parseInt(Character.toString(id.charAt(1)));
                        for (int i = i1 * 5; i < i1 * 5 + 5; i++) {
                            for (int j = i2 * 5; j < i2 * 5 + 5; j++) {
                                bufferedWriter.write(world.getArrayLevel()[i][j] + " ");
                            }
                        }
                    }
                    bufferedWriter.newLine();
                    bufferedWriter.flush();

                    bufferedWriter.close();
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }

    public List<String> changeLevelSea(int yShip, int xShip, int yPrevious, int xPrevious) {
        List<String> listIdMarksToInform = new ArrayList<>();
        int x1 = 0; //maks. x po lewej stronie
        int x2 = 0; //maks. x po prawej stronie
        int y1 = 0; //maks. y na górze
        int y2 = 0; //maks. y na dole

        synchronized (world.getArrayLevel()) {
            //usuń poprzednie wartości statku
            for (int y = -2; y <= 2; y++) {
                for (int x = -2; x <= 2; x++) {
                    if ((xPrevious + x) < 40 && (yPrevious + y) < 40 && (xPrevious + x) >= 0 && (yPrevious + y) >= 0) //blokada wyjścia poza tablice
                    {
                        //USTALANIE NAROŻNIKÓW
                        if (y < y1)
                            y1 = y;
                        if (y > y2)
                            y2 = y;
                        if (x < x1)
                            x1 = x;
                        if (x > x2)
                            x2 = x;

                        if (y == -2 || y == 2) {
                            if (x == -2 || x == 2)
                                continue;
                            else if (x == 0)
                                world.getArrayLevel()[y + yPrevious][x + xPrevious] -= 2;
                            else
                                world.getArrayLevel()[y + yPrevious][x + xPrevious] -= 1;
                        } else if (y == -1 || y == 1) {
                            if (x == -2 || x == 2)
                                world.getArrayLevel()[y + yPrevious][x + xPrevious] -= 1;
                            else if (x == 0)
                                world.getArrayLevel()[y + yPrevious][x + xPrevious] -= 3;
                            else
                                world.getArrayLevel()[y + yPrevious][x + xPrevious] -= 2;
                        } else //y=0
                        {
                            if (x == -2 || x == 2)
                                world.getArrayLevel()[y + yPrevious][x + xPrevious] -= 2;
                            else if (x == 0)
                                world.getArrayLevel()[y + yPrevious][x + xPrevious] -= 4;
                            else
                                world.getArrayLevel()[y + yPrevious][x + xPrevious] -= 3;
                        }
                    }
                }
            }
            updateListMarks(listIdMarksToInform, y1 + yPrevious, x1 + xPrevious); //LG
            updateListMarks(listIdMarksToInform, y1 + yPrevious, x2 + xPrevious); //PG
            updateListMarks(listIdMarksToInform, y2 + yPrevious, x1 + xPrevious); //LD
            updateListMarks(listIdMarksToInform, y2 + yPrevious, x2 + xPrevious); //PD

            x1 = 0; //maks. x po lewej stronie
            x2 = 0; //maks. x po prawej stronie
            y1 = 0; //maks. y na górze
            y2 = 0; //maks. y na dole

            //dodaj je w nowym miejscu
            for (int y = -2; y <= 2; y++) {
                for (int x = -2; x <= 2; x++) {
                    if ((xShip + x) < 40 && (yShip + y) < 40 && (xShip + x) >= 0 && (yShip + y) >= 0) //blokada wyjścia poza tablice
                    {
                        //USTALANIE NAROŻNIKÓW
                        if (y < y1)
                            y1 = y;
                        if (y > y2)
                            y2 = y;
                        if (x < x1)
                            x1 = x;
                        if (x > x2)
                            x2 = x;

                        if (y == -2 || y == 2) {
                            if (x == -2 || x == 2)
                                continue;
                            else if (x == 0)
                                world.getArrayLevel()[y + yShip][x + xShip] += 2;
                            else
                                world.getArrayLevel()[y + yShip][x + xShip] += 1;
                        } else if (y == -1 || y == 1) {
                            if (x == -2 || x == 2)
                                world.getArrayLevel()[y + yShip][x + xShip] += 1;
                            else if (x == 0)
                                world.getArrayLevel()[y + yShip][x + xShip] += 3;
                            else
                                world.getArrayLevel()[y + yShip][x + xShip] += 2;
                        } else //y=0
                        {
                            if (x == -2 || x == 2)
                                world.getArrayLevel()[y + yShip][x + xShip] += 2;
                            else if (x == 0)
                                world.getArrayLevel()[y + yShip][x + xShip] += 4;
                            else
                                world.getArrayLevel()[y + yShip][x + xShip] += 3;
                        }
                    }
                }
            }
        }
        updateListMarks(listIdMarksToInform, y1 + yShip, x1 + xShip); //LG
        updateListMarks(listIdMarksToInform, y1 + yShip, x2 + xShip); //PG
        updateListMarks(listIdMarksToInform, y2 + yShip, x1 + xShip); //LD
        updateListMarks(listIdMarksToInform, y2 + yShip, x2 + xShip); //PD

        return listIdMarksToInform;
    }

    public void updateListMarks(List<String> list, int y, int x) {
        String region = String.valueOf(World.countRegionY(y)) + String.valueOf(World.countRegionX(x));
        if (!list.contains(region))
            list.add(region);
    }

    public List<String> addLevelSea(int yShip, int xShip) {
        List<String> listIdMarksToInform = new ArrayList<>();
        int x1 = 0; //maks. x po lewej stronie
        int x2 = 0; //maks. x po prawej stronie
        int y1 = 0; //maks. y na górze
        int y2 = 0; //maks. y na dole

        synchronized (world.getArrayLevel()) {
            //dodaj je w nowym miejscu
            for (int y = -2; y <= 2; y++) {
                for (int x = -2; x <= 2; x++) {
                    if ((xShip + x) < 40 && (yShip + y) < 40 && (xShip + x) >= 0 && (yShip + y) >= 0) //blokada wyjścia poza tablice
                    {
                        //USTALANIE NAROŻNIKÓW
                        if (y < y1)
                            y1 = y;
                        if (y > y2)
                            y2 = y;
                        if (x < x1)
                            x1 = x;
                        if (x > x2)
                            x2 = x;

                        if (y == -2 || y == 2) {
                            if (x == -2 || x == 2)
                                continue;
                            else if (x == 0)
                                world.getArrayLevel()[y + yShip][x + xShip] += 2;
                            else
                                world.getArrayLevel()[y + yShip][x + xShip] += 1;
                        } else if (y == -1 || y == 1) {
                            if (x == -2 || x == 2)
                                world.getArrayLevel()[y + yShip][x + xShip] += 1;
                            else if (x == 0)
                                world.getArrayLevel()[y + yShip][x + xShip] += 3;
                            else
                                world.getArrayLevel()[y + yShip][x + xShip] += 2;
                        } else //y=0
                        {
                            if (x == -2 || x == 2)
                                world.getArrayLevel()[y + yShip][x + xShip] += 2;
                            else if (x == 0)
                                world.getArrayLevel()[y + yShip][x + xShip] += 4;
                            else
                                world.getArrayLevel()[y + yShip][x + xShip] += 3;
                        }
                    }
                }
            }
        }
        updateListMarks(listIdMarksToInform, y1 + yShip, x1 + xShip); //LG
        updateListMarks(listIdMarksToInform, y1 + yShip, x2 + xShip); //PG
        updateListMarks(listIdMarksToInform, y2 + yShip, x1 + xShip); //LD
        updateListMarks(listIdMarksToInform, y2 + yShip, x2 + xShip); //PD

        return listIdMarksToInform;
    }

    public List<String> deleteLevelSea(int yShip1, int xShip1, int yShip2, int xShip2) {
        List<String> listIdMarksToInform = new ArrayList<>();
        int x1 = 0; //maks. x po lewej stronie
        int x2 = 0; //maks. x po prawej stronie
        int y1 = 0; //maks. y na górze
        int y2 = 0; //maks. y na dole

        synchronized (world.getArrayLevel()) {
            //usuń poprzednie wartości statku
            for (int y = -2; y <= 2; y++) {
                for (int x = -2; x <= 2; x++) {
                    if ((xShip2 + x) < 40 && (yShip2 + y) < 40 && (xShip2 + x) >= 0 && (yShip2 + y) >= 0) //blokada wyjścia poza tablice
                    {
                        //USTALANIE NAROŻNIKÓW
                        if (y < y1)
                            y1 = y;
                        if (y > y2)
                            y2 = y;
                        if (x < x1)
                            x1 = x;
                        if (x > x2)
                            x2 = x;

                        if (y == -2 || y == 2) {
                            if (x == -2 || x == 2)
                                continue;
                            else if (x == 0)
                                world.getArrayLevel()[y + yShip2][x + xShip2] -= 2;
                            else
                                world.getArrayLevel()[y + yShip2][x + xShip2] -= 1;
                        } else if (y == -1 || y == 1) {
                            if (x == -2 || x == 2)
                                world.getArrayLevel()[y + yShip2][x + xShip2] -= 1;
                            else if (x == 0)
                                world.getArrayLevel()[y + yShip2][x + xShip2] -= 3;
                            else
                                world.getArrayLevel()[y + yShip2][x + xShip2] -= 2;
                        } else //y=0
                        {
                            if (x == -2 || x == 2)
                                world.getArrayLevel()[y + yShip2][x + xShip2] -= 2;
                            else if (x == 0)
                                world.getArrayLevel()[y + yShip2][x + xShip2] -= 4;
                            else
                                world.getArrayLevel()[y + yShip2][x + xShip2] -= 3;
                        }
                    }
                }
            }

            updateListMarks(listIdMarksToInform, y1 + yShip2, x1 + xShip2); //LG
            updateListMarks(listIdMarksToInform, y1 + yShip2, x2 + xShip2); //PG
            updateListMarks(listIdMarksToInform, y2 + yShip2, x1 + xShip2); //LD
            updateListMarks(listIdMarksToInform, y2 + yShip2, x2 + xShip2); //PD

            x1 = 0; //maks. x po lewej stronie
            x2 = 0; //maks. x po prawej stronie
            y1 = 0; //maks. y na górze
            y2 = 0; //maks. y na dole

            //usuń poprzednie wartości statku
            for (int y = -2; y <= 2; y++) {
                for (int x = -2; x <= 2; x++) {
                    if ((xShip1 + x) < 40 && (yShip1 + y) < 40 && (xShip1 + x) >= 0 && (yShip1 + y) >= 0) //blokada wyjścia poza tablice
                    {
                        //USTALANIE NAROŻNIKÓW
                        if (y < y1)
                            y1 = y;
                        if (y > y2)
                            y2 = y;
                        if (x < x1)
                            x1 = x;
                        if (x > x2)
                            x2 = x;

                        if (y == -2 || y == 2) {
                            if (x == -2 || x == 2)
                                continue;
                            else if (x == 0)
                                world.getArrayLevel()[y + yShip1][x + xShip1] -= 2;
                            else
                                world.getArrayLevel()[y + yShip1][x + xShip1] -= 1;
                        } else if (y == -1 || y == 1) {
                            if (x == -2 || x == 2)
                                world.getArrayLevel()[y + yShip1][x + xShip1] -= 1;
                            else if (x == 0)
                                world.getArrayLevel()[y + yShip1][x + xShip1] -= 3;
                            else
                                world.getArrayLevel()[y + yShip1][x + xShip1] -= 2;
                        } else //y=0
                        {
                            if (x == -2 || x == 2)
                                world.getArrayLevel()[y + yShip1][x + xShip1] -= 2;
                            else if (x == 0)
                                world.getArrayLevel()[y + yShip1][x + xShip1] -= 4;
                            else
                                world.getArrayLevel()[y + yShip1][x + xShip1] -= 3;
                        }
                    }
                }
            }
        }
        updateListMarks(listIdMarksToInform, y1 + yShip1, x1 + xShip1); //LG
        updateListMarks(listIdMarksToInform, y1 + yShip1, x2 + xShip1); //PG
        updateListMarks(listIdMarksToInform, y2 + yShip1, x1 + xShip1); //LD
        updateListMarks(listIdMarksToInform, y2 + yShip1, x2 + xShip1); //PD

        return listIdMarksToInform;
    }

    public List<String> deleteLevelSeaOneShip(int yShip, int xShip) {
        List<String> listIdMarksToInform = new ArrayList<>();
        int x1 = 0; //maks. x po lewej stronie
        int x2 = 0; //maks. x po prawej stronie
        int y1 = 0; //maks. y na górze
        int y2 = 0; //maks. y na dole

        //usuń poprzednie wartości statku
        for (int y = -2; y <= 2; y++) {
            for (int x = -2; x <= 2; x++) {
                if ((xShip + x) < 40 && (yShip + y) < 40 && (xShip + x) >= 0 && (yShip + y) >= 0) //blokada wyjścia poza tablice
                {
                    //USTALANIE NAROŻNIKÓW
                    if (y < y1)
                        y1 = y;
                    if (y > y2)
                        y2 = y;
                    if (x < x1)
                        x1 = x;
                    if (x > x2)
                        x2 = x;

                    if (y == -2 || y == 2) {
                        if (x == -2 || x == 2)
                            continue;
                        else if (x == 0)
                            world.getArrayLevel()[y + yShip][x + xShip] -= 2;
                        else
                            world.getArrayLevel()[y + yShip][x + xShip] -= 1;
                    } else if (y == -1 || y == 1) {
                        if (x == -2 || x == 2)
                            world.getArrayLevel()[y + yShip][x + xShip] -= 1;
                        else if (x == 0)
                            world.getArrayLevel()[y + yShip][x + xShip] -= 3;
                        else
                            world.getArrayLevel()[y + yShip][x + xShip] -= 2;
                    } else //y=0
                    {
                        if (x == -2 || x == 2)
                            world.getArrayLevel()[y + yShip][x + xShip] -= 2;
                        else if (x == 0)
                            world.getArrayLevel()[y + yShip][x + xShip] -= 4;
                        else
                            world.getArrayLevel()[y + yShip][x + xShip] -= 3;
                    }
                }
            }
        }
        updateListMarks(listIdMarksToInform,y1+yShip,x1+xShip); //LG
        updateListMarks(listIdMarksToInform,y1+yShip,x2+xShip); //PG
        updateListMarks(listIdMarksToInform,y2+yShip,x1+xShip); //LD
        updateListMarks(listIdMarksToInform,y2+yShip,x2+xShip); //PD

        return listIdMarksToInform;
    }
    public String initializeShip(String str)
    {
        Random random = new Random();
        int x;
        int y;

        while (true)
        {
            x = random.nextInt(40);
            y = random.nextInt(40);

            synchronized (world.getArrayShips()) {
                if (world.getArrayShips()[y][x] == 0) {
                    world.getArrayShips()[y][x] = 1;
                    id = idShips;
                    world.getListShips().put(idShips++, y + " " + x);
                    //wzgórza

                    //rysowanie statku
                    world.getMainFrameConsole().getPanelConsole().getArrayOfPanelsMain()[countRegionY(y)][countRegionX(x)].getPanels()[y % 5][x % 5].setShape(1);
                    world.getMainFrameConsole().getPanelConsole().getArrayOfPanelsMain()[countRegionY(y)][countRegionX(x)].getPanels()[y % 5][x % 5].repaint();

                    break;
                }
            }
        }

        addLevelSea(y,x).forEach(s ->
        {
            messageMark(s);
        });
        return id + " " + y + " " + x + "\n"; //"ID Y X"
    }
    public String moveShip(String str)
    {
        String returnString;
        String[] array = str.split(" "); // "move ID y x"
        Integer idFunc = Integer.valueOf(array[1]);
        int y = Integer.parseInt(array[2]);
        int x = Integer.parseInt(array[3]);
        String yAndx;
        synchronized (world.getListShips()) {
            yAndx = world.getListShips().get(idFunc);
        }
        String[] arrayPrevious = yAndx.split(" ");
        int yPrevious = Integer.parseInt(arrayPrevious[0]);
        int xPrevious = Integer.parseInt(arrayPrevious[1]);

        synchronized (world.getArrayShips()) {
        world.getArrayShips()[yPrevious][xPrevious] = 0;
        world.getMainFrameConsole().getPanelConsole().getArrayOfPanelsMain()[countRegionY(yPrevious)][countRegionX(xPrevious)].getPanels()[yPrevious%5][xPrevious%5].setShape(0);
        world.getMainFrameConsole().getPanelConsole().getArrayOfPanelsMain()[countRegionY(yPrevious)][countRegionX(xPrevious)].getPanels()[yPrevious%5][xPrevious%5].repaint();

        //collision
            if (world.getArrayShips()[y][x] == 1) {
                world.getArrayShips()[y][x] = 0;
                //narysuj puste pole
                world.getMainFrameConsole().getPanelConsole().getArrayOfPanelsMain()[countRegionY(y)][countRegionX(x)].getPanels()[y % 5][x % 5].setShape(0);
                world.getMainFrameConsole().getPanelConsole().getArrayOfPanelsMain()[countRegionY(y)][countRegionX(x)].getPanels()[y % 5][x % 5].repaint();
                //usuń z listy statki które się zderzyły
                Integer shipToEnd = world.getKey(world.getListShips(), y + " " + x);
                world.getListShips().remove(shipToEnd);
                world.getListShips().remove(idFunc);
                try {
                    Socket socket = new Socket("127.0.0.1", Ship.serverport + shipToEnd);
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                    writer.write(String.valueOf(shipToEnd) + " collision\n");
                    writer.newLine();
                    writer.flush();

                    writer.close();
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                returnString = "collision\n";
            } else {
                //jeśli jest miejsce to zmień dane statku
                world.getListShips().replace(idFunc, y + " " + x);
                world.getArrayShips()[y][x] = 1;
                //narysuj puste pole
                world.getMainFrameConsole().getPanelConsole().getArrayOfPanelsMain()[countRegionY(y)][countRegionX(x)].getPanels()[y % 5][x % 5].setShape(1);
                world.getMainFrameConsole().getPanelConsole().getArrayOfPanelsMain()[countRegionY(y)][countRegionX(x)].getPanels()[y % 5][x % 5].repaint();

                returnString = "good\n";
            }
        }
        if (returnString.equals("good\n"))
            changeLevelSea(y,x,yPrevious,xPrevious).forEach(s -> messageMark(s));
        else
            deleteLevelSea(y,x,yPrevious,xPrevious).forEach(s -> messageMark(s));

        return returnString;
    }
    public String scanShip(String str) //"scan id"   "y1 x1 y2 x2 ..."
    {
        synchronized (world.getListShips()) {
            if (world.getListShips().size() > 1) {
                String[] splitArray = str.split(" ");
                int idFunc = Integer.parseInt(splitArray[1]);
                StringBuilder scanReturn = new StringBuilder();
                for (Map.Entry<Integer, String> entry : world.getListShips().entrySet()) {
                    if (entry.getKey() != idFunc) {
                        scanReturn.append(entry.getValue() + " ");
                    }
                }
                scanReturn.deleteCharAt(scanReturn.length() - 1);
                return scanReturn + "\n";
            } else
                return " \n";
        }
    }
    public String deleteShip(String str)
    {
        String[] array = str.split(" "); // "move ID y x"
        Integer idFunc = Integer.valueOf(array[1]);
        String yAndx;
        synchronized (world.getListShips()) {
            yAndx = world.getListShips().get(idFunc);
            world.getListShips().remove(idFunc);
        }
        String[] array2 = yAndx.split(" ");
        int y = Integer.parseInt(array2[0]);
        int x = Integer.parseInt(array2[1]);

        synchronized (world.getArrayShips()) {
            world.getArrayShips()[y][x] = 0;
            world.getMainFrameConsole().getPanelConsole().getArrayOfPanelsMain()[countRegionY(y)][countRegionX(x)].getPanels()[y % 5][x % 5].setShape(0);
            world.getMainFrameConsole().getPanelConsole().getArrayOfPanelsMain()[countRegionY(y)][countRegionX(x)].getPanels()[y % 5][x % 5].repaint();
            System.out.println("siem");
        }
        deleteLevelSeaOneShip(y,x).forEach(s -> messageMark(s));
        return "deleted\n";
    }
    public Integer getId() {
        return id;
    }
}
