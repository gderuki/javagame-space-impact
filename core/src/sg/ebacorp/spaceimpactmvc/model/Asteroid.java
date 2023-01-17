package sg.ebacorp.spaceimpactmvc.model;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.PolygonRegion;
import com.badlogic.gdx.graphics.g2d.PolygonSprite;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.EarClippingTriangulator;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.RandomXS128;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Asteroid implements PolygonRenderAble {

    private boolean block;
    private Texture texture;

    private float[] vertices;
    private Vector2 position = new Vector2();
    private Vector2 acceleration = new Vector2();
    private Vector2 velocity;
    private float angle = 0;
    private boolean hasCollision;
    private float depth;
    private Vector2 overlapAxis;
    private float angleVelocity = 0;
    private float mass = 0;
    private float invMass = 0;
    //this.inertia = this.m * ((2*this.comp[0].width)**2 +(this.comp[0].length+2*this.comp[0].width)**2) / 12;
    private float inertia = 0;
    private float inversInertia = 0;
    private float staticFriction = 0.6f;
    private float dynamicFriction = 0.4f;
    private float restitution = 0.2f;
    RandomXS128 random = new RandomXS128();

    Vector2 gravity = new Vector2(0, -229f);
    private TextureRegion region;

    public Asteroid(float positionX, float positionY, boolean forward, boolean rectangle, float mass, Vector2 speed) {
        this(positionX, positionY, 50, 70, forward, rectangle, mass, speed);
    }

    public Asteroid(float positionX, float positionY, int width, int height, boolean forward, boolean rectangle, float mass, Vector2 speed) {
        this.mass = mass;
        if (mass > 0) {
            invMass = (float) 1 / mass;
            inertia = (1f / 12) * mass * (width * width + height * height);
        }
        if (inertia > 0) {
            inversInertia = (float) 1 / inertia;
        }
        this.velocity = new Vector2();
        velocity.set(speed);
        position.x = positionX;
        position.y = positionY;
        if (rectangle) {
            this.vertices = new float[4 * 2];
            this.vertices[0] = (float) -width / 2;
            this.vertices[1] = (float) height / 2;
            this.vertices[2] = (float) -width / 2 + width;
            this.vertices[3] = (float) height / 2;
            this.vertices[4] = (float) -width / 2 + width;
            this.vertices[5] = (float) -height / 2;
            this.vertices[6] = (float) -width / 2;
            this.vertices[7] = (float) -height / 2;
        } else {
            createVertices();
        }
        createTexture();
    }

    public Asteroid(int positionX, int positionY, boolean block) {
        random.setSeed((positionX & 0xFFFF) << 16 | (positionY & 0xFFFF));
        this.mass = 1;
        if (mass > 0) {
            invMass = (float) 1 / mass;
            inertia = (1f / 12) * mass * (10 * 10 + 10 * 10);
        }
        if (inertia > 0) {
            inversInertia = (float) 1 / inertia;
        }
        position.x = positionX + 25;
        position.y = positionY + 25;
        velocity = new Vector2();
        gravity = new Vector2();
        if (block) {
            this.block = true;
            int width = 50;
            int height = 50;
            this.vertices = new float[4 * 2];
            this.vertices[0] = (float) -width / 2;
            this.vertices[1] = (float) height / 2;
            this.vertices[2] = (float) -width / 2 + width;
            this.vertices[3] = (float) height / 2;
            this.vertices[4] = (float) -width / 2 + width;
            this.vertices[5] = (float) -height / 2;
            this.vertices[6] = (float) -width / 2;
            this.vertices[7] = (float) -height / 2;
        } else {
            createVertices();
        }
    }

    public boolean isBlock() {
        return block;
    }

    private int random(int start, int end) {
        return start + random.nextInt(end - start + 1);
    }

    private float random(float start, float end) {
        return start + random.nextFloat() * (end - start);
    }

    private void createTexture() {
        AABB aabb = getAABB();
        Pixmap pixmap = new Pixmap((int) aabb.getWidth(), (int) aabb.getHeight(), Pixmap.Format.RGBA8888);
        MyPixelRenderer.generatePerlin(pixmap, false, 8);
        texture = new Texture(pixmap);
        texture.setWrap(Texture.TextureWrap.ClampToEdge, Texture.TextureWrap.ClampToEdge);
        this.region = new TextureRegion(texture);
        // since we are using verticies with negative values, we need to adjust U and V axises
        this.region.setRegion(this.region.getU() + 0.5f, this.region.getV() - 0.5f, this.region.getU2() + 0.5f, this.region.getV2() - 0.5f);
    }

    public boolean hasCollision() {
        return this.hasCollision;
    }

    public Vector2 getOverlapAxis() {
        return overlapAxis;
    }

    private void createVertices() {
        int minPoints = 6;
        int maxPoints = 10;
        int points = random(minPoints, maxPoints);
        this.vertices = new float[points * 2];
        float deltaAngle = MathUtils.PI2 / (float) points;
        float angle = 0f;
        float minDist = 12f;
        float maxDist = 24f;
        for (int i = 0; i < points * 2; i = i + 2) {
            float dist = random(minDist, maxDist);
            float x = MathUtils.cos(angle) * dist;
            float y = MathUtils.sin(angle) * dist;
            this.vertices[i] = x;
            this.vertices[i + 1] = y;
            angle += deltaAngle;
        }
    }

    public float getDepth() {
        return depth;
    }

    public float[] getTransformedVertices(float ppuX, float ppuY, boolean draw) {
        //                vertices[i] = cos * vertexX - sin * vertexY;
//                vertices[i + 1] = sin * vertexX + cos * vertexY;
        float sin = MathUtils.sin(angle);
        float cos = MathUtils.cos(angle);

        float[] transformed = new float[vertices.length];
        for (int i = 0; i < vertices.length; i = i + 2) {
            float vertexX = vertices[i];
            float vertexY = vertices[i + 1];
            if (draw) {
                transformed[i] = vertexX + (position.x * ppuX);
                transformed[i + 1] = vertexY + (position.y * ppuY);
            } else {
                transformed[i] = (cos * vertexX - sin * vertexY) + (position.x * ppuX);
                transformed[i + 1] = (sin * vertexX + cos * vertexY) + (position.y * ppuY);
            }
        }
        return transformed;
    }

    public Overlap intersect(Asteroid anotherAsteroid, float ppuX, float ppuY) {
        float[] myTransformedVertices = anotherAsteroid.getTransformedVertices(ppuX, ppuY, false);
        float[] anotherTransrmedVertices = getTransformedVertices(ppuX, ppuY, false);
        Overlap overlap = hasGap(position, anotherAsteroid.getPosition(), myTransformedVertices, anotherTransrmedVertices);
        if (overlap.isGap()) {
            hasCollision = false;
            return overlap;
        }
        depth = overlap.depth;
        overlapAxis = overlap.axis;
        hasCollision = true;
        return overlap;
    }

    private Overlap hasGap(Vector2 position, Vector2 anotherAsteroidPosition, float[] AVerticies, float[] BVerticies) {
        Vector2 collisionAxis = null;
        float overlapDepth = Float.MAX_VALUE;
        for (int i = 0; i < AVerticies.length; i = i + 2) {
            // building edge from 2 vertic es of this polygon;
            float myXa = AVerticies[i];
            float myYa = AVerticies[i + 1];
            float myXb = AVerticies[(i + 2) % AVerticies.length];
            float myYb = AVerticies[(i + 3) % AVerticies.length];
            Vector2 vectorA = new Vector2(myXa, myYa);
            Vector2 vectorB = new Vector2(myXb, myYb);
            Vector2 edge = vectorB.cpy().sub(vectorA);
            // normal of edge is perpendicular to edge
            Vector2 normalOfEdge = new Vector2(-edge.y, edge.x);
            normalOfEdge = normalOfEdge.nor();
            ProjectedVertex myProjections = getProjectedVertex(AVerticies, normalOfEdge);
            ProjectedVertex anotherPorjections = getProjectedVertex(BVerticies, normalOfEdge);
            // No intersection
            if (myProjections.min >= anotherPorjections.max || anotherPorjections.min >= myProjections.max) {
                return new Overlap(0f, null, null, true);
            }
            float axisDepth = Math.min(anotherPorjections.max - myProjections.min, myProjections.max - anotherPorjections.min);
            if (axisDepth < overlapDepth) {
                overlapDepth = axisDepth;
                collisionAxis = normalOfEdge;
            }
        }
        for (int i = 0; i < BVerticies.length; i = i + 2) {
            // building edge from 2 vertic es of this polygon;
            float myXa = BVerticies[i];
            float myYa = BVerticies[i + 1];
            float myXb = BVerticies[(i + 2) % BVerticies.length];
            float myYb = BVerticies[(i + 3) % BVerticies.length];
            Vector2 vectorA = new Vector2(myXa, myYa);
            Vector2 vectorB = new Vector2(myXb, myYb);
            Vector2 edge = vectorB.cpy().sub(vectorA);
            // normal of edge is perpendicular to edge
            Vector2 normalOfEdge = new Vector2(-edge.y, edge.x);
            normalOfEdge = normalOfEdge.nor();
            ProjectedVertex myProjections = getProjectedVertex(AVerticies, normalOfEdge);
            ProjectedVertex anotherPorjections = getProjectedVertex(BVerticies, normalOfEdge);
            // No intersection
            if (myProjections.min >= anotherPorjections.max || anotherPorjections.min >= myProjections.max) {
                return new Overlap(0f, null, null, true);
            }
            float axisDepth = Math.min(anotherPorjections.max - myProjections.min, myProjections.max - anotherPorjections.min);
            if (axisDepth < overlapDepth) {
                overlapDepth = axisDepth;
                collisionAxis = normalOfEdge;
            }
        }

        ContactPoint collisionVertex = findPolygonsContactPoints(AVerticies, BVerticies);

        Vector2 direction = anotherAsteroidPosition.cpy().sub(position);
        if (Vector2.dot(direction.x, direction.y, collisionAxis.x, collisionAxis.y) < 0) {
            collisionAxis.scl(-1);
        }

        return new Overlap(overlapDepth, collisionAxis, collisionVertex.cp, false);
    }

    private ProjectedVertex getProjectedVertex(float[] transformedVertices, Vector2 axis) {
        float min = axis.dot(transformedVertices[0], transformedVertices[1]);
        float max = min;
        float collisionVertexX = transformedVertices[0];
        float collisionVertexY = transformedVertices[1];
        //float min = -Float.MAX_VALUE;
        //float max = Float.MAX_VALUE;
        for (int i = 0; i < transformedVertices.length; i = i + 2) {
            float x = transformedVertices[i];
            float y = transformedVertices[i + 1];
            float dot = Vector2.dot(x, y, axis.x, axis.y);
            if (dot < min) {
                collisionVertexX = x;
                collisionVertexY = y;
                min = dot;
            }
            if (dot > max) {
                max = dot;
            }
        }
        return new ProjectedVertex(min, max, new Vector2(collisionVertexX, collisionVertexY));
    }

    public ContactPoint findPolygonsContactPoints(float[] AVerticies, float[] BVerticies) {
        float minDist = Float.MAX_VALUE;
        ContactPoint closetContactPoint = null;
        for (int i = 0; i < AVerticies.length; i = i + 2) {
            float myXa = AVerticies[i];
            float myYa = AVerticies[i + 1];
            Vector2 point = new Vector2(myXa, myYa);

            for (int y = 0; y < BVerticies.length; y = y + 2) {
                Vector2 va = new Vector2(BVerticies[y], BVerticies[y + 1]);
                Vector2 vb = new Vector2(BVerticies[(y + 2) % BVerticies.length], BVerticies[(y + 3) % BVerticies.length]);
                ContactPoint contactPoint = PointSegmentDistance(point, va, vb);
                if (contactPoint.distance < minDist) {
                    minDist = contactPoint.distance;
                    closetContactPoint = contactPoint;
                }
            }
        }

        for (int i = 0; i < BVerticies.length; i = i + 2) {
            float myXa = BVerticies[i];
            float myYa = BVerticies[i + 1];
            Vector2 point = new Vector2(myXa, myYa);
            for (int y = 0; y < AVerticies.length; y = y + 2) {
                Vector2 va = new Vector2(AVerticies[y], AVerticies[y + 1]);
                Vector2 vb = new Vector2(AVerticies[(y + 2) % AVerticies.length], AVerticies[(y + 3) % AVerticies.length]);
                ContactPoint contactPoint = PointSegmentDistance(point, va, vb);
                if (contactPoint.distance < minDist) {
                    minDist = contactPoint.distance;
                    closetContactPoint = contactPoint;
                }
            }
        }
        return closetContactPoint;

    }

    public ContactPoint PointSegmentDistance(Vector2 p, Vector2 a, Vector2 b) {
        Vector2 ab = b.cpy().sub(a);
        Vector2 ap = p.cpy().sub(a);
        Vector2 cp;

        float proj = ap.dot(ab);
        float abLenSq = ab.len2();
        float d = proj / abLenSq;

        if (d <= 0f) {
            cp = a;
        } else if (d >= 1f) {
            cp = b;
        } else {
            Vector2 scl = ab.scl(d);
            cp = a.add(scl);
        }

        float distance = p.cpy().sub(cp).len2();
        return new ContactPoint(distance, cp);
    }

    public Vector2 getPosition() {
        return position;
    }

    public Vector2 getAcceleration() {
        return acceleration;
    }

    public Vector2 getVelocity() {
        return velocity;
    }

    public float getAngleVelocity() {
        return angleVelocity;
    }

    public void setAngleVelocity(float angleVelocity) {
        this.angleVelocity = angleVelocity;
    }

    public Vector2 getGravity() {
        return gravity;
    }

    public void setGravity(Vector2 gravity) {
        this.gravity = gravity;
    }

    @Override
    public void draw(PolygonSpriteBatch pSB, float ppuX, float ppuY) {
        PolygonSprite polygonSprite = createPolygonSprite(ppuX, ppuY);
        polygonSprite.draw(pSB);
    }

    private PolygonSprite createPolygonSprite(float ppuX, float ppuY) {
        // No need to transform vertices here, since libgdx will do it internally
        // check set position and set origin
        if (vertices == null) {
            createVertices();
        }
        if (texture == null) {
            createTexture();
        }
        float[] origVertices = vertices;
        PolygonRegion polygonRegion = new PolygonRegion(region, origVertices, new EarClippingTriangulator().computeTriangles(origVertices).toArray());
        PolygonSprite polygonSprite = new PolygonSprite(polygonRegion);
        polygonSprite.setPosition(position.x * ppuX, position.y * ppuY);
        polygonSprite.setOrigin(0, 0);
        polygonSprite.rotate(MathUtils.radiansToDegrees * angle);
        return polygonSprite;
    }


    public void update(float delta, int i) {
        delta = delta / (float) i;
        if (delta > 0) {
            angle += angleVelocity * delta;
            acceleration.scl(delta);
            if (mass > 0) {
                velocity.add(gravity.cpy().scl(delta));
            }
            velocity.add(acceleration.x, acceleration.y);
            velocity.scl(delta);
            position.add(velocity);
            velocity.scl(1 / delta);
            acceleration.scl(1 / delta);
        }
    }
    public AABB getAABB() {
        float minX = 999999999f;
        float minY = 999999999f;
        float maxX = -999999999f;
        float maxY = -999999999f;
        if (vertices == null) {
            createVertices();
        }
        float[] transformedVertices = getTransformedVertices(1, 1, false);
        for (int i = 0; i < transformedVertices.length; i = i + 2) {
            Vector2 v = new Vector2(transformedVertices[i], transformedVertices[i + 1]);
            if (v.x < minX) {
                minX = v.x;
            }
            if (v.x > maxX) {
                maxX = v.x;
            }
            if (v.y < minY) {
                minY = v.y;
            }
            if (v.y > maxY) {
                maxY = v.y;
            }
        }
        return new AABB(minX, minY, maxX, maxY);
    }

    public Rectangle getRectangle() {
        if (vertices == null || vertices.length == 0) {
            return new Rectangle(position.x, position.y, 60, 60);
        }
        AABB aabb = getAABB();
        return new Rectangle(position.x, position.y, aabb.getWidth(), aabb.getHeight());
    }

    public void collisionResolution33(Overlap intersect, Asteroid asteroid2, float ppuX, float ppuY) {
        Vector2 relativeVelocity = velocity.cpy().sub(asteroid2.velocity);
        float j = -(1f + (float) 0.2) * relativeVelocity.dot(intersect.axis);
        Vector2 impulse = intersect.axis.cpy().scl(j);
        velocity.add(impulse.cpy().scl(invMass));
        asteroid2.velocity.add(impulse.cpy().scl(asteroid2.invMass).scl(-1));
    }

    public void collisionResolution5(Overlap intersect, Asteroid asteroid2, float ppuX, float ppuY) {
        Vector2 collisionArm1 = intersect.getCollisionVertex().cpy().sub(this.position.cpy().scl(ppuX, ppuY));
        Vector2 rotationVelocity1 = new Vector2(-angleVelocity * collisionArm1.y, angleVelocity * collisionArm1.x);
        Vector2 closVelocity1 = velocity.cpy().add(rotationVelocity1);
        Vector2 collisionArm2 = intersect.getCollisionVertex().cpy().sub(asteroid2.getPosition().cpy().scl(ppuX, ppuY));
        Vector2 rotationVelocity2 = new Vector2(-asteroid2.angleVelocity * collisionArm2.y, asteroid2.angleVelocity * collisionArm2.x);
        Vector2 closVelocity2 = asteroid2.velocity.cpy().add(rotationVelocity2);

        //Vector2 normal = position.cpy().sub(asteroid2.getPosition()).nor();
        Vector2 normal = intersect.axis;
        float impulse1 = normal.crs(collisionArm1);
        impulse1 = impulse1 * inversInertia * impulse1;
        float impulse2 = normal.crs(collisionArm2);
        impulse2 = impulse2 * asteroid2.inversInertia * impulse2;

        Vector2 relativeVel = closVelocity1.cpy().sub(closVelocity2);
        float sepVelocity = relativeVel.dot(normal);
//        if(sepVelocity < 0) {

        float newSepVelocity = -sepVelocity * 0.5f; //elasticity
        float vsepDiff = newSepVelocity - sepVelocity;
//            float vsepDiff = newSepVelocity;

        float impulseToApply = vsepDiff / (invMass + asteroid2.invMass + impulse1 + impulse2);
        Vector2 impulseVec = normal.cpy().scl(impulseToApply);

        velocity.add(impulseVec.scl(invMass).scl(1));
        asteroid2.velocity.add(impulseVec.scl(asteroid2.invMass).scl(-1));
        angleVelocity += +collisionArm1.crs(impulseVec) * inversInertia;
        asteroid2.angleVelocity += -collisionArm2.crs(impulseVec) * asteroid2.inversInertia;
//        }
    }

    public void collisionResolution(Overlap intersect, Asteroid asteroid2, float ppuX, float ppuY) {
        Vector2 ra = intersect.collisionVertex.cpy().sub(position.cpy().scl(ppuX));
        Vector2 rb = intersect.collisionVertex.cpy().sub(asteroid2.position.cpy().scl(ppuY));

        Vector2 raPerp = new Vector2(-ra.y, ra.x);
        Vector2 rbPerp = new Vector2(-rb.y, rb.x);

        Vector2 angularLinearVelocityA = raPerp.cpy().scl(angleVelocity);
        Vector2 angularLinearVelocityB = rbPerp.cpy().scl(asteroid2.angleVelocity);

        Vector2 relativeVelocity = (asteroid2.velocity.cpy().add(angularLinearVelocityB).sub((velocity.cpy().add(angularLinearVelocityA))));

        float contactVelocityMag = relativeVelocity.dot(intersect.getAxis());

        if (contactVelocityMag < 0f) {
            float raPerpDotN = raPerp.dot(intersect.axis);
            float rbPerpDotN = rbPerp.dot(intersect.axis);

            float denom =
                    invMass + asteroid2.invMass + (raPerpDotN * raPerpDotN) * inversInertia + (rbPerpDotN * rbPerpDotN) * asteroid2.inversInertia;
            float j = -(1f + restitution) * contactVelocityMag;
            j /= denom;

            Vector2 impulse = intersect.axis.cpy().scl(j);

            velocity.add(impulse.cpy().scl(-1).scl(invMass));
            angleVelocity += -ra.crs(impulse) * inversInertia;
            asteroid2.velocity.add(impulse.cpy().scl(asteroid2.invMass));
            asteroid2.angleVelocity += rb.crs(impulse) * asteroid2.inversInertia;

            applyFriction(intersect, asteroid2, j);
        }


    }

    private void applyFriction(Overlap intersect, Asteroid asteroid2, float j) {
        Vector2 ra = intersect.collisionVertex.cpy().sub(position.cpy().scl(1));
        Vector2 rb = intersect.collisionVertex.cpy().sub(asteroid2.position.cpy().scl(1));

        Vector2 raPerp = new Vector2(-ra.y, ra.x);
        Vector2 rbPerp = new Vector2(-rb.y, rb.x);

        Vector2 angularLinearVelocityA = raPerp.cpy().scl(angleVelocity);
        Vector2 angularLinearVelocityB = rbPerp.cpy().scl(asteroid2.angleVelocity);

        Vector2 relativeVelocity = (asteroid2.velocity.cpy().add(angularLinearVelocityB).sub((velocity.cpy().add(angularLinearVelocityA))));

        Vector2 tangent = relativeVelocity.cpy().sub(intersect.axis.cpy().scl(relativeVelocity.dot(intersect.axis)));
        if (isNearEqual(tangent, Vector2.Zero)) {
            return;
        } else {
            tangent = tangent.nor();
        }

        float raPerpDotT = raPerp.dot(tangent);
        float rbPerpDotT = rbPerp.dot(tangent);
        float frictionDenom =
                invMass + asteroid2.invMass + (raPerpDotT * raPerpDotT) * inversInertia + (rbPerpDotT * rbPerpDotT) * asteroid2.inversInertia;

        float jt = -relativeVelocity.dot(tangent);
        jt /= frictionDenom;

        float sf = (staticFriction + asteroid2.staticFriction) * 0.5f;
        float df = (dynamicFriction + asteroid2.dynamicFriction) * 0.5f;
        Vector2 frictionImpulse;

        if (Math.abs(jt) <= j * sf) {
            frictionImpulse = tangent.cpy().scl(jt);
        } else {
            frictionImpulse = tangent.cpy().scl(-j).scl(df);
        }

        velocity.add(frictionImpulse.cpy().scl(-1).scl(invMass));
        angleVelocity += -ra.crs(frictionImpulse) * inversInertia;
        asteroid2.velocity.add(frictionImpulse.cpy().scl(asteroid2.invMass));
        asteroid2.angleVelocity += rb.crs(frictionImpulse) * asteroid2.inversInertia;
    }

    private boolean isNearEqual(Vector2 vector1, Vector2 vector2) {
        return vector1.dst2(vector2) < 0.01f;
    }

    public void collisionResolution55(Overlap intersect, Asteroid asteroid2, float ppuX, float ppuY) {
        Vector2 ra = intersect.collisionVertex.cpy().sub(position.cpy().scl(ppuX));
        Vector2 rb = intersect.collisionVertex.cpy().sub(asteroid2.position.cpy().scl(ppuY));

        Vector2 raPerp = new Vector2(-ra.y, ra.x);
        Vector2 rbPerp = new Vector2(-rb.y, rb.x);

        Vector2 angularLinearVelocityA = raPerp.cpy().scl(angleVelocity);
        Vector2 angularLinearVelocityB = rbPerp.cpy().scl(asteroid2.angleVelocity);

        Vector2 relativeVelocity = (asteroid2.velocity.cpy().add(angularLinearVelocityB).sub((velocity.cpy().add(angularLinearVelocityA))));

        float contactVelocityMag = relativeVelocity.dot(intersect.getAxis());

        if (contactVelocityMag < 0f) {
            float raPerpDotN = raPerp.dot(intersect.axis);
            float rbPerpDotN = rbPerp.dot(intersect.axis);

            float denom =
                    invMass + asteroid2.invMass + (raPerpDotN * raPerpDotN) * inversInertia + (rbPerpDotN * rbPerpDotN) * asteroid2.inversInertia;
            float j = -(1f + restitution) * contactVelocityMag;
            j /= denom;

            Vector2 impulse = intersect.axis.cpy().scl(j);

            velocity.add(impulse.cpy().scl(-1).scl(invMass));
            angleVelocity += -ra.crs(impulse) * inversInertia;
            asteroid2.velocity.add(impulse.cpy().scl(asteroid2.invMass));
            asteroid2.angleVelocity += rb.crs(impulse) * asteroid2.inversInertia;
        }


    }

    public void penetrationResolution(Overlap intersect, Asteroid asteroid2, float ppuX, float ppuY) {
        float depth = intersect.getDepth() / 2;
        Vector2 vector = intersect.getAxis().cpy().scl(depth);
        vector.x = vector.x / ppuX;
        vector.y = vector.y / ppuY;
        if (asteroid2.mass == 0) {
            this.getPosition().add(vector.scl(2).scl(-1f));
        } else if (mass == 0) {
            asteroid2.getPosition().add(vector.scl(2));
        } else {
            asteroid2.getPosition().add(vector);
            this.getPosition().add(vector.scl(-1f));
        }
    }

    public float getMass() {
        return mass;
    }

    private static class ProjectedVertex {
        float min;
        float max;
        private final Vector2 collisionVertex;

        public ProjectedVertex(float min, float max, Vector2 collisionVertex) {
            this.min = min;
            this.max = max;
            this.collisionVertex = collisionVertex;
        }
    }

    public static class Overlap {
        float depth;
        private Vector2 collisionVertex;
        Vector2 axis = new Vector2();
        boolean gap;

        public Overlap(float depth, Vector2 vector, Vector2 collisionVertex, boolean gap) {
            this.depth = depth;
            if (collisionVertex != null) {
                this.collisionVertex = collisionVertex;
            }
            if (vector != null) {
                this.axis.set(vector);
            }
            this.gap = gap;
        }

        public float getDepth() {
            return depth;
        }

        public Vector2 getAxis() {
            return axis;
        }

        public boolean isGap() {
            return gap;
        }

        public Vector2 getCollisionVertex() {
            return collisionVertex;
        }
    }

    private static class ContactPoint {

        private final float distance;
        private final Vector2 cp;

        public ContactPoint(float distance, Vector2 cp) {

            this.distance = distance;
            this.cp = cp;
        }
    }
}
