import com.badlogic.gdx.math.Vector2
import ktx.math.plusAssign
import ktx.math.random

class Ant(
	x: Float,
	y: Float
) {

	val pos: Vector2 = Vector2(x, y)
	val vel: Vector2 = Vector2((-1f..1f).random(), (-1f..1f).random())

	var hasFood: Boolean = false

	fun move() {
		pos +=
	}

}