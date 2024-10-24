package dk.nikolajbrinch.faz80.ide.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.SequencedCollection;

public class LruList<T> implements SequencedCollection<T> {

  private final LinkedHashSet<T> cache = new LinkedHashSet<>();

  private final int maxSize;

  public LruList(int maxSize) {
    this.maxSize = maxSize;
  }

  @Override
  public SequencedCollection<T> reversed() {
    return cache.reversed();
  }

  @Override
  public int size() {
    return cache.size();
  }

  @Override
  public boolean isEmpty() {
    return cache.isEmpty();
  }

  @Override
  public boolean contains(Object o) {
    return cache.contains(o);
  }

  @Override
  public Iterator<T> iterator() {
    return cache.iterator();
  }

  @Override
  public Object[] toArray() {
    return new Object[0];
  }

  @Override
  public <T1> T1[] toArray(T1[] a) {
    return cache.toArray(a);
  }

  @Override
  public boolean add(T t) {
    return addItem(t);
  }

  @Override
  public boolean remove(Object o) {
    return cache.remove(o);
  }

  @Override
  public boolean containsAll(Collection<?> c) {
    return cache.containsAll(c);
  }

  @Override
  public boolean addAll(Collection<? extends T> c) {
    boolean changed = true;

    for (T t : c) {
      changed &= addItem(t);
    }

    return changed;
  }

  @Override
  public boolean removeAll(Collection<?> c) {
    return cache.removeAll(c);
  }

  @Override
  public boolean retainAll(Collection<?> c) {
    return cache.retainAll(c);
  }

  @Override
  public void clear() {
    cache.clear();
  }

  private boolean addItem(T t) {
    cache.addFirst(t);

    if (cache.size() > maxSize) {
      cache.removeLast();
    }

    return true;
  }
}
