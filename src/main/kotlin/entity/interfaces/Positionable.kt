package entity.interfaces

import math.Vector2D
import kotlin.math.pow
import kotlin.math.sqrt

interface Positionable {

	val pos: Vector2D

	infix fun dist(p: Positionable): Double {
		return this.pos dist p.pos
	}

	infix fun dist(mp: MatrixPositionable): Double {
		return sqrt((this.pos.x - mp.x).pow(2) + (this.pos.y - mp.y).pow(2))
	}

}

