package sg.ebacorp.spaceimpactmvc.model.Spline;

import java.util.ArrayList;

import com.badlogic.gdx.math.Vector2;

public class Spline {

    Vector2 points[];

    public Spline(ArrayList<Vector2> points) {
        this.points = new Vector2[points.size()];
        for (int i = 0; i < points.size(); i++) {
            this.points[i] = points.get(i);
        }
    }

    public Vector2 GetSplinePoint(float t, boolean bLooped) {
        int p0, p1, p2, p3;
        if (!bLooped) {
            p1 = (int) t + 1;
            p2 = p1 + 1;
            p3 = p2 + 1;
            p0 = p1 - 1;
        } else {
            p1 = (int) t;
            p2 = (p1 + 1) % points.length;
            p3 = (p2 + 1) % points.length;
            p0 = p1 >= 1 ? p1 - 1 : points.length - 1;
        }

        t = t - (int) t;

        float tt = t * t;
        float ttt = tt * t;

        float q1 = -ttt + 2.0f * tt - t;
        float q2 = 3.0f * ttt - 5.0f * tt + 2.0f;
        float q3 = -3.0f * ttt + 4.0f * tt + t;
        float q4 = ttt - tt;

        float tx = 0.5f * (points[p0].x * q1 + points[p1].x * q2 + points[p2].x * q3 + points[p3].x * q4);
        float ty = 0.5f * (points[p0].y * q1 + points[p1].y * q2 + points[p2].y * q3 + points[p3].y * q4);

        return new Vector2(tx, ty);
    }

}
