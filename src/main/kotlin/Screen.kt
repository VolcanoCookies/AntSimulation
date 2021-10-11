import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import ktx.app.KtxScreen
import ktx.graphics.use
import java.util.*
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.properties.Delegates

class Screen : KtxScreen {

	val vertexShader = Gdx.files.internal("assets/vertex.shader")
	val fragmentShader = Gdx.files.internal("assets/fragment.shader")
	val shaderProgram = ShaderProgram(vertexShader, fragmentShader)

	val shapeRenderer = ShapeRenderer(1024, shaderProgram).apply {
		color = Color.RED
	}

	val ants: MutableList<Ant> = LinkedList<Ant>().let { l ->
		repeat(1000000) {
			l += Ant(200f, 200f)
		}
		l
	}

	var rad: Float = 0f

	var width by Delegates.notNull<Int>()
	var height by Delegates.notNull<Int>()

	var time: Float = 0f

	override fun render(delta: Float) {
		time = (time + delta) % 3
		rad = (rad + 0.5f * delta) % 2f
		val x = (width / 2 + 100 * cos(rad * PI)).toFloat()
		val y = (height / 2 + 100 * sin(rad * PI)).toFloat()
		shapeRenderer.use(ShapeRenderer.ShapeType.Filled) {
			shaderProgram.bind()
			shaderProgram.setUniformf("u_width", width.toFloat())
			shaderProgram.setUniformf("u_height", height.toFloat())
			//shaderProgram.setUniformf("x_center", x + 50f)
			//shaderProgram.setUniformf("y_center", y + 50f - height.toFloat())
			ants.forEach { ant ->
				ant.move()
				it.rect(ant.pos.x, ant.pos.y, 5f, 5f)
			}
		}
	}

	override fun resize(width: Int, height: Int) {
		this.width = width
		this.height = height
	}

	override fun dispose() {
		shapeRenderer.dispose()
	}

}