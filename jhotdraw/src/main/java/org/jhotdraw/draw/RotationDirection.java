/**
 * @(#)RotationDirection.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the 
 * accompanying license terms.
 */

package org.jhotdraw.draw;

/**
 * Specifies the possible directions for rotations on a two-dimensional plane.
 * <p>
 * This enumeration is used by drawing tools and handles to perform constrained
 * transforms of figures on a drawing.
 *
 * @see Constrainer
 * 
 * @author Werner Randelshofer
 * @version $Id: RotationDirection.java 785 2013-12-01 19:16:30Z rawcoder $
 */
public enum RotationDirection {
    CLOCKWISE,
    COUNTER_CLOCKWISE
}
