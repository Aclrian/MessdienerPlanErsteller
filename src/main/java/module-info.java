module MessdienerplanErsteller.main {

    requires javafx.graphics;
    requires javafx.controls;
    requires commons.validator;
    requires javafx.fxml;
    requires org.apache.commons.io;
    requires java.xml;
    requires java.desktop;
    requires spring.web;
    requires json.simple;
    requires xhtmlrenderer;
    requires html2pdf;
    requires javafx.web;
    requires org.docx4j.core;
	requires org.docx4j.openxml_objects;
    requires org.slf4j;
    requires org.apache.logging.log4j;
    requires log4j;

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