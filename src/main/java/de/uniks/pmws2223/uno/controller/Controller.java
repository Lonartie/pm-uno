package de.uniks.pmws2223.uno.controller;

import javafx.scene.Parent;

import java.io.IOException;

public interface Controller {
    String getTitle();

    void init();

    Parent render() throws IOException;

    void destroy();
}