package com.sensordata;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.*;
import java.nio.file.*;

/**
 * Unit tests for SensorDataProcessor.
 *
 * Branch map for calculate() inner k-loop:
 *   Branch A: average(data2[i][j]) > 10 && < 50  → break
 *   Branch B: Math.max(data, data2) > data        → break  (means data2 > data)
 *   Branch C: pow(|data|,3) < pow(|data2|,3) &&
 *             average(data[i][j]) < data2[i][j][k] &&
 *             (i+1)*(j+1) > 0                    → data2 *= 2
 *   Branch D: else                                → continue
 *   Catch:    IO exception from FileWriter        → prints error
 */
class SensorDataProcessorTest {

    private static final String OUTPUT_FILE = "RacingStatsData.txt";

    @AfterEach
    void cleanup() {
        new File(OUTPUT_FILE).delete();
    }

    @Test
    @DisplayName("Constructor stores data and limit references")
    void testConstructor() {
        double[][][] data  = { { { 1.0, 2.0 } } };
        double[][]   limit = { { 0.5 } };
        SensorDataProcessor sdp = new SensorDataProcessor(data, limit);
        assertSame(data,  sdp.data);
        assertSame(limit, sdp.limit);
    }

    @Test
    @DisplayName("Branch A: average of data2 row is in (10,50) → break inner k loop")
    void testCalculate_branchA() {
        double[][][] data  = { { { 30.0 } } };
        double[][]   limit = { { 0.0 } };
        SensorDataProcessor sdp = new SensorDataProcessor(data, limit);
        assertDoesNotThrow(() -> sdp.calculate(1.0));
        assertTrue(new File(OUTPUT_FILE).exists());
    }

    @Test
    @DisplayName("Branch B: data2 > data (Math.max detects it) → break inner k loop")
    void testCalculate_branchB() {
        double[][][] data  = { { { 1.0 } } };
        double[][]   limit = { { 0.0 } };
        SensorDataProcessor sdp = new SensorDataProcessor(data, limit);
        assertDoesNotThrow(() -> sdp.calculate(0.1));
        assertTrue(new File(OUTPUT_FILE).exists());
    }

    @Test
    @DisplayName("Branch C: |data| < |data2| && avg(data) < data2 && (i+1)(j+1)>0 → data2 *= 2")
    void testCalculate_branchC() {
        double[][][] data  = { { { -5.0, -100.0 } } };
        double[][]   limit = { { 0.0 } };
        SensorDataProcessor sdp = new SensorDataProcessor(data, limit);
        assertDoesNotThrow(() -> sdp.calculate(5.0 / 6.0));
        assertTrue(new File(OUTPUT_FILE).exists());
    }

    @Test
    @DisplayName("Branch D: all conditions false → else (continue)")
    void testCalculate_branchD_continue() {
        double[][][] data  = { { { 5.0 } } };
        double[][]   limit = { { 0.0 } };
        SensorDataProcessor sdp = new SensorDataProcessor(data, limit);
        assertDoesNotThrow(() -> sdp.calculate(1.0));
        assertTrue(new File(OUTPUT_FILE).exists());
    }

    @Test
    @DisplayName("Catch block: IOException from FileWriter → error message printed")
    void testCalculate_catchBlock_ioException() throws IOException {
        Path dir = Paths.get(OUTPUT_FILE);
        Files.createDirectories(dir);
        try {
            double[][][] data  = { { { 5.0 } } };
            double[][]   limit = { { 0.0 } };
            SensorDataProcessor sdp = new SensorDataProcessor(data, limit);
            assertDoesNotThrow(() -> sdp.calculate(1.0));
        } finally {
            Files.deleteIfExists(dir);
        }
    }

    @Test
    @DisplayName("Multi-group/sensor: exercises outer i and j loops")
    void testCalculate_multipleGroups() {
        int g = 2, s = 2, r = 3;
        double[][][] data  = new double[g][s][r];
        double[][]   limit = new double[g][s];
        for (int i = 0; i < g; i++)
            for (int j = 0; j < s; j++) {
                limit[i][j] = 0.0;
                for (int k = 0; k < r; k++)
                    data[i][j][k] = 5.0;
            }
        SensorDataProcessor sdp = new SensorDataProcessor(data, limit);
        assertDoesNotThrow(() -> sdp.calculate(1.0));
        assertTrue(new File(OUTPUT_FILE).exists());
    }

    @Test
    @DisplayName("Branch A alt: average >= 50 (the other \"false\" side of the AND)")
    void testCalculate_branchA_avgAbove50() {
        double[][][] data  = { { { 100.0 } } };
        double[][]   limit = { { 0.0 } };
        SensorDataProcessor sdp = new SensorDataProcessor(data, limit);
        assertDoesNotThrow(() -> sdp.calculate(1.0));
        assertTrue(new File(OUTPUT_FILE).exists());
    }
}
