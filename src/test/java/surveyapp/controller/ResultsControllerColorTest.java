package surveyapp.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ResultsControllerColorTest {

    @Autowired
    private ResultsController resultsController;

    @Test
    void testColorForRatio_Zero() {
        // When ratio = 0, should be red
        String color = (String) ReflectionTestUtils.invokeMethod(resultsController, "colorForRatio", 0.0);
        assertNotNull(color);
        assertTrue(color.contains("rgb") || color.contains("RGB"));
        assertTrue(color.contains("255")); // red channel should be high
    }

    @Test
    void testColorForRatio_Half() {
        // When ratio = 0.5, should be yellow-ish (red + green)
        String color = (String) ReflectionTestUtils.invokeMethod(resultsController, "colorForRatio", 0.5);
        assertNotNull(color);
        assertTrue(color.contains("rgba"));
    }

    @Test
    void testColorForRatio_One() {
        // When ratio = 1.0, should be green
        String color = (String) ReflectionTestUtils.invokeMethod(resultsController, "colorForRatio", 1.0);
        assertNotNull(color);
        assertTrue(color.contains("rgba"));
        assertTrue(color.contains("0,128,0")); // green RGB
    }

    @Test
    void testToRgba() {
        String rgba = (String) ReflectionTestUtils.invokeMethod(resultsController, "toRgba", 255, 0, 0, 0.5);
        assertEquals("rgba(255,0,0,0.50)", rgba);
    }

    @Test
    void testToRgba_BoundsClamping() {
        // Test that out-of-bounds values are clamped
        String rgba = (String) ReflectionTestUtils.invokeMethod(resultsController, "toRgba", 300, -10, 128, 1.5);
        assertEquals("rgba(255,0,128,1.00)", rgba);
    }
}

