// automatically generated by the FlatBuffers compiler, do not modify

package com.google.ar.sceneform.lullmodel;

/**
 * The directions a layout will grow in.
 * This controls the ordering of elements within wrapping rows, but not the
 * horizontal alignment of those rows as a whole (which is controlled by
 * LayoutHorizontalAlignment).
 */
public final class LayoutFillOrder {
  private LayoutFillOrder() { }
  /**
   * The first entity is added leftmost, and subsequent entities go to the
   * right (and then down if wrapping is enabled).
   */
  public static final int RightDown = 0;
  /**
   * The first entity is added rightmost, and subsequent entities go to the
   * left (and then down if wrapping is enabled).
   */
  public static final int LeftDown = 1;
  /**
   * The first entity is added leftmost, and subsequent entities go down
   * (and then right if wrapping is enabled).
   */
  public static final int DownRight = 2;
  /**
   * The first entity is added rightmost, and subsequent entities go down
   * (and then left if wrapping is enabled).
   */
  public static final int DownLeft = 3;
  /**
   * The first entity is added leftmost, and subsequent entities go to the
   * right (and then up if wrapping is enabled).
   */
  public static final int RightUp = 4;
  /**
   * The first entity is added rightmost, and subsequent entities go to the
   * left (and then up if wrapping is enabled).
   */
  public static final int LeftUp = 5;
  /**
   * The first entity is added leftmost, and subsequent entities go up
   * (and then right if wrapping is enabled).
   */
  public static final int UpRight = 6;
  /**
   * The first entity is added rightmost, and subsequent entities go up
   * (and then left if wrapping is enabled).
   */
  public static final int UpLeft = 7;

  public static final String[] names = { "RightDown", "LeftDown", "DownRight", "DownLeft", "RightUp", "LeftUp", "UpRight", "UpLeft", };

  public static String name(int e) { return names[e]; }
}
