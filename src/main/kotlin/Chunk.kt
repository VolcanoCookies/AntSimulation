import World.height
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import kotlin.math.floor

class Chunk(public val x: Int, public val y: Int) {
    var fill: Boolean = false
    val nabs: ArrayList<Chunk> = ArrayList()
    var trails: MutableList<Trail> = ArrayList(Config.CHUNK_SIZE * Config.CHUNK_SIZE)

    fun isIn(x: Double, y: Double): Boolean {
        val local = Chunk.toLocal(x, y)
        return local.x == x && local.y == y
    }

    fun draw(shapeRender: ShapeRenderer) {
        val size = Config.CHUNK_SIZE
        shapeRender.color = Color(0.2F, 0.2F, 0.2F, 1F)
        shapeRender.rect((x * size).toFloat(), height - ((y + 1) * size).toFloat(), size.toFloat(), size.toFloat())


        if (fill) {
            val shapeRenderFilled = ShapeRenderer()
            shapeRenderFilled.begin(ShapeRenderer.ShapeType.Filled)
            shapeRenderFilled.color = Color(0.2F, 0.2F, 0.2F, 1F)
            shapeRenderFilled.rect(
                (x * size).toFloat(),
                height - ((y + 1) * size).toFloat(),
                size.toFloat(),
                size.toFloat()
            )
            shapeRenderFilled.end()
            fill = false
        }
    }

    fun setNabs() {
        repeat(3) { offY ->
            repeat(3) { offX ->
                val chunk = getLocal(x + offX - 1, y + offY - 1);
                chunk?.let { nabs.add(it) }
            }
        }
    }

    fun getAreaTrails(): MutableList<Trail> {
        val trails: MutableList<Trail> = ArrayList<Trail>().toMutableList()
        nabs.forEach {
            trails += it.trails
        }
        return trails
    }

    fun getTrailGrid(): MutableList<Trail?> {
        val gridTrails: MutableList<Trail?> = MutableList(Config.CHUNK_SIZE * Config.CHUNK_SIZE) { null }
        trails.forEach {
            val subX = it.x.toInt() - x * Config.CHUNK_SIZE
            val subY = it.y.toInt() - y * Config.CHUNK_SIZE
            gridTrails[subY * Config.CHUNK_SIZE + subX] = it
        }
        return gridTrails
    }

    companion object {
        fun get(x: Double, y: Double): Chunk? {
            val local = toLocal(x, y)
            return getLocal(local.x.toInt(), local.y.toInt())
        }

        fun getLocal(x: Int, y: Int): Chunk? {
            if (x < 0 || y < 0 || y >= World.chunks.size || x >= World.chunks[y].size) {
                return null
            }
            return World.chunks[y][x]
        }

        fun toLocal(x: Double, y: Double): Vector {
            return Vector(floor(x / Config.CHUNK_SIZE), floor(y / Config.CHUNK_SIZE))
        }
    }
}