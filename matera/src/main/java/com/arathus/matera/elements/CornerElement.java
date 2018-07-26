package com.arathus.matera.elements;

import android.graphics.Path;
import android.graphics.Point;
import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;

public class CornerElement {

    public static final int TOP_LEFT = 0, TOP_RIGHT = 1, BOTTOM_RIGHT = 2, BOTTOM_LEFT = 3;
    private int type;
    private ArrayList<Path> paths;
    private ArrayList<Integer> x_coordinates;
    private ArrayList<Integer> y_coordinates;
    private Point a, b, c;

    public CornerElement(@CornerType int type) {
        this.type = type;
        this.paths = new ArrayList<>();
        this.x_coordinates = new ArrayList<>();
        this.y_coordinates = new ArrayList<>();
        a = new Point();
        b = new Point();
        c = new Point();

    }

    public void addX(Integer i) {
        x_coordinates.add(i);
    }

    public void addY(Integer i) {
        y_coordinates.add(i);
    }

    public void finalizeCornerElement(int width, int height) {

        for (int i = 0; i < x_coordinates.size(); i++) {

            Path path = new Path();
            switch (type) {

                case TOP_LEFT:
                    a.set(0, 0);
                    b.set(x_coordinates.get(i), 0);
                    c.set(0, y_coordinates.get(i));
                    break;

                case TOP_RIGHT:
                    a.set(width, 0);
                    b.set(width - x_coordinates.get(i), 0);
                    c.set(width, y_coordinates.get(i));
                    break;

                case BOTTOM_RIGHT:
                    a.set(width, height);
                    b.set(width - x_coordinates.get(i), height);
                    c.set(width, height - y_coordinates.get(i));
                    break;

                case BOTTOM_LEFT:
                    a.set(0, height);
                    b.set(x_coordinates.get(i), height);
                    c.set(0, height - y_coordinates.get(i));
                    break;

            }
            path.setFillType(Path.FillType.EVEN_ODD);
            path.moveTo(a.x, a.y);
            path.lineTo(b.x, b.y);
            path.lineTo(c.x, c.y);
            path.close();
            paths.add(path);
        }

    }

    public ArrayList<Path> getPaths() {
        return paths;
    }

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({TOP_LEFT, TOP_RIGHT, BOTTOM_RIGHT, BOTTOM_LEFT})
    @interface CornerType {
    }

}






