package com.flyinzen.pong;

import android.graphics.RectF;

import java.util.Random;

import static java.lang.Math.abs;

class Ball {

    private RectF rect;
    float width;
    float height;
    private float xSpeed;
    private float ySpeed;
    private float x;
    private float y;

    Ball(float xCenter, float yCenter, float width, float height) {
        this.x = xCenter - width / 2;
        this.y = yCenter - height / 2;
        this.width = width;
        this.height = height;
        this.rect = new RectF(x, y, x + width, y + height);
    }

    Ball(float width, float height) {
        this.width = width;
        this.height = height;
    }

    void update(long fps) {
        y += ySpeed / fps;
        x += xSpeed / fps;
        rect.top = y;
        rect.bottom = y + height;
        rect.left = x;
        rect.right = x + width;
    }

    RectF getRect() {
        return rect;
    }

    void setSpeed(int[] speed) {
        this.xSpeed = speed[0];
        this.ySpeed = speed[1];
    }

    void randomlyIncreaseSpeed(int range) {
        Random rand = new Random();
        this.xSpeed += rand.nextInt(range);
        this.ySpeed += rand.nextInt(range);
    }

    void invertYSpeed() {
        this.ySpeed = -this.ySpeed;
    }

    void invertXSpeed() {
        this.xSpeed = -this.xSpeed;
    }

    float getXCenter() {
        return rect.centerX();
    }

    float getYCenter() {
        return rect.centerY();
    }

    void reset(float xCenter, float yCenter) {
        this.x = xCenter - width / 2;
        this.y = yCenter - height / 2;
        this.rect = new RectF(x, y, x + width, y + height);
    }
}
