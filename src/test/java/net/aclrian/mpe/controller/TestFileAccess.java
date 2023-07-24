package net.aclrian.mpe.controller;

import org.junit.jupiter.api.Test;

class TestFileAccess {
    @Test
    void testFXMLs() {
        for (MainController.EnumPane pane : MainController.EnumPane.values()) {
            assert TestFileAccess.class.getResource(pane.getLocation()) != null;
        }
    }
}
