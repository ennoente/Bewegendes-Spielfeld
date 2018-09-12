package bewegendesspielfeld.com.test_1_bewegendesspielfeld;

import android.app.Activity;
import android.os.Handler;
import android.os.Bundle;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends Activity {
    static Game firstUp, firstDown;

    static Game[] obstaclesUp = new Game[5];
    static Game[] obstaclesDown = new Game[5];

    static Game[] obstaclesUp_2 = new Game[5];
    static Gold_Coin[] coin = new Gold_Coin[5];

    Figure figure;

    static Random random = new Random();

    public static int width;
    public static int height;

    public static int gapBetweenObstacles = 300; //200
    public static int horizontalDistanceBetweenObstacles = 500;//330;
    public static int startOfObstaclesX = 550;
    public static int endOfMovementForFigure = 300;
    public static double coinSpawnProx = 0.4;
    public static float gameSpeed = 2f;

    //public static int[] gapFinished = new int[5];
    public static int[] coinInGap = new int[5];


    int coinWidth = 65;
    int coinHeight = 65;

    Scoreboard scoreboard;

    public static int score = 0;
    public static int coinScore = 0;

    // 1=upper gap, 2=lower gap
    int whichGapIsFigureIn = 0;
    int whichObstacleJustPassedBy;

    public int acceleration = 1;
    public boolean gameHasStarted = false;
    public boolean figureHasToStop = false;

    Timer gameTimer = new Timer();
    Timer moveObstacleTimer = new Timer();
    Timer detectFailTimer = new Timer();

    GameOver gameOver;

    static Handler handler;
    static Handler handler2;


    public static FrameLayout frameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //frameLayout = new FrameLayout(this);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        frameLayout = new FrameLayout(this);

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        width = metrics.widthPixels;
        height = metrics.heightPixels;

        //gapFinished[0] = 0;
        //gapFinished[1] = 0;
        //gapFinished[2] = 0;
        //gapFinished[3] = 0;
        //gapFinished[4] = 0;

        setupStart();

        // frameLayout.addView(firstUp);
        // frameLayout.addView(firstDown);
        // frameLayout.addView(figure);
        // frameLayout.addView(b, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                gameOver = new GameOver(frameLayout.getContext());
                gameOver.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        System.out.println("YES!!! :):):)");
                    }
                });
                gameOver.restart.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        restart();
                    }
                });
            }
        };

        handler2 = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                coin[whichObstacleJustPassedBy].invalidate();
                System.out.println("handle message kommt an");
            }
        };

        frameLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN)
                {
                    if(!gameHasStarted) {
                        gameHasStarted = true;
                        setupTimers();
                    }

                    if(gameHasStarted)
                    {
                        //figure.setY(figure.getY() - 1);
                        acceleration = - 22;
                    }
                }
                return true;
            }
        });

        setContentView(frameLayout);

        System.out.println("width:" + width + "|height:" + height);
    }

    public void setupStart() {
        setupObstacles();
        setupFigure();
        setupScoreboard();

        gameTimer = new Timer();
        moveObstacleTimer = new Timer();
        detectFailTimer = new Timer();

        //setupTimers();
    }

    public void setupObstacles() {
        /*
        Reposition the obstacles when they are no longer visible
         */
        for(int i = 0; i < obstaclesUp.length; i++)
        {
            Random random = new Random();
            int stateOfNextObstacle = random.nextInt(2);
            int coinUpOrDown = random.nextInt(2);
            double coinSpawn = random.nextDouble();

            // stateOfNextObstacle == 0 -> TWO Gaps
            if (stateOfNextObstacle == 0) {
                obstaclesUp[i] = new Game(this, heightOfUpperObstacle(2));
                obstaclesUp[i].setX(startOfObstaclesX + horizontalDistanceBetweenObstacles * i);

                int obstaclesDownHeightAtI = height - obstaclesUp[i].giveHeight() - gapBetweenObstacles - heightOfUpperObstacle(2) - gapBetweenObstacles;

                obstaclesDown[i] = new Game(this, obstaclesDownHeightAtI);
                obstaclesDown[i].setX(obstaclesUp[i].getX());
                obstaclesDown[i].setY(obstaclesUp[i].giveHeight() + gapBetweenObstacles);

                int obstacleUp_2_HeightAtI = (int) (height - obstaclesDown[i].getY() - obstaclesDown[i].giveHeight() - gapBetweenObstacles);

                obstaclesUp_2[i] = new Game(this, obstacleUp_2_HeightAtI);
                obstaclesUp_2[i].setX(obstaclesUp[i].getX());
                obstaclesUp_2[i].setY(height - obstaclesUp_2[i].giveHeight());

                coin[i] = new Gold_Coin(this);
                coin[i].setSize(coinWidth, coinHeight);

                // If coin should be spawned
                if (coinSpawn <= coinSpawnProx)
                {
                    System.out.println("COIN SPAWNED!" + coinSpawn + "|" + coinSpawnProx);
                    coin[i].setX(obstaclesUp[i].getX() + (obstaclesUp[i].giveWidth() - coin[i].giveWidth()) / 2);

                    if (random.nextDouble() <= 0.5) {
                        // Place coin in upper gap
                        coin[i].setY(obstaclesUp[i].giveHeight() + (gapBetweenObstacles - coin[i].giveHeight()) / 2);
                        coinInGap[i] = 1;
                    } else {
                        // Place coin in lower gap
                        coin[i].setY(obstaclesDown[i].getY() + obstaclesDown[i].giveHeight() + (gapBetweenObstacles - coin[i].giveHeight()) / 2);
                        coinInGap[i] = 2;
                    }
                } else {
                    // No Coin shoukd be spawned.
                    coin[i].setY(-coinHeight);
                    coinInGap[i] = 0;

                    System.out.println("No Coin Should Be Spawned!" + coin[i].getY());
                }

                frameLayout.addView(obstaclesUp[i]);
                frameLayout.addView(obstaclesDown[i]);
                frameLayout.addView(obstaclesUp_2[i]);
                frameLayout.addView(coin[i]);
            }
            // stateOfNextObstacle == 1 -> Only one Gap
            else if (stateOfNextObstacle == 1)
            {
                obstaclesUp[i] = new Game(this, heightOfUpperObstacle(1));
                obstaclesUp[i].setX(startOfObstaclesX + horizontalDistanceBetweenObstacles * i);

                int obstacleDownHeightAtI = height - obstaclesUp[i].giveHeight() - gapBetweenObstacles;

                obstaclesDown[i] = new Game(this, obstacleDownHeightAtI);
                obstaclesDown[i].setX(obstaclesUp[i].getX());
                obstaclesDown[i].setY(height - obstaclesDown[i].giveHeight());

                obstaclesUp_2[i] = new Game(this, 0);

                coin[i] = new Gold_Coin(this);
                coin[i].setSize(coinWidth, coinHeight);

                // If coin should be spawned
                if (coinSpawn <= coinSpawnProx)
                {
                    System.out.println("COIN SPAWNED!" + coinSpawn + "|" + coinSpawnProx);
                    coin[i].setY(obstaclesUp[i].giveHeight() + (gapBetweenObstacles - coin[i].giveHeight()) / 2);
                    //coin[i].setSize(coinWidth, coinHeight);
                    coinInGap[i] = 1;
                } else {
                    coin[i].setY(-coinHeight);
                    coinInGap[i] = 0;
                }

                coin[i].setX(obstaclesUp[i].getX() + (obstaclesUp[i].giveWidth() - coin[i].giveWidth()) / 2);


                //coin[i].setSize(coinWidth, coinHeight);
                //coin[i].setX(obstaclesUp[i].getX() + (obstaclesUp[i].giveWidth() - coin[i].giveWidth()) / 2);
                //coin[i].setY(obstaclesUp[i].giveHeight() + (gapBetweenObstacles - coin[i].giveHeight()) / 2);

                //coin[i] = new Gold_Coin(this);
                //coin[i].setX(obstaclesUp[i].getX());
                //coin[i].setY(obstaclesUp[i].giveHeight());

                // If coin should be spawned
                //if(coinSpawn <= coinSpawnProx)
                //{
                    //coin[i] = new Gold_Coin(this);
                    //coin[i].setSize(coinWidth, coinHeight);
                    //coin[i].setX(obstaclesUp[i].getX() + (obstaclesUp[i].giveWidth() - coin[i].giveWidth()) / 2);
                    //coin[i].setY(obstaclesUp[i].giveHeight() + (gapBetweenObstacles - coin[i].giveWidth()) / 2);

                //    frameLayout.addView(coin[i]);
                //}
                //else
                //{
                //    coin[i] = new Gold_Coin(this);
                //    frameLayout.addView(coin[i]);
                //}

                frameLayout.addView(obstaclesUp[i]);
                frameLayout.addView(obstaclesDown[i]);
                frameLayout.addView(obstaclesUp_2[i]);
                frameLayout.addView(coin[i]);
            }
        }
    }

    public void setupFigure() {
        figure = new Figure(this);
        figure.setX(150);
        figure.setY(height / 1.3f);

        frameLayout.addView(figure);
    }

    public void setupScoreboard() {
        //int scoreboardWidth = (int) figure.getX() + figure.giveWidth();
        System.out.println("figureGetX" + figure.getX());
        //int scoreboardHeight = (int) (height - figure.getY() + figure.giveHeight());
        //int scoreboardHeight = (int) figure.getY() + figure.giveHeight();
        System.out.println("figureGetY" + figure.getY());

        //scoreboard = new Scoreboard(this, scoreboardWidth, scoreboardHeight);
        //scoreboard.setY((int) figure.getY() + figure.giveHeight());

        scoreboard = new Scoreboard(this, width, 75);
        scoreboard.setY(height - scoreboard.giveHeight());

        frameLayout.addView(scoreboard);
    }

    private void setupTimers() {
        /*
        GameTimer is the main timer moving the figure and all obstacles
         */
        gameTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if(gameHasStarted) {
                    //score ++;
                    scoreboard.postInvalidate();

                    acceleration += 2;
                    figure.setY(figure.getY() + acceleration);

                    if(figure.getX() >= endOfMovementForFigure)
                        figureHasToStop = true;

                    if(!figureHasToStop)
                    {
                        figure.setX(figure.getX() + 1);
                        for(int i = 0; i < obstaclesUp.length; i++)
                        {
                            coin[i].setX(coin[i].getX() - 4.5f * gameSpeed);

                            obstaclesUp[i].setX(obstaclesUp[i].getX() - 4.5f * gameSpeed);
                            obstaclesDown[i].setX(obstaclesDown[i].getX() - 4.5f * gameSpeed);
                            obstaclesUp_2[i].setX(obstaclesUp_2[i].getX() - 4.5f * gameSpeed);

                            //runOnUiThread(new Runnable() {
                               // @Override
                                //public void run() {
                              //  }
                            //});

                            System.out.println("FigurX|ObstX" + figure.getX() + "|" + obstaclesUp[0].getX());

                            if ((figure.getX() - 5.5f) > obstaclesUp[i].getX() && figure.getX() - 11 <= obstaclesUp[i].getX()) {
                                whichObstacleJustPassedBy = i;
                                pointScored();
                            }
                        }
                    }
                    if(figureHasToStop)
                    {
                        for(int i = 0; i < obstaclesUp.length; i++)
                        {
                            coin[i].setX(coin[i].getX() - 5.5f * gameSpeed);

                            obstaclesUp[i].setX(obstaclesUp[i].getX() - 5.5f * gameSpeed);
                            obstaclesDown[i].setX(obstaclesDown[i].getX() - 5.5f * gameSpeed);
                            obstaclesUp_2[i].setX(obstaclesUp_2[i].getX() - 5.5f * gameSpeed);

                            if ((figure.getX() - 5.5f) > obstaclesUp[i].getX() && figure.getX() - 11 <= obstaclesUp[i].getX()) {
                                whichObstacleJustPassedBy = i;
                                pointScored();
                            }

                            System.out.println("figurX-obstX:" + (figure.getX() - obstaclesUp[0].getX()));
                        }
                    }
                }
            }
        }, 0, 28);

        /*
        This Timer detects if obstacles are no longer visible and moves them
        to the end of the game screen, so the game can be infinitely long.
         */
        moveObstacleTimer.schedule(new TimerTask() {
            public void run() {
                detectInvisibleObstacles();
            }
        }, 0, 100);

        detectFailTimer.schedule(new TimerTask() {
            public void run() {
                /*
                First IF-Statement detects if the figure has fallen too low
                "too low" meaning end of the screen
                 */
                if(figure.getY() >= height)
                    gameOver();

                /*
                This for()-loop checks for FAIL OR SUCCES while
                flying through the obstacles and (hopefully) gaps
                 */
                for (int i = 0; i < obstaclesUp.length; i++)
                {
                    if ((figure.getX() + figure.giveWidth()) >= obstaclesUp[i].getX() && figure.getX() <= (obstaclesUp[i].getX() + obstaclesUp[i].giveWidth()))
                    {
                        //System.out.println("FLIEGT GRAD DURCH EIN OBSTACLE. FIGURX:" + figure.getX() + "|OBSTX:" + obstaclesUp[i].getX());
                        // IS FLYING THROUGH OBSTACLE RIGHT NOW
                        // TESTING NOW IF FLYING THROUGH GAP ITSELF

                        // Upper gap
                        if (figure.getY() < obstaclesUp[i].giveHeight() || (figure.getY() + figure.giveHeight()) > obstaclesDown[i].getY())
                        {
                            // Figure isnt in first gap, now check if its in second gap

                            /* LOWER GAP
                             Only works if there IS a second gap at all
                             We check for the height of the bottom-most obstacle pole's height.
                             If its height is bigger than 0, thus relevant, proceed.
                             The second condition makes sure to only check for SUCCESS OR FAIL if the user
                             didnt choose the other gap
                             */
                            //System.out.println("schonmal nicht in gap 1");
                            if(obstaclesUp_2[i].giveHeight() > 0) {
                                if (figure.getY() < (obstaclesDown[i].getY() + obstaclesDown[i].giveHeight())
                                        || (figure.getY() + figure.giveHeight()) > obstaclesUp_2[i].getY()) {
                                    System.out.println("FAILED GAP 2");
                                    gameOver();
                                } else {
                                    // SUCCESSFULLY IN GAP 2 / LOWER GAP
                                    whichGapIsFigureIn = 2;
                                }
                                //else {
                                //    if (figure.getX() - 5.5f == obstaclesUp[i].getX()) {
                                //        score++;
                                //        System.out.println("ERFOLGREICH DURCH GAP 2");
                                //    }
                                //}
                            } else {
                                System.out.println("Failed GAP 1|" + obstaclesUp_2[i].giveHeight());
                                gameOver();
                            }
                        } else {
                            // SUCCESSFULLY IN GAP 1 / UPPER GAP
                            whichGapIsFigureIn = 1;
                        }
                        //else {
                          //  if (figure.getX() - 5.5f == obstaclesUp[i].getX())
                          //  {
                          //      System.out.println("ERFOLGREICH DURCH 1. GAP");
                          //      score++;
                        //    }
                            //gapFinished[i] = 1;
                       // }
                    }
                            //&& figure.getY() > obstaclesUp[i].giveHeight()
                            //&& (figure.getY() + figure.giveHeight() > obstaclesDown[i].getY())
                            //)

                    //if (figure.getX() >= obstaclesUp[i].getX() && figure.getX() <= obstaclesUp[i].getX() + 5)
                    //    pointScored();
                }
            }
        }, 0, 10);
    }

    public void detectInvisibleObstacles() {
        for(int i = 0; i < obstaclesUp.length; i++)
        {
            if(obstaclesUp[i].getX() < (0 - obstaclesUp[i].giveWidth()))
            {
                Random random = new Random();
                int stateOfNextObstacle = random.nextInt(2);
                double coinProx = random.nextDouble();

                // statsOfNextObstacle == 0 -> TWO gaps
                if (stateOfNextObstacle == 0) {
                    obstaclesUp[i].setHeight(heightOfUpperObstacle(2));

                    int obstaclesDownHeightAtI = height - obstaclesUp[i].giveHeight() - gapBetweenObstacles - heightOfUpperObstacle(2) - gapBetweenObstacles;

                    obstaclesDown[i].setHeight(obstaclesDownHeightAtI);
                    obstaclesDown[i].setY(obstaclesUp[i].giveHeight() + gapBetweenObstacles);

                    int obstacleUp_2_HeightAtI = (int) (height - obstaclesDown[i].getY() - obstaclesDown[i].giveHeight() - gapBetweenObstacles);

                    // Second obstacle gap
                    //obstaclesUp_2[i] = new Game(this, obstacleUp_2_HeightAtI);
                    obstaclesUp_2[i].setHeight(obstacleUp_2_HeightAtI);
                    obstaclesUp_2[i].setY(height - obstaclesUp_2[i].giveHeight());

                    if (coinProx <= coinSpawnProx)
                    {
                        //coin[i].setSize(coinWidth, coinHeight);

                        // Place coin in upper gap
                        if(random.nextDouble() <= 0.5) {
                            coin[i].setY(obstaclesUp[i].giveHeight() + (gapBetweenObstacles - coin[i].giveHeight()) / 2);
                            coinInGap[i] = 1;
                        } else {
                            // Place coin in lower gap
                            coin[i].setY(obstaclesDown[i].getY() + obstaclesDown[i].giveHeight() + (gapBetweenObstacles - coin[i].giveHeight()) / 2);
                            coinInGap[i] = 2;
                        }
                    } else {
                        coinInGap[i] = 0;
                        coin[i].setY(-coinHeight);
                    }

                    //frameLayout.addView(obstaclesUp_2[i]);
                }

                // stateOfNextObstacle == 1 -> ONE gap
                if (stateOfNextObstacle == 1)
                {
                    obstaclesUp[i].setHeight(heightOfUpperObstacle(1));

                    int obstacleDownHeightAtI = height - obstaclesUp[i].giveHeight() - gapBetweenObstacles;
                    obstaclesDown[i].setHeight(obstacleDownHeightAtI);
                    obstaclesDown[i].setY(height - obstaclesDown[i].giveHeight());

                    obstaclesUp_2[i].setHeight(0);

                    if (coinProx <= coinSpawnProx) {
                        coin[i].setSize(coinWidth, coinHeight);

                        // Place coin in gap
                        coin[i].setY(obstaclesUp[i].giveHeight() + (gapBetweenObstacles - coin[i].giveHeight()) / 2);
                        coinInGap[i] = 1;
                    } else {
                        coinInGap[i] = 0;
                        coin[i].setY(-coinHeight);
                    }
                }

                //int obstaclesDownHeightAtI = height - obstaclesUp[i].giveHeight() - gapBetweenObstacles;
                //obstaclesDown[i].setHeight(obstaclesDownHeightAtI);
                //obstaclesDown[i].setY(height - obstaclesDown[i].giveHeight());

                int k;
                if(i == 0)
                    k = obstaclesUp.length - 1;
                else
                    k = i - 1;

                obstaclesUp[i].setX(obstaclesUp[k].getX() + horizontalDistanceBetweenObstacles);
                obstaclesDown[i].setX(obstaclesUp[i].getX());
                obstaclesUp_2[i].setX(obstaclesUp[i].getX());
                coin[i].setX(obstaclesUp[i].getX() + (obstaclesUp[i].giveWidth() - coin[i].giveWidth()) / 2);

                obstaclesUp[i].postInvalidate();
                obstaclesDown[i].postInvalidate();
                obstaclesUp_2[i].postInvalidate();
                coin[i].postInvalidate();
            }
        }
    }

    public void pointScored() {
        // POINT SCORED
        score++;
        System.out.println("PUNKT!!!");

        if (whichGapIsFigureIn == coinInGap[whichObstacleJustPassedBy])
        {
            System.out.println("!!!GOLD COIN COLLECTED!!!");
            //score += 3;
            coinScore ++;

            coin[whichObstacleJustPassedBy].setY(-coinHeight);

            //coin[whichObstacleJustPassedBy].setY(- coin[whichObstacleJustPassedBy].giveHeight());
            //coin[whichObstacleJustPassedBy].setLayoutParams(coin[whichObstacleJustPassedBy].getLayoutParams());

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    coin[whichObstacleJustPassedBy].requestLayout();
                    coin[whichObstacleJustPassedBy].invalidate();
                }
            });

            //handler2.sendEmptyMessage(0);
            //coin[whichObstacleJustPassedBy].postInvalidate();
            System.out.println("coinSetSIze:" + coin[whichObstacleJustPassedBy].giveHeight() + "|" + coin[whichObstacleJustPassedBy].giveWidth());
        }
    }

    public void gameOver() {
        onStop();
        handler.sendEmptyMessage(0);
    }

    public void restart() {
        frameLayout.removeAllViews();

        score = 0;
        coinScore = 0;
        setupStart();

        gameHasStarted = false;
        figureHasToStop = false;
    }

    @Override
    protected void onStop() {
        super.onStop();

        gameTimer.cancel();
        moveObstacleTimer.cancel();
        detectFailTimer.cancel();

        System.out.println("!!! ON STOP !!!");
    }

    @Override
    protected void onResume() {
        super.onResume();

        System.out.println("!!! ON RESUME !!!");
    }

    public static int heightOfUpperObstacle(int numberOfGaps) {
        // status == 2 means TWO gaps
        int i = 0;
        if (numberOfGaps == 2)
        {
            i = random.nextInt((height / 2) - gapBetweenObstacles - 150);
        }
        // status == 1 means ONE gap
        if (numberOfGaps == 1)
        {
            i = random.nextInt(height - gapBetweenObstacles - 150);
        }

        i += 50;
        return i;
    }
}
