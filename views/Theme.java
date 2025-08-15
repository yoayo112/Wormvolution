/**
 * A place to set and manage color preferences and stylistic choices
 * For now just basic light and dark theme, fonts, borders and button styles. 
 * @author Sky Vercauteren 2025
 */

package views;
import java.awt.*;

public class Theme {

    public String mode;

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

    //fonts
    public static Font titleFont = new Font("Arial", Font.BOLD, 15);
    public static Font generalFont = new Font("Arial", Font.PLAIN, 14);
    public static Font symbolFont = new Font("Dialog", Font.BOLD, 14);

    public Theme() {
        setDefaultColors();
        mode = "Default";
    }
}
