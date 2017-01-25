package br.com.infinitsolucoes.infinitvisitas.Business.Data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;

import br.com.infinitsolucoes.infinitvisitas.Interfaces.Findable;
import br.com.infinitsolucoes.infinitvisitas.Models.ColumnsDB;

import static br.com.infinitsolucoes.infinitvisitas.Business.AnnotationUtils.filter;

public abstract class InfinitSQLiteOpenHelper extends SQLiteOpenHelper {
    private Context context;
    protected static final String DATABASE_NAME = "InfinitSolucoes.sqlite";
    private static final String TAG = "InfinitSQLiteOpenHelper";
    protected static final int VERSAO = 1;
    private SQLiteDatabase liteDatabase;
    private List<ColumnsDB> columns;
    private String table;
    private final SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    protected String DROP_QUERY = "DROP TABLE IF EXISTS " + getTable();
    protected String CREATE_QUERY = "CREATE TABLE IF NOT EXISTS "
            + getTable()
            + " ( "
            + getColumnsJoinned()
            + (getConstraint().isEmpty() ? " " : ", ")
            + getConstraint()
            + " ) ";

    protected String getConstraint() {
        return "";
    }

    public Context getContext() {
        return context;
    }

    public SimpleDateFormat getSimpleDateFormat() {
        return mSimpleDateFormat;
    }

    public InfinitSQLiteOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSAO);
        this.context = context;

    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        onCreate(db);
    }

    public final String getColumnsJoinned() {
        return TextUtils.join(", ", getColumns());
    }

    public final String getTable() {
        if (this.table == null || this.table.isEmpty())
            this.table = onGetTable();
        return table;
    }

    public final String[] getColumnsArray() {
        List<String> stringList = new ArrayList<>();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            getColumns().forEach(r -> stringList.add(r.getName()));
        } else {
            for (ColumnsDB columnsDB :
                    getColumns()) {
                stringList.add(columnsDB.getName());
            }
        }
        return stringList.toArray(new String[stringList.size()]);
    }

    public final List<ColumnsDB> getColumns() {
        if (this.columns == null || this.columns.isEmpty())
            this.columns = onGetColumns();
        return this.columns;
    }

    public final String getDropQuery() {
        return DROP_QUERY;
    }

    public final String getCreateQuery() {
        return CREATE_QUERY;
    }

    protected final InfinitSQLiteOpenHelper setTable(String table) {
        this.table = table;
        return this;
    }

    protected final InfinitSQLiteOpenHelper setColumns(List<ColumnsDB> columns) {
        this.columns = columns;
        return this;
    }

    @NotNull
    protected abstract String onGetTable();

    @NotNull
    protected abstract List<ColumnsDB> onGetColumns();

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_QUERY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_QUERY);
        onCreate(db);
    }

    public SQLiteDatabase getLiteDatabase() {
        return this.liteDatabase;
    }

    protected void setLiteDatabase(SQLiteDatabase liteDatabase) {
        this.liteDatabase = liteDatabase;
    }

    public InfinitSQLiteOpenHelper open() {
        setLiteDatabase(this.getWritableDatabase());
        return this;
    }

    private Method getMethod(Class<?> aClass, String nMethod) throws NoSuchMethodException {
        for (Method method : aClass.getDeclaredMethods()) {
            if (method.getName().equalsIgnoreCase(nMethod))
                return method;
        }
        throw new NoSuchMethodException(nMethod);
    }

    protected int innerSave(final Object obj, final boolean onlySave) throws SQLException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        this.open();
        ContentValues contentValues = new ContentValues();
        final List<ColumnsDB> dbs = getColumns();
        final Collection<ColumnsDB> colunas = filter(dbs, type -> !(type.isAutoIncrement() && type.isPrimaryKey() && type.isIdentity()));
        final Collection<ColumnsDB> colunasId = filter(dbs, type -> (type.isAutoIncrement() && type.isPrimaryKey() && type.isIdentity()));

        final ColumnsDB idColuna = colunasId.iterator().next();

        for (ColumnsDB coluna : colunas) {
            final String methodName = "get" + coluna.getName();
            Method method = null;
            try {
                method = getMethod(obj.getClass(), methodName);
            } catch (NoSuchMethodException e) {
                method = getMethod(obj.getClass(), "is" + coluna.getName());
            }

            if (method.getReturnType() == Calendar.class) {
                final Calendar calendar = (Calendar) method.invoke(obj);
                contentValues.put(coluna.getName(), mSimpleDateFormat.format(calendar.getTime()));
            } else if (method.getReturnType() == boolean.class) {
                final boolean valor = (boolean) method.invoke(obj);
                contentValues.put(coluna.getName(), (valor ? 1 : 0));
            } else
                contentValues.put(coluna.getName(), method.invoke(obj).toString());
        }
        int idValue = 0;
        try {
            final String methodName = "get" + idColuna.getName();
            final Method method = getMethod(obj.getClass(), methodName);
            idValue = Integer.parseInt(method.invoke(obj).toString());
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }

        try {
            if (idValue > 0 && !onlySave)
                getLiteDatabase().update(getTable(), contentValues, idColuna.getName() + "=?", new String[]{String.valueOf(idValue)});
            else {
                idValue = (int) getLiteDatabase().insert(getTable(), null, contentValues);
                if (idValue == -1) {
                    throw new SQLiteException(getTable() + " not found");
                }
            }
        } catch (SQLiteException e) {
            onCreate(getLiteDatabase());
            if (idValue > 0 && !onlySave)
                getLiteDatabase().update(getTable(), contentValues, idColuna.getName() + "=?", new String[]{String.valueOf(idValue)});
            else
                idValue = (int) getLiteDatabase().insert(getTable(), null, contentValues);
        }
        this.close();
        return idValue;
    }

    protected int innerSave(final Object obj) throws SQLException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        return innerSave(obj, false);
    }

    protected int getCountQuery(String table, String selectionClause, String[] selectionArgs) {
        open();
        final String query = "SELECT COUNT(1) FROM " + table + " A WHERE " + selectionClause;
        final Cursor cursor;
        try {
            cursor = getLiteDatabase().rawQuery(query, selectionArgs);
        } catch (Exception e) {
            Log.w(TAG, e.getMessage(), e);
            return 0;
        }
        if (cursor == null || !cursor.moveToFirst())
            return 0;

        final int resultado = cursor.getInt(0);
        close();
        return resultado;
    }

    public List<Integer> findIds(final Findable pesquisa) {
        final List<Integer> retorno = new ArrayList<>();

        open();
        try {
            final Cursor cursor = getLiteDatabase().query(getTable(),
                    pesquisa.getColumnsFind(),
                    pesquisa.getWhereClause(),
                    pesquisa.getWhereArgs(),
                    pesquisa.getGroupByClause(),
                    pesquisa.getHavingClause(),
                    pesquisa.getOrderByClause());
            while (cursor.moveToNext()) {
                retorno.add(
                        cursor.getInt(
                                cursor.getColumnIndex(pesquisa.getColumnsFind()[0])));
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }

        close();
        return retorno;
    }
}
