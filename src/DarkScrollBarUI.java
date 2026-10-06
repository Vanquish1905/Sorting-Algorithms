import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;

public class DarkScrollBarUI extends BasicScrollBarUI {

    protected JButton createDecreaseButton(int orientation) {
        return emptyButton();
    }

    protected JButton createIncreaseButton(int orientation) {
        return emptyButton();
    }

    private JButton emptyButton() {
        JButton b = new JButton();
        b.setPreferredSize(new Dimension(0, 0));
        return b;
    }

    protected void paintTrack(Graphics g, JComponent c, Rectangle r) {
        g.setColor(Theme.PANEL);
        g.fillRect(r.x, r.y, r.width, r.height);
    }

    protected void paintThumb(Graphics g, JComponent c, Rectangle r) {
        g.setColor(Theme.BORDER);
        g.fillRoundRect(r.x + 2, r.y + 2, r.width - 4, r.height - 4, 6, 6);
    }
}
