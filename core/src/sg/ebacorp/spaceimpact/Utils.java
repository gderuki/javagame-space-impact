package sg.ebacorp.spaceimpact;

public class Utils {
    /*
    * INFO: what should be incremented is `increment` part.
    *  It goes like this: each step/tick we apply math curvature function and increase it,
    *  e.g. if curve is exp -> we get fast soon, slow later, etc, etc...
    * */
    public static float approach(float start, float target, float increment) {
        increment = Math.abs(increment);

        if (start < target) {
            start += increment;

            if (start > target) {
                start = target;
            }
        } else {
            start -= increment;

            if (start < target) {
                start = target;
            }
        }

        return start;
    }
}
