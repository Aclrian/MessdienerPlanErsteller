package net.aclrian.mpe.utils;

import java.util.ArrayList;
import java.util.HashSet;

public class RemoveDoppelte<E> {
    public void removeDuplicatedEntries(ArrayList<E> arrayList) {
	HashSet<E> hashSet = new HashSet<>(arrayList);
	arrayList.clear();
	arrayList.addAll(hashSet);
    }
}