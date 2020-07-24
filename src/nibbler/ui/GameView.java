package nibbler.ui;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.font.TextAttribute;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Ein Fenster, dass die Anzeige von ASCI-Spielen erlaubt, Ton ausgibt und Tastendrücke und Mausklicks als Events
 * zurückliefert.<br>
 * <br>
 *
 * @author Andreas Berl
 * <pre>
 * Version 5.3:
 * getKeyCodes ist jetzt public
 * Version 5.2:
 * Rechte Maustaste repariert
 * Javadoc Maus und Tasten repariert
 * getSringFromUser()
 * getKeyCodesFromPressedKeys
 * </pre>
 */
public class GameView {

    private static class Version {
        private final static String VERSION = "5.3";
        private final static LocalDate DATE = LocalDate.parse("2020-07-07");
        private final static String STANDARDTITLE = "GameView";
        private final static String SIGNATURE = "Prof. Dr. Andreas Berl - TH Deggendorf";

        static String getStatusSignature() {
            return "   " + STANDARDTITLE + " " + VERSION + " (" + DATE.getYear() + ") - " + SIGNATURE + " ";
        }

        static String getSTANDARDTITLE() {
            return STANDARDTITLE;
        }
    }

    // Konstante Aufrufparameter
    public static final int WINDOWSIZE_SMALL = 0;
    public static final int WINDOWSIZE_NORMAL = 1;
    public static final int WINDOWSIZE_LARGE = 2;
    public static final int WINDOWSIZE_MAXIMIZED = 3;

    // Konstanten
    private static final int MAX_LINES = 360;
    private static final int MAX_COLUMNS = 640;

    // Canvas-Übergreifende Variablen
    private HashMap<Character, Color> colormap;

    // Klassen
    private Canvas canvas;
    private Window window;
    private Mouse mouse;
    private Keyboard keyboard;
    private Sound sound;

    /**
     * Erzeugt ein Fenster zur Anzeige von ASCII-Spielen, mit Sound, Tastatur- und Maus-Eingaben. Es wird ein Fenster
     * mit <code>lines</code> Zeilen und <code>columns</code> Spalten erzeugt. Bitte initialisieren Sie das Fenster mit
     * der Auflösung, die am häufigsten benötigt wird, da die Form des Fensters davon abhängt. Sie können die Auflösung
     * später mit der Methode <code>changeResolution(int lines, int columns)</code> ändern. Die Größe des Fensters kann
     * festgelegt werden. Es gibt GameView.WINDOWSIZE_SMALL, GameView.WINDOWSIZE_NORMAL, GameView.WINDOWSIZE_LARGE und
     * GameView.WINDOWSIZE_MAXIMIZED.
     *
     * @param lines   Die gewählte Auflösung (Zeilen). Maximale Zeilenanzahl: {@value GameView#MAX_LINES}
     * @param columns Die gewählte Auflösung (Spalten). Maximale Spaltenanzahl: {@value GameView#MAX_COLUMNS}
     */
    public GameView(int lines, int columns) {
        this(lines, columns, WINDOWSIZE_NORMAL);
    }

    /**
     * Erzeugt ein Fenster zur Anzeige von ASCII-Spielen, mit Sound, Tastatur- und Maus-Eingaben. Es wird ein Fenster
     * mit <code>lines</code> Zeilen und <code>columns</code> Spalten erzeugt. Bitte initialisieren Sie das Fenster mit
     * der Auflösung, die am häufigsten benötigt wird, da die Form des Fensters davon abhängt. Sie können die Auflösung
     * später mit der Methode <code>changeResolution(int lines, int columns)</code> ändern. Die Größe des Fensters kann
     * festgelegt werden. Es gibt GameView.WINDOWSIZE_SMALL, GameView.WINDOWSIZE_NORMAL, GameView.WINDOWSIZE_LARGE und
     * GameView.WINDOWSIZE_MAXIMIZED.
     *
     * @param lines      Die gewählte Auflösung (Zeilen). Maximale Zeilenanzahl: {@value GameView#MAX_LINES}
     * @param columns    Die gewählte Auflösung (Spalten). Maximale Spaltenanzahl: {@value GameView#MAX_COLUMNS}
     * @param windowSize Größe des Fensters. Es gibt GameView.WINDOWSIZE_SMALL, GameView.WINDOWSIZE_NORMAL, GameView
     *                   .WINDOWSIZE_LARGE und GameView.WINDOWSIZE_MAXIMIZED.
     */
    public GameView(int lines, int columns, int windowSize) {
        checkResolution(lines, columns);

        // Canvas-Übergreifende Variablen
        initColormap();

        // Klassen
        SwingAdapter swingAdapter = new SwingAdapter(lines, columns, windowSize);
        this.window = new Window(swingAdapter);
        this.mouse = new Mouse(swingAdapter);
        this.keyboard = new Keyboard(swingAdapter);
        this.sound = new Sound();
        this.canvas = new Canvas(lines, columns, colormap);

        swingAdapter.registerListeners(window, mouse, keyboard, sound);
    }

    private void initColormap() {
        this.colormap = new HashMap<>();
        colormap.put('R', Color.RED);
        colormap.put('r', Color.RED.brighter());
        colormap.put('G', Color.GREEN);
        colormap.put('g', Color.GREEN.brighter());
        colormap.put('B', Color.BLUE);
        colormap.put('b', Color.BLUE.brighter());
        colormap.put('Y', Color.YELLOW);
        colormap.put('y', Color.YELLOW.brighter());
        colormap.put('P', Color.PINK);
        colormap.put('p', Color.PINK.brighter());
        colormap.put('C', Color.CYAN);
        colormap.put('c', Color.CYAN.brighter());
        colormap.put('M', Color.MAGENTA);
        colormap.put('m', Color.MAGENTA.brighter());
        colormap.put('O', Color.ORANGE);
        colormap.put('o', Color.ORANGE.brighter());
        colormap.put('W', Color.WHITE);
        colormap.put('L', Color.BLACK);
    }

    /**
     * Wandelt ein char[][] in einen String um.
     *
     * @param chars Das char[][], welches umgewandelt werden soll.
     * @return Das umgewandelte char[][] als String
     */
    public static String convertCharArrayToString(char[][] chars) {
        return StringHelper.convertCharArrayToString(chars);
    }

    /**
     * Wandelt einen String in ein rechteckiges char[][] um. Lücken werden mit Leerzeichen aufgefüllt.
     *
     * @param string Der String der umgewandelt werden soll.
     * @return Der umgewandelte String als Rechteck.
     */
    public static char[][] convertStringToCharArray(String string) {
        return StringHelper.convertStringToCharArray(string);
    }

    /**
     * Legt ein Symbol für die Titelleiste fest. Das Symbolfile muss in einem Verzeichnis "src/resources" liegen.Bitte
     * den Namen des Files ohne Verzeichnisnamen angeben, z.B."Symbol.png".
     *
     * @param windowIcon Das Symbol.
     */
    public void setWindowIcon(String windowIcon) {
        window.setWindowIcon(windowIcon);
    }

    /**
     * Text, der in der Statuszeile angezeigt wird.
     *
     * @param statusText Text der Statuszeile.
     */
    public void setStatusText(String statusText) {
        window.setStatusText(statusText);
    }

    /**
     * Setzt den Fenstertitel.
     *
     * @param title Der Fenstertitel
     */
    public void setTitle(String title) {
        window.setTitle(title);
    }

    /**
     * Ändert die Auflösung im Fenster. Die Fenstergröße wird dabei beibehalten.
     *
     * @param lines   Die gewählte Auflösung (Zeilen). Maximale Zeilenanzahl: {@value GameView#MAX_LINES}
     * @param columns Die gewählte Auflösung (Spalten). Maximale Spaltenanzahl: {@value GameView#MAX_COLUMNS}
     */
    public void changeResolution(int lines, int columns) {
        checkResolution(lines, columns);
        canvas = new Canvas(lines, columns, colormap);
        printCanvas();
        sleep(200); // Warte bis die Änderungen durchgeführt sind!
    }

    private void checkResolution(int lines, int columns) {
        if (lines < 1 || columns < 1 || lines > MAX_LINES || columns > MAX_COLUMNS) {
            throw new NumberFormatException("Zeilen und Spalten müssen größer als 1 und sein, Zeilen kleiner gleich " + MAX_LINES + " und Spalten kleiner gleich " + MAX_COLUMNS + ".");
        }
    }

    /**
     * Gibt den char an der Stelle (Zeile, Spalte) im aktuellen Canvas zurück.
     *
     * @return Char im aktuellen Canvas.
     */
    public char getCharacter(int line, int column) {
        return canvas.getCharacter(line, column);
    }

    /**
     * Legt fest, ob die Maus im Fenster benutzt werden soll. Falls sie nicht benutzt wird, wird der Cursor der Maus auf
     * den Default-Ansicht zurückgesetzt und die Maus wird ausgeblendet. Falls sie benutzt wird, werden Maus-Events
     * erzeugt, die verwendet werden können. Die Standardeinstellung ist
     * <code>false</code>.
     *
     * @param useMouse Legt fest, ob die Maus im Fenster benutzt werden soll.
     */
    public void useMouse(boolean useMouse) {
        mouse.useMouse(useMouse);
    }

    /**
     * Legt ein neues Symbol für den Maus-Cursor fest. Das Bildfile muss in einem Verzeichnis "src/resources" liegen.
     * Bitte den Namen des Files ohne Verzeichnisnamen angeben, z.B. "Cursor.png".
     *
     * @param cursor   Name des Bildfiles. Das Bildfile muss in einem Verzeichnis "src/resources" liegen. Bitte den
     *                 Namen des Files ohne Verzeichnisnamen angeben, z.B. "Cursor.png".
     * @param centered Gibt an, ob der Hotspot des Cursors in der Mitte des Symbols oder oben links liegen soll.
     *                 Ansonsten ist der Hotspot oben links.
     */
    public void setMouseCursor(String cursor, boolean centered) {
        mouse.setMouseCursor(cursor, centered);
    }


    /**
     * Der Maus-Cursor wird auf das Standard-Icon zurückgesetzt.
     */
    public void setStandardMouseCursor() {
        mouse.setStandardMouseCursor();
    }

    /**
     * Setzt die Default-Farbe der Schrift.
     *
     * @param fontColor Schriftfarbe
     */
    public void setDefaultFontColor(Color fontColor) {
        canvas.setDefaultFontColor(fontColor);
    }

    /**
     * Setzt die Default-Farbe des Hintergrunds.
     *
     * @param backgroundColor Hintergrundfarbe
     */
    public void setDefaultBackgroundColor(Color backgroundColor) {
        canvas.setDefaultBackgroundColor(backgroundColor);
    }

    /**
     * Gibt den übergebenen <code>String</code> zentriert im Fenster aus. Diese Methode zeigt maximal alle 16 ms ein
     * Bild an (60 Frames pro Sekunde).
     *
     * @param string Der anzuzeigende String.
     */
    public void printCentred(String string) {
        print(string, true);
    }


    /**
     * Gibt den übergebenen <code>String</code> im Fenster aus. Diese Methode zeigt maximal alle 16 ms ein Bild an (60
     * Frames pro Sekunde).
     *
     * @param string Der anzuzeigende String.
     */
    public void print(String string) {
        print(string, false);
    }

    private void print(String string, boolean centred) {
        clearCanvas();
        char temp = canvas.getNonTransparentSpace();
        setNonTransparentSpace('\u1000');
        if (centred) {
            addToCanvasCentered(string);
        } else {
            addToCanvas(string, 0, 0);
        }
        printCanvas();
        setNonTransparentSpace(temp);
    }


    /**
     * Erzeugt eine einfaches Eingabefeld für den Benutzer. Der Benutzer kann eine Zeichenfolge eingeben und dann die
     * Taste "Eingabe" drücken. Die Eingabe wird als String zurückgegeben. Das Eingabefeld erscheint an der angegebenen
     * Stelle und hat die angegebene Länge.
     *
     * @param fieldLength Die Länge des Eingabefelds.
     * @param line        Zeile in der der das Eingabefeld angezeigt werden soll. Zeile 0 ist oben.
     * @param column      Spalte, in der das Eingabefeld angezeigt werden soll. Spalte 0 ist links.
     * @return Die vom Benutzer eingegebene Zeichenfolge.
     */
    public String getStringFromUser(int fieldLength, int line, int column) {
        String input = "";
        String cursorOn = "_";
        String cursorOff = ".";
        String cursor = cursorOn;
        long blinkTime = 0;
        int currentColumn = column;
        addToCanvas(cursor + cursorOff.repeat(fieldLength - 1), line, currentColumn);
        while (true) {
            sleep(16);
            if (System.currentTimeMillis() - blinkTime > 500) {
                cursor = cursor.equals(cursorOff) ? cursorOn : cursorOff;
                printStringToCursorPosition(cursor, line, currentColumn, input.length(), fieldLength);
                blinkTime = System.currentTimeMillis();
            }
            KeyEvent[] keyEvents = getKeyEvents();
            for (KeyEvent keyEvent : keyEvents) {
                if (keyEvent.getID() == KeyEvent.KEY_PRESSED) {
                    if (keyEvent.getKeyCode() == KeyEvent.VK_ENTER) {
                        printStringToCursorPosition(cursorOff, line, currentColumn, input.length(), fieldLength);
                        return input.strip();
                    } else if (keyEvent.getKeyChar() != KeyEvent.CHAR_UNDEFINED) {
                        if (keyEvent.getKeyChar() == '\b') { // Backspace
                            if (input.length() > 0) {
                                printStringToCursorPosition(cursorOff, line, currentColumn, input.length(),
                                        fieldLength);
                                currentColumn--;
                                input = input.substring(0, input.length() - 1);
                                printStringToCursorPosition(cursor, line, currentColumn, input.length(), fieldLength);
                            }
                        } else if (input.length() < fieldLength) {
                            input += keyEvent.getKeyChar();
                            addToCanvas(cursorOff, line, currentColumn);
                            addToCanvas(String.valueOf(keyEvent.getKeyChar()), line, currentColumn);
                            currentColumn++;
                            printStringToCursorPosition(cursor, line, currentColumn, input.length(), fieldLength);
                        }
                    }
                }
            }
        }
    }

    private void printStringToCursorPosition(String cursor, int line, int column, int inputLength, int fieldLength) {
        if (inputLength < fieldLength) {
            addToCanvas(cursor, line, column);
        }
        printCanvas();
    }

    /**
     * Erzeugt eine einfaches Eingabefeld für den Benutzer. Dem Benutzer wird ein Hinweis angezeigt. Der Benutzer kann
     * eine Zeichenfolge eingeben und dann die Taste "Eingabe" drücken. Die Eingabe wird als String zurückgegeben. Der
     * Hinweis erscheint zusammen mit dem Eingabefeld unter der letzen angezeigten Zeile. Das Eingabefeld hat die
     * angegebene Länge.
     *
     * @param info        Ein Hinweis der vor dem Eingabefeld ausgegeben wird. Der Hinweis darf auch leer sein ("").
     * @param fieldLength Die Länge des Eingabefelds.
     * @return Die vom Benutzer eingegebene Zeichenfolge.
     */
    public String getStringFromUser(String info, int fieldLength) {
        int canvasLines = 0;
        loop:
        for (int zeile = canvas.characters.length - 1; zeile >= 0; zeile--) {
            for (int zeichen = 0; zeichen < canvas.characters[zeile].length; zeichen++) {
                if (canvas.characters[zeile][zeichen] != ' ') {
                    canvasLines = zeile;
                    break loop;
                }
            }
        }
        int infoLines = StringHelper.convertStringToCharArray(info).length;
        int lastLineBreak = Math.max(info.lastIndexOf("\n"), info.lastIndexOf("\r"));
        int column = info.length() - lastLineBreak - 1;
        addToCanvas(info, canvasLines + 1, 0);
        printCanvas();
        return getStringFromUser(fieldLength, canvasLines + infoLines, column);
    }

    /**
     * Löscht alle Inhalte auf dem Canvas.
     */
    public void clearCanvas() {
        canvas.clearCanvas();
    }


    /**
     * Schreibt den übergebenen <code>String</code> zentriert auf das Canvas, ohne die bisherigen Inhalte zu löschen.
     * Achtung: In dieser Methode ist das Leerzeichen durchsichtig (Objekte im Hintergrund sind zu sehen). Falls ein
     * undurchsichtiges Leerzeichen benötigt wird (die Hintergrundfarbe ist zu sehen), kann statt dessen das Semikolon
     * ";" verwendet werden. Das undurchsichtige Leerzeichen kann mit der Methode
     * <code>setNonTransparentSpace(char c)</code> auf ein anderes Zeichen gesetzt
     * werden.
     *
     * @param string Der zu schreibende <code>String</code>.
     * @param color  Farbe, die verwendet werden soll.
     */
    public void addToCanvasCentered(String string, Color color) {
        char[][] chars = StringHelper.convertStringToCharArray(string);
        int zeilen = chars.length;
        int spalten = chars[0].length;
        addToCanvas(string, canvas.getLines() / 2 - zeilen / 2, canvas.getColumns() / 2 - spalten / 2, color);
    }

    /**
     * Schreibt den übergebenen <code>String</code> zentriert auf das Canvas, ohne die bisherigen Inhalte zu löschen.
     * Achtung: In dieser Methode ist das Leerzeichen durchsichtig (Objekte im Hintergrund sind zu sehen). Falls ein
     * undurchsichtiges Leerzeichen benötigt wird (die Hintergrundfarbe ist zu sehen), kann statt dessen das Semikolon
     * ";" verwendet werden. Das undurchsichtige Leerzeichen kann mit der Methode
     * <code>setNonTransparentSpace(char c)</code> auf ein anderes Zeichen gesetzt
     * werden.
     *
     * @param string Der zu schreibende <code>String</code>.
     */
    public void addToCanvasCentered(String string) {
        addToCanvasCentered(string, null);
    }


    /**
     * Schreibt den übergebenen <code>String</code> auf das Canvas, ohne die bisherigen Inhalte zu löschen. Zusätzlich
     * werden Koordinaten ausgewertet: (0, 0) ist oben links. Negative Koordinaten können verwendet werden um Objekte
     * teilweise anzuzeigen. Achtung: In dieser Methode ist das Leerzeichen durchsichtig (Objekte im Hintergrund sind zu
     * sehen). Falls ein undurchsichtiges Leerzeichen benötigt wird (die Hintergrundfarbe ist zu sehen), kann statt
     * dessen das Semikolon ";" verwendet werden. Das undurchsichtige Leerzeichen kann mit der Methode
     * <code>setNonTransparentSpace(char c)</code> auf ein anderes Zeichen gesetzt
     * werden.
     *
     * @param string Der zu schreibende <code>String</code>.
     * @param line   Zeile in der der <code>String</code> angezeigt werden soll. Zeile0  ist oben.
     * @param column Spalte, in der der <code>String</code> angezeigt werden soll. Spalte 0 ist links.
     */
    public void addToCanvas(String string, int line, int column) {
        addToCanvas(string, line, column, null);
    }

    /**
     * Schreibt den übergebenen <code>String</code> auf das Canvas, ohne die bisherigen Inhalte zu löschen. Zusätzlich
     * werden Koordinaten ausgewertet: (0, 0) ist oben links. Negative Koordinaten können verwendet werden um Objekte
     * teilweise anzuzeigen. Achtung: In dieser Methode ist das Leerzeichen durchsichtig (Objekte im Hintergrund sind zu
     * sehen). Falls ein undurchsichtiges Leerzeichen benötigt wird (die Hintergrundfarbe ist zu sehen), kann statt
     * dessen das Semikolon ";" verwendet werden. Das undurchsichtige Leerzeichen kann mit der Methode
     * <code>setNonTransparentSpace(char c)</code> auf ein anderes Zeichen gesetzt
     * werden.
     *
     * @param string Der zu schreibende <code>String</code>.
     * @param line   Zeile in der der <code>String</code> angezeigt werden soll. Zeile 0 ist oben.
     * @param column Spalte, in der der <code>String</code> angezeigt werden soll. Spalte 0 ist links.
     * @param color  Farbe, die verwendet werden soll.
     */
    public void addToCanvas(String string, int line, int column, Color color) {
        canvas.addToCanvas(string, line, column, color, false);
    }

    /**
     * Der übergebene String aus Farbcodes wird auf das Canvas übertragen, ohne die bisherigen Inhalte zu löschen. Die
     * darin enthaltenen Buchstaben werden als Farben interpretiert. Jeder Buchstabe repräsentiert ein "Pixel". Dazu
     * wird eine Colormap ausgewertet, die durch eine Methode <code>setColormap()</code> geändert werden kann.
     * <pre>
     * <code>
     * HashMap<Character, Color> colormap = new HashMap<>();
     * colormap.put('R', Color.RED);
     * colormap.put('r', Color.RED.brighter());
     * colormap.put('G', Color.GREEN);
     * colormap.put('g', Color.GREEN.brighter());
     * colormap.put('B', Color.BLUE);
     * colormap.put('b', Color.BLUE.brighter());
     * colormap.put('Y', Color.YELLOW);
     * colormap.put('y', Color.YELLOW.brighter());
     * colormap.put('P', Color.PINK);
     * colormap.put('p', Color.PINK.brighter());
     * colormap.put('C', Color.CYAN);
     * colormap.put('c', Color.CYAN.brighter());
     * colormap.put('M', Color.MAGENTA);
     * colormap.put('m', Color.MAGENTA.brighter());
     * colormap.put('O', Color.ORANGE);
     * colormap.put('o', Color.ORANGE.brighter());
     * colormap.put('W', Color.WHITE);
     * </code>
     * </pre>
     * Zusätzlich werden Koordinaten ausgewertet: (0, 0) ist oben links. Negative Koordinaten können verwendet werden um
     * Objekte teilweise anzuzeigen. Achtung: In dieser Methode ist das Leerzeichen durchsichtig (Objekte im Hintergrund
     * sind zu sehen). Falls ein undurchsichtiges Leerzeichen benötigt wird (die Hintergrundfarbe ist zu sehen), kann
     * statt dessen das Semikolon ";" verwendet werden. Das undurchsichtige Leerzeichen kann mit der Methode
     * <code>setNonTransparentSpace(char c)</code> auf ein anderes Zeichen gesetzt
     * werden.
     *
     * @param colorString Der Farb-Codierte String.
     * @param line        Zeile in der der String angezeigt werden soll. Zeile 0 ist oben.
     * @param column      Spalte, in der der String angezeigt werden soll. Spalte 0 ist links.
     */
    public void addColorStringToCanvas(String colorString, int line, int column) {
        canvas.addToCanvas(colorString, line, column, null, true);
    }

    /**
     * Füllt alle Stellen des Canvas mit dem gegebenen Zeichen. Vorhandene Inhalte werden überschrieben.
     *
     * @param c     Zeichen, das auf den Canvas geschrieben werden soll.
     * @param color Farbe, die verwendet werden soll.
     */
    public void fillCanvas(char c, Color color) {
        canvas.fillCanvas(c, color);
    }

    /**
     * Füllt alle Stellen des Canvas mit der Angegebenen Farbe. Vorhandene Inhalte werden überschrieben.
     *
     * @param color Farbe, die verwendet werden soll.
     */
    public void fillCanvas(Color color) {
        canvas.fillCanvas(color);
    }

    /**
     * Füllt alle Stellen des Canvas mit dem gegebenen Zeichen. Vorhandene Inhalte werden überschrieben.
     *
     * @param c Zeichen, das auf den Canvas geschrieben werden soll.
     */
    public void fillCanvas(char c) {
        fillCanvas(c, null);
    }

    /**
     * Zeigt den aktuellen Inhalt des Canvas im Fenster an. Diese Methode zeigt maximal alle 16 ms ein Bild an (60
     * Frames pro Sekunde).
     */
    public void printCanvas() {
        window.printCanvas(canvas);
    }

    /**
     * Das undurchsichtige Leerzeichen kann mit dieser Methode auf ein anderes Zeichen gesetzt werden. In den Methoden
     * <code>addToCanvas(...)</code> ist das normale Leerzeichen durchsichtig (Objekte im Hintergrund sind zu sehen).
     * Falls ein undurchsichtiges Leerzeichen benötigt wird (die Hintergrundfarbe ist zu sehen), kann statt dessen das
     * Semikolon ";" verwendet werden. Diese Methode erlaubt es, die Voreinstellung zu ändern und ein anderes Zeichen zu
     * wählen.
     *
     * @param nonTransparentSpace Das neue undurchsichtige Leerzeichen.
     */
    public void setNonTransparentSpace(char nonTransparentSpace) {
        canvas.setNonTransparentSpace(nonTransparentSpace);
    }

    /**
     * Hier kann die Farb-Assoziation für die Methode <code> addColorStringToCanvas() </code> festgelegt werden. Als
     * Standard sind folgede Farben definiert:
     * <pre>
     * <code>
     * HashMap<Character, Color> colormap = new HashMap<>();
     * colormap.put('R', Color.RED);
     * colormap.put('r', Color.RED.brighter());
     * colormap.put('G', Color.GREEN);
     * colormap.put('g', Color.GREEN.brighter());
     * colormap.put('B', Color.BLUE);
     * colormap.put('b', Color.BLUE.brighter());
     * colormap.put('Y', Color.YELLOW);
     * colormap.put('y', Color.YELLOW.brighter());
     * colormap.put('P', Color.PINK);
     * colormap.put('p', Color.PINK.brighter());
     * colormap.put('C', Color.CYAN);
     * colormap.put('c', Color.CYAN.brighter());
     * colormap.put('M', Color.MAGENTA);
     * colormap.put('m', Color.MAGENTA.brighter());
     * colormap.put('O', Color.ORANGE);
     * colormap.put('o', Color.ORANGE.brighter());
     * colormap.put('W', Color.WHITE);
     * </code>
     * </pre>
     *
     * @param colormap Die Farbzuordnungen.
     */
    public void setColormap(HashMap<Character, Color> colormap) {
        this.colormap = colormap;
        canvas.setBackgroundColormap(colormap);
    }

    /**
     * Liefert alle Tastendrücke (java.awt.event.Keyevent) die seit dem letzten Aufruf dieser Methode aufgelaufen sind
     * als Array zurück. Es werden maximal die neuesten 25 Ereignisse zurückgegeben, alte Ereignisse werden gelöscht.
     * <p>
     * Das Array enthält Ereignisse vom Typ <code>java.awt.event.KeyEvent;</code>. Der Typ des Events ist entweder
     * <code>KeyEvent.KEY_PRESSED</code> (Taste wurde gedrückt),
     * <code>KeyEvent.KEY_RELEASED</code> (Taste wurde losgelassen)
     * oder <code>KeyEvent.KEY_TYPED</code>(Taste wurde getippt, dies funktioniert aber nicht für "nicht sichtbare"
     * Zeichen, wie z.B. Pfeil nach links). Sichtbare Zeichen lassen sich mit der Methode <code>getKeyChar()</code>
     * auswerten. Bei Tastendrücken gibt es die sogenannte Anschlagverzögerung und automatische Wiederholungen. Das
     * bedeutet, dass wenn man eine Taste gedrückt hält, dann wird die Taste einmal ausgelöst. Dann folgt eine kurze
     * Pause, dann folgt eine schnelle Wiederholung des Tastendrucks. Dieses Verhalten kann man in Java umgehen. Dazu
     * können aktuell gedrückte Tasten in einer Liste  gespeichert werden. Losgelassene Tasten werden aus der Liste
     * gelöscht. Somit enthält die Liste alle Tasten, die gerade gedrückt sind.
     *
     * <pre>
     * <code>
     * package keyevents;
     * import java.awt.event.KeyEvent;
     *
     * public class Test {
     *   GameView gameView;
     *
     *   public Test(){
     *     gameView = new GameView(20, 40);
     *   }
     *
     *   public void loop() {
     *     while(true) {
     *       KeyEvent[] keyEvents = gameView.getKeyEvents();
     *       for (int i = 0; i < keyEvents.length; i++) {
     *         KeyEvent keyEvent = keyEvents[i];
     *         if (keyEvent.getID() == KeyEvent.KEY_PRESSED) {
     *           if (keyEvent.getKeyCode() == KeyEvent.VK_ENTER) {
     *             System.out.println("Eingabetaste");
     *           }
     *         } else if (keyEvent.getID() == KeyEvent.KEY_TYPED) {
     *           System.out.println(keyEvent.getKeyChar());
     *         }
     *       }
     *     }
     *   }
     * }
     * </code>
     * </pre>
     *
     * <br>
     *
     * @return Alle <code>KeyEvent</code> Ereignisse seit dem letzten Aufruf dieser Methode.
     */
    public KeyEvent[] getKeyEvents() {
        return keyboard.getKeyEvents();
    }

    /**
     * Diese Methode gibt alle gerade im Moment gedrückten Tasten als KeyCode-Array zurück. Die Tasten sind in der
     * Reihenfolge enthalten, in der sie gedrückt wurden.
     * <p>
     * Ein Abgleich kann über Konstanten der Klasse <code>java.awt.KeyEvent</code> erfolgen.
     * <pre>
     * <code>
     * private void tastenAuswerten() {
     *     Integer[] gedruekteTasten = gameView.getKeyCodesOfCurrentlyPressedKeys();
     *     for (int keyCode : gedruekteTasten) {
     *         if (keyCode == KeyEvent.VK_UP) {
     *             fadenkreuz.up();
     *         } else if (keyCode == KeyEvent.VK_DOWN) {
     *             fadenkreuz.down();
     *         } else if (keyCode == KeyEvent.VK_LEFT) {
     *             fadenkreuz.left();
     *         } else if (keyCode == KeyEvent.VK_RIGHT) {
     *             fadenkreuz.right();
     *         } else if (keyCode == KeyEvent.VK_SPACE) {
     *             schuss();
     *         }
     *     }
     * }
     * </code>
     * </pre>
     *
     * @return Alle gerade gedrückten Tasten als <code>KeyCode</code> Ereignisse.
     */
    public Integer[] getKeyCodesOfCurrentlyPressedKeys() {
        return keyboard.getKeyCodesOfCurrentlyPressedKeys();
    }

    /**
     * Liefert alle Mausereignisse, seit dem letzten Aufruf dieser Methode aufgelaufen sind als Array zurück. Es werden
     * maximal die neuesten 25 Ereignisse zurückgegeben, alte Ereignisse werden gelöscht.
     * <p>
     * Das Array enthält Ereignisse vom Typ <code>java.awt.event.MouseEvent;</code> Das <code>MouseEvent</code> enthält
     * Koordinaten(des Fensters) und die Information ob die Maus gedrückt, losgelassen, gecklickt oder nur bewegt wurde.
     * Um festzustellen, wie die Maus betätigt wurde, kann der Typ des
     * <code>MouseEvent</code> abgefragt werden. Folgende <code>MouseEvent</code>
     * werden weitergeleitet: <br>
     * <code>MouseEvent.MOUSE_PRESSED</code> <br>
     * <code>MouseEvent.MOUSE_RELEASED</code> <br>
     * <code>MouseEvent.MOUSE_CLICKED</code> <br>
     * <code>MouseEvent.MOUSE_MOVED</code> <br>
     * <br>
     * Die Fensterkoordinaten können mit den Methoden<br>
     * <code>getX()</code> = Spalten<br>
     * <code>getY()</code> = Zeilen<br>
     * abgerufen werden, um Zeile und Spalte des Events zu bestimmen.<br>
     * <br>
     * Beispiel zur Erkennung einer geklickten Maustaste:<br>
     *
     * <pre>
     * <code>
     * package mouseevents;
     *
     * import java.awt.event.MouseEvent;
     *
     * public class Test {
     *   GameView gameView;
     *
     *   public Test() {
     *     gameView = new GameView(20, 40);
     *     gameView.useMouse(true);
     *   }
     *
     *   public void loop() {
     *     while (true) {
     *       MouseEvent[] mouseEvents = gameView.getMouseEvents();
     *       for (int i = 0; i < mouseEvents.length; i++) {
     *         MouseEvent mouseEvent = mouseEvents[i];
     *         if (mouseEvent.getID() == MouseEvent.MOUSE_CLICKED) {
     *         System.out.println("Geklickt in Spalte: " + mouseEvent.getX());
     *         }
     *       }
     *     }
     *   }
     * }
     *
     * </code>
     * </pre>
     *
     * @return Alle <code>MouseEvent</code> Ereignisse seit dem letzten Aufruf dieser Methode.
     */
    public MouseEvent[] getMouseEvents() {
        return mouse.getMouseEvents();
    }

    /**
     * Spielt einen Sound ab (z.B. eine wav.-Datei). Das Soundfile muss in einem Verzeichnis "src/resources" liegen.
     * Bitte den Namen des Files ohne Verzeichnisnamen angeben, z.B. "Sound.wav". Der Sound beendet sich selbst, sobald
     * er fertig abgespielt wurde. Der Parameter "replay" kann genutzt werden um den Sound endlos zu wiederholen. Mit
     * der Methode <code>stopSound(int number)</code> kann ein Sound frühzeitig beendet werden. Mit der Methode
     * <code>stopAllSounds()</code> können alle Sounds beendet werden
     *
     * @param sound  Name des Soundfiles. Das Soundfile muss in einem Verzeichnis "src/resources" liegen. Bitte den
     *               Namen des Files ohne Verzeichnisnamen angeben, z.B. "Sound.wav".
     * @param replay Legt fest, ob der Sound endlos wiederholt werden soll.
     * @return Die eindeutige Nummer des Soundfiles wird zurückgegeben. Dise Nummer kann genutzt werden um mit der
     * Methode <code>stopSound(int number)</code>  das Abspielen des Sounds zu beenden.
     */
    public int playSound(String sound, boolean replay) {
        return this.sound.playSound(sound, replay);
    }

    /**
     * Stoppt den Sound mit der angegebenen Nummer. Falls der Sound schon gestoppt wurde, passiert nichts.
     *
     * @param number Der eindeutige Nummer des Soundfiles, das gestoppt werden soll.
     */
    public void stopSound(int number) {
        sound.stopSound(number);
    }

    /**
     * Stoppt alle gerade spielenden Sounds.
     */
    public void stopAllSounds() {
        sound.stopAllSounds();
    }

    /**
     * Schließt entweder nur das GameView-Fenster oder die ganze Anwendung.
     *
     * @param terminateEverything Wenn hier <code>true</code> ausgewählt wird, wird die komplette Anwendung beendet.
     *                            Ansonsten wird nur das Fenster geschlossen.
     */
    public void closeGameView(boolean terminateEverything) {
        window.closeWindow(terminateEverything);
    }

    private void sleep(int millies) {
        try {
            Thread.sleep(millies);
        } catch (InterruptedException ignored) {
        }
    }

    Point getTextDisplayLocationOnScreen() {
        return window.getTextDisplayLocationOnScreen();
    }

    Dimension getTextDisplaySize() {
        return window.getTextDisplaySize();
    }

    private static class StringHelper {

        static String convertCharArrayToString(char[][] chars) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < chars.length; i++) {
                for (int j = 0; j < chars[i].length; j++) {
                    sb.append(chars[i][j]);
                }
                if (i < chars.length - 1) {
                    sb.append("\n");
                }
            }
            return sb.toString();
        }

        static char[][] convertStringToCharArray(String string) {
            char[] chars = string.toCharArray();
            int spalten = 0;
            int zeilen = 1;
            int counter = 0;
            for (char aChar : chars) {
                if (aChar == '\n') {
                    zeilen++;
                    if (counter > spalten) {
                        spalten = counter;
                    }
                    counter = 0;
                } else {
                    if (aChar != '\r') {
                        counter++;
                    }
                }
            }
            if (counter > spalten) {
                spalten = counter;
            }
            int zeile = 0;
            int spalte = 0;
            char[][] ca = new char[zeilen][spalten];
            for (char aChar : chars) {
                if (aChar != '\n') {
                    if (aChar != '\r') {
                        ca[zeile][spalte++] = aChar;
                    }
                } else {
                    while (spalte < spalten) {
                        ca[zeile][spalte++] = ' ';
                    }
                    spalte = 0;
                    zeile++;
                }
            }
            return ca;
        }
    }

    private static class Canvas implements Cloneable {
        private static Color defaultFontColor = Color.white;
        private static Color defaultBackgroundColor = Color.black;
        private static char nonTransparentSpace = ';';

        private int lines;
        private int columns;
        private char[][] characters;
        private Color[][] fontColor;
        private Color[][] backgroundColor;

        private HashMap<Character, Color> backgroundColormap;

        @Override
        protected Canvas clone() throws CloneNotSupportedException {
            Canvas clone = (Canvas) super.clone();
            clone.backgroundColormap = new HashMap<>(this.backgroundColormap);
            clone.characters = new char[lines][columns];
            clone.fontColor = new Color[lines][columns];
            clone.backgroundColor = new Color[lines][columns];
            for (int zeile = 0; zeile < clone.lines; zeile++) {
                for (int spalte = 0; spalte < clone.columns; spalte++) {
                    clone.characters[zeile][spalte] = this.characters[zeile][spalte];
                    clone.fontColor[zeile][spalte] = this.fontColor[zeile][spalte];
                    clone.backgroundColor[zeile][spalte] = this.backgroundColor[zeile][spalte];
                }
            }
            return clone;
        }

        Canvas(int lines, int columns, HashMap<Character, Color> backgroundColormap) {
            this.lines = lines;
            this.columns = columns;
            this.backgroundColormap = backgroundColormap;
            this.characters = new char[lines][columns];
            this.fontColor = new Color[lines][columns];
            this.backgroundColor = new Color[lines][columns];
            clearCanvas();
        }

        void setDefaultBackgroundColor(Color defaultBackgroundColor) {
            Canvas.defaultBackgroundColor = defaultBackgroundColor;
        }

        void setDefaultFontColor(Color defaultFontColor) {
            Canvas.defaultFontColor = defaultFontColor;
        }

        void setBackgroundColormap(HashMap<Character, Color> backgroundColormap) {
            this.backgroundColormap = backgroundColormap;
        }

        int getLines() {
            return lines;
        }

        int getColumns() {
            return columns;
        }

        char getCharacter(int zeile, int spalte) {
            return characters[zeile][spalte];
        }

        Color getFontColor(int zeile, int spalte) {
            return fontColor[zeile][spalte];
        }

        Color getBackgroundColor(int zeile, int spalte) {
            return backgroundColor[zeile][spalte];
        }

        char getNonTransparentSpace() {
            return nonTransparentSpace;
        }

        void setNonTransparentSpace(char nonTransparentSpace) {
            Canvas.nonTransparentSpace = nonTransparentSpace;
        }

        char[][] getCharacters() {
            return characters;
        }

        void fillCanvas(char character, Color fontColor) {
            for (int line = 0; line < lines; line++) {
                for (int column = 0; column < columns; column++) {
                    this.characters[line][column] = character;
                    if (fontColor != null) {
                        this.fontColor[line][column] = fontColor;
                    }
                }
            }
        }

        void fillCanvas(Color backgroundColor) {
            for (int line = 0; line < lines; line++) {
                for (int column = 0; column < columns; column++) {
                    this.characters[line][column] = ' ';
                    this.backgroundColor[line][column] = backgroundColor;
                }
            }
        }

        void clearCanvas() {
            for (int zeile = 0; zeile < lines; zeile++) {
                for (int spalte = 0; spalte < columns; spalte++) {
                    characters[zeile][spalte] = ' ';
                    fontColor[zeile][spalte] = defaultFontColor;
                    backgroundColor[zeile][spalte] = defaultBackgroundColor;
                }
            }
        }

        void addToCanvas(String string, int startLine, int startColumn, Color fontColor, boolean colorString) {
            char[] picture = string.toCharArray();
            int column = startColumn;
            int line = startLine;
            for (char c : picture) {
                if (c == '\n') {
                    line++; // next Line
                    column = startColumn;
                } else if (c != '\r') {
                    // nur schreiben, wenn Koorinaten erfüllt sind.
                    if (line >= 0 && column >= 0 && line < getLines() && column < getColumns()) {
                        checkAndAddCharacter(c, line, column, fontColor, backgroundColormap.get(c), colorString);
                    }
                    column++;
                }
            }
        }

        private void checkAndAddCharacter(char character, int line, int column, Color fontColor,
                                          Color backgroundColor, boolean colorString) {
            if (character != ' ') { // tansparente Zeichen nicht drucken
                if (character == nonTransparentSpace) {
                    this.characters[line][column] = ' ';
                    this.backgroundColor[line][column] = defaultBackgroundColor;
                } else {
                    if (colorString) {
                        this.characters[line][column] = ' ';
                        this.backgroundColor[line][column] = backgroundColor;
                    } else {
                        this.characters[line][column] = character;
                        this.fontColor[line][column] = (fontColor != null) ? fontColor : defaultFontColor;
                    }
                }
            }
        }
    }

    private static class Frame extends JFrame {

        // Klassen
        private Window window;
        private Mouse mouse;
        private Keyboard keyboard;

        private JPanel statusBar;
        private JLabel statusLabelLinks;

        void registerListeners(Window window, Mouse mouse, Keyboard keyboard) {
            this.window = window;
            this.mouse = mouse;
            this.keyboard = keyboard;
        }

        Frame(int lines, int columns, int windowSize, TextPanel textPanel) {

            // Struktur
            Box boxAroundTextPanel = new Box(BoxLayout.Y_AXIS);
            boxAroundTextPanel.setAlignmentX(JComponent.CENTER_ALIGNMENT);
            boxAroundTextPanel.add(Box.createVerticalGlue());
            boxAroundTextPanel.add(textPanel);
            boxAroundTextPanel.add(Box.createVerticalGlue());
            boxAroundTextPanel.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));

            statusBar = new JPanel() {
                {
                    setLayout(new BorderLayout());
                    setBorder(BorderFactory.createRaisedBevelBorder());
                    setBackground(Color.WHITE);
                    setForeground(Color.BLACK);
                    statusLabelLinks = new JLabel();
                    statusLabelLinks.setBackground(Color.WHITE);
                    statusLabelLinks.setForeground(Color.BLACK);
                    statusLabelLinks.setHorizontalAlignment(JLabel.LEFT);

                    JLabel statusLabelRechts = new JLabel(Version.getStatusSignature());
                    statusLabelRechts.setBackground(Color.WHITE);
                    statusLabelRechts.setForeground(Color.BLACK);
                    statusLabelRechts.setHorizontalAlignment(JLabel.RIGHT);
                    add(statusLabelLinks, BorderLayout.WEST);
                    add(statusLabelRechts, BorderLayout.EAST);
                }
            };

            JPanel textPanelAndStatusBar = new JPanel(new BorderLayout());
            textPanelAndStatusBar.setBackground(Color.BLACK);
            textPanelAndStatusBar.add(boxAroundTextPanel, BorderLayout.CENTER);
            textPanelAndStatusBar.add(statusBar, BorderLayout.SOUTH);
            add(textPanelAndStatusBar);

            // Eigenschaften
            setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            setTitle(Version.getSTANDARDTITLE());
            textPanel.requestFocus();
            setResizable(true);

            // Listeners
            addComponentListener(new ComponentAdapter() {
                public void componentResized(ComponentEvent componentEvent) {
                    if (window != null) {
                        window.update();
                    }
                }
            });
            addKeyListener(new KeyListener() {

                @Override
                public void keyTyped(KeyEvent keyEvent) {
                    all(keyEvent);
                }

                @Override
                public void keyReleased(KeyEvent keyEvent) {
                    all(keyEvent);
                }

                @Override
                public void keyPressed(KeyEvent keyEvent) {
                    all(keyEvent);
                }

                private void all(KeyEvent keyEvent) {
                    if (keyboard != null) {
                        keyboard.update(keyEvent);
                    }
                }
            });
            MouseAdapter mouseAdapter = new MouseAdapter() {
                @Override
                public void mouseReleased(MouseEvent mouseEvent) {
                    all(mouseEvent);
                }

                @Override
                public void mousePressed(MouseEvent mouseEvent) {
                    all(mouseEvent);
                }

                @Override
                public void mouseMoved(MouseEvent mouseEvent) {
                    all(mouseEvent);
                }

                @Override
                public void mouseClicked(MouseEvent mouseEvent) {
                    all(mouseEvent);
                }

                private void all(MouseEvent mouseEvent) {
                    if (mouse != null) {
                        mouse.update(mouseEvent);
                    }
                }
            };
            textPanel.addMouseMotionListener(mouseAdapter);
            textPanel.addMouseListener(mouseAdapter);

            // Größe des Fensters
            setMinimumSize(new Dimension(450, 300));
            pack();
            if (windowSize == WINDOWSIZE_MAXIMIZED) {
                setExtendedState(JFrame.MAXIMIZED_BOTH);
            } else {
                int width = Toolkit.getDefaultToolkit().getScreenSize().width;
                int height = Toolkit.getDefaultToolkit().getScreenSize().height;
                if (windowSize == WINDOWSIZE_NORMAL) {
                    width = (int) (width / 3d * 2d);
                    height = (int) (height / 3d * 2d);
                } else if (windowSize == WINDOWSIZE_LARGE) {
                    width = width - 100;
                    height = height - 100;
                } else if (windowSize == WINDOWSIZE_SMALL) {
                    width = (int) (width / 3.5d * 2d);
                    height = (int) (height / 3.5d * 2d);
                }
                setSize(new Dimension(width, height));
            }
            // Warten bis Änderungen übernommen sind.
            try {
                Thread.sleep(100);
            } catch (InterruptedException ignored) {
            }
            textPanel.setFontsizeThatFitsPanelAndUpdatePanelDimensions(lines, columns);
            if (windowSize != WINDOWSIZE_MAXIMIZED) {
                pack();
            }

            // Location und Ausgeben
            setLocationRelativeTo(null);
            setVisible(true);
        }

        JLabel getStatusLabelLinks() {
            return statusLabelLinks;
        }

        JPanel getStatusBar() {
            return statusBar;
        }
    }

    private static class Keyboard {
        private SwingAdapter swingAdapter;
        private ArrayBlockingQueue<KeyEvent> keyboardEvents;
        private ArrayBlockingQueue<Integer> keyCodesOfCurrentlyPressedKeys;

        private final static int KEY_EVENT_BUFFER_SIZE = 25;

        Keyboard(SwingAdapter swingAdapter) {
            this.swingAdapter = swingAdapter;
            keyboardEvents = new ArrayBlockingQueue<>(KEY_EVENT_BUFFER_SIZE);
            keyCodesOfCurrentlyPressedKeys = new ArrayBlockingQueue<>(10);
        }

        void update(KeyEvent keyEvent) {
            int code = keyEvent.getKeyCode();
            if (KeyEvent.VK_ESCAPE == code) {
                swingAdapter.closeWindow(true);
            }
            if (keyboardEvents.size() == KEY_EVENT_BUFFER_SIZE) {
                keyboardEvents.remove();
            }
            keyboardEvents.add(keyEvent);

            if (keyEvent.getID() == KeyEvent.KEY_PRESSED) {
                if (!keyCodesOfCurrentlyPressedKeys.contains(keyEvent.getKeyCode()))
                    keyCodesOfCurrentlyPressedKeys.add(keyEvent.getKeyCode());
            } else if (keyEvent.getID() == KeyEvent.KEY_RELEASED) {
                keyCodesOfCurrentlyPressedKeys.remove((Integer) keyEvent.getKeyCode());
            }
        }

        KeyEvent[] getKeyEvents() {
            KeyEvent[] events = keyboardEvents.toArray(new KeyEvent[0]);
            keyboardEvents.clear();
            return events;
        }

        Integer[] getKeyCodesOfCurrentlyPressedKeys() {
            return keyCodesOfCurrentlyPressedKeys.toArray(new Integer[0]);
        }
    }

    private static class Mouse {
        private SwingAdapter swingAdapter;

        private boolean invisibleMouseCursor;
        private Timer invisibleMouseTimer;
        private boolean invisibleMouseCursorMoved;
        private TimerTask invisibleMouseTimerTask;

        private final static int MOUSE_EVENT_BUFFER_SIZE = 25;
        private ArrayBlockingQueue<MouseEvent> mousePointerEvents;

        private boolean useMouse;

        Mouse(SwingAdapter swingAdapter) {
            this.swingAdapter = swingAdapter;

            this.invisibleMouseCursor = false;
            this.invisibleMouseCursorMoved = true;
            this.invisibleMouseTimerTask = new TimerTask() {
                @Override
                public void run() {
                    if (invisibleMouseCursorMoved) {
                        if (invisibleMouseCursor) {
                            setStandardMouseCursor();
                        }
                        invisibleMouseCursorMoved = false;
                    } else {
                        if (!invisibleMouseCursor) {
                            setInvisibleMouseCursor();
                        }
                    }
                }
            };
            this.invisibleMouseTimer = new Timer(true);

            this.mousePointerEvents = new ArrayBlockingQueue<>(MOUSE_EVENT_BUFFER_SIZE);

            useMouse(false);
        }

        void useMouse(boolean useMouse) {
            this.useMouse = useMouse;
            if (useMouse) {
                setStandardMouseCursor();
                invisibleMouseTimer.cancel();
            } else {
                setInvisibleMouseCursor();
                invisibleMouseTimer.cancel();
                invisibleMouseTimer = new Timer(true);
                invisibleMouseTimer.schedule(invisibleMouseTimerTask, 500, 500);
            }
        }

        void setStandardMouseCursor() {
            this.invisibleMouseCursor = false;
            swingAdapter.setStandardMouseCursor();
        }

        void setMouseCursor(String cursorImageFile, boolean centered) {
            this.invisibleMouseCursor = false;
            swingAdapter.setMouseCursor(cursorImageFile, centered);
        }

        private void setInvisibleMouseCursor() {
            invisibleMouseCursor = true;
            swingAdapter.setInvisibleMouseCursor();
        }

        void update(MouseEvent mouseEvent) {
            if (useMouse) {
                int mouseEventLine = Math.max(0,
                        swingAdapter.getLines() * mouseEvent.getY() / swingAdapter.getTextDisplaySize().height);
                mouseEventLine = Math.min(swingAdapter.getLines() - 1, mouseEventLine);
                int mouseEventColumn = Math.max(0,
                        swingAdapter.getColumns() * mouseEvent.getX() / swingAdapter.getTextDisplaySize().width);
                mouseEventColumn = Math.min(swingAdapter.getColumns() - 1, mouseEventColumn);
                MouseEvent fixedMouseEvent = new MouseEvent(mouseEvent.getComponent(), mouseEvent.getID(),
                        mouseEvent.getWhen(), mouseEvent.getModifiersEx(), mouseEventColumn, mouseEventLine,
                        mouseEvent.getClickCount(), mouseEvent.isPopupTrigger(), mouseEvent.getButton());
                if (mousePointerEvents.size() == MOUSE_EVENT_BUFFER_SIZE) {
                    mousePointerEvents.remove();
                }
                mousePointerEvents.add(fixedMouseEvent);
            } else {
                invisibleMouseCursorMoved = true;
            }
        }

        MouseEvent[] getMouseEvents() {
            MouseEvent[] events = mousePointerEvents.toArray(new MouseEvent[0]);
            mousePointerEvents.clear();
            return events;
        }
    }

    private static class Sound {

        private ConcurrentHashMap<Integer, Clip> clips;
        private static int soundCounter;

        Sound() {
            this.clips = new ConcurrentHashMap<>();
            soundCounter = 0;
        }

        int playSound(String sound, boolean replay) {
            final int number = ++soundCounter;
            new Thread(() -> {
                try {
                    AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(GameView.class.getResource(
                            "/resources/" + sound));
                    Clip clip = AudioSystem.getClip();
                    clip.open(audioInputStream);
                    clips.put(number, clip);
                    clip.addLineListener(event -> {
                        if (event.getType().equals(LineEvent.Type.STOP)) {
                            event.getLine().close();
                            clips.remove(number);
                        }
                    });
                    if (replay) {
                        clip.loop(Clip.LOOP_CONTINUOUSLY);
                    } else {
                        clip.start();
                    }
                } catch (Exception e) {
                    System.err.println("Soundfile \"" + sound + "\" konnte nicht abgespielt werden!");
                    e.printStackTrace();
                    System.exit(1);
                }
            }).start();
            return number;
        }

        void stopSound(int number) {
            Clip clip = clips.get(number);
            if (clip != null) {
                clip.stop();
            }
        }

        void stopAllSounds() {
            for (Clip clip : this.clips.values()) {
                if (clip != null) {
                    clip.stop();
                }
            }
        }
    }

    private static class SwingAdapter {
        //Swing Klassen
        private TextPanel textPanel;
        private Frame frame;
        private Sound sound;

        // Variablen
        private int lines;
        private int columns;
        private boolean terminated;

        void registerListeners(Window window, Mouse mouse, Keyboard keyboard, Sound sound) {
            frame.registerListeners(window, mouse, keyboard);
            this.sound = sound;
        }

        SwingAdapter(int lines, int columns, int windowSize) {
            this.lines = lines;
            this.columns = columns;
            this.textPanel = new TextPanel();
            this.frame = new Frame(lines, columns, windowSize, textPanel);
            this.terminated = false;
            javax.swing.Timer sixtyFramesPerSecond = new javax.swing.Timer(15, new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (!textPanel.painting) {
                        textPanel.repaint();
                    }
                    if (terminated) {
                        ((javax.swing.Timer) (e.getSource())).stop();
                    }
                }
            });
            sixtyFramesPerSecond.start();
        }

        // Getter
        int getLines() {
            return lines;
        }

        int getColumns() {
            return columns;
        }

        Point getTextDisplayLocationOnScreen() {
            return textPanel.getLocationOnScreen();
        }

        Dimension getTextDisplaySize() {
            return textPanel.getSize();
        }

        // Anzeige
        void setStatusText(String statusText) {
            SwingUtilities.invokeLater(() -> {
                frame.getStatusLabelLinks().setText(statusText);
                int minWidth = frame.getStatusBar().getPreferredSize().width + 50;
                frame.setMinimumSize(new Dimension(minWidth, minWidth / 16 * 9));
            });
        }

        void printToDisplay(Canvas printCanvas) {
            this.lines = printCanvas.getLines();
            this.columns = printCanvas.getColumns();
            textPanel.canvasFromWindow = printCanvas;
            textPanel.canvasConsumed = false;
        }

        void windowSizeChanged() {
            textPanel.windowResized = true;
        }

        // Fenster-Dekorationen
        void setTitle(String title) {
            frame.setTitle(title);
        }

        void setWindowIcon(String windowIcon) {
            Image fensterSymbol = null;
            try {
                fensterSymbol = new ImageIcon(GameView.class.getResource("/resources/" + windowIcon)).getImage();
            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("Symbolfile \"" + windowIcon + "\" konnte nicht gefunden werden!");
            }
            frame.setIconImage(fensterSymbol);
        }

        // Maus Cursor
        void setMouseCursor(String cursor, boolean centered) {
            Image im = null;
            try {
                im = new ImageIcon(GameView.class.getResource("/resources/" + cursor)).getImage();
            } catch (Exception e) {
                System.out.println("Cursorfile konnte nicht gefunden werden!");
                System.exit(1);
            }
            textPanel.setCursor(createCursor(im, centered));
        }

        private Cursor createCursor(Image im, boolean centered) {
            Toolkit toolkit = textPanel.getToolkit();
            Dimension cursorSize = Toolkit.getDefaultToolkit().getBestCursorSize(64, 64);
            Point cursorHotSpot = new Point(0, 0);
            if (centered) {
                cursorHotSpot = new Point(cursorSize.width / 2, cursorSize.height / 2);
            }
            return toolkit.createCustomCursor(im, cursorHotSpot, "Cross");
        }

        void setStandardMouseCursor() {
            textPanel.setCursor(Cursor.getDefaultCursor());
        }

        void setInvisibleMouseCursor() {
            Image im = new ImageIcon("").getImage();
            textPanel.setCursor(createCursor(im, false));
        }

        // Beenden
        void closeWindow(boolean terminateEverything) {
            terminated = true;
            frame.dispose();
            sound.stopAllSounds();
            if (terminateEverything) {
                System.exit(0);
            }
        }
    }

    private static class TextPanel extends JPanel {
        private BufferedImage image;
        private Graphics2D g2;
        private Canvas lastPaintedCanvas;
        private boolean structureIsNew;

        volatile Canvas canvasFromWindow;
        volatile boolean canvasConsumed;
        volatile boolean windowResized;
        volatile boolean painting;

        private int lineHeight;
        private int charWidth;
        private int height;
        private int width;
        private Map<TextAttribute, Object> fontMap;
        private Font font;

        // Variablen zur Optimierung nicht lokal
        private int xForBoth;
        private int yForRect;
        private int yForString;
        private Canvas canvasToPaint;

        TextPanel() {
            setBackground(Color.BLACK);
            setForeground(Color.WHITE);
            fontMap = new HashMap<>();
            fontMap.put(TextAttribute.FAMILY, "Monospaced");
            fontMap.put(TextAttribute.WIDTH, 2.25);
            this.font = new Font(fontMap);
            this.canvasConsumed = true;
            this.painting = false;
            setFont(font);
        }

        @Override
        protected void paintComponent(Graphics g) {
            this.painting = true;
            if (!canvasConsumed) {
                canvasToPaint = this.canvasFromWindow;
                this.canvasConsumed = true;
                if (canvasStructureChanged(canvasToPaint, lastPaintedCanvas) || image == null || windowResized) {
                    setFontsizeThatFitsPanelAndUpdatePanelDimensions(canvasToPaint.getLines(),
                            canvasToPaint.getColumns());
                    // Das ganze Bild soll jetzt neu gezeichnet werden
                    if (g2 != null) {
                        g2.dispose();
                    }
                    image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
                    g2 = image.createGraphics();
                    g2.setFont(font);
                    this.structureIsNew = true;
                    this.windowResized = false;
                } else {
                    this.structureIsNew = false;
                }
                for (int i = 0; i < canvasToPaint.getLines(); i++) {
                    for (int j = 0; j < canvasToPaint.getColumns(); j++) {
                        // Optimierung: Es muss zusätzlich j + 1 und i + 1 neu geschrieben werden
                        if (structureIsNew || isDifferent(canvasToPaint, lastPaintedCanvas, i, j) || (j > 0 && isDifferent(canvasToPaint, lastPaintedCanvas, i, j - 1)) || (i > 0 && isDifferent(canvasToPaint, lastPaintedCanvas, i - 1, j))) {
                            yForRect = i * lineHeight;
                            xForBoth = j * charWidth;
                            yForString = yForRect + (int) Math.rint(lineHeight * 0.75);
                            g2.setColor(canvasToPaint.getBackgroundColor(i, j));
                            g2.fillRect(xForBoth, yForRect, charWidth, lineHeight);
                            if (canvasToPaint.getCharacter(i, j) != ' ') {
                                g2.setColor(canvasToPaint.getFontColor(i, j));
                                g2.drawChars(canvasToPaint.getCharacters()[i], j, 1, xForBoth, yForString);
                            }
                        }
                    }
                }
                lastPaintedCanvas = canvasToPaint;
            }
            super.paintComponent(g);
            g.drawImage(image, 0, 0, null);
            g.dispose();
            this.painting = false;
        }

        private boolean canvasStructureChanged(Canvas currentCanvas, Canvas lastCanvas) {
            return lastCanvas == null || currentCanvas.getLines() != lastCanvas.getLines() || currentCanvas.getColumns() != lastCanvas.getColumns();
        }

        private boolean isDifferent(Canvas currentCanvas, Canvas lastCanvas, int zeile, int spalte) {
            return currentCanvas.getCharacter(zeile, spalte) != lastCanvas.getCharacter(zeile, spalte) || currentCanvas.getFontColor(zeile, spalte) != lastCanvas.getFontColor(zeile, spalte) || currentCanvas.getBackgroundColor(zeile, spalte) != lastCanvas.getBackgroundColor(zeile, spalte);
        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(width, height);
        }

        @Override
        public Dimension getSize() {
            return getPreferredSize();
        }

        @Override
        public Dimension getMaximumSize() {
            return getPreferredSize();
        }

        private void setPanelAndFontDimensionsForFontsize(int fontsize, int lines, int columns) {
            fontMap.put(TextAttribute.SIZE, fontsize);
            font = new Font(fontMap);
            setFont(font);
            Rectangle2D bounds = getFontMetrics(font).getStringBounds("Q", 0, 1, getGraphics());
            this.lineHeight = (int) (bounds.getHeight());
            this.charWidth = (int) bounds.getWidth();
            this.height = lines * lineHeight;
            this.width = charWidth * columns;
        }

        void setFontsizeThatFitsPanelAndUpdatePanelDimensions(int lines, int columns) {
            int fontsize = 1;
            int step = 256;
            while (true) {
                setPanelAndFontDimensionsForFontsize(fontsize + step, lines, columns);
                if (width < getParent().getWidth() && height < getParent().getHeight()) {
                    fontsize = fontsize + step;
                } else {
                    if (step == 1) {
                        // fontsize passt jetzt
                        break;
                    }
                }
                step = Math.max(step / 2, 1);
            }
            setPanelAndFontDimensionsForFontsize(fontsize, lines, columns);
        }
    }

    private static class Window {

        private Canvas printCanvas;
        private SwingAdapter swingAdapter;
        private long lastTime;
        private int countPrintedFrames;

        Window(SwingAdapter swingAdapter) {
            this.swingAdapter = swingAdapter;
        }

        void printCanvas(Canvas canvas) {
            updatePrintsPerSecond();
            try {
                printCanvas = canvas.clone();
            } catch (CloneNotSupportedException ignored) {
            }
            swingAdapter.printToDisplay(printCanvas);
        }

        private void updatePrintsPerSecond() {
            countPrintedFrames++;
            long now = System.currentTimeMillis();
            if (now - lastTime > 1000) {
                if (countPrintedFrames > 200) {
                    System.out.println("Zu viele Prints pro Sekunde: " + countPrintedFrames + " PPS");
                }
                countPrintedFrames = 0;
                lastTime = now;
            }
        }

        void setStatusText(String statusText) {
            swingAdapter.setStatusText(statusText);
        }

        void setWindowIcon(String windowIcon) {
            swingAdapter.setWindowIcon(windowIcon);
        }

        void setTitle(String title) {
            swingAdapter.setTitle(title);
        }

        void closeWindow(boolean terminateEverything) {
            swingAdapter.closeWindow(terminateEverything);
        }

        Point getTextDisplayLocationOnScreen() {
            return swingAdapter.getTextDisplayLocationOnScreen();
        }

        Dimension getTextDisplaySize() {
            return swingAdapter.getTextDisplaySize();
        }

        void update() {
            if (printCanvas != null) {
                swingAdapter.windowSizeChanged();
                swingAdapter.printToDisplay(printCanvas);
            }
        }
    }
}
