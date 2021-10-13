import World.height
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin

class Ant(var x: Double, var y: Double) {
    var heading: Double = Math.random() * 360
    var dropCooldown: Int = 0


    fun draw(shapeRenderer: ShapeRenderer) {
        shapeRenderer.circle(x.toFloat(), height - y.toFloat(), Config.ANT_SIZE.toFloat())
    }

    fun move(width: Int, height: Int) {
        if (heading > 360) heading -= 360
        if (heading < 0) heading += 360

        val dx = cos(rad(heading)) * Config.ANT_SPEED
        val dy = sin(rad(heading)) * Config.ANT_SPEED
        x = max(0.0, min(x + dx, width.toDouble()))
        y = max(0.0, min(y + dy, height.toDouble()))
        val vec = Vector().fromAngle(rad(heading))
        if (x == 0.0 || x == width.toDouble()) vec.x *= -1
        if (y == 0.0 || y == height.toDouble()) vec.y *= -1

        heading = deg(vec.toAngle())

        if (dropCooldown-- <= 0) {
            dropCooldown = Config.trailConfigs[TrailType.home.ordinal].dropRate
            World.addTrail(
                Trail(type = TrailType.home, x = x, y = y)
            )
        }
    }

    fun update(width: Int, height: Int, shapeRenderer: ShapeRenderer) {
        move(width, height)
        if (Config.DRAW_ANTS) draw(shapeRenderer)
        Chunk.get(x, y)?.let { it.fill = true }
        heading += (Math.random() * Config.WANDER_POWER * 2) - Config.WANDER_POWER
    }
}

fun rad(deg: Double): Double {
    return deg * Math.PI / 180
}

fun deg(rad: Double): Double {
    return rad * 180 / Math.PI
}
