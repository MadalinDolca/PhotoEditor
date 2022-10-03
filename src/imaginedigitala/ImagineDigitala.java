package imaginedigitala;

import static imaginedigitala.Interfata.jCheckBoxAlbNegru;
import static imaginedigitala.Interfata.jCheckBoxR;
import static imaginedigitala.Interfata.jCheckBoxG;
import static imaginedigitala.Interfata.jCheckBoxB;
import static imaginedigitala.Interfata.jComboBoxZonaDilatare;
import static imaginedigitala.Interfata.jPanelHistogramaB;
import static imaginedigitala.Interfata.jPanelHistogramaG;
import static imaginedigitala.Interfata.jPanelHistogramaR;
import static imaginedigitala.Interfata.jPanelHistogramaY;
import static imaginedigitala.Interfata.id;
import static imaginedigitala.Interfata.idImagineRegiuni;
import static imaginedigitala.Interfata.idImagineSecundaraMontaj;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.ColorModel;
import java.awt.image.MemoryImageSource;
import java.awt.image.PixelGrabber;
import java.util.Arrays;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class ImagineDigitala {

    static final int DIM = 512; // dimensiunea / rezolutia

    // Imaginea originala
    public float y[][] = new float[DIM][DIM]; // luminozitatea (rezolutia originala)
    public float c1[][] = new float[DIM / 2][DIM / 2]; // crominanta 1 (injumatatire pe verticala & orizontala)
    public float c2[][] = new float[DIM / 2][DIM / 2]; // crominanta 2 (injumatatire pe verticala & orizontala)

    // Imaginea modificata
    public float ym[][] = new float[DIM][DIM];
    public float c1m[][] = new float[DIM / 2][DIM / 2];
    public float c2m[][] = new float[DIM / 2][DIM / 2];

    int pixeliImagine[] = new int[DIM * DIM]; // Imaginea originala pastrata in spatiul de culoare RGB
    int pixeliImagineM[] = new int[DIM * DIM]; // Imaginea modificata in RGB

    //static 
    float s[][] = new float[DIM][DIM]; // Pentru salvari

    PixelGrabber grabber; // citire poza de pe disc (retrieve a subset of the pixels in that image)
    ColorModel CM = ColorModel.getRGBdefault(); // specificare spatiu de culoare pentru reprezentare imagine
    JFrame interfata; // interfata de lucru a obiectului

    /**
     * Initializeaza campul interfata si urmeaza citirea datelor de pe disc.
     *
     * @param fisierPoza numele fisierului in care se gaseste poza
     * @param interfata
     * @throws InterruptedException
     */
    ImagineDigitala(String fisierPoza, JFrame interfata) throws InterruptedException {
        this.interfata = interfata; // manipulare JFrame "Interfata"
        ImageIcon ii = new ImageIcon(fisierPoza); // imaginea de lucru

        // Argumente:
        // ImageProducer,
        // 0, 0=coordonatele coltului din stanga sus,
        // DIM, DIM = latime, inaltime,
        // pixeliImagine = tabloul in care se pun datele imaginii,
        // 0 = offsetul la care se plaseaza primul pixel,
        // Lungimea unei linii (tabloul nu e bidimensional).
        grabber = new PixelGrabber(ii.getImage().getSource(), 0, 0, DIM, DIM, pixeliImagine, 0, DIM); // citire poza de pe disc (retrieve a subset of the pixels in that image)

        grabber.grabPixels(); // citeste datele din fisier si plaseaza pixelii imaginii in tabloul pixeliImagine

        System.arraycopy(pixeliImagine, 0, pixeliImagineM, 0, DIM * DIM); // Salvare in pixeliImagineM. Va fi util pentru afisarea histogramelor RGB
        separaCulori(pixeliImagine); // conversie RGB -> YC1C2
    }

    // 1. LUMINANTA - CROMINANTA ///////////////////////////////////////////////////////////////////////////////
    /**
     * Afiseaza imaginea in jLabel si apeleaza functiile
     * <code>histograma()</code> si <code>histogramaRGB()</code>pentru afisarea
     * histogramelor.
     *
     * @param pozaRGB tabloul in care sunt datele
     * @param eticheta unde se afiseaza imaginea
     */
    void afiseaza(int[] pozaRGB, JLabel eticheta) {
        // Plaseaza imaginea din pozaRGB pe eticheta.
        Image imagine = interfata.createImage(new MemoryImageSource(DIM, DIM, pozaRGB, 0, DIM));
        // Argumente: 
        // latime, inaltime, pixeli, offset, lungimea unui rand de pixeli.

        eticheta.setIcon(new ImageIcon(imagine));

        // Afiseaza histogramele	
        histograma();
        histogramaRGB();
    }

    /**
     * Converteste imaginea intr-un spatiu de culoare tip luminanta-crominanta
     * si plaseaza cele 3 componente rezultate in tablourile y, c1 si c2 (dar si
     * ym, c1m si c2m).
     *
     * @param tabPixeli tabloul in care se afla pixelii imaginii
     */
    void separaCulori(int[] tabPixeli) {
        int r, g, b;

        for (int i = 0; i < DIM; i++) {
            for (int j = 0; j < DIM; j++) {
                // r,g,b reprezinta componentele cromatice ale pixelului curent
                r = CM.getRed(tabPixeli[i * DIM + j]);
                g = CM.getGreen(tabPixeli[i * DIM + j]);
                b = CM.getBlue(tabPixeli[i * DIM + j]);

                // calculeaza luminozitatea pixelului curent (y)
                y[i][j] = ym[i][j] = 0.299f * r + 0.587f * g + 0.114f * b;

                // componentele de crominanta sunt pastrate la rezolutie 
                // injumatatita. Fiecare element al tablourilor c1 si c2 contine 
                // media pentru 4 pixeli invecinati.
                if (i % 2 == 0 && j % 2 == 0) {
                    c1[i / 2][j / 2] = c1m[i / 2][j / 2] = 0; // initializare cu 0
                    c2[i / 2][j / 2] = c2m[i / 2][j / 2] = 0;
                }

                // altfel, calculeaza media grupului de 4 pixeli invecinati
                c1[i / 2][j / 2] += (0.5f * r - 0.2f * g - 0.3f * b) / 4;
                c2[i / 2][j / 2] += (0.3f * r + 0.4f * g - 0.7f * b) / 4;

                // copiere valori crominante in matricele pentru modificare
                c1m[i / 2][j / 2] = c1[i / 2][j / 2];
                c2m[i / 2][j / 2] = c2[i / 2][j / 2];
                // matricea de transformare:
                // 0.299   +0.587  +0.114      suma liniei = 1
                // 0.5     -0.2    -0.3        suma liniei = 0
                // 0.3     +0.4    -0.7        suma liniei = 0
            }
        }
    }

    /**
     * Corecteaza eventualele erori la capete in caz de depasire interval (0,
     * 255).
     *
     * @param v valoarea de corectat
     * @return valoarea corectata
     */
    int corectCapete(int v) {
        if (v < 0) {
            return 0;
        }

        if (v > 255) {
            return 255;
        }

        return v;
    }

    /**
     * Transforma din y, c1, c2 in r, g, b. Completeaza tabloul
     * <code>pixeliImagine</code> cu datele luate din tablourile ym, c1m, c2m.
     *
     * @param albNegru stadiul de selectie a jCheckBoxAlbNegru pentru a decide
     * daca sa ne neutralizele crominantele c1 si c2
     */
    void compuneCulori(boolean albNegru) {
        // Imaginea se gaseste in ym, c1m, c2m.
        int r, g, b;
        float y, c1, c2;

        for (int i = 0; i < DIM; i++) {
            for (int j = 0; j < DIM; j++) {
                y = ym[i][j]; // preluare valoare luminozitate pixel din matricea de modificare

                if (albNegru) { // daca jCheckBoxAlbNegru este bifat
                    c1 = c2 = 0; // neutralizam crominantele
                } else { // altfel folosim crominantele
                    c1 = c1m[i / 2][j / 2];
                    c2 = c2m[i / 2][j / 2];
                }

                // daca checkbox-urile sunt selectate punem componenta R/G/B,
                // altfel punem 0 (nu afisam, nu tinem cont de ea)
                r = jCheckBoxR.isSelected() ? corectCapete(Math.round(y + 1.756f * c1 - 0.590f * c2)) : 0;
                g = jCheckBoxG.isSelected() ? corectCapete(Math.round(y - 0.937f * c1 + 0.564f * c2)) : 0;
                b = jCheckBoxB.isSelected() ? corectCapete(Math.round(y + 0.217f * c1 - 1.359f * c2)) : 0;
                // Inversa matricei de transformare:
                //  1   +1.756   -0.590
                //  1   -0.937   +0.564
                //  1   +0.217   -1.359

                // Fiecare pixel e reprezentat pe 24 de biti:
                // primii 8 reprezinta opacitatea, 
                // urmatorii 8 reprezinta componenta r, urm. 8 g, ultimii 8 b.
                pixeliImagineM[i * DIM + j] = 0xff000000; // opacitatea 255
                pixeliImagineM[i * DIM + j] |= r << 16;   // componenta r
                pixeliImagineM[i * DIM + j] |= g << 8;    // componenta g
                pixeliImagineM[i * DIM + j] |= b;         // componenta b
                // mai scurt: pixeliImagine[i*DIM+j] = r<<16 | g<<8 | b | 0xff000000;
            }
        }
    }

    void compuneCuloriClick(boolean albNegru, int xp, int yp, int grosimeCreion) {
        int r, g, b;
        float y, c1, c2;

        for (int i = (yp - grosimeCreion > 0 ? yp - grosimeCreion : 0); i < (yp + grosimeCreion < DIM ? yp + grosimeCreion : DIM); i++) {
            for (int j = (xp - grosimeCreion > 0 ? xp - grosimeCreion : 0); j < (xp + grosimeCreion < DIM ? xp + grosimeCreion : DIM); j++) {
                y = ym[i][j];

                if (albNegru) {
                    c1 = c2 = 0;
                } else {
                    c1 = c1m[i / 2][j / 2];
                    c2 = c2m[i / 2][j / 2];
                }

                r = jCheckBoxR.isSelected() ? corectCapete(Math.round(y + 1.756f * c1 - 0.590f * c2)) : 0;
                g = jCheckBoxG.isSelected() ? corectCapete(Math.round(y - 0.937f * c1 + 0.564f * c2)) : 0;
                b = jCheckBoxB.isSelected() ? corectCapete(Math.round(y + 0.217f * c1 - 1.359f * c2)) : 0;

                pixeliImagineM[i * DIM + j] = 0xff000000; // opacitatea 255
                pixeliImagineM[i * DIM + j] |= r << 16;   // componenta r
                pixeliImagineM[i * DIM + j] |= g << 8;    // componenta g
                pixeliImagineM[i * DIM + j] |= b;         // componenta b
            }
        }
    }

    // 2. HSV ///////////////////////////////////////////////////////////////////////////////
    /**
     * Actioneaza asupra saturatiei si a luminozitatii.
     *
     * @param ks valoare saturatie din slider
     * @param kv valoare luminozitate HSV din slider
     */
    void satureaza(int ks, int kv) {
        float nivels = ks / 100.f, nivelv = kv / 100.f; // impartire nivel saturatie si luminozitate HSV la 100 pentru obtinere valori intre -1 si 1
        float[] hsv = new float[3]; // tablou memorare hue, saturation, brightness
        int r, g, b;

        for (int i = 0; i < DIM; i++) {
            for (int j = 0; j < DIM; j++) {
                // preluare componente cromatice pixel curent
                r = CM.getRed(pixeliImagine[i * DIM + j]);
                g = CM.getGreen(pixeliImagine[i * DIM + j]);
                b = CM.getBlue(pixeliImagine[i * DIM + j]);

                // conversia din RGB in spatiul de culoare HSB (HSV)
                hsv = Color.RGBtoHSB(r, g, b, hsv);

                // Ajustare saturatie:
                hsv[1] += nivels; // adaugare nivel saturatie
                if (hsv[1] > 1) {
                    hsv[1] = 1;
                }
                if (hsv[1] < 0) {
                    hsv[1] = 0;
                }

                // Ajustare luminozitate:
                hsv[2] += nivelv; // adaugare nivel luminozitate
                if (hsv[2] > 1) {
                    hsv[2] = 1;
                }
                if (hsv[2] < 0) {
                    hsv[2] = 0;
                }

                // Conversie din HSV/HSB in spatiul de culoare RGB:
                pixeliImagineM[i * DIM + j] = Color.HSBtoRGB(hsv[0], hsv[1], hsv[2]);
            }
        }

        separaCulori(pixeliImagineM); // reimprospatare y, c1, c2, ym, c1m, c2m
        // Necesar pentru afisarea histogramei ym.
    }

    /**
     * Desatureaza imaginea in functie de valoarea data in slider.
     *
     * @param k valoarea din slider
     */
    void desatureaza(double k) {
        int r, g, b, poz;

        for (int i = 0; i < DIM; i++) {
            for (int j = 0; j < DIM; j++) {
                // pozitia pixelului curent
                poz = DIM * i + j;

                // extragere componente cromatice pixel curent
                r = CM.getRed(pixeliImagine[poz]);
                g = CM.getGreen(pixeliImagine[poz]);
                b = CM.getBlue(pixeliImagine[poz]);

                // corectie valori
                r = corectCapete((int) (r + k * (r - y[i][j])));
                g = corectCapete((int) (g + k * (g - y[i][j])));
                b = corectCapete((int) (b + k * (b - y[i][j])));

                // Salvare rezultate desaturatie in vector
                // Fiecare pixel e reprezentat pe 24 de biti: opacitate, r, g, b
                pixeliImagineM[poz] = 0xff000000 | r << 16 | g << 8 | b;
            }
        }
        // | r |   | r |       | r - y |   k = -1 => fara culori
        // | g | = | g | + k * | g - y |   k = 0 => nemodificata
        // | b |   | b |       | b - y |   k = 1 => saturata

        separaCulori(pixeliImagineM); // reimprospatare y, c1, c2, ym, c1m, c2m
        // Necesar pentru afisarea histogramei ym.
    }

    /**
     * Copiere componenta 'ym' in 'y', 'cm1' in 'c1', 'cm2' in 'c2',
     * "pixeliImagineM" in "pixeliImagine".
     */
    void salveazaMO() {
        System.arraycopy(pixeliImagineM, 0, pixeliImagine, 0, DIM * DIM);
        salveaza(); // ym, c1m, c2m --> y, c1, c2
    }

    /**
     * Salveaza imaginea modificata din ym, c1m, c2m in y, c1, c2 aplicand trei
     * <code>copiaza(float[][] dest, float[][] sursa, int n)</code>
     *
     * @see imaginedigitala.ImagineDigitala#copiaza(float[][], float[][], int)
     */
    void salveaza() {
        // Salveaza imaginea modificata.
        copiaza(y, ym, DIM);
        copiaza(c1, c1m, DIM / 2);
        copiaza(c2, c2m, DIM / 2);
    }

    /**
     * Copiaza componentele de luminozitate si crominanta dintr-un vector in
     * altul.
     *
     * @param dest matricea in care se salveaza noile valori
     * @param sursa matricea din care se vor prelua valorile
     * @param n dimensiunea matricei
     */
    void copiaza(float[][] dest, float[][] sursa, int n) {
        for (int i = 0; i < n; i++) {
            System.arraycopy(sursa[i], 0, dest[i], 0, n);
        }
    }

    /**
     * Copiere componenta 'y' in 'ym', 'c1' in 'cm1', 'c2' in 'cm2',
     * "pixeliImagine" in "pixeliImagineM".
     */
    void salveazaOM() {
        reface(); // y, c1, c2 --> ym, c1m, c2m.
        compuneCulori(jCheckBoxAlbNegru.isSelected());
    }

    void reface() {
        copiaza(ym, y, DIM);
        copiaza(c1m, c1, DIM / 2);
        copiaza(c2m, c2, DIM / 2);
    }

    // 3. CONTRAST - LUMINOZITATE ///////////////////////////////////////////////////////////////////////////////
    /**
     * Ajusteaza contrastul imaginii in functie de valoarea maxima si minima de
     * luminozitate din pixelii vectorului imagine.
     */
    void contrastAutomat() {
        float max = 0, min = 255; // intervalele de culoare

        // cauta valoarea maxima si valoarea minima de luminozitate din pixelii vectorului imagine
        for (int i = 0; i < DIM; i++) {
            for (int j = 0; j < DIM; j++) {
                if (y[i][j] < min) {
                    min = y[i][j];
                }

                if (y[i][j] > max) {
                    max = y[i][j];
                }
            }
        }

        // aplicare contrast automat pe fiecare pixel din vector
        for (int i = 0; i < DIM; i++) {
            for (int j = 0; j < DIM; j++) {
                // pixelul cel mai luminos va primii valoarea 255
                // pixelul cel mai intunecat va primii valoarea 0
                // ceilalti pixeli se ajusteaza automat
                ym[i][j] = (y[i][j] - min) * 255 / (max - min);

                // (min-min)*255/(max-min) => 0
                // (max-min)*255/(max-min) => 255
            }
        }

        compuneCulori(jCheckBoxAlbNegru.isSelected());
    }

    /**
     * Aplica efectul de contrast liniar pe fiecare pixel din tabloul de
     * luminanta in functie de valorile din grafic.
     *
     * @param a coordonata x a primului punct
     * @param fa coordonata y a primului punct
     * @param b coordonata x a celui de al doilea punct
     * @param fb coordonata y a celui de al doilea punct
     */
    void contrastLin(int a, int fa, int b, int fb) {
        float xp, yp;

        for (int i = 0; i < DIM; i++) {
            for (int j = 0; j < DIM; j++) {
                xp = y[i][j]; // preluare valoare pixel curent

                if (xp < a) { // x [0, a); daca valoarea pixelului curent este mai mica decat cea a valorii coordonatei x a primului punct
                    yp = fa / a * xp; // calcul valoare luminanta
                } else if (xp < b) { // x [a,b); daca valoarea pixelului curent este mai mica decat cea a valorii coordonatei x a celui de al doilea punct
                    yp = (fb - fa) * (xp - a) / (b - a) + fa; // calcul valoare luminanta
                } else { // x (b, MAX=255]
                    yp = (255 - fb) * (xp - b) / (255 - b) + fb; // calcul valoare luminanta
                }

                ym[i][j] = yp; // actualizare valoare luminanta in tabelul ym
            }
        }

        compuneCulori(jCheckBoxAlbNegru.isSelected());
    }

    /**
     * Modifica componenta 'y' pentru fiecare pixel din imagine adaugand
     * valoarea din slider.
     *
     * @param nLumi nivelul de luminozitate din slider
     */
    void ajustareLuminozitate(int nLumi) {
        for (int i = 0; i < DIM; i++) {
            for (int j = 0; j < DIM; j++) {
                ym[i][j] = nLumi + y[i][j];
            }
        }
        compuneCulori(jCheckBoxAlbNegru.isSelected());
    }

    /**
     * Ajustare contrast sinusoidal.
     *
     * @param nContrast nivelul de ajustare a contrastului (amplitudinea,
     * valoarea din slider)
     */
    void ajustareContrast(int nContrast) {
        // f(x) = x - a * sin(x * (2PI / MAX))
        // x = valoarea luminozitatii pixelului curent        
        // a = nivelul de ajustare a contrastului (amplitudinea)
        // MAX = nivelul maxim de luminozitate

        float MAX = 255, x;

        for (int i = 0; i < DIM; i++) {
            for (int j = 0; j < DIM; j++) {
                x = y[i][j]; // valoarea luminozitatii pixelului curent
                ym[i][j] = x - (float) (nContrast * Math.sin(x * 2 * Math.PI / MAX));
            }
        }

        compuneCulori(jCheckBoxAlbNegru.isSelected());
    }

    // 4. BINARIZARE - NEGATIVARE - DILATARE ///////////////////////////////////////////////////////////////////////////////
    /**
     * Aplica efectul de binarizare pe fiecare pixel din tabloul de luminanta.
     *
     * @param k valoarea din slider
     */
    void binarizeaza(int k) {
        for (int i = 0; i < DIM; i++) {
            for (int j = 0; j < DIM; j++) {
                // daca valoarea pixelului este mai mare decat cea din slider, acesta devine alb
                // in caz contrat, pixelul devine alb
                ym[i][j] = y[i][j] > k ? 255 : 0;
            }
        }

        compuneCulori(true);
    }

    void binarizeazaClick(int k, int xp, int yp, int grosimeCreion) {
        for (int i = (yp - grosimeCreion > 0 ? yp - grosimeCreion : 0); i < (yp + grosimeCreion < DIM ? yp + grosimeCreion : DIM); i++) {
            for (int j = (xp - grosimeCreion > 0 ? xp - grosimeCreion : 0); j < (xp + grosimeCreion < DIM ? xp + grosimeCreion : DIM); j++) {
                ym[i][j] = y[i][j] > k ? 255 : 0;
            }
        }
        System.out.println("\nxp: " + xp + " | yp: " + yp
                + "\nxp - gros: " + (xp - grosimeCreion) + " | yp - gros: " + (yp - grosimeCreion)
                + "\nxp + gros: " + (xp + grosimeCreion) + " | yp + gros: " + (yp + grosimeCreion));
        compuneCuloriClick(true, xp, yp, grosimeCreion);
    }

    /**
     * Aplica efectul de negativare pe fiecare pixel al imaginii, tinand cont
     * daca aceasta este alb-negru sau color.
     */
    void negativeaza() {
        int r, g, b;

        if (jCheckBoxAlbNegru.isSelected()) {
            // Negativare alb - negru
            for (int i = 0; i < DIM; i++) {
                for (int j = 0; j < DIM; j++) {
                    // se aplica doar pe luminanta
                    ym[i][j] = 255 - ym[i][j]; // f(x) = MAX - x
                }
            }
            compuneCulori(true);
            copiaza(y, ym, DIM);
        } else {
            // Negativare color
            for (int i = 0; i < DIM; i++) {
                for (int j = 0; j < DIM; j++) {
                    // f(x) = MAX - x
                    r = 255 - CM.getRed(pixeliImagineM[i * DIM + j]);
                    g = 255 - CM.getGreen(pixeliImagineM[i * DIM + j]);
                    b = 255 - CM.getBlue(pixeliImagineM[i * DIM + j]);
                    pixeliImagineM[i * DIM + j] = r << 16 | g << 8 | b | 0xff000000;
                }
            }
            separaCulori(pixeliImagineM);//Reface y, cl, c2
        }
    }

    /**
     * Dilata vectorii de luminanta si cei doi de crominanta.
     */
    void dilataTot() {
        dilata(ym, DIM);
        dilata(c1m, DIM / 2);
        dilata(c2m, DIM / 2);
        compuneCulori(jCheckBoxAlbNegru.isSelected());
    }

    /**
     * Dilata imaginea in zona specificata, se memoreaza pixelii din doi in doi
     * (spatiu de un rand si o coloana intre pixeli).
     *
     * @param tab vectorul (ym, c1m, c2m) in care se vor dilata pixelii
     * @param DIM dimensiunea vectorului (DIM sau DIM/2)
     */
    void dilata(float[][] tab, int DIM) {
        // dilatare stanga sus
        if (jComboBoxZonaDilatare.getSelectedItem().equals("Stânga Sus")) {
            for (int i = 0; i < DIM / 2; i++) {
                for (int j = 0; j < DIM / 2; j++) {
                    s[2 * i][2 * j] = tab[i][j]; // se memoreaza pixelii din doi in doi (spatiu de un rand si o coloana intre pixeli)
                }
            }
        } // dilatare stanga jos 
        else if (jComboBoxZonaDilatare.getSelectedItem().equals("Stânga Jos")) {
            for (int i = DIM / 2, x = 0; i < DIM; i++, x++) {
                for (int j = 0, y = 0; j < DIM / 2; j++, y++) {
                    s[2 * x][2 * y] = tab[i][j];
                }
            }
        } // dilatare dreapta sus
        else if (jComboBoxZonaDilatare.getSelectedItem().equals("Dreapta Sus")) {
            for (int i = 0, x = 0; i < DIM / 2; i++, x++) {
                for (int j = DIM / 2, y = 0; j < DIM; j++, y++) {
                    s[2 * x][2 * y] = tab[i][j];
                }
            }
        } // dilatare dreapta jos
        else if (jComboBoxZonaDilatare.getSelectedItem().equals("Dreapta Jos")) {
            for (int i = DIM / 2, x = 0; i < DIM; i++, x++) {
                for (int j = DIM / 2, y = 0; j < DIM; j++, y++) {
                    s[2 * x][2 * y] = tab[i][j];
                }
            }
        } // dilatare central 
        else if (jComboBoxZonaDilatare.getSelectedItem().equals("Central")) {
            for (int i = DIM / 4, x = 0; i < DIM - DIM / 4; i++, x++) {
                for (int j = DIM / 4, y = 0; j < DIM - DIM / 4; j++, y++) {
                    s[2 * x][2 * y] = tab[i][j];
                }
            }
        }

        interpolare(s, DIM);
        copiaza(tab, s, DIM);
    }

    /**
     * Se interpoleaza elementele din tabloul cu pixelii dilatati in care sunt
     * linii si coloane libere intre elemente.
     *
     * @param tab tabloul cu pixelii dilatati
     * @param n dimensiunea tabloului
     */
    void interpolare(float[][] tab, int n) {
        // interpolare pe coloane
        for (int i = 0; i < n; i += 2) // trateaza doar liniile pare (acolo unde s-au pus elementele)
        {
            for (int j = 1; j < n - 1; j += 2) { // trateaza pozitiile impare unde nu sunt elemente
                tab[i][j] = (tab[i][j - 1] + tab[i][j + 1]) / 2; // se face interpolarea
            }
        }

        // copiaza ultima coloana (de pe pozitia 255), altfel ramane neinitializata
        for (int i = 0; i < n - 1; i++) {
            tab[i][n - 1] = tab[i][n - 2]; // se copiaza penultima coloana pe pozitia ultimei coloane
        }

        // interpolare pe linii
        for (int i = 1; i < n - 1; i += 2) { // trateaza doar liniile impare (cele necompletate)
            for (int j = 0; j < n; j++) { // calculez fiecare element de pe liniile impare
                // elementul ij = (elementul aflat deasupra + elementul aflat dedesubt) / 2
                tab[i][j] = (tab[i - 1][j] + tab[i + 1][j]) / 2; // se face interpolarea
            }
        }

        // copiaza ultima linie (de pe pozitia 255), altfel ramane neinitializata
        System.arraycopy(tab[n - 2], 0, tab[n - 1], 0, n); // se copiaza penultima linie pe pozitia ultimei linii, 0 = pozitia de start, n = numarul de elemente ce se copiaza
    }

    // 5. HISTOGRAME ///////////////////////////////////////////////////////////////////////////////
    int[] pixeli; // Frecvente nuante. Va fi folosit la normalizarea hostogramei

    /**
     * Traseaza histograma Y.
     */
    public void histograma() {
        pixeli = new int[256]; // pe fiecare pozitie o sa fie numarul de pixeli de intensitate i
        // Tabloul e initializat automat cu 0.
        // Va contine frecventele de aparitie ale nuantelor.
        // Valoarea elementului i -> nr de pixeli de nuanta i.

        int p, zero;

        // Numara pixelii de fiecare nuanta.
        for (int i = 0; i < DIM; i++) {
            for (int j = 0; j < DIM; j++) {
                p = corectCapete(Math.round(ym[i][j]));

                pixeli[p]++; // actualizare vector de frecventa
            }
        }

        deseneazaHistograma(jPanelHistogramaY, Color.black, pixeli); // apel pentru desenarea histogramei
    }

    /**
     * Traseaza histogramele R, G si B.
     */
    void histogramaRGB() {
        // pe fiecare pozitie din tablou o sa fie numarul de pixeli de intensitate i
        // fiecare tabloul e initializat automat cu 0.
        int pixeliR[] = new int[256];
        int pixeliG[] = new int[256];
        int pixeliB[] = new int[256];

        int r, g, b;

        // Numara pixelii de fiecare nuanta.
        for (int i = 0; i < DIM * DIM; i++) {
            r = CM.getRed(pixeliImagineM[i]);
            g = CM.getGreen(pixeliImagineM[i]);
            b = CM.getBlue(pixeliImagineM[i]);

            // actualizare vectori de frecventa
            pixeliR[r]++;
            pixeliG[g]++;
            pixeliB[b]++;
        }

        // apel pentru desenarea histogramelor
        deseneazaHistograma(jPanelHistogramaR, Color.red, pixeliR);
        deseneazaHistograma(jPanelHistogramaG, Color.green, pixeliG);
        deseneazaHistograma(jPanelHistogramaB, Color.blue, pixeliB);
    }

    /**
     * Deseneaza histograma in JPanel.
     *
     * @param jp JPanel-ul pe care trebuie facut desenul
     * @param c culoarea cu care se traseaza histograma
     * @param tab tabloul in care se gasesc frecventele intensitatilor luminoase
     * (pixelilor)
     */
    public void deseneazaHistograma(JPanel jp, Color c, int[] tab) {
        Graphics g = jp.getGraphics(); // obiect de desenare pe JPanel-ul histogramei

        // sterge o eventuala histograma existenta
        g.setColor(Color.white);
        g.fillRect(0, 0, jp.getWidth(), jp.getHeight());

        g.setColor(c); // culoarea de desenare a histogramei

        int p = 0, zero = jp.getHeight(); // stabileste pozitia abscisei (Y de sus in jos)

        // 256 de nuante posibile
        for (int i = 0; i < 256; i++) {
            if (i % 2 == 0) { // face o medie
                p = tab[i];
            } else {
                p += tab[i]; // face o suma
                // Traseaza mediile aritmetice ale nuantelor invecinate,
                // pentru ca nu au loc toate. Latimea jPanelului este 128 sau getHeight().
                g.drawLine(i / 2, zero, i / 2, zero - p / 40);
                // Traseaza o linie verticala. Lungimea e proportionala cu
                // media numarului de pixeli de intensitati i-1 si i.
                // pentru fiecare grup de doi pixeli se traseaza o singura linie verticala
            }
        }
    }

    /**
     * Normalizare histograma varianta 1. Pixelii intunecati devin si mai
     * intunecati. Pixelii luminosi raman aproximativ neschimbati. Creste
     * contrastul si scade luminozitatea.
     */
    void normalizareHistograma() {
        // uc = u * p(u) | uc = intensitate corectata

        // n(u) = nr pixeli de culoare u, u = {0, 1, ..., MAX}, MAX = 255
        // N = nr total pixeli = DIM * DIM
        // N = n(0) + n(1) + ... + n(MAX)
        // p(u) = probabilitatea ca nuanta unui pixel sa fie <= u
        // p(u) = [n(0) + n(1) + ... + n(u)] / N
        float[] ponderi = new float[256];
        float suma = 0;

        for (int i = 0; i < 256; i++) {
            suma += pixeli[i]; // suma pixeli mai intunecati decat i
            // tabloul pixeli e calculat la trasarea histogramei
            ponderi[i] = suma / DIM / DIM; // calculeaza ponderea pentru fiecare nuanta
        }

        int p;

        for (int i = 0; i < DIM; i++) {
            for (int j = 0; j < DIM; j++) {
                p = corectCapete(Math.round(ym[i][j]));
                // p=luminozitatea initiala.
                ym[i][j] *= ponderi[p]; // Pozitia in tabloul de 
                // ponderi corespunde luminozitatii.
            }
        }

        compuneCulori(jCheckBoxAlbNegru.isSelected());
    }

    /**
     * Apeleaza metoda 2 de normalizare <code>normH(ym)</code>.
     */
    void normalizareHistograma2() {
        normH(ym);
        compuneCulori(jCheckBoxAlbNegru.isSelected());
    }

    /**
     * Normalizare histograma varianta 2. Cea mai inchisa nuanta sa devine 0 si
     * cea mai deschisa nuanta sa devina 255.
     *
     * @param tab vectorul luminanta modificat <code>ym</code>
     */
    void normH(float[][] tab) {
        // uc = [d(u) - dmin) / (N - dmin)] * MAX | uc = intensitate corectata

        // d(u) = distributia cumulativa = nr total de pixeli de intensitate <= u
        // dmin = distributia cumulativa pentru nuanta cea mai intunecata din imagine
        // N = DIM * DIM
        // MAX = 255 (intensitate maxima)
        int[] nuante = new int[256]; // frecventele tuturor intensitatilor luminoase
        int p, n;

        // calculeaza numarul de pixeli pentru fiecare nuanta
        for (int i = 0; i < DIM; i++) {
            for (int j = 0; j < DIM; j++) {
                n = corectCapete(Math.round(tab[i][j]));
                nuante[n]++; // pozitia din tablou reprezinta nuanta
                // Valorile din tablou reprezinta distributiile nuantelor
                // (numarul de pixeli pentru fiecare nuanta)
            }
        }

        // determina numarul de pixeli de intensitate minima
        int dmin = 0;
        for (int i = 0; i < 256; i++) {
            if (nuante[i] != 0) {
                dmin = nuante[i]; // primul element nenul din tablou e dmin
                break;
            }
        }

        int suma = 0;
        for (int i = 0; i < 256; i++) {
            // calculeaza distributiile cumulative
            suma += nuante[i];
            nuante[i] = suma; // nuante[255] = DIM * DIM
        }

        int u;
        for (int i = 0; i < DIM; i++) {
            for (int j = 0; j < DIM; j++) {
                u = corectCapete(Math.round(tab[i][j]));
                tab[i][j] = (nuante[u] - dmin) * 255 / (DIM * DIM - dmin); // uc = [d(u) - dmin) / (N - dmin)] * MAX
            }
        }
    }

    // 6. FILTRE DE NETEZIRE ///////////////////////////////////////////////////////////////////////////////
    float[] unu3 = {1, 1, 1};
    float[] unu5 = {1, 1, 1, 1, 1};
    float[] unu7 = {1, 1, 1, 1, 1, 1, 1};
    float[][] filtruBox3x3 = {unu3, unu3, unu3};
    float[][] filtruBox5x5 = {unu5, unu5, unu5, unu5, unu5};
    float[][] filtruBox7x7 = {unu7, unu7, unu7, unu7, unu7, unu7, unu7};
    float[][] filtruGaussian = {{1, 2, 1}, {2, 4, 2}, {1, 2, 1}};

    /**
     * Initializeaza filtrul box si apeleaza metoda de filtrare
     * <code>filtrare()</code> pentru fiecare dintre componentele ym, c1m, c2m
     * si repune imaginea in tabloul original dupa operatia de filtrare.
     * Estompare, contururile sunt afectate, imaginea pare manjita; reduce
     * zgomotele.
     *
     * @param dimF dimensiunea de filtrare din combo box
     */
    void box(int dimF) {
        float[][] filtruBox; // filtrul folosit in filtrare

        // initializare filtru box
        switch (dimF) {
            case 3:
                filtruBox = filtruBox3x3;
                break;
            case 5:
                filtruBox = filtruBox5x5;
                break;
            default:
                filtruBox = filtruBox7x7;
                break;
        }

        filtreaza(ym, s, filtruBox, dimF * dimF, DIM);
        copiaza(ym, s, DIM); // repunere imagine in tabloul original dupa operatia de filtrare

        filtreaza(c1m, s, filtruBox, dimF * dimF, DIM / 2);
        copiaza(c1m, s, DIM / 2);

        filtreaza(c2m, s, filtruBox, dimF * dimF, DIM / 2);
        copiaza(c2m, s, DIM / 2);

        compuneCulori(jCheckBoxAlbNegru.isSelected());
    }

    /**
     * Apeleaza metoda <code>fil()</code> pentru calcularea sumei de produse
     * pentru fiecare pixel din tabloul care contine imaginea ce trebuie
     * filtrata <code>orig</code> si adaugarea rezultatului in tabloul in care
     * trebuie pus rezultatul <code>dest</code>.
     *
     * @param orig tabloul care contine imaginea ce trebuie filtrata
     * @param dest tabloul in care trebuie pus rezultatul
     * @param w filtrul (dimensiunea matriciala a filtrului box)
     * @param suma patratul valorii din combo box
     * @param n dimensiunea vectorului (latura) ym, c1m, c2m (DIM | DIM/2)
     */
    void filtreaza(float[][] orig, float[][] dest, float[][] w, float suma, int n) {
        // w = filtrul
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) // i, j reprezinta pozitia pixelului filtrat
            {
                // fil calculeaza suma de produse
                dest[i][j] = fil(orig, w, i, j, n) / suma;
            }
        }
    }

    /**
     * Asezare filtru centrat pe pixel.
     *
     * @param orig tabloul care contine imaginea ce trebuie filtrata
     * @param w filtrul (dimensiunea matriciala a filtrului box)
     * @param lin indice pixel linie de filtrat
     * @param col indice pixel coloana de filtrat
     * @param n dimensiunea vectorului (latura) ym, c1m, c2m (DIM | DIM/2)
     * @return suma de produse
     */
    float fil(float[][] orig, float[][] w, int lin, int col, int n) {
        // calculeaza pozitia elementului din centrul filtrului
        // elementul central al filtrului se gaseste pe linia l si coloana h
        int l = (w.length - 1) / 2;
        int h = (w[0].length - 1) / 2; // tabloul e dreptunghiular

        float rez = 0; // memoreaza suma de produse

        // parcurge toti pixelii care sunt acoperiti de filtru
        for (int i = -l; i <= l; i++) {
            for (int j = -h; j <= h; j++) {
                // rez += elementul din tabloul original * coeficientul corespunzator din filtru
                // copiaza pixelii acoperiti de filtru in rez
                rez += orig[corectareIndex(lin + i, n)][corectareIndex(col + j, n)] * w[i + l][j + h];
            }
        }

        return rez;
    }

    /**
     * Multiplicare linie / coloana imaginativ folosind primul / ultimul /
     * elementul indicat.
     *
     * @param index pozitia pixelului
     * @param n dimensiunea vectorului (latura) ym, c1m, c2m (DIM | DIM/2)
     * @return elementul de accesat
     */
    int corectareIndex(int index, int n) {
        if (index < 0) {
            return 0; // accesare element de pe linia / coloana 0
        }

        if (index >= n) {
            return n - 1;  // accesare element de pe linia / coloana n - 1
        }

        return index; // linia / coloana indicata
    }

    /**
     * Actioneaza mai bland asupra contururilor, reduce zgomotele.
     */
    void gaussian() {
        filtreaza(ym, s, filtruGaussian, 16, DIM);
        copiaza(ym, s, DIM);

        filtreaza(c1m, s, filtruGaussian, 16, DIM / 2);
        copiaza(c1m, s, DIM / 2);

        filtreaza(c2m, s, filtruGaussian, 16, DIM / 2);
        copiaza(c2m, s, DIM / 2);

        compuneCulori(jCheckBoxAlbNegru.isSelected());
    }

    /**
     * Aplica imaginea zgomotoasa peste elementele de luminanta si crominanta
     * ale imaginii originale.
     *
     * @param nivel valoarea din slider
     */
    void adaugaZgomot(int nivel) {
        float k = nivel / 500.f; // ajustare nivel zgomot
        ImageIcon zgomot = new ImageIcon("zgomot.jpg"); // imaginea zgomotoasa
        int pixeliImagine[] = new int[DIM * DIM]; // tabloul care va contine zgomotul
        PixelGrabber grabber = new PixelGrabber(zgomot.getImage().getSource(), 0, 0, DIM, DIM, pixeliImagine, 0, DIM); // adaugare imagine in vector

        try {
            grabber.grabPixels(); // citeste datele din fisier si plaseaza pixelii imaginii in tabloul pixeliImagine
        } catch (Exception e) {
            System.out.println(e);
            return;
        }

        int r, g, b;
        for (int i = 0; i < DIM; i++) {
            for (int j = 0; j < DIM; j++) {
                // preluare componente R, G, B
                r = CM.getRed(pixeliImagine[i * DIM + j]);
                g = CM.getGreen(pixeliImagine[i * DIM + j]);
                b = CM.getBlue(pixeliImagine[i * DIM + j]);

                ym[i][j] = y[i][j] + k * aleatorPozNeg() * (0.299f * r + 0.587f * g + 0.114f * b); // aplicare luminozitate zgomot peste luminanta

                if (i % 2 == 0 && j % 2 == 0) {
                    // aplicare luminozitate zgomot peste crominante
                    c1m[i / 2][j / 2] = c1[i / 2][j / 2] + k * aleatorPozNeg() * (0.5f * r - 0.2f * g - 0.3f * b);
                    c2m[i / 2][j / 2] = c2[i / 2][j / 2] + k * aleatorPozNeg() * (0.3f * r + 0.4f * g - 0.7f * b);
                }
            }
        }
        compuneCulori(jCheckBoxAlbNegru.isSelected());
    }

    /**
     * Pozitie aleatorie a zgomotului pentru a evita cresterea intensitatii
     * luminoase.
     *
     * @return -1 sau 1
     */
    int aleatorPozNeg() {
        return Math.random() > 0.5 ? -1 : 1;
    }

    /**
     * Initializeaza filtrul box pentru regiuni si apeleaza de 100 de ori metoda
     * de filtrare <code>filtrare()</code> pentru fiecare dintre componentele
     * ym, c1m, c2m si repune imaginea in tabloul original dupa operatia de
     * filtrare. Estompare, contururile sunt afectate, imaginea pare manjita;
     * reduce zgomotele.
     *
     * @param dimF dimensiunea de filtrare din combo box
     */
    void estompareRegiuni(int dimF) {
        float[][] filtruBox; // filtrul folosit in filtrare

        // initializare filtru box
        switch (dimF) {
            case 3:
                filtruBox = filtruBox3x3;
                break;
            case 5:
                filtruBox = filtruBox5x5;
                break;
            default:
                filtruBox = filtruBox7x7;
                break;
        }

        for (int i = 0; i < 100; i++) {
            filtreazaRegiuni(ym, s, filtruBox, dimF * dimF, DIM);
            copiaza(ym, s, DIM); // repunere imagine in tabloul original dupa operatia de filtrare

            filtreazaRegiuni(c1m, s, filtruBox, dimF * dimF, DIM / 2);
            copiaza(c1m, s, DIM / 2);

            filtreazaRegiuni(c2m, s, filtruBox, dimF * dimF, DIM / 2);
            copiaza(c2m, s, DIM / 2);

            compuneCulori(false);
        }
    }

    /**
     * Apeleaza metoda <code>fil()</code> pentru calcularea sumei de produse
     * atunci cand la parcurgere se intalnesc pixeli negri in tabloul luminanta
     * <code>y</code> al imaginii binare si aplica valoarea in tabloul care
     * contine imaginea ce trebuie filtrata <code>orig</code> si adaugarea
     * rezultatului in tabloul in care trebuie pus rezultatul <code>dest</code>.
     *
     * @param orig tabloul care contine imaginea ce trebuie filtrata
     * @param dest tabloul in care trebuie pus rezultatul
     * @param w filtrul (dimensiunea matriciala a filtrului box)
     * @param suma patratul valorii din combo box
     * @param n dimensiunea vectorului (latura) ym, c1m, c2m (DIM | DIM/2)
     */
    void filtreazaRegiuni(float[][] orig, float[][] dest, float[][] w, float suma, int n) {
        // w = filtrul
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) // i, j reprezinta pozitia pixelului filtrat
            {
                // aplica filtrul doar in zonele in care imaginea binara contine pixeli negri
                if ((n == DIM && idImagineRegiuni.y[i][j] == 0) || (n == DIM / 2 && idImagineRegiuni.y[2 * i][2 * j] == 0)) {
                    // i, j reprezinta pozitia pixelului care se modifica
                    dest[i][j] = fil(orig, w, i, j, n) / suma; // fil calculeaza suma de produse
                } else {
                    dest[i][j] = orig[i][j]; // nu filtreaza
                }
            }
        }
    }

    // 7. FILTRE LAPACIAN & STATISTICE ///////////////////////////////////////////////////////////////////////////////
    /**
     * Aplica filtrul Laplacian simplu Horizontal + Vertical (FALSE) sau
     * modificat Horizontal + Vertical + Diagonal cu discontinuitati (TRUE).
     *
     * @param hvd tipul de filtru: simplu = FALSE; modificat = TRUE
     */
    void laplacian(boolean hvd) {
        if (hvd) {
            // trimite coeficientii filtrului H + V + D
            filtreaza(y, ym, new float[][]{{1, 1, 1}, {1, -8, 1}, {1, 1, 1}}, 1, DIM);
        } else {
            // trimite coeficientii filtrului H + V
            filtreaza(y, ym, new float[][]{{0, 1, 0}, {1, -4, 1}, {0, 1, 0}}, 1, DIM);
        }

        compuneCulori(true);
    }

    /**
     * Aplica filtrul statistic pe fiecare pixel din tabloul de luminanta ym.
     *
     * @param orig tabloul luminanta in care se adauga valorile
     * @param dest imaginea filtrata (tabloul <code>s</code>)
     * @param n dimensiunea DIM
     * @param lat dimensiunea filtrului
     * @param tipFil tipul filtrului 1 = minim, 2 = maxim, 3 = median, 4 =
     * interval
     */
    void filtruStatistic(float[][] orig, float[][] dest, int n, int lat, int tipFil) {
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                dest[i][j] = calcFilStat(orig, dest, n, i, j, lat, tipFil);
            }
        }
    }

    /**
     *
     * @param orig tabloul luminanta in care se adauga valorile
     * @param dest imaginea filtrata (tabloul <code>s</code>)
     * @param n dimensiunea DIM
     * @param i pozitia de pe linie a pixelului
     * @param j pozitia de pe coloana a pixelului
     * @param lat dimensiunea filtrului
     * @param tipFil tipul filtrului 1 = minim, 2 = maxim, 3 = median, 4 =
     * interval
     * @return valoare de filtrare in functie de tipul de filtru
     */
    float calcFilStat(float[][] orig, float[][] dest, int n, int i, int j, int lat, int tipFil) {
        // 1. scoate asta in afara, nu face la fiecare apel alocare de memorie
        // 2. filtrele minim, maxim, interval nu au nevoie de sortare
        float[] tab = new float[lat * lat]; // daca e de 3x3 => 9 elemente; 5x5 => 15 elemente; 7x7 => 49 elemente

        int x = 0;
        int l = (lat - 1) / 2; // jumatatea filtrului

        // parcurge toti pixelii care sunt acoperiti de filtru
        for (int k = -l; k <= l; k++) {
            for (int p = -l; p <= l; p++) {
                // rez += elementul din tabloul original * coeficientul corespunzator din filtru
                // copiaza pixelii acoperiti de filtru in tab
                tab[x++] = orig[corectareIndex(i + k, n)][corectareIndex(j + p, n)];
            }
        }

        Arrays.sort(tab); // sortare tablou in ordine crescatoare

        switch (tipFil) {
            case 1:
                return tab[0]; // minim
            case 2:
                return tab[tab.length - 1]; // maxim
            case 3:
                return tab[tab.length / 2]; // median
            default:
                return Math.abs(tab[tab.length - 1] - tab[0]); // interval
        }
    }

    /**
     * Aplica filtrul statistic neliniar pe componenta ym.
     *
     * @param nFiltru dimensiunea filtrului
     */
    void median(int nFiltru) {
        filtruStatistic(ym, s, DIM, nFiltru, 3);
        copiaza(ym, s, DIM);
        compuneCulori(jCheckBoxAlbNegru.isSelected());
    }

    /**
     * Aplica filtrul statistic maxim. Regiunile luminoase cresc si le domina pe
     * celelalte.
     *
     * @param nFiltru dimensiunea filtrului
     */
    void max(int nFiltru) {
        filtruStatistic(ym, s, DIM, nFiltru, 2);
        copiaza(ym, s, DIM);
        compuneCulori(jCheckBoxAlbNegru.isSelected());
    }

    /**
     * Aplica filtrul statistic minim. Regiunile intunecate cresc si le domina
     * pe celelalte.
     *
     * @param nFiltru dimensiunea filtrului
     */
    void min(int nFiltru) {
        filtruStatistic(ym, s, DIM, nFiltru, 1);
        copiaza(ym, s, DIM);
        compuneCulori(jCheckBoxAlbNegru.isSelected());
    }

    /**
     * Aplica filtrul statistic interval. Diferenta dintre valoarea minima si
     * cea maxima a pixelilor din vecinatate. Filtru omnidirectional si
     * neliniar, care detecteaza contururile.
     *
     * @param nFiltru dimensiunea filtrului
     */
    void interval(int nFiltru) {
        filtruStatistic(ym, s, DIM, nFiltru, 4);
        copiaza(ym, s, DIM);
        compuneCulori(jCheckBoxAlbNegru.isSelected());
    }

    // 8. ACCENTUARE DETALII & TRASARE CONTURURI ///////////////////////////////////////////////////////////////////////////////
    /**
     * Pentru salvari contururi.
     */
    float[][] s2 = new float[DIM][DIM];

    /**
     * Tine evidenta in cazul in care se schimba ceva in imagine dupa apelul
     * conectareContururi().
     */
    boolean modificat;

    /**
     * Metoda de aplicare a filtrului Gaussian fin, dedicata pentru reducerea
     * zgomotului la trasarea contururilor.
     *
     * @param fg status check box filtru Gaussian; true = apelare metoda
     * <code>filtreaza()</code>; false = apelare metoda <code>copiaza()</code>
     */
    void gaussianY(boolean fg) {
        // imaginea care va fi conturata e plasata in s, cu sau fara filtrare
        if (fg) {
            filtreaza(y, s, filtruGaussian, 16, DIM);
        } else {
            copiaza(s, y, DIM);
        }
    }

    /**
     * Trasare contur vecinatati; apeleaza metoda <code>trasareContur</code>.
     *
     * @param prag valoarea din slider prag sus
     * @param interior true = vecinatati interior; false = vecinatati exterior
     */
    void trasareConturVec(int prag, boolean interior) {
        trasareContur(s, ym, DIM, prag, interior); // s --> ym

        compuneCulori(true);
    }

    /**
     * Primeste tabloul care contine datele si aplica trasarea conturului. Un
     * pixel apartine conturului, daca exista cel putin un vecin pentru care
     * diferenta de luminozitate depaseste un anumit prag. Daca vecinul e mai
     * luminos se va trasa conturul interior, iar in caz contrat cel exterior.
     *
     * @param orig imaginea de conturat
     * @param dest rezultatul dupa trasarea conturului
     * @param n latura DIM
     * @param prag valoarea din slider prag sus
     * @param interior true = vecinatati interior; false = vecinatati exterior
     */
    void trasareContur(float[][] orig, float[][] dest, int n, int prag, boolean interior) {
        // parcurgere imagine
        // primele si ultimele linii si coloane sunt omise pentru aplicarea elementului structural
        for (int i = 1; i < n - 1; i++) {
            for (int j = 1; j < n - 1; j++) {
                if (interior) { // daca se doreste conturul interior
                    if (peConturInt(orig, i, j, prag)) { // daca metoda raspunde "da"
                        dest[i][j] = 0; // trasare pixel negru
                    } else {
                        dest[i][j] = 255; // trasare pixel alb
                    }
                } // daca se doreste conturul exterior 
                else if (peConturExt(orig, i, j, prag)) {
                    dest[i][j] = 0; // trasare pixel negru
                } else {
                    dest[i][j] = 255; // trasare pixel alb
                }
            }
        }
    }

    /**
     * Verifica daca pixelul apartine pe conturul interior. Un pixel apartine
     * conturului, daca exista cel putin un vecin pentru care diferenta de
     * luminozitate depaseste un anumit prag.
     *
     * @param tab imaginea originala
     * @param i pozitia i a pixelului
     * @param j pozitia j a pixelului
     * @param p valoarea din slider prag sus
     * @return existenta expresiei pentru luminozitatea care depaseste pragul
     */
    boolean peConturInt(float[][] tab, int i, int j, int p) {
        float x = tab[i][j];
        // pixelul acoperit de elementul central
        // pixel central mai intunecat ==> contur interior

        // elementul central este sub forma de patrat cu latura de 3x3
        // cautare existenta subexpresie (diferenta de luminozitate care depaseste pragul)
        return tab[i - 1][j - 1] - x > p // vecinul din stanga sus
                || tab[i - 1][j] - x > p // vecinul de sus de pe aceeasi coloana
                || tab[i - 1][j + 1] - x > p // vecinul din dreapta sus 
                || tab[i][j - 1] - x > p // vecinul din stanga de pe aceeasi linie
                || tab[i + 1][j + 1] - x > p // vecinul din dreapta jos
                || tab[i][j + 1] - x > p // vecinul din dreapta de pe aceeasi linie
                || tab[i + 1][j - 1] - x > p // vecinul din stanga jos
                || tab[i + 1][j] - x > p; // vecinul de jos de pe aceeasi coloana
    }

    /**
     * Verifica daca pixelul apartine pe conturul exterior. Daca vecinul e mai
     * luminos se va trasa conturul interior, iar in caz contrat cel exterior.
     *
     * @param tab imaginea originala
     * @param i pozitia i a pixelului
     * @param j pozitia j a pixelului
     * @param p valoarea din slider prag sus
     * @return existenta expresiei pentru luminozitatea care depaseste pragul
     */
    boolean peConturExt(float[][] tab, int i, int j, int p) {
        float x = tab[i][j];
        // pixel central mai luminos ==> contur exterior

        // elementul central este sub forma de patrat cu latura de 3x3
        // cautare existenta subexpresie (diferenta de luminozitate care depaseste pragul)
        return x - tab[i - 1][j - 1] > p // vecinul din stanga sus
                || x - tab[i - 1][j] > p // vecinul de sus de pe aceeasi coloana
                || x - tab[i - 1][j + 1] > p // vecinul din dreapta sus
                || x - tab[i][j - 1] > p // vecinul din stanga de pe aceeasi linie
                || x - tab[i + 1][j + 1] > p // vecinul din dreapta jos
                || x - tab[i][j + 1] > p // vecinul din dreapta de pe aceeasi linie
                || x - tab[i + 1][j - 1] > p // vecinul din stanga jos
                || x - tab[i + 1][j] > p; // vecinul de jos de pe aceeasi coloana
    }

    /**
     * Aplica conturul Laplacian pentru discontinuitati pe orizontala, pe
     * verticala si pe diagonala. Apeleaza <code>filtreaza()</code> cu
     * impartirea la 1.
     *
     * @param k valoare prag contur slider
     */
    void conturLaplacian(int k) {
        filtreaza(s, ym, new float[][]{{1, 1, 1}, {1, -8, 1}, {1, 1, 1}}, 1, DIM);

        for (int i = 0; i < DIM; i++) {
            for (int j = 0; j < DIM; j++) {
                // negativare + binarizare
                // ym[i][j] = Math.abs(ym[i][j]) > k ? 0 : 255; // derivata poate fi si negativa, asa nu e bine pentru ca apar contururi duble
                ym[i][j] = ym[i][j] > k ? 0 : 255; // daca valoarea depaseste un anumit prag, pixelul este pe contur, altfel nu este
            }
        }

        compuneCulori(true);
    }

    /**
     * Trasare contur pe baza filtrului Sobel si Prewitt. Estimeaza gradientii
     * pe directiile x (orizontala) si y (verticala).
     *
     * @param prag nivelul din slider prag sus
     * @param sobel true = filtru Sobel; false = filtru Prewitt
     */
    void trasareConturSobelPrewitt(int prag, boolean sobel) {
        // gx(x,y) = f(x + 1, y) - f(x - 1, y); Gradient orizontal (diferenta dintre vecinul din stranga si cel din dreapta)
        // gy(x,y) = f(x, y + 1) - f(x, y - 1); Gradient vertical (diferenta dintre vecinul de sus si cel de jos)

        prag *= 8; // ajustare prag

        // pentru reducerea zgomotelor se calculeaza o medie a gradientului pe o vecinatate de 3 pixeli cu urmatoarele filtre
        if (sobel) { // filtrul Sobel
            // pentru gradientul pixelului central are pondere dubla fata de gradientii vecinilor
            filtreaza(s, s2, new float[][]{{-1, 0, 1}, {-2, 0, 2}, {-1, 0, 1}}, 1, DIM); // s2 va contine gradientii orizontali
            filtreaza(s, ym, new float[][]{{-1, -2, -1}, {0, 0, 0}, {1, 2, 1}}, 1, DIM); // ym va contine gradientii verticali
        } else { // filtrul Prewitt
            filtreaza(s, s2, new float[][]{{-1, 0, 1}, {-1, 0, 1}, {-1, 0, 1}}, 1, DIM); // media gradientilor pe orizontala pentru cei 3 pixeli acoperiti pe coloana din mijloc
            filtreaza(s, ym, new float[][]{{-1, -1, -1}, {0, 0, 0}, {1, 1, 1}}, 1, DIM); // media gradientilor pe verticala pentru cei 3 pixeli acoperiti pe linia din mijloc
        }

        // urmeaza binarizarea, care tine cont de modulul gradientului.
        // (modul simplificat) = suma modulelor
        for (int i = 0; i < DIM; i++) {
            for (int j = 0; j < DIM; j++) {
                if (Math.abs(s2[i][j]) + Math.abs(ym[i][j]) > prag) { // aplicare varianta simplificata a modulului
                    ym[i][j] = 0; // modul peste prag --> pixel negru
                } else {
                    ym[i][j] = 255; // pixel alb
                }
            }
        }

        compuneCulori(true);
    }

    /**
     * Trasare contur pe baza filtrului Interval. Apeleaza metoda
     * <code>filtruStatistic()</code>.
     *
     * @param k valoare prag contur slider
     */
    void trasareConturInterval(int k) {
        k *= 2; // ajustare prag
        filtruStatistic(s, ym, DIM, 3, 4);

        // aplicare filtru interval
        for (int i = 0; i < DIM; i++) // negativare + binarizare
        {
            for (int j = 0; j < DIM; j++) {
                ym[i][j] = ym[i][j] > k ? 0 : 255;
            }
        }

        compuneCulori(true);
    }

    /**
     * Trasare contur pe baza filtrului Canny. Compromis intre reducerea
     * zgomotelor si localizarea contururilor.
     * <p>
     * 1. Aplica filtrul Gaussian.</p>
     * <p>
     * 2. Calculeaza modulul si orientarea gradientului (tablourile g si theta
     * Θ).</p>
     * <p>
     * 3. Algoritm de subtiere a contururilor:</p>
     * <br/>
     * Tabloul subtiat de gradienti: <code>gs</code>
     * <br/><br/>
     * Pentru fiecare gradient (x, y):
     * <ul>
     * <li>3.1. aproximeaza directia theta <code>Θ(x, y)</code> a gradientului
     * prin 0, 45, 90, -90 sau -45 grade.</li>
     * <li>3.2. daca <code>g(x, y)</code> MAI MIC decat vecinul aflat pe
     * directia aproximata sau <code>g(x, y)</code> MAI MIC decat vecinul aflat
     * pe directia aproximata +180 <code>gs(x, y) = 0</code> altfel
     * <code>gs(x, y) = g(x, y)</code></li>
     * </ul>
     * <p>
     * Binarizare si histerezis</p>
     * <ul>
     * <li>2 praguri: <code>Pjos</code> si <code>Psus</code></li>
     * <li>Pe baza pragului Psus se determina conturul initial</li>
     * <li>Toti vecinii unui pixel aflat pe conturul initial, al caror nivel
     * depaseste Pjos sunt adaugati la contur</li>
     * </ul>
     *
     * @param prags valoare prag sus slider de discretizare pentru trasare
     * contur initial (prea incarcat sau contururi intrerupte)
     * @param pragj valoare prag jos slider de discretizare (conectare contururi
     * intrerupte)
     */
    void canny(int prags, int pragj) {
        prags *= 4; // ajustare prag

        // estimarea gradientilor folosind filtrul Sobel
        filtreaza(s, ym, new float[][]{{-1, 0, 1}, {-2, 0, 2}, {-1, 0, 1}}, 1, DIM); // ym <-- gradient orizontal
        filtreaza(s, s2, new float[][]{{-1, -2, -1}, {0, 0, 0}, {1, 2, 1}}, 1, DIM); // s2 <—- gradient vertical

        float thetaGrad; // orientarea gradientului
        float modulGrad; // modulul gradientului

        // crearea tablourilor
        for (int i = 1; i < DIM - 1; i++) {
            for (int j = 1; j < DIM - 1; j++) {
                thetaGrad = (float) (Math.atan(s2[i][j] / ym[i][j]) * 180 / Math.PI); // orientarea gradientului (conversie din radiani in grade)
                modulGrad = (float) Math.sqrt(ym[i][j] * ym[i][j] + s2[i][j] * s2[i][j]); // modulul gradientului (modulul absolut)
                ym[i][j] = rotunjireGrad(thetaGrad); // ym <-- orientari gradienti
                s[i][j] = modulGrad; // s <-- module gradienti
            }
        }

        // urmeaza subtierea conturului
        for (int i = 1; i < DIM - 1; i++) {
            for (int j = 1; j < DIM - 1; j++) {
                // verificare daca modulul pixelului este maxim pe directia data de orientarea gradientului sau nu
                if (nuEMaximPeDirectie(ym[i][j], i, j)) {
                    s2[i][j] = 0; // daca exista un vecin mai important, sterg pixelul de pe contur
                } else {
                    s2[i][j] = s[i][j]; // toti vecinii au gradient mai mic
                }
            }
        }

        // Binarizare cu histerezis:
        // Etapa 1: stabilirea conturului initial (toti pixelii pentru care gradientul depaseste pragul de sus sunt adaugati la contur)
        for (int i = 1; i < DIM - 1; i++) {
            for (int j = 1; j < DIM - 1; j++) {
                if (s2[i][j] > prags) {
                    ym[i][j] = 0; // negru = contur
                } else {
                    ym[i][j] = 255;
                }
            }
        }

        // Etapa 2: conectare contururi (adauga la contur toti vecinii fiecarui pixel care depasesc pragul de jos)
        do {
            modificat = false;

            for (int i = 2; i < DIM - 2; i++) {
                for (int j = 2; j < DIM - 2; j++) {
                    if (ym[i][j] == 0) {
                        // 8 vecini
                        conectareContururi(i - 1, j, pragj);
                        conectareContururi(i - 1, j - 1, pragj);
                        conectareContururi(i - 1, j + 1, pragj);
                        conectareContururi(i, j + 1, pragj);
                        conectareContururi(i, j - 1, pragj);
                        conectareContururi(i + 1, j, pragj);
                        conectareContururi(i + 1, j - 1, pragj);
                        conectareContururi(i + 1, j + 1, pragj);
                    }
                }
            }
        } while (modificat); // se repeta pana cand apar modificati la conectareContururi()

        compuneCulori(true);
    }

    /**
     * Verifica daca modulul este maxim pe directia data de orientarea
     * gradientului sau nu. Pentru gradientul (x, y) aproximeaza directia theta
     * Θ(x, y) a gradientului prin 0, 45, 90, -90 sau -45 grade.
     *
     * @param t orientarea gradientului
     * @param i pozitia x a pixelului
     * @param j pozitia y a pixelului
     * @return true daca pixelul este maxim pe directie; false in caz contrar
     */
    boolean nuEMaximPeDirectie(float t, int i, int j) {
        // modificata pentru ca y e cu -
        float g = s[i][j]; // s = modulul, t = orientarea

        return (t == 0 && (g < s[i][j + 1] || g < s[i][j - 1])) // orientare orizontala (ne uitam la vecinul aflat in stanga si la vecinul aflat in dreapta)
                || (t == 45 && (g < s[i - 1][j + 1] || g < s[i + 1][j - 1])) // orientare stanga jos si dreapta sus
                || (t == -45 && (g < s[i + 1][j + 1] || g < s[i - 1][j - 1])) // orientare dreapta jos si stanga sus
                || ((t == -90 || t == 90) && (g < s[i + 1][j] || g < s[i - 1][j])); // orientare verticala
    }

    /**
     * Rotunjire theta, aproximeaza directia gradientului.
     *
     * @param grad
     * @return directia gradientului
     */
    int rotunjireGrad(float grad) {
        if (grad < -67.5) // -45 - 45 / 2
        {
            return -90;
        }

        if (grad < -22.5) // -45 / 2
        {
            return -45;
        }

        if (grad < 22.5) {
            return 0;
        }

        if (grad < 67.5) // 45 + 45 / 2
        {
            return 45;
        }

        return 90;
    }

    /**
     * Verifica daca vecinul pixelului depaseste pragul de jos pentru a-l adauga
     * la contur.
     *
     * @param i pozitia x
     * @param j pozitia y
     * @param pragj valoare prag jos
     */
    void conectareContururi(int i, int j, int pragj) {
        if (s2[i][j] > pragj) {
            ym[i][j] = 0;
            s2[i][j] = 0; // Altfel intra aici si la urmatoarea iteratie, 
            // rezultand o bucla infinita.
            modificat = true; // in cazul in care se schimba ceva in imagine
        }
    }

    /**
     * Apeleaza metoda <code>filtreaza()</code> avand ca argument un tablou
     * bidimensional care contine valoarea <code>c</code> si trimite pentru
     * impartire valoarea <b>c - 8</b> pentru a nu modifica luminozitatea.
     *
     * @param c valoarea ajustata din slider
     */
    void accentuareDetalii(float c) {
        // c = 8 --> Laplacian (infinit).
        // c foarte mare --> imagine nemodificata (filtrul trece sus; Laplacian)
        // c > 8, dar apropiat de 8 --> accentuare detalii.
        // c < 8, estompare.

        // impartire la "c - 8" pentru a nu modifica luminozitatea imaginii; k = 1 / (c - 8)
        filtreaza(y, ym, new float[][]{{-1, -1, -1}, {-1, c, -1}, {-1, -1, -1}}, c - 8, DIM);
        compuneCulori(jCheckBoxAlbNegru.isSelected());
    }

    /**
     * Traseaza conturul imaginii binare peste imaginea principala folosindu-se
     * de eroziunea produsa de metoda <code>calcEroziuneContur()</code>.
     */
    void conturBinRegiuni() {
        calcEroziuneContur(ym, s, DIM);

        for (int i = 0; i < DIM; i++) {
            for (int j = 0; j < DIM; j++) {
                if (s[i][j] == 255 && ym[i][j] == 0) { // daca e alb in imaginea erodata si negru in imaginea binara, e pe contur
                    id.ym[i][j] = 255; // traseaza conturul in poza cu alb
                }
            }
        }

        id.compuneCulori(false);
    }

    /**
     * Daca originea elementului structural coincide cu un pixel negru din
     * imagine, si exista cel putin un vecin care se suprapune peste un pixel
     * intre valorile 255 si 10 al imaginii, atunci pixelul acoperit de
     * elementul structural devine alb.
     *
     * @param orig imaginea care trebuie erodata
     * @param dest tabloul in care se pune imaginea erodata
     * @param n latura DIM
     */
    void calcEroziuneContur(float[][] orig, float[][] dest, int n) {
        for (int i = 1; i < n - 1; i++) {
            for (int j = 1; j < n - 1; j++) {
                if (orig[i][j] == 0) { // origine = negru
                    if ((orig[i - 1][j] <= 255 && orig[i - 1][j] > 10)
                            || (orig[i + 1][j] <= 255 && orig[i + 1][j] > 10)
                            || (orig[i][j - 1] <= 255 && orig[i][j - 1] > 10)
                            || (orig[i][j + 1] <= 255 && orig[i][j + 1] > 10)) { // exista un vecin alb
                        dest[i][j] = 255; // origine = alb
                    } else {
                        dest[i][j] = 0;
                    }
                } else {
                    dest[i][j] = 255; // zonele albe raman albe
                }
            }
        }
    }

    // 9. TRANSFORMAREA IMAGINILOR BINARE ///////////////////////////////////////////////////////////////////////////////
    /**
     * Aplica efectul de dilatare folosind metoda <code>calcDil()</code> dupa
     * care copiaza rezultatul in <code>ym</code> si aplica
     * <code>compuneCulori()</code>.
     */
    void dilatare() {
        calcDil(ym, s, DIM);
        copiaza(ym, s, DIM);
        compuneCulori(true);
    }

    /**
     * Deplasarea elementului structural astfel incat sa parcurga intreaga
     * imagine. Daca originea elementului structural coincide cu un pixel negru
     * din imagine, atunci toti pixelii acoperiti de vecinii acestuia devin
     * negri. Curata cu alb tabloul destinatie folosind <code>fill()</code>. In
     * tabloul rezultat se pun doar pixelii negri.
     *
     * @param orig tabloul original
     * @param dest tabloul destinatie in care trebuie pus rezultatul
     * @param n latura DIM
     */
    void calcDil(float[][] orig, float[][] dest, int n) {
        fill(dest, n, 255); // curata tabloul destinatie

        // parcurgere tabloul prin omiterea primei si ultimei lini / coloane pentru asezarea filtrului
        for (int i = 1; i < n - 1; i++) {
            for (int j = 1; j < n - 1; j++) {
                if (orig[i][j] == 0) // daca pixelul origine la care am ajuns este negru
                {
                    dest[i][j]
                            = dest[i - 1][j]
                            = dest[i + 1][j]
                            = dest[i][j - 1]
                            = dest[i][j + 1] = 0; // atunci inlocuim toti vecinii cu negru
                }
            }
        }
    }

    /**
     * Curata tabloul destinatie, umplandu-l cu <code>val</code> (255 adica
     * alb).
     *
     * @param tab tabloul destinatie de curatat
     * @param n latura DIM
     * @param val culoarea pentru curatare
     */
    void fill(float[][] tab, int n, float val) {
        for (int i = 0; i < n; i++) {
            Arrays.fill(tab[i], val);
        }
    }

    /**
     * Aplica efectul de eroziune folosind metoda <code>calcEroziune()</code>
     * dupa care copiaza rezultatul in <code>ym</code> si aplica
     * <code>compuneCulori()</code>.
     */
    void eroziune() {
        calcEroziune(ym, s, DIM); // plaseaza rezultatul in s
        copiaza(ym, s, DIM);
        compuneCulori(true);
    }

    /**
     * Daca originea elementului structural coincide cu un pixel negru din
     * imagine, si exista cel putin un vecin care se suprapune peste un pixel
     * alb al imaginii, atunci pixelul acoperit de elementul structural devine
     * alb.
     *
     * @param orig imaginea care trebuie erodata
     * @param dest tabloul in care se pune imaginea erodata
     * @param n latura DIM
     */
    void calcEroziune(float[][] orig, float[][] dest, int n) {
        // parcurgere tabloul prin omiterea primei si ultimei lini / coloane pentru asezarea filtrului
        for (int i = 1; i < n - 1; i++) {
            for (int j = 1; j < n - 1; j++) {
                if (orig[i][j] == 0) { // daca elementul central origine este negru
                    if (orig[i - 1][j] == 255
                            || orig[i + 1][j] == 255
                            || orig[i][j - 1] == 255
                            || orig[i][j + 1] == 255) { // si daca exista un vecin alb
                        dest[i][j] = 255; // atunci originea devine alba
                    } else {
                        dest[i][j] = 0; // altfel originea devine neagra
                    }
                } else {
                    dest[i][j] = 255; // daca originea nu este neagra atunci zonele albe raman albe
                }
            }
        }
    }

    /**
     * Scade imagine erodata folosind <code>calcEroziune()</code> din original,
     * iar pixelii care sunt negri si in imaginea originala si in cea erodata,
     * se transforma in albi si astfel ramane doar conturul.
     */
    void conturBin() {
        calcEroziune(ym, s, DIM); // scadere imagine erodata din original

        for (int i = 0; i < DIM; i++) {
            for (int j = 0; j < DIM; j++) {
                if (s[i][j] == 0 && ym[i][j] == 0) // anulare pixeli din interiorul conturului
                {
                    ym[i][j] = 255; // interiorul se umple cu alb
                }
            }
        }

        compuneCulori(true);
    }

    /**
     * Umple contururile cu negru. Porneste de la un singur pixel cu conditia ca
     * acesta sa fie in interiorul conturului. Se aplica dilatarea pixelului
     * negru respectiv, dupa fiecare etapa de dilatare toti pixelii de pe contur
     * devin albi pentru a nu permite regiunii negre crescatoare sa iasa inafara
     * conturului. Se opreste in momentul in care imaginea ramane nemodificata
     * folosind metoda <code>compara()</code>.
     *
     * @param mouseY coordonata Y a pixelului la apasarea pe label poza
     * @param mouseX coordonata X a pixelului la apasarea pe label poza
     */
    void umplere(int mouseY, int mouseX) {
        fill(s, DIM, 255); // umple tabloul s cu alb (construire interior contur)
        s[mouseY][mouseX] = 0; // un pixel negru in regiunea data de coordonatele mouse-ului

        do {
            copiaza(s2, s, DIM); // copiere imagine s in s2
            calcDil(s2, s, DIM); // dilatare imagine s2 si adaugare in s

            // stergere pixeli de pe contur
            for (int i = 0; i < DIM; i++) {
                for (int j = 0; j < DIM; j++) {
                    if (ym[i][j] == 0) // daca e pe contur
                    {
                        s[i][j] = 255; // sterge conturul (il traseaza cu alb)
                    }
                }
            }
        } while (!compara(s, s2, DIM)); // verifica daca ceea ce am obtinut e diferit fata de ceea ce avem in copie
        // se incheie in momentul in care nu s-a mai modificat nimic

        // adauga conturul umplut la imaginea originala
        for (int i = 0; i < DIM; i++) {
            for (int j = 0; j < DIM; j++) {
                if (s2[i][j] == 0) { // daca suntem in interiorul conturului
                    ym[i][j] = 0;
                }
            }
        }

        compuneCulori(true);
    }

    /* O alta metoda de umplere ar fi algoritmul liniilor de baleiaj (NEOPTIM).
    Parcurge imaginea de la stanga la dreapta pana cand ajunge la o anumita
    linie de contur (pixel negru) si constata ca intra in interiorul
    corpului, atunci umple toti pixelii de pe linia respectiva cu negru pana
    la intalnirea celeilalte linii de contur (pixel negru).*/
    /**
     * Verifica daca la ultimul pas s-a modificat ceva sau nu in imagine. Daca
     * s-a modificat atunci inseamna ca algoritmul poate sa continue, altfel
     * daca nu s-a modificat nimic inseamna ca conturul este plin.
     *
     * @param tab1 tabloul alb s
     * @param tab2 tabloul copie s2
     * @param n latura DIM
     * @return
     */
    boolean compara(float[][] tab1, float[][] tab2, int n) {
        for (int i = 0; i < n; i++) {
            if (!Arrays.equals(tab1[i], tab2[i])) {
                return false;
            }
        }
        return true;
    }

    // MONTARE IMAGINI REGIUNI ///////////////////////////////////////////////////////////////////////////////
    /**
     * Constructor pentru montarea a doua imagini in functie de o a treia
     * imagine.
     *
     * @param idImgPrinc prima imagine din montaj
     * @param idImgSec a doua imagine din montaj
     * @param idImgReg a treia imagine cu regiuni (imagine binara)
     * @param interfata interfata de lucru
     */
    public ImagineDigitala(ImagineDigitala idImgPrinc, ImagineDigitala idImgSec, ImagineDigitala idImgReg, JFrame interfata) {
        this.interfata = interfata;

        for (int i = 0; i < DIM; i++) {
            for (int j = 0; j < DIM; j++) {
                if (idImgReg.ym[i][j] < 100) { // conturul nu e negru; pixelul e preluat din prima fotografie
                    pixeliImagine[DIM * i + j] = idImgPrinc.pixeliImagine[DIM * i + j];
                } else { // pixelul e preluat din a doua fotografie
                    pixeliImagine[DIM * i + j] = idImgSec.pixeliImagine[DIM * i + j];
                }
            }
        }
    }

    /**
     * Contureaza imaginea de montaj facand cu negru toti pixelii luminanta 'y'
     * mai mici decat 100.
     */
    void contureaza() {
        for (int i = 0; i < DIM; i++) {
            for (int j = 0; j < DIM; j++) {
                if (idImagineRegiuni.y[i][j] < 100) {
                    pixeliImagine[DIM * i + j] = 0xff000000;
                }
            }
        }
    }

    // MONTARE IMAGINI CAREU ///////////////////////////////////////////////////////////////////////////////
    /**
     * Constructor pentru montarea a doua imagini si organizarea acestuia pe
     * linii si coloane in functie de falorile din text field.
     *
     * @param id1 prima imagine din montaj
     * @param id2 a doua imagine din montaj
     * @param interfata interfata de lucru
     * @param linii numarul de linii din text field
     * @param coloane numarul de coloane din text field
     */
    ImagineDigitala(ImagineDigitala id1, ImagineDigitala id2, JFrame interfata, int linii, int coloane) {
        this.interfata = interfata;
        int lin, col;

        for (int i = 0; i < DIM; i++) {
            lin = i / linii;

            for (int j = 0; j < DIM; j++) {
                col = j / coloane;

                if (lin % 2 == col % 2) {
                    pixeliImagine[i * DIM + j] = id1.pixeliImagine[DIM * i + j];
                } else {
                    pixeliImagine[i * DIM + j] = id2.pixeliImagine[DIM * i + j];
                }
            }
        }
    }

    // EFECT LUMINOZITATE SI AFISARE RGB ///////////////////////////////////////////////////////////////////////////////
    void efectLuminozitate() {
        int di, dj, d;

        for (int i = 0; i < DIM; i++) {
            for (int j = 0; j < DIM; j++) {
                di = Math.abs(DIM / 2 - i);
                dj = Math.abs(DIM / 2 - j);
                d = di > dj ? di : dj;

                if (d > DIM / 4) {
                    d -= DIM / 4;
                    ym[i][j] = y[i][j] + 2 * d;
                }
            }
        }

        compuneCulori(jCheckBoxAlbNegru.isSelected());
    }

    void compuneRGB() {
        for (int i = 0; i < DIM; i++) {
            for (int j = 0; j < DIM; j++) {
                // dreaptea jos, afisare Blue
                if (i > DIM / 2 && j > DIM / 2) {
                    pixeliImagineM[DIM * i + j] &= 0xff0000ff;
                } // stanga jos, afisare Green
                else if (i > DIM / 2) {
                    pixeliImagineM[DIM * i + j] &= 0xff00ff00;
                } // dreapta sus, afisare Red
                else if (j > DIM / 2) {
                    pixeliImagineM[DIM * i + j] &= 0xffff0000;
                }
            }
        }
    }

    // COLORARE IMAGINE ///////////////////////////////////////////////////////////////////////////////
    int culoareUmplere;

    int culoarePixel(int mouseY, int mouseX) {
        culoareUmplere = pixeliImagine[mouseY * DIM + mouseX];

        return culoareUmplere;
    }

    void umplereCuloare(int mouseY, int mouseX) {
        for (int i = 0; i < DIM; i++) {
            for (int j = 0; j < DIM; j++) {
                s[i][j] = 255; // imagine alba
            }
        }

        s[mouseY][mouseX] = 0; // un pixel negru in regiune

        do {
            copiaza(s2, s, DIM);
            calcDil(s2, s, DIM);

            // stergere pixeli de pe contur
            for (int i = 0; i < DIM; i++) {
                for (int j = 0; j < DIM; j++) {
                    if (ym[i][j] < 150) { // daca e pe contur
                        s[i][j] = 255; // stergere pixel
                    }
                }
            }
        } while (!compara(s, s2, DIM));

        // coloreaza regiunea umpluta
        for (int i = 0; i < DIM; i++) {
            for (int j = 0; j < DIM; j++) {
                if (s2[i][j] == 0) {
                    pixeliImagineM[DIM * i + j] = culoareUmplere;
                }
            }
        }
    }
}
