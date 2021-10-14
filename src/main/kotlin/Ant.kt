import World.height
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import kotlin.math.*

class SensedComparitor : Comparator<Triple<Sensor, List<Trail>, Double>> {
    override fun compare(o1: Triple<Sensor, List<Trail>, Double>?, o2: Triple<Sensor, List<Trail>, Double>?): Int {
        if (o1 != null && o2 != null) {
            return (o2.third - o1.third).toInt()
        }
        return 0
    }
}

class Ant(var x: Double, var y: Double) {
    var heading: Double = Math.random() * 360
    var dropCooldown: Int = 0
    val sensors: Array<Sensor> = arrayOf(
        Sensor(this, -Config.SENSOR_OFFSET),
        Sensor(this, 0),
        Sensor(this, +Config.SENSOR_OFFSET)
    )

    fun valueTrails(trails: List<Trail>): Double {
        var sum = 0.0
        trails.forEach {
            sum += 1 - max(it.age, 0) / it.config.lifetime
        }
        return sum
    }

    fun handleSensors() {
        val sensed = sensors.map {
            val trails = it.sense(TrailType.home)
            Triple(it, trails, valueTrails(trails))
        }.sortedWith(SensedComparitor())
        val best = sensed[0]
//        println(best)
        if (best.third > Config.TRAIL_MIN_VALUE) {
            val turnDir = sign(best.first.offset.toDouble())
            heading += Config.trailConfigs[TrailType.home.ordinal].turnRate * turnDir
        }
    }

    fun draw(shapeRenderer: ShapeRenderer, sensorShapeRenderer: ShapeRenderer) {
        shapeRenderer.circle(x.toFloat(), height - y.toFloat(), Config.ANT_SIZE.toFloat())
        if (Config.DRAW_SENSORS) sensors.forEach { it.draw(sensorShapeRenderer) }

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

    fun update(width: Int, height: Int) {
        handleSensors()
        move(width, height)
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
