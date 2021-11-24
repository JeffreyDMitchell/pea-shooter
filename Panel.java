/*
-CLEAN UP
-make dodging less jank
-change emitter life / path duration stuff
-rework constructors and parameter setting mess
-make defining levels more concise, load levels from text file?
-make levels
-add level "scripting"
-add boss fights
*/
//dude this is actually so bad hehe 

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Collections;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.io.*;
import java.nio.channels.AsynchronousCloseException;


public class Panel extends JPanel implements ActionListener, KeyListener
{
    
    Timer t = new Timer(5, this);

    ArrayList<Level> levels = new ArrayList<Level>();
    ArrayList<Player> pList = new ArrayList<Player>();
    ArrayList<Projectile> projList = new ArrayList<Projectile>();
    ArrayList<Mobile> movList = new ArrayList<Mobile>();
    ArrayList<Drawable> drawList = new ArrayList<Drawable>();
    ArrayList<Emitter> emitList = new ArrayList<Emitter>();
    ArrayList<Prompt> promptList = new ArrayList<Prompt>();

    ArrayList[] LISTS = new ArrayList[]{pList, projList, emitList, movList, drawList, promptList};

    Player player = new Player(960, 540);
    Player boss;

    Player testplayer = new Player(500, 500, "./resources/enemyship1.png");

    Level currentLevel;
    int levelIndex = 1;
    int maxLevel = 1;
    long levelTime = 0;
    int fc = 0;
    int bossState = 0;

    Level levelOne, levelTwo;

    Emitter AHH;
    Emitter BHH;
    Emitter CHH;

    final int WINDOWBAR_WIDTH = 85;
    final int PLAYER_MAX_SPEED = 10;
    final int PLAYER_WIDTH = 50;
    int SCREEN_WIDTH, SCREEN_HEIGHT;
    
    public Panel(int x, int y)
    {

        setBackground(new Color(0x676767));
        

        addKeyListener(this);
        setFocusable(true);

        SCREEN_WIDTH = x;
        SCREEN_HEIGHT = y - WINDOWBAR_WIDTH;

        //testing nonsense
        {

        // try
        // {

        //     shipImage = ImageIO.read(new File("./resources/ship.png"));

        // }
        // catch(IOException e)
        // {

        //     System.out.println("uh oh fucky wucky");

        // }

        //TODO temp code
        player.hp = player.maxHP = 3000;
        player.sizeX = 35;
        player.sizeY = 35;
        player.allegiance = 0;
        player.decayFactor = .9;
        player.maxCooldown = 50;
        player.spread = .1;
        player.textured = true;
        player.setImage("./resources/ship.png");
        player.rammingDmg = 1;

        //TODO temp code spawn players at start
        for(int i = 0; i < 0; i++)
        {

            Player p = new Player(rand(0, SCREEN_WIDTH), rand(0, SCREEN_HEIGHT), rand(-3, 3), rand(-3, 3));

            p.decayFactor = 1;

            setLists(p, new int[]{1, 0, 0, 1, 1, 0});

        }

        

        //TODO temp code testing emitters
        //AHH = new Emitter(new Path(new Point(100, 100), new Point(500, 700)), 5000);
        //BHH = new Emitter(new Path(new Point(500, 500), new Point(500, 700)), 5000);
        //CHH = new Emitter(new Path(new Point(100, 100), new Point(500, 700)), 5000);

        //BHH.mortal = false;

        //AHH = new Emitter(960, 540, 100000);

        levelOne = new Level();
        levelTwo = new Level();

        levels.add(levelOne);
        levels.add(levelTwo);

        Emitter test = new Emitter(new Path(new Point(0, 0), new Point(500, 500)), 100);
        test.props = new double[]{100, 5, 2, 2, 2};
        test.type = 1;
        test.life = 15000;
        test.mortal = true;

        Emitter test2 = new Emitter(SCREEN_WIDTH / 2.0, SCREEN_HEIGHT / 2.0);
        test2.life = 20000;
        test2.props = new double[]{20, 6, 1, .01, 1000};
        test2.type = 2;
        test2.mortal = true;

        //Emitter test3 = new Emitter(new Path(new Point(0, 0), new Point(SCREEN_WIDTH, SCREEN_HEIGHT)), 10000);

        ArrayList<Emitter> emitgthree = new ArrayList<Emitter>();
        ArrayList<Player> playgthree = new ArrayList<Player>();

        for(int j = 0; j < 4; j++)
            for(int i = 0; i < 4; i++)
            {

                Emitter temp = new Emitter(new Path(new Point(-40 + (10 * i) - (300 * j), 1), new Point(SCREEN_WIDTH + 1200 + (10 * i) - (300 * j), 1)), 10000);
                temp.type = 3;
                temp.dir = 2;
                temp.props = new double[]{2, 4, 0};
                //temp.props = new double[]{2, 1, 10};

                temp.allegiance = 2;

                emitgthree.add(temp);

            }
        // for(int j = 0; j < 4; j++)
        //     for(int i = 0; i < 100; i++)
        //     {

        //         Emitter temp = new Emitter(1, map(i, 100, 0, SCREEN_HEIGHT, 0), 1000);

        //         temp.type = 3;
        //         temp.dir = 1;
        //         temp.props = new double[]{1000 + j, 1, 0};

        //         temp.allegiance = 2;

        //         emitgthree.add(temp);

        //     }

        double theta = 0;

        for(int i = 0; i < 8; i++)
        {

            Player temp = new Player(960 + 100 * Math.cos(theta), 540 + 100 * Math.sin(theta));

            temp.setImage("./resources/alien.png");
            temp.textured = true;

            playgthree.add(temp);

            theta += 2 * Math.PI / 8;

        }


        // levelOne.Groups.add(new ArrayList());

        // levelOne.Groups.get(0).add(test);
        // levelOne.Groups.get(0).add(new Player(750, 750, -1, -1));

        ArrayList<Player> playgzero = new ArrayList<Player>();
        ArrayList<Emitter> emitgzero = new ArrayList<Emitter>(); 

        ArrayList<Player> playgone = new ArrayList<Player>();
        ArrayList<Emitter> emitgone = new ArrayList<Emitter>();

        ArrayList<Emitter> emitgtwo = new ArrayList<Emitter>();
        
        //emitgzero.add(test);
        playgzero.add(testplayer);

        playgone.add(new Player(300, 400, ("./resources/enemyship" + (int) (Math.random() * 2 + 1) + ".png")));
        playgone.add(new Player(200, 21, 10, 10, ("./resources/enemyship" + (int) (Math.random() * 2 + 1) + ".png")));
        playgone.add(new Player(400, 400, ("./resources/enemyship" + (int) (Math.random() * 2 + 1) + ".png")));

        emitgtwo.add(test2);

        

        levelOne.Emitters.add(emitgzero);
        levelOne.Players.add(playgzero);

        levelOne.Emitters.add(emitgone);
        levelOne.Players.add(playgone);
        levelOne.Times.add(10000);

        levelOne.Emitters.add(emitgtwo);
        levelOne.Players.add(new ArrayList<Player>());
        levelOne.Times.add(20000);

        levelOne.Emitters.add(emitgthree);
        levelOne.Players.add(playgthree);
        levelOne.Times.add(45000);

        ArrayList<Player> ohboy = new ArrayList<Player>();

        Player l2player = new Player(50, 50, ("./resources/enemyboss.png"));
        l2player.sizeX = 320;
        l2player.sizeY = 135;
        l2player.setRot(Math.PI);
        l2player.hp = l2player.maxHP = 750;
        l2player.rammingDmg = 25;
        l2player.allegiance = 5; //TODO
        ohboy.add(l2player);

        // Player a = new Player(50, 500, "./resources/enemyship1.png");
        // a.hp = a.maxHP = 50;
        // a.rammingDmg = 0;

        // Player b = new Player(350, 500, "./resources/enemyship1.png");
        // a.hp = a.maxHP = 50;
        // a.rammingDmg = 1;

        // Player c = new Player(650, 500, "./resources/enemyship1.png");
        // a.hp = a.maxHP = 50;
        // a.rammingDmg = 5;

        // Player d = new Player(950, 500, "./resources/enemyship1.png");
        // a.hp = a.maxHP = 50;
        // a.rammingDmg = 10;

        // ohboy.add(a);
        // ohboy.add(b);
        // ohboy.add(c);
        // ohboy.add(d);

        for(int i = 0; i < 60; i++)
        {

            Player a = new Player(Math.random() * SCREEN_WIDTH, Math.random() * SCREEN_HEIGHT, ("./resources/enemyship" + (int) (Math.random() * 2 + 1) + ".png"));
            a.hp = a.maxHP = 100 + (int) (Math.random() * 200);
            a.rammingDmg = (int) (Math.random() * 10 + 1);
            a.allegiance = i;
            a.target = player;

            ohboy.add(a);

        }

        Prompt testprompt = new Prompt(player, 10000, "I'm just a little goblin");
        testprompt.duration = 5000;
        testprompt.font = new Font("Aerial", Font.PLAIN, 25);

        Prompt anotherPrompt = new Prompt(50, 50, 15000, "What the fuck did you just say about me you little punk?\nI'll ahve you know I'm a US navy seal\nblah blah blah\ni just need more text tbh\nI'm gonna keap it up wahoo wahoo this is a test of the prompt system,\nmaybe i can test my typing seed too!\nI'd like to maybe add some fancy stuff, like changing font / style mid string,\nmaybe i could have some zany effects\nlike shaking in the mix. not right now though, this is just a test");
        anotherPrompt.duration = 5000;
        anotherPrompt.font = new Font("TimesRoman", Font.PLAIN, 50);

        //levelTwo.Prompts.add(testprompt);
        //levelTwo.Prompts.add(anotherPrompt);

        levelTwo.Players.add(ohboy);
        levelTwo.Times.add(10);

        }  

        loadLevel(levels.get(levelIndex));

        t.start();

    }

    public void loadLevel(Level level)
    {

        for(ArrayList l : LISTS)
        {

            l.clear();

        }

        setLists(player, new int[]{1, 0, 0, 1, 1, 0});

        player.hp = player.maxHP;

        currentLevel = level;

        levelTime = 0;

        initPhase();

        levelLoadScripts(levelIndex); //kinda bad, loadlevel works for generic levels, levelloadscripts takes a level index

    }

    //cuter random method
    public double rand(double min, double max)
    {

        return min + ((max - min) * Math.random());

    }

    //maps value from old range to new
    public double map(double val, double omax, double omin, double max, double min)
    {

        return min + ((max - min) * ((val - omin) / (omax - omin)));

    }

    //adds or removes objects from various lists
    public void setLists(Object o, int[] set)
    {

        if(LISTS.length != set.length)
            System.out.println("possible fuckup");

        for(int i = 0; i < Math.min(set.length, LISTS.length); i++)
        {

            if(set[i] == 1 && !LISTS[i].contains(o))
                LISTS[i].add(o);
            else if (set[i] == 0 && LISTS[i].contains(o))
                LISTS[i].remove(o);

        }

        //bruh this actually works WHAT hahaha. orders list in such a way that bullets are drawn behind players
        //maybe only sort list when adding players, move them to front of list, bullets will be added to end by default,
        if(o instanceof Player)
            Collections.sort(drawList); //TODO probably bad for performance, runs on each player spawn, sorts drawlist

    }

    //if called with only one parameter, removes from all lists, overload
    public void setLists(Object o)
    {

        setLists(o, new int[LISTS.length]);

    }

    //explode overload
    public void explode(Shooter p, int count, double speed)
    {

        explode(p, count, speed, 0);

    }

    //explode overload
    public void explode(double x, double y, int count, double speed)
    {

        explode(x, y, count, speed, 0);

    }

    //explode "overload"
    public void explode(Shooter p, int count, double speed, double offset)
    {

        double theta = offset;

        for(int j = 0; j < count; j++)
        {

            Projectile proj = new Projectile(p, Math.sin(theta) * speed, Math.cos(theta) * speed);

            setLists(proj, new int[]{0, 1, 0, 1, 1, 0});

            theta += 2 * Math.PI / count;

        }

    }

    //creates projectiles around target coordinate x, y, modulated by count speed and offset.
    public void explode(double x, double y, int count, double speed, double offset)
    {

        double theta = offset;

        for(int j = 0; j < count; j++)
        {

            Projectile proj = new Projectile(x, y, Math.sin(theta) * speed, Math.cos(theta) * speed);

            setLists(proj, new int[]{0, 1, 0, 1, 1, 0});

            theta += 2 * Math.PI / count;

        }

    }

    void dodge(Player p)
    {

        //really jank way of keeping player from dodging in place. same as if p.control[0] == 0 && p.control[1] == 0 && .... probably worse tbh. oh well, only happens when dodging, so low impact
        if(.1 * p.control[0] + .2 * p.control[1] + .3 * p.control[2] + .4 * p.control[3] == 0)
            return;

        if(p.iFrames < 30)
            p.iFrames = 30;

        p.rFrames = 100;

        p.xVel += 13 * (p.control[2] + p.control[3]);
        p.yVel += 13 * (p.control[0] + p.control[1]);

    }

    void initPhase()
    {

            if(currentLevel.Players.size() > 0)
            {

                for(Player p : currentLevel.Players.get(0))
                {

                    setLists(p, new int[]{1, 0, 0, 1, 1, 0});

                }

                currentLevel.Players.remove(0);

            }

            if(currentLevel.Emitters.size() > 0)
            {

                for(Emitter emit : currentLevel.Emitters.get(0))
                {

                    emit.birth = System.currentTimeMillis();
                    setLists(emit, new int[]{0, 0, 1, 0, 0, 0});

                }

                currentLevel.Emitters.remove(0);

            }

            if(currentLevel.Prompts.size() > 0)
            {

                currentLevel.Prompts.get(0).birth = levelTime;
                setLists(currentLevel.Prompts.get(0), new int[]{0, 0, 0, 0, 0, 1});

                currentLevel.Prompts.remove(0);

            }

    }

    public void levelLoadScripts(int id)
    {

        switch(id)
        {

            case 0:

            break;

            case 1:

                boss = pList.get(1);

            break;

            case 2:

            break;

            default:

            break;

        }

    }

    public void levelScripts(int id)
    {

        switch(id)
        {

            case 0:

                //level 0 scripts

                //script demo
                if(pList.size() == 1)
                {

                    player.setImage("./resources/enemyship1.png");
                    player.maxCooldown = 5;
                    player.spread = 2;


                }
                else
                {

                    player.setImage("./resources/ship.png");
                    player.spread = .1;
                    player.maxCooldown = 50;

                }

                if(fc % 200 == 0 && fc < 1000)
                {

                    explode(rand(0, 300), rand(0, SCREEN_HEIGHT), 25, 1);
                    explode(rand(SCREEN_WIDTH - 300, SCREEN_WIDTH), rand(0, SCREEN_HEIGHT), 25, 1);

                }

            break;

            case 1:

            if(pList.size() > 1)
            {

                switch(bossState)
                {

                    case 0:

                        boss.x = (SCREEN_WIDTH / 2.0) + (Math.sin(fc / 100.0)) * (SCREEN_WIDTH / 2.0);
                        boss.y += (-3 * Math.sin(fc / 77.0)) + ((player.y - boss.y) * .02);

                        if(boss.hp < boss.maxHP * .50)
                            bossState = 1;
                            

                    break;

                    case 1:

                        if(boss.alive)
                        {

                            //if(boss.x != player.x)
                            boss.x += 2 * (player.x - boss.x) / Math.abs(boss.x - player.x);
                            boss.y += (50 - boss.y) * .01;

                            if(Math.abs(boss.x - player.x) < 150)
                            {

                                Projectile left = new Projectile(boss.x - 118 + (Math.random() * 30) - 15, boss.y, 0, 25);
                                Projectile right = new Projectile(boss.x + 118 + (Math.random() * 30) - 15, boss.y, 0, 25);

                                //left.source = right.source = boss;

                                left.allegiance = 5;
                                right.allegiance = 5;

                                setLists(left, new int[]{0, 1, 0, 1, 1, 0});
                                setLists(right, new int[]{0, 1, 0, 1, 1, 0});


                            }

                        }

                    break;

                    case 2:

                    boss.x = boss.y = 200;

                    break;

                    default:

                    break;

                }

            }

                //level 1 scripts

            break;

            case 2:

                //level 2 scripts

            break;

        }

        //universal scripts

    }

    //draws graphics to display
    public void paintComponent(Graphics g)
    {

        super.paintComponent(g);

        //draws contents of drawList
        for(int i = drawList.size() - 1; i >= 0; i--)
        {

            Drawable d = drawList.get(i);

            if(d.textured())
            {

                BufferedImage image = d.getImage();

                int width = image.getWidth();
                int height = image.getHeight();

                //stole the next two lines from stack overflow, no idea how it works
                AffineTransform tx = AffineTransform.getRotateInstance(d.getRot(), width / 2.0, height / 2.0);
                AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR);


                g.drawImage(op.filter(image, null), (int) (d.getX() - width / 2.0), (int) (d.getY() - height / 2.0), null);
                

                //g.drawImage(image, (int) (d.getX() - width / 2.0), (int) (d.getY() - height / 2.0), null);

            }
            else{

                //diameter of ball to be drawn
                double sizeX = d.getSizeX();
                double sizeY = d.getSizeY();

                g.setColor(d.getColor());
                g.fillOval((int) (d.getX() - sizeX / 2.0), (int) (d.getY() - sizeY / 2.0), (int) sizeX, (int) sizeY);    

            }

        }

        //draw prompts

        for(Prompt p : promptList)
        {

            g.setFont(p.font);

            //g.drawString(p.text.substring(0, Math.min(p.text.length(), (int) (p.text.length() * ((levelTime - p.birth) / (double) p.duration)))) + (levelTime % 1000 < 500 ? "|" : ""), (int) p.x, (int) p.y);

            String cropped = p.text.substring(0, Math.min(p.text.length(), (int) (p.text.length() * ((levelTime - p.birth) / (double) p.duration))));

            int i = 0;

            for(String s : cropped.split("\n"))
            {

                g.drawString(s, (int) p.x, (int) (p.y + (g.getFontMetrics().getHeight() * i)));

                i++;

            }

        }

        //draw healthbar
        g.setColor(Color.RED);

        g.fillRect((int) (SCREEN_WIDTH / 2.0 - map(player.hp, player.maxHP, 0, 200, 0)), (int) (SCREEN_HEIGHT * .95), (int) (2 * map(player.hp, player.maxHP, 0, 200, 0)), 25);

        g.setColor(Color.BLACK);

        g.drawRect((int) (SCREEN_WIDTH / 2.0 - 200), (int) (SCREEN_HEIGHT * .95), 400, 25);

    }

    //game tick, activated by timer
    public void actionPerformed(ActionEvent e)
    {

        //updates player-controlled player's controls, really regretting the naming choices now
        if(player.rFrames < 60)
        {

            player.x += (player.control[2] + player.control[3]) * 2;
            player.y += (player.control[0] + player.control[1]) * 2;

        }

        for(Mobile m : movList)
        {

            //updates position by velocity for all mobiles
            m.cX(m.getXVel());
            m.cY(m.getYVel());

            m.setXVel(m.getXVel() * m.getDecayFactor());
            m.setYVel(m.getYVel() * m.getDecayFactor());

        }

        //updates for every player
        for(int i = 0; i < pList.size(); i++)
        {

            Player p = pList.get(i);

            //caps player velocity to PLAYER_MAX_SPEED
            double vVec = Math.sqrt(Math.pow(p.xVel, 2) + Math.pow(p.yVel, 2));

            if(vVec > PLAYER_MAX_SPEED)
            {

                p.xVel = PLAYER_MAX_SPEED * (p.xVel / vVec);
                p.yVel = PLAYER_MAX_SPEED * (p.yVel / vVec);

            }

            //player edge collision
            if(p.x + p.getSizeX() / 2.0 > SCREEN_WIDTH || p.x - p.getSizeX() / 2.0 < 0)
            {

                p.xVel *= -1;
                p.x =  (p.x + p.getSizeX() / 2.0 < (SCREEN_WIDTH - p.getSizeX() / 2.0 - p.x)) ? p.getSizeX() / 2.0 : SCREEN_WIDTH - p.getSizeX() / 2.0;

            }
            if(p.y + p.getSizeY() / 2.0 > SCREEN_HEIGHT || p.y - p.getSizeY() / 2.0 < 0)
            {

                p.yVel *= -1;
                p.y =  (p.y + p.getSizeY() / 2.0 < (SCREEN_HEIGHT - p.getSizeY() / 2.0 - p.y)) ? p.getSizeY() / 2.0 : SCREEN_HEIGHT - p.getSizeY() / 2.0;

            }

            //player on player collision
            if(p.rammingDmg > 0)
            {

                boolean hit = false;

                for(int j = 0; j < pList.size(); j++)
                {

                    Player q = pList.get(j);

                    if(/*p != q && */p.allegiance != q.allegiance)
                    {

                        //collision
                        double pHalfSizeX = p.sizeX / 2.0;
                        double qHalfSizeX = q.sizeX / 2.0;
                        double pHalfSizeY = p.sizeY / 2.0;
                        double qHalfSizeY = q.sizeY / 2.0;

                        if((p.x + pHalfSizeX) > (q.x - qHalfSizeX) && (p.x - pHalfSizeX) < (q.x + qHalfSizeX) && (p.y + pHalfSizeY) > (q.y - qHalfSizeY) && (p.y - pHalfSizeY) < (q.y + qHalfSizeY))
                        {

                            hit = true;

                            if(!p.rammingCD)
                            {

                                q.hp -= p.rammingDmg;
                                p.rammingCD = true;

                            }

                            // q.xVel += ((q.x - p.x) / Math.abs(q.x - p.x)) * (Math.sqrt(p.rammingDmg) / (Math.sqrt(q.rammingDmg)));
                            // q.yVel += ((q.y - p.y) / Math.abs(q.y - p.y)) * (Math.sqrt(p.rammingDmg) / (Math.sqrt(q.rammingDmg)));

                            q.xVel += ((q.x - p.x) / Math.sqrt(Math.pow(q.x - p.x, 2) + Math.pow(q.y - p.y, 2)) * (Math.sqrt(p.rammingDmg) / (Math.sqrt(q.rammingDmg))));
                            q.yVel += ((q.y - p.y) / Math.sqrt(Math.pow(q.x - p.x, 2) + Math.pow(q.y - p.y, 2)) * (Math.sqrt(p.rammingDmg) / (Math.sqrt(q.rammingDmg))));

                        }

                    }

                }

                if(!hit)
                    p.rammingCD = false;

            }
                

            //TODO BLeehhhh 🤮🤮🤮🤮🤮🤮🤮🤮🤮 LMAO remove this
            for(int j = 0; j < p.fireDir.length; j++)
            {

                if(p.fireDir[j] == 1 && p.cooldown == 0)
                {
                    p.cooldown = p.maxCooldown;

                    Projectile proj = new Projectile(p, 4 * Math.sin(Math.PI * j / 2.0) + rand(-1 * p.spread, p.spread), -4 * Math.cos(Math.PI * j / 2.0) + rand(-1 * p.spread, p.spread));

                    setLists(proj, new int[]{0, 1, 0, 1, 1, 0});

                    break;

                }

            }

            if(p.target != null)
            {

                //p.rot = Math.atan((p.getY() - player.getY()) / (p.getX() - player.getX())) + Math.PI / 2.0 + (p.getX() - player.getX() <= 0 ? 0 : Math.PI);

                //p.rot = Math.atan((p.getX() - player.getX()) / (p.getY() - player.getY())) + Math.PI / 2.0;

                p.setRot(p.target);

            }

            //player dies
            if(p.hp <= 0)
            {

                explode(p, 5, 2);

                p.alive = false;

                setLists(p);

            }

            //increase health by 1 to max health every 100 frames
            if(fc % 100 == 0)
                p.hp += p.hp < p.maxHP ? 1 : 0;

            p.cooldown -= p.cooldown > 0 ? 1 : 0;

            if(p.rFrames > 0)
                p.rFrames--;

            if(p.iFrames > 0)
                p.iFrames--;

        }

        //projectile specific code
        for(int i = 0; i < projList.size(); i++)
        {

            Projectile proj = projList.get(i);

            double projX = proj.getX();
            double projY = proj.getY();

            //Deletes offscreen projectiles
            if(projX > SCREEN_WIDTH || projX < 0 || projY > SCREEN_HEIGHT || projY < 0)
            {

                setLists(proj);

                i -= i == 0 ? 0 : 1;

            }

            for(int j = 0; j < pList.size(); j++)
            {

                Player p = pList.get(j);

                //player hit detection, uses bounding box instead of circular collisions because maybe faster?

                //if(proj.allegiance != p.allegiance && p.iFrames == 0 && Math.abs(p.getX() - projX) < collideDist && Math.abs(p.getY() - projY) < collideDist)
                if(proj.allegiance != p.allegiance && p.iFrames == 0 && Math.abs(p.getX() - projX) < (proj.getSize() + p.getSizeX() / 2.0) && Math.abs(p.getY() - projY) < (proj.getSize() + p.getSizeY() / 2.0))
                {

                    p.hp -= 5;

                    setLists(proj);

                    i -= i == 0 ? 0 : 1;

                }

            }
            
        }

        //Emitter specific code
        for(int i = 0; i < emitList.size(); i++)
        {

            Emitter emit = emitList.get(i);

            //kills mortal emitters past lifespan
            if(emit.mortal && System.currentTimeMillis() >= emit.birth + emit.life)
            {

                System.out.println("Killing emitter");
                setLists(emit);

            }
                

            //moves pathed emitters along paths
            if(emit.path != null)
            {

                Point temp = emit.path.getPos((System.currentTimeMillis() - emit.birth) / (double) emit.life);

                emit.setX(temp.x);
                emit.setY(temp.y);

            }
            else if(emit.anchor != null)
            {

                emit.setX(emit.anchor.getX() + emit.xOffset);
                emit.setY(emit.anchor.getY() + emit.yOffset);

            }

            //allows for different emitter behavior
            switch(emit.type)
            {

                case 0:
                    break;

                case 1:
                    if(fc % emit.props[0] == 0)
                        explode(emit, (int) emit.props[1], emit.props[2]);
                    break;

                case 2:
                    if(fc % emit.props[0] == 0)
                        explode(emit, (int) emit.props[1], emit.props[2], emit.props[3]* fc * ((fc / (int) emit.props[4])));
                        //System.out.println(((fc / (int) emit.props[4])));
                    break;
                case 3:
                    //shooter
                    if(fc % emit.props[0] == 0)
                    {

                        Projectile proj = new Projectile(emit, (emit.props[1] * 2 * Math.sin(Math.PI * emit.dir / 2.0) + rand(-1 * emit.props[2], emit.props[2])), (emit.props[1] * -2 * Math.cos(Math.PI * emit.dir / 2.0) + rand(-1 * emit.props[2], emit.props[2])));

                        //Projectile proj = new Projectile(emit, emit.props[1], emit.props[2]);

                        setLists(proj, new int[]{0, 1, 0, 1, 1, 0});

                    }
                    
                    break;

            }

        }

        //prompt specific code
        for(int i = 0; i < promptList.size(); i++)
        {

            Prompt prompt = promptList.get(i);

            if(prompt.anchor != null)
            {

                prompt.x = prompt.anchor.getX();
                prompt.y = prompt.anchor.getY();

            }

            if(levelTime > prompt.birth + prompt.life)
            {

                setLists(prompt);

            }

        }

        if(fc % 15 == 0)
        {
            //TODO shift emitter type 2 parameters to enable this behavior
            //explode(AHH.getX(), AHH.getY(), 5, 2, .01 * fc * (fc / 1000));
            //explode(BHH.getX(), BHH.getY(), 5, 2, fc / 1000.0);

            // Player p = new Player(rand(0, SCREEN_WIDTH), rand(0, SCREEN_HEIGHT), rand(-3, 3), rand(-3, 3));
            
            // p.decayFactor = 1;

            // setLists(p, new int[]{1, 0, 0, 1, 1, 0});

        }

        // if(fc % 500 == 0)
        //     System.out.println(projList.size());

        //player death, junk testing code TODO
        if(player.hp <= 0)
        {

            emitList.clear();

        }

        //System.out.println(currentLevel.Times.get(0));

        if(currentLevel.Times.size() != 0 && currentLevel.Times.get(0) <= levelTime)
        {

            //System.out.println("here" + levelTime);
            initPhase();

            currentLevel.Times.remove(0);

        }
        else if (levelIndex < maxLevel && currentLevel.Times.size() == 0 && pList.size() == 1 && pList.contains(player))
        {

            //TODO load next level

            System.out.println("Loading level " + (levelIndex + 1) + ".");
            loadLevel(levels.get(++levelIndex));

    

        }

        levelScripts(levelIndex);

        levelTime += t.getDelay();
             
        //iterates frame counter
        fc++;

        //draws new frame
        revalidate(); // no idea what this one is
        repaint();

    }

    //input
    @Override
    public void keyPressed(KeyEvent e) 
    {
        
        switch(e.getKeyCode())
        {

            case KeyEvent.VK_W:
                player.control[0] = -1;
                break;

            case KeyEvent.VK_S:
                player.control[1] = 1;
                break;

            case KeyEvent.VK_A:
                player.control[2] = -1;
                break;

            case KeyEvent.VK_D:
                player.control[3] = 1;
                break;

            case KeyEvent.VK_UP:
                player.fireDir[0] = 1;
                player.rot = 0;
                break;

            case KeyEvent.VK_DOWN:
                player.fireDir[2] = 1;
                player.rot = Math.PI;
                break;

            case KeyEvent.VK_LEFT:
                player.fireDir[3] = 1;
                player.rot = 3 * Math.PI / 2.0;
                break;

            case KeyEvent.VK_RIGHT:
                player.fireDir[1] = 1;
                player.rot = Math.PI / 2.0;
                break;

            case KeyEvent.VK_SPACE:
                if(player.rFrames == 0)
                    dodge(player);
                break;

        }

        
    }

    //input
    @Override
    public void keyReleased(KeyEvent e) 
    {
    
        switch(e.getKeyCode())
        {

            case KeyEvent.VK_W:
                player.control[0] = 0;
                break;

            case KeyEvent.VK_S:
                player.control[1] = 0;
                break;

            case KeyEvent.VK_A:
                player.control[2] = 0;
                break;

            case KeyEvent.VK_D:
                player.control[3] = 0;
                break;

            case KeyEvent.VK_UP:
                player.fireDir[0] = 0;
                break;

            case KeyEvent.VK_DOWN:
                player.fireDir[2] = 0;
                break;

            case KeyEvent.VK_LEFT:
                player.fireDir[3] = 0;
                break;

            case KeyEvent.VK_RIGHT:
                player.fireDir[1] = 0;
                break;

        }
        
    }

    //necessary override
    @Override
    public void keyTyped(KeyEvent e) 
    {
    
        
        
    }

}
