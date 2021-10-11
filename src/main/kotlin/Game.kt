import ktx.app.KtxGame

class Game : KtxGame<Screen>() {

	override fun create() {
		addScreen(Screen())
		setScreen<Screen>()
	}

}