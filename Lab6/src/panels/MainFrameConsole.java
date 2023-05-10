package panels;

import main.Console;
import main.Ship;
import main.World;

import javax.swing.*;
import java.awt.*;


public class MainFrameConsole extends JFrame{
    PanelConsole panelConsole;

    public MainFrameConsole(String title, JPanel optionPanel, Console console)
    {
        panelConsole = new PanelConsole();
        this.setTitle(title);
        this.setVisible(true);
        this.setResizable(true);
        this.setLayout(new BorderLayout());
        this.add(panelConsole,BorderLayout.CENTER);
        this.add(optionPanel,BorderLayout.EAST);
        this.pack();
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                console.close();
            }
        });
    }
    public MainFrameConsole(String title, JPanel optionPanel, Ship ship)
    {
        panelConsole = new PanelConsole();
        this.setTitle(title);
        this.setVisible(true);
        this.setResizable(true);
        this.setLayout(new BorderLayout());
        this.add(panelConsole,BorderLayout.CENTER);
        this.add(optionPanel,BorderLayout.EAST);
        this.pack();
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                ship.delete();
            }
        });
    }
    public MainFrameConsole(String title, World world)
    {
        panelConsole = new PanelConsole();
        this.setTitle(title);
        this.setVisible(true);
        this.setResizable(true);
        this.setLayout(new BorderLayout());
        this.add(panelConsole,BorderLayout.CENTER);
        this.pack();
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                world.close();
            }
        });

    }

    public synchronized PanelConsole getPanelConsole() {
        return panelConsole;
    }

    public void shipError()
    {
        int i = JOptionPane.showOptionDialog(this,"Statek zatonął!","ZDERZENIE",JOptionPane.DEFAULT_OPTION,JOptionPane.ERROR_MESSAGE,null,null,null);
        if (i==0 ||i == -1)
        {
            this.dispose(); //zamknij aplikacje
        }
    }
    public void moveError()
    {
        JOptionPane.showMessageDialog(this,"Niedozwolony ruch!","",JOptionPane.WARNING_MESSAGE);
    }
}
