package jp.ac.hal.surfaceviewsample01;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity implements SurfaceHolder.Callback2,Runnable {

    //画面の描画時に必要なHolder
    private SurfaceHolder holder;
    private Thread mainLoop;
    private float ballX, ballY, ballSpeedX, ballSpeedY, ballRadius;

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


    }

    @Override
    public void surfaceRedrawNeeded(@NonNull SurfaceHolder holder) {

    }

    //surfaceView生成時の実行メソッド
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

    public void BoundCircleBall(float ballX, float ballY, float ballRadius, float ballSpeedX, float ballSpeedY, SurfaceHolder holder) {
        ballX += ballSpeedX;
        ballY += ballSpeedY;

        // 壁に当たったら反転する
        if (ballX < ballRadius || ballX > holder.getSurfaceFrame().width() - ballRadius) {
            ballSpeedX = -ballSpeedX;
        }
        if (ballY < ballRadius || ballY > holder.getSurfaceFrame().height() - ballRadius) {
            ballSpeedY = -ballSpeedY;
        }

        //canvasを使って描画
        Canvas canvas = holder.lockCanvas();

        //Paint 色スタイルの指定
        Paint paint = new Paint();
        paint.setColor(Color.BLUE);

        // 背景色
        canvas.drawColor(Color.WHITE);

        // 円の描画
        canvas.drawCircle(ballX, ballY, ballRadius, paint);

        //unlock
        holder.unlockCanvasAndPost(canvas);

        try {
            Thread.sleep(100); // 約60FPSで描画するために16ミリ秒待機
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void run() {
        while (mainLoop != null) {
//            BoundCircleBall(ballX, ballY, ballRadius,  ballSpeedX, ballSpeedY, holder);
            ballX += ballSpeedX;
            ballY += ballSpeedY;

            // 壁に当たったら反転する
            if (ballX < ballRadius || ballX > holder.getSurfaceFrame().width() - ballRadius) {
                ballSpeedX = -ballSpeedX;
            }
            if (ballY < ballRadius || ballY > holder.getSurfaceFrame().height() - ballRadius) {
                ballSpeedY = -ballSpeedY;
            }

            //canvasを使って描画
            Canvas canvas = holder.lockCanvas();

            //Paint 色スタイルの指定
            Paint paint = new Paint();
            paint.setColor(Color.rgb(255, 0,0));

            // 背景色
            canvas.drawColor(Color.WHITE);

            // 円の描画
            canvas.drawCircle(ballX, ballY, ballRadius, paint);

            //unlock
            holder.unlockCanvasAndPost(canvas);

            try {
                Thread.sleep(15); // 約60FPSで描画するために16ミリ秒待機
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}