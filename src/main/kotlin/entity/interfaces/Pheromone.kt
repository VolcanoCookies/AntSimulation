package entity.interfaces

import Settings
import math.Vector2D
import new
import java.awt.Color
import java.awt.Graphics2D

interface Pheromone : Tickable, Renderable, MatrixPositionable {

	var intensity: Double

	val color: Color

	val dir: Vector2D

	override fun tick() {
		intensity -= intensity * Settings.pheromoneDegradeFactor
	}

	override fun render(g: Graphics2D) {
		g.new {
			it.color = color
			it.drawRect(x, y, 1, 1)
		}

	}

}

