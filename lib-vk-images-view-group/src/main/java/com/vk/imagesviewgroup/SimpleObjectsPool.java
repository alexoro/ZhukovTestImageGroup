package com.vk.imagesviewgroup;

import java.util.Stack;

/**
 * Not thread safe.
 */
class SimpleObjectsPool<T> {

    public interface ObjectFactory<T> {
        T newObject();
        void reset(T object);
    }

    private Stack<T> mPool;
    private ObjectFactory<T> mFactory;

    public SimpleObjectsPool(ObjectFactory<T> objectFactory) {
        mPool = new Stack<>();
        mFactory = objectFactory;
    }

    public T acquire() {
        if (mPool.isEmpty()) {
            return mFactory.newObject();
        } else {
            return mPool.pop();
        }
    }

    public void release(T object) {
        mPool.push(object);
        mFactory.reset(object);
    }

}