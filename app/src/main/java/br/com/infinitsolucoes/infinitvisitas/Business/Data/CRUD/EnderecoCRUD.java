package br.com.infinitsolucoes.infinitvisitas.Business.Data.CRUD;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import br.com.infinitsolucoes.infinitvisitas.Business.AnnotationUtils;
import br.com.infinitsolucoes.infinitvisitas.Business.Data.InfinitSQLiteOpenHelper;
import br.com.infinitsolucoes.infinitvisitas.Models.ColumnsDB;
import br.com.infinitsolucoes.infinitvisitas.Models.Enderecos;

public final class EnderecoCRUD extends InfinitSQLiteOpenHelper {

    private static final String TAG = "EnderecoCRUD";

    public EnderecoCRUD(Context context) {
        super(context);
    }

    @NotNull
    @Override
    protected String onGetTable() {
        return "enderecos";
    }

    @NotNull
    @Override
    protected List<ColumnsDB> onGetColumns() {
        List<ColumnsDB> columnsDBList = new ArrayList<>();
        try {
            columnsDBList = AnnotationUtils.getColumns(Enderecos.class);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return columnsDBList;
    }

    public Enderecos save(Enderecos endereco) throws SQLException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        int novoId = 0;

        novoId = innerSave(endereco);

        if (endereco.get_enderecoId() <= 0 && novoId > 0) {
            final Enderecos novoEndereco = new Enderecos(novoId, endereco.getLogradouro(), endereco.getMunicipio(), endereco.getUf());
            novoEndereco.setBairro(endereco.getBairro());
            novoEndereco.setNumero(endereco.getNumero());
            endereco = novoEndereco;
        }
        return endereco;
    }

    public boolean delete(Enderecos endereco) {
        boolean result = getLiteDatabase().delete(getTable(), "_enderecoId = " + endereco.get_enderecoId(), null) > 0;
        if (result) {
            endereco = null;
        }
        return result;
    }

    public boolean isUsed(final Enderecos enderecos) {
        final String query = "SELECT 1 FROM "
                + new EmpresaCRUD(getContext()).getTable()
                + " WHERE enderecoId="
                + String.valueOf(enderecos.get_enderecoId());
        final Cursor cursor = getLiteDatabase().rawQuery(query, null);
        return (cursor != null && cursor.getCount() > 0);
    }

    public boolean isUsed(final int enderecoId) {
        try {
            return isUsed(get(enderecoId));
        } catch (SQLException e) {
            Log.e(TAG, e.getMessage(), e);
            return false;
        }
    }

    private Enderecos convertCursorToEnderecos(Cursor cursor) throws SQLException {
        Enderecos endereco = new Enderecos(cursor.getInt(cursor.getColumnIndex("_enderecoId")),
                cursor.getString(cursor.getColumnIndex("logradouro")),
                cursor.getString(cursor.getColumnIndex("uf")),
                cursor.getString(cursor.getColumnIndex("municipio")));
        endereco.setNumero(cursor.getString(cursor.getColumnIndex("numero")));
        endereco.setBairro(cursor.getString(cursor.getColumnIndex("bairro")));
        return endereco;
    }

    public Enderecos getByModel(Enderecos enderecos) throws SQLException {
        super.open();
        String where = "logradouro=? AND numero=? AND municipio=? AND uf=? AND bairro=?";
        String[] whereArgs = new String[]{
                enderecos.getLogradouro(),
                enderecos.getNumero(),
                enderecos.getMunicipio(),
                enderecos.getBairro()
        };
        Cursor cursor;
        try {
            cursor = getLiteDatabase().query(getTable(), getColumnsArray(), where, whereArgs, null, null, null);
        } catch (Exception e) {
            onCreate(getLiteDatabase());
            cursor = getLiteDatabase().query(getTable(), getColumnsArray(), where, whereArgs, null, null, null);
        }
        if (cursor.moveToFirst())
            enderecos = convertCursorToEnderecos(cursor);
        else
            throw new SQLException(EnderecoCRUD.class.getEnclosingMethod().getName() + ": cursor is empty");
        super.close();
        return enderecos;
    }

    public boolean exists(final int enderecoId) {
        try {
            return get(enderecoId).get_enderecoId() > 0;
        } catch (SQLException e) {
        }
        return false;
    }

    public boolean exists(final Enderecos enderecos) {
        try {
            return getByModel(enderecos).get_enderecoId() > 0;
        } catch (Exception e) {
        }
        return false;
    }

    public Enderecos get(final int enderecoId) throws SQLException {
        super.open();
        String[] whereArgs = new String[]{String.valueOf(enderecoId)};
        Cursor cursor;
        try {
            cursor = getLiteDatabase().query(getTable(), getColumnsArray(), "_enderecoId=?", whereArgs, null, null, null);
        } catch (Exception e) {
            this.onCreate(getLiteDatabase());
            cursor = getLiteDatabase().query(getTable(), getColumnsArray(), "_enderecoId=?", whereArgs, null, null, null);
        }
        Enderecos enderecos;
        if (cursor.moveToFirst())
            enderecos = convertCursorToEnderecos(cursor);
        else
            throw new SQLException(this.getClass().getEnclosingMethod().getName());
        super.close();
        return enderecos;
    }

    public List<Enderecos> getAll() throws SQLException {
        super.open();
        Cursor cursor = getLiteDatabase().query(getTable(), getColumnsArray(), null, null, null, null, "_enderecoId DESC");
        List<Enderecos> retorno = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                retorno.add(convertCursorToEnderecos(cursor));
            } while (cursor.moveToNext());
        }
        super.close();
        return retorno;
    }
}
