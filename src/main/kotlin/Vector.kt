import kotlin.math.acos
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

class Vector(var x: Double = 0.0, var y: Double = 0.0) {

    /* INSTANCE METHODS */

    fun negative(): Vector {
        x = -x;
        y = -y;
        return this;
    }

    fun add(v: Double): Vector {
        x += v;
        y += v;

        return this;
    }

    fun add(v: Vector): Vector {
        x += v.x;
        y += v.y;

        return this;
    }

    fun subtract(v: Double): Vector {
        x -= v;
        y -= v;

        return this;
    }

    fun subtract(v: Vector): Vector {
        x -= v.x;
        y -= v.y;

        return this;
    }

    fun multiply(v: Double): Vector {
        x *= v;
        y *= v;

        return this;
    }

    fun multiply(v: Vector): Vector {
        x *= v.x;
        y *= v.y;

        return this;
    }

    fun divide(v: Double): Vector {
        if (v != 0.0) {
            x /= v;
            y /= v;
        }
        return this;
    }

    fun divide(v: Vector): Vector {
        if (v.x != 0.0) x /= v.x;
        if (v.y != 0.0) y /= v.y;

        return this;
    }

    fun equals(v: Vector): Boolean {
        return x == v.x && y == v.y;
    }

    fun dot(v: Vector): Double {
        return x * v.x + y * v.y;
    }

    fun cross(v: Vector): Double {
        return x * v.y - y * v.x;
    }

    fun length(): Double {
        return Math.sqrt(dot(this));
    }

    fun normalize(): Vector {
        return divide(length());
    }

    fun min(): Double {
        return Math.min(x, y);
    }

    fun max(): Double {
        return Math.max(x, y);
    }

    fun fromAngle(angle: Double): Vector {
        return set(cos(angle), sin(angle)).normalize();
    }

    fun toAngle(): Double {
        return -atan2(-y, x);
    }

    fun angleTo(a: Vector): Double {
        return acos(dot(a) / (length() * a.length()));
    }

    fun clone(): Vector {
        return Vector(x, y);
    }

    fun set(x: Double, y: Double): Vector {
        this.x = x;
        this.y = y;
        return this;
    }

}