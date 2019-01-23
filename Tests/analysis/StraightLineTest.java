package analysis;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StraightLineTest {

    @Test
    void getIntersectionPoint() {
        //Given
        Point center = new Point(0, 0);
        Point left = new Point(-1, 0);
        Point right = new Point(1, 0);
        Point top = new Point(0, 1);
        Point down = new Point(0, -1);
        Point topRight = new Point(1, 1);
        Point bottomLeft = new Point(-1, -1);
        Point topFarRight = new Point(2, 1);
        Point farRight = new Point(2, 0);

        Edge zero = new Edge(center, center);
        Edge vertical = new Edge(down, top);
        Edge vertical2 = new Edge(farRight, topFarRight);
        Edge horizontal1 = new Edge(left, right);
        Edge horizontal2 = new Edge(right, left);
        Edge tilted = new Edge(bottomLeft, topRight);

        //Then
        assertEquals(center, vertical.getIntersectionPoint(horizontal1));
        assertEquals(center, vertical.getIntersectionPoint(horizontal2));
        assertEquals(null, vertical.getIntersectionPoint(vertical));
        assertEquals(null, vertical.getIntersectionPoint(vertical2));
        assertEquals(null, horizontal2.getIntersectionPoint(horizontal1));
        assertEquals(center, zero.getIntersectionPoint(horizontal1));
        assertEquals(center, zero.getIntersectionPoint(horizontal2));
        assertEquals(null, zero.getIntersectionPoint(vertical));
        assertEquals(null, zero.getIntersectionPoint(vertical2));
        assertEquals(center, tilted.getIntersectionPoint(horizontal1));
        assertEquals(center, tilted.getIntersectionPoint(vertical));
        assertEquals(new Point(2, 2), tilted.getIntersectionPoint(vertical2));
    }
}