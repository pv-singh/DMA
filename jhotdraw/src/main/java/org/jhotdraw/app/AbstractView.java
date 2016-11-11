/*
 * @(#)AbstractView.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the 
 * accompanying license terms.
 */
package org.jhotdraw.app;

import edu.umd.cs.findbugs.annotations.Nullable;
import java.net.URI;
import java.util.*;
import javax.swing.*;
import java.util.concurrent.*;
import java.util.prefs.*;
import org.jhotdraw.util.prefs.PreferencesUtil;

/**
 * This abstract class can be extended to implement a {@link View}.
 * 
 * @author Werner Randelshofer
 * @version $Id: AbstractView.java 788 2014-03-22 07:56:28Z rawcoder $
 */
public abstract class AbstractView extends JPanel implements View {
    private static final long serialVersionUID = 1L;

    private Application application;
    /**
     * The executor used to perform background tasks for the View in a
     * controlled manner. This executor ensures that all background tasks
     * are executed sequentually.
     */
    @Nullable protected ExecutorService executor;
    /**
     * This is set to true, if the view has unsaved changes.
     */
    private boolean hasUnsavedChanges;
    /**
     * The preferences of the view.
     */
    protected Preferences preferences;
    /**
     * This id is used to make multiple open views of the same URI
     * identifiable.
     */
    private int multipleOpenId = 1;
    /**
     * This is set to true, if the view is showing.
     */
    private boolean isShowing;
    /**
     * The title of the view.
     */
    private String title;
    /** List of objects that need to be disposed when this view is disposed. */
    @Nullable private LinkedList<Disposable> disposables;
    /**
     * The URI of the view.
     * Has a null value, if the view has not been loaded from a URI
     * or has not been saved yet.
     */
    @Nullable protected URI uri;

    /**
     * Creates a new instance.
     */
    public AbstractView() {
        preferences = PreferencesUtil.userNodeForPackage(getClass());
    }

    /** Initializes the view.
     * This method does nothing, subclasses don't neet to call super. */
    @Override
    public void init() {
    }

    /** Starts the view.
     * This method does nothing, subclasses don't neet to call super. */
    @Override
    public void start() {
    }

    /** Activates the view.
     * This method does nothing, subclasses don't neet to call super. */
    @Override
    public void activate() {
    }

    /** Deactivates the view.
     * This method does nothing, subclasses don't neet to call super. */
    @Override
    public void deactivate() {
    }

    /** Stops the view.
     * This method does nothing, subclasses don't neet to call super. */
    @Override
    public void stop() {
    }

    /**
     * Gets rid of all the resources of the view.
     * No other methods should be invoked on the view afterwards.
     */
            @SuppressWarnings("unchecked")
    @Override
    public void dispose() {
        if (executor != null) {
            executor.shutdown();
            executor = null;
        }

        if (disposables != null) {
            for (Disposable d : (LinkedList<Disposable>)disposables.clone()) {
                d.dispose();
            }
            disposables = null;
        }

        removeAll();
    }

    @Override
    public boolean canSaveTo(URI uri) {
        return true;
    }

    @Override
   @Nullable public URI getURI() {
        return uri;
    }

    @Override
    public void setURI(@Nullable URI newValue) {
        URI oldValue = uri;
        uri = newValue;
        if (preferences != null && newValue != null) {
            preferences.put("projectFile", newValue.toString());
        }
        firePropertyChange(URI_PROPERTY, oldValue, newValue);
    }


    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setLayout(new java.awt.BorderLayout());
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
    @Override
    public void setApplication(Application newValue) {
        Application oldValue = application;
        application = newValue;
        firePropertyChange("application", oldValue, newValue);
    }

    @Override
    public Application getApplication() {
        return application;
    }

    @Override
    public JComponent getComponent() {
        return this;
    }
    @Override
    public boolean isEmpty() {
        return getURI()==null&&!hasUnsavedChanges();
    }

    /**
     * Returns true, if the view has unsaved changes.
     * This is a bound property.
     */
    @Override
    public boolean hasUnsavedChanges() {
        return hasUnsavedChanges;
    }

    protected void setHasUnsavedChanges(boolean newValue) {
        boolean oldValue = hasUnsavedChanges;
        hasUnsavedChanges = newValue;
        firePropertyChange(HAS_UNSAVED_CHANGES_PROPERTY, oldValue, newValue);
    }

    /**
     * Executes the specified runnable on the worker thread of the view.
     * Execution is performed sequentially in the same sequence as the
     * runnables have been passed to this method.
     */
    @Override
    public void execute(Runnable worker) {
        if (executor == null) {
            executor = Executors.newSingleThreadExecutor();
        }
        executor.execute(worker);
    }

    @Override
    public void setMultipleOpenId(int newValue) {
        int oldValue = multipleOpenId;
        multipleOpenId = newValue;
        firePropertyChange(MULTIPLE_OPEN_ID_PROPERTY, oldValue, newValue);
    }

    @Override
    public int getMultipleOpenId() {
        return multipleOpenId;
    }

    @Override
    public void setShowing(boolean newValue) {
        boolean oldValue = isShowing;
        isShowing = newValue;
        firePropertyChange(SHOWING_PROPERTY, oldValue, newValue);
    }

    @Override
    public boolean isShowing() {
        return isShowing;
    }

    @Override
    public void markChangesAsSaved() {
        setHasUnsavedChanges(false);
    }

    @Override
    public void setTitle(String newValue) {
        String oldValue = title;
        title = newValue;
        firePropertyChange(TITLE_PROPERTY, oldValue, newValue);
    }

    @Override
    public String getTitle() {
        return title;
    }

    /**
     * Adds a disposable object, which will be disposed when the specified view
     * is disposed.
     *
     * @param disposable
     */
    @Override
    public void addDisposable(Disposable disposable) {
        if (disposables == null) {
            disposables = new LinkedList<Disposable>();
        }
        disposables.add(disposable);
    }

    /**
     * Removes a disposable object, which was previously added.
     *
     * @param disposable
     */
    @Override
    public void removeDisposable(Disposable disposable) {
        if (disposables != null) {
            disposables.remove(disposable);
            if (disposables.isEmpty()) {
                disposables = null;
            }
        }
    }
}
