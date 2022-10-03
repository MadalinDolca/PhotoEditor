package imaginedigitala;

import static imaginedigitala.ImagineDigitala.DIM;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.MemoryImageSource;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

public class Interfata extends javax.swing.JFrame {

    String fisierPoza; // memoreaza numele fisierului imagine
    static ImagineDigitala id; // obiectul clasei imagine digitala
    static Graphics gp; // componenta de desenat pe panel
    Punct a = new Punct(70, 70); // primul punct din grafic panel
    Punct b = new Punct(255 - 70, 255 - 70); // al doilea punct din panel
    Contrast contrast = new Contrast(a, b); // obiect al clasei pentru contrast liniar

    public Interfata() {
        initComponents();

        actualizeazaFisiere(jComboBoxSelector); // adaugare imagini in combo box principal
        actualizeazaFisiere(jComboBoxEstompareRegiuni); // adaugare imagini in combo box estompare
        actualizeazaFisiere(jComboBoxConturRegiuni); // adaugare imagini in combo box trasare contur
        actualizeazaFisiere(jComboBoxImaginePrimaraMontaj); // adaugare imagini in combo box imagine primara montaj
        actualizeazaFisiere(jComboBoxImagineSecundaraMontaj); // adaugare imagini in combo box imagine secundara montaj
        actualizeazaFisiere(jComboBoxImagineRegiuniMontaj); // adaugare imagini in combo box imagine regiuni montaj
        actualizeazaFisiere(jComboBoxImaginePrimaraMontajCareu); // adaugare imagini in combo box imagine montaj careu
        actualizeazaFisiere(jComboBoxImagineSecundaraMontajCareu); // adaugare imagini in combo box imagine montaj careu

        // jPanelContrast = new MyJPanel(contrast); adaugat in DESIGNER
        gp = jPanelContrast.getGraphics(); // preluare componente grafice panel contrast
        jButtonOriginalActionPerformed(null);

        // modificare valori slidere (minimul de la 0 trece la -100)
        jSliderLumi.setMinimum(-100);
        jSliderContrastSin.setMinimum(-100);
        jSliderSaturatie.setMinimum(-100);
        jSliderLuminozitateHSV.setMinimum(-100);
        jSliderPragBinarizare.setMaximum(255);
    }

    /**
     * Initializeaza interfata aplicatiei.
     */
    void initInterfata() {
        jCheckBoxR.setSelected(true);
        jCheckBoxG.setSelected(true);
        jCheckBoxB.setSelected(true);
        jCheckBoxAlbNegru.setSelected(false);

        // repozitionare puncte panel contrast liniar 
        contrast.setA(70, 70);
        contrast.setB(255 - 70, 255 - 70);

        // curatare si redesenare panel contrast liniar
        if (jPanelContrast.isShowing()) {
            gp.setColor(Color.white);
            gp.fillRect(0, 0, jPanelContrast.getWidth(), jPanelContrast.getHeight());
            contrast.afiseaza(gp);
        }

        jSliderLumi.setValue(0);
        jSliderContrastSin.setValue(0);
        jLabelContrastSin.setText("0");
        jRadioButtonContrastLiniar.setSelected(true);

        // modificare valori initiale slidere
        jSliderSaturatie.setValue(0);
        jSliderLuminozitateHSV.setValue(0);
        jLabelSaturatie.setText("0");
        jLabelLuminozitateHSV.setText("0");

        jSliderSaturatieRGB.setValue(50);
        jLabelSaturatieRGB.setText("0");

        jLabelPragBinarizare.setText(jSliderPragBinarizare.getValue() + "");

        jSliderZgomot.setValue(0);
        jLabelZgomot.setText("0");
        jCheckBoxZgomot.setSelected(false);
        jSliderZgomot.setEnabled(false);

        jCheckBoxTrasareContur.setSelected(false);
        jCheckBoxAccentuareDetalii.setSelected(false);
        jSliderPragContur.setValue(15);
        jLabelPragContur.setText("15");
        jSliderPragConturJ.setValue(50);
        jLabelPragConturJ.setText("50");

        jSliderC.setEnabled(false);

        comutareComponente(jPanelTrasareContur, false);
    }

    /**
     * Activeaza / Dezactiveaza elementele componentei specificate.
     *
     * @param container componenta de manipulat
     * @param enable true = activare componenta; false = dezactivare componenta
     */
    public void comutareComponente(Container container, boolean enable) {
        Component[] components = container.getComponents();
        for (Component component : components) {
            component.setEnabled(enable);
            if (component instanceof Container) {
                comutareComponente((Container) component, enable);
            }
        }
    }

    /**
     * Adauga toate numele fisierelor care au extensiile ".jpg", ".png", ".gif"
     * si ".bmp" din directorul <code>cale</code> specificat intr-un ArrayList.
     *
     * @param cale
     * @return ArrayList-ul care contine numele fisierelor
     */
    private ArrayList<String> numeFisiereImagineDinCale(String cale) {
        ArrayList<String> rez = new ArrayList<>();
        File folder = new File(cale);

        if (folder.isDirectory()) {
            String[] lista = folder.list();

            for (int i = 0; i < lista.length; i++) {
                if (lista[i].endsWith(".jpg") || lista[i].endsWith(".png") || lista[i].endsWith(".gif") || lista[i].endsWith(".bmp")) {
                    rez.add(lista[i]);
                }
            }
        }

        return rez;
    }

    private Object makeObj(final String item) {
        return new Object() {
            public String toString() {
                return item;
            }
        };
    }

    /**
     * Adauga numele fisierelor din directorul specificat in combo box.
     *
     * @param jComboBoxSelector combo box-ul in care se vor adauga numele
     * fisierelor
     */
    private void actualizeazaFisiere(javax.swing.JComboBox jComboBoxSelector) {
        ArrayList<String> fisiere = numeFisiereImagineDinCale(".");

        if (fisiere.size() > 0) {
            for (int i = 0; i < fisiere.size(); i++) {
                jComboBoxSelector.addItem(makeObj(fisiere.get(i)));
            }

            jComboBoxSelector.setSelectedIndex(0);
            fisierPoza = fisiere.get(jComboBoxSelector.getSelectedIndex());
        } else {
            System.out.println("Nu exista fisiere imagine in directorul curent...");
            System.exit(0);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroupContrast = new javax.swing.ButtonGroup();
        buttonGroupDimFiltre = new javax.swing.ButtonGroup();
        buttonGroupContur = new javax.swing.ButtonGroup();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanelTabContrast = new javax.swing.JPanel();
        jRadioButtonContrastSinusoidal = new javax.swing.JRadioButton();
        jRadioButtonContrastLiniar = new javax.swing.JRadioButton();
        jRadioButtonContrastAutomat = new javax.swing.JRadioButton();
        jLabel4 = new javax.swing.JLabel();
        jLabelContrastSin = new javax.swing.JLabel();
        jPanelContrast = new MyJPanel(contrast);
        jSliderContrastSin = new javax.swing.JSlider();
        jRadioButtonImagineBinara = new javax.swing.JRadioButton();
        jLabel6 = new javax.swing.JLabel();
        jLabelPragBinarizare = new javax.swing.JLabel();
        jSliderPragBinarizare = new javax.swing.JSlider();
        jLabel7 = new javax.swing.JLabel();
        jSpinnerGrosimeCreion = new javax.swing.JSpinner();
        jCheckBoxBinarizareClick = new javax.swing.JCheckBox();
        jPanelTabSaturatie = new javax.swing.JPanel();
        jPanelSaturatieHSV = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabelSaturatie = new javax.swing.JLabel();
        jLabelLuminozitateHSV = new javax.swing.JLabel();
        jSliderSaturatie = new javax.swing.JSlider();
        jSliderLuminozitateHSV = new javax.swing.JSlider();
        jPanelSaturatieRGB = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jLabelSaturatieRGB = new javax.swing.JLabel();
        jSliderSaturatieRGB = new javax.swing.JSlider();
        jPanelTabHistograma = new javax.swing.JPanel();
        jButtonNormalizareHistograma1 = new javax.swing.JButton();
        jButtonNormalizareHistograma2 = new javax.swing.JButton();
        jPanelTabFiltre = new javax.swing.JPanel();
        jPanelFiltreNetezire = new javax.swing.JPanel();
        jButtonFiltruBox = new javax.swing.JButton();
        jButtonFiltruGaussian = new javax.swing.JButton();
        jLabel8 = new javax.swing.JLabel();
        jRadioButtonDim3 = new javax.swing.JRadioButton();
        jRadioButtonDim5 = new javax.swing.JRadioButton();
        jRadioButtonDim7 = new javax.swing.JRadioButton();
        jPanelFiltreLaplacian = new javax.swing.JPanel();
        jButtonLaplacianHV = new javax.swing.JButton();
        jButtonLaplacianHVD = new javax.swing.JButton();
        jPanelFiltreStatistice = new javax.swing.JPanel();
        jButtonMedian = new javax.swing.JButton();
        jButtonMaxim = new javax.swing.JButton();
        jButtonMinim = new javax.swing.JButton();
        jButtonInterval = new javax.swing.JButton();
        jPanelEstompareRegiuni = new javax.swing.JPanel();
        jButtonEstompareRegiuni = new javax.swing.JButton();
        jComboBoxEstompareRegiuni = new javax.swing.JComboBox<>();
        jPanelTabConturDetalii = new javax.swing.JPanel();
        jPanelTrasareContur = new javax.swing.JPanel();
        jCheckBoxFiltruGaussian = new javax.swing.JCheckBox();
        jRadioButtonVecinatatiInterior = new javax.swing.JRadioButton();
        jRadioButtonVecinatatiExterior = new javax.swing.JRadioButton();
        jRadioButtonConturLaplacian = new javax.swing.JRadioButton();
        jRadioButtonConturPrewitt = new javax.swing.JRadioButton();
        jRadioButtonConturSobel = new javax.swing.JRadioButton();
        jRadioButtonConturInterval = new javax.swing.JRadioButton();
        jRadioButtonConturCanny = new javax.swing.JRadioButton();
        jSliderPragContur = new javax.swing.JSlider();
        jSliderPragConturJ = new javax.swing.JSlider();
        jLabelPragContur = new javax.swing.JLabel();
        jLabelPragConturJ = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jPanelConturRegiuni = new javax.swing.JPanel();
        jButtonConturRegiuni = new javax.swing.JButton();
        jComboBoxConturRegiuni = new javax.swing.JComboBox<>();
        jCheckBoxTrasareContur = new javax.swing.JCheckBox();
        jCheckBoxAccentuareDetalii = new javax.swing.JCheckBox();
        jLabelC = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jSliderC = new javax.swing.JSlider();
        jPanelTransfBinare = new javax.swing.JPanel();
        jButtonDilatareBinara = new javax.swing.JButton();
        jButtonEroziune = new javax.swing.JButton();
        jButtonConturBinar = new javax.swing.JButton();
        jButtonUmplere = new javax.swing.JButton();
        jLabelPozXY = new javax.swing.JLabel();
        jTabbedPane2 = new javax.swing.JTabbedPane();
        jPanelMontajImaginiSigla = new javax.swing.JPanel();
        jComboBoxImagineSecundaraMontaj = new javax.swing.JComboBox<>();
        jButtonMontareImagini = new javax.swing.JButton();
        jComboBoxImagineRegiuniMontaj = new javax.swing.JComboBox<>();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jComboBoxImaginePrimaraMontaj = new javax.swing.JComboBox<>();
        jButtonConturMontaj = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jButtonInversareMontajCareu = new javax.swing.JButton();
        jButtonMontareImaginiCareu = new javax.swing.JButton();
        jLabel16 = new javax.swing.JLabel();
        jComboBoxImagineSecundaraMontajCareu = new javax.swing.JComboBox<>();
        jComboBoxImaginePrimaraMontajCareu = new javax.swing.JComboBox<>();
        jLabel17 = new javax.swing.JLabel();
        jTextFieldLatime = new javax.swing.JTextField();
        jTextFieldInaltime = new javax.swing.JTextField();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jPanelHistograme = new javax.swing.JPanel();
        jPanelHistogramaR = new javax.swing.JPanel();
        jPanelHistogramaG = new javax.swing.JPanel();
        jPanelHistogramaB = new javax.swing.JPanel();
        jPanelHistogramaY = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jCheckBoxAlbNegru = new javax.swing.JCheckBox();
        jComboBoxSelector = new javax.swing.JComboBox<>();
        jButtonOriginal = new javax.swing.JButton();
        jButtonSalvare = new javax.swing.JButton();
        jButtonRefacere = new javax.swing.JButton();
        jTabbedPaneImagini = new javax.swing.JTabbedPane();
        jPanelPoza = new javax.swing.JPanel();
        jLabelPoza = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jLabelImagineColorare = new javax.swing.JLabel();
        jTabbedPane3 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jPanelRGB = new javax.swing.JPanel();
        jCheckBoxR = new javax.swing.JCheckBox();
        jCheckBoxG = new javax.swing.JCheckBox();
        jCheckBoxB = new javax.swing.JCheckBox();
        jButtonNegativare = new javax.swing.JButton();
        jButtonDilatare = new javax.swing.JButton();
        jComboBoxZonaDilatare = new javax.swing.JComboBox<>();
        jLabel5 = new javax.swing.JLabel();
        jLabelLumi = new javax.swing.JLabel();
        jSliderLumi = new javax.swing.JSlider();
        jCheckBoxZgomot = new javax.swing.JCheckBox();
        jLabel9 = new javax.swing.JLabel();
        jLabelZgomot = new javax.swing.JLabel();
        jSliderZgomot = new javax.swing.JSlider();
        jButtonSalvarePNG = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        jButtonEfectLuminozitate = new javax.swing.JButton();
        jButtonAfisareRGB = new javax.swing.JButton();
        jPanel6 = new javax.swing.JPanel();
        jButtonColorare = new javax.swing.JButton();
        jToggleButtonSelectareCuloare = new javax.swing.JToggleButton();
        jLabelAfisareCuloareSelectata = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Prelucrare Imagini");
        setSize(new java.awt.Dimension(640, 480));

        jTabbedPane1.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N

        buttonGroupContrast.add(jRadioButtonContrastSinusoidal);
        jRadioButtonContrastSinusoidal.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        jRadioButtonContrastSinusoidal.setText("Contrast Sinusoidal");
        jRadioButtonContrastSinusoidal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonContrastSinusoidalActionPerformed(evt);
            }
        });

        buttonGroupContrast.add(jRadioButtonContrastLiniar);
        jRadioButtonContrastLiniar.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        jRadioButtonContrastLiniar.setText("Contrast Liniar");
        jRadioButtonContrastLiniar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonContrastLiniarActionPerformed(evt);
            }
        });

        buttonGroupContrast.add(jRadioButtonContrastAutomat);
        jRadioButtonContrastAutomat.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        jRadioButtonContrastAutomat.setText("Contrast Automat");
        jRadioButtonContrastAutomat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonContrastAutomatActionPerformed(evt);
            }
        });

        jLabel4.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        jLabel4.setText("Nivel");

        jLabelContrastSin.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        jLabelContrastSin.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jPanelContrast.setBackground(new java.awt.Color(255, 255, 255));
        jPanelContrast.setPreferredSize(new java.awt.Dimension(254, 254));
        jPanelContrast.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                jPanelContrastMouseDragged(evt);
            }
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                jPanelContrastMouseMoved(evt);
            }
        });

        javax.swing.GroupLayout jPanelContrastLayout = new javax.swing.GroupLayout(jPanelContrast);
        jPanelContrast.setLayout(jPanelContrastLayout);
        jPanelContrastLayout.setHorizontalGroup(
            jPanelContrastLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 254, Short.MAX_VALUE)
        );
        jPanelContrastLayout.setVerticalGroup(
            jPanelContrastLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 254, Short.MAX_VALUE)
        );

        jSliderContrastSin.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSliderContrastSinStateChanged(evt);
            }
        });

        buttonGroupContrast.add(jRadioButtonImagineBinara);
        jRadioButtonImagineBinara.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        jRadioButtonImagineBinara.setText("Imagine Binară");
        jRadioButtonImagineBinara.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonImagineBinaraActionPerformed(evt);
            }
        });

        jLabel6.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        jLabel6.setText("Prag");

        jLabelPragBinarizare.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        jLabelPragBinarizare.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jSliderPragBinarizare.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSliderPragBinarizareStateChanged(evt);
            }
        });

        jLabel7.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        jLabel7.setText("Grosime Creion");

        jSpinnerGrosimeCreion.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        jSpinnerGrosimeCreion.setModel(new javax.swing.SpinnerNumberModel(30, 5, 100, 1));
        jSpinnerGrosimeCreion.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

        jCheckBoxBinarizareClick.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        jCheckBoxBinarizareClick.setText("Binarizare Click");

        javax.swing.GroupLayout jPanelTabContrastLayout = new javax.swing.GroupLayout(jPanelTabContrast);
        jPanelTabContrast.setLayout(jPanelTabContrastLayout);
        jPanelTabContrastLayout.setHorizontalGroup(
            jPanelTabContrastLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelTabContrastLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelTabContrastLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelTabContrastLayout.createSequentialGroup()
                        .addGroup(jPanelTabContrastLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jRadioButtonContrastAutomat)
                            .addComponent(jRadioButtonContrastLiniar))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jPanelContrast, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanelTabContrastLayout.createSequentialGroup()
                        .addGroup(jPanelTabContrastLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jCheckBoxBinarizareClick)
                            .addComponent(jRadioButtonImagineBinara))
                        .addGap(52, 157, Short.MAX_VALUE)
                        .addGroup(jPanelTabContrastLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelTabContrastLayout.createSequentialGroup()
                                .addComponent(jLabel6)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabelPragBinarizare, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jSliderPragBinarizare, javax.swing.GroupLayout.PREFERRED_SIZE, 224, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanelTabContrastLayout.createSequentialGroup()
                                .addComponent(jLabel7)
                                .addGap(18, 18, 18)
                                .addComponent(jSpinnerGrosimeCreion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(jPanelTabContrastLayout.createSequentialGroup()
                        .addComponent(jRadioButtonContrastSinusoidal)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabelContrastSin, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSliderContrastSin, javax.swing.GroupLayout.PREFERRED_SIZE, 224, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanelTabContrastLayout.setVerticalGroup(
            jPanelTabContrastLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelTabContrastLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelTabContrastLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jCheckBoxBinarizareClick)
                    .addComponent(jLabel7)
                    .addComponent(jSpinnerGrosimeCreion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanelTabContrastLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelTabContrastLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jRadioButtonImagineBinara)
                        .addComponent(jLabel6))
                    .addComponent(jLabelPragBinarizare, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jSliderPragBinarizare, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanelTabContrastLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelTabContrastLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jRadioButtonContrastSinusoidal)
                        .addComponent(jLabel4))
                    .addComponent(jSliderContrastSin, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelContrastSin, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(jPanelTabContrastLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelTabContrastLayout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(jRadioButtonContrastLiniar)
                        .addGap(18, 18, 18)
                        .addComponent(jRadioButtonContrastAutomat))
                    .addGroup(jPanelTabContrastLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jPanelContrast, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(29, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Contrast", jPanelTabContrast);

        jPanelSaturatieHSV.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "HSV", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 18))); // NOI18N

        jLabel1.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        jLabel1.setText("Saturație");

        jLabel2.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        jLabel2.setText("Luminozitate");

        jLabelSaturatie.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        jLabelSaturatie.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabelLuminozitateHSV.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        jLabelLuminozitateHSV.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jSliderSaturatie.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSliderSaturatieStateChanged(evt);
            }
        });

        jSliderLuminozitateHSV.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSliderLuminozitateHSVStateChanged(evt);
            }
        });

        javax.swing.GroupLayout jPanelSaturatieHSVLayout = new javax.swing.GroupLayout(jPanelSaturatieHSV);
        jPanelSaturatieHSV.setLayout(jPanelSaturatieHSVLayout);
        jPanelSaturatieHSVLayout.setHorizontalGroup(
            jPanelSaturatieHSVLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelSaturatieHSVLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelSaturatieHSVLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addComponent(jLabel1))
                .addGroup(jPanelSaturatieHSVLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelSaturatieHSVLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabelLuminozitateHSV, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanelSaturatieHSVLayout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(jLabelSaturatie, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelSaturatieHSVLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSliderSaturatie, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jSliderLuminozitateHSV, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanelSaturatieHSVLayout.setVerticalGroup(
            jPanelSaturatieHSVLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelSaturatieHSVLayout.createSequentialGroup()
                .addGroup(jPanelSaturatieHSVLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabelSaturatie, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jSliderSaturatie, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanelSaturatieHSVLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSliderLuminozitateHSV, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanelSaturatieHSVLayout.createSequentialGroup()
                        .addGroup(jPanelSaturatieHSVLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addComponent(jLabelLuminozitateHSV, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 6, Short.MAX_VALUE)))
                .addContainerGap())
        );

        jPanelSaturatieRGB.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "RGB", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 18))); // NOI18N

        jLabel3.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        jLabel3.setText("Saturație");

        jLabelSaturatieRGB.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        jLabelSaturatieRGB.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jSliderSaturatieRGB.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSliderSaturatieRGBStateChanged(evt);
            }
        });

        javax.swing.GroupLayout jPanelSaturatieRGBLayout = new javax.swing.GroupLayout(jPanelSaturatieRGB);
        jPanelSaturatieRGB.setLayout(jPanelSaturatieRGBLayout);
        jPanelSaturatieRGBLayout.setHorizontalGroup(
            jPanelSaturatieRGBLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelSaturatieRGBLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3)
                .addGap(36, 36, 36)
                .addComponent(jLabelSaturatieRGB, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSliderSaturatieRGB, javax.swing.GroupLayout.DEFAULT_SIZE, 319, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanelSaturatieRGBLayout.setVerticalGroup(
            jPanelSaturatieRGBLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelSaturatieRGBLayout.createSequentialGroup()
                .addGroup(jPanelSaturatieRGBLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabelSaturatieRGB, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jSliderSaturatieRGB, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 12, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanelTabSaturatieLayout = new javax.swing.GroupLayout(jPanelTabSaturatie);
        jPanelTabSaturatie.setLayout(jPanelTabSaturatieLayout);
        jPanelTabSaturatieLayout.setHorizontalGroup(
            jPanelTabSaturatieLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelTabSaturatieLayout.createSequentialGroup()
                .addContainerGap(66, Short.MAX_VALUE)
                .addGroup(jPanelTabSaturatieLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanelSaturatieHSV, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanelSaturatieRGB, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(65, Short.MAX_VALUE))
        );
        jPanelTabSaturatieLayout.setVerticalGroup(
            jPanelTabSaturatieLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelTabSaturatieLayout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addComponent(jPanelSaturatieHSV, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanelSaturatieRGB, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(227, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Saturație", jPanelTabSaturatie);

        jButtonNormalizareHistograma1.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        jButtonNormalizareHistograma1.setText("Normalizare Histograma 1");
        jButtonNormalizareHistograma1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonNormalizareHistograma1ActionPerformed(evt);
            }
        });

        jButtonNormalizareHistograma2.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        jButtonNormalizareHistograma2.setText("Normalizare Histograma 2");
        jButtonNormalizareHistograma2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonNormalizareHistograma2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelTabHistogramaLayout = new javax.swing.GroupLayout(jPanelTabHistograma);
        jPanelTabHistograma.setLayout(jPanelTabHistogramaLayout);
        jPanelTabHistogramaLayout.setHorizontalGroup(
            jPanelTabHistogramaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelTabHistogramaLayout.createSequentialGroup()
                .addContainerGap(200, Short.MAX_VALUE)
                .addGroup(jPanelTabHistogramaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButtonNormalizareHistograma1)
                    .addComponent(jButtonNormalizareHistograma2))
                .addContainerGap(200, Short.MAX_VALUE))
        );
        jPanelTabHistogramaLayout.setVerticalGroup(
            jPanelTabHistogramaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelTabHistogramaLayout.createSequentialGroup()
                .addGap(36, 36, 36)
                .addComponent(jButtonNormalizareHistograma1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonNormalizareHistograma2)
                .addContainerGap(330, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Histograma", jPanelTabHistograma);

        jPanelFiltreNetezire.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Netezire", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 18))); // NOI18N

        jButtonFiltruBox.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        jButtonFiltruBox.setText("Box");
        jButtonFiltruBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonFiltruBoxActionPerformed(evt);
            }
        });

        jButtonFiltruGaussian.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        jButtonFiltruGaussian.setText("Gaussian");
        jButtonFiltruGaussian.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonFiltruGaussianActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelFiltreNetezireLayout = new javax.swing.GroupLayout(jPanelFiltreNetezire);
        jPanelFiltreNetezire.setLayout(jPanelFiltreNetezireLayout);
        jPanelFiltreNetezireLayout.setHorizontalGroup(
            jPanelFiltreNetezireLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelFiltreNetezireLayout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(jPanelFiltreNetezireLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jButtonFiltruBox, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonFiltruGaussian, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanelFiltreNetezireLayout.setVerticalGroup(
            jPanelFiltreNetezireLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelFiltreNetezireLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButtonFiltruBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonFiltruGaussian)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel8.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        jLabel8.setText("Dimensiune Netezire & Statistice:");

        buttonGroupDimFiltre.add(jRadioButtonDim3);
        jRadioButtonDim3.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        jRadioButtonDim3.setSelected(true);
        jRadioButtonDim3.setText("3");

        buttonGroupDimFiltre.add(jRadioButtonDim5);
        jRadioButtonDim5.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        jRadioButtonDim5.setText("5");

        buttonGroupDimFiltre.add(jRadioButtonDim7);
        jRadioButtonDim7.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        jRadioButtonDim7.setText("7");

        jPanelFiltreLaplacian.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Laplacian", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 18))); // NOI18N

        jButtonLaplacianHV.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        jButtonLaplacianHV.setText("H+V");
        jButtonLaplacianHV.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonLaplacianHVActionPerformed(evt);
            }
        });

        jButtonLaplacianHVD.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        jButtonLaplacianHVD.setText("H+V+D");
        jButtonLaplacianHVD.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonLaplacianHVDActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelFiltreLaplacianLayout = new javax.swing.GroupLayout(jPanelFiltreLaplacian);
        jPanelFiltreLaplacian.setLayout(jPanelFiltreLaplacianLayout);
        jPanelFiltreLaplacianLayout.setHorizontalGroup(
            jPanelFiltreLaplacianLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelFiltreLaplacianLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelFiltreLaplacianLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButtonLaplacianHV, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonLaplacianHVD, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanelFiltreLaplacianLayout.setVerticalGroup(
            jPanelFiltreLaplacianLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelFiltreLaplacianLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButtonLaplacianHV)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonLaplacianHVD)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanelFiltreStatistice.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Statistice", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 18))); // NOI18N

        jButtonMedian.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        jButtonMedian.setText("Median");
        jButtonMedian.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonMedianActionPerformed(evt);
            }
        });

        jButtonMaxim.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        jButtonMaxim.setText("Max");
        jButtonMaxim.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonMaximActionPerformed(evt);
            }
        });

        jButtonMinim.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        jButtonMinim.setText("Min");
        jButtonMinim.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonMinimActionPerformed(evt);
            }
        });

        jButtonInterval.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        jButtonInterval.setText("Interval");
        jButtonInterval.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonIntervalActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelFiltreStatisticeLayout = new javax.swing.GroupLayout(jPanelFiltreStatistice);
        jPanelFiltreStatistice.setLayout(jPanelFiltreStatisticeLayout);
        jPanelFiltreStatisticeLayout.setHorizontalGroup(
            jPanelFiltreStatisticeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelFiltreStatisticeLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelFiltreStatisticeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButtonMedian, javax.swing.GroupLayout.DEFAULT_SIZE, 130, Short.MAX_VALUE)
                    .addComponent(jButtonMaxim, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButtonMinim, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButtonInterval, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanelFiltreStatisticeLayout.setVerticalGroup(
            jPanelFiltreStatisticeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelFiltreStatisticeLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButtonMedian)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonMaxim)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonMinim)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonInterval)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanelEstompareRegiuni.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Estompare Regiuni cu Imagine Binară", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 18))); // NOI18N

        jButtonEstompareRegiuni.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        jButtonEstompareRegiuni.setText("Estompare Regiuni");
        jButtonEstompareRegiuni.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonEstompareRegiuniActionPerformed(evt);
            }
        });

        jComboBoxEstompareRegiuni.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N

        javax.swing.GroupLayout jPanelEstompareRegiuniLayout = new javax.swing.GroupLayout(jPanelEstompareRegiuni);
        jPanelEstompareRegiuni.setLayout(jPanelEstompareRegiuniLayout);
        jPanelEstompareRegiuniLayout.setHorizontalGroup(
            jPanelEstompareRegiuniLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelEstompareRegiuniLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButtonEstompareRegiuni)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jComboBoxEstompareRegiuni, javax.swing.GroupLayout.PREFERRED_SIZE, 179, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanelEstompareRegiuniLayout.setVerticalGroup(
            jPanelEstompareRegiuniLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelEstompareRegiuniLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelEstompareRegiuniLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonEstompareRegiuni)
                    .addComponent(jComboBoxEstompareRegiuni, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanelTabFiltreLayout = new javax.swing.GroupLayout(jPanelTabFiltre);
        jPanelTabFiltre.setLayout(jPanelTabFiltreLayout);
        jPanelTabFiltreLayout.setHorizontalGroup(
            jPanelTabFiltreLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelTabFiltreLayout.createSequentialGroup()
                .addGap(52, 52, 52)
                .addGroup(jPanelTabFiltreLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanelEstompareRegiuni, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanelTabFiltreLayout.createSequentialGroup()
                        .addComponent(jLabel8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jRadioButtonDim3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jRadioButtonDim5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jRadioButtonDim7))
                    .addGroup(jPanelTabFiltreLayout.createSequentialGroup()
                        .addComponent(jPanelFiltreNetezire, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jPanelFiltreLaplacian, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jPanelFiltreStatistice, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(63, Short.MAX_VALUE))
        );
        jPanelTabFiltreLayout.setVerticalGroup(
            jPanelTabFiltreLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelTabFiltreLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(jPanelTabFiltreLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jRadioButtonDim3)
                    .addComponent(jRadioButtonDim5)
                    .addComponent(jRadioButtonDim7)
                    .addComponent(jLabel8))
                .addGap(18, 18, 18)
                .addGroup(jPanelTabFiltreLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanelFiltreStatistice, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanelFiltreNetezire, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanelFiltreLaplacian, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jPanelEstompareRegiuni, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(56, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Filtre", jPanelTabFiltre);

        jPanelTrasareContur.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Contur", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 18))); // NOI18N

        jCheckBoxFiltruGaussian.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        jCheckBoxFiltruGaussian.setText("Filtru Gaussian");
        jCheckBoxFiltruGaussian.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxFiltruGaussianActionPerformed(evt);
            }
        });

        buttonGroupContur.add(jRadioButtonVecinatatiInterior);
        jRadioButtonVecinatatiInterior.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        jRadioButtonVecinatatiInterior.setText("Vecinatati interior");
        jRadioButtonVecinatatiInterior.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonVecinatatiInteriorActionPerformed(evt);
            }
        });

        buttonGroupContur.add(jRadioButtonVecinatatiExterior);
        jRadioButtonVecinatatiExterior.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        jRadioButtonVecinatatiExterior.setText("Vecinatati exterior");
        jRadioButtonVecinatatiExterior.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonVecinatatiExteriorActionPerformed(evt);
            }
        });

        buttonGroupContur.add(jRadioButtonConturLaplacian);
        jRadioButtonConturLaplacian.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        jRadioButtonConturLaplacian.setText("Laplacian");
        jRadioButtonConturLaplacian.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonConturLaplacianActionPerformed(evt);
            }
        });

        buttonGroupContur.add(jRadioButtonConturPrewitt);
        jRadioButtonConturPrewitt.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        jRadioButtonConturPrewitt.setText("Prewitt");
        jRadioButtonConturPrewitt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonConturPrewittActionPerformed(evt);
            }
        });

        buttonGroupContur.add(jRadioButtonConturSobel);
        jRadioButtonConturSobel.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        jRadioButtonConturSobel.setText("Sobel");
        jRadioButtonConturSobel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonConturSobelActionPerformed(evt);
            }
        });

        buttonGroupContur.add(jRadioButtonConturInterval);
        jRadioButtonConturInterval.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        jRadioButtonConturInterval.setText("Interval");
        jRadioButtonConturInterval.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonConturIntervalActionPerformed(evt);
            }
        });

        buttonGroupContur.add(jRadioButtonConturCanny);
        jRadioButtonConturCanny.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        jRadioButtonConturCanny.setText("Canny");
        jRadioButtonConturCanny.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonConturCannyActionPerformed(evt);
            }
        });

        jSliderPragContur.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSliderPragConturStateChanged(evt);
            }
        });

        jSliderPragConturJ.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSliderPragConturJStateChanged(evt);
            }
        });

        jLabelPragContur.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        jLabelPragContur.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabelPragConturJ.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        jLabelPragConturJ.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel10.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        jLabel10.setText("Prag");

        jLabel11.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        jLabel11.setText("Prag J Canny");

        jPanelConturRegiuni.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Trasare Contur cu Imagine Binară", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 18))); // NOI18N

        jButtonConturRegiuni.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        jButtonConturRegiuni.setText("Contur Regiuni");
        jButtonConturRegiuni.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonConturRegiuniActionPerformed(evt);
            }
        });

        jComboBoxConturRegiuni.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N

        javax.swing.GroupLayout jPanelConturRegiuniLayout = new javax.swing.GroupLayout(jPanelConturRegiuni);
        jPanelConturRegiuni.setLayout(jPanelConturRegiuniLayout);
        jPanelConturRegiuniLayout.setHorizontalGroup(
            jPanelConturRegiuniLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelConturRegiuniLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButtonConturRegiuni)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jComboBoxConturRegiuni, 0, 170, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanelConturRegiuniLayout.setVerticalGroup(
            jPanelConturRegiuniLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelConturRegiuniLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelConturRegiuniLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonConturRegiuni)
                    .addComponent(jComboBoxConturRegiuni, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanelTrasareConturLayout = new javax.swing.GroupLayout(jPanelTrasareContur);
        jPanelTrasareContur.setLayout(jPanelTrasareConturLayout);
        jPanelTrasareConturLayout.setHorizontalGroup(
            jPanelTrasareConturLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelTrasareConturLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelTrasareConturLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelTrasareConturLayout.createSequentialGroup()
                        .addComponent(jRadioButtonVecinatatiInterior)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jCheckBoxFiltruGaussian))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelTrasareConturLayout.createSequentialGroup()
                        .addGroup(jPanelTrasareConturLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanelTrasareConturLayout.createSequentialGroup()
                                .addComponent(jRadioButtonConturLaplacian)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel11))
                            .addGroup(jPanelTrasareConturLayout.createSequentialGroup()
                                .addComponent(jRadioButtonVecinatatiExterior)
                                .addGap(39, 39, 39)
                                .addComponent(jLabel10)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanelTrasareConturLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabelPragContur, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabelPragConturJ, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanelTrasareConturLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jSliderPragContur, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jSliderPragConturJ, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(jPanelTrasareConturLayout.createSequentialGroup()
                        .addGroup(jPanelTrasareConturLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jRadioButtonConturPrewitt)
                            .addComponent(jRadioButtonConturCanny)
                            .addComponent(jRadioButtonConturSobel))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanelTrasareConturLayout.createSequentialGroup()
                        .addComponent(jRadioButtonConturInterval)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 143, Short.MAX_VALUE)
                        .addComponent(jPanelConturRegiuni, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanelTrasareConturLayout.setVerticalGroup(
            jPanelTrasareConturLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelTrasareConturLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelTrasareConturLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelTrasareConturLayout.createSequentialGroup()
                        .addGroup(jPanelTrasareConturLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jRadioButtonVecinatatiInterior, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jCheckBoxFiltruGaussian))
                        .addGap(5, 5, 5)
                        .addGroup(jPanelTrasareConturLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jRadioButtonVecinatatiExterior)
                            .addComponent(jLabel10)))
                    .addComponent(jSliderPragContur, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelPragContur, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(5, 5, 5)
                .addGroup(jPanelTrasareConturLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSliderPragConturJ, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanelTrasareConturLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jRadioButtonConturLaplacian)
                        .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabelPragConturJ, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(5, 5, 5)
                .addComponent(jRadioButtonConturPrewitt)
                .addGap(5, 5, 5)
                .addGroup(jPanelTrasareConturLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanelTrasareConturLayout.createSequentialGroup()
                        .addComponent(jRadioButtonConturSobel)
                        .addGap(5, 5, 5)
                        .addComponent(jRadioButtonConturInterval)
                        .addGap(5, 5, 5)
                        .addComponent(jRadioButtonConturCanny))
                    .addComponent(jPanelConturRegiuni, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jCheckBoxTrasareContur.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        jCheckBoxTrasareContur.setText("Trasare contur");
        jCheckBoxTrasareContur.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxTrasareConturActionPerformed(evt);
            }
        });

        jCheckBoxAccentuareDetalii.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        jCheckBoxAccentuareDetalii.setText("Accentuare detalii");
        jCheckBoxAccentuareDetalii.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxAccentuareDetaliiActionPerformed(evt);
            }
        });

        jLabelC.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        jLabelC.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel12.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        jLabel12.setText("c");

        jSliderC.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSliderCStateChanged(evt);
            }
        });

        javax.swing.GroupLayout jPanelTabConturDetaliiLayout = new javax.swing.GroupLayout(jPanelTabConturDetalii);
        jPanelTabConturDetalii.setLayout(jPanelTabConturDetaliiLayout);
        jPanelTabConturDetaliiLayout.setHorizontalGroup(
            jPanelTabConturDetaliiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelTabConturDetaliiLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelTabConturDetaliiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanelTrasareContur, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanelTabConturDetaliiLayout.createSequentialGroup()
                        .addComponent(jCheckBoxTrasareContur)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanelTabConturDetaliiLayout.createSequentialGroup()
                        .addComponent(jCheckBoxAccentuareDetalii)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel12)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabelC, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSliderC, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanelTabConturDetaliiLayout.setVerticalGroup(
            jPanelTabConturDetaliiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelTabConturDetaliiLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jCheckBoxTrasareContur)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanelTrasareContur, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(23, 23, 23)
                .addGroup(jPanelTabConturDetaliiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanelTabConturDetaliiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jCheckBoxAccentuareDetalii)
                        .addComponent(jLabel12))
                    .addComponent(jLabelC, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jSliderC, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(24, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Contur/Detalii", jPanelTabConturDetalii);

        jButtonDilatareBinara.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        jButtonDilatareBinara.setText("Dilatare");
        jButtonDilatareBinara.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDilatareBinaraActionPerformed(evt);
            }
        });

        jButtonEroziune.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        jButtonEroziune.setText("Eroziune");
        jButtonEroziune.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonEroziuneActionPerformed(evt);
            }
        });

        jButtonConturBinar.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        jButtonConturBinar.setText("Trasare Contur");
        jButtonConturBinar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonConturBinarActionPerformed(evt);
            }
        });

        jButtonUmplere.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        jButtonUmplere.setText("Umplere");
        jButtonUmplere.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonUmplereActionPerformed(evt);
            }
        });

        jLabelPozXY.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        jLabelPozXY.setText(" ");
        jLabelPozXY.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jTabbedPane2.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N

        jComboBoxImagineSecundaraMontaj.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N

        jButtonMontareImagini.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        jButtonMontareImagini.setText("Montare Imagini");
        jButtonMontareImagini.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonMontareImaginiActionPerformed(evt);
            }
        });

        jComboBoxImagineRegiuniMontaj.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N

        jLabel13.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        jLabel13.setText("Imagine Regiuni");

        jLabel14.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        jLabel14.setText("Imagine Secundară");

        jLabel15.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        jLabel15.setText("Imagine Primară");

        jComboBoxImaginePrimaraMontaj.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N

        jButtonConturMontaj.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        jButtonConturMontaj.setText("Contur Montaj");
        jButtonConturMontaj.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonConturMontajActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelMontajImaginiSiglaLayout = new javax.swing.GroupLayout(jPanelMontajImaginiSigla);
        jPanelMontajImaginiSigla.setLayout(jPanelMontajImaginiSiglaLayout);
        jPanelMontajImaginiSiglaLayout.setHorizontalGroup(
            jPanelMontajImaginiSiglaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelMontajImaginiSiglaLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelMontajImaginiSiglaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jButtonConturMontaj, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanelMontajImaginiSiglaLayout.createSequentialGroup()
                        .addGroup(jPanelMontajImaginiSiglaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel14)
                            .addComponent(jLabel13)
                            .addComponent(jLabel15))
                        .addGap(18, 18, 18)
                        .addGroup(jPanelMontajImaginiSiglaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jComboBoxImaginePrimaraMontaj, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jComboBoxImagineRegiuniMontaj, 0, 422, Short.MAX_VALUE)
                            .addComponent(jComboBoxImagineSecundaraMontaj, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addComponent(jButtonMontareImagini, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanelMontajImaginiSiglaLayout.setVerticalGroup(
            jPanelMontajImaginiSiglaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelMontajImaginiSiglaLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelMontajImaginiSiglaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jComboBoxImaginePrimaraMontaj, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel15))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelMontajImaginiSiglaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jComboBoxImagineSecundaraMontaj, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel14))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelMontajImaginiSiglaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel13)
                    .addComponent(jComboBoxImagineRegiuniMontaj, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonMontareImagini)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonConturMontaj)
                .addContainerGap())
        );

        jTabbedPane2.addTab("Montaj Imagini cu Regiuni", jPanelMontajImaginiSigla);

        jButtonInversareMontajCareu.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        jButtonInversareMontajCareu.setText("Inversează Montaj Careu");
        jButtonInversareMontajCareu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonInversareMontajCareuActionPerformed(evt);
            }
        });

        jButtonMontareImaginiCareu.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        jButtonMontareImaginiCareu.setText("Montare Imagini Careu");
        jButtonMontareImaginiCareu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonMontareImaginiCareuActionPerformed(evt);
            }
        });

        jLabel16.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        jLabel16.setText("Imagine Secundară");

        jComboBoxImagineSecundaraMontajCareu.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N

        jComboBoxImaginePrimaraMontajCareu.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N

        jLabel17.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        jLabel17.setText("Imagine Primară");

        jTextFieldLatime.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N

        jTextFieldInaltime.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N

        jLabel18.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        jLabel18.setText("Lățime");

        jLabel19.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        jLabel19.setText("Înălțime");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel18)
                            .addComponent(jLabel16)
                            .addComponent(jLabel19)
                            .addComponent(jLabel17))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jTextFieldLatime, javax.swing.GroupLayout.PREFERRED_SIZE, 147, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jTextFieldInaltime, javax.swing.GroupLayout.PREFERRED_SIZE, 147, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(0, 275, Short.MAX_VALUE))
                            .addComponent(jComboBoxImagineSecundaraMontajCareu, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jComboBoxImaginePrimaraMontajCareu, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jButtonMontareImaginiCareu, javax.swing.GroupLayout.PREFERRED_SIZE, 290, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButtonInversareMontajCareu, javax.swing.GroupLayout.PREFERRED_SIZE, 266, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jComboBoxImaginePrimaraMontajCareu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel17))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jComboBoxImagineSecundaraMontajCareu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel16))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextFieldLatime, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel18))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextFieldInaltime, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel19))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonInversareMontajCareu)
                    .addComponent(jButtonMontareImaginiCareu))
                .addContainerGap())
        );

        jTabbedPane2.addTab("Montaj Imagini Careu", jPanel4);

        javax.swing.GroupLayout jPanelTransfBinareLayout = new javax.swing.GroupLayout(jPanelTransfBinare);
        jPanelTransfBinare.setLayout(jPanelTransfBinareLayout);
        jPanelTransfBinareLayout.setHorizontalGroup(
            jPanelTransfBinareLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelTransfBinareLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelTransfBinareLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTabbedPane2)
                    .addGroup(jPanelTransfBinareLayout.createSequentialGroup()
                        .addGroup(jPanelTransfBinareLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jButtonEroziune, javax.swing.GroupLayout.PREFERRED_SIZE, 152, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButtonDilatareBinara, javax.swing.GroupLayout.PREFERRED_SIZE, 152, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanelTransfBinareLayout.createSequentialGroup()
                                .addGroup(jPanelTransfBinareLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(jButtonUmplere, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jButtonConturBinar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addGap(18, 18, 18)
                                .addComponent(jLabelPozXY, javax.swing.GroupLayout.PREFERRED_SIZE, 214, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanelTransfBinareLayout.setVerticalGroup(
            jPanelTransfBinareLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelTransfBinareLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButtonDilatareBinara)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonEroziune)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonConturBinar)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelTransfBinareLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonUmplere)
                    .addComponent(jLabelPozXY, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jTabbedPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 236, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(1, 1, 1))
        );

        jTabbedPane1.addTab("Transformări Binare", jPanelTransfBinare);

        jPanelHistograme.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        javax.swing.GroupLayout jPanelHistogramaRLayout = new javax.swing.GroupLayout(jPanelHistogramaR);
        jPanelHistogramaR.setLayout(jPanelHistogramaRLayout);
        jPanelHistogramaRLayout.setHorizontalGroup(
            jPanelHistogramaRLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanelHistogramaRLayout.setVerticalGroup(
            jPanelHistogramaRLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanelHistogramaGLayout = new javax.swing.GroupLayout(jPanelHistogramaG);
        jPanelHistogramaG.setLayout(jPanelHistogramaGLayout);
        jPanelHistogramaGLayout.setHorizontalGroup(
            jPanelHistogramaGLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanelHistogramaGLayout.setVerticalGroup(
            jPanelHistogramaGLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 126, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanelHistogramaBLayout = new javax.swing.GroupLayout(jPanelHistogramaB);
        jPanelHistogramaB.setLayout(jPanelHistogramaBLayout);
        jPanelHistogramaBLayout.setHorizontalGroup(
            jPanelHistogramaBLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanelHistogramaBLayout.setVerticalGroup(
            jPanelHistogramaBLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanelHistogramaYLayout = new javax.swing.GroupLayout(jPanelHistogramaY);
        jPanelHistogramaY.setLayout(jPanelHistogramaYLayout);
        jPanelHistogramaYLayout.setHorizontalGroup(
            jPanelHistogramaYLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanelHistogramaYLayout.setVerticalGroup(
            jPanelHistogramaYLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanelHistogrameLayout = new javax.swing.GroupLayout(jPanelHistograme);
        jPanelHistograme.setLayout(jPanelHistogrameLayout);
        jPanelHistogrameLayout.setHorizontalGroup(
            jPanelHistogrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelHistogrameLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanelHistogramaR, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanelHistogramaG, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanelHistogramaB, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanelHistogramaY, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanelHistogrameLayout.setVerticalGroup(
            jPanelHistogrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelHistogrameLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelHistogrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanelHistogramaG, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanelHistogramaY, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanelHistogramaR, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanelHistogramaB, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jCheckBoxAlbNegru.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        jCheckBoxAlbNegru.setText("Alb / Negru");
        jCheckBoxAlbNegru.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxAlbNegruActionPerformed(evt);
            }
        });

        jComboBoxSelector.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        jComboBoxSelector.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboBoxSelectorItemStateChanged(evt);
            }
        });

        jButtonOriginal.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        jButtonOriginal.setText("Original");
        jButtonOriginal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonOriginalActionPerformed(evt);
            }
        });

        jButtonSalvare.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        jButtonSalvare.setText("Salvare");
        jButtonSalvare.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSalvareActionPerformed(evt);
            }
        });

        jButtonRefacere.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        jButtonRefacere.setText("Refacere");
        jButtonRefacere.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonRefacereActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jCheckBoxAlbNegru)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jComboBoxSelector, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonOriginal, javax.swing.GroupLayout.PREFERRED_SIZE, 156, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonSalvare, javax.swing.GroupLayout.PREFERRED_SIZE, 143, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonRefacere, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jComboBoxSelector, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonOriginal)
                    .addComponent(jButtonSalvare)
                    .addComponent(jButtonRefacere))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jCheckBoxAlbNegru)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jCheckBoxAlbNegru.getAccessibleContext().setAccessibleName("");

        jTabbedPaneImagini.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N

        jLabelPoza.setBackground(new java.awt.Color(255, 255, 255));
        jLabelPoza.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabelPozaMouseClicked(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jLabelPozaMousePressed(evt);
            }
        });

        javax.swing.GroupLayout jPanelPozaLayout = new javax.swing.GroupLayout(jPanelPoza);
        jPanelPoza.setLayout(jPanelPozaLayout);
        jPanelPozaLayout.setHorizontalGroup(
            jPanelPozaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelPozaLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabelPoza, javax.swing.GroupLayout.PREFERRED_SIZE, 512, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanelPozaLayout.setVerticalGroup(
            jPanelPozaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelPozaLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabelPoza, javax.swing.GroupLayout.PREFERRED_SIZE, 512, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jTabbedPaneImagini.addTab("Imagine", jPanelPoza);

        jLabelImagineColorare.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jLabelImagineColorareMousePressed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabelImagineColorare, javax.swing.GroupLayout.DEFAULT_SIZE, 512, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabelImagineColorare, javax.swing.GroupLayout.DEFAULT_SIZE, 512, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPaneImagini.addTab("Imagine Colorare", jPanel3);

        jTabbedPane3.setTabPlacement(javax.swing.JTabbedPane.BOTTOM);
        jTabbedPane3.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N

        jPanelRGB.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Afișează", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 18))); // NOI18N
        jPanelRGB.setToolTipText("");

        jCheckBoxR.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        jCheckBoxR.setText("R");
        jCheckBoxR.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxRActionPerformed(evt);
            }
        });

        jCheckBoxG.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        jCheckBoxG.setText("G");
        jCheckBoxG.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxGActionPerformed(evt);
            }
        });

        jCheckBoxB.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        jCheckBoxB.setText("B");
        jCheckBoxB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxBActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelRGBLayout = new javax.swing.GroupLayout(jPanelRGB);
        jPanelRGB.setLayout(jPanelRGBLayout);
        jPanelRGBLayout.setHorizontalGroup(
            jPanelRGBLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelRGBLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jCheckBoxR)
                .addGap(18, 18, 18)
                .addComponent(jCheckBoxG)
                .addGap(18, 18, 18)
                .addComponent(jCheckBoxB)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanelRGBLayout.setVerticalGroup(
            jPanelRGBLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelRGBLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelRGBLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jCheckBoxR)
                    .addComponent(jCheckBoxG)
                    .addComponent(jCheckBoxB))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jCheckBoxR.getAccessibleContext().setAccessibleName("");
        jCheckBoxG.getAccessibleContext().setAccessibleName("");
        jCheckBoxB.getAccessibleContext().setAccessibleName("");

        jButtonNegativare.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        jButtonNegativare.setText("Negativare");
        jButtonNegativare.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonNegativareActionPerformed(evt);
            }
        });

        jButtonDilatare.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        jButtonDilatare.setText("Dilatare");
        jButtonDilatare.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDilatareActionPerformed(evt);
            }
        });

        jComboBoxZonaDilatare.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        jComboBoxZonaDilatare.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Stânga Sus", "Stânga Jos", "Central", "Dreapta Sus", "Dreapta Jos" }));

        jLabel5.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        jLabel5.setText("Luminozitate");

        jLabelLumi.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        jLabelLumi.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jSliderLumi.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSliderLumiStateChanged(evt);
            }
        });

        jCheckBoxZgomot.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        jCheckBoxZgomot.setText("Adaugare Zgomot");
        jCheckBoxZgomot.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxZgomotActionPerformed(evt);
            }
        });

        jLabel9.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        jLabel9.setText("Nivel");

        jLabelZgomot.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        jLabelZgomot.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jSliderZgomot.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSliderZgomotStateChanged(evt);
            }
        });

        jButtonSalvarePNG.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        jButtonSalvarePNG.setText("Salvare PNG");
        jButtonSalvarePNG.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSalvarePNGActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jPanelRGB, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButtonSalvarePNG)
                        .addGap(18, 18, 18)
                        .addComponent(jButtonNegativare, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jButtonDilatare, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jComboBoxZonaDilatare, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jCheckBoxZgomot)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel9))
                            .addComponent(jLabel5))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabelZgomot, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabelLumi, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jSliderLumi, javax.swing.GroupLayout.DEFAULT_SIZE, 234, Short.MAX_VALUE)
                            .addComponent(jSliderZgomot, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButtonNegativare)
                            .addComponent(jButtonDilatare)
                            .addComponent(jButtonSalvarePNG))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jComboBoxZonaDilatare, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanelRGB, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelLumi, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jSliderLumi, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel9)
                        .addComponent(jCheckBoxZgomot))
                    .addComponent(jSliderZgomot, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabelZgomot, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabbedPane3.addTab("Main", jPanel1);

        jButtonEfectLuminozitate.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        jButtonEfectLuminozitate.setText("Efect Luminozitate");
        jButtonEfectLuminozitate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonEfectLuminozitateActionPerformed(evt);
            }
        });

        jButtonAfisareRGB.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        jButtonAfisareRGB.setText("Afișare RGB");
        jButtonAfisareRGB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAfisareRGBActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(115, 115, 115)
                .addComponent(jButtonEfectLuminozitate)
                .addGap(44, 44, 44)
                .addComponent(jButtonAfisareRGB, javax.swing.GroupLayout.PREFERRED_SIZE, 176, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(125, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(61, 61, 61)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonEfectLuminozitate)
                    .addComponent(jButtonAfisareRGB))
                .addContainerGap(81, Short.MAX_VALUE))
        );

        jTabbedPane3.addTab("Efecte", jPanel5);

        jButtonColorare.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        jButtonColorare.setText("Colorare");
        jButtonColorare.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonColorareActionPerformed(evt);
            }
        });

        jToggleButtonSelectareCuloare.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        jToggleButtonSelectareCuloare.setText("Selectare Culoare");

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                .addContainerGap(114, Short.MAX_VALUE)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabelAfisareCuloareSelectata, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(jToggleButtonSelectareCuloare)
                        .addGap(69, 69, 69)
                        .addComponent(jButtonColorare, javax.swing.GroupLayout.PREFERRED_SIZE, 174, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(109, 109, 109))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                .addContainerGap(48, Short.MAX_VALUE)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButtonColorare)
                    .addComponent(jToggleButtonSelectareCuloare, javax.swing.GroupLayout.Alignment.TRAILING))
                .addGap(18, 18, 18)
                .addComponent(jLabelAfisareCuloareSelectata, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(45, 45, 45))
        );

        jTabbedPane3.addTab("Colorare", jPanel6);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanelHistograme, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jTabbedPaneImagini))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jTabbedPane1, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTabbedPane3, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jTabbedPaneImagini, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanelHistograme, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTabbedPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 213, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    // 1. LUMINANTA - CROMINANTA ///////////////////////////////////////////////////////////////////////////////
    /**
     * Reafiseaza imaginea in functie de fisierul selectat si reinitializeaza
     * interfata.
     *
     * @param evt
     */
    private void jButtonOriginalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonOriginalActionPerformed
        // afisarea imaginii selectate in jLabelPoza
        try {
            id = new ImagineDigitala(fisierPoza, this); // parametri: imaginea, fereastra
            id.afiseaza(id.pixeliImagine, jLabelPoza); // afisare pixeli din tablou in jLabelPoza
        } catch (InterruptedException ex) {
            System.out.println(ex);
        }

        // afisarea imaginii de colorat in jLabelImagineColorare
        try {
            idImagineColorare = new ImagineDigitala("yoda.png", this); // parametri: imaginea, fereastra
            idImagineColorare.afiseaza(idImagineColorare.pixeliImagine, jLabelImagineColorare); // afisare pixeli din tablou in jLabelPoza
        } catch (InterruptedException ex) {
            System.out.println(ex);
        }

        initInterfata(); // reinitializare interfata
    }//GEN-LAST:event_jButtonOriginalActionPerformed

    /**
     * Face imaginea alb negru.
     *
     * @param evt
     */
    private void jCheckBoxAlbNegruActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxAlbNegruActionPerformed
        // jCheckBoxRActionPerformed(null);
        id.compuneCulori(jCheckBoxAlbNegru.isSelected()); // daca jCheckBoxAlbNegru este selectat afiseaza alb-negru
        id.afiseaza(id.pixeliImagineM, jLabelPoza);
    }//GEN-LAST:event_jCheckBoxAlbNegruActionPerformed

    /**
     * Afiseaza elementele rosii ale imaginii.
     *
     * @param evt
     */
    private void jCheckBoxRActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxRActionPerformed
        id.compuneCulori(jCheckBoxAlbNegru.isSelected()); // daca jCheckBoxAlbNegru este selectat afiseaza alb-negru
        id.afiseaza(id.pixeliImagineM, jLabelPoza);
    }//GEN-LAST:event_jCheckBoxRActionPerformed

    /**
     * Afiseaza elementele verzi ale imaginii.
     *
     * @param evt
     */
    private void jCheckBoxGActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxGActionPerformed
        jCheckBoxRActionPerformed(null);
    }//GEN-LAST:event_jCheckBoxGActionPerformed

    /**
     * Afiseaza elementele albastre ale imaginii.
     *
     * @param evt
     */
    private void jCheckBoxBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxBActionPerformed
        jCheckBoxRActionPerformed(null);
    }//GEN-LAST:event_jCheckBoxBActionPerformed

    /**
     * Ofera lui <code>fisierPoza</code> numele imaginii selectate.
     *
     * @param evt
     */
    private void jComboBoxSelectorItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBoxSelectorItemStateChanged
        fisierPoza = jComboBoxSelector.getSelectedItem().toString();
        jButtonOriginalActionPerformed(null);
    }//GEN-LAST:event_jComboBoxSelectorItemStateChanged

    // 2. HSV ///////////////////////////////////////////////////////////////////////////////
    /**
     * Copiaza componenta 'ym' in 'y', 'cm1' in 'c1', 'cm2' in 'c2'. Copiaza
     * componenta "pixeliImagineM" in "pixeliImagine". Reinitializeaza
     * interfata.
     */
    void salveaza() {
        id.salveazaMO();
        initInterfata();
    }

    /**
     * Salvare imagine modificata in tabloul care contine imaginea originala.
     *
     * @param evt
     */
    private void jButtonSalvareActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSalvareActionPerformed
        salveaza();
        id.compuneCulori(jCheckBoxAlbNegru.isSelected());
        id.afiseaza(id.pixeliImagineM, jLabelPoza);
    }//GEN-LAST:event_jButtonSalvareActionPerformed

    private void jButtonRefacereActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonRefacereActionPerformed
        id.salveazaOM();
        initInterfata();
        id.afiseaza(id.pixeliImagineM, jLabelPoza); // afisare imagine
    }//GEN-LAST:event_jButtonRefacereActionPerformed

    /**
     * Apeleaza metoda <code>jSliderSaturatieStateChanged()</code>.
     *
     * @param evt
     */
    private void jSliderLuminozitateHSVStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jSliderLuminozitateHSVStateChanged
        jSliderSaturatieStateChanged(evt);
    }//GEN-LAST:event_jSliderLuminozitateHSVStateChanged

    private void jSliderSaturatieStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jSliderSaturatieStateChanged
        // preluare valori slidere Saturatie si LuminozitateHSV
        int ks = jSliderSaturatie.getValue();
        int kv = jSliderLuminozitateHSV.getValue();

        jLabelSaturatie.setText(ks + "");
        jLabelLuminozitateHSV.setText(kv + "");

        id.satureaza(ks, kv); // actioneaza asupra saturatiei si a luminozitatii
        id.afiseaza(id.pixeliImagineM, jLabelPoza); // afisez imaginea modificata
    }//GEN-LAST:event_jSliderSaturatieStateChanged

    private void jSliderSaturatieRGBStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jSliderSaturatieRGBStateChanged
        // citire valoare selectata de pe slider (intre 0 si 100)
        // impartire la 50 (rezultat intre 0 si 2)
        // adunat cu -1 face ca "k" sa ia valori intre -1 si 1 (fara culoare si accentuare culoare)
        double k = -1 + jSliderSaturatieRGB.getValue() / 50.;
        id.desatureaza(k); // trimitem constanta "k" pentru desatutare

        // jLabelSaturatieRGB.setText(k + ""); 
        jLabelSaturatieRGB.setText(new DecimalFormat("##.##").format(k)); // afisare valoare k

        id.afiseaza(id.pixeliImagineM, jLabelPoza); // afisez imaginea modificata
    }//GEN-LAST:event_jSliderSaturatieRGBStateChanged

    // 3. CONTRAST - LUMINOZITATE ///////////////////////////////////////////////////////////////////////////////
    /**
     * Activeaza / Dezactiveaza <code>jSliderContrastSin</code> la selectare /
     * deselectare radio button.
     *
     * @param evt
     */
    private void jRadioButtonContrastSinusoidalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonContrastSinusoidalActionPerformed
        jSliderContrastSin.setEnabled(true);
    }//GEN-LAST:event_jRadioButtonContrastSinusoidalActionPerformed

    private void jSliderContrastSinStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jSliderContrastSinStateChanged
        // Ajustare contrast sinusoidé.
        if (!jRadioButtonContrastSinusoidal.isSelected()) {
            return;
        }

        int nContrast = jSliderContrastSin.getValue(); // preluare valoare slider

        jLabelContrastSin.setText(nContrast + ""); // afisare valoare slider in label
        id.ajustareContrast(nContrast); // ajustare contrast sinusoidal in functie de valoare slider
        id.afiseaza(id.pixeliImagineM, jLabelPoza); // afisare imagine modificata
    }//GEN-LAST:event_jSliderContrastSinStateChanged

    /**
     * Activeaza / Dezactiveaza panelul pentru contrastul liniar.
     *
     * @param evt
     */
    private void jRadioButtonContrastLiniarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonContrastLiniarActionPerformed

    }//GEN-LAST:event_jRadioButtonContrastLiniarActionPerformed

    /**
     * Apeleaza functia de selectie pentru afisarea cercului la mutarea
     * mouse-ului pe panel atunci cand <code>jRadioButtonContrastLiniar</code>
     * este selectat.
     *
     * @param evt
     */
    private void jPanelContrastMouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanelContrastMouseMoved
        if (jRadioButtonContrastLiniar.isSelected()) {
            int xm = evt.getX(); // coordonata x a mouse-ului
            int ym = 255 - evt.getY(); // coordonata y a mouse-ului (inversata)

            contrast.selecteaza(xm, ym);
        } else {
            return;
        }
    }//GEN-LAST:event_jPanelContrastMouseMoved

    /**
     * Aplica efectul de contrast liniar la tragerea mouse-ului pe panel atunci
     * cand <code>jRadioButtonContrastLiniar</code> este selectat.
     *
     * @param evt
     */
    private void jPanelContrastMouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanelContrastMouseDragged
        if (jRadioButtonContrastLiniar.isSelected()) {
            int xm = evt.getX(); // coordonata x a mouse-ului
            int ym = 255 - evt.getY(); // coordonata y a mouse-ului (inversata)

            // corectarea capetelor
            xm = id.corectCapete(xm);
            ym = id.corectCapete(ym);

            contrast.deplaseaza(xm, ym);
            id.contrastLin(contrast.a.x, contrast.a.y, contrast.b.x, contrast.b.y);
            id.afiseaza(id.pixeliImagineM, jLabelPoza);
        } else {
            return;
        }
    }//GEN-LAST:event_jPanelContrastMouseDragged

    private void jRadioButtonContrastAutomatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonContrastAutomatActionPerformed
        // Contrast automat.
        id.contrastAutomat();
        id.afiseaza(id.pixeliImagineM, jLabelPoza); // afisez imaginea modificata
    }//GEN-LAST:event_jRadioButtonContrastAutomatActionPerformed

    private void jSliderLumiStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jSliderLumiStateChanged
        // Ajustare luminozitate.
        int nLumi = jSliderLumi.getValue(); // citire valoare slider
        jLabelLumi.setText(nLumi + ""); // scriere valoare din slider in label
        id.ajustareLuminozitate(nLumi); // trimite nivelul de luminozitate in metoda
        id.afiseaza(id.pixeliImagineM, jLabelPoza);
    }//GEN-LAST:event_jSliderLumiStateChanged

    // 4. BINARIZARE - NEGATIVARE - DILATARE ///////////////////////////////////////////////////////////////////////////////
    private void jRadioButtonImagineBinaraActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonImagineBinaraActionPerformed
        jSliderPragBinarizareStateChanged(null);
    }//GEN-LAST:event_jRadioButtonImagineBinaraActionPerformed

    /**
     * Apeleaza metoda de binarizare a imaginii la modificarea sliderului de
     * prag binarizare.
     *
     * @param evt
     */
    private void jSliderPragBinarizareStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jSliderPragBinarizareStateChanged
        // Slider binarizare
        if (jRadioButtonImagineBinara.isSelected()) {
            int k = jSliderPragBinarizare.getValue(); // preluare valoare slider
            jLabelPragBinarizare.setText(k + ""); // afisare valoare slider in label
            id.binarizeaza(k); // apel functie binarizare
            id.afiseaza(id.pixeliImagineM, jLabelPoza); // afisare imagine modicata
        }
    }//GEN-LAST:event_jSliderPragBinarizareStateChanged

    /**
     * Apeleaza funtia de negativare la apasarea butonului.
     *
     * @param evt
     */
    private void jButtonNegativareActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonNegativareActionPerformed
        id.negativeaza();
        id.afiseaza(id.pixeliImagineM, jLabelPoza);
    }//GEN-LAST:event_jButtonNegativareActionPerformed

    private void jButtonDilatareActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDilatareActionPerformed
        // Dilatare
        id.dilataTot();
        id.afiseaza(id.pixeliImagineM, jLabelPoza);
    }//GEN-LAST:event_jButtonDilatareActionPerformed

    /**
     * Preia coordonatele mouse-ului la click pe label. Binarizare regiune poza
     * in functie de valoarea din slider si spinner la apasare mouse.
     *
     * @param evt
     */
    private void jLabelPozaMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabelPozaMouseClicked
        if (jCheckBoxBinarizareClick.isSelected()) {
            int k = jSliderPragBinarizare.getValue();

            id.binarizeazaClick(k, evt.getX(), evt.getY(), (int) jSpinnerGrosimeCreion.getValue());
            id.afiseaza(id.pixeliImagineM, jLabelPoza);
        }
    }//GEN-LAST:event_jLabelPozaMouseClicked

    // 5. HISTOGRAME ///////////////////////////////////////////////////////////////////////////////
    private void jButtonNormalizareHistograma1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonNormalizareHistograma1ActionPerformed
        // Normalizare histograma
        id.normalizareHistograma();
        id.afiseaza(id.pixeliImagineM, jLabelPoza);
    }//GEN-LAST:event_jButtonNormalizareHistograma1ActionPerformed

    private void jButtonNormalizareHistograma2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonNormalizareHistograma2ActionPerformed
        id.normalizareHistograma2();
        id.afiseaza(id.pixeliImagineM, jLabelPoza);
    }//GEN-LAST:event_jButtonNormalizareHistograma2ActionPerformed

    // 6. FILTRE DE NETEZIRE ///////////////////////////////////////////////////////////////////////////////
    /**
     * Verifica care radio button este selectat pentru dimensiunea de filtrare.
     *
     * @return dimensiunea de filtrare
     */
    int dimFiltre() {
        if (jRadioButtonDim3.isSelected()) {
            return 3;
        } else if (jRadioButtonDim5.isSelected()) {
            return 5;
        }
        return 7;
    }

    private void jButtonFiltruBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonFiltruBoxActionPerformed
        // Filtru box.
        id.box(dimFiltre()); // preluare dimensiune filtrare
        id.afiseaza(id.pixeliImagineM, jLabelPoza);
    }//GEN-LAST:event_jButtonFiltruBoxActionPerformed

    private void jButtonFiltruGaussianActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonFiltruGaussianActionPerformed
        //Aplica filtrul Gaussian.
        id.gaussian();
        id.afiseaza(id.pixeliImagineM, jLabelPoza);
    }//GEN-LAST:event_jButtonFiltruGaussianActionPerformed

    /**
     * Activeaza efectul de zgomot prin apelarea
     * <code>jSliderZgomotStateChanged()</code> atunci cand check box-ul este
     * selectat si reafiseaza imaginea initiala atunci cand check box-ul este
     * deselectat.
     *
     * @param evt
     */
    private void jCheckBoxZgomotActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxZgomotActionPerformed
        if (jCheckBoxZgomot.isSelected()) {
            jSliderZgomot.setEnabled(true);

            jSliderZgomotStateChanged(null);
        } else {
            jSliderZgomot.setEnabled(false);

            id.separaCulori(id.pixeliImagine);
            // Anuleaza zgomotul, reface tablourile y, cl, c2.
            id.afiseaza(id.pixeliImagine, jLabelPoza);
        }
    }//GEN-LAST:event_jCheckBoxZgomotActionPerformed

    private void jSliderZgomotStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jSliderZgomotStateChanged
        if (jCheckBoxZgomot.isSelected()) {
            int nZgomot = jSliderZgomot.getValue();
            jLabelZgomot.setText(nZgomot + "");
            id.adaugaZgomot(nZgomot);
            id.afiseaza(id.pixeliImagineM, jLabelPoza);
        }
    }//GEN-LAST:event_jSliderZgomotStateChanged

    /**
     * Obiectul care va detine imaginea binara folosita pentru estomparea
     * regiunilor.
     */
    static ImagineDigitala idImagineRegiuni;

    /**
     * Va prelua numele imaginii din <code>jComboBoxEstompareRegiuni</code> si
     * va apela metoda <code>estompareRegiuni(dimFiltre())</code> dupa care va
     * afisa noua imagine.
     *
     * @param evt
     */
    private void jButtonEstompareRegiuniActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonEstompareRegiuniActionPerformed
        try {
            idImagineRegiuni = new ImagineDigitala(jComboBoxEstompareRegiuni.getSelectedItem().toString(), this);
        } catch (InterruptedException ex) {
            System.out.println(ex);
        }

        id.estompareRegiuni(dimFiltre());
        id.afiseaza(id.pixeliImagineM, jLabelPoza);
    }//GEN-LAST:event_jButtonEstompareRegiuniActionPerformed

    // 7. FILTRE LAPACIAN & STATISTICE ///////////////////////////////////////////////////////////////////////////////
    /**
     * Aplica filtrul Lapacian H+V (Horizontal + Vertical).
     *
     * @param evt
     */
    private void jButtonLaplacianHVActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonLaplacianHVActionPerformed
        // Aplica filtrul Laplacian H+V.
        id.laplacian(false);
        jCheckBoxAlbNegru.setSelected(true);
        id.afiseaza(id.pixeliImagineM, jLabelPoza);
    }//GEN-LAST:event_jButtonLaplacianHVActionPerformed

    /**
     * Aplica filtrul Lapacian H+V+D (Horizontal + Vertical + Diagonal).
     *
     * @param evt
     */
    private void jButtonLaplacianHVDActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonLaplacianHVDActionPerformed
        id.laplacian(true);
        jCheckBoxAlbNegru.setSelected(true);
        id.afiseaza(id.pixeliImagineM, jLabelPoza);
    }//GEN-LAST:event_jButtonLaplacianHVDActionPerformed

    /**
     * Aplica filtrul statistic median.
     *
     * @param evt
     */
    private void jButtonMedianActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonMedianActionPerformed
        id.median(dimFiltre());
        id.afiseaza(id.pixeliImagineM, jLabelPoza);
    }//GEN-LAST:event_jButtonMedianActionPerformed

    /**
     * Aplica filtrul statistic maxim.
     *
     * @param evt
     */
    private void jButtonMaximActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonMaximActionPerformed
        id.max(dimFiltre());
        id.afiseaza(id.pixeliImagineM, jLabelPoza);
    }//GEN-LAST:event_jButtonMaximActionPerformed

    /**
     * Aplica filtrul statistic minim.
     *
     * @param evt
     */
    private void jButtonMinimActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonMinimActionPerformed
        id.min(dimFiltre());
        id.afiseaza(id.pixeliImagineM, jLabelPoza);
    }//GEN-LAST:event_jButtonMinimActionPerformed

    /**
     * Aplica filtrul statistic interval prin apelarea metodei
     * <code>interval()</code>.
     *
     * @param evt
     */
    private void jButtonIntervalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonIntervalActionPerformed
        jCheckBoxAlbNegru.setSelected(true);
        id.interval(dimFiltre());
        id.afiseaza(id.pixeliImagineM, jLabelPoza);
    }//GEN-LAST:event_jButtonIntervalActionPerformed

    // 8. ACCENTUARE DETALII & TRASARE CONTURURI ///////////////////////////////////////////////////////////////////////////////
    private void jCheckBoxFiltruGaussianActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxFiltruGaussianActionPerformed
        jSliderPragConturStateChanged(null);
    }//GEN-LAST:event_jCheckBoxFiltruGaussianActionPerformed

    private void jRadioButtonVecinatatiInteriorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonVecinatatiInteriorActionPerformed
        jSliderPragConturStateChanged(null);
    }//GEN-LAST:event_jRadioButtonVecinatatiInteriorActionPerformed

    private void jRadioButtonVecinatatiExteriorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonVecinatatiExteriorActionPerformed
        jSliderPragConturStateChanged(null);
    }//GEN-LAST:event_jRadioButtonVecinatatiExteriorActionPerformed

    private void jRadioButtonConturLaplacianActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonConturLaplacianActionPerformed
        jSliderPragConturStateChanged(null);
    }//GEN-LAST:event_jRadioButtonConturLaplacianActionPerformed

    private void jRadioButtonConturPrewittActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonConturPrewittActionPerformed
        jSliderPragConturStateChanged(null);
    }//GEN-LAST:event_jRadioButtonConturPrewittActionPerformed

    private void jRadioButtonConturSobelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonConturSobelActionPerformed
        jSliderPragConturStateChanged(null);
    }//GEN-LAST:event_jRadioButtonConturSobelActionPerformed

    private void jRadioButtonConturIntervalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonConturIntervalActionPerformed
        jSliderPragConturStateChanged(null);
    }//GEN-LAST:event_jRadioButtonConturIntervalActionPerformed

    private void jRadioButtonConturCannyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonConturCannyActionPerformed
        jSliderPragConturStateChanged(null);
    }//GEN-LAST:event_jRadioButtonConturCannyActionPerformed

    /**
     * Slider pentru ajustarea aspectului contururilor pentru fiecare dintre
     * variante.
     *
     * @param evt
     */
    private void jSliderPragConturStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jSliderPragConturStateChanged
        // daca jCheckBoxTrasareContur nu este selectat, nu se mai face nimic
        if (!jCheckBoxTrasareContur.isSelected()) {
            return;
        }

        id.gaussianY(jCheckBoxFiltruGaussian.isSelected()); // aplicare filtru Gaussian pe componenta y daca este selectat

        int pragContur = jSliderPragContur.getValue(); // preluare valoare slider prag sus
        jLabelPragContur.setText(pragContur + "");

        int pragConturJ = jSliderPragConturJ.getValue(); // preluare valoare slider prag jos (pentru Canny)
        jLabelPragConturJ.setText(pragConturJ + "");

        if (jRadioButtonVecinatatiInterior.isSelected()) {
            id.trasareConturVec(pragContur, true); // vecinatati interior

        } else if (jRadioButtonVecinatatiExterior.isSelected()) {
            id.trasareConturVec(pragContur, false); // vecinatati exterior

        } else if (jRadioButtonConturLaplacian.isSelected()) {
            id.conturLaplacian(pragContur); // trasare contur pe baza filtrului Laplacian

        } else if (jRadioButtonConturPrewitt.isSelected()) {
            id.trasareConturSobelPrewitt(pragContur, false); // trasare contur pe baza filtrului Prewitt

        } else if (jRadioButtonConturSobel.isSelected()) {
            id.trasareConturSobelPrewitt(pragContur, true); // trasare contur pe baza filtrului Sobel

        } else if (jRadioButtonConturInterval.isSelected()) {
            id.trasareConturInterval(pragContur); // trasare contur pe baza filtrului Interval

        } else if (jRadioButtonConturCanny.isSelected()) {
            id.canny(pragContur, pragConturJ); // trasare contur pe baza filtrului Canny
        }

        id.afiseaza(id.pixeliImagineM, jLabelPoza);
    }//GEN-LAST:event_jSliderPragConturStateChanged

    /**
     * Slider pentru ajustarea aspectului contururilor de jos Canny pentru
     * conectarea contururilor intrerupte.
     *
     * @param evt
     */
    private void jSliderPragConturJStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jSliderPragConturJStateChanged
        jSliderPragConturStateChanged(null);
    }//GEN-LAST:event_jSliderPragConturJStateChanged

    /**
     * Activeaza functionalitatile de trasare a contururilor folosind
     * <code>jSliderPragConturStateChanged()</code> atunci cand check box-ul
     * este selectat si dezactiveaza contururile refacand imaginea atunci cand
     * check box-ul este deselectat.
     *
     * @param evt
     */
    private void jCheckBoxTrasareConturActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxTrasareConturActionPerformed
        if (jCheckBoxTrasareContur.isSelected()) {
            comutareComponente(jPanelTrasareContur, true);
            jSliderPragConturStateChanged(null);
        } else {
            comutareComponente(jPanelTrasareContur, false);
            id.separaCulori(id.pixeliImagine);
            id.afiseaza(id.pixeliImagine, jLabelPoza);
        }
    }//GEN-LAST:event_jCheckBoxTrasareConturActionPerformed

    /**
     * Activeaza functionalitatea de accentuare a detaliilor prin apelarea
     * metodei <code>jSliderCStateChanged()</code> atunci cand check box-ul este
     * selectat si reafiseaza imaginea initiala atunci cand check box-ul este
     * deselectat.
     *
     * @param evt
     */
    private void jCheckBoxAccentuareDetaliiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxAccentuareDetaliiActionPerformed
        if (jCheckBoxAccentuareDetalii.isSelected()) {
            jSliderC.setEnabled(true);
            jSliderCStateChanged(null);
        } else {
            jSliderC.setEnabled(false);
            id.afiseaza(id.pixeliImagine, jLabelPoza);
        }
    }//GEN-LAST:event_jCheckBoxAccentuareDetaliiActionPerformed

    /**
     * Slider accentuare detalii. Apeleaza metoda
     * <code>accentuareDetalii(c)</code> cu parametrul <code>c</code> ajustat.
     *
     * @param evt
     */
    private void jSliderCStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jSliderCStateChanged
        if (jCheckBoxAccentuareDetalii.isSelected()) {
            float c = jSliderC.getValue() - 50; // obtinere valori intre [-50, +50]
            c = 8 + c / 5; // ajustare pentru preluare valori intre limitele [-2c, +18]
            jLabelC.setText(new DecimalFormat("##.##").format(c));
            id.accentuareDetalii(c);
            id.afiseaza(id.pixeliImagineM, jLabelPoza);
        }
    }//GEN-LAST:event_jSliderCStateChanged

    /**
     * Va prelua numele imaginii din <code>jComboBoxConturRegiuni</code> si va
     * apela metoda <code>conturBin()</code> pentru conturarea zonelor estompate
     * dupa care va afisa noua imagine.
     *
     * @param evt
     */
    private void jButtonConturRegiuniActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonConturRegiuniActionPerformed
        try {
            idImagineRegiuni = new ImagineDigitala(jComboBoxConturRegiuni.getSelectedItem().toString(), this);
        } catch (InterruptedException ex) {
            System.out.println(ex);
        }

        idImagineRegiuni.conturBinRegiuni();

        id.afiseaza(id.pixeliImagineM, jLabelPoza);
    }//GEN-LAST:event_jButtonConturRegiuniActionPerformed

    // 9. TRANSFORMAREA IMAGINILOR BINARE ///////////////////////////////////////////////////////////////////////////////
    int mouseX, mouseY;

    // DESCHIDERE = EROZIUNE + DILATARE (elimina petele mici si negre (zgomot) din regiunile complexe)
    // INCHIDERE = DILATARE + EROZIUNE (umplerea de goluri si mici discontinuitati)
    // TRASARE CONTUR INTERIOR = Imagine Originala - Imagine erodata
    // TRASARE CONTUR EXTERIOR = Imagine Dilatata - Imagine Originala

    private void jButtonDilatareBinaraActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDilatareBinaraActionPerformed
        // Dilatare binara.
        id.dilatare();
        id.afiseaza(id.pixeliImagineM, jLabelPoza);
    }//GEN-LAST:event_jButtonDilatareBinaraActionPerformed

    private void jButtonEroziuneActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonEroziuneActionPerformed
        id.eroziune();
        id.afiseaza(id.pixeliImagineM, jLabelPoza);
    }//GEN-LAST:event_jButtonEroziuneActionPerformed

    private void jButtonConturBinarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonConturBinarActionPerformed
        // Contur binar.
        id.conturBin();
        id.afiseaza(id.pixeliImagineM, jLabelPoza);
    }//GEN-LAST:event_jButtonConturBinarActionPerformed

    private void jButtonUmplereActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonUmplereActionPerformed
        // Umplere contur.
        id.umplere(mouseY, mouseX);
        id.afiseaza(id.pixeliImagineM, jLabelPoza);
    }//GEN-LAST:event_jButtonUmplereActionPerformed

    /**
     * Preluare coordonate pentru umplere si afisare valori intr-un label
     * impreuna cu desenarea unui plus pe label poza.
     *
     * @param evt
     */
    private void jLabelPozaMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabelPozaMousePressed
        mouseX = evt.getX();
        mouseY = evt.getY();
        jLabelPozXY.setText("   X = " + mouseX + "       Y = " + mouseY);

        Graphics g = jLabelPoza.getGraphics(); // cu g se poate desena pe eticheta
        g.setColor(Color.red);
        g.drawLine(mouseX, mouseY - 6, mouseX, mouseY + 6); // marcare pozitie mouse
        g.drawLine(mouseX - 6, mouseY, mouseX + 6, mouseY);
    }//GEN-LAST:event_jLabelPozaMousePressed

    // SALVARE IMAGINE PNG ///////////////////////////////////////////////////////////////////////////////
    /**
     * Salvare imagine modificata intr-un fisier PNG.
     *
     * @param evt
     */
    private void jButtonSalvarePNGActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSalvarePNGActionPerformed
        Image imagine = createImage(new MemoryImageSource(DIM, DIM, id.pixeliImagineM, 0, DIM));
        ImageIcon ii = new ImageIcon(imagine);
        BufferedImage bi = new BufferedImage(ii.getIconWidth(), ii.getIconHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics g = bi.createGraphics();

        ii.paintIcon(null, g, 0, 0); // scrie ii in Bufferedlmage

        try { // scrie bi in fieier
            if (ImageIO.write(bi, "png", new File("imagine.png"))) {
                System.out.println("imagine salvata");
            }
        } catch (IOException ex) {
            System.out.println(ex);
        }
    }//GEN-LAST:event_jButtonSalvarePNGActionPerformed

    // MONTARE IMAGINI REGIUNI ///////////////////////////////////////////////////////////////////////////////
    static ImagineDigitala idImagineSecundaraMontaj, idMontaj;

    private void jButtonMontareImaginiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonMontareImaginiActionPerformed
        try {
            id = new ImagineDigitala(jComboBoxImaginePrimaraMontaj.getSelectedItem().toString(), this);
            idImagineSecundaraMontaj = new ImagineDigitala(jComboBoxImagineSecundaraMontaj.getSelectedItem().toString(), this);
            idImagineRegiuni = new ImagineDigitala(jComboBoxImagineRegiuniMontaj.getSelectedItem().toString(), this);

            idImagineRegiuni.umplere(10, 10); // exteriorul contururilor va fi umplut cu negru
            //  idImagineRegiuni.umplere(10, 10); // daca imaginea are doar contur

            idMontaj = new ImagineDigitala(id, idImagineSecundaraMontaj, idImagineRegiuni, this);

            id.afiseaza(idMontaj.pixeliImagine, jLabelPoza);
        } catch (Exception e) {
            System.out.println(e);
        }
    }//GEN-LAST:event_jButtonMontareImaginiActionPerformed

    /**
     * Contureaza imaginea din montaj la apasarea butonului si o afiseaza.
     *
     * @param evt
     */
    private void jButtonConturMontajActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonConturMontajActionPerformed
        idMontaj.contureaza();
        id.afiseaza(idMontaj.pixeliImagine, jLabelPoza);
    }//GEN-LAST:event_jButtonConturMontajActionPerformed

    // MONTARE IMAGINI CAREU ///////////////////////////////////////////////////////////////////////////////
    /**
     * Verificator de stadiu inversare montaj.
     */
    boolean primul = true;

    /**
     * Monteaza cele doua imagini selectate in combo box la apasarea butonului
     * si afiseaza imaginea rezultata.
     *
     * @param evt
     */
    private void jButtonMontareImaginiCareuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonMontareImaginiCareuActionPerformed
        try {
            id = new ImagineDigitala(jComboBoxImaginePrimaraMontajCareu.getSelectedItem().toString(), this);
            idImagineSecundaraMontaj = new ImagineDigitala(jComboBoxImagineSecundaraMontajCareu.getSelectedItem().toString(), this);

            idMontaj = new ImagineDigitala(id, idImagineSecundaraMontaj, this, Integer.parseInt(jTextFieldInaltime.getText()), Integer.parseInt(jTextFieldLatime.getText()));

            id.afiseaza(idMontaj.pixeliImagine, jLabelPoza);
            primul = false;
        } catch (Exception e) {
            System.out.println(e);
        }
    }//GEN-LAST:event_jButtonMontareImaginiCareuActionPerformed

    /**
     * Monteaza invers cele doua imagini selectate in combo box la apasarea
     * butonului si afiseaza imaginea rezultata.
     *
     * @param evt
     */
    private void jButtonInversareMontajCareuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonInversareMontajCareuActionPerformed
        if (!primul) {
            idMontaj = new ImagineDigitala(idImagineSecundaraMontaj, id, this, Integer.parseInt(jTextFieldInaltime.getText()), Integer.parseInt(jTextFieldLatime.getText()));
            id.afiseaza(idMontaj.pixeliImagine, jLabelPoza);
            primul = true;
        } else {
            jButtonMontareImaginiCareuActionPerformed(null);
        }
    }//GEN-LAST:event_jButtonInversareMontajCareuActionPerformed

    // EFECT LUMINOZITATE SI AFISARE RGB ///////////////////////////////////////////////////////////////////////////////

    private void jButtonEfectLuminozitateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonEfectLuminozitateActionPerformed
        id.efectLuminozitate();
        id.afiseaza(id.pixeliImagineM, jLabelPoza);
    }//GEN-LAST:event_jButtonEfectLuminozitateActionPerformed

    private void jButtonAfisareRGBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAfisareRGBActionPerformed
        id.compuneRGB();
        id.afiseaza(id.pixeliImagineM, jLabelPoza);
    }//GEN-LAST:event_jButtonAfisareRGBActionPerformed

    // COLORARE IMAGINE ///////////////////////////////////////////////////////////////////////////////
    ImagineDigitala idImagineColorare;

    int colorMouseX, colorMouseY;
    int culoare;

    private void jButtonColorareActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonColorareActionPerformed
        idImagineColorare.umplereCuloare(colorMouseY, colorMouseX);
        idImagineColorare.afiseaza(idImagineColorare.pixeliImagineM, jLabelImagineColorare);
    }//GEN-LAST:event_jButtonColorareActionPerformed

    private void jLabelImagineColorareMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabelImagineColorareMousePressed
        colorMouseX = evt.getX();
        colorMouseY = evt.getY();

        if (jToggleButtonSelectareCuloare.isSelected()) {
            culoare = idImagineColorare.culoarePixel(colorMouseY, colorMouseX);

            jLabelAfisareCuloareSelectata.setBackground(new Color(culoare));
            jLabelAfisareCuloareSelectata.setOpaque(true);

            jToggleButtonSelectareCuloare.setSelected(false);
        } else {
            Graphics g = jLabelImagineColorare.getGraphics();

            // marcheaza pozitia mouse-ului
            g.setColor(Color.red);
            g.drawLine(colorMouseX, colorMouseY - 6, colorMouseX, colorMouseY + 6);
            g.drawLine(colorMouseX - 6, colorMouseY, colorMouseX + 6, colorMouseY);
        }
    }//GEN-LAST:event_jLabelImagineColorareMousePressed

    public static void main(String args[]) {
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Windows".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Interfata.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Interfata.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Interfata.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Interfata.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        // Create and display the form
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Interfata().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroupContrast;
    private javax.swing.ButtonGroup buttonGroupContur;
    private javax.swing.ButtonGroup buttonGroupDimFiltre;
    private javax.swing.JButton jButtonAfisareRGB;
    private javax.swing.JButton jButtonColorare;
    private javax.swing.JButton jButtonConturBinar;
    private javax.swing.JButton jButtonConturMontaj;
    private javax.swing.JButton jButtonConturRegiuni;
    private javax.swing.JButton jButtonDilatare;
    private javax.swing.JButton jButtonDilatareBinara;
    private javax.swing.JButton jButtonEfectLuminozitate;
    private javax.swing.JButton jButtonEroziune;
    private javax.swing.JButton jButtonEstompareRegiuni;
    private javax.swing.JButton jButtonFiltruBox;
    private javax.swing.JButton jButtonFiltruGaussian;
    private javax.swing.JButton jButtonInterval;
    private javax.swing.JButton jButtonInversareMontajCareu;
    private javax.swing.JButton jButtonLaplacianHV;
    private javax.swing.JButton jButtonLaplacianHVD;
    private javax.swing.JButton jButtonMaxim;
    private javax.swing.JButton jButtonMedian;
    private javax.swing.JButton jButtonMinim;
    private javax.swing.JButton jButtonMontareImagini;
    private javax.swing.JButton jButtonMontareImaginiCareu;
    private javax.swing.JButton jButtonNegativare;
    private javax.swing.JButton jButtonNormalizareHistograma1;
    private javax.swing.JButton jButtonNormalizareHistograma2;
    private javax.swing.JButton jButtonOriginal;
    private javax.swing.JButton jButtonRefacere;
    private javax.swing.JButton jButtonSalvare;
    private javax.swing.JButton jButtonSalvarePNG;
    private javax.swing.JButton jButtonUmplere;
    private javax.swing.JCheckBox jCheckBoxAccentuareDetalii;
    static javax.swing.JCheckBox jCheckBoxAlbNegru;
    static javax.swing.JCheckBox jCheckBoxB;
    private javax.swing.JCheckBox jCheckBoxBinarizareClick;
    private javax.swing.JCheckBox jCheckBoxFiltruGaussian;
    static javax.swing.JCheckBox jCheckBoxG;
    static javax.swing.JCheckBox jCheckBoxR;
    private javax.swing.JCheckBox jCheckBoxTrasareContur;
    private javax.swing.JCheckBox jCheckBoxZgomot;
    static javax.swing.JComboBox<String> jComboBoxConturRegiuni;
    static javax.swing.JComboBox<String> jComboBoxEstompareRegiuni;
    static javax.swing.JComboBox<String> jComboBoxImaginePrimaraMontaj;
    static javax.swing.JComboBox<String> jComboBoxImaginePrimaraMontajCareu;
    static javax.swing.JComboBox<String> jComboBoxImagineRegiuniMontaj;
    static javax.swing.JComboBox<String> jComboBoxImagineSecundaraMontaj;
    static javax.swing.JComboBox<String> jComboBoxImagineSecundaraMontajCareu;
    private javax.swing.JComboBox<String> jComboBoxSelector;
    static javax.swing.JComboBox<String> jComboBoxZonaDilatare;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel jLabelAfisareCuloareSelectata;
    private javax.swing.JLabel jLabelC;
    private javax.swing.JLabel jLabelContrastSin;
    private javax.swing.JLabel jLabelImagineColorare;
    private javax.swing.JLabel jLabelLumi;
    private javax.swing.JLabel jLabelLuminozitateHSV;
    private javax.swing.JLabel jLabelPozXY;
    private javax.swing.JLabel jLabelPoza;
    private javax.swing.JLabel jLabelPragBinarizare;
    private javax.swing.JLabel jLabelPragContur;
    private javax.swing.JLabel jLabelPragConturJ;
    private javax.swing.JLabel jLabelSaturatie;
    private javax.swing.JLabel jLabelSaturatieRGB;
    private javax.swing.JLabel jLabelZgomot;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanelContrast;
    private javax.swing.JPanel jPanelConturRegiuni;
    private javax.swing.JPanel jPanelEstompareRegiuni;
    private javax.swing.JPanel jPanelFiltreLaplacian;
    private javax.swing.JPanel jPanelFiltreNetezire;
    private javax.swing.JPanel jPanelFiltreStatistice;
    static javax.swing.JPanel jPanelHistogramaB;
    static javax.swing.JPanel jPanelHistogramaG;
    static javax.swing.JPanel jPanelHistogramaR;
    static javax.swing.JPanel jPanelHistogramaY;
    private javax.swing.JPanel jPanelHistograme;
    private javax.swing.JPanel jPanelMontajImaginiSigla;
    private javax.swing.JPanel jPanelPoza;
    private javax.swing.JPanel jPanelRGB;
    private javax.swing.JPanel jPanelSaturatieHSV;
    private javax.swing.JPanel jPanelSaturatieRGB;
    private javax.swing.JPanel jPanelTabContrast;
    private javax.swing.JPanel jPanelTabConturDetalii;
    private javax.swing.JPanel jPanelTabFiltre;
    private javax.swing.JPanel jPanelTabHistograma;
    private javax.swing.JPanel jPanelTabSaturatie;
    private javax.swing.JPanel jPanelTransfBinare;
    private javax.swing.JPanel jPanelTrasareContur;
    private javax.swing.JRadioButton jRadioButtonContrastAutomat;
    private javax.swing.JRadioButton jRadioButtonContrastLiniar;
    private javax.swing.JRadioButton jRadioButtonContrastSinusoidal;
    private javax.swing.JRadioButton jRadioButtonConturCanny;
    private javax.swing.JRadioButton jRadioButtonConturInterval;
    private javax.swing.JRadioButton jRadioButtonConturLaplacian;
    private javax.swing.JRadioButton jRadioButtonConturPrewitt;
    private javax.swing.JRadioButton jRadioButtonConturSobel;
    private javax.swing.JRadioButton jRadioButtonDim3;
    private javax.swing.JRadioButton jRadioButtonDim5;
    private javax.swing.JRadioButton jRadioButtonDim7;
    private javax.swing.JRadioButton jRadioButtonImagineBinara;
    private javax.swing.JRadioButton jRadioButtonVecinatatiExterior;
    private javax.swing.JRadioButton jRadioButtonVecinatatiInterior;
    private javax.swing.JSlider jSliderC;
    private javax.swing.JSlider jSliderContrastSin;
    private javax.swing.JSlider jSliderLumi;
    private javax.swing.JSlider jSliderLuminozitateHSV;
    private javax.swing.JSlider jSliderPragBinarizare;
    private javax.swing.JSlider jSliderPragContur;
    private javax.swing.JSlider jSliderPragConturJ;
    private javax.swing.JSlider jSliderSaturatie;
    private javax.swing.JSlider jSliderSaturatieRGB;
    private javax.swing.JSlider jSliderZgomot;
    private javax.swing.JSpinner jSpinnerGrosimeCreion;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTabbedPane jTabbedPane2;
    private javax.swing.JTabbedPane jTabbedPane3;
    private javax.swing.JTabbedPane jTabbedPaneImagini;
    private javax.swing.JTextField jTextFieldInaltime;
    private javax.swing.JTextField jTextFieldLatime;
    private javax.swing.JToggleButton jToggleButtonSelectareCuloare;
    // End of variables declaration//GEN-END:variables
}
