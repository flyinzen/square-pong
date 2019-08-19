package com.flyinzen.pong;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

public class GameView extends SurfaceView implements Runnable {

    private Thread gameThread = null;
    private SurfaceHolder holder;
    private Paint paint;
    volatile boolean playing;
    private boolean paused = true;
    private long fps;

    float screenX;
    float screenY;

    Paddle playerPaddle;
    Paddle computerPaddle;
    Ball ball;

    float PADDLE_WIDTH = 100;
    float PADDLE_HEIGHT = 300;
    int PADDLE_SPEED = 300;
    float BALL_WIDTH = 100;
    float BALL_HEIGHT = 100;

    int playerScore = 0;
    int computerScore = 0;

    public GameView(Context context) {
        super(context);
        holder = getHolder();
        paint = new Paint();

        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        assert wm != null;
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        screenX = size.x;
        screenY = size.y;

        int[] ballSpeed = {1000, 1000};

        playerPaddle = new Paddle(PADDLE_WIDTH / 2, screenY / 2,
                PADDLE_WIDTH, PADDLE_HEIGHT, PADDLE_SPEED);
        computerPaddle = new Paddle(screenX - PADDLE_WIDTH / 2, screenY / 2,
                PADDLE_WIDTH, PADDLE_HEIGHT, PADDLE_SPEED);

        ball = new Ball(BALL_WIDTH, BALL_HEIGHT);
        ball.setSpeed(ballSpeed);

        restart(1);
    }

    public void restart(int paddle) {
        ball.randomlyIncreaseSpeed(100);
        if (paddle == 1) {
            ball.reset(playerPaddle.getRect().right + ball.width / 2,
                    playerPaddle.getYCenter());
        } else if (paddle == 2) {
            ball.reset(computerPaddle.getRect().left + ball.width / 2,
                    computerPaddle.getYCenter());
        }
    }

    @Override
    public void run() {
        while (playing) {
            long startFrameTime = System.currentTimeMillis();
            if (!paused) {
                update();
            }
            draw();
            long timeThisFrame = System.currentTimeMillis() - startFrameTime;

            if (timeThisFrame >= 1) {
                fps = 1000 / timeThisFrame;
                if (fps <= 0) {
                    System.out.println(fps);
                }
            }
        }
    }

    public void update() {
        if (ball.getRect().top <= 0 || ball.getRect().bottom >= screenY) {
            ball.invertYSpeed();
        }

        if (ball.getRect().left < 0) {
            computerScore += 1;
            restart(2);
        } else if (ball.getRect().right > screenX) {
            playerScore += 1;
            restart(1);
        } else if (RectF.intersects(ball.getRect(), playerPaddle.getRect())
                || (RectF.intersects(ball.getRect(), computerPaddle.getRect()))) {
            ball.invertXSpeed();
        }

        if (ball.getYCenter() - computerPaddle.getYCenter() > 0) {
            computerPaddle.setMovementState(Paddle.DOWN);
        } else {
            computerPaddle.setMovementState(Paddle.UP);
        }

        ball.update(fps);
        playerPaddle.update(fps);
        computerPaddle.update(fps);
    }

    public void draw() {
        if (holder.getSurface().isValid()) {
            Canvas canvas = holder.lockCanvas();
            canvas.drawColor(Color.rgb(0, 0, 0));

            paint.setColor(Color.rgb(255, 255, 255));
            canvas.drawRect(playerPaddle.getRect(), paint);
            canvas.drawRect(computerPaddle.getRect(), paint);
            canvas.drawRect(ball.getRect(), paint);

            paint.setTextSize(80);
            canvas.drawText(String.valueOf(playerScore),
                    (int) 1 * screenX / 4, (int) 1 * screenY / 8,
                    paint);
            canvas.drawText(String.valueOf(computerScore),
                    (int) 3 * screenX / 4, (int) 1 * screenY / 8,
                    paint);

            holder.unlockCanvasAndPost(canvas);
        }
    }

    public void pause() {
        playing = false;
        try {
            gameThread.join();
        } catch (InterruptedException e) {
            Log.e("Error:", "joining thread");
        }
    }

    public void resume() {
        playing = true;
        gameThread = new Thread(this);
        gameThread.start();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {

        switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:

                paused = false;

                if (motionEvent.getY() > screenY / 2) {
                    playerPaddle.setMovementState(Paddle.DOWN);
                } else {
                    playerPaddle.setMovementState(Paddle.UP);
                }

                break;

            case MotionEvent.ACTION_UP:

                playerPaddle.setMovementState(playerPaddle.STOPPED);
                break;
        }

        return true;
    }
}
