package jp.ac.hal.surfaceviewsample01;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.KeyEvent;  // KeyEvent をインポート
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Random;

public class MainActivity extends AppCompatActivity implements SurfaceHolder.Callback2, Runnable {

    // 画面の描画時に必要なHolder
    private SurfaceHolder holder;
    private Thread mainLoop;
    private float ballX, ballY, ballSpeedX, ballSpeedY, ballRadius;
    private int backgroundColor;
    private Random random;

    // ブロックの位置とサイズ
    private float blockX, blockY, blockWidth, blockHeight;
    private float blockSpeed = 15; // ブロックの移動速度

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        SurfaceView surfaceView = findViewById(R.id.surfaceView);
        holder = surfaceView.getHolder();
        holder.addCallback(this);

        ballX = 500;
        ballY = 500;
        ballSpeedX = 5;
        ballSpeedY = 5;
        ballRadius = 50;

        // 背景色とランダム生成器を初期化
        backgroundColor = Color.WHITE;
        random = new Random();

        // ブロックの初期化
        blockWidth = 300;
        blockHeight = 50;
        blockX = 400;
        blockY = 1700; // 画面下部に配置
    }

    @Override
    public void surfaceRedrawNeeded(@NonNull SurfaceHolder holder) {

    }

    // surfaceView生成時の実行メソッド
    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {
        mainLoop = new Thread(this);
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {
        mainLoop.start();
    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
        mainLoop = null;
    }

    @Override
    public void run() {
        while (mainLoop != null) {
            ballX += ballSpeedX;
            ballY += ballSpeedY;

            // 壁に当たったら反転する
            if (ballX < ballRadius) {
                ballSpeedX = -ballSpeedX;
                changeBackgroundColor(); // 左側の壁に当たったら背景色を変える
            }
            if (ballX > holder.getSurfaceFrame().width() - ballRadius) {
                ballSpeedX = -ballSpeedX;
                changeBackgroundColor(); // 右側の壁に当たったら背景色を変える

                // 右側の壁に当たったらボールのスピードを上げる
                ballSpeedX *= 1.1;
                ballSpeedY *= 1.1;
            }
            if (ballY < ballRadius || ballY > holder.getSurfaceFrame().height() - ballRadius) {
                ballSpeedY = -ballSpeedY;
                changeBackgroundColor(); // 上下の壁に当たったら背景色を変える
            }

            // ボールとブロックの衝突判定
            if (ballX + ballRadius > blockX && ballX - ballRadius < blockX + blockWidth &&
                    ballY + ballRadius > blockY && ballY - ballRadius < blockY + blockHeight) {
                ballSpeedY = -ballSpeedY; // ボールのY方向を反転
                changeBackgroundColor(); // ブロックに当たったら背景色を変える
            }

            // canvasを使って描画
            Canvas canvas = holder.lockCanvas();

            // 背景色の描画
            canvas.drawColor(backgroundColor);

            // Paint 色スタイルの指定
            Paint paint = new Paint();
            paint.setColor(Color.rgb(255, 0, 0));

            // 円の描画
            canvas.drawCircle(ballX, ballY, ballRadius, paint);

            // ブロックの描画
            paint.setColor(Color.rgb(0, 0, 255));
            canvas.drawRect(blockX, blockY, blockX + blockWidth, blockY + blockHeight, paint);

            // unlock
            holder.unlockCanvasAndPost(canvas);

            try {
                Thread.sleep(10); // 約60FPSで描画するために16ミリ秒待機
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    // ランダムな背景色に変更するメソッド
    private void changeBackgroundColor() {
        backgroundColor = Color.rgb(random.nextInt(256), random.nextInt(256), random.nextInt(256));
    }

    // キーボード入力の処理
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_1:
                blockX -= blockSpeed; // 左キーでブロックを左に移動
                if (blockX < 0) {
                    blockX = 0; // 画面の左端を超えないようにする
                }
                return true;
            case KeyEvent.KEYCODE_2:
                blockX += blockSpeed; // 右キーでブロックを右に移動
                if (blockX + blockWidth > holder.getSurfaceFrame().width()) {
                    blockX = holder.getSurfaceFrame().width() - blockWidth; // 画面の右端を超えないようにする
                }
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
