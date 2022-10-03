package imaginedigitala;

import static imaginedigitala.Interfata.gp;
import java.awt.Color;

public class Punct {

    int x, y; // coordonatele unui punct

    public Punct(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Ne spune daca mouse-ul este in apropierea punctului.
     *
     * @param xm coodonata x a mouse-ului
     * @param ym coodonata y a mouse-ului
     * @return <code>true</code> daca pozitia de pe ordonata si abscisa valorii
     * absolute (modulul) este mai mic decat 5
     */
    boolean aproape(int xm, int ym) {
        return Math.abs(x - xm) < 5 && Math.abs(y - ym) < 5;
    }

    /**
     * Deseneaza cu alb cercul din jurul punctului.
     */
    void sterge() {
        gp.setColor(Color.white);
        deseneaza();
    }

    /**
     * Deseneaza cu rosu cercul din jurul punctului.
     */
    void afiseaza() {
        gp.setColor(Color.red);
        deseneaza();
    }

    /**
     * Afiseaza cercul din jurul punctului.
     */
    void deseneaza() {
        gp.drawOval(x - 10, 255 - y - 10, 20, 20);
    }
}
