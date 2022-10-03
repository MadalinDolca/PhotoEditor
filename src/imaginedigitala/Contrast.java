package imaginedigitala;

import static imaginedigitala.Interfata.gp;
import java.awt.Color;
import java.awt.Graphics;

/**
 * Clasa pentru graficul de contrast liniar.
 *
 * @author Madalin
 */
public class Contrast {

    Punct a, b, selectat = null;
    Graphics g;

    /**
     * Primul punct din panel.
     *
     * @param x coordonata x
     * @param y coordonata y
     */
    void setA(int x, int y) {
        a.x = x;
        a.y = y;
    }

    /**
     * Al doilea punct din panel.
     *
     * @param x coordonata x
     * @param y coordonata y
     */
    void setB(int x, int y) {
        b.x = x;
        b.y = y;
    }

    /**
     * Initializeaza cele doua puncte din panel.
     *
     * @param a primul punct
     * @param b al doilea punct
     */
    Contrast(Punct a, Punct b) {
        this.a = a;
        this.b = b;
    }

    /**
     * Verifica daca mouse-ul se apropie de punctul din panel si afiseaza
     * cercul.
     *
     * @param xm coordonata x a mouse-ului
     * @param ym coordonata y a mouse-ului
     */
    void selecteaza(int xm, int ym) {
        if (a.aproape(xm, ym)) {
            selectat = a;
            a.afiseaza();
            return;
        } else {
            a.sterge();
        }

        if (b.aproape(xm, ym)) {
            selectat = b;
            b.afiseaza();
            return;
        } else {
            b.sterge();
        }

        selectat = null;
    }

    /**
     * Deseneaza cele trei linii din panel si cele doua puncte.
     *
     * @param g
     */
    public void deseneaza(Graphics g) {
        g.drawLine(0, 255, a.x, 255 - a.y);
        g.drawLine(a.x, 255 - a.y, b.x, 255 - b.y);
        g.drawLine(255, 0, b.x, 255 - b.y);
        g.fillOval(a.x - 3, 255 - a.y - 3, 6, 6);
        g.fillOval(b.x - 3, 255 - b.y - 3, 6, 6);
    }

    /**
     * Deseneaza cercul daca punctul este selectat, linia diagonala verde si
     * apeleaza functia de desenare a elementelor graficului in panel.
     *
     * @param g
     */
    void afiseaza(Graphics g) {
        this.g = g; // La primul apel salveaza g.

        if (selectat != null) {
            selectat.afiseaza();
        }

        g.setColor(Color.green); // Traseaza diagonala.
        g.drawLine(0, 255, 255, 0);
        g.setColor(Color.black);

        deseneaza(g);
    }

    /**
     * Apeleaza functia de stergere a cercului din jurul punctului si
     * redeseneaza elementele graficului apeland functia de desenare.
     */
    void sterge() {
        selectat.sterge();
        gp.setColor(Color.white);
        deseneaza(gp);
    }

    /**
     * Actualizarea graficului la mutarea punctelor din panel.
     *
     * @param xm coordonata x a mouse-ului din miscare
     * @param ym coordonata y a mouse-ului din miscare
     */
    void deplaseaza(int xm, int ym) {
        if (selectat == null) {
            return;
        }

        sterge();

        selectat.x = xm;
        selectat.y = ym;

        // 'a' trebuie sa ramana la stanga lui 'b'
        if (selectat == a && a.x >= b.x) {
            a.x = b.x;
        }

        if (selectat == b && b.x <= a.x) {
            b.x = a.x;
        }

        afiseaza(gp);
    }
}
