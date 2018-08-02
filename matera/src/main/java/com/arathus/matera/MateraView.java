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

    public static final int CHAOTIC = 0;
    public static final int FRAME_STRICT = 1;
    public static final int FRAME_CHAOTIC = 2;
    public static final int FRAME_REGULAR = 3;
    public static final int INTEGRAL_EQUAL = 4;
    public static final int INTEGRAL_STRICT = 5;

    private int style;
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

    private int x_value, y_value, x_unit, y_unit;

    private MateraView(Context context, int stlye, int size, ArrayList<Integer> cornerOrder, int numberOfLayers,
                       int dropShadowRadius, String color,
                       String backgroundColor, boolean inverse, int colorshade,
                       boolean blackfilter, int filtervalue) {
        super(context);
        this.cornerOrder = cornerOrder;
        this.style = stlye;
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

        cornerOrder= new ArrayList<>();
        cornerOrder.add(-1);
        cornerOrder.add(-1);
        cornerOrder.add(-1);
        cornerOrder.add(-1);
        style = at.getInt(R.styleable.MateraView_materaStyle, 0);
        size = at.getInt(R.styleable.MateraView_elementSizes, 0);
        numberOfLayers = at.getInt(R.styleable.MateraView_numberOfLayers, 2);
        dropShadowRadius = at.getInt(R.styleable.MateraView_dropShadowSize, 6);
        color = String.format("#%06X", (0xFFFFFF & at.getColor(R.styleable.MateraView_elementColor, Color.parseColor("#388E3C"))));
        backgroundColor = String.format("#%06X", (0xFFFFFF & at.getColor(R.styleable.MateraView_backgroundColor, Color.parseColor("#000000"))));
        inverse = at.getBoolean(R.styleable.MateraView_inverseColors, false);
        colorshade = at.getInt(R.styleable.MateraView_shadeOfColor, 5);
        blackfilter = at.getBoolean(R.styleable.MateraView_blackFilter, false);
        filtervalue = at.getInt(R.styleable.MateraView_filterValue, 0);

        cornerOrder.set(0, at.getInt(R.styleable.MateraView_firstCornerToDraw, -1));
        cornerOrder.set(1, at.getInt(R.styleable.MateraView_secondCornerToDraw, -1));
        cornerOrder.set(2, at.getInt(R.styleable.MateraView_thirdCornerToDraw, -1));
        cornerOrder.set(3, at.getInt(R.styleable.MateraView_fourthCornerToDraw, -1));

        at.recycle();

    }

    /**
     * Initialize the necessary variables on the creation of the view.
     */
    private void initializeVariables() {

        elements = new ArrayList<>();

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
     * Checks if there is preset rendering order to the view. <p>
     * If there is no such preset, the default rendering order is: <p>
     * - TOP_LEFT <p>
     * - TOP_RIGHT <p>
     * - BOTTOM_LEFT <p>
     * - BOTTOM_RIGHT <p>
     */

    private void checkCornerOrder() {

        boolean emptyorder = true;
        for (Integer i : cornerOrder) {
            if (i != -1) {
                emptyorder = false;
            }
        }

        if (emptyorder) {
            cornerOrder.set(0, TOP_LEFT);
            cornerOrder.set(1, TOP_RIGHT);
            cornerOrder.set(2, BOTTOM_RIGHT);
            cornerOrder.set(3, BOTTOM_LEFT);

        }
    }

    /**
     * Generates the values of the corner elements according to the preset parameters.
     * <p>
     * Belongs to Chaotic style.
     */
    private void generateChaoticCornerValues(int viewWidth, int viewHeight) {

        switch (size) {
            case UNDERSIZED:
                x_value = Math.round(viewWidth / 3);
                y_value = Math.round(viewHeight / 3);
                break;

            case NORMAL:
                x_value = Math.round(viewWidth / 2);
                y_value = Math.round(viewHeight / 2);
                break;

            /*OVERSIZED*/
            default:
                x_value = Math.round(viewWidth);
                y_value = Math.round(viewHeight);
                break;
        }

        x_unit = Math.round(x_value / numberOfLayers);
        y_unit = Math.round(y_value / numberOfLayers);

        checkCornerOrder();
        for (Integer act_corn : cornerOrder) {
            if (act_corn != -1) {
                CornerElement cornerElement = new CornerElement(act_corn);
                for (int i = 0; i < numberOfLayers; i++) {
                    cornerElement.addX(x_unit + x_value - (x_unit * i) + ((randInt(0, 1) == 0) ? randInt(x_unit / 2, x_unit) : (randInt(x_unit / 2, x_unit) * -1)));
                    cornerElement.addY(y_unit + y_value - (y_unit * i) + ((randInt(0, 1) == 0) ? randInt(y_unit / 2, y_unit) : (randInt(y_unit / 2, y_unit) * -1)));
                }
                cornerElement.finalizeCornerElement(viewWidth, viewHeight);
                elements.add(cornerElement);
            }
        }
    }

    /**
     * Generates the values of the corner elements according to the preset parameters.
     * <p>
     * Belongs to Integral style.
     */
    private void generateIntegralCornerValues(int viewWidth, int viewHeight) {

        switch (size) {
            case UNDERSIZED:
                x_value = Math.round(viewWidth / 2);
                y_value = Math.round(viewHeight / 2);
                break;

            case NORMAL:
                x_value = Math.round((viewWidth / 3) * 2);
                y_value = Math.round((viewHeight / 3) * 2);
                break;

            /*OVERSIZED*/
            default:
                x_value = Math.round(viewWidth + (viewWidth / 2));
                y_value = Math.round(viewHeight + (viewHeight / 2));
                break;
        }

        checkCornerOrder();
        if (style == INTEGRAL_STRICT) {
            x_value = (x_value > y_value) ? y_value : x_value;
            y_value = (x_value > y_value) ? y_value : x_value;
        }

        x_unit = Math.round(x_value / numberOfLayers);
        y_unit = Math.round(y_value / numberOfLayers);

        for (Integer act_corn : cornerOrder) {
            if (act_corn != -1) {
                CornerElement cornerElement = new CornerElement(act_corn);
                for (int i = 0; i < numberOfLayers; i++) {
                    cornerElement.addX(x_value - (i * x_unit));
                    cornerElement.addY(y_unit + (i * y_unit));
                }
                cornerElement.finalizeCornerElement(viewWidth, viewHeight);
                elements.add(cornerElement);
            }
        }

    }

    /**
     * Generates the values of the corner elements according to the preset parameters.
     * <p>
     * Belongs to Frame style.
     */
    private void generateFrameCornerValues(int viewWidth, int viewHeight) {

        switch (size) {
            case UNDERSIZED:
                x_value = Math.round(viewWidth / 2);
                y_value = Math.round(viewHeight / 2);
                break;

            case NORMAL:
                x_value = Math.round((viewWidth / 3) * 2);
                y_value = Math.round((viewHeight / 3) * 2);
                break;

            /*OVERSIZED*/
            default:
                x_value = Math.round(viewWidth + (viewWidth / 2));
                y_value = Math.round(viewHeight + (viewHeight / 2));
                break;
        }

        x_value = (x_value > y_value) ? y_value : x_value;
        x_unit = Math.round(x_value / numberOfLayers);

        checkCornerOrder();
        for (Integer act_corn : cornerOrder) {
            if (act_corn != -1) {
                CornerElement cornerElement = new CornerElement(act_corn);
                for (int i = 0; i < numberOfLayers; i++) {
                    int rand = randInt(0, Math.round((x_unit * 3) / 4));
                    cornerElement.addX((x_value - (i * x_unit)) + ((style == FRAME_STRICT) ? 0 : ((style == FRAME_REGULAR) ? rand : randInt(0, Math.round((x_unit * 3) / 4)))));
                    cornerElement.addY((x_value - (i * x_unit)) + ((style == FRAME_STRICT) ? 0 : ((style == FRAME_REGULAR) ? rand : randInt(0, Math.round((x_unit * 3) / 4)))));
                }
                cornerElement.finalizeCornerElement(viewWidth, viewHeight);
                elements.add(cornerElement);
            }
        }

    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        //Generate the values of the the corners
        switch (style) {

            case CHAOTIC:
                generateChaoticCornerValues(getMeasuredWidth(), getMeasuredHeight());
                break;

            case FRAME_STRICT:
            case FRAME_CHAOTIC:
            case FRAME_REGULAR:
                generateFrameCornerValues(getMeasuredWidth(), getMeasuredHeight());
                break;

            case INTEGRAL_EQUAL:
            case INTEGRAL_STRICT:
                generateIntegralCornerValues(getMeasuredWidth(), getMeasuredHeight());
                break;

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


    @Retention(RetentionPolicy.SOURCE)
    @IntDef({UNDERSIZED, NORMAL, OVERSIZED})
    @interface LayerSizes {
    }

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({CHAOTIC, FRAME_STRICT, FRAME_CHAOTIC, FRAME_REGULAR, INTEGRAL_EQUAL, INTEGRAL_STRICT})
    @interface MateraStyles {
    }

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({TOP_LEFT, TOP_RIGHT, BOTTOM_RIGHT, BOTTOM_LEFT})
    @interface CornerType {
    }

    public static class Builder {

        private Context c;
        private int style = 0;
        private int size = 0;
        private int numberOfLayers = 2;
        private int dropShadowRadius = 6;

        private String color = "#388E3C";
        private String backgroundColor = "#000000";
        private boolean inverse = false;

        private int colorshade = 5;
        private boolean blackfilter = false;
        private int filtervalue = 0;

        private ViewGroup parentView = null;

        private ArrayList<Integer> cornerOrder;

        public Builder(Context c) {
            this.c = c;
            cornerOrder = new ArrayList<>();
            cornerOrder.add(-1);
            cornerOrder.add(-1);
            cornerOrder.add(-1);
            cornerOrder.add(-1);

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
         * Add the main color to the MateraView. <p>
         * By default is #388E3C
         */

        public Builder setColor(int color) {
            this.color = String.format("#%06X", (0xFFFFFF & color));
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
         * Here you can choose what style do you want to use to the MateraView
         * <p>
         * Default is CHAOTIC style.
         */

        public Builder setStyle(@MateraStyles int style) {
            this.style = style;
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
         * Set which layer will be drawn first
         */

        public Builder setFirstCornerToDraw(@CornerType int corner) {
            this.cornerOrder.set(0, corner);
            return this;
        }

        /**
         * Set which layer will be drawn after the first
         */

        public Builder setSecondCornerToDraw(@CornerType int corner) {
            this.cornerOrder.set(1, corner);
            return this;
        }

        /**
         * Set which layer will be drawn after the second
         */

        public Builder setThirdCornerToDraw(@CornerType int corner) {
            this.cornerOrder.set(2, corner);
            return this;
        }

        /**
         * Set which layer will be drawn after the third
         */

        public Builder setFourthCornerToDraw(@CornerType int corner) {
            this.cornerOrder.set(3, corner);
            return this;
        }

        /**
         * If you set ParentView, it adds the MateraView to it. <p>
         * If you don't, it returns with the MateraView you created before.
         */

        public MateraView build() {
            if (parentView == null) {
                return new MateraView(c, style, size, cornerOrder, numberOfLayers, dropShadowRadius, color, backgroundColor, inverse, colorshade, blackfilter, filtervalue);
            } else {
                parentView.addView(new MateraView(c, style, size, cornerOrder, numberOfLayers, dropShadowRadius, color, backgroundColor, inverse, colorshade, blackfilter, filtervalue), 0);
                return null;
            }
        }
    }
}


