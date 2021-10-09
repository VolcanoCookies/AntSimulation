import java.awt.Graphics2D

fun Graphics2D.new(func: (Graphics2D) -> Unit) {
	this.create().let {
		it as Graphics2D
		func.invoke(it)
		it.dispose()
	}
}
