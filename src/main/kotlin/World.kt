import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Gdx2DPixmap
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.MathUtils.random
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.job
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import ktx.app.KtxGame
import org.lwjgl.BufferUtils
import org.lwjgl.util.Rectangle
import java.nio.ByteBuffer
import kotlin.math.pow
import kotlin.system.measureTimeMillis
import kotlin.time.ExperimentalTime


object World : KtxGame<Screen>() {
    lateinit var screen: Screen
    var width: Int = 0
    var height: Int = 0

    val ants: ArrayList<Ant> = ArrayList()
    var trails: ParLinkedList<Trail> = ParLinkedList(10000)
    val chunks: ArrayList<ArrayList<Chunk>> = ArrayList()

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

//        val x = screen.width / 2
//        val y = screen.height / 2

        initChunks(screen.width, screen.height)

        repeat(Config.NUM_ANTS) {
            val x = random.nextInt(width)
            val y = random.nextInt(height)
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
        val trailGridIdx = trail.y.toInt() * width + trail.x.toInt()
        if (trailGridIdx < 0 || trailGridIdx > trailGrid.size) return

        val existing = trailGrid[trailGridIdx]
        if (existing != null && existing.type == trail.type) {
            existing.age -= trail.config.lifetime
        } else {
            if (existing != null) {
                existing.age = existing.config.lifetime + 1
            }

            trails.add(trail)
            trailGrid[trailGridIdx] = trail

            val idx = trail.y.toInt() * width * 4 + trail.x.toInt() * 4
            val color = trail.config.color
            trailTextureArr[idx + 0] = (color.r * 255).toInt().toByte()
            trailTextureArr[idx + 1] = (color.g * 255).toInt().toByte()
            trailTextureArr[idx + 2] = (color.b * 255).toInt().toByte()
            trailTextureArr[idx + 3] = (255).toByte()
        }
    }

    fun doTrailUpdate() = GlobalScope.launch {
        for (chunk in trails.chunks) {
            launch {
                val itt = chunk.iterator()
                while (itt.hasNext()) {
                    val trail = itt.next()
                    if (trail.isAlive()) {
                        trail.update()
                        val idx = trail.y.toInt() * width * 4 + trail.x.toInt() * 4
                        trailTextureArr[idx + 3] = (trail.lifetime.pow(0.4) * 255).toInt().toByte()

                        if (!trail.isAlive()) {
                            trailGrid[trail.y.toInt() * width + trail.x.toInt()] = null
                            trailTextureArr[idx + 0] = 0
                            trailTextureArr[idx + 1] = 0
                            trailTextureArr[idx + 2] = 0
                            trailTextureArr[idx + 3] = 0
                            itt.remove()
                        }
                    }
                }
            }
        }
    }

    fun updateTrails() {
        runBlocking {
            doTrailUpdate().job.join()
            trails.sync()
        }
    }

    fun doAntUpdate() = GlobalScope.launch {
        ants.forEach {
//            launch {
//            it.forEach {
            it.update(width, height)
//            }
//            }
        }
    }

    fun updateAnts() {
        runBlocking {
            doAntUpdate().job.join()
        }
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

    fun drawAnts() {
        if (Config.DRAW_ANTS) {
            val sensorShapeRender = ShapeRenderer()
            sensorShapeRender.begin(ShapeRenderer.ShapeType.Line)
            sensorShapeRender.color = Color(0.4F, 0.4F, 0.4F, 1F)

            val antShapeRender = ShapeRenderer()
            antShapeRender.begin(ShapeRenderer.ShapeType.Filled)
            antShapeRender.color = Color(1F, 1F, 1F, 1F)

            ants.forEach {
                it.draw(antShapeRender, sensorShapeRender)
            }
            antShapeRender.end()
            sensorShapeRender.end()
        }
    }

    @OptIn(ExperimentalTime::class, kotlinx.coroutines.DelicateCoroutinesApi::class)
    fun run(width: Int, height: Int) {
        this.width = width
        this.height = height
        val frametime = measureTimeMillis {
            val chunkShapeRender = ShapeRenderer()
            chunkShapeRender.begin(ShapeRenderer.ShapeType.Line)
            if (Config.DRAW_CHUNKS) chunks.forEach { row -> row.forEach { it.draw(chunkShapeRender) } }
            chunkShapeRender.end()

            updateTrails()
            drawTrails()
            updateAnts()
            drawAnts()
        }

        println("Fame time: ${frametime}ms / ${1000 / frametime}fps")
    }

}