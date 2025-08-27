/**
 * A custom window object to be able to:
 * - include dropdown menues
 * - change the theme according to our needs.
 * - change the look and feel
 * avoids asking the OS to build the window by default.
 * 
 * @author Sky Vercauteren 2025.
 */

package views;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;

import controllers.*;

public class Window extends JFrame {

    //panels and frames
    private final JPanel contentPane;
    private final JPanel titleBar;
    public JPanel titleBar(){return titleBar;}
    private final JPanel mainContent;
    public JPanel mainContent() {return mainContent;}
    private static MenuBar menuBar;
    public MenuBar menuBar(){return menuBar;}

    //sizes
	private static Dimension screenSize;
    public static void setScreenSize() {
        // Dynamic screen size -used for window size, board size, and component spacing.
    	GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
    	screenSize = new Dimension(gd.getDisplayMode().getWidth(), gd.getDisplayMode().getHeight());
    }
    public static Dimension getScreenSize(){return screenSize;}
	private static Dimension windowSize = new Dimension(1200,1100);
	private static int sizeConstraint;
    private static boolean portrait;
    private static boolean landscape;
    public static void orient(){
        if(screenSize.width >= screenSize.height){
            landscape = true;
            portrait = false;
        }else{
            landscape = false;
            portrait = true;
        }
    }
    public int getSizeConstraint() {return sizeConstraint;}
    private static double scale = 1;

    //colors
    private static Theme theme;
    public static void setTheme(Theme newTheme) {theme = newTheme;}
    public static Theme getTheme() { return theme;}

    //behavior
    private static MainFrame mainFrame;
    public MainFrame mainFrame(){return mainFrame;}
    public boolean isOpen = true;

    // make sure it triggers isOpen = false on close.
    @Override
    public void dispose() {
        isOpen = false;
        super.dispose();
    }

    // Main constructor for the window
    public Window(Theme mainTheme) {
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        mainFrame = new MainFrame(this);

        // find the smallest dimension, consider padding, and make a square.
        setScreenSize();
        orient();
    	sizeConstraint = (screenSize.width > screenSize.height ? screenSize.height : screenSize.width);
        sizeConstraint = (int)(screenSize.width*0.4);
        sizeConstraint = sizeConstraint - (int)(sizeConstraint * 0.06); // extra padding for visibility
        
        // build initial window frame
        int extra = sizeConstraint/2;
    	windowSize.width = portrait? sizeConstraint: sizeConstraint + extra;
    	windowSize.height = portrait? sizeConstraint + extra: sizeConstraint;

        // Remove the default window decorations provided by the OS
        setUndecorated(true);
        theme = mainTheme;

        // set colors
        if(theme == null) { theme = new Theme();}
        theme.setThemeColors();
        setBackground(new Color(0, 0, 0, 0)); // Transparent background
        
        // Use a content pane with a transparent background
        contentPane = new JPanel(new BorderLayout());
        contentPane.setBackground(theme.color_background);
        add(contentPane);
        
        // Create the title bar
        titleBar = newTitleBar();
        contentPane.add(titleBar, BorderLayout.NORTH);
        
        // Add listeners for dragging and resizing
        mainFrame.addListeners();

        // Main content area
        // This should only ever have two things in it: the world itself, and the control panel.
        mainContent = new JPanel();
        mainContent.setPreferredSize(windowSize);
        mainContent.setBackground(theme.color_background);
        
        // Pack and set visible
        contentPane.add(mainContent, BorderLayout.CENTER);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    // helper to add the board component from MAIN
    public void addBoard(Board board){
        board.setBackground(theme.color_background);
        mainContent.add(board, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    // sub constructor for title bar: contains menus, min, max and x
    private JPanel newTitleBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(theme.color_titlebar); // Dark grey title bar
        
        // Title label
        JLabel titleLabel = new JLabel(" WORMVOLUTION ");
        titleLabel.setForeground(theme.color_text);
        titleLabel.setFont(theme.titleFont);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 10)); //pad the side before "file" menu
        bar.add(titleLabel, BorderLayout.WEST);

        // Menu Bar
        menuBar = new MenuBar(theme, this);
        bar.add(menuBar, BorderLayout.CENTER);

        // Control buttons (minimize, maximize, close)
        JPanel basicButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        basicButtons.setOpaque(false);
        
        // Minimize button
        JButton minimize = createControlButton("-", (e) -> setState(JFrame.ICONIFIED));
        
        // Maximize button (simple toggle)
        JButton maximize = createControlButton("O", (e) -> {
            if (getExtendedState() == JFrame.NORMAL) {
                setExtendedState(JFrame.MAXIMIZED_BOTH);
            } else {
                setExtendedState(JFrame.NORMAL);
            }
        });
        
        // Close button
        JButton close = createControlButton("X", (e) -> dispose());
        close.setPreferredSize(new Dimension(80,25));
        close.setBackground(new Color(181, 60, 60)); // Red close button hardcoded regardless of theme 
        
        basicButtons.add(minimize);
        basicButtons.add(maximize);
        basicButtons.add(close);
        
        bar.add(basicButtons, BorderLayout.EAST);

        return bar;
    }

    // sub - sub constructor for basic buttons inside titlebar (-, o, X)
    private JButton createControlButton(String text, ActionListener action) {
        JButton button = new JButton(text);
        button.setFont(theme.symbolFont);
        button.setFocusable(false);
        button.setPreferredSize(new Dimension(60, 25));
        button.setBorderPainted(false);
        button.setForeground(theme.color_text);
        button.setBackground(theme.color_titlebar);
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(theme.color_titlebuttonHover);
                if (text.equals("X")) {
                    button.setBackground(new Color(255, 60, 60));
                }
            }
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(theme.color_titlebar);
                // Special handling for close button
                if (text.equals("X")) {
                    button.setBackground(new Color(181, 60, 60));
                }
            }
        });
        button.addActionListener(action);
        return button;
    }
    
    public boolean isResizeable() {
        // This is a simplified check. You may want to add more conditions.
        return true;
    }
}
