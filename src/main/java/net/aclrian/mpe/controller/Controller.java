package net.aclrian.mpe.controller;

public interface Controller {
	void initialize();
	void afterstartup();
	boolean isLocked();
}
