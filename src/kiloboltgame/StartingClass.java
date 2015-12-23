package kiloboltgame;

import java.applet.Applet;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.StringTokenizer;

import kiloboltframework.Animation;

/**
 * Created by moRhen on 26.11.15.
 */
public class StartingClass extends Applet implements Runnable, KeyListener {

    private static Robot robot;
    private Heliboy hb1, hb2;
    private Animation anim, hanim;
    private Image image,currentSprite, character, character2, character3, background, characterDown, characterJumped, heliboy, heliboy2, heliboy3, heliboy4, heliboy5;
    public static Image tiledirt, tilegrassTop, tilegrassBot, tilegrassLeft, tilegrassRight;
    private URL base;
    private Graphics second;
    private static Background bg1, bg2;
    private ArrayList<Tile> tilearray = new ArrayList<Tile>();

    @Override
    public void init() {
        setSize(800, 480);
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(this);
        Frame frame = (Frame) this.getParent().getParent();
        frame.setTitle("Q-Bot Alpha");
        try {
            //TODO: not sure about that..
            base = StartingClass.class.getResource("/data/");
        } catch (Exception e) {
            //TODO: handling exception
        }

        character = getImage(base, "character.png");
        character2 = getImage(base, "character2.png");
        character3 = getImage(base, "character3.png");
        characterDown = getImage(base, "down.png");
        characterJumped = getImage(base, "jumped.png");

        background = getImage(base, "background.png");

        heliboy = getImage(base, "heliboy.png");
        heliboy2 = getImage(base, "heliboy2.png");
        heliboy3 = getImage(base, "heliboy3.png");
        heliboy4 = getImage(base, "heliboy4.png");
        heliboy5 = getImage(base, "heliboy5.png");

        tiledirt = getImage(base, "tiledirt.png");
        tilegrassTop = getImage(base, "tilegrasstop.png");
        tilegrassBot = getImage(base, "tilegrassbot.png");
        tilegrassLeft = getImage(base, "tilegrassleft.png");
        tilegrassRight = getImage(base, "tilegrassright.png");

        anim = new Animation();
        anim.addFrame(character, 1250);
        anim.addFrame(character2, 50);
        anim.addFrame(character3, 50);
        anim.addFrame(character2, 50);

        hanim = new Animation();
        hanim.addFrame(heliboy, 100);
        hanim.addFrame(heliboy2, 100);
        hanim.addFrame(heliboy3, 100);
        hanim.addFrame(heliboy4, 100);
        hanim.addFrame(heliboy5, 100);
        hanim.addFrame(heliboy4, 100);
        hanim.addFrame(heliboy3, 100);
        hanim.addFrame(heliboy2, 100);

        currentSprite = character;
    }

    @Override
    public void start() {
        bg1 = new Background(0, 0);
        bg2 = new Background(2160, 0);
        robot = new Robot();

        try{
            //lame but it works for now
            String mapDir = System.getProperty("user.dir") + "/IdeaProjects/KiloboltGame/out/production/KiloboltGame/data/map1.txt";
            loadMap(mapDir);
        }catch (IOException e){
            e.printStackTrace();
        }
        hb1 = new Heliboy(340, 360);
        hb2 = new Heliboy(700, 360);
        Thread thread = new Thread(this);
        thread.start();

    }

    private void loadMap(String filename) throws IOException {
        ArrayList lines = new ArrayList();
        int width = 0;
        int height = 0;

        BufferedReader reader = new BufferedReader(new FileReader(filename));
        while(true){
            String line = reader.readLine();
            if(line == null){
                reader.close();
                break;
            }
            if(!line.startsWith("!")){
                lines.add(line);
                width = Math.max(width, line.length());
            }
        }
        height = lines.size();
        for(int j = 0; j < 12; j++){
            String line = (String) lines.get(j);
            for(int i = 0; i < width; i++){
                System.out.println(i + "is i ");
                if(i < line.length()){
                    char ch = line.charAt(i);
                    Tile t = new Tile(i, j, Character.getNumericValue(ch));
                    tilearray.add(t);
                }
            }
        }
    }

    @Override
    public void stop() {

    }

    @Override
    public void destroy() {

    }

    @Override
    public void run() {
        while (true) {
            robot.update();
            if (robot.isJumped()) {
                currentSprite = characterJumped;
            } else if (!robot.isJumped() && !robot.isDucked()) {
                currentSprite = anim.getImage();
            }
            ArrayList projectiles = robot.getProjectiles();
            for(int i = 0; i < projectiles.size(); i++){
                Projectile p =(Projectile) projectiles.get(i);
                if(p.isVisible()){
                    p.update();
                }else{
                    projectiles.remove(i);
                }
            }
            updateTiles();
            hb1.update();
            hb2.update();
            bg1.update();
            bg2.update();
            animate();
            repaint();
            try {
                Thread.sleep(17);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void update(Graphics g) {
        if (image == null) {
            image = createImage(this.getWidth(), this.getHeight());
            second = image.getGraphics();
        }
        second.setColor(getBackground());
        second.fillRect(0, 0, getWidth(), getHeight());
        second.setColor(getForeground());
        paint(second);
        g.drawImage(image, 0, 0, this);
    }

    @Override
    public void paint(Graphics g) {
        g.drawImage(background, bg1.getBgX(), bg1.getBgY(), this);
        g.drawImage(background, bg2.getBgX(), bg2.getBgY(), this);
        paintTiles(g);

        ArrayList projectiles =  robot.getProjectiles();
        for(int i = 0; i < projectiles.size(); i++){
            Projectile p = (Projectile) projectiles.get(i);
            g.setColor(Color.YELLOW);
            g.fillRect(p.getX(), p.getY(), 10, 5);
        }
        g.drawRect((int)robot.rect.getX(), (int)robot.rect.getY(), (int)robot.rect.getWidth(), (int)robot.rect.getHeight());
        g.drawRect((int)robot.rect2.getX(), (int)robot.rect2.getY(), (int)robot.rect2.getWidth(), (int)robot.rect2.getHeight());
        g.drawRect((int)robot.rect3.getX(), (int)robot.rect3.getY(), (int)robot.rect3.getWidth(), (int)robot.rect3.getHeight());
        g.drawRect((int)robot.rect4.getX(), (int)robot.rect4.getY(), (int)robot.rect4.getWidth(), (int)robot.rect4.getHeight());
        g.drawRect((int)robot.yellowRed.getX(), (int)robot.yellowRed.getY(), (int)robot.yellowRed.getWidth(), (int)robot.yellowRed.getHeight());
        g.drawImage(currentSprite, robot.getCenterX() - 61, robot.getCenterY() - 63, this);
        g.drawImage(hanim.getImage(), hb1.getCenterX() - 48, hb1.getCenterY() - 48, this);
        g.drawImage(hanim.getImage(), hb2.getCenterX() - 48, hb2.getCenterY() - 48, this);
    }

    private void updateTiles(){
        for(int i = 0; i < tilearray.size(); i++){
            Tile t = (Tile) tilearray.get(i);
            t.update();
        }
    }

    private void paintTiles(Graphics g){
        for(int i = 0; i < tilearray.size(); i++){
            Tile t = (Tile) tilearray.get(i);
            g.drawImage(t.getTileImage(), t.getTileX(), t.getTileY(), this);
        }
    }

    public void animate(){
        anim.update(10);
        hanim.update(50);
    }

    @Override
    public void keyTyped(KeyEvent keyEvent) {

    }

    @Override
    public void keyPressed(KeyEvent e) {

        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP:
                System.out.println("Move up");
                break;

            case KeyEvent.VK_DOWN:
                currentSprite = characterDown;
                if (!robot.isJumped()){
                    robot.setDucked(true);
                    robot.setSpeedX(0);
                }
                break;

            case KeyEvent.VK_LEFT:
                robot.moveLeft();
                robot.setMovingLeft(true);
                break;

            case KeyEvent.VK_RIGHT:
                robot.moveRight();
                robot.setMovingRight(true);
                break;

            case KeyEvent.VK_SPACE:
                robot.jump();
                break;

            case KeyEvent.VK_CONTROL:
                if(!robot.isDucked() && !robot.isJumped()){
                    robot.shoot();
                    robot.setReadyToFire(false);
                }
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP:
                System.out.println("Stop moving up");
                break;

            case KeyEvent.VK_DOWN:
                currentSprite = anim.getImage();
                robot.setDucked(false);
                break;

            case KeyEvent.VK_LEFT:
                robot.stopLeft();
                break;

            case KeyEvent.VK_RIGHT:
                robot.stopRight();
                break;

            case KeyEvent.VK_SPACE:
                System.out.println("Stop jumping");
                break;
            case KeyEvent.VK_CONTROL:
                robot.setReadyToFire(true);
                break;
        }
    }
    public static Background getBg1(){
        return bg1;
    }
    public static Background getBg2(){
        return bg2;
    }

    public static Robot getRobot() {
        return robot;
    }
}
