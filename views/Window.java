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

    private final JPanel contentPane;
    private final JPanel titleBar;
    
    private int mouseX, mouseY;
    private boolean isResizing = false;
    private int resizeDirection = 0;
    private static final int DRAG_MARGIN = 5;

    // Main constructor for the window
    public Window() {
        // Remove the default window decorations provided by the OS
        setUndecorated(true);
        setBackground(new Color(0, 0, 0, 0)); // Transparent background
        
        // Use a content pane with a transparent background
        contentPane = new JPanel(new BorderLayout());
        contentPane.setBackground(new Color(240, 240, 240));
        add(contentPane);
        
        // Create the title bar
        titleBar = newTitleBar();
        contentPane.add(titleBar, BorderLayout.NORTH);
        
        // Add listeners for dragging and resizing
        addListeners();

        // Main content area
        JPanel mainContent = new JPanel();
        mainContent.setPreferredSize(new Dimension(800, 600));
        contentPane.add(mainContent, BorderLayout.CENTER);
        
        // Pack and set visible
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private JPanel newTitleBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(new Color(60, 60, 60)); // Dark grey title bar
        
        // Title label
        JLabel titleLabel = new JLabel(" WORMVOLUTION");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        bar.add(titleLabel, BorderLayout.WEST);

        // Menu Bar
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBorderPainted(false);
        menuBar.setBackground(new Color(60, 60, 60));
        
        // File menu
        JMenu fileMenu = new JMenu("File");
        fileMenu.setForeground(Color.WHITE);
        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(e -> dispose());
        fileMenu.add(exitItem);
        menuBar.add(fileMenu);

        // View menu
        JMenu viewMenu = new JMenu("View");
        viewMenu.setForeground(Color.WHITE);
        JMenuItem colorItem = new JMenuItem("Change Title Bar Color...");
        colorItem.addActionListener(e -> {
            Color newColor = JColorChooser.showDialog(this, "Choose a Title Bar Color", titleBar.getBackground());
            if (newColor != null) {
                titleBar.setBackground(newColor);
                menuBar.setBackground(newColor);
            }
        });
        viewMenu.add(colorItem);
        menuBar.add(viewMenu);
        
        bar.add(menuBar, BorderLayout.CENTER);

        // Control buttons (minimize, maximize, close)
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        controlPanel.setOpaque(false);
        
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
        close.setBackground(new Color(255, 60, 60)); // Red close button
        
        controlPanel.add(minimize);
        controlPanel.add(maximize);
        controlPanel.add(close);
        
        bar.add(controlPanel, BorderLayout.EAST);

        return bar;
    }

    private JButton createControlButton(String text, ActionListener action) {
        JButton button = new JButton(text);
        button.setFont(new Font("Dialog", Font.BOLD, 14));
        button.setFocusable(false);
        button.setPreferredSize(new Dimension(60, 25));
        button.setBorderPainted(false);
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(80, 80, 80));
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(100, 100, 100));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(new Color(80, 80, 80));
                // Special handling for close button
                if (text.equals("X")) {
                    button.setBackground(new Color(255, 60, 60));
                }
            }
        });
        button.addActionListener(action);
        return button;
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
