package analysis;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

class VectorAnalyzerTest {

    @Test
    void getAngleBetween() {
        //Given
        Point zero = new Point(0, 0);
        Point verticalVector = new Point(0, 1);
        Point horizontalVector = new Point(1, 0);
        Point squareDiagonal = new Point(1, 1);
        Point negativeSquareDiagonal = new Point(-1, -1);

        //Then
        assertEquals(90, VectorAnalyzer.getAngleBetween(verticalVector, horizontalVector));
        assertEquals(0, VectorAnalyzer.getAngleBetween(verticalVector, verticalVector));
        assertEquals(0, VectorAnalyzer.getAngleBetween(horizontalVector, horizontalVector));
        assertEquals(45, VectorAnalyzer.getAngleBetween(squareDiagonal, horizontalVector));
        assertEquals(135, VectorAnalyzer.getAngleBetween(negativeSquareDiagonal, horizontalVector));
        assertEquals(135, VectorAnalyzer.getAngleBetween(negativeSquareDiagonal, horizontalVector));
        assertEquals(135, VectorAnalyzer.getAngleBetween(zero, negativeSquareDiagonal));
        assertEquals(90, VectorAnalyzer.getAngleBetween(zero, verticalVector));
        assertEquals(0, VectorAnalyzer.getAngleBetween(zero, horizontalVector));
        assertEquals(0, VectorAnalyzer.getAngleBetween(zero, zero));

    }

    @Test
    void getAngleToHorizon() {
        //Given
        Point zero = new Point(0, 0);
        Point verticalVector = new Point(0, 1);
        Point horizontalVector = new Point(1, 0);
        Point squareDiagonal = new Point(1, 1);
        Point negativeSquareDiagonal = new Point(-1, -1);

        //Then
        assertEquals(90, VectorAnalyzer.getAngleToHorizon(verticalVector));
        assertEquals(0, VectorAnalyzer.getAngleToHorizon(horizontalVector));
        assertEquals(45, VectorAnalyzer.getAngleToHorizon(squareDiagonal));
        assertEquals(225, VectorAnalyzer.getAngleToHorizon(negativeSquareDiagonal));
        assertEquals(0, VectorAnalyzer.getAngleToHorizon(zero));
    }
}