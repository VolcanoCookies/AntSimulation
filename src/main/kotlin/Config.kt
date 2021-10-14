import com.badlogic.gdx.graphics.Color

object Config {
    val ANT_SIZE = 3
    val ANT_SPEED = 1
    val NUM_ANTS = 500
    val WANDER_POWER = 10

    val trailConfigs: ArrayList<TrailConfig> = arrayListOf(
        TrailConfig(color = Color(0F, 0.8F, 0F, 1F), dropRate = 0)
    )
    val TRAIL_MIN_VALUE = 1.0

    val SENSOR_SIZE = 8
    val SENSOR_OFFSET = 60
    val SENSOR_DIST = 20

    val CHUNK_SIZE = 25


    val DRAW_TRAILS = false
    val DRAW_CHUNKS = false
    val DRAW_SENSORS = false
    val DRAW_ANTS = true
}

data class TrailConfig(
    val lifetime: Int = 500,
    val minAgeMult: Double = 3.0,
    val color: Color = Color(1F, 1F, 1F, 1F),
    val turnRate: Double = 1.5,
    val dropRate: Int = 0
)