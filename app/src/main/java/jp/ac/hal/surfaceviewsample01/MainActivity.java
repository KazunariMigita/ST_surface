package jp.ac.hal.surfaceviewsample01;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.KeyEvent; // KeyEvent をインポート
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity implements SurfaceHolder.Callback2, Runnable {

    // 画面の描画時に必要なHolder
    private SurfaceHolder holder;
    private Thread mainLoop;
    private float ballX, ballY, ballSpeedX, ballSpeedY, ballRadius;

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

        // ブロックの初期化
        blockWidth = 300;
        blockHeight = 50;
        blockX = 400; // ブロックのX位置
        blockY = holder.getSurfaceFrame().height() + 200; // ブロックのY位置を画面下部に設定
    }

    @Override
    public void surfaceRedrawNeeded(@NonNull SurfaceHolder holder) {

    }

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
            if (ballX < ballRadius || ballX > holder.getSurfaceFrame().width() - ballRadius) {
                ballSpeedX = -ballSpeedX;
            }
            if (ballY < ballRadius || ballY > holder.getSurfaceFrame().height() - ballRadius) {
                ballSpeedY = -ballSpeedY;
            }

            // ボールとブロックの衝突判定
            if (ballX + ballRadius > blockX && ballX - ballRadius < blockX + blockWidth &&
                    ballY + ballRadius > blockY && ballY - ballRadius < blockY + blockHeight) {
                ballSpeedY = -ballSpeedY; // ボールのY方向を反転
            }

            // canvasを使って描画
            Canvas canvas = holder.lockCanvas();

            // 背景色の描画
            canvas.drawColor(Color.WHITE);

            // Paint 色スタイルの指定
            Paint paint = new Paint();
            paint.setColor(Color.rgb(0, 155, 10));

            // 円の描画
            canvas.drawCircle(ballX, ballY, ballRadius, paint);

            // ブロックの描画
            paint.setColor(Color.rgb(0, 255, 0));
            canvas.drawRect(blockX, blockY, blockX + blockWidth, blockY + blockHeight, paint);

            paint.setColor(Color.DKGRAY); // テキストの色
            paint.setTextSize(70); // テキストサイズ
            canvas.drawText("空也きゅんのボーるゲーム♡", 100, 100, paint);
            // unlock
            holder.unlockCanvasAndPost(canvas);

            try {
                Thread.sleep(15); // 約60FPSで描画するために16ミリ秒待機
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    // キーボード入力の処理
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_W: // 'W'キーで上に移動
                blockY -= blockSpeed;
                if (blockY < 0) {
                    blockY = 0; // 画面の上端を超えないようにする
                }
                return true;
            case KeyEvent.KEYCODE_X: // 'X'キーで下に移動
                blockY += blockSpeed;
                if (blockY + blockHeight > holder.getSurfaceFrame().height()) {
                    blockY = holder.getSurfaceFrame().height() - blockHeight; // 画面の下端を超えないようにする
                }
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
