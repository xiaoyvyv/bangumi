/*
 * Copyright(c) Live2D Inc. All rights reserved.
 *
 * Use of this source code is governed by the Live2D Open Software license
 * that can be found at http://live2d.com/eula/live2d-open-software-license-agreement_en.html.
 */

package com.live2d.sdk.cubism.framework.math;

/**
 * Utility class used for numerical calculations, etc.
 */
public class CubismMath {
    public static final float PI = 3.1415926535897932384626433832795f;
    public static final float EPSILON = 0.00001f;

    /**
     * Returns the value of the first argument in the range of minimum and maximum values.
     *
     * @param value target value
     * @param min lower bound
     * @param max upper bound
     * @return value within the range of minimum and maximum values
     */
    public static float rangeF(float value, float min, float max) {
        if (value < min) {
            value = min;
        } else if (value > max) {
            value = max;
        }
        return value;
    }

    /**
     * Caluculate sine value from radian angle value.
     *
     * @param x angle(radian)
     * @return sine value
     */
    public static float sinF(float x) {
        return (float) (Math.sin(x));
    }

    /**
     * Caluculate cosine value from radian angle value.
     *
     * @param x angle(radian)
     * @return cosine value
     */
    public static float cosF(float x) {
        return (float) (Math.cos(x));
    }

    /**
     * Calculate the absolute value.
     *
     * @param x target value
     * @return calculated absolute value
     */
    public static float absF(float x) {
        return Math.abs(x);
    }

    /**
     * Calculate the square root.
     *
     * @param x target value
     * @return calculated square root value
     */
    public static float sqrtF(float x) {
        return (float) (Math.sqrt(x));
    }

    /**
     * Calculate the easing processed sin. Can be used for easing during fade in/out.
     *
     * @param value target value
     * @return eased sin value
     */
    public static float getEasingSine(float value) {
        if (value < 0.0f) {
            return 0.0f;
        } else if (value > 1.0f) {
            return 1.0f;
        }
        return (float) (0.5f - 0.5f * Math.cos(value * PI));
    }

    /**
     * Convert an angle value to a radian value.
     *
     * @param degrees angle value[degree]
     * @return radian value converted from angle value
     */
    public static float degreesToRadian(float degrees) {
        return (degrees / 180.0f) * PI;
    }

    /**
     * Convert a radian value to an angle value
     *
     * @param radian radian value[rad]
     * @return angle value converted from radian value
     */
    public static float radianToDegrees(float radian) {
        return (radian * 180.0f) / PI;
    }

    /**
     * Calculate a radian value from two vectors.
     *
     * @param from position vector of a starting point
     * @param to position vector of an end point
     * @return direction vector calculated from radian value
     */
    public static float directionToRadian(CubismVector2 from, CubismVector2 to) {
        float q1 = (float) Math.atan2(to.y, to.x);
        float q2 = (float) Math.atan2(from.y, from.x);

        float radian = q1 - q2;

        while (radian < -PI) {
            radian += PI * 2.0f;
        }

        while (radian > PI) {
            radian -= PI * 2.0f;
        }

        return radian;
    }

    /**
     * Calculate an angle value from two vectors.
     *
     * @param from position vector of a starting point
     * @param to position vector of an end point
     * @return direction vector calculated from angle value
     */
    public static float directionToDegrees(CubismVector2 from, CubismVector2 to) {
        float radian = directionToRadian(from, to);
        float degree = radianToDegrees(radian);

        if ((to.x - from.x) > 0.0f) {
            degree = -degree;
        }

        return degree;
    }

    /**
     * Convert a radian value to a direction vector.
     *
     * @param totalAngle radian value
     * @param result CubismVector2 instance for storing calculation results
     * @return direction vector calculated from radian value
     */
    public static CubismVector2 radianToDirection(float totalAngle, CubismVector2 result) {
        result.x = sinF(totalAngle);
        result.y = cosF(totalAngle);

        return result;
    }

    /**
     * Calculate the solution to the quadratic equation.
     *
     * @param a coefficient value of quadratic term
     * @param b coefficient value of the first order term
     * @param c value of constant term
     * @return solution of a quadratic equation
     */
    public static float quadraticEquation(float a, float b, float c) {
        if (absF(a) < EPSILON) {
            if (absF(b) < EPSILON) {
                return -c;
            }
            return -c / b;
        }
        return -(b + sqrtF(b * b - 4.0f * a * c)) / (2.0f * a);
    }

    /**
     * Calculate the solution of the cubic equation corresponding to the Bezier's t-value by the Cardano's formula.
     * Returns a solution that has a value of 0.0~1.0 when it is a multiple solution.
     *
     * @param a coefficient value of cubic term
     * @param b coefficient value of quadratic term
     * @param c coefficient value of the first order term
     * @param d value of constant term
     * @return solution between 0.0~1.0
     */
    public static float cardanoAlgorithmForBezier(float a, float b, float c, float d) {
        if (absF(a) < EPSILON) {
            return rangeF(quadraticEquation(b, c, d), 0.0f, 1.0f);
        }
        float ba = b / a;
        float ca = c / a;
        float da = d / a;

        float p = (3.0f * ca - ba * ba) / 3.0f;
        float p3 = p / 3.0f;
        float q = (2.0f * ba * ba * ba - 9.0f * ba * ca + 27.0f * da) / 27.0f;
        float q2 = q / 2.0f;
        float discriminant = q2 * q2 + p3 * p3 * p3;

        final float center = 0.5f;
        final float threshold = center + 0.01f;

        if (discriminant < 0.0f) {
            float mp3 = -p / 3.0f;
            float mp33 = mp3 * mp3 * mp3;
            float r = sqrtF(mp33);
            float t = -q / (2.0f * r);
            float cosphi = rangeF(t,
                -1.0f, 1.0f);
            float phi = (float) Math.acos(cosphi);
            float crtr = (float) Math.cbrt(r);
            float t1 = 2.0f * crtr;

            float root1 = t1 * cosF(phi / 3.0f) - ba / 3.0f;
            if (Math.abs(root1 - center) < threshold) {
                return rangeF(root1, 0.0f, 1.0f);
            }

            float root2 = t1 * cosF((phi + 2.0f * PI) / 3.0f) - ba / 3.0f;
            if (Math.abs(root2 - center) < threshold) {
                return rangeF(root2, 0.0f, 1.0f);
            }

            float root3 = t1 * cosF((phi + 4.0f * PI) / 3.0f) - ba / 3.0f;
            return rangeF(root3, 0.0f, 1.0f);
        }

        if (discriminant == 0.0f) {
            float u1;
            if (q2 < 0.0f) {
                u1 = (float) Math.cbrt(-q2);
            } else {
                u1 = (float) -Math.cbrt(q2);
            }

            float root1 = 2.0f * u1 - ba / 3.0f;
            if (Math.abs(root1 - center) < threshold) {
                return rangeF(root1, 0.0f, 1.0f);
            }

            float root2 = -u1 - ba / 3.0f;
            return rangeF(root2, 0.0f, 1.0f);
        }

        float sd = sqrtF(discriminant);
        float u1 = (float) Math.cbrt(sd - q2);
        float v1 = (float) Math.cbrt(sd + q2);
        float root1 = u1 - v1 - ba / 3.0f;
        return rangeF(root1, 0.0f, 1.0f);
    }

    /**
     * private constructor.
     * (Prevent instantiation.)
     */
    private CubismMath() {}
}
