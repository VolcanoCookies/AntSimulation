import ktx.app.KtxScreen
import kotlin.properties.Delegates

class Screen : KtxScreen {
    var width by Delegates.notNull<Int>()
    var height by Delegates.notNull<Int>()

    override fun render(delta: Float) {
        World.run(width, height)
    }

    override fun resize(width: Int, height: Int) {
        this.width = width
        this.height = height
    }

    override fun dispose() {
    }

}