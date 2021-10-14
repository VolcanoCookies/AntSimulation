import World.height
import World.width
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import kotlin.math.pow
import kotlin.math.sqrt

fun dist(x1: Double, y1: Double, x2: Double, y2: Double) = sqrt((x1 - x2).pow(2) + (y1 - y2).pow(2));
fun dist(x1: Int, y1: Int, x2: Int, y2: Int) = sqrt((x1 - x2).toDouble().pow(2) + (y1 - y2).toDouble().pow(2));

class Sensor(private val ant: Ant, val offset: Int) {
    fun calcPos(): Vector {
        val angle = rad(ant.heading + offset)
        val vec = Vector(ant.x, ant.y)
        vec.add(Vector().fromAngle(angle).multiply(Config.SENSOR_DIST.toDouble()))
        return vec
    }

    fun draw(shapeRenderer: ShapeRenderer) {
        val pos = calcPos()
        shapeRenderer.circle(pos.x.toFloat(), height - pos.y.toFloat(), Config.SENSOR_SIZE.toFloat())
    }

    fun sense(target: TrailType): List<Trail> {
        val pos = calcPos()
        pos.subtract(Vector((Config.SENSOR_SIZE / 2).toDouble(), (Config.SENSOR_SIZE / 2).toDouble()))
        val x = pos.x
        val y = pos.y
        val hits: ArrayList<Trail> = ArrayList()
        Sensor.grid.forEach {
            val sx = x.toInt() + it.first
            val sy = y.toInt() + it.second
            if (sx > 0 && sy > 0 && sx < width && sy < height) {
                val trail = World.trailGrid[sy * width + sx]
                if (trail != null) hits.add(trail)
            }
        }
        return hits

//        val chunk = Chunk.get(pos.x, pos.y) ?: return ArrayList()
//        val trails = chunk.getAreaTrails()
//        return trails.filter { it.type == target && dist(pos.x, pos.y, it.x, it.y) < Config.SENSOR_SIZE }
    }

    companion object {
        var grid = setup()
        private fun setup(): ArrayList<Pair<Int, Int>> {
            val points: ArrayList<Pair<Int, Int>> = ArrayList()
            for (y in 0..Config.CHUNK_SIZE) {
                for (x in 0..Config.CHUNK_SIZE) {
                    if (dist(x, y, Config.CHUNK_SIZE / 2, Config.CHUNK_SIZE / 2) < Config.SENSOR_DIST) {
                        points.add(Pair(x, y))
                    }
                }
            }
            return points
        }
    }
}