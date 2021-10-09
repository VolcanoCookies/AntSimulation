package entity

import entity.interfaces.MatrixPositionable
import entity.interfaces.Renderable
import entity.interfaces.Tickable
import java.awt.Graphics2D

class Nest(
	override val x: Int,
	override val y: Int
) : Tickable, Renderable, MatrixPositionable {

	companion object {

		const val radius = 15
	}

	var food = 0

	override fun render(g: Graphics2D) {
		g.drawOval(x - radius - 1, y - radius - 1, radius * 2 + 1, radius * 2 + 1)
		g.drawString("$food", x, y)
	}

	override fun tick() {
		TODO("Not yet implemented")
	}

}