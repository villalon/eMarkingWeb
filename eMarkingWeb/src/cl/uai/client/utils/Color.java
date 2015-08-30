/**
 * 
 */
package cl.uai.client.utils;

import com.google.gwt.user.client.ui.Widget;

/**
 * @author Jorge
 *
 */
public class Color {

	/**
	 * Adds a CSS class with a specific color to a widget as background color
	 * @param sequence sequence
	 * @param widget the widget to be painted
	 */
	public static void setWidgetBackgroundHueColor(int sequence, Widget widget) {
		setWidgetStyleHueColor(sequence, widget, "background-color");
	}
	
	/**
	 * Adds a CSS class with a specific color to a widget as background color
	 * @param sequence sequence
	 * @param widget the widget to be painted
	 */
	public static void setWidgetFontHueColor(int sequence, Widget widget) {
		setWidgetStyleHueColor(sequence, widget, "color");
	}
	
	public static String getCSSHueColor(int sequence) {

		// We pick a sequence based on Golden Ratio of the 360 values of HUE
		double goldenratio = 0.618033988749895 * 360;
		// We choose the number corresponding to the sequence
		double color = (double) sequence * goldenratio;
		color = color % 360;
		
		// We need the int version
		int hue = (int) color;

		return "hsl(" + hue + ",50%,85%)";
	}
	
	/**
	 * Adds a CSS class with a specific color to a widget as background color
	 * @param sequence sequence
	 * @param widget the widget to be painted
	 * @param styleAttribute the attribute which will be colored in the style
	 */
	private static void setWidgetStyleHueColor(int sequence, Widget widget, String styleAttribute) {
		String style = widget.getElement().getAttribute("style");
		style = style.replaceAll("color:hsl\\(\\d+,\\d+%,\\d+%\\);", "");
		widget.getElement().setAttribute("style", style += styleAttribute + ":" + getCSSHueColor(sequence) + ";");
	}
}
