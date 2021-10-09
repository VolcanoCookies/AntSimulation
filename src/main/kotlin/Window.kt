import com.formdev.flatlaf.FlatDarkLaf
import javax.swing.JFrame
import javax.swing.UIManager

class Window(title: String) {
	init {
		UIManager.setLookAndFeel(FlatDarkLaf())
		
		val frame = JFrame(title)
		//frame.preferredSize = Dimension(width, height)
		//frame.minimumSize = Dimension(width, height)
		//frame.maximumSize = Dimension(width, height)
		frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
		frame.isResizable = false
		frame.add(Game.Game)
		frame.pack()
		frame.setLocationRelativeTo(null)
		frame.isVisible = true
		Game.Game.requestFocus()
	}
}