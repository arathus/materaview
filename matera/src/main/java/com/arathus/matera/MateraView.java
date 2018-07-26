package com.arathus.matera;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.IntDef;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.arathus.matera.elements.CornerElement;
import com.shopgun.android.materialcolorcreator.MaterialColorImpl;
import com.shopgun.android.materialcolorcreator.Shade;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
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
    private String backgroundColor;
    private boolean inverse;

    private int colorshade;
    private boolean blackfilter;
    private int filtervalue;
    private ArrayList<Integer> colorPalette;

    private ArrayList<CornerElement> elements;
    private ArrayList<Integer> cornerOrder;


    private MateraView(Context context, int size, int numberOfLayers,
                       int dropShadowRadius, String color,
                       String backgroundColor, boolean inverse, int colorshade,
                       boolean blackfilter, int filtervalue) {
        super(context);
        this.size = size;
        this.numberOfLayers = numberOfLayers;
        this.dropShadowRadius = dropShadowRadius;
        this.color = color;
        this.backgroundColor = backgroundColor;
        this.inverse = inverse;
        this.colorshade = colorshade;
        this.blackfilter = blackfilter;
        this.filtervalue = filtervalue;
        initializeVariables();

    }

    public MateraView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeParameters(context, attrs);
        initializeVariables();

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

    /**
     * Read the necessary parameters to the view from the attributes, if they are available.
     */
    private void initializeParameters(Context context, AttributeSet attrs) {

        TypedArray at = context.obtainStyledAttributes(attrs,
                R.styleable.MateraView, 0, 0);

        size = at.getInt(R.styleable.MateraView_elementSizes, 0);
        numberOfLayers = at.getInt(R.styleable.MateraView_numberOfLayers, 2);
        dropShadowRadius = at.getInt(R.styleable.MateraView_dropShadowSize, 8);
        color = String.format("#%06X", (0xFFFFFF & at.getColor(R.styleable.MateraView_elementColor, Color.parseColor("#388E3C"))));
        backgroundColor = String.format("#%06X", (0xFFFFFF & at.getColor(R.styleable.MateraView_backgroundColor, Color.parseColor("#000000"))));
        inverse = at.getBoolean(R.styleable.MateraView_inverseColors, false);
        colorshade = at.getInt(R.styleable.MateraView_shadeOfColor, 5);
        blackfilter = at.getBoolean(R.styleable.MateraView_blackFilter, false);
        filtervalue = at.getInt(R.styleable.MateraView_filterValue, 0);
        at.recycle();

    }

    /**
     * Initialize the necessary variables on the creation of the view.
     */
    private void initializeVariables() {

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

        if (!backgroundColor.equals("#000000")) {
            colorPalette.set(0, Color.parseColor(backgroundColor));
        }

        if (dropShadowRadius < 0) {
            dropShadowRadius = 0;
        }

        if (numberOfLayers < 0) {
            numberOfLayers = 0;
        }

    }

    /**
     * Generates the values of the corner elements according to the preset parameters
     */
    private void generateCornersValues(int viewWidth, int viewHeight) {

        int min_width_value = 0;
        int max_width_value = 0;
        int min_height_value = 0;
        int max_heigth_value = 0;

        for (Integer act_corn : cornerOrder) {
            CornerElement cornerElement = new CornerElement(act_corn);
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
                cornerElement.addX(randInt(min_width_value, max_width_value));
                cornerElement.addY(randInt(min_height_value, max_heigth_value));
            }
            cornerElement.finalizeCornerElement(viewWidth, viewHeight);
            elements.add(cornerElement);
        }


    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        //Generate the values of the the corners
        generateCornersValues(getMeasuredWidth(), getMeasuredHeight());
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


    @Retention(RetentionPolicy.SOURCE)
    @IntDef({UNDERSIZED, NORMAL, OVERSIZED})
    @interface LayerSizes {
    }

    public static class Builder {

        private Context c;
        private int size = 0;
        private int numberOfLayers = 2;
        private int dropShadowRadius = 8;

        private String color = "#388E3C";
        private String backgroundColor = "#000000";
        private boolean inverse = false;

        private int colorshade = 5;
        private boolean blackfilter = false;
        private int filtervalue = 0;

        private ViewGroup parentView = null;

        public Builder(Context c) {
            this.c = c;
        }

        /**
         * Set the size of the material textures. Default value is NORMAL.
         * <p>
         *
         * @param size Can be one of the @{@link LayerSizes}
         *             UNDERSIZED - the textures are smaller than the sizes of the view
         *             NORMAL     - the textures may be as big as the view
         *             OVERSIZED  - the textures could be bigger than the view
         */

        public Builder setElementsSize(@LayerSizes int size) {
            this.size = size;
            return this;
        }

        /**
         * You can set here how many elements will be superposed.
         * <p>
         * (The best looking MateraViews for Matera Style, are between 2-5 layers) <p>
         * Default is 2.
         */
        public Builder setNumberOfLayers(int numberOfLayers) {
            this.numberOfLayers = numberOfLayers;
            return this;
        }

        /**
         * Set the size of the dropshadow. <p>
         * By default is 8
         */

        public Builder setDropShadowRadius(int dropShadowRadius) {
            this.dropShadowRadius = dropShadowRadius;
            return this;
        }

        /**
         * Add the main color to the MateraView. <p>
         * By default is #388E3C
         */

        public Builder setColor(String color) {
            this.color = color;
            return this;

        }

        /**
         * You can add here custom background color, if you want some special looking design. <p>
         * By default the background color is the color you pick in setColor.
         */

        public Builder setBackgroundColor(String backgroundColor) {
            this.backgroundColor = backgroundColor;
            return this;
        }

        /**
         * Here you can set where the MateraView will be placed.
         * <p>
         * IMPORTANT!!! If you do not set a parent view, the build() section will return with the MateraView you create with the builder.
         * Otherwise the build() section returns with null.
         */

        public Builder setParentView(ViewGroup parentView) {
            this.parentView = parentView;
            return this;
        }


        /**
         * Normally the darkest color is in the background and the lighter colors are in the foreground. <p>
         * Here you can change it.
         */

        public Builder setInverse(boolean inverse) {
            this.inverse = inverse;
            return this;
        }

        /**
         * Colorshade sets shade-difference between the layers. <p>
         * Default is 5.
         */

        public Builder setColorShade(int colorshade) {
            this.colorshade = colorshade;
            return this;
        }

        /**
         * Normally there is a light filter on the top of the view. You can set it here to black. <p>
         * If you want to set it black/dark.
         */

        public Builder setBlackFilter(boolean blackfilter) {
            this.blackfilter = blackfilter;
            return this;
        }


        /**
         * Normally there is a light filter on the top of the view. You can set it here to black. <p>
         * If you want to set it black/dark.
         */

        public Builder setFilterValue(int filtervalue) {
            this.filtervalue = filtervalue;
            return this;
        }


        /**
         * If you set ParentView, it adds the MateraView to it. <p>
         * If you don't, it returns with the MateraView you created before.
         */

        public MateraView build() {
            if (parentView == null) {
                return new MateraView(c, size, numberOfLayers, dropShadowRadius, color, backgroundColor, inverse, colorshade, blackfilter, filtervalue);
            } else {
                parentView.addView(new MateraView(c, size, numberOfLayers, dropShadowRadius, color, backgroundColor, inverse, colorshade, blackfilter, filtervalue), 0);
                return null;
            }
        }
    }
}


