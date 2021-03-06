package ayalma.ir.epandablenavigationview;

import android.annotation.TargetApi;
import android.content.res.ColorStateList;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RippleDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.StateListDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.os.Build;
import android.view.View;
import android.view.animation.RotateAnimation;

import java.util.Arrays;

/**
 * Created by alimohammadi on 4/29/16.
 *
 * @author alimohammadi.
 */
public class Util {

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static Drawable getPressedColorRippleDrawable(int normalColor, int pressedColor) {
        return new RippleDrawable(ColorStateList.valueOf(pressedColor),
                null, getRippleMask(normalColor));
    }


    public static ColorDrawable getColorDrawableFromColor(int color) {
        return new ColorDrawable(color);
    }

    private static Drawable getRippleMask(int color) {
        float[] outerRadii = new float[8];
        // 3 is radius of final ripple,
        // instead of 3 you can give required final radius
        Arrays.fill(outerRadii, 3);

        RoundRectShape r = new RoundRectShape(outerRadii, null, null);
        ShapeDrawable shapeDrawable = new ShapeDrawable(r);
        shapeDrawable.getPaint().setColor(color);
        return shapeDrawable;
    }

    public static StateListDrawable getStateListDrawable(int normalColor, int pressedColor) {
        StateListDrawable states = new StateListDrawable();
        states.addState(new int[]{android.R.attr.state_pressed},
                new ColorDrawable(pressedColor));
        states.addState(new int[]{android.R.attr.state_focused},
                new ColorDrawable(pressedColor));
        states.addState(new int[]{android.R.attr.state_activated},
                new ColorDrawable(pressedColor));
        states.addState(new int[]{},
                new ColorDrawable(normalColor));
        return states;
    }

    public static RotateAnimation getRotateAnim(float fromDegree, float toDegree, long duration) {
        RotateAnimation rotateAnim = new RotateAnimation(fromDegree, toDegree,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f);

        rotateAnim.setDuration(duration);
        rotateAnim.setFillAfter(true);

        return rotateAnim;
    }

    public static void setRippleColor(int rippleColor, int backColor, View view) {

        if (Build.VERSION.SDK_INT >= 21) {
            view.setBackgroundColor(backColor);
            view.setBackgroundColor(backColor);
            view.setBackground(Util.getPressedColorRippleDrawable(backColor, rippleColor));
        } else {
            view.setBackgroundColor(backColor);
            view.setBackgroundDrawable(Util.getStateListDrawable(backColor, rippleColor));
        }
    }
}
