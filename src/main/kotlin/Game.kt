import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.awt.*
import kotlin.system.measureTimeMillis

class Game(gfx: GraphicsConfiguration) : Canvas(gfx) {

	companion object {

		@JvmStatic
		fun main(args: Array<String>) {
			Game.start()
		}

		val Game = Game(
			GraphicsEnvironment.getLocalGraphicsEnvironment()
				.defaultScreenDevice
				.defaultConfiguration
		)
		val window = Window("Ants")
		val settingsWindow = SettingsWindow()
	}

	override fun getSize(): Dimension {
		return Dimension(Settings.width, Settings.height)
	}

	private val gui = GUI()

	fun start() {
		GlobalScope.run {
			GlobalScope.launch { tickLoop() }
			GlobalScope.launch { renderLoop() }
		}
	}

	suspend fun tickLoop() {
		var lastTime = System.nanoTime()
		var delta = 0.0
		var timer = System.currentTimeMillis()
		var updates = 0
		while (Settings.running) {
			val ns: Double = 1000000000.0 / Settings.tickRate
			val now = System.nanoTime()
			delta += (now - lastTime) / ns
			lastTime = now
			while (delta >= 1) {
				gui.tick()
				if (!Settings.tickPaused) {
					val t = measureTimeMillis {
						World.tick()
					}
					Statistics.tickTime.registerSample(t)
				}
				updates++
				delta--
			}
			if (System.currentTimeMillis() - timer > 1000) {
				timer += 1000
				Statistics.ticksPerSecond = updates
				updates = 0
			}
		}
	}

	suspend fun renderLoop() {
		var timer = System.currentTimeMillis()
		var frames = 0
		while (Settings.running) {
			val t = measureTimeMillis {
				render()
			}
			Statistics.renderTime.registerSample(t)
			frames++
			if (System.currentTimeMillis() - timer > 1000) {
				timer += 1000
				Statistics.framesPerSecond = frames
				frames = 0
			}
		}
	}

	suspend fun render() {
		val bs = bufferStrategy
		if (bs == null) {
			createBufferStrategy(2)
			return
		}
		val g = bs.drawGraphics as Graphics2D
		g.color = Color.WHITE
		g.fillRect(0, 0, width, height)
		g.color = Color.BLACK

		g.translate(Settings.xOffset, Settings.yOffset)
		g.scale(Settings.scale, Settings.scale)

		// Render World
		g.create().let {
			World.render(it as Graphics2D)
			it.dispose()
		}
		// Render Gui
		if (Settings.drawStatistics)
			g.create().let {
				gui.render(it as Graphics2D)
				it.dispose()
			}

		g.dispose()
		bs.show()
	}

}