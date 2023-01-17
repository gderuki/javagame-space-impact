package sg.ebacorp.spaceimpactmvc.model.PathFinding;

import java.util.ArrayList;

import com.badlogic.gdx.math.Vector2;

public class APathNode implements Comparable<APathNode> {

    private Vector2 position;
    private float localGoal = Float.MAX_VALUE;
    private float globalGoal = Float.MAX_VALUE;
    private boolean visited;
    private ArrayList<APathNode> vecNeighbours = new ArrayList<>();
    private APathNode parent;
    private boolean obstacle;

    public APathNode(Vector2 position) {
        this.position = position;
    }

    public Vector2 getPosition() {
        return position;
    }

    void addNeighbour(APathNode node) {
        vecNeighbours.add(node);
    }

    public void setLocalGoal(float localGoal) {
        this.localGoal = localGoal;
    }

    public void setGlobalGoal(float globalGoal) {
        this.globalGoal = globalGoal;
    }

    @Override
    public int compareTo(APathNode o) {
        return new Float(globalGoal).compareTo(new Float(o.globalGoal));
    }

    public boolean isVisited() {
        return visited;
    }

    public void setVisited() {
        visited = true;
    }

    public ArrayList<APathNode> getChilds() {
        return vecNeighbours;
    }

    public boolean isObstacle() {
        return obstacle;
    }

    public float getLocalGoal() {
        return localGoal;
    }

    public void setParent(APathNode nodeCurrent) {
        this.parent = nodeCurrent;
    }

    public APathNode getParent() {
        return parent;
    }

    public void setObstacle() {
        obstacle = true;
    }
}
