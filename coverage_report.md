# SensorDataProcessor Code Coverage Report

## Coverage Summary

| Metric | Achievement | Status |
| :--- | :--- | :--- |
| **Statement Coverage** | 100% | ✅ Full |
| **Branch Coverage** | 95.8% (23/24) | ⚠️ Max Reachable |
| **Methods** | 100% | ✅ Full |

## Unreachable Code Analysis

While statement coverage is at 100%, total branch coverage cannot reach 100% due to a theoretically impossible condition in the source code.

### 1. Dead Sub-expression (Line 53)
**Code:**
```java
else if (Math.pow(Math.abs(data[i][j][k]), 3) < Math.pow(Math.abs(data2[i][j][k]), 3)
        && average(data[i][j]) < data2[i][j][k] && (i + 1) * (j + 1) > 0)
```
- **Branch:** The false case of `(i + 1) * (j + 1) > 0`.
- **Reason:** Both `i` and `j` are loop indices starting from `0` and incrementing. 
  - `i >= 0` → `i + 1 >= 1`
  - `j >= 0` → `j + 1 >= 1`
  - Their product `(i + 1) * (j + 1)` is therefore mathematically guaranteed to be `>= 1`.
  - The condition `> 0` will **always** evaluate to `true`.
- **Impact:** JaCoCo identifies a missed branch for the `false` outcome of this expression, which is physically impossible to exercise without modifying the code.

### 2. Multi-Part Boolean Short-Circuiting
The conditions in `Branch A` (`average > 10 && average < 50`) and `Branch C` are fully covered across their true/false outcomes for the component predicates, except for the one noted above.

## Test Strategy Applied
- **Reflective Directory Injection:** To trigger the `catch (Exception e)` block on line 73, the test suite creates a directory with the same name as the target output file (`RacingStatsData.txt`). This causes the the `FileWriter` constructor to throw an `IOException`, which is caught and handled by the class.
- **Data Sculpting:** Specific values (including negative numbers and carefully calculated averages) were used to steer execution into every reachable logical branch.

## Conclusion
The project has reached the maximum possible logical coverage (100% statements, 100% reachable branches). The single missing branch is an artifact of redundant logic in the original source code which was requested to remain unaltered.
