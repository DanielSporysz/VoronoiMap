package analysis;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EdgeTest {

    @Test
    public void compareTo() {
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
        assertEquals(-1, zero.compareTo(horizontal1));
        assertEquals(0, zero.compareTo(zero));
        assertEquals(0, vertical.compareTo(vertical));
        assertEquals(-1, vertical.compareTo(vertical2));
        assertEquals(-1, vertical.compareTo(horizontal2));
        assertEquals(-1, tilted.compareTo(horizontal2));
        assertEquals(-1, tilted.compareTo(vertical));
        assertEquals(0, tilted.compareTo(tilted));
    }

    @Test
    public void contains() {
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
        Edge tilted = new Edge(bottomLeft, topRight);

        //Then
        assertEquals(false, zero.contains(top));
        assertEquals(true, zero.contains(center));
        assertEquals(true, vertical.contains(top));
        assertEquals(false, vertical.contains(topRight));
        assertEquals(true, vertical2.contains(topFarRight));
        assertEquals(true, horizontal1.contains(right));
        assertEquals(true, horizontal1.contains(left));
        assertEquals(true, tilted.contains(center));
        assertEquals(true, tilted.contains(bottomLeft));
    }

    @Test
    public void getClosestPointTo() {
        //Given
        Point center = new Point(0, 0);
        Point left = new Point(-1, 0);
        Point right = new Point(1, 0);
        Point top = new Point(0, 1);
        Point down = new Point(0, -1);
        Point farTopFarRight = new Point(10, 10);
        Point farBottomFarRight = new Point(10, -10);
        Point inBetween = new Point(1, 0.5d);

        Edge zero = new Edge(center, center);
        Edge vertical = new Edge(down, top);
        Edge horizontal1 = new Edge(left, right);

        //Then
        assertEquals(center, zero.getClosestPointTo(top));
        assertEquals(top, vertical.getClosestPointTo(farTopFarRight));
        assertEquals(down, vertical.getClosestPointTo(farBottomFarRight));
        assertEquals(new Point(0, inBetween.y), vertical.getClosestPointTo(inBetween));
        assertEquals(new Point(inBetween.x, 0), horizontal1.getClosestPointTo(inBetween));
    }

}
