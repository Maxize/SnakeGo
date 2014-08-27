package com.amnon.snakego.snake.scene;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.util.Log;
import android.view.KeyEvent;
import com.amnon.snakego.snake.data.GameDefaultData;
import com.amnon.snakego.snake.data.GameState;
import com.amnon.snakego.snake.entity.TileEntity;
import com.amnon.snakego.snake.res.GameString;
import com.amnon.snakego.snake.res.Res;
import com.amnon.snakego.snake.util.ConstantUtil;
import com.amnon.snakego.snake.util.Coordinate;
import com.orange.content.SceneBundle;
import com.orange.engine.handler.timer.ITimerCallback;
import com.orange.engine.handler.timer.TimerHandler;
import com.orange.entity.scene.Scene;
import com.orange.entity.text.Text;
import com.orange.input.touch.TouchEvent;
import com.orange.res.FontRes;
import com.orange.util.HorizontalAlign;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by amnonma on 2014/8/16.
 */
public class GameScene extends Scene {
    public static final String TAG = "GameScene";

    private String mScoreNumStr = "0";

    private Text mScoreNumTx, mGameLoseTx, mGameScoreTx, mGameBeginTx;

    private boolean mGrabbed = false;

    private float mStartX,mStartY,offsetX,offsetY;
    private int mMode = GameState.READY;

    /**
     * Current direction the snake is headed.
     */
    private int mDirection = NORTH;
    private int mNextDirection = NORTH;
    private static final int NORTH = 1;
    private static final int SOUTH = 2;
    private static final int EAST = 3;
    private static final int WEST = 4;

    /**
     * Labels for the drawables that will be loaded into the TileView class
     */
    private static final int RED_STAR = 1;
    private static final int YELLOW_STAR = 2;
    private static final int GREEN_STAR = 3;

    /**
     * mSnakeTrail: a list of Coordinates that make up the snake's body
     * mAppleList: the secret location of the juicy apples the snake craves.
     */
    private ArrayList<Coordinate> mSnakeTrail = new ArrayList<Coordinate>();
    private ArrayList<Coordinate> mAppleList = new ArrayList<Coordinate>();

    /**
     * Everyone needs a little randomness in their life
     */
    private static final Random RNG = new Random();

    /**
     * mScore: used to track the number of apples captured mMoveDelay: number of
     * milliseconds between snake movements. This will decrease as apples are
     * captured.
     */
    private long mScore = 0;
    private long mMoveDelay = GameDefaultData.MOVE_DELAY_TIME;
    /**
     * mLastMove: tracks the absolute time when the snake last moved, and is used
     * to determine if a move should be made based on mMoveDelay.
     */
    private long mLastMove;

    /**
     * A hash that maps integer handles specified by the subclasser to the
     * drawable that will be used for that reference
     */
    private String[] mTileArray;

    /**
     *   存储整个布局的数组
     */
    private TileEntity[][] mTileEntityArray;

    protected static int mTileSize;
    protected static int mXTileCount;
    protected static int mYTileCount;
    private static int mXOffset;
    private static int mYOffset;
    private TimerHandler mTimerHandler;

    @Override
    public void onSceneCreate(SceneBundle bundle) {
        super.onSceneCreate(bundle);
        initView();
        initTimerHander();
    }

    private void initView() {
        mGameLoseTx = new Text(120,300,
                FontRes.getFont(ConstantUtil.YOU_LOSE_STR), GameString.YOU_LOSE_STR, 4, getVertexBufferObjectManager());
        this.attachChild(mGameLoseTx);
        mGameLoseTx.setVisible(false);
        mGameScoreTx = new Text(120,300,
                FontRes.getFont(ConstantUtil.SCORE_STR), GameString.SCORE_STR, 4, getVertexBufferObjectManager());
        this.attachChild(mGameScoreTx);
        mGameBeginTx = new Text(20,300,
                FontRes.getFont(ConstantUtil.GAME_BEGIN_STR), GameString.GAME_BEGIN_STR, 4, getVertexBufferObjectManager());
        this.attachChild(mGameBeginTx);
        // 最佳得分文本
        mScoreNumTx = new Text(360, 800,
                FontRes.getFont(ConstantUtil.FONT_SCORE_NUM), mScoreNumStr, 4,
                getVertexBufferObjectManager());
        mScoreNumTx.setHorizontalAlign(HorizontalAlign.RIGHT);
        // 设置 mScoreNumTx 的X坐标上的中点在字符的中间位置
//        mScoreNumTx.setCentrePositionX(60);
        this.attachChild(mScoreNumTx);
        // 这里初始化 背景图层《小蛇移动的舞台》
        // 假设在 480* 760的舞台上
        int w = 480;
        int h = 760;
        mTileSize = 12;  // 自己设置的每个瓦片的尺寸
        mXTileCount = (int) Math.floor(w / mTileSize);
        mYTileCount = (int) Math.floor(h / mTileSize);
        // 偏差值
        mXOffset = ((w - (mTileSize * mXTileCount)) / 2);
        mYOffset = ((h - (mTileSize * mYTileCount)) / 2);
        mTileEntityArray = new TileEntity[mXTileCount][mYTileCount];
        // 把整个场景裁剪成 mXTileCount * mYTileCount 的方块
        // 默认每个方块都没有填充东西
        // 后续依靠填充 东西来让他有东西
        clearTiles();  // 初始化瓦片
        // 更新瓦片
        resetTiles(4);
        loadTile(GREEN_STAR,Res.GREEN_STAR);
        loadTile(RED_STAR,Res.RED_STAR);
        loadTile(YELLOW_STAR,Res.YELLOW_STAR);
        // 初始化游戏
        setMode(GameState.READY);

    }

    private void initNewGame() {
        mSnakeTrail.clear();
        mAppleList.clear();

        // For now we're just going to load up a short default eastbound snake
        // that's just turned north

        mSnakeTrail.add(new Coordinate(7, 7));
        mSnakeTrail.add(new Coordinate(6, 7));
        mSnakeTrail.add(new Coordinate(5, 7));
        mSnakeTrail.add(new Coordinate(4, 7));
        mSnakeTrail.add(new Coordinate(3, 7));
        mSnakeTrail.add(new Coordinate(2, 7));
        mNextDirection = NORTH;

        // Two apples to start with
        addRandomApple();
        addRandomApple();

        mMoveDelay = GameDefaultData.MOVE_DELAY_TIME;
        mScore = 0;
        this.registerUpdateHandler(mTimerHandler);
    }

    private void initTimerHander() {
        mTimerHandler = new TimerHandler((float)(mMoveDelay/1000.0), true,
                new ITimerCallback() {

                    @Override
                    public void onTimePassed(TimerHandler pTimerHandler) {
                        // 定时刷新界面
                        update();
                    }
                });
    }

    /**
     * 清除当前分数
     */
    public void clearScore() {
        mScore = 0;
        updateCurrScore(mScore);
    }

    // 更新当前分数
    private void updateCurrScore(long scoreNum) {
        mScoreNumTx.setText(scoreNum+"");
    }

    @Override
    public boolean onSceneTouchEvent(TouchEvent pSceneTouchEvent) {
        if (pSceneTouchEvent.isActionDown()) {
            mGrabbed = true;
            mStartX = pSceneTouchEvent.getX();
            mStartY = pSceneTouchEvent.getY();

        } else if (pSceneTouchEvent.isActionUp()) {
            if (mGrabbed) {
                mGrabbed = false;
                offsetX = pSceneTouchEvent.getX() - mStartX;
                offsetY = pSceneTouchEvent.getY() - mStartY;

                if (Math.abs(offsetX) > Math.abs(offsetY)) {
                    if (offsetX < -GameDefaultData.FLING_MIN_DISTANCE) {
                        // 向左滑
                        if (mDirection != EAST) {
                            mNextDirection = WEST;
                        }
                        return true;
                    } else if (offsetX > GameDefaultData.FLING_MIN_DISTANCE) {
                        // 向右滑
                        if (mDirection != WEST) {
                            mNextDirection = EAST;
                        }
                        return true;
                    }
                } else {
                    if (offsetY < -GameDefaultData.FLING_MIN_DISTANCE) {
                        // 向上滑
                        if (mMode == GameState.READY | mMode == GameState.LOSE) {
                        /*
                         * At the beginning of the game, or the end of a previous one,
                         * we should start a new game.
                         */
                            initNewGame();
                            setMode(GameState.RUNNING);
                            update();
                            return true;
                        }

                        if (mMode ==  GameState.PAUSE) {
                         /*
                         * If the game is merely paused, we should just continue where
                         * we left off.
                         */
                            setMode( GameState.RUNNING);
                            update();
                            return (true);
                        }

                        if (mDirection != SOUTH) {
                            mNextDirection = NORTH;
                        }
                        return true;
                    } else if (offsetY > GameDefaultData.FLING_MIN_DISTANCE) {
                        // 向下滑
                        if (mDirection != NORTH) {
                            mNextDirection = SOUTH;
                        }
                        return true;
                    }
                }
            }

        }
        return true;
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            showDialog();
            return true;
        }
        return false;
    }

    /**
     * Handles the basic update loop, checking to see if we are in the running
     * state, determining if a move should be made, updating the snake's location.
     */
    public void update() {
        if (mMode == GameState.RUNNING) {
            long now = System.currentTimeMillis();
            if (now - mLastMove > mMoveDelay) {  // 这里设置的值用于刷新屏幕的频率
                clearTiles();
                updateWalls();
                updateSnake();
                updateApples();
                mLastMove = now;
            }
//            mRedrawHandler.sleep(mMoveDelay);
        }

    }

    /**
     *  更新舞台背景
     */
    private void updateWalls() {
        for (int x = 0; x < mXTileCount; x++) {
            setTile(GREEN_STAR, x, 0);
            setTile(GREEN_STAR, x, mYTileCount - 1);
        }
        for (int y = 1; y < mYTileCount - 1; y++) {
            setTile(GREEN_STAR, 0, y);
            setTile(GREEN_STAR, mXTileCount - 1, y);
        }
    }

    /**
     *  更新小苹果
     */
    private void updateApples() {
        for (Coordinate c : mAppleList) {
            setTile(YELLOW_STAR, c.getX(), c.getY());
        }

    }

    /**
     *  更新小蛇
     */
    private void updateSnake() {
        boolean growSnake = false;

        // grab the snake by the head
        Coordinate head = mSnakeTrail.get(0);
        Coordinate newHead = new Coordinate(1, 1);

        // 你在哪个方向上逃窜呢
        mDirection = mNextDirection;
        switch (mDirection){
            case EAST: {
                newHead = new Coordinate(head.getX() + 1, head.getY());
                break;
            }
            case WEST: {
                newHead = new Coordinate(head.getX() - 1, head.getY());
                break;
            }
            case NORTH: {
                newHead = new Coordinate(head.getX(), head.getY() - 1);
                break;
            }
            case SOUTH: {
                newHead = new Coordinate(head.getX(), head.getY() + 1);
                break;
            }
        }
        // 是不是跑到墙壁那里去了
        // Collision detection
        // For now we have a 1-square wall around the entire arena
        if ((newHead.getX() < 1) || (newHead.getY() < 1) || (newHead.getX() > mXTileCount - 2)
                || (newHead.getY() > mYTileCount - 2)) {
            setMode(GameState.LOSE);
            return;
        }
        // 检验是不是吃到自己了
        // Look for collisions with itself
        int snakelength = mSnakeTrail.size();
        for (int snakeindex = 0; snakeindex < snakelength; snakeindex++) {
            Coordinate c = mSnakeTrail.get(snakeindex);
            if (c.equals(newHead)) {
                setMode(GameState.LOSE);
                return;
            }
        }
        // 吃苹果
        // Look for apples
        int applecount = mAppleList.size();
        for (int appleindex = 0; appleindex < applecount; appleindex++) {
            Coordinate c = mAppleList.get(appleindex);
            if (c.equals(newHead)) {
                mAppleList.remove(c);
                addRandomApple();
                // 更新当前分数
                updateCurrScore(++mScore);
                mMoveDelay *= 0.9;

                growSnake = true;
            }
        }
        // 更新当前的新头
        // push a new head onto the ArrayList and pull off the tail
        mSnakeTrail.add(0, newHead);
        // except if we want the snake to grow
        // 没有长大的话，证明吃不到苹果，尾部的小苹果要去掉哦。
        if (!growSnake) {
            Coordinate uselessSnake = mSnakeTrail.remove(mSnakeTrail.size() - 1);
            // 释放小蛇并清除当前点的视图表现
            setTile(0, uselessSnake.getX(),uselessSnake.getY());
        }
        // 遍历小蛇的坐标，然后再屏幕上打洞，表示小蛇
        int index = 0;
        for (Coordinate c : mSnakeTrail) {
            if (index == 0) {  // 头是黄色的，好淫荡
                setTile(YELLOW_STAR, c.getX(), c.getY());
            } else {
                setTile(RED_STAR, c.getX(), c.getY());
            }
            index++;
        }

    }

    // 清楚每个tiles，设置为零，表示这个点不填充任何东西
    public void clearTiles() {
        for (int x = 0; x < mXTileCount; x++) {
            for (int y = 0; y < mYTileCount; y++) {
                setTile(0, x, y);
            }
        }
    }

    /**
     *  初始化方块种类
     * @param tilecount
     */
    public void resetTiles(int tilecount) {
        mTileArray = new String[tilecount];
    }

    /**
     *  加载 瓦片
     * @param key
     * @param tileName
     */
    public void loadTile(int key, String tileName) {
        mTileArray[key] = tileName;
    }

    // 在 方块上面填充东西《索引值表示图片》
    // 后续根据索引值把对应的方块放上去。
    public void setTile(int tileIndex, int x, int y) {
        if (tileIndex == 0 ) {
            if (mTileEntityArray[x][y] != null) {
                mTileEntityArray[x][y].clearTileImg();
            }
            return;
        }
        if (mTileEntityArray[x][y] == null) {
            mTileEntityArray[x][y] = new TileEntity(x,y,this);
            mTileEntityArray[x][y].setTileImg(mTileArray[tileIndex],mTileSize, mTileSize);
            mTileEntityArray[x][y].setPosition(mXOffset + x * mTileSize, mYOffset + y * mTileSize);
            this.attachChild(mTileEntityArray[x][y]);
        }else {
            mTileEntityArray[x][y].setTileImg(mTileArray[tileIndex],mTileSize, mTileSize);
        }
    }

    /**
     * Updates the current mode of the application (RUNNING or PAUSED or the like)
     * as well as sets the visibility of textview for notification
     *
     * @param newMode
     */
    public void setMode(int newMode) {
        int oldMode = mMode;
        mMode = newMode;

        if (newMode == GameState.RUNNING & oldMode != GameState.RUNNING) {
            update();
            return;
        }

        CharSequence str = "";
        if (newMode == GameState.PAUSE) {
            Log.d(TAG, "the game result is PAUSE");
        }
        if (newMode == GameState.READY) {
            Log.d(TAG, "the game result is READY");
        }
        if (newMode == GameState.LOSE) {
            Log.d(TAG, "the game result is LOSE");
            gameOver();
        }

    }

    private void gameOver() {
        unregisterUpdateHandler(mTimerHandler);
    }

    /**
     * Selects a random location within the garden that is not currently covered
     * by the snake. Currently _could_ go into an infinite loop if the snake
     * currently fills the garden, but we'll leave discovery of this prize to a
     * truly excellent snake-player.
     *
     */
    private void addRandomApple() {
        Coordinate newCoord = null;
        boolean found = false;
        while (!found) {
            // Choose a new location for our apple
            int newX = 1 + RNG.nextInt(mXTileCount - 2);
            int newY = 1 + RNG.nextInt(mYTileCount - 2);
            newCoord = new Coordinate(newX, newY);

            // Make sure it's not already under the snake
            boolean collision = false;
            int snakelength = mSnakeTrail.size();
            for (int index = 0; index < snakelength; index++) {
                if (mSnakeTrail.get(index).equals(newCoord)) {
                    collision = true;
                }
            }
            // if we're here and there's been no collision, then we have
            // a good location for an apple. Otherwise, we'll circle back
            // and try again
            found = !collision;
        }
        if (newCoord == null) {
            Log.e(TAG, "Somehow ended up with a null newCoord!");
        }
        mAppleList.add(newCoord);
    }

    /**
     * 退出游戏确认对话框
     */
    private void showDialog() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new AlertDialog.Builder(getActivity())
                        .setTitle(GameString.EXIT_GAME_STR)
                        .setMessage(GameString.EXIT_GAME_MSG)
                        .setPositiveButton(GameString.OK_STR,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        getActivity().finish();

                                    }
                                }).setNegativeButton(GameString.CANCEL_STR, null).show();
            }
        });

    }

}
