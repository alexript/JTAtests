/*
 * Copyright 2023 malyshev.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.napilnik.ui;

import java.awt.Dimension;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

/**
 *
 * @author malyshev
 */
public class AppProperties extends Properties {

    private static final long serialVersionUID = 6210907450490298467L;

    private static final String WINDOW_X = "window.X";
    private static final String WINDOW_Y = "window.Y";
    private static final String WINDOW_W = "window.W";
    private static final String WINDOW_H = "window.H";
    private static final String WINDOW_MAXIMIZED = "window.maximazed";
    private static final String WINDOW_DIVIDER = "window.divider";
    private final String propsFile;

    public AppProperties(String propsFile) {
        super();
        this.propsFile = propsFile;
        readProps();
    }

    private void readProps() {
        File file = getPropsFile();
        try (InputStream is = new FileInputStream(file)) {
            this.load(is);
        } catch (FileNotFoundException ex) {
        } catch (IOException ex) {
        }
    }

    public void writeProps() {
        File file = getPropsFile();
        try (OutputStream os = new FileOutputStream(file)) {
            this.store(os, "");
        } catch (FileNotFoundException ex) {
        } catch (IOException ex) {
        }
    }

    private File getPropsFile() {
        File file = new File(propsFile);
        return file;
    }

    private int getInt(String name, int defv) {
        String property = getProperty(name, Integer.toString(defv));
        return Integer.parseInt(property);
    }

    private void set(String name, int v) {
        this.setProperty(name, Integer.toString(v));
    }

    private boolean getBool(String name, boolean defv) {
        String property = getProperty(name, Boolean.toString(defv));
        return Boolean.parseBoolean(property);
    }

    private void set(String name, boolean v) {
        this.setProperty(name, Boolean.toString(v));
    }

    public void setX(int x) {
        this.set(WINDOW_X, x);
    }

    public void setY(int y) {
        this.set(WINDOW_Y, y);
    }

    public void setSize(Dimension size) {
        this.set(WINDOW_W, size.width);
        this.set(WINDOW_H, size.height);
    }

    public int getX() {
        return this.getInt(WINDOW_X, 10);
    }

    public int getY() {
        return this.getInt(WINDOW_Y, 10);
    }

    public Dimension getSize() {
        int w = this.getInt(WINDOW_W, 1000);
        int h = this.getInt(WINDOW_H, 700);
        return new Dimension(w, h);
    }

    public int getDividerPosition(int defpos) {
        return this.getInt(WINDOW_DIVIDER, defpos);
    }

    public int getDividerPosition() {
        return getDividerPosition(500);
    }

    public void setDividerPosition(int pos) {
        this.set(WINDOW_DIVIDER, pos);
    }

    public boolean getMaximized() {
        return this.getBool(WINDOW_MAXIMIZED, false);
    }

    public void setMaximized(boolean val) {
        this.set(WINDOW_MAXIMIZED, val);
    }

}
