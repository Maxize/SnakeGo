package com.amnon.snakego.snake.entity;

import com.amnon.snakego.snake.res.GameString;
import com.amnon.snakego.snake.util.ConstantUtil;
import com.orange.entity.layer.Layer;
import com.orange.entity.primitive.Rectangle;
import com.orange.entity.scene.Scene;
import com.orange.entity.text.Text;
import com.orange.res.FontRes;

/**
 * Created by amnonma on 2014/8/29.
 */
public class GameResultLayer extends Layer {

    private Rectangle mTranslucentBg;
    private Text mGameResultTx;

    private float mResultTxWidth, mResultTxHeight;

    public GameResultLayer(Scene pScene) {
        super(pScene);
        mTranslucentBg = new Rectangle(0,0,pScene.getWidth(),pScene.getHeight(),getVertexBufferObjectManager());
        mTranslucentBg.setColor(0,0,0,0.4f);
        this.attachChild(mTranslucentBg);
        initView();
    }

    private void initView() {
        mGameResultTx = new Text(0,0,
                FontRes.getFont(ConstantUtil.GAME_RESULT_STR), GameString.GAME_BEGIN_STR, 50, getVertexBufferObjectManager());
        this.attachChild(mGameResultTx);
    }

    public void setGameResult(String result) {
        if (result == null && result.length() <= 0){
            return;
        }
        if (mGameResultTx != null) {
            mGameResultTx.setText(result);
            // measure the tx width and height
            setmResultTxWidth(mGameResultTx.getWidth());
            setmResultTxHeight(mGameResultTx.getHeight());
            // set the resultTx position center
            mGameResultTx.setPosition(((ConstantUtil.SCREEN_WIDTH - getmResultTxWidth()) / 2), ((ConstantUtil.SCREEN_HEIGHT - getmResultTxHeight()) /2));
        }
    }

    /**
     *   动画功能后续实现
     *   todo 想让文字在屏幕左右上下运动
     * @param isTrue
     */
    public void setTxAnim (Boolean isTrue) {
        if (isTrue) {
            if (mGameResultTx != null) {
                // 启动动画
//                mGameResultTx.
            }
        }
    }

    public float getmResultTxWidth() {
        return mResultTxWidth;
    }

    public void setmResultTxWidth(float mResultTxWidth) {
        this.mResultTxWidth = mResultTxWidth;
    }

    public float getmResultTxHeight() {
        return mResultTxHeight;
    }

    public void setmResultTxHeight(float mResultTxHeight) {
        this.mResultTxHeight = mResultTxHeight;
    }
}
