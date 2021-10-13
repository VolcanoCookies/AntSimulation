import com.badlogic.gdx.math.MathUtils.clamp

class Trail(val type: TrailType, val x: Double, val y: Double) {
    constructor() : this(TrailType.home, 0.0, 0.0) {
        age = Int.MAX_VALUE
    }

    var age: Int = 0
    val lifetime: Double
        get() {
            return clamp(1.0 - (age.toDouble() / config.lifetime.toDouble()), 0.0, 1.0)
        }
    val config: TrailConfig = Config.trailConfigs[type.ordinal]

    fun isAlive(): Boolean {
        return age < config.lifetime
    }

    fun update() {
        age++
    }
}

enum class TrailType {
    home
}