package com.amnon.snakego.snake;

import android.graphics.Color;
import android.graphics.Typeface;
import com.amnon.snakego.snake.res.Res;
import com.amnon.snakego.snake.scene.GameScene;
import com.amnon.snakego.snake.util.ConstantUtil;
import com.orange.engine.camera.ZoomCamera;
import com.orange.engine.options.PixelPerfectEngineOptions;
import com.orange.engine.options.PixelPerfectMode;
import com.orange.engine.options.ScreenOrientation;
import com.orange.res.FontRes;
import com.orange.res.RegionRes;
import com.orange.ui.activity.GameActivity;

public class MainActivity extends GameActivity {

    @Override
    protected PixelPerfectEngineOptions onCreatePixelPerfectEngineOptions() {
        PixelPerfectEngineOptions pixelPerfectEngineOptions = new PixelPerfectEngineOptions(
            this, ZoomCamera.class);
        // 设置竖屏
        pixelPerfectEngineOptions
                .setScreenOrientation(ScreenOrientation.PORTRAIT_FIXED);
        // 适配模式,这里设置为“保持宽度不变，改变高”
        pixelPerfectEngineOptions
                .setPixelPerfectMode(PixelPerfectMode.CHANGE_HEIGHT);
        // 参考尺寸
        pixelPerfectEngineOptions.setDesiredSize(ConstantUtil.DESIRED_SIZE);
        return pixelPerfectEngineOptions;
    }

    @Override
    protected void onLoadResources() {
        RegionRes.loadTexturesFromAssets(Res.ALL_XML);
        // 加载字体资源
        //
        FontRes.loadFont(ConstantUtil.GAME_RESULT_WIDTH, ConstantUtil.GAME_RESULT_HEIGHT,
                Typeface.create(Typeface.DEFAULT, Typeface.BOLD),ConstantUtil.GAME_FONT_SIZE, true,
                Color.WHITE, ConstantUtil.GAME_RESULT_STR);

        FontRes.loadFont(ConstantUtil.GAME_SCORE_WIDTH, ConstantUtil.GAME_SOCRE_HEIGHT,
                Typeface.create(Typeface.DEFAULT, Typeface.NORMAL), ConstantUtil.GAME_FONT_SIZE, true,
                Color.WHITE, ConstantUtil.FONT_SCORE_NUM);


    }

    @Override
    protected void onLoadComplete() {
        this.startScene(GameScene.class);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        android.os.Process.killProcess(android.os.Process.myPid());
    }
}
