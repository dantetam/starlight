package com.example.currentplacedetailsonmap;

/**
 * Created by Dante on 6/3/2017.
 */
public abstract class VariableListener<T> {

    protected T data;

    public VariableListener(T initialValue) {
        data = initialValue;
    }

    public T value() {
        return data;
    }

    public void set(T newValue) {
        data = newValue;
        callback();
    }

    public abstract void callback();

    public String toString() {
        return data.toString();
    }

}
