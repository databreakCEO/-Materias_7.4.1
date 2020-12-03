/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package materias;

import Utilerias.JTable.ColorCeldas;
import Utilerias.JTable.JTabla;
import java.awt.Color;
import java.awt.HeadlessException;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Daniel Gonzalez Cabrera
 */
public class MateriasFrame extends javax.swing.JFrame implements Runnable {

    /**
     * Creates new form MateriasFrame2
     */
    Thread t;
    JTabla tablaHorariosColoreados;
    JTabla tablaMateriasColoreadas;
    DefaultTableModel modelo;
    DefaultTableModel modeloProfesores;
    DefaultListModel materiasPendientes;
    ArrayList<String> listaElegidos;
    JList jlist1;
    DefaultListModel listaMateriasPosibles;
    ArrayList<Materia> listaMaestros;
    ArrayList<Materia> listaDesAprov;

    DefaultTableModel modeloTabla;
    ArrayList<Horario> sc;
    ArrayList<Horario> horario;
//    ArrayList<Horario> sc;
    boolean mandar = false;
    boolean mandarIsSelected = false;
    Cuenta c;
    int horariosHabiles = 0;
    int conSpiner = 0;
    String nombre;
    // Color[] colores = {Color.GREEN, Color.RED, Color.YELLOW, Color.MAGENTA, Color.ORANGE, Color.CYAN, Color.pink};
    Color[] colores = new Color[10];
    int numHorario = 0;
    ColorCeldas horariosPintados;
    ColorCeldas colorCeldas;
    Cargando load;
    int[] creditosMat;

    public MateriasFrame(DefaultListModel materiasPendientes, String carrera, String nombre) {
        initComponents();
        try {
            this.setIconImage(new ImageIcon(getClass().getResource("/Imagenes/rayo.jpg")).getImage());
        } catch (Exception e) {

        }
        setTitle("Creador de horarios");
        load = new Cargando();
        t = new Thread(this);
        t.setPriority(Thread.MAX_PRIORITY);
        colores[0] = new Color(244, 124, 131);
        colores[1] = new Color(197, 124, 244);
        colores[2] = new Color(241, 244, 124);
        colores[3] = new Color(244, 174, 124);
        colores[4] = new Color(124, 244, 197);
        colores[5] = new Color(124, 206, 244);
        colores[6] = new Color(166, 244, 124);
        colores[7] = new Color(243, 124, 244);
        colores[8] = new Color(255, 248, 220);
        colores[9] = new Color(255, 255, 240);

        modelo = (DefaultTableModel) jTable1.getModel();
        modeloProfesores = (DefaultTableModel) jTable2.getModel();
        listaElegidos = new ArrayList<>();
        listaMaestros = new ArrayList<>();
        this.materiasPendientes = materiasPendientes;
        this.nombre = nombre;

        colorCeldas = new ColorCeldas(jTable1);
        horario = new ArrayList<>();

        modeloTabla = (DefaultTableModel) jTable3.getModel();

        jTable3.getColumnModel().getColumn(0).setPreferredWidth(10);
        jTable3.getColumnModel().getColumn(1).setPreferredWidth(150);
        jTable3.getColumnModel().getColumn(2).setPreferredWidth(150);
        jTable3.getColumnModel().getColumn(3).setPreferredWidth(150);
        jTable3.getColumnModel().getColumn(4).setPreferredWidth(150);
        jTable3.getColumnModel().getColumn(5).setPreferredWidth(150);
        jTable3.setRowHeight(75);
        jTable3.setGridColor(Color.black);
        jTable3.setShowGrid(true);
        jTable3.setEnabled(false);
        listaMateriasPosibles = new DefaultListModel();
        sc = new ArrayList<>();
//        jSpinner1.setValue(1);
        Materia.crearListaMaterias(carrera);
        ArrayList<String[]> matPen = new ArrayList<>();
        if (carrera.equals("Sistemas")) {
            matPen = secciones(materiasPendientes);
        } else {
            for (int i = 0; i < Materia.lista.size(); i++) {
                matPen.add(new String[]{"", Materia.lista.get(i).nombreMat});
            }
        }
        for (int i = 0; i < matPen.size(); i++) {
            for (int j = 0; j < matPen.size() - 1; j++) {
                if (matPen.get(j)[1].compareTo(matPen.get(j + 1)[1]) > 0) {
                    String aux[] = matPen.get(j);
                    matPen.set(j, matPen.get(j + 1));
                    matPen.set(j + 1, aux);
                }
            }
        }
        creditosMat = new int[matPen.size()];
        int index = 0;

        for (int i = 0; i < matPen.size(); i++) {
//            System.out.println("");
//            System.out.print(matPen.get(i)[1] + " ");
            for (int j = 0; j < Materia.lista.size(); j++) {
                if (Materia.lista.get(j).nombreMat.equals(matPen.get(i)[1])) {

                    System.out.print(Materia.lista.get(j).nombreMat + " ");
                    creditosMat[i] = Materia.lista.get(j).costo;
//                    index++;
                    System.out.println(Materia.lista.get(j).costo);
                    break;
                }
            }

        }
        for (int i = 0; i < matPen.size(); i++) {
            modelo.setRowCount(i + 1);
            jTable1.setValueAt(false, i, 0);
            jTable1.setValueAt(matPen.get(i)[1], i, 1);
            jTable1.setValueAt(creditosMat[i], i, 2);
        }

        for (int i = 0; i < Materia.lista.size() - 1; i++) {
            if (Materia.lista.get(i).codigoMat.equals(Materia.lista.get(i + 1).codigoMat)) {
                Materia.lista.get(i).unirMateria(Materia.lista.get(i + 1));
                Materia.lista.remove(i + 1);
                i--;
            }
        }
        this.jLabError.setVisible(false);
    }

    private ArrayList<String[]> rows = new ArrayList<>();
//    ArrayList<Materia> lista = new ArrayList<>();

    ArrayList<String> datos = new ArrayList<>();
    ArrayList<String> materiasPasadas = new ArrayList<>();

    public ArrayList<String[]> secciones(DefaultListModel materias) {
        // se definen las restricciones por ligadura directa e indirecta
        rows = new ArrayList<>();
        String materia;
        ArrayList<String> calculo = new ArrayList<>();
        calculo.add("CALCULO DIFERENCIAL");
        calculo.add("CALCULO INTEGRAL");
        calculo.add("CALCULO VECTORIAL");
        calculo.add("ECUACIONES DIFERENCIALES");

        ArrayList<String> programacion = new ArrayList<>();
        programacion.add("FUNDAMENTOS DE PROGRAMACION");
        programacion.add("PROGRAMACION ORIENTADA A OBJETOS");
        programacion.add("ESTRUCTURA DE DATOS");

        ArrayList<String> baseDeDatos = new ArrayList<>();
        baseDeDatos.add("FUNDAMENTOS DE BASES DE DATOS");
        baseDeDatos.add("TALLER DE BASE DE DATOS");
        baseDeDatos.add("ADMINISTRACION DE BASE DE DATOS");

        ArrayList<String> ingenieria = new ArrayList<>();
        ingenieria.add("FUNDAMENTOS DE INGENIERIA DE SOFTWARE");
        ingenieria.add("INGENIERIA DE SOFTWARE");
        ingenieria.add("GESTION DE PROYECTOS DE SOFTWARE");

        ArrayList<String> SO = new ArrayList<>();
        SO.add("SISTEMAS OPERATIVOS");
        SO.add("TALLER DE SISTEMAS OPERATIVOS");

        ArrayList<String> lenguajes = new ArrayList<>();
        lenguajes.add("LENGUAJES Y AUTOMATAS I");
        lenguajes.add("LENGUAJES Y AUTOMATAS II");

        ArrayList<String> algebra = new ArrayList<>();
        algebra.add("ALGEBRA LINEAL");
        algebra.add("INVESTIGACION DE OPERACIONES");
        algebra.add("SIMULACION");

        ArrayList<String> arquitectura = new ArrayList<>();
        arquitectura.add("ARQUITECTURA DE COMPUTADORAS");
        arquitectura.add("LENGUAJES DE INTERFAZ");

        ArrayList<String> tele = new ArrayList<>();
        tele.add("FUNDAMENTOS DE TELECOMUNICACIONES");
        tele.add("REDES DE COMPUTADORAS");
        tele.add("CONMUTACION Y ENRUTAMIENTO EN REDES DE DATOS");
//        tele.add("ADMINISTRACION DE REDES");

        ArrayList<String> invest = new ArrayList<>();
        invest.add("TALLER DE INVESTIGACION I");
        invest.add("TALLER DE INVESTIGACION II");

        ArrayList<String> desarrollo = new ArrayList<>();
        desarrollo.add("DESARROLLO WEB PILA COMPLETA I");
        desarrollo.add("DESARROLLO WEB PILA COMPLETA II");

        ArrayList<String> movil = new ArrayList<>();
        movil.add("DESARROLLO EN ANDROID");
        movil.add("DESARROLLO EN IOS");

        ArrayList<String> single55percent = new ArrayList<>();
        single55percent.add("SEGURIDAD");
        single55percent.add("INTELIGENCIA ARTIFICIAL");
        single55percent.add("PROGRAMACION LOGICA Y FUNCIONAL");
        single55percent.add("SISTEMAS PROGRAMABLES");
        single55percent.add("DESARROLLO DE HABILIDADES PROFESIONALES EN INFORMATICA");
        single55percent.add("CULTURA EMPRESARIAL");
        single55percent.add("BIG DATA");
        single55percent.add("COMPUTO EN LA NUBE");
        single55percent.add("COMPUTACION INTELIGENTE");

        ArrayList<String> singleWOPercent = new ArrayList<>();
        singleWOPercent.add("CONTABILIDAD FINANCIERA");
        singleWOPercent.add("DESARROLLO SUSTENTABLE");
        singleWOPercent.add("FISICA GENERAL");
        singleWOPercent.add("FUNDAMENTOS DE INVESTIGACION");
        singleWOPercent.add("MATEMATICAS DISCRETAS");
        singleWOPercent.add("METODOS NUMERICOS");
        singleWOPercent.add("PROBABILIDAD Y ESTADISTICA");
        singleWOPercent.add("QUIMICA");
        singleWOPercent.add("TALLER DE ADMINISTRACION");
        singleWOPercent.add("TALLER DE ETICA");

        ArrayList<ArrayList<String>> secciones = new ArrayList<>();
        boolean poo = true, estructura = true, principios = false;
        //secciones libres solo la primera
//        System.out.println(Alumno.creditosAcumulados);
        for (int i = 0; i < materias.size(); i++) {
            materia = materias.get(i).toString();
            if (materia.equals("PRINCIPIOS ELECTRICOS Y APLICACIONES DIGITALES")) {
                principios = true;
            }
            for (int j = 0; j < calculo.size(); j++) {
                if (materia.equals(calculo.get(j))) {
                    calculo.remove(j);
                    break;
                }
            }
            for (int j = 0; j < programacion.size(); j++) {
                if (materia.equals(programacion.get(j))) {
                    programacion.remove(j);
                    break;
                }
            }
            for (int j = 0; j < algebra.size(); j++) {
                if (materia.equals(algebra.get(j))) {
                    algebra.remove(j);
                    break;
                }
            }
            for (int j = 0; j < invest.size(); j++) {
                if (materia.equals(invest.get(j))) {
                    invest.remove(j);
                    break;
                }
            }
        }

//        System.out.println("principios = "+principios);
        for (int i = 0; i < programacion.size(); i++) {
            if (programacion.get(i).equals("ESTRUCTURA DE DATOS")) {
                estructura = false;
            }

            if (programacion.get(i).equals("PROGRAMACION ORIENTADA A OBJETOS")) {
                poo = false;
            }

        }
//        System.out.println("estructura = "+estructura+" poo = "+poo);

        boolean progaWeb = true;

        if (poo) {
            for (int i = 0; i < materias.size(); i++) {
                materia = materias.get(i).toString();
                for (int j = baseDeDatos.size() - 1; j >= 0; j--) {
                    if (materia.equals(baseDeDatos.get(j))) {
                        baseDeDatos.remove(j);
                    }
                }
                if (materia.equals("PROGRAMACION WEB")) {
                    progaWeb = false;
                }
            }
            if (progaWeb) {
                rows.add(new String[]{"", "PROGRAMACION WEB"});
            }

            secciones.add(baseDeDatos);
        }
        boolean graf = true, topicos = true;

        if (estructura) {
            for (int i = 0; i < materias.size(); i++) {
                materia = materias.get(i).toString();
                for (int j = ingenieria.size() - 1; j >= 0; j--) {
                    if (materia.equals(ingenieria.get(j))) {
                        ingenieria.remove(j);
                    }
                }
                for (int j = SO.size() - 1; j >= 0; j--) {
                    if (materia.equals(SO.get(j))) {
                        SO.remove(j);
                    }
                }
                for (int j = lenguajes.size() - 1; j >= 0; j--) {
                    if (materia.equals(lenguajes.get(j))) {
                        lenguajes.remove(j);
                    }
                }
                if (!materia.equals("GRAFICACION")) {
                    graf = false;
                }
                if (!materia.equals("TOPICOS AVANZADOS DE PROGRAMACION")) {
                    topicos = false;
                }
            }
            if (graf) {
                rows.add(new String[]{"", "GRAFICACION"});
            }
            if (topicos) {
                rows.add(new String[]{"", "TOPICOS AVANZADOS DE PROGRAMACION"});
            }
            secciones.add(ingenieria);
            secciones.add(SO);
            secciones.add(lenguajes);
        }

        if (principios) {
            for (int i = 0; i < materias.size(); i++) {
                materia = materias.get(i).toString();
                for (int j = arquitectura.size() - 1; j >= 0; j--) {
                    if (materia.equals(arquitectura.get(j))) {
                        arquitectura.remove(j);
                    }
                }
                for (int j = tele.size() - 1; j >= 0; j--) {
                    if (materia.equals(tele.get(j))) {
                        tele.remove(j);

                    }
                }
            }
            secciones.add(arquitectura);
            secciones.add(tele);
        } else {
            rows.add(new String[]{"", "PRINCIPIOS ELECTRICOS Y APLICACIONES DIGITALES"});
        }

        boolean redes = true;
        for (int i = 0; i < tele.size(); i++) {
            materia = tele.get(i);
            if (materia.equals("REDES DE COMPUTADORAS")) {
                redes = false;
                break;
            }
        }
        boolean adminRedes = true;
        if (redes) {
            for (int i = 0; i < materias.size(); i++) {
                if (materias.get(i).toString().equals("ADMINISTRACION DE REDES")) {
                    adminRedes = false;
                    break;
                }
            }
        }
        if (adminRedes) {
            rows.add(new String[]{"", "ADMINISTRACION DE REDES"});
        }

//se crean restricciones por Alumno.creditosAcumulados de creditos (55% de creditos)
        if (Alumno.creditosAcumulados >= 55) {
            for (int i = 0; i < materias.size(); i++) {
                materia = materias.get(i).toString();
                for (int j = desarrollo.size() - 1; j >= 0; j--) {
                    if (materia.equals(desarrollo.get(j))) {
                        desarrollo.remove(j);
                        break;
                    }
                }
                for (int j = movil.size() - 1; j >= 0; j--) {
                    if (materia.equals(movil.get(j))) {
                        movil.remove(j);
                        break;
                    }
                }
                for (int j = single55percent.size() - 1; j >= 0; j--) {
                    if (materia.equals(single55percent.get(j))) {
                        single55percent.remove(j);

                    }
                }
            }

        }

// Materias sin restriccion directa ni de Alumno.creditosAcumulados de creditos
        for (int i = 0; i < materias.size(); i++) {
            materia = materias.get(i).toString();
            for (int j = singleWOPercent.size() - 1; j >= 0; j--) {
                if (singleWOPercent.get(j).equals(materia)) {
                    singleWOPercent.remove(j);
                }
            }

        }

        secciones.add(calculo);
        secciones.add(programacion);

        secciones.add(algebra);
        secciones.add(invest);

        //cualquiera de las siguientes
        //55&
        if (Alumno.creditosAcumulados >= 55) {
            secciones.add(desarrollo);
            secciones.add(movil);
            for (int i = 0; i < single55percent.size(); i++) {
                rows.add(new String[]{"", single55percent.get(i)});

            }
        }

        for (int i = 0; i < secciones.size(); i++) {
            if (secciones.get(i).size() > 0) {
                rows.add(new String[]{"", secciones.get(i).get(0)});
            }
        }
        /*
        secciones.add(single55percent);

        secciones.add(singleWOPercent);

         */

        for (int i = 0; i < singleWOPercent.size(); i++) {
            rows.add(new String[]{"", singleWOPercent.get(i)});
        }
        return rows;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTable3 = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();
        CrearHorarios = new javax.swing.JButton();
        SiguienteHorario = new javax.swing.JButton();
        AnnteriorHorario = new javax.swing.JButton();
        jNumHorario = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jRadioButPrimera = new javax.swing.JRadioButton();
        jRadioButHoras = new javax.swing.JRadioButton();
        jLabCreditos = new javax.swing.JLabel();
        jLabError = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel36 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        jLabel31 = new javax.swing.JLabel();
        jLabel32 = new javax.swing.JLabel();
        jLabel33 = new javax.swing.JLabel();
        jLabel34 = new javax.swing.JLabel();
        jLabel35 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent evt) {
                formWindowClosed(evt);
            }
        });
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "", "Materia", "Costo"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Boolean.class, java.lang.Object.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                true, true, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable1.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jTable1.getTableHeader().setReorderingAllowed(false);
        jTable1.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                jTable1MouseDragged(evt);
            }
        });
        jTable1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable1MouseClicked(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jTable1MousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jTable1MouseReleased(evt);
            }
        });
        jTable1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTable1KeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTable1KeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTable1KeyTyped(evt);
            }
        });
        jScrollPane1.setViewportView(jTable1);
        if (jTable1.getColumnModel().getColumnCount() > 0) {
            jTable1.getColumnModel().getColumn(0).setResizable(false);
            jTable1.getColumnModel().getColumn(1).setResizable(false);
            jTable1.getColumnModel().getColumn(1).setPreferredWidth(1000);
            jTable1.getColumnModel().getColumn(2).setResizable(false);
        }

        getContentPane().add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 40, 350, 280));

        jTable3.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {"7 AM", "", null, null, null, null},
                {"8 AM", null, null, null, null, null},
                {"9 AM", null, null, null, null, null},
                {"10 AM", null, null, null, null, null},
                {"11 AM", null, null, null, null, null},
                {"12 PM", null, null, null, null, null},
                {"1 PM", null, null, null, null, null},
                {"2 PM", null, null, null, null, null},
                {"3 PM", null, null, null, null, null},
                {"4 PM", null, null, null, null, null},
                {"5 PM", null, null, null, null, null},
                {"6 PM", null, null, null, null, null},
                {"7 PM", null, null, null, null, null},
                {"8 PM", null, null, null, null, null},
                {"9 PM", null, null, null, null, null}
            },
            new String [] {
                "Hora", "Lunes", "Martes", "Miercoles", "Jueves", "Viernes"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable3.getTableHeader().setReorderingAllowed(false);
        jTable3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jTable3MousePressed(evt);
            }
        });
        jScrollPane3.setViewportView(jTable3);

        getContentPane().add(jScrollPane3, new org.netbeans.lib.awtextra.AbsoluteConstraints(360, 60, 980, 640));

        jLabel1.setBackground(new java.awt.Color(0, 0, 0));
        jLabel1.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(0, 204, 0));
        jLabel1.setText("Posibles Horarios: ");
        getContentPane().add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(360, 40, -1, -1));

        jTable2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "", "Profesor", "Hora"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Boolean.class, java.lang.Object.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                true, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable2.getTableHeader().setReorderingAllowed(false);
        jTable2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable2MouseClicked(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jTable2MouseReleased(evt);
            }
        });
        jScrollPane2.setViewportView(jTable2);
        if (jTable2.getColumnModel().getColumnCount() > 0) {
            jTable2.getColumnModel().getColumn(0).setPreferredWidth(3);
            jTable2.getColumnModel().getColumn(1).setPreferredWidth(200);
        }

        getContentPane().add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 420, 350, 140));

        CrearHorarios.setBackground(new java.awt.Color(51, 255, 0));
        CrearHorarios.setText("Crear Horarios");
        CrearHorarios.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CrearHorariosActionPerformed(evt);
            }
        });
        getContentPane().add(CrearHorarios, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 320, 160, 30));

        SiguienteHorario.setText("Siguiente");
        SiguienteHorario.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SiguienteHorarioActionPerformed(evt);
            }
        });
        getContentPane().add(SiguienteHorario, new org.netbeans.lib.awtextra.AbsoluteConstraints(830, 30, -1, -1));

        AnnteriorHorario.setText("Anterior");
        AnnteriorHorario.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AnnteriorHorarioActionPerformed(evt);
            }
        });
        getContentPane().add(AnnteriorHorario, new org.netbeans.lib.awtextra.AbsoluteConstraints(580, 30, -1, -1));

        jNumHorario.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jNumHorario.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jNumHorario.setText("Horario #1");
        jNumHorario.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        getContentPane().add(jNumHorario, new org.netbeans.lib.awtextra.AbsoluteConstraints(660, 20, 160, 30));

        jButton1.setText("Guardar Horario");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(950, 30, 140, -1));

        buttonGroup1.add(jRadioButPrimera);
        jRadioButPrimera.setText("Ord. por primera hora");
        jRadioButPrimera.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButPrimeraActionPerformed(evt);
            }
        });
        getContentPane().add(jRadioButPrimera, new org.netbeans.lib.awtextra.AbsoluteConstraints(660, 0, -1, -1));

        buttonGroup1.add(jRadioButHoras);
        jRadioButHoras.setSelected(true);
        jRadioButHoras.setText("Ord. por horas escolares");
        jRadioButHoras.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButHorasActionPerformed(evt);
            }
        });
        getContentPane().add(jRadioButHoras, new org.netbeans.lib.awtextra.AbsoluteConstraints(460, 0, -1, -1));

        jLabCreditos.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabCreditos.setText("Creditos acumulados: ");
        getContentPane().add(jLabCreditos, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 320, 180, 30));

        jLabError.setBackground(new java.awt.Color(215, 9, 9));
        jLabError.setForeground(new java.awt.Color(255, 255, 255));
        jLabError.setText(" LIMITE DE CREDITOS EXCEDIDO ");
        jLabError.setOpaque(true);
        getContentPane().add(jLabError, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 20, -1, -1));

        jLabel2.setBackground(new java.awt.Color(255, 255, 255));
        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel2.setText("Marque las casillas de las materias de las que quiera hacer horarios");
        jLabel2.setOpaque(true);
        getContentPane().add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        jLabel20.setBackground(new java.awt.Color(255, 255, 255));
        jLabel20.setText("Desmarque los maestros que no quiera en sus horarios");
        jLabel20.setOpaque(true);
        getContentPane().add(jLabel20, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 400, -1, -1));

        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/engranes.jpg"))); // NOI18N
        getContentPane().add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(1260, 0, 80, 175));

        jLabel4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/engranes.jpg"))); // NOI18N
        getContentPane().add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 180, 175));

        jLabel5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/engranes.jpg"))); // NOI18N
        getContentPane().add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 0, 180, 175));

        jLabel6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/engranes.jpg"))); // NOI18N
        getContentPane().add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(360, 0, 180, 175));

        jLabel7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/engranes.jpg"))); // NOI18N
        getContentPane().add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(540, 0, 180, 175));

        jLabel8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/engranes.jpg"))); // NOI18N
        jLabel8.setText("Daniel Gonzalez Cabrera");
        getContentPane().add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(720, 0, 180, 175));

        jLabel9.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/engranes.jpg"))); // NOI18N
        getContentPane().add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(900, 0, 180, 175));

        jLabel36.setText("Numero de horas: ");
        getContentPane().add(jLabel36, new org.netbeans.lib.awtextra.AbsoluteConstraints(1130, 30, 140, -1));

        jLabel10.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/engranes.jpg"))); // NOI18N
        getContentPane().add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(1080, 0, 180, 175));

        jLabel11.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/engranes.jpg"))); // NOI18N
        getContentPane().add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(1260, 170, 70, 175));

        jLabel12.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/engranes.jpg"))); // NOI18N
        getContentPane().add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 175, 180, 175));

        jLabel13.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/engranes.jpg"))); // NOI18N
        getContentPane().add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 175, 180, 175));

        jLabel14.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/engranes.jpg"))); // NOI18N
        getContentPane().add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(360, 175, 180, 175));

        jLabel15.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/engranes.jpg"))); // NOI18N
        getContentPane().add(jLabel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(540, 175, 180, 175));

        jLabel16.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/engranes.jpg"))); // NOI18N
        getContentPane().add(jLabel16, new org.netbeans.lib.awtextra.AbsoluteConstraints(720, 175, 180, 175));

        jLabel17.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/engranes.jpg"))); // NOI18N
        getContentPane().add(jLabel17, new org.netbeans.lib.awtextra.AbsoluteConstraints(900, 175, 180, 175));

        jLabel18.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/engranes.jpg"))); // NOI18N
        getContentPane().add(jLabel18, new org.netbeans.lib.awtextra.AbsoluteConstraints(1080, 175, 180, 175));

        jLabel19.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/engranes.jpg"))); // NOI18N
        jLabel19.setText("Daniel Gonzalez Cabrera");
        getContentPane().add(jLabel19, new org.netbeans.lib.awtextra.AbsoluteConstraints(1260, 350, 70, 175));

        jLabel21.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/engranes.jpg"))); // NOI18N
        getContentPane().add(jLabel21, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 350, 180, 175));

        jLabel22.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/engranes.jpg"))); // NOI18N
        getContentPane().add(jLabel22, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 350, 180, 175));

        jLabel23.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/engranes.jpg"))); // NOI18N
        getContentPane().add(jLabel23, new org.netbeans.lib.awtextra.AbsoluteConstraints(360, 350, 180, 175));

        jLabel24.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/engranes.jpg"))); // NOI18N
        getContentPane().add(jLabel24, new org.netbeans.lib.awtextra.AbsoluteConstraints(540, 350, 180, 175));

        jLabel25.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/engranes.jpg"))); // NOI18N
        getContentPane().add(jLabel25, new org.netbeans.lib.awtextra.AbsoluteConstraints(720, 350, 180, 175));

        jLabel26.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/engranes.jpg"))); // NOI18N
        getContentPane().add(jLabel26, new org.netbeans.lib.awtextra.AbsoluteConstraints(900, 350, 180, 175));

        jLabel27.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/engranes.jpg"))); // NOI18N
        getContentPane().add(jLabel27, new org.netbeans.lib.awtextra.AbsoluteConstraints(1080, 350, 180, 175));

        jLabel28.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/engranes.jpg"))); // NOI18N
        getContentPane().add(jLabel28, new org.netbeans.lib.awtextra.AbsoluteConstraints(1260, 520, 70, 175));

        jLabel29.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/engranes.jpg"))); // NOI18N
        getContentPane().add(jLabel29, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 525, 180, 175));

        jLabel30.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/engranes.jpg"))); // NOI18N
        getContentPane().add(jLabel30, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 525, 180, 175));

        jLabel31.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/engranes.jpg"))); // NOI18N
        jLabel31.setText("Daniel Gonzalez Cabrera");
        getContentPane().add(jLabel31, new org.netbeans.lib.awtextra.AbsoluteConstraints(360, 525, 180, 175));

        jLabel32.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/engranes.jpg"))); // NOI18N
        getContentPane().add(jLabel32, new org.netbeans.lib.awtextra.AbsoluteConstraints(540, 525, 180, 175));

        jLabel33.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/engranes.jpg"))); // NOI18N
        getContentPane().add(jLabel33, new org.netbeans.lib.awtextra.AbsoluteConstraints(720, 525, 180, 175));

        jLabel34.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/engranes.jpg"))); // NOI18N
        getContentPane().add(jLabel34, new org.netbeans.lib.awtextra.AbsoluteConstraints(900, 525, 180, 175));

        jLabel35.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/engranes.jpg"))); // NOI18N
        getContentPane().add(jLabel35, new org.netbeans.lib.awtextra.AbsoluteConstraints(1080, 525, 180, 175));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseClicked

    }//GEN-LAST:event_jTable1MouseClicked

    private void jTable2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable2MouseClicked
    }//GEN-LAST:event_jTable2MouseClicked

    private void CrearHorariosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CrearHorariosActionPerformed
        try {
            t = new Thread(this);
            t.start();
        } catch (Exception e) {

        }
    }//GEN-LAST:event_CrearHorariosActionPerformed

    private void AnnteriorHorarioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AnnteriorHorarioActionPerformed
        // TODO add your handling code here:
        try {
            if (numHorario > 0) {
                horariosPintados = new ColorCeldas(jTable3);
                numHorario--;
                modeloTabla.setRowCount(0);
                modeloTabla.setNumRows(14);
                for (int i = 7; i < 21; i++) {
                    if (i == 12) {
                        jTable3.setValueAt((i) + " PM", i - 7, 0);
                    } else if (i > 12) {
                        jTable3.setValueAt((i - 12) + " PM", i - 7, 0);
                    } else {
                        jTable3.setValueAt(i + " AM", i - 7, 0);
                    }

                }
                sc.get(numHorario).Mandar(jTable3, horariosPintados);
                jNumHorario.setText("Horario #" + (numHorario + 1));
            }
            jLabel36.setText("Numero de horas: " + (sc.get(numHorario).horaSalida - sc.get(numHorario).horaEntrada));

        } catch (Exception e) {

        }

    }//GEN-LAST:event_AnnteriorHorarioActionPerformed

    private void SiguienteHorarioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SiguienteHorarioActionPerformed
        try {
            if (numHorario < sc.size() - 1) {
                horariosPintados = new ColorCeldas(jTable3);
                numHorario++;
                modeloTabla.setRowCount(0);
                modeloTabla.setNumRows(14);
                for (int i = 7; i < 21; i++) {
                    if (i == 12) {
                        jTable3.setValueAt((i) + " PM", i - 7, 0);
                    } else if (i > 12) {
                        jTable3.setValueAt((i - 12) + " PM", i - 7, 0);
                    } else {
                        jTable3.setValueAt(i + " AM", i - 7, 0);
                    }

                }
                sc.get(numHorario).Mandar(jTable3, horariosPintados);
                jNumHorario.setText("Horario #" + (numHorario + 1));
            }
            jLabel36.setText("Numero de horas: " + (sc.get(numHorario).horaSalida - sc.get(numHorario).horaEntrada));

        } catch (Exception e) {

        }
    }//GEN-LAST:event_SiguienteHorarioActionPerformed

    private void jTable3MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable3MousePressed
        // TODO add your handling code here:
        try {
            horariosPintados.repaint();
        } catch (Exception ex) {
        }
    }//GEN-LAST:event_jTable3MousePressed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:

        try {
            if (sc.size() > 0) {
                String nombreA = "";
                while ("".equals(nombreA)) {
                    nombreA = JOptionPane.showInputDialog("Elija un nombre para este horario.", nombreA);
                    if (nombreA != null) {
                        if (nombreA.length() > 0) {
                            Archivo horariosGuardados = new Archivo(c.nombre + "\\horariosGuardados.txt");
                            horariosGuardados.crearLectura();
                            String texto = "";
                            try {
                                String line;

                                do {
                                    line = horariosGuardados.LeerLinea();
                                    if (line != null) {
                                        texto += line + "\n";

                                    }
                                } while (line != null);

                            } catch (Exception ex) {
                            }
                            horariosGuardados.crearEscritura();
                            texto += nombreA + "->";
                            for (int i = 0; i < sc.get(numHorario).getSchedule().size(); i++) {
                                if (i < sc.get(numHorario).getSchedule().size() - 1) {
                                    texto += sc.get(numHorario).getSchedule().get(i).codigoMat + "-";
                                } else {
                                    texto += sc.get(numHorario).getSchedule().get(i).codigoMat;
                                }

                            }
                            horariosGuardados.EscribirLinea(texto);
                            horariosGuardados.CerrarEscritura();

                        } else {
                            JOptionPane.showMessageDialog(this, "Asigne un nombre al horario que desea guardar.");
                        }
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "Asegúrese de que tenga al menos un horario creado.");
            }
        } catch (HeadlessException ex) {
        }

    }//GEN-LAST:event_jButton1ActionPerformed

    private void jTable1MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MousePressed

    }//GEN-LAST:event_jTable1MousePressed

    private void jTable1MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseReleased
        // TODO add your handling code here:
        try {
            InsertarMaterias();
            int r = jTable1.getSelectedRow();
            jTable1.changeSelection(r, 0, false, false);
        } catch (Exception e) {

        }
    }//GEN-LAST:event_jTable1MouseReleased

    private void jTable1MouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseDragged

    }//GEN-LAST:event_jTable1MouseDragged

    private void jTable2MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable2MouseReleased
        // TODO add your handling code here:
        try {
            int r = jTable2.getSelectedRow();
            for (int j = 0; j < Materia.lista.size(); j++) {
                for (int i = 0; i < listaMaestros.size(); i++) {
                    if (listaMaestros.get(i).codigoMat.equals(Materia.lista.get(j).codigoMat)) {
                        Materia.lista.get(j).aprobado = (boolean) jTable2.getValueAt(i, 0);
                    }
                }

            }
            horario.clear();
            for (int i = 0; i < listaElegidos.size(); i++) {
                crearArreglosHorarios(listaElegidos.get(i), colores[i]);
            }
            jTable2.clearSelection();
            t = new Thread(this);
            t.start();
        } catch (Exception e) {

        }
    }//GEN-LAST:event_jTable2MouseReleased

    private void formWindowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosed
        // TODO add your handling code here:
        try {
            c.setVisible(true);
        } catch (Exception e) {

        }
    }//GEN-LAST:event_formWindowClosed

    public ArrayList<Horario> Sort(ArrayList<Horario> a, int opt) {
        ArrayList<Horario> nuevo = new ArrayList<>();
        nuevo.add(a.get(0));
        if (opt == 0) {
            for (int i = 1; i < a.size(); i++) {
                nuevo.add(a.get(i));
                for (int j = nuevo.size() - 1; j >= 1; j--) {
                    if (nuevo.get(j).horaEntrada < nuevo.get(j - 1).horaEntrada) {
                        Horario b = nuevo.get(j);
                        nuevo.set(j, nuevo.get(j - 1));
                        nuevo.set(j - 1, b);
                    } else {
                        break;
                    }
                }
            }
        } else {
            for (int i = 1; i < a.size(); i++) {
                nuevo.add(a.get(i));
                for (int j = nuevo.size() - 1; j >= 1; j--) {
                    if ((nuevo.get(j).horaSalida - nuevo.get(j).horaEntrada)
                            < (nuevo.get(j - 1).horaSalida - nuevo.get(j - 1).horaEntrada)) {
                        Horario b = nuevo.get(j);
                        nuevo.set(j, nuevo.get(j - 1));
                        nuevo.set(j - 1, b);
                    } else {
                        break;
                    }
                }
            }
        }

        return nuevo;
    }
    private void jRadioButPrimeraActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButPrimeraActionPerformed
        // TODO add your handling code here:
        if (sc.size() > 0) {

            sc = Sort(sc, 0);
            numHorario = 0;

            modeloTabla.setRowCount(0);
            modeloTabla.setNumRows(14);
            for (int i = 7; i < 21; i++) {
                if (i == 12) {
                    jTable3.setValueAt((i) + " PM", i - 7, 0);
                } else if (i > 12) {
                    jTable3.setValueAt((i - 12) + " PM", i - 7, 0);
                } else {
                    jTable3.setValueAt(i + " AM", i - 7, 0);
                }

            }
            horariosHabiles = 0;
            horariosPintados = new ColorCeldas(jTable3);

            if (sc.size() > 0) {
                sc.get(0).Mandar(jTable3, horariosPintados);
            }
            jLabel1.setText("Posibles Horarios " + sc.size());
            jTable1.setEnabled(true);
            this.jNumHorario.setText("Horario #" + (numHorario + 1));
        }
    }//GEN-LAST:event_jRadioButPrimeraActionPerformed

    private void jRadioButHorasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButHorasActionPerformed
        // TODO add your handling code here:
//        OrdenarXHorasEscolares(sc, 0, sc.size() - 1, 0);
        if (sc.size() > 0) {
            sc = Sort(sc, 1);
            numHorario = 0;

            modeloTabla.setRowCount(0);
            modeloTabla.setNumRows(14);
            for (int i = 7; i < 21; i++) {
                if (i == 12) {
                    jTable3.setValueAt((i) + " PM", i - 7, 0);
                } else if (i > 12) {
                    jTable3.setValueAt((i - 12) + " PM", i - 7, 0);
                } else {
                    jTable3.setValueAt(i + " AM", i - 7, 0);
                }

            }
            horariosHabiles = 0;
            horariosPintados = new ColorCeldas(jTable3);

            if (sc.size() > 0) {
                sc.get(0).Mandar(jTable3, horariosPintados);
            }
            jLabel1.setText("Posibles Horarios " + sc.size());
            jTable1.setEnabled(true);
            this.jNumHorario.setText("Horario #" + (numHorario + 1));
        }
    }//GEN-LAST:event_jRadioButHorasActionPerformed

    private void jTable1KeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTable1KeyTyped
        // TODO add your handling code here:


    }//GEN-LAST:event_jTable1KeyTyped

    private void jTable1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTable1KeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTable1KeyPressed

    private void jTable1KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTable1KeyReleased
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_DOWN || evt.getKeyCode() == KeyEvent.VK_UP) {
            try {
                InsertarMaterias();
                int r = jTable1.getSelectedRow();
                System.out.println(r);
                jTable1.changeSelection(r, 0, false, false);

            } catch (Exception e) {

            }
        }
    }//GEN-LAST:event_jTable1KeyReleased
    public void crearArreglosHorarios(String materia, Color color) {
        try {
            horario.add(new Horario());

            for (int j = 0; j < Materia.lista.size(); j++) {
                if (materia.equals(Materia.lista.get(j).nombreMat.substring(0, Materia.lista.get(j).nombreMat.length())) && Materia.lista.get(j).aprobado) {
                    Materia.lista.get(j).color = color;
                    horario.get(horario.size() - 1).getSchedule().add(Materia.lista.get(j));
                }
            }
        } catch (Exception e) {

        }
    }

    public void CrearCombinaciones(int ind, int n, Horario h) {
        for (int i = 0; i < horario.get(ind).getSchedule().size(); i++) {
            if (ind == 0) {
                h = new Horario();
                h.getSchedule().add(horario.get(ind).getSchedule().get(i));
                CrearCombinaciones(ind + 1, n, h);
            } else if (ind < n - 1) {
                Horario aux = null;
                try {
                    aux = (Horario) h.clone();
                } catch (CloneNotSupportedException ex) {
                }
                if (aux != null && aux.insertar(horario.get(ind).getSchedule().get(i))) {
                    CrearCombinaciones(ind + 1, n, aux);
                }
            } else {
                Horario aux = null;
                try {
                    aux = (Horario) h.clone();
                } catch (CloneNotSupportedException ex) {
                }
                if (aux != null && aux.insertar(horario.get(ind).getSchedule().get(i))) {
                    sc.add(aux);
                }
            }
        }
    }

    public void OrdenarXHorasEscolares(ArrayList<Horario> a, int start, int end, int opt) {
        int i = start;
        int j = end;
        int n1, n2, n3;
        Horario pivot;
        if (j - i >= 1) {
            if (opt == 0) {
                pivot = a.get(i);
                n2 = (pivot.horaSalida - pivot.horaEntrada);
                while (j > i) {
                    while ((a.get(i).horaSalida - a.get(i).horaEntrada - n2) <= 0 && i < end && j > i) {
                        i++;
                    }
                    while ((a.get(j).horaSalida - a.get(j).horaEntrada - n2) >= 0 && j > start && j >= i) {
                        j--;
                    }
                    if (j > i) {
                        swap(a, i, j);
                    }
                }

                swap(a, start, j);

                OrdenarXHorasEscolares(a, start, j - 1, opt);
                OrdenarXHorasEscolares(a, j + 1, end, opt);
            }
        }
    }

    private static void swap(ArrayList<Horario> a, int i, int j) {
        Horario temp = a.get(i);
        a.set(i, a.get(j));
        a.set(j, temp);
    }

    public void crearHorarios() {
        try {
            numHorario = 0;

            modeloTabla.setRowCount(0);
            modeloTabla.setNumRows(14);
            for (int i = 7; i < 21; i++) {
                if (i == 12) {
                    jTable3.setValueAt((i) + " PM", i - 7, 0);
                } else if (i > 12) {
                    jTable3.setValueAt((i - 12) + " PM", i - 7, 0);
                } else {
                    jTable3.setValueAt(i + " AM", i - 7, 0);
                }

            }
            sc = new ArrayList<>();

            try {
                CrearCombinaciones(0, listaElegidos.size(), new Horario());
                for (int i = 0; i < sc.size(); i++) {
                    sc.get(i).ordenarHoras();
                }

                OrdenarXHorasEscolares(sc, 0, sc.size() - 1, 0);
                horariosHabiles = 0;
                horariosPintados = new ColorCeldas(jTable3);

                if (sc.size() > 0) {
                    sc.get(0).Mandar(jTable3, horariosPintados);
                }
                jLabel1.setText("Posibles Horarios " + sc.size());

                if (sc.size() < 1) {
                    JOptionPane.showMessageDialog(this, "No se ha podido crear ningun horario con estas especificaciones");
                }

                load.setVisible(false);
            } catch (HeadlessException ex) {
                load.setVisible(false);
                JOptionPane.showMessageDialog(this, "Debe escoger de 2 a 8 materias para hacer horarios.");
            }
            jTable1.setEnabled(true);
            this.jNumHorario.setText("Horario #" + (numHorario + 1));
        } catch (HeadlessException e) {

        }
    }
    int contAzul = 0;
    int suma;

    public void InsertarMaterias() {
        suma = 0;
        colorCeldas = new ColorCeldas(jTable1);
        int r = jTable1.getSelectedRow();
        listaElegidos.clear();
        int index = 0;
        for (int i = 0; i < jTable1.getRowCount(); i++) {
            if ((boolean) jTable1.getValueAt(i, 0) == true) {
                listaElegidos.add(jTable1.getValueAt(i, 1).toString());
                suma += Integer.valueOf(jTable1.getValueAt(i, 2).toString());
            }
        }
        if (suma > 36) {
            suma -= Integer.valueOf(jTable1.getValueAt(r, 2).toString());
            for (int i = 0; i < listaElegidos.size(); i++) {
                if (jTable1.getValueAt(r, 1).toString().equals(listaElegidos.get(i))) {
                    listaElegidos.remove(i);
                    jTable1.setValueAt(false, r, 0);
                }
            }
            this.jLabError.setText(" LIMITE DE " + Alumno.creditos + " CREDITOS EXCEDIDO ");
            this.jLabError.setVisible(true);
        } else {
            this.jLabError.setVisible(false);
        }
        if ((boolean) jTable1.getValueAt(r, 0) != true) {
            colorCeldas.addCelda(r, 0, new Color(57, 105, 138));
            colorCeldas.addCelda(r, 1, new Color(57, 105, 138));
            colorCeldas.addCelda(r, 2, new Color(57, 105, 138));
        }

        for (int i = 0; i < listaElegidos.size(); i++) {
            for (int j = 0; j < jTable1.getRowCount(); j++) {
                if (listaElegidos.get(i).equals(jTable1.getValueAt(j, 1))) {
                    colorCeldas.addCelda(j, 0, colores[index]);
                    colorCeldas.addCelda(j, 1, colores[index]);
                    colorCeldas.addCelda(j, 2, colores[index++]);
                    break;
                }
            }
        }

        colorCeldas.repaint();
        jLabCreditos.setText("Creditos acumulados: " + suma);
        int cont = 0;
        boolean band = false;
        listaMaestros.clear();
        String materiaTabla, materiaLista;
        for (int i = 0; i < Materia.lista.size(); i++) {
            materiaTabla = jTable1.getValueAt(r, 1).toString();
            materiaLista = Materia.lista.get(i).nombreMat;
            if (materiaTabla.equals(materiaLista)) {
                cont++;
                modeloProfesores.setRowCount(cont);
                jTable2.setValueAt(Materia.lista.get(i).aprobado, cont - 1, 0);
                jTable2.setValueAt(Materia.lista.get(i).maestroMat, cont - 1, 1);
                int mayor = 0;
                int indice = 0;
                for (int j = 0; j < Materia.lista.get(i).horarioMat.length; j++) {
                    String[] finales = Materia.lista.get(i).horaFinales[j].split(":");
                    String[] inicios = Materia.lista.get(i).horaInicios[j].split(":");
                    int horaFinal = Integer.valueOf(finales[0]);
                    int horaInicio = Integer.valueOf(inicios[0]);
                    if ((horaFinal - horaInicio) > mayor) {
                        mayor = (horaFinal - horaInicio);
                        indice = j;
                    }
                }
                jTable2.setValueAt(Materia.lista.get(i).horarioMat[indice], cont - 1, 2);
                band = true;
                listaMaestros.add(Materia.lista.get(i));
            } else {
                if (band) {
                    break;
                }
            }
        }

        horario.clear();
        if (listaElegidos.size() > 1) {
            for (int i = 0; i < listaElegidos.size(); i++) {
                crearArreglosHorarios(listaElegidos.get(i), colores[i]);
            }
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MateriasFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            new MateriasFrame(null, "", "").setVisible(true);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton AnnteriorHorario;
    private javax.swing.JButton CrearHorarios;
    private javax.swing.JButton SiguienteHorario;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabCreditos;
    private javax.swing.JLabel jLabError;
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
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel jNumHorario;
    private javax.swing.JRadioButton jRadioButHoras;
    private javax.swing.JRadioButton jRadioButPrimera;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTable jTable1;
    private javax.swing.JTable jTable2;
    private javax.swing.JTable jTable3;
    // End of variables declaration//GEN-END:variables

    @Override
    public void run() {
//        t.start();
        load.setVisible(true);
        crearHorarios();
    }
}
