package net.aclrian.mpe.utils;

import net.aclrian.mpe.messdiener.*;
import net.aclrian.mpe.pfarrei.*;

import java.io.*;
import java.nio.channels.*;
import java.util.*;

public interface IDateienVerwalter {

    File getSavePath();

    void reloadMessdiener();

    List<Messdiener> getMessdiener();

    FileLock getLock();

    FileOutputStream getPfarreiFileOutputStream();

    void removeOldPfarrei(File neuePfarrei);

    Pfarrei getPfarrei();

    class NoSuchPfarrei extends Exception {
        private final File savepath;

        public NoSuchPfarrei(File savepath) {
            this.savepath = savepath;
        }

        public File getSavepath() {
            return savepath;
        }
    }
}
