package com.arathus.matera;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.support.annotation.IntDef;
import android.util.AttributeSet;
import android.view.View;

import com.shopgun.android.materialcolorcreator.MaterialColorImpl;
import com.shopgun.android.materialcolorcreator.Shade;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/**
 * Created by arathus on 2018.07.06..
 */

public class MateraView extends View {

    // Declare the constants
    public static final int UNDERSIZED = -1;
    public static final int NORMAL = 0;
    public static final int OVERSIZED = 1;
    private int size;
    private int numberOfLayers;
    private int dropShadowRadius;
    private Paint paint;
    private String color = "#388E3C";
    private boolean inverse;
    private int colorshade;
    private ArrayList<Path> paths;
    private ArrayList<Integer> x_points;
    private ArrayList<Integer> y_points;
    private int TOP_LEFT = 0, TOP_RIGHT = 1, BOTTOM_RIGHT = 2, BOTTOM_LEFT = 3;
    private ArrayList<Integer> cornerOrder;

    public MateraView(Context context) {
        super(context);
    }

    public MateraView(Context context, AttributeSet attrs) {
        super(context, attrs);

        x_points = new ArrayList<>();
        y_points = new ArrayList<>();
        paths = new ArrayList<>();
        cornerOrder = new ArrayList<>();
        cornerOrder.add(TOP_LEFT);
        cornerOrder.add(TOP_RIGHT);
        cornerOrder.add(BOTTOM_RIGHT);
        cornerOrder.add(BOTTOM_LEFT);
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);

        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.MateraView, 0, 0);

        size = a.getInt(R.styleable.MateraView_elementSizes, 0);
        numberOfLayers = a.getInt(R.styleable.MateraView_numberOfLayers, 2);
        dropShadowRadius = a.getInt(R.styleable.MateraView_dropShadowSize, 8);
        color = String.format("#%06X", (0xFFFFFF & a.getColor(R.styleable.MateraView_elementColor, Color.parseColor("#388E3C"))));
        inverse = a.getBoolean(R.styleable.MateraView_inverseColors, false);
        colorshade = a.getInt(R.styleable.MateraView_shadeOfColor, 5);
        a.recycle();
    }

    public MateraView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

    }

    /**
     * Generate a random integer
     *
     * @param min The minimal value of the random number
     * @param max The maximal value of the random number
     * @return A random integer between min and max
     */
    private static int randInt(int min, int max) {
        Random rand = new Random();
        return rand.nextInt((max - min) + 1) + min;
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        int width = getMeasuredWidth();
        int height = getMeasuredHeight();

        if (dropShadowRadius < 0) {
            dropShadowRadius = 0;
        }

        int min_width_value = 0;
        int max_width_value = 0;
        int min_height_value = 0;
        int max_heigth_value = 0;

        //Check if there is more than one layers
        if (numberOfLayers > 0) {

            //Generate the values of the the triangle corners
            for (Integer act_corn : cornerOrder) {

                for (int i = 0; i < numberOfLayers; i++) {

                    switch (size) {

                        //TODO aránytalanul rajzolja ki az egyes rétegeket
                        case -1/*UNDERSIZED*/:
                            min_width_value = Math.round(width / 7);
                            max_width_value = Math.round((width / 5) * 2);
                            min_height_value = Math.round(height / 7);
                            max_heigth_value = Math.round((height / 5) * 2);
                            break;

                        case 0/*NORMAL*/:
                            min_width_value = Math.round(width / 4);
                            max_width_value = Math.round((width / 3) * 2);
                            min_height_value = Math.round(height / 4);
                            max_heigth_value = Math.round((height / 3) * 2);
                            break;

                        case 1/*OVERSIZED*/:
                            min_width_value = Math.round((width / 5) * 3);
                            max_width_value = Math.round(width);
                            min_height_value = Math.round((height / 5) * 3);
                            max_heigth_value = Math.round(height);
                            break;
                    }

                    //TODO meg kell oldani, hogy az egyes generált rétegek értékei ne legyenek drasztikusan hasonlók (?!) Magyarán ne legyen két réteg egymástól miliméter távolságra, mert gagyin néz ki

                    x_points.add(randInt(min_width_value, max_width_value));
                    y_points.add(randInt(min_height_value, max_heigth_value));

                }
            }

            //Generate every layer, to every corner. The ordering is according to the cornerOrder
            for (Integer act_corn : cornerOrder) {

                for (int i = 0; i < numberOfLayers; i++) {

                    int act_corn_position = cornerOrder.indexOf(act_corn);
                    Path path = new Path();

                    Point a, b, c;
                    switch (act_corn) {

                        case 0:
                            a = new Point(0, 0);
                            b = new Point(x_points.get(((act_corn_position * numberOfLayers) + i)), 0);
                            c = new Point(0, y_points.get(((act_corn_position * numberOfLayers) + i)));
                            break;

                        case 1:
                            a = new Point(width, 0);
                            b = new Point(width - x_points.get(((act_corn_position * numberOfLayers) + i)), 0);
                            c = new Point(width, y_points.get(((act_corn_position * numberOfLayers) + i)));
                            break;

                        case 2:
                            a = new Point(width, height);
                            b = new Point(width - x_points.get(((act_corn_position * numberOfLayers) + i) - 1), height);
                            c = new Point(width, height - y_points.get(((act_corn_position * numberOfLayers) + i) - 1));
                            break;

                        default:
                            a = new Point(0, height);
                            b = new Point(x_points.get(((act_corn_position * numberOfLayers) + i)), height);
                            c = new Point(0, height - y_points.get(((act_corn_position * numberOfLayers) + i)));
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
        }


    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        ArrayList<Integer> colorPalette = (inverse ? getInverseColorPalette(color) : getColorPalette(color));
        canvas.drawColor(colorPalette.get(0));

        paint.setShadowLayer(dropShadowRadius, 0, 0, Color.BLACK);
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.FILL);
        setLayerType(LAYER_TYPE_SOFTWARE, paint);

        for (Path p : paths) {
            paint.setColor(colorPalette.get((paths.indexOf(p) % numberOfLayers) + 1));
            canvas.drawPath(p, paint);
        }
    }

    /**
     * Set the size of the material textures
     *
     * @param size Can be one of the @{@link LayerSizes}
     *             UNDERSIZED - the textures are smaller than the sizes of the view
     *             NORMAL     - the textures may be as big as the view
     *             OVERSIZED  - the textures could be bigger than the view
     */

    public void setSize(@LayerSizes int size) {
        this.size = size;
    }

    /**
     * Generate a darker-lighter color palette to a given color.
     * The number of color shades equals the number of layers.
     *
     * @param color The main color as a hexa string
     * @return An ArrayList with the color palette integers
     */

    private ArrayList<Integer> getColorPalette(String color) {

        int actual_color = MaterialColorImpl.getModifiedColor(Color.parseColor(color), Shade.values()[colorshade]).getValue();

        ArrayList<Integer> actual = new ArrayList<>();
        actual.add(actual_color);
        for (int i = 0; i < numberOfLayers; i++) {
            actual_color = MaterialColorImpl.getModifiedColor(actual_color, Shade.values()[colorshade - 1]).getValue();
            actual.add(MaterialColorImpl.getModifiedColor(actual_color, Shade.values()[colorshade - 1]).getValue());
        }

        return actual;
    }

    /**
     * Generate a lighter-darker color palette to a given color.
     * The number of color shades equals the number of layers.
     *
     * @param color The main color as a hexa string
     * @return An ArrayList with the color palette integers
     */
    private ArrayList<Integer> getInverseColorPalette(String color) {
        ArrayList<Integer> cp = getColorPalette(color);
        Collections.reverse(cp);
        return cp;
    }

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({UNDERSIZED, NORMAL, OVERSIZED})
    @interface LayerSizes {
    }

}


