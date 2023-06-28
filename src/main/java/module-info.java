module MessdienerplanErsteller.main {

    requires java.desktop;
    requires java.sql;
    requires java.xml;

    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires html2pdf;//this is the only direct dependency that is non-modular
    requires jodconverter.local;
    requires jodconverter.core;
    requires org.apache.commons.io;
    requires org.apache.logging.log4j;
    requires spring.web;
    requires com.google.gson;
    requires org.apache.logging.log4j.core;

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
    exports net.aclrian.mpe.controller.converter;
    opens net.aclrian.mpe.controller.converter;

}