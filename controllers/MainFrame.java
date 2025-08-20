/**
 * The GUI controller. to handle behavior related to the main view, frame and window. 
 * Resizing, theme, colors, layout, etc. Mouse listeners.
 * 
 * There is no physical component here. There are no buttons, sliders or anything to paint. This is listeners only. 
 * 
 * @author Sky Vercauteren August 2025
 */

package controllers;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import views.*;

public class MainFrame{
    // the window
    private views.Window window;
    private views.Theme theme;
    private views.MenuBar menuBar;

    // window function
    private int mouseX, mouseY;
    private boolean isResizing = false;
    private int resizeDirection = 0;
    private static final int DRAG_MARGIN = 5;
    private boolean isOpen = true;
    public boolean isOpen() { return isOpen; }

    public MainFrame(views.Window w){
        window = w;
        theme = w.getTheme();
        menuBar = w.menuBar();
    }

    /**
     * Window Helpers
     */
        // systematically repaint all the components.
    private void retheme() {
        // the titlebar.
        window.titleBar().setBackground(theme.color_titlebar);
        window.titleBar().setForeground(theme.color_text);

        // the title
        window.titleBar().getComponent(0).setBackground(theme.color_background);
        window.titleBar().getComponent(0).setForeground(theme.color_text);

        // menuBar, menus and menu items
        window.menuBar().setBackground(theme.color_titlebar);
        window.menuBar().setForeground(theme.color_text);
        for (JMenu m : window.menuBar().menus)
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
        JPanel bb = (JPanel) window.titleBar().getComponent(2);
        bb.getComponent(0).setBackground(theme.color_titlebar); //minimize
        bb.getComponent(0).setForeground(theme.color_text);
        bb.getComponent(1).setBackground(theme.color_titlebar); //maximize
        bb.getComponent(1).setForeground(theme.color_text);
        // component(2) is exit, leave that alone.

        // Frame Content
        window.mainContent().setBackground(theme.color_background);
        window.mainContent().setForeground(theme.color_text);
    }
    
    // Add mouse listeners to the frame for moving and resizing
    public void addListeners() {
        // Listener for on close
        window.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                window.isOpen = false;
            }

            @Override
            public void windowClosed(WindowEvent e) {
                window.isOpen = false;
            }
        });

        // Listener for moving the frame
        window.titleBar().addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                mouseX = e.getX();
                mouseY = e.getY();
            }
        });
        
        window.titleBar().addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (window.getExtendedState() == JFrame.NORMAL) {
                    window.setLocation(window.getX() + e.getX() - mouseX, window.getY() + e.getY() - mouseY);
                }
            }
        });
        
        // Listener for resizing the frame from the edges
        window.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (window.isResizeable()) {
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

        window.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (isResizing) {
                    resize(e);
                }
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                if (window.isResizeable() && window.getExtendedState() == JFrame.NORMAL) {
                    window.setCursor(getResizeCursor(e.getPoint()));
                } else {
                    window.setCursor(Cursor.getDefaultCursor());
                }
            }
        });
    }
    
    private int getResizeDirection(Point p) {
        int dir = 0;
        int x = p.x;
        int y = p.y;
        int width = window.getWidth();
        int height = window.getHeight();

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
        Rectangle bounds = window.getBounds();
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
        if (newWidth > window.getMinimumSize().width && newHeight > window.getMinimumSize().height) {
            window.setBounds(newX, newY, newWidth, newHeight);
            mouseX = e.getXOnScreen();
            mouseY = e.getYOnScreen();
        }
    }

    // toggle the dark mode theme on and off.
    public void toggleDarkmode(boolean selected, Theme thm)
    {
        if (selected) {
                thm.setThemeColors();
                thm.mode = "Dark";
            }else{
                thm.setDefaultColors();
                thm.mode = "Default";
            }
            retheme();
            window.revalidate();
            window.repaint();
    }


    /**
     * Menu Bar Helpers
     */

    // this is where we can define new file-menu items --NOTE: factor behavior elsewhere. 
    public JMenu populateFileMenu() {
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
        menuBar.fileMenuItems.add(exitItem);

        //return menu
        return file;
    }
    
    // this is where we can define new view-menu items --NOTE: factor behavior elsewhere. 
    public JMenu populateViewMenu() {
        // basic
        JMenu view = new JMenu("View");
        view.setForeground(theme.color_text);
        view.setBackground(theme.color_background);

        // dont be afraid to add non-checkbox items and seperators

        // add checkbox components
        JCheckBoxMenuItem darkmode = new JCheckBoxMenuItem("Dark Theme");
        darkmode.setSelected(true);
        darkmode.addActionListener(e -> { toggleDarkmode(darkmode.isSelected(), window.getTheme());});
        view.add(darkmode);

        // store references to items.
        menuBar.viewMenuCheckboxes.add(darkmode);

        return view;
    }
}