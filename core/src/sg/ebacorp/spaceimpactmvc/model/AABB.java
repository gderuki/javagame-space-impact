package sg.ebacorp.spaceimpactmvc.model;

public class AABB {

    private final float minX;
    private final float minY;
    private final float maxX;
    private final float maxY;

    public AABB(float minX, float minY, float maxX, float maxY) {
        this.minX = minX;
        this.minY = minY;
        this.maxX = maxX;
        this.maxY = maxY;
    }

    public static boolean IntersectAABBs(AABB a, AABB b) {
        if (a.maxX <= b.minX || b.maxX <= a.minX || a.maxY <= b.minY || b.maxY <= a.minY) {
            return false;
        }

        return true;
    }
}
