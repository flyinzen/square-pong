package com.flyinzen.pong;

import android.graphics.RectF;

class Paddle {

    private RectF rect;
    private float width;
    private float height;
    private float x;
    private float y;
    private float speed;

    final int STOPPED = 0;
    static final int UP = 1;
    static final int DOWN = 2;

    private int paddleMoving = STOPPED;

    Paddle(float xCenter, float yCenter, float width, float height, int speed) {
        this.x = xCenter - width / 2;
        this.y = yCenter - height / 2;
        this.width = width;
        this.height = height;
        this.speed = speed;
        rect = new RectF(x, y, x + width, y + height);
    }

    RectF getRect() {
        return rect;
    }
    float getXCenter() {
        return rect.centerX();
    }

    float getYCenter() {
        return rect.centerY();
    }

    void setMovementState(int state) {
        paddleMoving = state;
    }

    void update(long fps) {
        if (paddleMoving == UP) {
            y -= speed / fps;
        }
        if (paddleMoving == DOWN) {
            y += speed / fps;
        }
        rect.top = y;
        rect.bottom = y + height;
    }
}
