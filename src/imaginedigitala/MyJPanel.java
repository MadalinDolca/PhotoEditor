package imaginedigitala;

import java.awt.Graphics;
import javax.swing.JPanel;

public class MyJPanel extends JPanel {

    Contrast contrast;

    MyJPanel(Contrast contrast) {
        this.contrast = contrast;
    }

    /**
     * Deseneaza graficul pe ecran.
     * 
     * @param g 
     */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        contrast.afiseaza(g);
    }

}
