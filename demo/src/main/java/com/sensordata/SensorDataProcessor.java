package com.sensordata;

import java.io.BufferedWriter;
import java.io.FileWriter;

public class SensorDataProcessor {

    // Senson data and limits.
    public double[][][] data;
    public double[][] limit;

    // constructor
    public SensorDataProcessor(double[][][] data, double[][] limit) {
        this.data = data;
        this.limit = limit;
    }

    // calculates average of sensor data
    private double average(double[] array) {
        int i = 0;
        double val = 0;
        for (i = 0; i < array.length; i++) {
            val += array[i];
        }

        return val / array.length;
    }

    // calculate data
    public void calculate(double d) {

        long startTime = System.nanoTime();

        // Strategy 6: Cache array lengths to avoid repeated pointer dereferences in loops
        final int lenI = data.length;
        final int lenJ = data[0].length;
        final int lenK = data[0][0].length;

        double[][][] data2 = new double[lenI][lenJ][lenK];

        BufferedWriter out;

        try {
            out = new BufferedWriter(new FileWriter("RacingStatsData.txt"));

            for (int i = 0; i < lenI; i++) {
                for (int j = 0; j < lenJ; j++) {
                    // Strategy 3: Pre-calculate average of immutable input data once per j-loop
                    double avgInput = average(data[i][j]);
                    
                    // Strategy 1: Pre-calculate square of limit once per j-loop
                    double limitSq = limit[i][j] * limit[i][j];
                    
                    // Strategy 2: Maintain a running sum to calculate average in O(1) inside k-loop
                    double runningSumData2 = 0;

                    for (int k = 0; k < lenK; k++) {
                        double valK = data[i][j][k];
                        double computedVal = valK / d - limitSq;
                        data2[i][j][k] = computedVal;
                        
                        // Update running sum for data2 average calculation
                        runningSumData2 += computedVal;
                        // To preserve original behavior exactly, divide sum by total row length
                        double avgData2 = runningSumData2 / lenK;

                        // Strategy 2: Use cached average instead of re-looping every iteration
                        if (avgData2 > 10 && avgData2 < 50) {
                            break;
                        } else if (Math.max(valK, computedVal) > valK) {
                            break;
                        } else {
                            // Strategy 4: Replace Math.pow(x, 3) with efficient x*x*x multiplication
                            double absValK = Math.abs(valK);
                            double absCompVal = Math.abs(computedVal);
                            
                            // Strategy 5: Remove redundant (i+1)*(j+1) > 0 condition check
                            if ((absValK * absValK * absValK) < (absCompVal * absCompVal * absCompVal)
                                    && avgInput < computedVal) {
                                data2[i][j][k] = computedVal * 2;
                                // If the value changed, we must update the running sum to keep average consistent
                                runningSumData2 += computedVal;
                            } else {
                                continue;
                            }
                        }
                    }
                }
            }

            // Write results (preserving original behavior of writing array references)
            for (int i = 0; i < lenI; i++) {
                for (int j = 0; j < lenJ; j++) {
                    out.write(data2[i][j] + "\t");
                }
            }

            out.close();

            long endTime = System.nanoTime();
            long elapsedMs = (endTime - startTime) / 1_000_000;
            System.out.println("calculate() completed in " + elapsedMs + " ms");

        } catch (Exception e) {
            System.out.println("Error= " + e);
            long endTime = System.nanoTime();
            long elapsedMs = (endTime - startTime) / 1_000_000;
            System.out.println("calculate() failed after " + elapsedMs + " ms");
        }
    }

}