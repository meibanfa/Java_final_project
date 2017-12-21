package nju.java;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.util.Random;

/**
 * Created by yangmengfei on 2017/12/20.
 */
public class Monster extends Creature  implements Runnable{
    public int id;
    public Monster(int x, int y, int id, Field field, String pic) {
        super(x, y, field, pic);

        URL loc = this.getClass().getClassLoader().getResource(pic);
        ImageIcon iia = new ImageIcon(loc);
        Image image = iia.getImage();
        this.setImage(image);
        this.id =id;
    }
    public void move(int x, int y) {
        int nx = this.x() + x;
        int ny = this.y() + y;
        this.setX(nx);
        this.setY(ny);
    }

    public void run() {
        while (!Thread.interrupted()) {
            Random rand = new Random();
            try {
                if(this.die==1) break;
                Thread.sleep(rand.nextInt(1000) + 1000);

                Thing2D t = field.fd_min_calabash(this);
                this.field.change_value(t, this);

                this.repaint();

            } catch (Exception e) {

            }
        }
    }
}
