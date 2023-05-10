package panels;

import main.Console;

import javax.swing.*;
import java.awt.*;

public class PanelConsole extends JPanel
{
    JPanel mainPanel;
    JPanel counterUpper;
    JPanel counterLeft;
    PanelRegion[][] arrayOfPanelsMain;
    public PanelConsole()
    {
        this.setPreferredSize(new Dimension(860,860));
        this.setLayout(new BorderLayout());
        this.arrayOfPanelsMain = new PanelRegion[8][8];
        this.mainPanel = new JPanel();
        this.add(mainPanel,BorderLayout.CENTER);
        mainPanel.setLayout(new GridLayout(8,8,2,2));
        mainPanel.setBackground(Color.black);

        this.counterUpper = new JPanel();
        this.counterLeft = new JPanel();
        this.add(counterUpper,BorderLayout.NORTH);
        this.add(counterLeft,BorderLayout.WEST);

        counterUpper.setLayout(new GridLayout(1,8));
        counterLeft.setLayout(new GridLayout(8,1));
        //tworzenie paneli do tablicy paneli
        //ZROBIENIE 64 JPaneli do zrobienia tablicy  (???)

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                arrayOfPanelsMain[i][j] = new PanelRegion();
                mainPanel.add(arrayOfPanelsMain[i][j]);
            }
        }

        //tworzenie numeracji poziomej i pionowej
        for (int i = 1; i < 9; i++) {
            JPanel panel1 = new JPanel();
            JPanel panel2 = new JPanel();
            JLabel label1 = new JLabel(String.valueOf(i));
            JLabel label2 = new JLabel(String.valueOf(i));
            label1.setFont(new Font(Font.MONOSPACED, Font.BOLD, 22));
            label2.setFont(new Font(Font.MONOSPACED, Font.BOLD, 22));

            panel1.setLayout(new GridBagLayout());
            panel1.add(label1);

            panel2.setLayout(new GridBagLayout());
            panel2.add(label2);
            counterUpper.add(panel1);
            counterLeft.add(panel2);
        }
    }
    public void print(int yPrevious, int xPrevious, int y, int x)
    {

    }

    public synchronized PanelRegion[][] getArrayOfPanelsMain() {
        return arrayOfPanelsMain;
    }
}