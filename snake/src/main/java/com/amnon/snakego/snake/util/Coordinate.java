package com.amnon.snakego.snake.util;

/**
 * Created by amnonma on 2014/8/19.
 */
public class Coordinate {

    private int x;
    private int y;

    public Coordinate(int newX, int newY) {
        x = newX;
        y = newY;
    }

    public boolean equals(Coordinate other) {
        if (x == other.x && y == other.y) {
            return true;
        }
        return false;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getX() {

        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    @Override
    public String toString() {
        return "Coordinate: [" + x + "," + y + "]";
    }
}
