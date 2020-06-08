package com.collectman.script;

import com.collectman.common.CollectManException;
import com.collectman.common.Utils;
import com.queryflow.accessor.AccessorFactory;
import com.queryflow.accessor.AccessorFactoryBuilder;
import com.queryflow.config.DatabaseConfig;
import org.mozilla.javascript.*;

import java.sql.ResultSetMetaData;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class Database extends IdScriptableObject {

    private static final long serialVersionUID = 1L;

    private static final String DATABASE_TAG = "database";

    private AccessorFactory accessorFactory;

    private Database() {

    }

    private Database(Object configs) {
        if (!(configs instanceof NativeObject)) {
            throw new CollectManException("the param must be a Object.");
        }
        String configStr = Utils.GSON.toJson(configs);
        DatabaseConfig databaseConfig = Utils.GSON.fromJson(configStr, DatabaseConfig.class);
        AccessorFactoryBuilder factoryBuilder = new AccessorFactoryBuilder();
        factoryBuilder.addDatabase(databaseConfig);
        this.accessorFactory = factoryBuilder.build(false);
    }

    public static void init(Scriptable scope) {
        Database obj = new Database();
        obj.exportAsJSClass(MAX_PROTOTYPE_ID, scope, false);
    }

    private static final int
        Id_constructor = 1,
        Id_toString = 2,
        Id_query = 3,
        Id_update = 4,
        Id_page = 5,
        Id_openTransaction = 6,
        Id_commit = 7,
        Id_rollback = 8,
        Id_close = 9,
        MAX_PROTOTYPE_ID = 9;

    private static final int
        ConstructorId_connect = -1;

    @Override
    protected void fillConstructorProperties(IdFunctionObject ctor) {
        addIdFunctionProperty(ctor, DATABASE_TAG, ConstructorId_connect, "connect", 1);
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
                arity = 0;
                name = "toString";
                break;
            case Id_query:
                arity = 2;
                name = "query";
                break;
            case Id_update:
                arity = 2;
                name = "update";
                break;
            case Id_page:
                arity = 4;
                name = "page";
                break;
            case Id_openTransaction:
                arity = 0;
                name = "openTransaction";
                break;
            case Id_commit:
                arity = 0;
                name = "commit";
                break;
            case Id_rollback:
                arity = 0;
                name = "rollback";
                break;
            case Id_close:
                arity = 0;
                name = "close";
                break;
            default:
                throw new IllegalArgumentException(String.valueOf(id));
        }
        initPrototypeMethod(DATABASE_TAG, id, name, arity);
    }

    @Override
    protected int findPrototypeId(String name) {
        switch (name) {
            case "constructor":
                return Id_constructor;
            case "toString":
                return Id_toString;
            case "query":
                return Id_query;
            case "update":
                return Id_update;
            case "page":
                return Id_page;
            case "openTransaction":
                return Id_openTransaction;
            case "commit":
                return Id_commit;
            case "rollback":
                return Id_rollback;
            case "close":
                return Id_close;
            default:
                throw new IllegalStateException(name);
        }
    }

    @Override
    public Object execIdCall(IdFunctionObject f, Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
        if (!f.hasTag(DATABASE_TAG)) {
            return super.execIdCall(f, cx, scope, thisObj, args);
        }
        int id = f.methodId();
        switch (id) {
            case ConstructorId_connect:
                return connect(scope, args);
            case Id_query:
                return js_query(scope, ScriptUtils.asString(args, 0), args);
            case Id_update:
                return js_update(ScriptUtils.asString(args, 0), args);
            case Id_page:
                return js_page(ScriptUtils.asString(args, 0),
                    ScriptUtils.asInteger(args, 1), ScriptUtils.asInteger(args, 2), args);
            case Id_openTransaction:
                return js_openTransaction();
            case Id_commit:
                return js_commit();
            case Id_rollback:
                return js_rollback();
            case Id_close:
                return js_close();
            case Id_toString:
                return "[object Object]";
            default:
                throw new IllegalArgumentException(String.valueOf(id));
        }
    }

    public Database connect(Scriptable scope, Object... args) {
        if (args == null || args.length == 0 || args[0] == null) {
            throw new CollectManException("please config the database");
        }
        Scriptable classPrototype = ScriptableObject.getClassPrototype(scope, DATABASE_TAG);
        System.out.println(classPrototype);
        Database database = new Database(args[0]);
        database.setPrototype(classPrototype);
        database.setParentScope(this.getParentScope());
        return database;
    }

    public Object js_query(Scriptable scope, String sql, Object[] args) {
        if (Utils.isEmpty(sql)) {
            return ScriptRuntime.makeError("the sql is empty");
        }
        Object[] values = toValues(args, 1);
        try {
            return AccessorFactory.accessor().query(sql, values).result(rs -> {
                ResultSetMetaData metaData = rs.getMetaData();
                int colCount = metaData.getColumnCount();
                List<Object> result = new LinkedList<>();
                while (rs.next()) {
                    if (colCount == 1) {
                        result.add(ScriptRuntime.toObject(scope, rs.getObject(1)));
                    } else {
                        NativeObject column = new NativeObject();
                        for (int i = 1; i <= colCount; i++) {
                            column.put(metaData.getColumnName(i), column, ScriptRuntime.toObject(scope,
                                rs.getObject(i)));
                        }
                        result.add(column);
                    }
                }
                if (result.size() == 1) {
                    return result.get(0);
                }
                return ScriptRuntime.toObject(scope, Utils.toArray(result));
            });
        } catch (Exception e) {
            return ScriptRuntime.makeError(e.getMessage());
        }
    }

    public Object js_update(String sql, Object[] args) {
        if (Utils.isEmpty(sql)) {
            return ScriptRuntime.makeError("the sql is empty");
        }
        Object[] values = toValues(args, 1);
        try {
            return this.accessorFactory.getAccessor().update(sql, values);
        } catch (Exception e) {
            return ScriptRuntime.makeError(e.getMessage());
        }
    }

    public Object js_page(String sql, Integer limit, Integer page, Object[] args) {
        if (Utils.isEmpty(sql)) {
            return ScriptRuntime.makeError("the sql is empty");
        }
        if (limit == null) {
            return ScriptRuntime.makeError("specify limit param");
        }
        if (page == null) {
            return ScriptRuntime.makeError("specify page param");
        }
        Object[] values = toValues(args, 3);

        return null;
    }

    public Object js_openTransaction() {
        this.accessorFactory.getAccessor().openTransaction();
        return Undefined.instance;
    }

    public Object js_commit() {
        this.accessorFactory.getAccessor().commit();
        return Undefined.instance;
    }

    public Object js_rollback() {
        this.accessorFactory.getAccessor().rollback();
        return Undefined.instance;
    }

    public Object js_close() {
        this.accessorFactory.getAccessor().close();
        return Undefined.instance;
    }

    private Object[] toValues(Object[] args, int start) {
        Object[] values;
        if (args.length == 0) {
            values = null;
        } else {
            values = Arrays.copyOfRange(args, start, args.length);
            if (values.length == 1 && values[0] instanceof NativeArray) {
                values = Utils.toArray(values[0]);
            }
        }
        return values;
    }

    public void close() {
        this.accessorFactory.getAccessor().close();
    }

    @Override
    public String getClassName() {
        return DATABASE_TAG;
    }
}
