package ua.setcom.developertools.Models;

/**
 * Created by daniil on 10/23/14.
 */
public class Operation {

    public int id;
    public Object data;

    public boolean isBoolean() {
        return data instanceof Boolean;
    }
    public boolean isInt() {
        return data instanceof Integer;
    }
    public boolean isString() {
        return data instanceof String;
    }
}
