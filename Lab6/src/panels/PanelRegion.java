package panels;

import javax.swing.*;
import java.awt.*;

public class PanelRegion extends JPanel
{
    PanelInside[][] panels;
    public PanelRegion()
    {
        this.panels = new PanelInside[5][5];
        this.setLayout(new GridLayout(5,5,2,2));
        this.setBorder(BorderFactory.createLineBorder(Color.GREEN,1));
        this.setBackground(Color.black);

        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                panels[i][j] = new PanelInside();
                this.add(panels[i][j]);
            }
        }
    }

    public PanelInside[][] getPanels() {
        return panels;
    }
}