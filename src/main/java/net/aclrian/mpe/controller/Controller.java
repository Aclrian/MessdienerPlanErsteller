package net.aclrian.mpe.controller;


import javafx.stage.*;

public interface Controller {
	void initialize();

	void afterStartup(Window window, MainController mc);

	boolean isLocked();
}
