package br.com.infinitsolucoes.infinitvisitas.Business.Data.CRUD;

import android.content.Context;
import android.database.Cursor;
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
import br.com.infinitsolucoes.infinitvisitas.Models.ColumnsDB;
import br.com.infinitsolucoes.infinitvisitas.Models.Empresas;
import br.com.infinitsolucoes.infinitvisitas.Models.FollowUp;

public final class FollowUpCRUD extends InfinitSQLiteOpenHelper {
    private static final String TAG = "FollowUpCRUD";

    public FollowUpCRUD(Context context) {
        super(context);
    }

    @NotNull
    @Override
    public String onGetTable() {
        return "followup";
    }

    @NotNull
    @Override
    protected List<ColumnsDB> onGetColumns() {
        List<ColumnsDB> columnsDBsList = new ArrayList<>();
        try {
            columnsDBsList = AnnotationUtils.getColumns(FollowUp.class);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return columnsDBsList;
    }

    private FollowUp convertCursorToFollowUp(Cursor cursor) throws SQLException {
        final int empresaId = cursor.getInt(cursor.getColumnIndex("empresaId"));
        final Empresas empresa = new EmpresaCRUD(getContext()).get(empresaId);
        final FollowUp followUp = new FollowUp(cursor.getInt(cursor.getColumnIndex("_followUpId")), empresa);
        try {
            final Calendar calendar = Calendar.getInstance();
            calendar.setTime(getSimpleDateFormat().parse(cursor.getString(cursor.getColumnIndex("data"))));
            followUp.setData(calendar);
        } catch (ParseException e) {
            Log.e(TAG, e.getMessage(), e);
            followUp.setData(Calendar.getInstance());
        }
        followUp.setRetornoTempo(cursor.getInt(cursor.getColumnIndex("retornoTempo")));
        followUp.setRetornoMedida(cursor.getString(cursor.getColumnIndex("retornoMedida")));
        followUp.setNaoRetornarMais(cursor.getInt(cursor.getColumnIndex("naoRetornarMais")) > 0 ? true : false);
        followUp.setNivelInteresse(cursor.getInt(cursor.getColumnIndex("nivelInteresse")));
        followUp.setObservacao(cursor.getString(cursor.getColumnIndex("observacao")));
        return followUp;
    }

    public FollowUp save(FollowUp followUp) throws InvocationTargetException, SQLException, IllegalAccessException, NoSuchMethodException {
        followUp.setData(Calendar.getInstance());
        final int novoId = innerSave(followUp);

        if (followUp.get_followUpId() <= 0 && novoId > 0) {
            final FollowUp novoFollowUp = new FollowUp(novoId, followUp.getEmpresa());
            novoFollowUp.setData(followUp.getData());
            novoFollowUp.setRetornoTempo(followUp.getRetornoTempo());
            novoFollowUp.setRetornoMedida(followUp.getRetornoMedida());
            novoFollowUp.setNaoRetornarMais(followUp.isNaoRetornarMais());
            novoFollowUp.setObservacao(followUp.getObservacao());
            novoFollowUp.setNivelInteresse(followUp.getNivelInteresse());
            followUp = novoFollowUp;
        }

        return followUp;
    }

    public FollowUp get(final int followUpId) throws SQLException {
        super.open();
        final String[] whereArgs = new String[]{String.valueOf(followUpId)};
        Cursor cursor;
        try {
            cursor = getLiteDatabase().query(getTable(), getColumnsArray(), "_followUpId=?", whereArgs, null, null, null);
        } catch (Exception e) {
            this.onCreate(getLiteDatabase());
            cursor = getLiteDatabase().query(getTable(), getColumnsArray(), "_followUpId=?", whereArgs, null, null, null);
        }
        final FollowUp followUp;
        if (cursor.moveToFirst())
            followUp = convertCursorToFollowUp(cursor);
        else
            throw new SQLException(this.getClass().getEnclosingMethod().getName() + ": Cursor is empty!");

        super.close();
        return followUp;
    }

    public List<FollowUp> getAll(final int empresaId) throws SQLException {
        super.open();
        final String[] whereArgs = new String[]{String.valueOf(empresaId)};
        Cursor cursor;
        try {
            cursor = getLiteDatabase().query(getTable(), getColumnsArray(), "empresaId=?", whereArgs, null, null, null);
        } catch (Exception e) {
            onCreate(getLiteDatabase());
            cursor = getLiteDatabase().query(getTable(), getColumnsArray(), "empresaId=?", whereArgs, null, null, null);
        }

        final List<FollowUp> retorno = new ArrayList<>();
        while (cursor.moveToNext()) {
            retorno.add(convertCursorToFollowUp(cursor));
        }

        super.close();
        return retorno;
    }

    public boolean delete(final FollowUp followUp) throws SQLException {
        super.open();
        final int result = getLiteDatabase().delete(getTable(), "_followUpId=?", new String[]{String.valueOf(followUp.get_followUpId())});
        super.close();
        return result > 0;
    }
}
