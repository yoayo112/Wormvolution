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
    private static MenuBar menuBar;

    //sizes
	private static Dimension screenSize;
	private static Dimension windowSize = new Dimension(1200,1100);
	private static int sizeConstraint;
    public int getSizeConstraint() {return sizeConstraint;}
    private static double scale = 1;

    //colors
    private static Theme theme;
    public static void setTheme(Theme newTheme) {theme = newTheme;}
    public static Theme getTheme() { return theme;}

    // window function
    private int mouseX, mouseY;
    private boolean isResizing = false;
    private int resizeDirection = 0;
    private static final int DRAG_MARGIN = 5;
    private boolean isOpen = true;
    public boolean isOpen() { return isOpen; }

    // make sure it triggers isOpen = false on close.
    @Override
    public void dispose() {
        isOpen = false;
        super.dispose();
    }

    // Main constructor for the window
    public Window(Theme mainTheme) {
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        // Dynamic screen size -used for window size, board size, and component spacing.
        // find the smallest dimension, consider padding, and make a square.
    	GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
    	screenSize = new Dimension(gd.getDisplayMode().getWidth(), gd.getDisplayMode().getHeight());
    	sizeConstraint = (screenSize.width > screenSize.height ? screenSize.height : screenSize.width);
        sizeConstraint = sizeConstraint - (int)(sizeConstraint * 0.06); // extra padding for visibility
        
        // build initial window frame
    	windowSize.width = sizeConstraint;
    	windowSize.height = sizeConstraint;

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
        addListeners();

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

    // toggle the dark mode theme on and off.
    public void toggleDarkmode(boolean selected)
    {
        if (selected) {
                theme.setThemeColors();
                theme.mode = "Dark";
            }else{
                theme.setDefaultColors();
                theme.mode = "Default";
            }
            retheme();
            revalidate();
            repaint();
    }

    // systematically repaint all the components.
    private void retheme() {
        // the titlebar.
        titleBar.setBackground(theme.color_titlebar);
        titleBar.setForeground(theme.color_text);

        // the title
        titleBar.getComponent(0).setBackground(theme.color_background);
        titleBar.getComponent(0).setForeground(theme.color_text);

        // menuBar, menus and menu items
        menuBar.setBackground(theme.color_titlebar);
        menuBar.setForeground(theme.color_text);
        for (JMenu m : menuBar.menus)
        {
            m.setBackground(theme.color_titlebar);
            m.setForeground(theme.color_text);
            int numberOfItems = m.getItemCount();
            for(int j =0; j<numberOfItems; j++) {
                JMenuItem item = m.getItem(j);
                item.setBackground(theme.color_background);
                item.setForeground(theme.color_text);
            }
        }

        //basic buttons. 
        JPanel bb = (JPanel) titleBar.getComponent(2);
        bb.getComponent(0).setBackground(theme.color_titlebar); //minimize
        bb.getComponent(0).setForeground(theme.color_text);
        bb.getComponent(1).setBackground(theme.color_titlebar); //maximize
        bb.getComponent(1).setForeground(theme.color_text);
        // component(2) is exit, leave that alone.

        // Frame Content
        mainContent.setBackground(theme.color_background);
        mainContent.setForeground(theme.color_text);
    }
    
    // Add mouse listeners to the frame for moving and resizing
    private void addListeners() {
        // Listener for on close
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                isOpen = false;
            }

            @Override
            public void windowClosed(WindowEvent e) {
                isOpen = false;
            }
        });

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
