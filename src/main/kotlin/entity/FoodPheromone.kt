package entity

import Settings
import entity.interfaces.Pheromone
import math.Vector2D
import java.awt.Color
import kotlin.math.min

class FoodPheromone(
	override val x: Int,
	override val y: Int,
	override var intensity: Double,
	override val dir: Vector2D
) : Pheromone {

	override val color: Color
		get() = Color(150, 40, 255, min(((intensity / Settings.pheromoneInitialIntensity) * 255).toInt(), 255))

}