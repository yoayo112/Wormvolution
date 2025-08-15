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

public class MenuBar extends JMenuBar {

    // menus
    public static List<JMenu> menus  = new ArrayList<>();
    public static JMenu fileMenu;
    public static JMenu viewMenu;

    // file menu items
    private static List<JMenuItem> fileMenuItems = new ArrayList<>();
    private static List<JCheckBoxMenuItem> fileMenuCheckboxes = new ArrayList<>();

    // view menu items
    private static List<JMenuItem> viewMenuItems = new ArrayList<>();
    private static List<JCheckBoxMenuItem> viewMenuCheckboxes = new ArrayList<>();

    // local references
    private static Theme theme;
    private static Window window;

    // constructor requiring local references.
    public MenuBar(Theme mainTheme, Window mainWindow) {
        // set basics
        super();
        theme = mainTheme;
        window = mainWindow;
        setBorderPainted(false);
        setBackground(theme.color_titlebar);

        // add file menu
        fileMenu = populateFileMenu();
        menus.add(fileMenu);
        add(fileMenu);

        // View menu
        viewMenu = populateViewMenu();
        menus.add(viewMenu);
        add(viewMenu);
        
    }

    // this is where we can define new file-menu items --NOTE: factor behavior elsewhere. 
    private static JMenu populateFileMenu() {
        // basic
        JMenu file = new JMenu("File");
        file.setForeground(theme.color_text);
        file.setBackground(theme.color_background);

        // add components
        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(e -> window.dispose());
        file.add(exitItem);

        //dont be afraid to add seperators and checkbox items

        //store references to components
        fileMenuItems.add(exitItem);

        //return menu
        return file;
    }
    
    // this is where we can define new view-menu items --NOTE: factor behavior elsewhere. 
    private static JMenu populateViewMenu() {
        // basic
        JMenu view = new JMenu("View");
        view.setForeground(theme.color_text);
        view.setBackground(theme.color_background);

        // dont be afraid to add non-checkbox items and seperators

        // add checkbox components
        JCheckBoxMenuItem darkmode = new JCheckBoxMenuItem("Dark Theme");
        darkmode.setSelected(true);
        darkmode.addActionListener(e -> { window.toggleDarkmode(darkmode.isSelected());});
        view.add(darkmode);

        // store references to items.
        viewMenuCheckboxes.add(darkmode);

        return view;
    }

}