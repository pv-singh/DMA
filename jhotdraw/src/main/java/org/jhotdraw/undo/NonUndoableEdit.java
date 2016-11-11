/*
 * @(#)NonUndoableEdit.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the 
 * accompanying license terms.
 */

package org.jhotdraw.undo;

import javax.swing.undo.*;
/**
 * NonUndoableEdit.
 *
 * @author  Werner Randelshofer
 * @version $Id: NonUndoableEdit.java 788 2014-03-22 07:56:28Z rawcoder $
 */
public class NonUndoableEdit extends AbstractUndoableEdit {
        private static final long serialVersionUID = 1L;

    /** Creates a new instance. */
    public NonUndoableEdit() {
    }
    
    @Override
    public boolean canUndo() {
        return false;
    }
    @Override
    public boolean canRedo() {
        return false;
    }
}
