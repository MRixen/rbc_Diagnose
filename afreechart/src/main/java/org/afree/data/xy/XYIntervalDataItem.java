/* ===========================================================
 * AFreeChart : a free chart library for Android(tm) platform.
 *              (based on JFreeChart and JCommon)
 * ===========================================================
 *
 * (C) Copyright 2010, by ICOMSYSTECH Co.,Ltd.
 * (C) Copyright 2000-2008, by Object Refinery Limited and Contributors.
 *
 * Project Info:
 *    AFreeChart: http://code.google.com/p/afreechart/
 *    JFreeChart: http://www.jfree.org/jfreechart/index.html
 *    JCommon   : http://www.jfree.org/jcommon/index.html
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * [Android is a trademark of Google Inc.]
 *
 * -----------------------
 * XYIntervalDataItem.java
 * -----------------------
 * 
 * (C) Copyright 2010, by ICOMSYSTECH Co.,Ltd.
 *
 * Original Author:  shiraki  (for ICOMSYSTECH Co.,Ltd);
 * Contributor(s):   Sato Yoshiaki ;
 *                   Niwano Masayoshi;
 *
 * Changes (from 19-Nov-2010)
 * --------------------------
 * 19-Nov-2010 : port JFreeChart 1.0.13 to Android as "AFreeChart"
 * 
 * ------------- JFreeChart ---------------------------------------------
 * (C) Copyright 2006-2008, by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * Changes
 * -------
 * 20-Oct-2006 : Version 1 (DG);
 *
 */

package org.afree.data.xy;

import org.afree.data.ComparableObjectItem;



/**
 * An item representing data in the form (x, x-low, x-high, y, y-low, y-high).
 *
 * @since JFreeChart 1.0.3
 */
public class XYIntervalDataItem extends ComparableObjectItem {

    /**
     * 
     */
    private static final long serialVersionUID = -2659769384719754955L;

    /**
     * Creates a new instance of <code>XYIntervalItem</code>.
     *
     * @param x  the x-value.
     * @param xLow  the lower bound of the x-interval.
     * @param xHigh  the upper bound of the x-interval.
     * @param y  the y-value.
     * @param yLow  the lower bound of the y-interval.
     * @param yHigh  the upper bound of the y-interval.
     */
    public XYIntervalDataItem(double x, double xLow, double xHigh, double y,
            double yLow, double yHigh) {
        super(new Double(x), new XYInterval(xLow, xHigh, y, yLow, yHigh));
    }

    /**
     * Returns the x-value.
     *
     * @return The x-value (never <code>null</code>).
     */
    public Double getX() {
        return (Double) getComparable();
    }

    /**
     * Returns the y-value.
     *
     * @return The y-value.
     */
    public double getYValue() {
        XYInterval interval = (XYInterval) getObject();
        if (interval != null) {
            return interval.getY();
        }
        else {
            return Double.NaN;
        }
    }

    /**
     * Returns the lower bound of the x-interval.
     *
     * @return The lower bound of the x-interval.
     */
    public double getXLowValue() {
        XYInterval interval = (XYInterval) getObject();
        if (interval != null) {
            return interval.getXLow();
        }
        else {
            return Double.NaN;
        }
    }

    /**
     * Returns the upper bound of the x-interval.
     *
     * @return The upper bound of the x-interval.
     */
    public double getXHighValue() {
        XYInterval interval = (XYInterval) getObject();
        if (interval != null) {
            return interval.getXHigh();
        }
        else {
            return Double.NaN;
        }
    }

    /**
     * Returns the lower bound of the y-interval.
     *
     * @return The lower bound of the y-interval.
     */
    public double getYLowValue() {
        XYInterval interval = (XYInterval) getObject();
        if (interval != null) {
            return interval.getYLow();
        }
        else {
            return Double.NaN;
        }
    }

    /**
     * Returns the upper bound of the y-interval.
     *
     * @return The upper bound of the y-interval.
     */
    public double getYHighValue() {
        XYInterval interval = (XYInterval) getObject();
        if (interval != null) {
            return interval.getYHigh();
        }
        else {
            return Double.NaN;
        }
    }

}
