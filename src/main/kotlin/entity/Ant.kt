package entity

import Settings
import World
import entity.interfaces.Pheromone
import entity.interfaces.Positionable
import entity.interfaces.Renderable
import entity.interfaces.Tickable
import math.Matrix2D
import math.Vector2D
import math.vec
import new
import java.awt.Color
import java.awt.Graphics2D
import java.util.*
import kotlin.math.*

class Ant(
	override val pos: Vector2D,
	val vel: Vector2D
) : Tickable, Renderable, Positionable {

	private var carryFood = false

	companion object {

		val random = Random()
	}

	fun chunkCoords(): Pair<Int, Int> {
		val cx = floor(this@Ant.pos.x / Settings.chunkSize).toInt()
		val cy = floor(this@Ant.pos.y / Settings.chunkSize).toInt()

		return Pair(
			min(cx, Settings.width / Settings.chunkSize - 1),
			min(cy, Settings.height / Settings.chunkSize - 1)
		)
	}

	//private val pointsX = intArrayOf(-2, -3, 3, -3)
	//private val pointsY = intArrayOf(0, 3, 0, -3)

	/*
	val pointsXRotated: IntArray
		get() = IntArray(4) {
			(pos.x + (pointsX[it] * cos(-vel.a) + pointsY[it] * sin(-vel.a))).roundToInt()
		}

	val pointsYRotated: IntArray
		get() = IntArray(4) {
			(pos.y + (-pointsX[it] * sin(-vel.a) + pointsY[it] * cos(-vel.a))).roundToInt()
		}
	*/

	private fun sense(): Double {

		val pa: Matrix2D<Pheromone?>
		if (carryFood) pa = World.toHome
		else pa = World.toFood

		fun intensity(a: Double): Double {
			val p = pos + ((vel.unit * Settings.senseDistance) rotate a)
			val sx = max((p.x - Settings.senseDistance).roundToInt(), 0)
			val sy = max((p.y - Settings.senseDistance).roundToInt(), 0)
			val mx = min((p.x + Settings.senseDistance).roundToInt(), Settings.width - 1)
			val my = min((p.y + Settings.senseDistance).roundToInt(), Settings.height - 1)
			var intensity = 0.0
			for (x in (sx..mx)) {
				for (y in (sy..my)) {
					val ph = pa[x, y]
					if (ph != null && this dist ph < Settings.senseDistance && (ph.dir angleToRad vel).absoluteValue < 0.5) {
						intensity += ph.intensity
					}
				}
			}
			return intensity
		}

		fun intensityDirection(a: Double): Vector2D {
			val p = pos + ((vel.unit * Settings.senseDistance) rotate a)
			val sx = max((p.x - Settings.senseDistance).roundToInt(), 0)
			val sy = max((p.y - Settings.senseDistance).roundToInt(), 0)
			val mx = min((p.x + Settings.senseDistance).roundToInt(), Settings.width - 1)
			val my = min((p.y + Settings.senseDistance).roundToInt(), Settings.height - 1)
			val intensity = vec(0.0, 0.0)
			var sum = 0
			for (x in (sx..mx)) {
				for (y in (sy..my)) {
					val ph = pa[x, y]
					/*if (ph != null && this dist ph < Settings.senseDistance && (ph.dir angleToRad vel).absoluteValue < 0.5) {
						intensity += ph.intensity
					}*/
					if (ph != null) {
						intensity += ph.dir * ph.intensity
						sum++
					}
				}
			}
			return intensity / if (sum == 0) 1.0 else sum.toDouble()
		}

		val il = intensity(Settings.senseAngle)
		val ic = intensity(0.0)
		val ir = intensity(-Settings.senseAngle)

		var aMod = 0.0
		if (il > ic && il > ir) aMod = Settings.senseTurnFactor
		else if (ir > ic && ir > il) aMod = -Settings.senseTurnFactor

		return aMod

		//val dir = il + ir

		//return vel angleTo (vel.unit * Settings.senseDistance + dir.unit * Settings.senseDistance)
	}

	override fun render(g: Graphics2D) {
		g.new {
			it.color = if (carryFood) Color(150, 50, 50) else Color.BLACK
			it.fillRect(pos.x.roundToInt() - 2, pos.y.roundToInt() - 2, 5, 5)
		}
		if (Settings.drawAntHeading) {
			g.new {
				it.color = Color.RED
				val e = pos + vel.unit * 5.0
				it.drawLine(pos.x.toInt(), pos.y.toInt(), e.x.toInt(), e.y.toInt())
			}
		}
		if (Settings.drawAntSense) {
			g.new {
				fun draw(a: Double) {
					val v = pos + ((vel.unit * Settings.senseDistance) rotate a)

					it.drawOval(
						v.x.toInt() - Settings.senseRadius - 1,
						v.y.toInt() - Settings.senseRadius - 1,
						Settings.senseRadius * 2 + 1,
						Settings.senseRadius * 2 + 1
					)
				}

				it.color = Color.GREEN
				draw(0.0)
				it.color = Color.BLUE
				draw(Settings.senseAngle)
				it.color = Color.RED
				draw(-Settings.senseAngle)
			}

		}
	}

	override fun tick() {
		vel rotateRad (sense() + ((random.nextDouble() - 0.5) * 2 * Settings.randomnessFactor))

		pos += vel.unit * Settings.speed

		if (pos.x < 0) {
			pos.x = -pos.x
			vel *= vec(-1.0, 1.0)
		} else if (pos.x > Settings.width) {
			pos.x = 2 * Settings.width - pos.x
			vel *= vec(-1.0, 1.0)
		}
		if (pos.y < 0) {
			pos.y = -pos.y
			vel *= vec(1.0, -1.0)
		} else if (pos.y > Settings.height) {
			pos.y = 2 * Settings.height - pos.y
			vel *= vec(1.0, -1.0)
		}

		val rx = pos.x.toInt()
		val ry = pos.y.toInt()

		if (World.food[rx, ry] != null) {
			carryFood = true
			World.food[rx, ry]!!.left--
			vel *= -1.0
		} else if (carryFood) {
			val n = World.nests.firstOrNull() {
				this dist it < Nest.radius
			}
			if (n != null) {
				carryFood = false
				n.food++
				vel *= -1.0
			}
		}

		val pa: Matrix2D<Pheromone?>
		if (carryFood) pa = World.toFood
		else pa = World.toHome

		/*if (random.nextInt() % Settings.pheromoneInfrequency == 0) {
			if (carryFood) {
				val prev = pa[rx, ry]
				if (prev == null) {
					val p = FoodPheromone(rx, ry, Settings.pheromoneInitialIntensity, vel * -1.0)
					pa[rx, ry] = p
					World.toFoodIterable.add(p)
				} else {
					prev.intensity += Settings.pheromoneWalkoverIntensityBoost
				}
			} else {
				val prev = pa[rx, ry]
				if (prev == null) {
					val p = HomePheromone(rx, ry, Settings.pheromoneInitialIntensity, vel * -1.0)
					pa[rx, ry] = p
					World.toHomeIterable.add(p)
				} else {
					prev.intensity += Settings.pheromoneWalkoverIntensityBoost
				}
			}
		}*/

		if (carryFood) {
			val prev = pa[rx, ry]
			if (prev == null) {
				val p = FoodPheromone(rx, ry, Settings.pheromoneInitialIntensity, vel * -1.0)
				pa[rx, ry] = p
				World.toFoodIterable.add(p)
			} else {
				prev.intensity += Settings.pheromoneWalkoverIntensityBoost
			}
		} else {
			val prev = pa[rx, ry]
			if (prev == null) {
				val p = HomePheromone(rx, ry, Settings.pheromoneInitialIntensity, vel * -1.0)
				pa[rx, ry] = p
				World.toHomeIterable.add(p)
			} else {
				prev.intensity += Settings.pheromoneWalkoverIntensityBoost
			}
		}

	}

}