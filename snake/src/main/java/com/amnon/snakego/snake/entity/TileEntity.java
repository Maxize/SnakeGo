package com.amnon.snakego.snake.entity;

import com.orange.entity.group.EntityGroup;
import com.orange.entity.scene.Scene;
import com.orange.entity.sprite.AnimatedSprite;
import com.orange.opengl.vbo.VertexBufferObjectManager;

/**
 * Created by MaxizMa on 2014/8/25.
 */
public class TileEntity extends EntityGroup {

    private AnimatedSprite mTileImg;

    public TileEntity(float pWidth, float pHeight, Scene pScene) {
        super(pWidth, pHeight, pScene);

    }

    /**
     *   设置图片
     * @param resStr
     * @param width
     * @param height
     */
    public void setTileImg(String resStr, float width, float height) {
        if (mTileImg != null) {
            mTileImg = new AnimatedSprite(0,0,resStr,getVertexBufferObjectManager());
            mTileImg.setSize(width, height);
            this.attachChild(mTileImg);
        }else{
            this.detachChild(mTileImg);
            mTileImg = new AnimatedSprite(0,0,resStr,getVertexBufferObjectManager());
            mTileImg.setSize(width, height);
            this.attachChild(mTileImg);
        }
    }

    /**
     *  清除图片
     */
    public void clearTileImg() {
        if (mTileImg != null) {
            this.detachChild(mTileImg);
        }
    }

    public AnimatedSprite getTileImg(){
        return mTileImg;
    }


}
