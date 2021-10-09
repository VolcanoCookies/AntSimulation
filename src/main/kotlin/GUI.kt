import java.awt.Color
import java.awt.Graphics2D
import java.util.*

class GUI {

	var information: List<String> = LinkedList()

	fun render(g: Graphics2D) {

		for (i in information.indices) {
			val bounds = g.font.getStringBounds(information[i], g.fontRenderContext)
			g.color = Color.WHITE
			g.fillRect(3, 1 + 14 * i, bounds.width.toInt(), bounds.height.toInt())
			g.color = Color.BLACK
			g.drawString(information[i], 3, 1 + 14 * (i + 1))
		}

	}

	fun tick() {
		val list: MutableList<String> = ArrayList()

		list.add("FPS: " + Statistics.framesPerSecond)
		list.add("TPS: " + Statistics.ticksPerSecond)
		list.add("ETPS: " + Settings.tickRate)
		list.add("Avg Render: ${Statistics.renderTime}ms")
		list.add("Avg Tick: ${Statistics.tickTime}ms")

		if (Settings.tickPaused) {
			list.add("Paused")
		}

		information = list
	}

}