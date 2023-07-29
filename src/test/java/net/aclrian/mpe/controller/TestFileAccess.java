package net.aclrian.mpe.controller;

import org.junit.jupiter.api.Test;

public class TestFileAccess {
    @Test
    public void testFXMLs() {
        for (MainController.EnumPane pane : MainController.EnumPane.values()) {
            assert TestFileAccess.class.getResource(pane.getLocation()) != null;
        }
    }
}
