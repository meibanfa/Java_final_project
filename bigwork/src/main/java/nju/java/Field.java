package nju.java;

import sun.awt.geom.AreaOp;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.lang.reflect.Array;
import java.util.ArrayList;
import javax.swing.JPanel;
import java.util.Iterator;
import java.util.Random;
import java.util.concurrent.locks.*;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import java.io.*;

public class Field extends JPanel {

    private final int OFFSET = 30;
    private final int SPACE = 20;
    private final int CALABASH = 1;
    private final int MONSTER =2;
    private final int MOVE =1;
    private final int DIE = 2;
    private final double[][] OPPO= {{0,0,0,0,0}, {0,0.6}};

    private int mod = 1;
    public String runfile;
    public String outfile="";
    private FileOutputStream out;
    BufferedWriter bw;
    private ArrayList thread = new ArrayList();
    private ArrayList tiles = new ArrayList();
    private ArrayList monster = new ArrayList();
    private ArrayList calabash = new ArrayList();
    private Monitor monitor;
    private int calabash_id, monster_id;
    public Lock lock = new ReentrantLock();

    private int w = 0;
    private int h = 0;
    private boolean completed = false;

    private String level =
            "...............\n" +
            ".........m.....\n" +
            ".1......m......\n" +
            "..2....mm.m....\n" +
            "...3..mmm......\n" +
            "....4.mmm......\n" +
            "...5..mmm......\n" +
            "..6....mm.m....\n" +
            ".7......m......\n" +
            ".........m.....\n" +
            "...............\n";

    public Field() {
        addKeyListener(new TAdapter());
        setFocusable(true);
        initWorld();

    }

    public int getBoardWidth() {
        return this.w;
    }

    public int getBoardHeight() {
        return this.h;
    }

    public final void initWorld() {

        int x = OFFSET;
        int y = OFFSET;

        Tile a;
        Calabash c; calabash_id =0;
        Monster m; monster_id = 0;


        for (int i = 0; i < level.length(); i++) {

            char item = level.charAt(i);

            if (item == '\n') {
                y += SPACE;
                if (this.w < x) {
                    this.w = x;
                }

                x = OFFSET;
            }
            else {
                a = new Tile(x, y);
                tiles.add(a);
                if (item == '1') {
                    c = new Calabash( x, y, calabash_id++,this, "red.png");
                    calabash.add(c);
                }
                else if(item == '2'){
                    c = new Calabash( x, y, calabash_id++, this, "ora.png");
                    calabash.add(c);
                }
                else if(item == '3'){
                    c = new Calabash( x, y, calabash_id++, this, "yel.png");
                    calabash.add(c);
                }
                else if(item == '4'){
                    c = new Calabash( x, y, calabash_id++, this, "gre.png");
                    calabash.add (c);
                }
                else if(item == '5'){
                    c = new Calabash( x, y, calabash_id++, this, "gbl.png");
                    calabash.add(c);
                }
                else if(item == '6'){
                    c = new Calabash( x, y, calabash_id++, this, "blu.png");
                    calabash.add(c);
                }
                else if(item == '7'){
                    c = new Calabash( x, y, calabash_id++, this, "vio.png");
                    calabash.add(c);
                }
                else if(item == 'm'){
                    m = new Monster( x, y, monster_id++, this, "ene.png");
                    monster.add(m);
                }
                x += SPACE;
            }

            h = y;
        }
        monitor = new Monitor(-100,-100,this, "player.png");
    }

    public void buildWorld(Graphics g) {

        g.setColor(new Color(250, 240, 170));
        g.fillRect(0, 0, this.getWidth(), this.getHeight());

        ArrayList world = new ArrayList();
        world.addAll(tiles);

        world.addAll(calabash);

        world.addAll(monster);
        for (int i = 0; i < world.size(); i++) {

            Thing2D item = (Thing2D) world.get(i);

            if (item instanceof Player) {
                g.drawImage(item.getImage(), item.x() + 2, item.y() + 2, this);
            } else {
                g.drawImage(item.getImage(), item.x(), item.y(), this);
            }
            if (completed) {
                g.setColor(new Color(0, 0, 0));
                g.drawString("Completed", 25, 20);
            }
        }
    }
    void change_value(Thing2D t, Calabash c){
        lock.lock();
        try {
            int flag=1;
            for(int i=0;i<calabash.size();++i){
                Calabash m = (Calabash)calabash.get(i);
                if(m.x() == t.x() && m.y() == t.y()){
                    flag=0; break;
                }
            }
            if(flag==1) {
                if(c.x()!=t.x() || c.y()!=t.y()) {
                    Thing2D tc = new Thing2D(c.x(), c.y());
                    move(CALABASH, c.id, tc, t);
                }
                c.setX(t.x());
                c.setY(t.y());
                for(int i=0;i<monster.size();++i){
                    Monster ca= (Monster)monster.get(i);
                    if(ca.x() == c.x() && ca.y() == c.y()){
                        Random rand = new Random();
                        int r= rand.nextInt(1000);
                        if(r > 700) {
                            die(c);
                        }
                        else {
                            die(ca);
                        }
                    }
                }
            }
        }finally {
            lock.unlock();
        }
    }
    void die( Calabash c){
        int id = -1, type = CALABASH;
        Iterator<Calabash> it = calabash.iterator();
        while(it.hasNext()){
            Calabash tc = it.next();
            if(tc == c){
                id=c.id; c.die =1;
                it.remove(); break;
            }
        }
        if(id!=-1) {
            try {
                bw.write(DIE + " " + type + " " + id + "\n");
            }
            catch (Exception e){

            }
            System.out.print(DIE + " " + type + " " + id + "\n");
        }
    }
    void die(Monster c){
        int id = -1, type = MONSTER;
        Iterator<Monster> it = monster.iterator();
        while(it.hasNext()){
            Monster tc = it.next();
            if(tc == c){
                id=c.id; c.die =1;
                it.remove(); break;
            }
        }
        if(id!=-1) {
            try {
                bw.write(DIE + " " + type + " " + id + "\n");
            } catch (Exception e) {

            }
            System.out.print(DIE + " " + type + " " + id + "\n");
        }
    }
    void move(int type, int id, Thing2D c, Thing2D t){
        try {
            bw.write(MOVE+" "+type+" "+id+" ");
            bw.write(c.x()+" "+c.y()+" "+t.x()+" "+t.y()+"\n");
        }
        catch (Exception e){

        }
        System.out.print(MOVE+" "+type+" "+id+" ");
        System.out.print(c.x()+" "+c.y()+" "+t.x()+" "+t.y()+"\n");
    }
    void change_value(Thing2D t, Monster c){
        lock.lock();
        try {
            int flag=1;
            for(int i=0;i<monster.size();++i){
                Monster m  = (Monster)monster.get(i);
                if(m.x() == t.x() && m.y() == t.y()){
                    flag=0; break;
                }
            }
            if(flag==1) {
                if(c.x()!=t.x() || c.y()!=t.y()) {
                    Thing2D tc = new Thing2D(c.x(), c.y());
                    move(MONSTER, c.id, tc, t);
                }
                c.setX(t.x());
                c.setY(t.y());
                for(int i=0;i<calabash.size();++i) {
                    Calabash ca = (Calabash) calabash.get(i);
                    if (ca.x() == c.x() && ca.y() == c.y()) {
                        Random rand = new Random();
                        int r= rand.nextInt(1000);
                        if(r > 700) {
                            die(ca);
                        }
                        else {
                            die(c);
                        }
                    }
                }
            }
        }finally {
            lock.unlock();
        }
    }
    public Thing2D fd_min_monster(Calabash ca){
        lock.lock();
        Thing2D t = new Thing2D(ca.x(), ca.y());
        double min_dis= 100000000000.0;
        Thing2D nt = new Thing2D(t.x(), t.y());
        try{
           for(int i=0;i<monster.size();++i){
               Monster m = (Monster) monster.get(i);
               double x = t.x()- m.x(); x*=x;
               double y = t.y()- m.y(); y*=y;
               if(x+y < min_dis){
                   min_dis = x+y;
                   int nx=t.x(), ny=t.y();
                   if(t.x() < m.x()){
                       nx = t.x() + SPACE;
                   }
                   else if(t.x() > m.x()){
                       nx = t.x() - SPACE;
                   }
                   if(t.y() < m.y()){
                       ny = t.y() + SPACE;
                   }
                   else if(t.y() > m.y()){
                       ny = t.y() - SPACE;
                   }
                   nt.setX(nx); nt.setY(ny);
               }
           }
        }finally {
            lock.unlock();
        }
        return nt;
    }
    public Thing2D fd_min_calabash(Monster mon){
        lock.lock();
        Thing2D t = new Thing2D(mon.x(), mon.y());
        double min_dis= 100000000000.0;
        Thing2D nt = new Thing2D(t.x(), t.y());
        try{
            for(int i=0;i<calabash.size();++i){
                Calabash m = (Calabash) calabash.get(i);
                double x = t.x()- m.x(); x*=x;
                double y = t.y()- m.y(); y*=y;
                if(x+y < min_dis){
                    min_dis = x+y;
                    int nx=t.x(), ny=t.y();
                    if(t.x() < m.x()){
                        nx = t.x() + SPACE;
                    }
                    else if(t.x() > m.x()){
                        nx = t.x() - SPACE;
                    }
                    if(t.y() < m.y()){
                        ny = t.y() + SPACE;
                    }
                    else if(t.y() > m.y()){
                        ny = t.y() - SPACE;
                    }
                    nt.setX(nx); nt.setY(ny);
                }
            }
        }finally {
            lock.unlock();
        }
        return nt;
    }
    @Override
    public void paint(Graphics g) {
        super.paint(g);
        buildWorld(g);
    }
    void del(int type, int id){
        if(type== CALABASH) {
            Iterator<Calabash> it = calabash.iterator();
            while (it.hasNext()) {
                Calabash tc = it.next();
                if (tc.id == id) {
                    it.remove();
                    break;
                }
            }
        }
        else if(type == MONSTER){
            Iterator<Monster> it= monster.iterator();
            while(it.hasNext()){
                Monster m = it.next();
                if(m.id == id){
                    it.remove();
                    break;
                }
            }
        }
    }
    void mov(int type, int id, int nx, int ny){
        if(type == CALABASH){
            Iterator<Calabash> it = calabash.iterator();
            while (it.hasNext()) {
                Calabash tc = it.next();
                if (tc.id == id) {
                    tc.setX(nx); tc.setY(ny);
                    break;
                }
            }
        }
        else if(type == MONSTER) {
            Iterator<Monster> it= monster.iterator();
            while(it.hasNext()){
                Monster m = it.next();
                if(m.id == id){
                    m.setX(nx); m.setY(ny);
                    break;
                }
            }
        }
    }
    void parse_order(String line){
        lock.lock();
        String []arr;
        arr=line.split(" ");
        int []ans = new int[10];
        for(int i=0;i<arr.length;++i){
            ans[i]=Integer.parseInt(arr[i]);
        }
        int event=ans[0], type=ans[1], id=ans[2];
        if(event==MOVE){
            int nx = ans[5], ny = ans[6];
            mov(type, id, nx, ny);
        }
        else if(event ==DIE){
            del(type, id);
        }
        lock.unlock();
    }

    class TAdapter extends KeyAdapter {

        @Override
        public void keyPressed(KeyEvent e) {
            if (completed) {
                return;
            }
            int key = e.getKeyCode();

            if (key == KeyEvent.VK_SPACE) {
                for(int i=0;i<calabash.size();++i){
                    Thread t = new Thread((Calabash)calabash.get(i));
                    thread.add(t);
                    t.start();
                }
                for(int i=0;i<monster.size();++i){
                    Thread t = new Thread((Monster)monster.get(i));
                    thread.add(t);
                    t.start();
                }
                if(outfile.length() ==0) {
                    outfile = "out.txt";
                }
                try {
                    FileWriter fw = new FileWriter(outfile, false);
                    bw = new BufferedWriter(fw);
                }
                catch (Exception te){

                }
                //new Thread(player).start();
            }
            else if(key == KeyEvent.VK_S){
                try{
                    bw.close();
                }
                catch (Exception ee){

                }
            }
            else if(key == KeyEvent.VK_L){
                JFileChooser jfc=new JFileChooser();
                jfc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES );
                jfc.showDialog(new JLabel(), "选择");
                File file=jfc.getSelectedFile();
                while(file.isDirectory()){
                    System.out.println("文件夹:"+file.getAbsolutePath());
                    jfc.showDialog(new JLabel(), "请重新选择");
                    file=jfc.getSelectedFile();
                }
                runfile = jfc.getSelectedFile().getAbsolutePath();
                System.out.println(runfile);
                restartLevel();
                Thread t = new Thread(monitor);
                t.start();
                thread.add(t);
            }
            else if (key == KeyEvent.VK_R) {
                restartLevel();
            }

            repaint();
        }
    }
    void stopall(){
        Iterator<Thread> it = thread.iterator();
        while(it.hasNext()){
            Thread t = it.next();
            t.interrupt();
        }
        thread.clear();
    }

    public void restartLevel() {
        tiles.clear();
        stopall();
        calabash.clear();
        monster.clear();
        initWorld();
        if (completed) {
            completed = false;
        }
    }
}