package math

import kotlin.math.*

data class Vector2D(
	var x: Double,
	var y: Double
) {

	// Might need a mutex for functions that modify both x and y?

	infix fun angleTo(v: Vector2D): Double {
		return atan2(v.x - x, v.y - y)
	}

	infix fun angleToRad(v: Vector2D): Double {
		return atan2(v.x - x, v.y - y) / PI
	}

	infix fun rotateRad(a: Double) {
		rotate(a * PI)
	}

	infix fun rotate(a: Double): Vector2D {
		x = x * cos(a) + y * sin(a)
		y = -x * sin(a) + y * cos(a)
		return this
	}

	val a: Double
		get() = atan2(x, y)

	val l: Double
		get() = sqrt(x * x + y * y)

	operator fun plusAssign(v: Vector2D) {
		x += v.x
		y += v.y
	}

	operator fun minusAssign(v: Vector2D) {
		x -= v.x
		y -= v.y
	}

	operator fun times(d: Double): Vector2D {
		return vec(x * d, y * d)
	}

	operator fun timesAssign(d: Double) {
		x *= d
		y *= d
	}

	operator fun timesAssign(v: Vector2D) {
		x *= v.x
		y *= v.y
	}

	operator fun div(d: Double): Vector2D {
		return vec(x / d, y / d)
	}

	operator fun divAssign(d: Double) {
		x /= d
		y /= d
	}

	operator fun plus(v: Vector2D): Vector2D {
		return vec(x + v.x, y + v.y)
	}

	infix fun dist(v: Vector2D): Double {
		return sqrt((x - v.x).pow(2) + (y - v.y).pow(2))
	}

	val unit: Vector2D
		get() = if (l > 0.0) this / l else this

}

fun vec(x: Double, y: Double): Vector2D {
	return Vector2D(x, y)
}