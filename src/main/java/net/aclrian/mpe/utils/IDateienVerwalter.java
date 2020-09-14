package net.aclrian.mpe.utils;

import net.aclrian.mpe.messdiener.Messdiener;
import net.aclrian.mpe.pfarrei.Pfarrei;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.channels.FileLock;
import java.util.List;

public interface IDateienVerwalter {

    File getSavepath();
    void reloadMessdiener();

    List<Messdiener> getMessdiener();

    FileLock getLock();

    FileOutputStream getPfarreiFileOutputStream();

    void removeoldPfarrei(File neuePfarrei);

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
