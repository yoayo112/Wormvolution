/**
 * an extended/overwritten JMenuBar specifically for all the stuff we will put in the topmost menu bars
 * -- Important -- 
 * Any non-superficial functionality controlled in these menus should be factored out to the simulation controller.
 * 
 * @author Sky Vercauteren 2025
 */

package views;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

import controllers.*;

public class MenuBar extends JMenuBar {

    // menus
    public static List<JMenu> menus  = new ArrayList<>();
    public static JMenu fileMenu;
    public static JMenu viewMenu;

    // file menu items
    public static List<JMenuItem> fileMenuItems = new ArrayList<>();
    public static List<JCheckBoxMenuItem> fileMenuCheckboxes = new ArrayList<>();

    // view menu items
    public static List<JMenuItem> viewMenuItems = new ArrayList<>();
    public static List<JCheckBoxMenuItem> viewMenuCheckboxes = new ArrayList<>();

    // local references
    private static Theme theme;
    private static Window window;
    private static controllers.MainFrame mainFrame;

    // constructor requiring local references.
    public MenuBar(Theme mainTheme, Window mainWindow) {
        // set basics
        super();
        theme = mainTheme;
        window = mainWindow;
        mainFrame = mainWindow.mainFrame();
        setBorderPainted(false);
        setBackground(theme.color_titlebar);

        // add file menu
        fileMenu = mainFrame.populateFileMenu();
        menus.add(fileMenu);
        add(fileMenu);

        // View menu
        viewMenu = mainFrame.populateViewMenu();
        menus.add(viewMenu);
        add(viewMenu);
        
    }
}