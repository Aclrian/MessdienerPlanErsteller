package net.aclrian.mpe.utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class RemoveDoppelte<E> {
    public List<E> removeDuplicatedEntries(List<E> list) {
        HashSet<E> hashSet = new HashSet<>(list);
        return new ArrayList<>(hashSet);
    }
}