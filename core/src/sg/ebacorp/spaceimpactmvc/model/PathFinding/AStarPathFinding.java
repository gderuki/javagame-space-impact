package sg.ebacorp.spaceimpactmvc.model.PathFinding;

import java.util.ArrayList;
import java.util.Collections;

import com.badlogic.gdx.math.Vector2;

public class AStarPathFinding {

    public static ArrayList<Vector2> findPath(int[] obstacleMap, int nMapWidth, int nMapHeight, Vector2 start, Vector2 end) {
        ArrayList<APathNode> aPathNodes = new ArrayList<>();
        for (int y = 0; y < nMapHeight; y++) {
            for (int x = 0; x < nMapWidth; x++) {

                APathNode node = new APathNode(new Vector2(x, y));
                if (obstacleMap[y * nMapWidth + x] == 1) {
                    node.setObstacle();
                }
                aPathNodes.add(node);
            }
        }

        for (int x = 0; x < nMapWidth; x++) {
            for (int y = 0; y < nMapHeight; y++) {
                if (y > 0) {
                    aPathNodes.get(y * nMapWidth + x).addNeighbour(aPathNodes.get((y - 1) * nMapWidth + (x + 0)));
                }
                if (y < nMapHeight - 1) {
                    aPathNodes.get(y * nMapWidth + x).addNeighbour(aPathNodes.get((y + 1) * nMapWidth + (x + 0)));
                }
                if (x > 0) {
                    aPathNodes.get(y * nMapWidth + x).addNeighbour(aPathNodes.get((y + 0) * nMapWidth + (x - 1)));
                }
                if (x < nMapWidth - 1) {
                    aPathNodes.get(y * nMapWidth + x).addNeighbour(aPathNodes.get((y + 0) * nMapWidth + (x + 1)));
                }
            }
        }
        int posx = (int) start.x / 50;
        int posy = (int) start.y / 50;

        APathNode nodeStart = aPathNodes.get(posy * nMapWidth + posx);
        nodeStart.setLocalGoal(0);

        int posxEnd = (int) end.x / 50;
        int posyEnd = (int) end.y / 50;

        APathNode nodeEnd = aPathNodes.get(posyEnd * nMapWidth + posxEnd);
        nodeStart.setGlobalGoal(start.dst(nodeEnd.getPosition()));

        ArrayList<APathNode> listNotTestedNodes = new ArrayList<>();
        listNotTestedNodes.add(nodeStart);
        APathNode nodeCurrent = nodeStart;

        while (!listNotTestedNodes.isEmpty() && nodeCurrent != nodeEnd) {
            Collections.sort(listNotTestedNodes);
            while (!listNotTestedNodes.isEmpty() && listNotTestedNodes.get(0).isVisited()) {
                listNotTestedNodes.remove(0);
            }
            if (listNotTestedNodes.isEmpty()) {
                break;
            }
            nodeCurrent = listNotTestedNodes.get(0);
            nodeCurrent.setVisited();
            for (APathNode nodeNeighbour : nodeCurrent.getChilds()) {
                if (!nodeNeighbour.isVisited() && !nodeNeighbour.isObstacle()) {
                    listNotTestedNodes.add(nodeNeighbour);
                }
                float fPossiblyLowerGoal = nodeCurrent.getLocalGoal() + nodeCurrent.getPosition().dst(nodeNeighbour.getPosition());
                if (fPossiblyLowerGoal < nodeNeighbour.getLocalGoal()) {
                    nodeNeighbour.setParent(nodeCurrent);
                    nodeNeighbour.setLocalGoal(fPossiblyLowerGoal);
                    nodeNeighbour.setGlobalGoal(nodeNeighbour.getLocalGoal() + nodeNeighbour.getPosition().dst(nodeEnd.getPosition()));
                }
            }
        }
        ArrayList<Vector2> result = new ArrayList<>();
        APathNode parent = nodeEnd.getParent();
        if (parent != null) {
            result.add(nodeEnd.getPosition());
            result.add(parent.getPosition());
            while (parent != null) {
                parent = parent.getParent();
                if (parent != null) {
                    result.add(parent.getPosition());
                }
            }
        }
        return result;
    }
}
