package nju.java;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by yangmengfei on 2017/12/20.
 */
public class Creature extends Thing2D {
    public Field field;
    private String pic;
    private final int SPACE = 20;
    public int die;
    public void stop()  {
        try {
            Thread.sleep(100000000);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
    public Creature(int x, int y, Field field, String pic) {
        super(x, y);

        this.field = field;
        this.pic = pic;

        URL loc = this.getClass().getClassLoader().getResource(pic);
        ImageIcon iia = new ImageIcon(loc);
        Image image = iia.getImage();
        this.setImage(image);

        die=0;
    }
    public void repaint() {
        this.field.repaint();
    }
}
