package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.audio.AudioNode;
import com.jme3.bounding.BoundingVolume;
import com.jme3.collision.CollisionResults;
import com.jme3.font.BitmapText;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.light.DirectionalLight;
import com.jme3.light.PointLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath; 
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Node;
import com.jme3.shadow.DirectionalLightShadowRenderer;
import com.jme3.shadow.PointLightShadowRenderer;
import com.jme3.system.AppSettings;
import com.jme3.ui.Picture;
import java.util.Random;


public class Main extends SimpleApplication {
   //collision sound ball and wall
    private AudioNode ballWallSound;
    //collision sound ball and balls
    private AudioNode ballballSound;
    //win sound
    private AudioNode winSound;
    //lose sound
    private AudioNode loseSound;
    //backgroud music
    private AudioNode bgm;
    //pictures
    private Picture p = new Picture("Picture");
    
    private int rotationSpeed;
   
    private BitmapText text;
    
    private int score;
   
    private int level;
    
    private Node ball1,ball2, ball3, ball4, ball5, ball6,ball7;
    private Node wall;
    private Node paddle;
    Vector3f velocity;
    //build wall on jme
    final Vector3f upVector = new Vector3f((float) -4.7, 0, (float) -5.9);
    final Vector3f downVector = new Vector3f((float) -4.7, 0, (float) 5.9);
    final Vector3f leftVector = new Vector3f((float) -4.7, 0, (float) -5.9);
    final Vector3f rightVector = new Vector3f((float) 4.7, 0, (float) -5.9);
    //four direction for the wall
    Ray up = new Ray(upVector, Vector3f.UNIT_X);
    Ray down = new Ray(downVector, Vector3f.UNIT_X);
    Ray left = new Ray(leftVector, Vector3f.UNIT_Z);
    Ray right = new Ray(rightVector, Vector3f.UNIT_Z);
    
    public static void main(String[] args) {

        Main app = new Main();//create instance
        app.showSettings = false;
        AppSettings screenSet = new AppSettings(true);
        screenSet.put("Width", 960);//set the Width of screen is 960
        screenSet.put("Height", 540);//set the height of screen is 540
        screenSet.put("Title", "Zhouzhe Wu-ball ball");//set title
        app.setSettings(screenSet);
        app.start();//start


    }

    @Override
    public void simpleInitApp() {
 
        viewPort.setBackgroundColor(ColorRGBA.White);//set background colour
        
        initCam();//set camera
        initSound();//set sounds
        initObject();//set objects
        initKeyboard();//set keyboard
        initText();//set text
        initLight();//set light
        initStart();//set first picture
       }
    
    private void initStart(){
        p.setPosition(0, 0);
        p.setWidth(settings.getWidth());
        p.setHeight(settings.getHeight());
        p.setImage(assetManager, "Interface/background.png", false);

        // attach geometry to orthoNode
        guiNode.attachChild(p);
        velocity = new Vector3f(0, 0, 0);
        ball1.setLocalTranslation(0, 0, 3);
    }
    
    
    private void initLight(){
        // Set up the lights
        DirectionalLight sun = new DirectionalLight();
        sun.setDirection(new Vector3f(1, -1, -1));
        rootNode.addLight(sun);
        //set up a point light
        PointLight myLight = new PointLight();
        myLight.setColor(ColorRGBA.White);
        myLight.setPosition(new Vector3f(0, 2, 2));
        myLight.setRadius(20);
        rootNode.addLight(myLight);
         // Casting shadows
        // The monkey can only cast shadows
        ball1.setShadowMode(RenderQueue.ShadowMode.Cast);
        // The table can both cast and receive
        wall.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);

        // setting up the renderers. Every kind of light needs a separate one
        PointLightShadowRenderer plsr = new PointLightShadowRenderer(assetManager, 512);

        plsr.setLight(myLight);
        plsr.setFlushQueues(false); // should be false for all but the last renderer

        DirectionalLightShadowRenderer dlsr = new DirectionalLightShadowRenderer(assetManager, 512, 2);
        dlsr.setLight(sun);

        // adding them to the view port (what we see)
        viewPort.addProcessor(plsr);
        viewPort.addProcessor(dlsr);
        
         /*
         *
         * set up the camera and look at the original position
         *
         */
        cam.setLocation(new Vector3f(0, 17, 6));
        cam.lookAt(Vector3f.ZERO, Vector3f.UNIT_Y);
        cam.clearViewportChanged();
    }
    
    private void initText(){
        text = new BitmapText(guiFont);
        text.setSize(guiFont.getCharSet().getRenderedSize());
        //set the position
        text.move( // x/y coordinates and z = depth layer 0
                settings.getWidth() /20,
                text.getLineHeight()+500,
                0);

        text.setSize(30f);
        text.setColor(ColorRGBA.Blue);
        guiNode.attachChild(text);
    }
    
    private void initSound(){
       
        bgm = new AudioNode(assetManager, "Sounds/background.wav", false);
        bgm.setPositional(false);
        bgm.setLooping(true);
        bgm.setVolume(0.1f);
        bgm.play();
       
        ballWallSound = new AudioNode(assetManager, "Sounds/ballwall.wav", false);
        ballWallSound.setPositional(false);
        ballWallSound.setLooping(false);
        ballWallSound.setVolume(1.8f);
       
        ballballSound = new AudioNode(assetManager, "Sounds/ballball.wav", false);
        ballballSound.setPositional(false);
        ballballSound.setLooping(false);
        ballballSound.setVolume(0.5f);
        
        winSound = new AudioNode(assetManager, "Sounds/win.wav", false);
        winSound.setPositional(false);
        winSound.setLooping(false);
        winSound.setVolume(40f);
        
        loseSound = new AudioNode(assetManager, "Sounds/lose.wav", false);
        loseSound.setPositional(false);
        loseSound.setLooping(false);
        loseSound.setVolume(40f);
    }
    
    
    private void initCam(){
        //set the camera as stabale 
        this.flyCam.setEnabled(false);
        this.setDisplayFps(false);
        this.setDisplayStatView(false);
        initSound();
    }
    
    private void initObject(){
        // load the table node
       
        wall = (Node) assetManager.loadModel("Models/wall.j3o");
        rootNode.attachChild(wall);

        /*
         *
         * set up the ball position, ball2 is mercury ball3 is venus ball4 is
         * mars ball5 is jupiter ball6 is neptune
         *
         */
        ball1 = (Node) assetManager.loadModel("Models/ball1.j3o");
        ball1.scale(0.4f);
        rootNode.attachChild(ball1);
        //load the other all node
        ball2 = (Node) assetManager.loadModel("Models/ball2.j3o");
        ball3 = (Node) assetManager.loadModel("Models/ball3.j3o");
        ball4 = (Node) assetManager.loadModel("Models/ball4.j3o");
        ball5 = (Node) assetManager.loadModel("Models/ball5.j3o");
        ball6 = (Node) assetManager.loadModel("Models/ball6.j3o");
        ball7 = (Node) assetManager.loadModel("Models/ball7.j3o");

        rootNode.attachChild(ball2);
        rootNode.attachChild(ball3);
        rootNode.attachChild(ball4);
        rootNode.attachChild(ball5);
        rootNode.attachChild(ball6);
        rootNode.attachChild(ball7);
        
          ball2.scale(0.5f);
              ball3.scale(0.5f);
              ball4.scale(0.5f);
              ball5.scale(0.5f);
              ball6.scale(0.5f);
              ball7.scale(0.5f);
       
        /*
         *
         * set up the paddle position
         *
         */

        paddle = (Node) assetManager.loadModel("Models/paddle.j3o");
        paddle.scale(1, 1, 0.7f);
        rootNode.attachChild(paddle);
    }
    
    
    
    private void initKeyboard(){
         /*
         * input event handle there are four event
         * 1. move paddle left
         * 2. move paddle right
         * 3. restart key TAB
         * 4. gravity function Key
         */
        inputManager.addMapping("Move right",
                new KeyTrigger(KeyInput.KEY_RIGHT));
        inputManager.addMapping("Move left",
                new KeyTrigger(KeyInput.KEY_LEFT));
        inputManager.addMapping("Restart",
                new KeyTrigger(KeyInput.KEY_SPACE));
        inputManager.addMapping("level2",
                new KeyTrigger(KeyInput.KEY_2));
        inputManager.addMapping("level3",
                new KeyTrigger(KeyInput.KEY_3));
        inputManager.addMapping("level1",
                new KeyTrigger(KeyInput.KEY_1));
       //  Test multiple listeners per mapping
        inputManager.addListener(analogListener, "Move right");
        inputManager.addListener(analogListener, "Move left");
        inputManager.addListener(analogListener, "Restart");
        inputManager.addListener(analogListener, "level2");
        inputManager.addListener(analogListener, "level3");
        inputManager.addListener(analogListener, "level1");
        
    }
    
    
    /**
     * AnalogListener anonymous class handles all the keyboard input SPACE key
     * for the gravity LEFT key to move the paddle left RIGHT key to move the
     * paddle right TAB key to restart the game
     */
    private AnalogListener analogListener = new AnalogListener() {
        public void onAnalog(String name, float value, float tpf) {
            if (name.equals("Move right")) {
                paddle.move(7 * tpf, 0, 0);
        } else if (name.equals("Move left")) {
                paddle.move(-7 * tpf, 0, 0);
            } else if (name.equals("Restart")) {
                
                
        
        p.setPosition(0, 0);
        p.setWidth(settings.getWidth());
        p.setHeight(settings.getHeight());
        p.setImage(assetManager, "Interface/Intro.png", false);
       } else if (name.equals("level1")) {
                //let the picture dispear
                p.setWidth(0);
                p.setHeight(0);
                //start the level 1
               
                Level1();
            }else if (name.equals("level2")||score==6) {
               //let the picture dispear
                p.setWidth(0);
                p.setHeight(0);
                //restart the level 1
              
                Level2();
            }else if (name.equals("level3")||score==12) {
               //let the picture dispear
                p.setWidth(0);
                p.setHeight(0);
                //restart the level 1
               
                Level3();
            }if(score==18){
                Win();
            }if(score==6){
                Level2();
            }if(score==12){
                Level3();
            }
            
        }
    };

    /**
     * when the ball is out of the field the stop method will be triggered
     */
    protected void Lose() {
        rotationSpeed = 0;
        velocity = new Vector3f(0, 0, 0);
        //set it below the paddle
        ball1.setLocalTranslation(0, 0, (float) (6 - 0.3 * 2.5));
        //move the paddle down make sure it wont collide with the ball 
        paddle.setLocalTranslation(0, 0, 10);
        // make it appear in front of stats view
        p.move(0, 0, 1);
        p.setPosition(0, 0);
        p.setWidth(settings.getWidth());
        p.setHeight(settings.getHeight());
        p.setImage(assetManager, "Interface/lost.png", false);
        
        loseSound.playInstance();
}

    /**
     * The first level of the game set up the ball set up the initial speed of
     * ball and the roation speed of
     *
     */
    protected void Level1() {
        p.setImage(assetManager, "Interface/lost.png", false);

        level = 1;
        score = 0;
        rotationSpeed = 2;
        //ball start at random angle 
        float num = new Random().nextFloat() * 2 - 1;
        velocity = new Vector3f(num, 0, -1);
        velocity = velocity.mult(7f);
        

        ball1.setLocalTranslation(0, 0, (float) (6 - 0.35 * 2));
        //also set up the position of  ball2 ball3 and ball4 in level1
        
        ball2.setLocalTranslation(-2f, 0, -3f);
        ball3.setLocalTranslation(2f, 0, -3f);
        ball4.setLocalTranslation(0, 0, -3f);
        ball5.setLocalTranslation(-2f, 0, 0f);
        ball6.setLocalTranslation(2f, 0, 0f);
        ball7.setLocalTranslation(0, 0, 0f);
        
        paddle.setLocalTranslation(0, 0, 6);
         if (score == 5) {

            Level2();
        }
       

    }

    /**
     * The second level of the game, increase the initial speed of the ball
     * change the postion of the other balls are place in different position
     *
     */
    protected void Level2() {
        level = 2;
        score = 6;

        rotationSpeed = 0;
        float num = new Random().nextFloat() * 2 - 1;
        velocity = new Vector3f(num, 0, -1);
        //add the speed of the earth (red ball)
        velocity = velocity.mult(7f);
        ball1.setLocalTranslation(0, 0, (float) (6 - 0.3 * 3));
        //set up new position of the balls
         ;
        
        ball2.setLocalTranslation(-3f, 0, -3f);
        ball3.setLocalTranslation(3f, 0, -3f);
        ball4.setLocalTranslation(2f, 0, -1f);
        ball5.setLocalTranslation(-2f, 0, -1f);
        ball6.setLocalTranslation(0, 0, -1f);
        ball7.setLocalTranslation(0, 0, 1);
        paddle.setLocalTranslation(0, 0, 6);

    }

    /**
     * The third level of the game, increase the initial speed of the ball
     * change the postion of the other balls are place in different position
     *
     */
    protected void Level3() {


        level = 3;
        score = 12;


        rotationSpeed = 2;
        float num = new Random().nextFloat() * 2 - 1;
        velocity = new Vector3f(num, 0, -1);
        velocity = velocity.mult(7f);
        //set up all the position of the ball
        ball1.setLocalTranslation(0, 0, (float) (6 - 0.3 * 3));
        ball2.setLocalTranslation(1f, 0, 1f);
        ball3.setLocalTranslation(-2f, 0, -1f);
        ball4.setLocalTranslation(2f, 0, -1f);
        ball5.setLocalTranslation(-1f, 0, -3f);
        ball6.setLocalTranslation(1f, 0, -3f);
        ball7.setLocalTranslation(-1f, 0, 1f);

        paddle.setLocalTranslation(0, 0, 6);

    }

    protected void Win() {
        
        p.move(0, 0, 1); // make it appear behind stats view
        p.setPosition(0, 0);
        p.setWidth(settings.getWidth());
        p.setHeight(settings.getHeight());
        p.setImage(assetManager, "Interface/win.png", false);
        winSound.playInstance();
        velocity = velocity.mult(0f);
    }

    /**
     * update loop of the game, simpleUpdate will be executed throughout the
     * whole game
     *
     */
    @Override
    public void simpleUpdate(float tpf) {
//        distance = (int) Vector3f.ZERO.distance(cam.getLocation());
//        distanceText.setText("Distance: "+distance);


       text.setText("Level " + level + " :-D \n" + "Your Score: " + (score) +" :-O\n \nPass level3 to win"+ "\n \nSPACE to Intro :)");
        ball1.rotate(0, rotationSpeed * FastMath.PI * tpf, 0);
        //initial move!
        ball1.move(velocity.mult(tpf));
        /*if paddle collision with the table
         */
        CollisionResults resultsPaddle = new CollisionResults();
        BoundingVolume bvBord = paddle.getWorldBound();
        wall.collideWith(bvBord, resultsPaddle);

        if (resultsPaddle.size() > 0) {
            //Restrict the move range of the paddle 
            if (paddle.getLocalTranslation().x < 0) {
                //board.move((float) 0.02, 0, 0);
                paddle.setLocalTranslation((float) -3.65, 0, 6);
            } else {
                paddle.setLocalTranslation((float) 3.65, 0, 6);
            }
        }
        
        ballWallCollision();
        ballPaddleCollision();
        ballBallCollision();
    
    }
    
    
    private void ballWallCollision(){//ball collision with wall
         CollisionResults resultUp = new CollisionResults();
        CollisionResults resultDown = new CollisionResults();
        CollisionResults resultLeft = new CollisionResults();
        CollisionResults resultRight = new CollisionResults();
        ball1.collideWith(up, resultUp);
        ball1.collideWith(down, resultDown);
        ball1.collideWith(left, resultLeft);
        ball1.collideWith(right, resultRight);
        if (resultUp.size() > 0) {


            velocity.setZ(FastMath.abs(velocity.getZ()));
            velocity = velocity.mult(1.005f);
            ballWallSound.playInstance();
        } else if (resultDown.size() > 0) {
            //if the ball fly out of field
            Lose();


        } else if (resultLeft.size() > 0) {

            velocity.setX(FastMath.abs(velocity.getX()));
            velocity = velocity.mult(1.005f);
            ballWallSound.playInstance();
        } else if (resultRight.size() > 0) {

            velocity.setX(-FastMath.abs(velocity.getX()));
            velocity = velocity.mult(1.005f);
            ballWallSound.playInstance();
        }
        
    }
    private void ballBallCollision(){//ball1 collision with stable balls 
       
        CollisionResults collisionBall2 = new CollisionResults();
        BoundingVolume bvBall2 = ball2.getWorldBound();
        ball1.collideWith(bvBall2, collisionBall2);

        if (collisionBall2.size() > 0) {

            ballballSound.playInstance();
            score += 1;
            //normal vector from ball to ball2
            Vector3f norm = new Vector3f((ball2.getLocalTranslation().subtract(ball1.getLocalTranslation())).normalize());
            //length of projection on norm
            float v = velocity.dot(norm);
            //vector projrction
            Vector3f projection = norm.mult(v);
            //parall vector
            Vector3f parall = velocity.subtract(projection);
            velocity = parall.subtract(projection);
            ball2.move(10, 10, 10);// move all ball here after collision 

        }
        
        CollisionResults collisionBall3 = new CollisionResults();
        BoundingVolume bvBall3 = ball3.getWorldBound();
        ball1.collideWith(bvBall3, collisionBall3);

        if (collisionBall3.size() > 0) {

            ballballSound.playInstance();
            score += 1;
            //normal vector from ball to ball2
            Vector3f norm = new Vector3f(ball3.getLocalTranslation().subtract(ball1.getLocalTranslation()).normalize());
            //length of projection on norm
            float v = velocity.dot(norm);
            //vector projrction
            Vector3f projection = norm.mult(v);
            //parall vector
            Vector3f parall = velocity.subtract(projection);
            velocity = parall.subtract(projection);
            ball3.move(10, 10, 10);// move all ball here after collision 

        }
       
        CollisionResults collisionBall4 = new CollisionResults();
        BoundingVolume bvBall4 = ball4.getWorldBound();
        ball1.collideWith(bvBall4, collisionBall4);

        if (collisionBall4.size() > 0) {

            ballballSound.playInstance();
            score += 1;
            //normal vector from ball to ball2
            Vector3f norm = new Vector3f(ball4.getLocalTranslation().subtract(ball1.getLocalTranslation()).normalize());
            //length of projection on norm
            float v = velocity.dot(norm);
            //vector projrction
            Vector3f projection = norm.mult(v);
            //parall vector
            Vector3f parall = velocity.subtract(projection);
            velocity = parall.subtract(projection);
            ball4.move(10, 10, 10);// move all ball here after collision 

        }
      
        CollisionResults collisionBall5 = new CollisionResults();
        BoundingVolume bvBall5 = ball5.getWorldBound();
        ball1.collideWith(bvBall5, collisionBall5);

        if (collisionBall5.size() > 0) {

            ballballSound.playInstance();
            score += 1;
            //normal vector from ball to ball2
            Vector3f norm = new Vector3f(ball5.getLocalTranslation().subtract(ball1.getLocalTranslation()).normalize());
            //length of projection on norm
            float v = velocity.dot(norm);
            //vector projrction
            Vector3f projection = norm.mult(v);
            //parall vector
            Vector3f parall = velocity.subtract(projection);
            velocity = parall.subtract(projection);
            ball5.move(10, 10, 10);// move all ball here after collision 

        }

        /**
         * ball collision with ball6
         *
         *
         */
        CollisionResults collisionBall6 = new CollisionResults();
        BoundingVolume bvBall6 = ball6.getWorldBound();
        ball1.collideWith(bvBall6, collisionBall6);

        if (collisionBall6.size() > 0) {

            ballballSound.playInstance();
            score += 1;
            //normal vector from ball to ball2
            Vector3f norm = new Vector3f(ball6.getLocalTranslation().subtract(ball1.getLocalTranslation()).normalize());
            //length of projection on norm
            float v = velocity.dot(norm);
            //vector projrction
            Vector3f projection = norm.mult(v);
            //parall vector
            Vector3f parall = velocity.subtract(projection);
            velocity = parall.subtract(projection);
            ball6.move(10, 10, 10);// move all ball here after collision 
         

        }
        CollisionResults collisionBall7 = new CollisionResults();
        BoundingVolume bvBall7 = ball7.getWorldBound();
        ball1.collideWith(bvBall7, collisionBall7);
        if (collisionBall7.size() > 0) {

            ballballSound.playInstance();
            score += 1;
            //normal vector from ball to ball2
            Vector3f norm = new Vector3f(ball7.getLocalTranslation().subtract(ball1.getLocalTranslation()).normalize());
            //length of projection on norm
            float projVal = velocity.dot(norm);
            //vector projrction
            Vector3f projection = norm.mult(projVal);
            //parall vector
            Vector3f parall = velocity.subtract(projection);
            velocity = parall.subtract(projection);
            ball7.move(10, 10, 10);// move all ball here after collision 
       }

    }
    
    private void ballPaddleCollision(){//ball collision with paddle
          
        CollisionResults resultBallPaddle = new CollisionResults();
        BoundingVolume bvBall = ball1.getWorldBound();
        paddle.collideWith(bvBall, resultBallPaddle);

        if (resultBallPaddle.size() > 0) {


            ballWallSound.playInstance();
            velocity.setZ(-FastMath.abs(velocity.getZ()));
            //friction on x axis
            if (paddle.getLocalTranslation().getX() < 0) {
                velocity.setX(velocity.getX() + 0.5f);

            } else if (paddle.getLocalTranslation().getX() > 0) {
                velocity.setX(velocity.getX() - 0.5f);

            }

           //change paddle's colour
            Material boxMat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
            boxMat.setBoolean("UseMaterialColors", true);

            boxMat.setColor("Diffuse", ColorRGBA.randomColor());


            paddle.setMaterial(boxMat);

        }
    }
    
  
    
}
