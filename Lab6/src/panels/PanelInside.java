package panels;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;

public class PanelInside extends JPanel {
    static public boolean color = false;
    int shape;
    int number;
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (color&&shape==3)
            drawColor();
        else {
            if (shape == 1)
                drawShip(g);
            else if (shape == 2)
                drawOwnShip(g);
            else if (shape==3)
                drawNumber(g);
        }
    }

    public void drawShip(Graphics g)
    {
        Graphics2D g2d = (Graphics2D) g;
        if (this.getWidth()/2<this.getHeight()/2) //mechanizm, aby kropka była na środku
            g2d.fill(new Ellipse2D.Double(this.getWidth()/4,this.getHeight()/4+(this.getHeight()/4-this.getWidth()/4),this.getWidth()/2,this.getWidth()/2));
        else
            g2d.fill(new Ellipse2D.Double(this.getWidth()/4+(this.getWidth()/4-this.getHeight()/4),this.getHeight()/4,this.getHeight()/2,this.getHeight()/2));
    }
    public void drawOwnShip(Graphics g)
    {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setPaint(Color.RED);
        drawShip(g);
    }
    public void drawNumber(Graphics g)
    {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setPaint(Color.BLACK);
        g2d.drawString(String.valueOf(number),0,this.getHeight()/2);
    }
    public void drawColor()
    {
        if (number*20<=255) {
            this.setBackground(new Color(40, number * 20, 255));
        }
        else
            this.setBackground(new Color(40, 255, 255-((number * 20)-255)));
    }

    public void setShape(int shape) {
        this.shape = shape;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public int getNumber() {
        return number;
    }
}
