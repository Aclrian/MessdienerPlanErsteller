module MessdienerplanErsteller.main {

	requires javafx.graphics;
	requires javafx.controls;
	requires commons.validator;
	requires javafx.fxml;
	requires org.apache.commons.io;
	requires java.xml;
	requires java.desktop;
	requires spring.web;
	requires com.jfoenix;
	requires json.simple;
	requires xhtmlrenderer;
	requires html2pdf;
	requires javafx.web;
	requires log4j;

	requires fr.opensagres.poi.xwpf.converter.core;
	requires fr.opensagres.poi.xwpf.converter.pdf;
	requires fr.opensagres.poi.xwpf.converter.xhtml;
	requires fr.opensagres.xdocreport.converter;
	requires fr.opensagres.xdocreport.converter.docx.xwpf;
	requires fr.opensagres.xdocreport.core;
	requires poi.ooxml;

	opens net.aclrian.fx;
	opens net.aclrian.mpe;
	opens net.aclrian.mpe.controller;
	opens net.aclrian.mpe.converter;
	opens net.aclrian.mpe.messdiener;
	opens net.aclrian.mpe.messe;
	opens net.aclrian.mpe.pfarrei;
	opens net.aclrian.mpe.utils;

	exports net.aclrian.fx;
	exports net.aclrian.mpe;
	exports net.aclrian.mpe.controller;
	exports net.aclrian.mpe.converter;
	exports net.aclrian.mpe.messdiener;
	exports net.aclrian.mpe.messe;
	exports net.aclrian.mpe.pfarrei;
	exports net.aclrian.mpe.utils;
}