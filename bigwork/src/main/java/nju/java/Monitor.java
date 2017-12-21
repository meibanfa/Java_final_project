/**
 * Created by yangmengfei on 2017/12/21.
 */
package nju.java;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.util.Random;
import java.io.*;
public class Monitor extends Creature  implements Runnable {
    public Monitor(int x, int y, Field field, String pic) {
        super(x, y, field, pic);
    }

    public void run() {
        while (!Thread.interrupted()) {
            Random rand = new Random();
            try {
                String file = field.runfile;
                FileReader fr = new FileReader(file);
                BufferedReader bf = new BufferedReader(fr);
                String line = "";
                while ((line = bf.readLine()) != null) {
                    Thread.sleep(rand.nextInt(100));
                    field.parse_order(line);
                    this.repaint();
                }
                break;
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
