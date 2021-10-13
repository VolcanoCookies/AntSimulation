import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Gdx2DPixmap
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import ktx.app.KtxGame
import org.lwjgl.BufferUtils
import org.lwjgl.util.Rectangle
import java.nio.ByteBuffer
import java.util.*
import kotlin.system.measureTimeMillis
import kotlin.time.ExperimentalTime


fun abgr(color: Color): Color {
    return Color(color.a, color.b, color.g, color.r)
}

object World : KtxGame<Screen>() {
    lateinit var screen: Screen
    var width: Int = 0
    var height: Int = 0

    val ants: ArrayList<Ant> = ArrayList()
    var trails: LinkedList<Trail> = LinkedList()
    val chunks: ArrayList<ArrayList<Chunk>> = ArrayList()

    var trailCleanCooldown = 0
    lateinit var trailGrid: MutableList<Trail?>
    lateinit var trailTextureArr: ByteArray
    lateinit var trailBuffer: ByteBuffer


    override fun create() {
        screen = Screen()
        addScreen(screen)
        setScreen<Screen>()

        width = screen.width
        height = screen.height
        trailGrid = MutableList(width * height) { null }
        trailTextureArr = ByteArray(width * height * 4) { 0 }
        trailBuffer = BufferUtils.createByteBuffer(width * height * 4)

        val x = screen.width / 2
        val y = screen.height / 2

        initChunks(screen.width, screen.height)

        repeat(Config.NUM_ANTS) {
            ants.add(Ant(x.toDouble(), y.toDouble()))
        }
    }


    fun initChunks(width: Int, height: Int) {
        var x = 0
        var y = 0
        while (y < height) {
            chunks.add(ArrayList())
            while (x < width) {
                val local = Chunk.toLocal(x.toDouble(), y.toDouble())
                val chunk = Chunk(local.x.toInt(), local.y.toInt())
                chunks[local.y.toInt()].add(chunk)
                x += Config.CHUNK_SIZE
            }
            x = 0
            y += Config.CHUNK_SIZE
        }

        chunks.forEach { row -> row.forEach { it.setNabs() } }
    }

    fun addTrail(trail: Trail) {
        val chunk = Chunk.get(trail.x, trail.y)
        chunk?.let {
            val trailGridIdx = trail.y.toInt() * width + trail.x.toInt()
            val existing = trailGrid[trailGridIdx]
            if (existing != null && existing.type == trail.type) {
                existing.age -= trail.config.lifetime
            } else {
                if (existing != null) {
                    it.trails.remove(existing)
                    trails.remove(existing)
                }

                it.trails.add(trail)
                trails.add(trail)
                trailGrid[trailGridIdx] = trail

                val idx = trail.y.toInt() * width * 4 + trail.x.toInt() * 4
                trailTextureArr[idx + 0] = (255).toByte()
                trailTextureArr[idx + 1] = (255).toByte()
                trailTextureArr[idx + 2] = (255).toByte()
                trailTextureArr[idx + 3] = (255).toByte()
            }
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun updateTrails() {
        val trailUpdateTime = measureTimeMillis {
            runBlocking {
                trails.chunked(10000).forEach {
                    launch {
                        it.forEach {
                            if (it.isAlive()) {
                                it.update()
                                val idx = it.y.toInt() * width * 4 + it.x.toInt() * 4
                                trailTextureArr[idx + 3] = (it.lifetime * 255).toInt().toByte()

                                if (!it.isAlive()) {
                                    trailGrid[it.y.toInt() * width + it.x.toInt()] = null
                                    trailTextureArr[idx + 0] = 0
                                    trailTextureArr[idx + 1] = 0
                                    trailTextureArr[idx + 2] = 0
                                    trailTextureArr[idx + 3] = 0
                                }
                            }
                        }
                    }
                }
            }

            if (trailCleanCooldown-- <= 0) {
                val itt = trails.iterator()
                while (itt.hasNext()) {
                    val trail = itt.next()
                    if (!trail.isAlive()) itt.remove()
                }
                trailCleanCooldown = Config.CLEAN_RATE
            }
        }

        println("Trails: ${trailUpdateTime}ms for ${trails.size / 1000}k")
    }

    fun drawTrails() {
        trailBuffer.put(trailTextureArr)
        trailBuffer.rewind()

        val pixmap = Gdx2DPixmap(
            trailBuffer,
            longArrayOf(0, width.toLong(), height.toLong(), Gdx2DPixmap.GDX2D_FORMAT_RGBA8888.toLong())
        )
        val texture = Texture(Pixmap(pixmap))
        val batch = SpriteBatch()
        val trailDraw = Rectangle(0, 0, width, height)
        batch.begin()
        batch.draw(texture, trailDraw.x.toFloat(), trailDraw.y.toFloat())
        batch.draw(texture, trailDraw.x.toFloat(), trailDraw.y.toFloat())
        batch.end()
        texture.dispose()
    }

    @OptIn(ExperimentalTime::class, kotlinx.coroutines.DelicateCoroutinesApi::class)
    fun run(width: Int, height: Int) {
        this.width = width
        this.height = height
        val chunkUpdateTime = measureTimeMillis {
            val chunkShapeRender = ShapeRenderer()
            chunkShapeRender.begin(ShapeRenderer.ShapeType.Line)
            if (Config.DRAW_CHUNKS) chunks.forEach { row -> row.forEach { it.draw(chunkShapeRender) } }
            chunkShapeRender.end()
        }

        updateTrails()

        drawTrails()

        val antUpdateTime = measureTimeMillis {
            val antShapeRender = ShapeRenderer()
            antShapeRender.begin(ShapeRenderer.ShapeType.Filled)
            antShapeRender.color = Color(1F, 1F, 1F, 1F)
            ants.forEach { it.update(width, height, antShapeRender) }
            antShapeRender.end()
        }

    }

}