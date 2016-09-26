package com.vk.imagesviewgroup;

/**
 * Created by a.sorokin@mail.vk.com on 28.07.2015.
 */
class Size {

    public int width;
    public int height;

    public Size() {

    }

    public Size(int width, int height) {
        this.width = width;
        this.height = height;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Size)) return false;

        Size size = (Size) obj;

        if (width != size.width) return false;
        return height == size.height;
    }

    @Override
    public int hashCode() {
        int result = width;
        result = 31 * result + height;
        return result;
    }

    @Override
    public String toString() {
        return "Size{" +
                "width=" + width +
                ", height=" + height +
                '}';
    }

}