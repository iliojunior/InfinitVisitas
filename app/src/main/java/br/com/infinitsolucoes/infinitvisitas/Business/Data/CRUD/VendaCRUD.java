package br.com.infinitsolucoes.infinitvisitas.Business.Data.CRUD;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import br.com.infinitsolucoes.infinitvisitas.Business.AnnotationUtils;
import br.com.infinitsolucoes.infinitvisitas.Business.Data.InfinitSQLiteOpenHelper;
import br.com.infinitsolucoes.infinitvisitas.Interfaces.Findable;
import br.com.infinitsolucoes.infinitvisitas.Models.ColumnsDB;
import br.com.infinitsolucoes.infinitvisitas.Models.Empresas;
import br.com.infinitsolucoes.infinitvisitas.Models.Vendas;
import br.com.infinitsolucoes.infinitvisitas.Models.Visitas;

public final class VendaCRUD extends InfinitSQLiteOpenHelper {

    private static final String TAG = "VendasCRUD";

    public VendaCRUD(final Context context) {
        super(context);
    }

    @NotNull
    @Override
    protected String onGetTable() {
        return "vendas";
    }

    @NotNull
    @Override
    protected List<ColumnsDB> onGetColumns() {
        List<ColumnsDB> mColumnsDBs = new ArrayList<>();
        try {
            mColumnsDBs = AnnotationUtils.getColumns(Vendas.class);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return mColumnsDBs;
    }

    @Override
    protected String getConstraint() {
        return "FOREIGN KEY(empresaId) REFERENCES empresas(_empresaId)";
    }

    private Vendas convertCursorToVendas(final Cursor cursor) throws SQLException {
        final Empresas empresa = new EmpresaCRUD(getContext())
                .get(cursor.getInt(cursor.getColumnIndex("empresaId")));
        final Vendas venda = new Vendas(cursor.getInt(cursor.getColumnIndex("_vendaId")), empresa);
        final Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTime(getSimpleDateFormat().
                    parse(cursor
                            .getString(cursor
                                    .getColumnIndex("dataVenda"))));
            venda.setDataVenda(calendar);
        } catch (ParseException e) {
            Log.e(TAG, e.getMessage(), e);
            venda.setDataVenda(Calendar.getInstance());
        }
        return venda;
    }

    public Vendas save(Vendas venda) throws InvocationTargetException, SQLException, IllegalAccessException, NoSuchMethodException {
        final int novoId = innerSave(venda, true);

        if (venda.get_vendaId() <= 0 && novoId > 0) {
            final Vendas novaVenda = new Vendas(novoId, venda.getEmpresa());
            novaVenda.setDataVenda(Calendar.getInstance());
            venda = novaVenda;
        }

        return venda;
    }

    public List<Vendas> getAll() throws SQLException {
        super.open();
        Cursor cursor;
        try {
            cursor = getLiteDatabase().query(getTable(), getColumnsArray(), null, null, null, null, null);
        } catch (SQLiteException e) {
            onCreate(getLiteDatabase());
            cursor = getLiteDatabase().query(getTable(), getColumnsArray(), null, null, null, null, null);
        }
        final List<Vendas> resultado = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                resultado.add(convertCursorToVendas(cursor));
            } while (cursor.moveToNext());
        }
        super.close();
        return resultado;
    }

    public Vendas get(final int _vendaId) throws SQLException {
        super.open();
        final String[] whereArgs = new String[]{
                String.valueOf(_vendaId)
        };
        Cursor cursor;
        try {
            cursor = getLiteDatabase().query(getTable(), getColumnsArray(), "_vendaId=?", whereArgs, null, null, null);
        } catch (Exception e) {
            onCreate(getLiteDatabase());
            cursor = getLiteDatabase().query(getTable(), getColumnsArray(), "_vendaId=?", whereArgs, null, null, null);
        }

        final Vendas venda;
        if (cursor.moveToFirst())
            venda = convertCursorToVendas(cursor);
        else
            throw new SQLException(this.getClass().getEnclosingMethod().getName() + ": Cursor is empty!");

        super.close();
        return venda;
    }

    public Vendas get(final Vendas venda) throws SQLException {
        super.open();
        final String whereClause = "empresaId=? AND dataVenda=?";
        final String[] whereArgs = new String[]{
                String.valueOf(venda.getEmpresaId()),
                getSimpleDateFormat().format(venda.getDataVenda().getTime())
        };
        Cursor cursor;
        try {
            cursor = getLiteDatabase().query(getTable(), getColumnsArray(), whereClause, whereArgs, null, null, null);
        } catch (Exception e) {
            onCreate(getLiteDatabase());
            cursor = getLiteDatabase().query(getTable(), getColumnsArray(), whereClause, whereArgs, null, null, null);
        }
        final Vendas resultVenda;
        if (cursor.moveToFirst())
            resultVenda = convertCursorToVendas(cursor);
        else
            throw new SQLException(this.getClass().getEnclosingMethod().getName() + ": Cursor is empty!");

        super.close();
        return resultVenda;
    }

    public boolean exists(final int vendaId) {
        try {
            return get(vendaId).get_vendaId() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean exists(final Vendas venda) {
        try {
            return get(venda).get_vendaId() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean delete(final Vendas venda) {
        super.open();
        final String[] whereArgs = new String[]{String.valueOf(venda.get_vendaId())};
        final int resultado = getLiteDatabase().delete(getTable(), "_vendaId=?", whereArgs);
        super.close();
        return resultado > 0;
    }

    public Vendas save(final Visitas visitas)
            throws SQLException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        final VisitaCRUD visitaCRUD = new VisitaCRUD(getContext());
        final Vendas novaVenda = new Vendas(visitas);
        visitaCRUD.delete(visitas);
        return save(novaVenda);
    }

    public List<Vendas> find(final Findable pesquisa) {
        final List<Vendas> retorno = new ArrayList<>();
        super.open();
        try {
            final Cursor cursor = getLiteDatabase().query(getTable(),
                    getColumnsArray(),
                    pesquisa.getWhereClause(),
                    pesquisa.getWhereArgs(),
                    pesquisa.getGroupByClause(),
                    pesquisa.getHavingClause(),
                    pesquisa.getOrderByClause());

            while (cursor.moveToNext()) {
                retorno.add(convertCursorToVendas(cursor));
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }

        super.close();
        return retorno;
    }
}
