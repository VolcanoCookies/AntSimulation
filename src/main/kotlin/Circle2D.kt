import math.Vector2D
import math.vec

data class Circle2D(
	var pos: Vector2D,
	var rad: Double
) {

}

fun cir(x: Double, y: Double, a: Double): Circle2D {
	return Circle2D(vec(x, y), a)
}