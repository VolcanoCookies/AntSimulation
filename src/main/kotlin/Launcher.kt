import com.badlogic.gdx.backends.lwjgl.LwjglApplication
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration

class Launcher {

    companion object {

        @JvmStatic
        fun main(args: Array<String>) {

            val config = LwjglApplicationConfiguration()
            config.title = "Ants"
            config.width = 1600
            config.height = 800
            LwjglApplication(World, config)

        }

    }

}