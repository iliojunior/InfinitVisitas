package br.com.infinitsolucoes.infinitvisitas.Business.Data.CRUD;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import br.com.infinitsolucoes.infinitvisitas.Business.AnnotationUtils;
import br.com.infinitsolucoes.infinitvisitas.Business.Data.InfinitSQLiteOpenHelper;
import br.com.infinitsolucoes.infinitvisitas.Interfaces.Findable;
import br.com.infinitsolucoes.infinitvisitas.Models.ColumnsDB;
import br.com.infinitsolucoes.infinitvisitas.Models.Empresas;
import br.com.infinitsolucoes.infinitvisitas.Models.Visitas;

public final class VisitaCRUD extends InfinitSQLiteOpenHelper {
    private static final String TAG = "VisitaCRUD";


    public VisitaCRUD(Context context) {
        super(context);
    }

    @NotNull
    @Override
    protected String onGetTable() {
        return "visitas";
    }

    @NotNull
    @Override
    protected List<ColumnsDB> onGetColumns() {
        List<ColumnsDB> columnsDBList = new ArrayList<>();
        try {
            columnsDBList = AnnotationUtils.getColumns(Visitas.class);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return columnsDBList;
    }

    @Override
    protected String getConstraint() {
        return "FOREIGN KEY(empresaId) REFERENCES empresas(_empresaId)";
    }

    private Visitas convertCursorToVisitas(Cursor cursor) throws SQLException {
        final int empresaId = cursor.getInt(cursor.getColumnIndex("empresaId"));
        final Empresas empresa = new EmpresaCRUD(getContext()).get(empresaId);
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Visitas visita = new Visitas(cursor.getInt(cursor.getColumnIndex("_visitaId")), empresa);
        try {
            final Calendar calendar = Calendar.getInstance();
            calendar.setTime(simpleDateFormat.parse(cursor.getString(cursor.getColumnIndex("data"))));
            visita.setData(calendar);
        } catch (ParseException e) {
            Log.e(TAG, e.getMessage(), e);
            visita.setData(Calendar.getInstance());
        }
        visita.setObservacao(cursor.getString(cursor.getColumnIndex("observacao")));
        return visita;
    }

    public Visitas save(Visitas visita) throws InvocationTargetException, SQLException, IllegalAccessException, NoSuchMethodException {
        int novoId = 0;
        final EmpresaCRUD empresaCRUD = new EmpresaCRUD(getContext());
        if (visita.getEmpresaId() <= 0 && !empresaCRUD.exists(visita.getEmpresa()))
            visita.setEmpresa(empresaCRUD.save(visita.getEmpresa()));

        novoId = innerSave(visita);

        if (!(visita.get_visitaId() > 0) && novoId > 0) {
            final Visitas novaVisita = new Visitas(novoId, visita.getEmpresa());
            novaVisita.setObservacao(visita.getObservacao());
            novaVisita.setData(visita.getData());
            visita = novaVisita;
        }
        return visita;
    }

    public List<Visitas> getAll() throws SQLException {
        super.open();
        Cursor cursor;
        try {
            cursor = getLiteDatabase().query(getTable(), getColumnsArray(), null, null, null, null, null);
        } catch (SQLiteException e) {
            this.onCreate(getLiteDatabase());
            cursor = getLiteDatabase().query(getTable(), getColumnsArray(), null, null, null, null, null);
        }

        List<Visitas> retorno = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                retorno.add(convertCursorToVisitas(cursor));
            } while (cursor.moveToNext());
        }
        super.close();
        return retorno;
    }

    public Visitas get(final int visitaId) throws SQLException {
        super.open();
        String[] whereArgs = new String[]{String.valueOf(visitaId)};
        Cursor cursor;
        try {
            cursor = getLiteDatabase().query(getTable(), getColumnsArray(), "_visitaId=?", whereArgs, null, null, null);
        } catch (Exception e) {
            this.onCreate(getLiteDatabase());
            cursor = getLiteDatabase().query(getTable(), getColumnsArray(), "_visitaId=?", whereArgs, null, null, null);
        }
        Visitas visita;
        if (cursor.moveToFirst())
            visita = convertCursorToVisitas(cursor);
        else
            throw new SQLiteException(this.getClass().getEnclosingMethod().getName() + ": Cursor is empty!");

        super.close();
        return visita;
    }

    public Visitas get(Visitas visita) throws SQLException {
        super.open();
        String whereClause = "observacao=? AND data=? AND empresaId=?";
        final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        String[] whereArgs = new String[]{
                visita.getObservacao(),
                dateFormat.format(visita.getData()),
                String.valueOf(visita.getEmpresaId())
        };
        Cursor cursor;
        try {
            cursor = getLiteDatabase().query(getTable(), getColumnsArray(), whereClause, whereArgs, null, null, null);
        } catch (Exception e) {
            onCreate(getLiteDatabase());
            cursor = getLiteDatabase().query(getTable(), getColumnsArray(), whereClause, whereArgs, null, null, null);
        }

        if (cursor.moveToFirst())
            visita = convertCursorToVisitas(cursor);
        else
            throw new SQLException(this.getClass().getEnclosingMethod().getName() + ": Cursor is empty!");

        super.close();
        return visita;
    }

    public boolean exists(final int visitaId) {
        try {
            return get(visitaId).get_visitaId() > 0;
        } catch (SQLException e) {
        }
        return false;
    }

    public boolean exists(final Visitas visitas) {
        try {
            return get(visitas).get_visitaId() > 0;
        } catch (SQLException e) {
        }
        return false;
    }

    public boolean delete(Visitas visitas) throws SQLException {
        super.open();
        final int result = getLiteDatabase().delete(getTable(), "_visitaId=?", new String[]{String.valueOf(visitas.get_visitaId())});
        super.close();
        return result > 0;
    }

    public List<Visitas> find(final Findable pesquisa) {
        final List<Visitas> visitasList = new ArrayList<>();
        super.open();
        try {
            Cursor cursor = getLiteDatabase().query(getTable(),
                    getColumnsArray(),
                    pesquisa.getWhereClause(),
                    pesquisa.getWhereArgs(),
                    pesquisa.getGroupByClause(),
                    pesquisa.getHavingClause(),
                    pesquisa.getOrderByClause());

            while (cursor.moveToNext()) {
                visitasList.add(convertCursorToVisitas(cursor));
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
        super.close();
        return visitasList;
    }

    public List<Visitas> findToday(final Findable pesquisa) {
        final Calendar today = Calendar.getInstance();
        final Calendar tomorrow = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        tomorrow.set(Calendar.DAY_OF_MONTH, today.get(Calendar.DAY_OF_MONTH) + 1);
        tomorrow.set(Calendar.HOUR_OF_DAY, 0);
        tomorrow.set(Calendar.MINUTE, 0);
        final List<Visitas> listAgenda = new ArrayList<>();
        for (Visitas visita : find(pesquisa)) {
            if (visita.getData().after(today)
                    && visita.getData().before(tomorrow))
                listAgenda.add(visita);
        }
        return listAgenda;
    }

    public List<Visitas> getToday() throws SQLException {
        final Calendar today = Calendar.getInstance();
        final Calendar tomorrow = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        tomorrow.set(Calendar.DAY_OF_MONTH, today.get(Calendar.DAY_OF_MONTH) + 1);
        tomorrow.set(Calendar.HOUR_OF_DAY, 0);
        tomorrow.set(Calendar.MINUTE, 0);
        super.open();

        final List<Visitas> listAgenda = new ArrayList<>();
        for (Visitas visita : getAll()) {
            if (visita.getData().after(today)
                    && visita.getData().before(tomorrow))
                listAgenda.add(visita);
        }
        return listAgenda;
    }
}
