package dk.nikolajbrinch.faz80.ide.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class LruListTests {

  @Test
  void testInsert() {
    LruList<Integer> lruList = new LruList<>(3);
    lruList.add(1);
    lruList.add(2);
    lruList.add(3);
    lruList.add(4);
    lruList.add(5);

    Assertions.assertEquals(3, lruList.size());
    Assertions.assertEquals(5, lruList.getFirst());
    Assertions.assertEquals(3, lruList.getLast());
  }
}
