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

public class Window extends JFrame {

    //panels and frames
    private final JPanel contentPane;
    private final JPanel titleBar;
    private final JPanel mainContent;

    //sizes
	private static Dimension screenSize;
	private static Dimension windowSize = new Dimension(1200,1100);
	private static int sizeConstraint;
	private static int boardSize;
    private static double scale = 1;

    //colors
    public static Color[] themeColors = {
        new Color(7, 3, 28),        //primary
        new Color(197, 215, 252),   //secondary
        new Color(34, 46, 56),      //background
        new Color(70, 70, 70),      //titlebar
        new Color(50, 50, 50),      //titlebuttonHover
        new Color(99, 129, 138),    //button
        new Color(240, 240, 240)      //label
        };

    public static Color[] defaultColors = {
        new Color(153, 153, 153),   //control dark
        new Color(255, 255, 255),   //control highlight
        new Color(238, 238, 238),   //background
        new Color(245, 254, 253),   //titlebar
        new Color(225, 226, 230),   //titlebuttonHover
        new Color(204, 204, 204),   //button
        new Color(0, 0, 0)         //text
    };

    public static Color color_primary;
    public static Color color_secondary;
	public static Color color_background;
    public static Color color_titlebar;
    public static Color color_titlebuttonHover;
    public static Color color_button;
    public static Color color_text;

    public static void setDefaultColors(){
        int i = 0;
        color_primary = defaultColors[i];
        i++;
        color_secondary = defaultColors[i];
        i++;
        color_background = defaultColors[i];
        i++;
        color_titlebar = defaultColors[i];
        i++;
        color_titlebuttonHover = defaultColors[i];
        i++;
        color_button = defaultColors[i];
        i++;
        color_text = defaultColors[i];
    }
    public static void setThemeColors(){
        int i = 0;
        color_primary = themeColors[i];
        i++;
        color_secondary = themeColors[i];
        i++;
        color_background = themeColors[i];
        i++;
        color_titlebar = themeColors[i];
        i++;
        color_titlebuttonHover = themeColors[i];
        i++;
        color_button = themeColors[i];
        i++;
        color_text = themeColors[i];
    }

    // window function
    private int mouseX, mouseY;
    private boolean isResizing = false;
    private int resizeDirection = 0;
    private static final int DRAG_MARGIN = 5;

    // Main constructor for the window
    public Window() {
        // Remove the default window decorations provided by the OS
        setUndecorated(true);

        // set colors
        setDefaultColors();
        setBackground(new Color(0, 0, 0, 0)); // Transparent background
        
        // Use a content pane with a transparent background
        contentPane = new JPanel(new BorderLayout());
        contentPane.setBackground(color_background);
        add(contentPane);
        
        // Create the title bar
        titleBar = newTitleBar();
        contentPane.add(titleBar, BorderLayout.NORTH);
        
        // Add listeners for dragging and resizing
        addListeners();

        // Main content area
        mainContent = new JPanel();
        mainContent.setPreferredSize(new Dimension(800, 600));
        contentPane.add(mainContent, BorderLayout.CENTER);
        
        // Pack and set visible
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    // sub constructor for title bar: contains menus, min, max and x
    private JPanel newTitleBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(color_titlebar); // Dark grey title bar
        
        // Title label
        JLabel titleLabel = new JLabel(" WORMVOLUTION");
        titleLabel.setForeground(color_text);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10)); //pad the side before "file" menu
        bar.add(titleLabel, BorderLayout.WEST);

        // Menu Bar
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBorderPainted(false);
        menuBar.setBackground(color_titlebar);
        
        // File menu
        JMenu fileMenu = new JMenu("File");
        fileMenu.setForeground(color_text);
        fileMenu.setBackground(color_background);
        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(e -> dispose());
        fileMenu.add(exitItem);
        menuBar.add(fileMenu);

        // View menu
        JMenu viewMenu = new JMenu("View");
        viewMenu.setForeground(color_text);
        viewMenu.setBackground(color_background);
        JCheckBoxMenuItem themeSelector = new JCheckBoxMenuItem("Toggle Dark Theme");
        themeSelector.addActionListener(e -> {
            if (themeSelector.isSelected()) {
                setThemeColors();
            }else{
                setDefaultColors();
            }
            retheme();
            revalidate();
            repaint();
        });
        viewMenu.add(themeSelector);
        menuBar.add(viewMenu);
        
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
        close.setBackground(new Color(181, 60, 60)); // Red close button regardless of theme 
        
        basicButtons.add(minimize);
        basicButtons.add(maximize);
        basicButtons.add(close);
        
        bar.add(basicButtons, BorderLayout.EAST);

        return bar;
    }

    // sub - sub constructor for basic buttons inside titlebar
    private JButton createControlButton(String text, ActionListener action) {
        JButton button = new JButton(text);
        button.setFont(new Font("Dialog", Font.BOLD, 14));
        button.setFocusable(false);
        button.setPreferredSize(new Dimension(60, 25));
        button.setBorderPainted(false);
        button.setForeground(color_text);
        button.setBackground(color_titlebar);
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(color_titlebuttonHover);
                if (text.equals("X")) {
                    button.setBackground(new Color(255, 60, 60));
                }
            }
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(color_titlebar);
                // Special handling for close button
                if (text.equals("X")) {
                    button.setBackground(new Color(181, 60, 60));
                }
            }
        });
        button.addActionListener(action);
        return button;
    }

    // systematically repaint all the components.
    private void retheme() {
        // the titlebar.
        titleBar.setBackground(color_titlebar);
        titleBar.setForeground(color_text);

        // the title
        titleBar.getComponent(0).setBackground(color_background);
        titleBar.getComponent(0).setForeground(color_text);

        // menus and basic buttons
        //file menu and items
        JMenuBar mb = (JMenuBar)titleBar.getComponent(1);
        mb.setBackground(color_titlebar);
        mb.setForeground(color_text);
        JMenu menu = mb.getMenu(0); 
        menu.setBackground(color_titlebar);
        menu.setForeground(color_text);
        int numberOfItems = menu.getItemCount();
        for(int i =0; i<numberOfItems; i++) {
            JMenuItem item = menu.getItem(i);
            item.setBackground(color_background);
            item.setForeground(color_text);
        }
        //view menu and items
        menu = mb.getMenu(1); 
        menu.setBackground(color_titlebar);
        menu.setForeground(color_text);
        numberOfItems = menu.getItemCount();
        for(int i =0; i<numberOfItems; i++) {
            JMenuItem item = menu.getItem(i);
            item.setBackground(color_background);
            item.setForeground(color_text);
        }

        //basic buttons. 
        JPanel bb = (JPanel) titleBar.getComponent(2);
        bb.getComponent(0).setBackground(color_titlebar); //minimize
        bb.getComponent(0).setForeground(color_text);
        bb.getComponent(1).setBackground(color_titlebar); //maximize
        bb.getComponent(1).setForeground(color_text);
        // component(3) is exit, leave that alone.

        // Frame Conent
        mainContent.setBackground(color_background);
        mainContent.setForeground(color_text);
    }
    
    // Add mouse listeners to the frame for moving and resizing
    private void addListeners() {
        // Listener for moving the frame
        titleBar.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                mouseX = e.getX();
                mouseY = e.getY();
            }
        });
        
        titleBar.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (getExtendedState() == JFrame.NORMAL) {
                    setLocation(getX() + e.getX() - mouseX, getY() + e.getY() - mouseY);
                }
            }
        });
        
        // Listener for resizing the frame from the edges
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (isResizeable()) {
                    resizeDirection = getResizeDirection(e.getPoint());
                    if (resizeDirection != 0) {
                        isResizing = true;
                        mouseX = e.getXOnScreen();
                        mouseY = e.getYOnScreen();
                    }
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                isResizing = false;
                resizeDirection = 0;
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (isResizing) {
                    resize(e);
                }
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                if (isResizeable() && getExtendedState() == JFrame.NORMAL) {
                    setCursor(getResizeCursor(e.getPoint()));
                } else {
                    setCursor(Cursor.getDefaultCursor());
                }
            }
        });
    }
    
    private int getResizeDirection(Point p) {
        int dir = 0;
        int x = p.x;
        int y = p.y;
        int width = getWidth();
        int height = getHeight();

        // Check corners and edges for resizing
        if (x < DRAG_MARGIN && y < DRAG_MARGIN) {
            dir = Cursor.NW_RESIZE_CURSOR;
        } else if (x > width - DRAG_MARGIN && y < DRAG_MARGIN) {
            dir = Cursor.NE_RESIZE_CURSOR;
        } else if (x < DRAG_MARGIN && y > height - DRAG_MARGIN) {
            dir = Cursor.SW_RESIZE_CURSOR;
        } else if (x > width - DRAG_MARGIN && y > height - DRAG_MARGIN) {
            dir = Cursor.SE_RESIZE_CURSOR;
        } else if (x < DRAG_MARGIN) {
            dir = Cursor.W_RESIZE_CURSOR;
        } else if (x > width - DRAG_MARGIN) {
            dir = Cursor.E_RESIZE_CURSOR;
        } else if (y < DRAG_MARGIN) {
            dir = Cursor.N_RESIZE_CURSOR;
        } else if (y > height - DRAG_MARGIN) {
            dir = Cursor.S_RESIZE_CURSOR;
        }
        return dir;
    }
    
    private Cursor getResizeCursor(Point p) {
        int dir = getResizeDirection(p);
        if (dir != 0) {
            return Cursor.getPredefinedCursor(dir);
        }
        return Cursor.getDefaultCursor();
    }
    
    private void resize(MouseEvent e) {
        Rectangle bounds = getBounds();
        int dx = e.getXOnScreen() - mouseX;
        int dy = e.getYOnScreen() - mouseY;

        int newX = bounds.x;
        int newY = bounds.y;
        int newWidth = bounds.width;
        int newHeight = bounds.height;
        
        switch (resizeDirection) {
            case Cursor.N_RESIZE_CURSOR:
                newY += dy;
                newHeight -= dy;
                break;
            case Cursor.S_RESIZE_CURSOR:
                newHeight += dy;
                break;
            case Cursor.W_RESIZE_CURSOR:
                newX += dx;
                newWidth -= dx;
                break;
            case Cursor.E_RESIZE_CURSOR:
                newWidth += dx;
                break;
            case Cursor.NW_RESIZE_CURSOR:
                newX += dx;
                newY += dy;
                newWidth -= dx;
                newHeight -= dy;
                break;
            case Cursor.NE_RESIZE_CURSOR:
                newY += dy;
                newWidth += dx;
                newHeight -= dy;
                break;
            case Cursor.SW_RESIZE_CURSOR:
                newX += dx;
                newWidth -= dx;
                newHeight += dy;
                break;
            case Cursor.SE_RESIZE_CURSOR:
                newWidth += dx;
                newHeight += dy;
                break;
        }
        
        // Prevent window from getting too small
        if (newWidth > getMinimumSize().width && newHeight > getMinimumSize().height) {
            setBounds(newX, newY, newWidth, newHeight);
            mouseX = e.getXOnScreen();
            mouseY = e.getYOnScreen();
        }
    }
    
    private boolean isResizeable() {
        // This is a simplified check. You may want to add more conditions.
        return true;
    }
}
