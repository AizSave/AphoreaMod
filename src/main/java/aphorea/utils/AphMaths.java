package aphorea.utils;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class AphMaths {
    @NotNull
    @Contract(pure = true)
    static public float[] perpendicularVector(float p1x, float p1y, float p2x, float p2y) {
        float Vx = p1y - p2y;
        float Vy = -p1x + p2x;
        float[] vector = new float[2];
        vector[0] = Vx;
        vector[1] = Vy;

        return vector;
    }

    @NotNull
    @Contract(value = "_, _ -> new", pure = true)
    static public float[] normalVector(float Vx, float Vy) {
        float magnitude = (float) Math.sqrt(Vx * Vx + Vy * Vy);
        if (magnitude == 0) {
            return new float[]{0, 0}; // Retorna un vector nulo si la magnitud es 0
        }
        return new float[]{Vx / magnitude, Vy / magnitude};
    }

    @NotNull
    static public float[] perpendicularNormalVector(float p1x, float p1y, float p2x, float p2y) {
        float[] perpendicular = perpendicularVector(p1x, p1y, p2x, p2y);
        return normalVector(perpendicular[0], perpendicular[1]);
    }
}
