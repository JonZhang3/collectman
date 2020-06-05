package com.collectman.script.database;

import org.mozilla.javascript.*;

import java.util.Arrays;

public class Database extends IdScriptableObject {

    private static final long serialVersionUID = 1L;

    private static final String DATABASE_TAG = "db";

    private Database() {

    }

    public static void init(Scriptable scope) {
        Database obj = new Database();
        obj.exportAsJSClass(MAX_PROTOTYPE_ID, scope, true);
    }

    private static final int
        ConstructorId_newConnection = -1,
        Id_constructor = 1,
        Id_toString = 2,
        MAX_PROTOTYPE_ID = 2;

    @Override
    protected void fillConstructorProperties(IdFunctionObject ctor) {
        addIdFunctionProperty(ctor, DATABASE_TAG, ConstructorId_newConnection, "newConnection", READONLY);
        super.fillConstructorProperties(ctor);
    }

    @Override
    protected void initPrototypeId(int id) {
        String name;
        int arity;
        switch (id) {
            case Id_constructor:
                arity = 1;
                name = "constructor";
                break;
            case Id_toString:
                arity = 1;
                name = "toString";
                break;
            default: throw new IllegalArgumentException(String.valueOf(id));
        }
        initPrototypeMethod(DATABASE_TAG, id, name, arity);
    }

    @Override
    public Object execIdCall(IdFunctionObject f, Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
        if (!f.hasTag(DATABASE_TAG)) {
            return super.execIdCall(f, cx, scope, thisObj, args);
        }
        int id = f.methodId();
        if (id == ConstructorId_newConnection) {
            return newConnection(args);
        } else if(id == Id_constructor) {
            return EMPTY;
        } else if(id == Id_toString) {
            return "";
        }
        throw new IllegalArgumentException(String.valueOf(id));
    }

    @Override
    protected int findPrototypeId(String name) {
        switch (name) {
            case "constructor":
                return Id_constructor;
            case "toString":
                return Id_toString;
            default:
                throw new IllegalStateException(name);
        }
    }

    public Database newConnection(Object... args) {
        System.out.println(Arrays.toString(args));
        return new Database();
    }

    public Object query() {
        return null;
    }

    @Override
    public String getClassName() {
        return DATABASE_TAG;
    }
}
