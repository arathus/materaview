package com.arathus.matera;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

import com.arathus.matera.elements.CornerElement;
import com.shopgun.android.materialcolorcreator.MaterialColorImpl;
import com.shopgun.android.materialcolorcreator.Shade;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import static com.arathus.matera.elements.CornerElement.BOTTOM_LEFT;
import static com.arathus.matera.elements.CornerElement.BOTTOM_RIGHT;
import static com.arathus.matera.elements.CornerElement.TOP_LEFT;
import static com.arathus.matera.elements.CornerElement.TOP_RIGHT;

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
    private String color;
    private boolean inverse;

    private int colorshade;
    private boolean blackfilter;
    private int filtervalue;
    private ArrayList<Integer> colorPalette;

    private ArrayList<CornerElement> elements;
    private ArrayList<Integer> cornerOrder;


    public MateraView(Context context) {
        super(context);
    }

    public MateraView(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray at = context.obtainStyledAttributes(attrs,
                R.styleable.MateraView, 0, 0);

        size = at.getInt(R.styleable.MateraView_elementSizes, 0);
        numberOfLayers = at.getInt(R.styleable.MateraView_numberOfLayers, 2);
        dropShadowRadius = at.getInt(R.styleable.MateraView_dropShadowSize, 8);
        color = String.format("#%06X", (0xFFFFFF & at.getColor(R.styleable.MateraView_elementColor, Color.parseColor("#388E3C"))));
        inverse = at.getBoolean(R.styleable.MateraView_inverseColors, false);
        colorshade = at.getInt(R.styleable.MateraView_shadeOfColor, 5);
        blackfilter = at.getBoolean(R.styleable.MateraView_blackFilter, false);
        filtervalue = at.getInt(R.styleable.MateraView_filterValue, 0);

        at.recycle();

        elements = new ArrayList<>();

        cornerOrder = new ArrayList<>();
        cornerOrder.add(TOP_LEFT);
        cornerOrder.add(TOP_RIGHT);
        cornerOrder.add(BOTTOM_RIGHT);
        cornerOrder.add(BOTTOM_LEFT);

        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setShadowLayer(dropShadowRadius, 0, 0, Color.BLACK);
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.FILL);
        setLayerType(LAYER_TYPE_SOFTWARE, paint);

        colorPalette = (inverse ? getInverseColorPalette(color) : getColorPalette(color));

        if (dropShadowRadius < 0) {
            dropShadowRadius = 0;
        }

        if (numberOfLayers < 0) {
            numberOfLayers = 0;
        }
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

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);


        //Generate the values of the the triangle corners
        generateValues(getMeasuredWidth(), getMeasuredHeight());
    }

    private void generateValues(int viewWidth, int viewHeight) {

        int min_width_value = 0;
        int max_width_value = 0;
        int min_height_value = 0;
        int max_heigth_value = 0;

        for (Integer act_corn : cornerOrder) {
            CornerElement actual = new CornerElement(act_corn);
            for (int i = 0; i < numberOfLayers; i++) {

                switch (size) {

                    case -1/*UNDERSIZED*/:
                        min_width_value = Math.round(viewWidth / 7);
                        max_width_value = Math.round((viewWidth / 5) * 2);
                        min_height_value = Math.round(viewHeight / 7);
                        max_heigth_value = Math.round((viewHeight / 5) * 2);
                        break;

                    case 0/*NORMAL*/:
                        min_width_value = Math.round(viewWidth / 4);
                        max_width_value = Math.round((viewWidth / 3) * 2);
                        min_height_value = Math.round(viewHeight / 4);
                        max_heigth_value = Math.round((viewHeight / 3) * 2);
                        break;

                    case 1/*OVERSIZED*/:
                        min_width_value = Math.round((viewWidth / 5) * 3);
                        max_width_value = Math.round(viewWidth);
                        min_height_value = Math.round((viewHeight / 5) * 3);
                        max_heigth_value = Math.round(viewHeight);
                        break;
                }

                actual.addX(randInt(min_width_value, max_width_value));
                actual.addY(randInt(min_height_value, max_heigth_value));
            }
            actual.finalizeCornerElement(viewWidth, viewHeight);
            elements.add(actual);
        }


    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawColor(colorPalette.get(0));

        for (CornerElement ce : elements) {
            for (Path p : ce.getPaths()) {
                paint.setColor(colorPalette.get(ce.getPaths().indexOf(p) + 1));
                canvas.drawPath(p, paint);
            }
        }

        int value = blackfilter ? 0 : 255;
        canvas.drawColor(Color.argb(filtervalue, value, value, value));

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

    /*
    @Retention(RetentionPolicy.SOURCE)
    @IntDef({UNDERSIZED, NORMAL, OVERSIZED})
    @interface LayerSizes {
    }

    *//**
     * Set the size of the material textures. Default value is NORMAL.
     *
     * @param size Can be one of the @{@link LayerSizes}
     *             UNDERSIZED - the textures are smaller than the sizes of the view
     *             NORMAL     - the textures may be as big as the view
     *             OVERSIZED  - the textures could be bigger than the view
     *//*

    public void setSize(@LayerSizes int size) {
        this.size = size;
        this.invalidate();
    }*/


}


