package dk.nikolajbrinch.faz80.assembler;

import java.util.Arrays;
import java.util.List;
import java.util.function.LongSupplier;
import java.util.stream.LongStream;
import java.util.stream.Stream;

public class ByteSource {

  private final List<ByteSupplier> bytes;
  private final long length;

  private ByteSource(List<ByteSupplier> bytes, long length) {
    this.bytes = bytes;
    this.length = length;
  }

  private ByteSource(Stream<ByteSupplier> bytes, long length) {
    this.bytes = bytes.toList();
    this.length = length;
  }

  /*
   * 1 byte
   */

  public static ByteSource of(ByteSupplier byte1) {
    return new ByteSource(Stream.of(byte1), 1);
  }

  public static ByteSource of(long byte1) {
    return new ByteSource(Stream.of(ByteSupplier.of(byte1)), 1);
  }

  /*
   * 2 bytes
   */

  public static ByteSource of(ByteSupplier byte1, ByteSupplier byte2) {
    return new ByteSource(Stream.of(byte1, byte2), 2);
  }

  public static ByteSource of(ByteSupplier byte1, long byte2) {
    return of(byte1).append(of(byte2));
  }

  public static ByteSource of(long byte1, long byte2) {
    return new ByteSource(LongStream.of(byte1, byte2).mapToObj(ByteSupplier::of), 2);
  }

  public static ByteSource of(long byte1, ByteSupplier byte2) {
    return of(byte1).append(of(byte2));
  }

  /*
   * 3 bytes
   */

  public static ByteSource of(ByteSupplier byte1, ByteSupplier byte2, ByteSupplier byte3) {
    return new ByteSource(Stream.of(byte1, byte2, byte3), 3);
  }

  public static ByteSource of(long byte1, ByteSupplier byte2, ByteSupplier byte3) {
    return of(byte1).append(of(byte2, byte3));
  }

  public static ByteSource of(ByteSupplier byte1, long byte2, ByteSupplier byte3) {
    return of(byte1).append(of(byte2)).append(of(byte3));
  }

  public static ByteSource of(ByteSupplier byte1, ByteSupplier byte2, long byte3) {
    return of(byte1, byte2).append(of(byte3));
  }

  public static ByteSource of(long byte1, long byte2, long byte3) {
    return new ByteSource(LongStream.of(byte1, byte2, byte3).mapToObj(ByteSupplier::of), 3);
  }

  public static ByteSource of(ByteSupplier byte1, long byte2, long byte3) {
    return of(byte1).append(of(byte2, byte3));
  }

  public static ByteSource of(long byte1, ByteSupplier byte2, long byte3) {
    return of(byte1).append(of(byte2)).append(of(byte3));
  }

  public static ByteSource of(long byte1, long byte2, ByteSupplier byte3) {
    return of(byte1).append(of(byte2, byte3));
  }

  /*
   * 4 bytes
   */

  public static ByteSource of(
      ByteSupplier byte1, ByteSupplier byte2, ByteSupplier byte3, ByteSupplier byte4) {
    return new ByteSource(Stream.of(byte1, byte2, byte3, byte4), 4);
  }

  public static ByteSource of(
      int byte1, ByteSupplier byte2, ByteSupplier byte3, ByteSupplier byte4) {
    return of(byte1).append(of(byte2, byte3, byte4));
  }

  public static ByteSource of(
      ByteSupplier byte1, long byte2, ByteSupplier byte3, ByteSupplier byte4) {
    return of(byte1).append(of(byte2)).append(of(byte3, byte4));
  }

  public static ByteSource of(
      ByteSupplier byte1, ByteSupplier byte2, long byte3, ByteSupplier byte4) {
    return of(byte1, byte2).append(of(byte3)).append(of(byte4));
  }

  public static ByteSource of(
      ByteSupplier byte1, ByteSupplier byte2, ByteSupplier byte3, long byte4) {
    return of(byte1, byte2, byte3).append(of(byte4));
  }

  public static ByteSource of(long byte1, long byte2, ByteSupplier byte3, ByteSupplier byte4) {
    return of(byte1, byte2).append(of(byte3, byte4));
  }

  public static ByteSource of(ByteSupplier byte1, long byte2, long byte3, ByteSupplier byte4) {
    return of(byte1).append(of(byte2, byte3)).append(of(byte4));
  }

  public static ByteSource of(ByteSupplier byte1, ByteSupplier byte2, long byte3, long byte4) {
    return of(byte1, byte2).append(of(byte3, byte4));
  }

  public static ByteSource of(long byte1, ByteSupplier byte2, long byte3, long byte4) {
    return of(byte1).append(of(byte2)).append(of(byte3, byte4));
  }

  public static ByteSource of(long byte1, long byte2, ByteSupplier byte3, long byte4) {
    return of(byte1, byte2).append(of(byte3)).append(of(byte4));
  }

  public static ByteSource of(long byte1, long byte2, long byte3, ByteSupplier byte4) {
    return of(byte1, byte2, byte3).append(of(byte4));
  }

  public static ByteSource of(ByteSupplier byte1, long byte2, long byte3, long byte4) {
    return of(byte1).append(of(byte2, byte3, byte4));
  }

  public static ByteSource of(long byte1, long byte2, long byte3, long byte4) {
    return new ByteSource(LongStream.of(byte1, byte2, byte3, byte4).mapToObj(ByteSupplier::of), 4);
  }

  public static ByteSource of(long byte1, ByteSupplier byte2, long byte3, ByteSupplier byte4) {
    return of(byte1).append(of(byte2)).append(of(byte3)).append(of(byte4));
  }

  public static ByteSource of(long byte1, ByteSupplier byte2, ByteSupplier byte3, long byte4) {
    return of(byte1).append(of(byte2, byte3)).append(of(byte4));
  }

  public static ByteSource of(ByteSupplier byte1, long byte2, ByteSupplier byte3, long byte4) {
    return of(byte1).append(of(byte2)).append(of(byte3)).append(of(byte4));
  }

  /*
   * Utility
   */

  public static ByteSource of(long... bytes) {
    return new ByteSource(Arrays.stream(bytes).mapToObj(ByteSupplier::of), bytes.length);
  }

  public static ByteSource of(List<ByteSupplier> bytes) {
    return new ByteSource(bytes.stream(), bytes.size());
  }

  public ByteSource append(ByteSource other) {
    return new ByteSource(
        Stream.concat(bytes.stream(), other.bytes.stream()), this.length + other.length());
  }

  public long[] getBytes() {
    return bytes.stream().mapToLong(LongSupplier::getAsLong).toArray();
  }

  public long length() {
    return length;
  }
}
