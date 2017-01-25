package br.com.infinitsolucoes.infinitvisitas.Business.Data.CRUD;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import br.com.infinitsolucoes.infinitvisitas.Business.AnnotationUtils;
import br.com.infinitsolucoes.infinitvisitas.Business.Data.InfinitSQLiteOpenHelper;
import br.com.infinitsolucoes.infinitvisitas.Business.Enumerador;
import br.com.infinitsolucoes.infinitvisitas.Business.Enumerador.ResponseCode;
import br.com.infinitsolucoes.infinitvisitas.Interfaces.Findable;
import br.com.infinitsolucoes.infinitvisitas.Models.ColumnsDB;
import br.com.infinitsolucoes.infinitvisitas.Models.Empresas;
import br.com.infinitsolucoes.infinitvisitas.Models.Enderecos;

public final class EmpresaCRUD extends InfinitSQLiteOpenHelper {

    private static final String TAG = "EmpresaCRUD";

    public EmpresaCRUD(Context context) {
        super(context);
    }

    @NotNull
    @Override
    protected String onGetTable() {
        return "empresas";
    }

    @NotNull
    @Override
    public List<ColumnsDB> onGetColumns() {
        List<ColumnsDB> colunas = new ArrayList<>();
        try {
            colunas = AnnotationUtils.getColumns(Empresas.class);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return colunas;
    }

    private Method getMethod(Class<?> aClass, String nMethod) throws NoSuchMethodException {
        for (Method method : aClass.getDeclaredMethods()) {
            if (method.getName().equalsIgnoreCase(nMethod))
                return method;
        }
        throw new NoSuchMethodException(nMethod);
    }

    public Empresas save(Empresas empresa) throws SQLException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        int novoId = 0;
        final EnderecoCRUD enderecoCRUD = new EnderecoCRUD(getContext());
        if (empresa.getEnderecoId() <= 0 && !enderecoCRUD.exists(empresa.getEndereco()))
            empresa.setEndereco(enderecoCRUD.save(empresa.getEndereco()));

        novoId = innerSave(empresa);

        if (!(empresa.get_empresaId() > 0) && novoId > 0) {
            final Empresas novaEmpresa = new Empresas(novoId, empresa.getNome(), empresa.getEndereco());
            novaEmpresa.setTelefone(empresa.getTelefone());
            novaEmpresa.setCelular(empresa.getCelular());
            novaEmpresa.setResponsavel(empresa.getResponsavel());
            novaEmpresa.setEmail(empresa.getEmail());
            empresa = novaEmpresa;
        }

        return empresa;
    }

    private Empresas convertCursorToEmpresas(final Cursor cursor) throws SQLException {
        final int enderecoId = cursor.getInt(cursor.getColumnIndex("enderecoId"));
        final Enderecos endereco = new EnderecoCRUD(getContext()).get(enderecoId);
        Empresas empresa = new Empresas(
                cursor.getInt(cursor.getColumnIndex("_empresaId")),
                cursor.getString(cursor.getColumnIndex("nome")),
                endereco);
        empresa.setTelefone(cursor.getString(cursor.getColumnIndex("telefone")));
        empresa.setCelular(cursor.getString(cursor.getColumnIndex("celular")));
        empresa.setResponsavel(cursor.getString(cursor.getColumnIndex("responsavel")));
        empresa.setEmail(cursor.getString(cursor.getColumnIndex("email")));
        empresa.setNivelInteresse(cursor.getInt(cursor.getColumnIndex("nivelInteresse")));
        empresa.setCpf_cnpj(cursor.getString(cursor.getColumnIndex("cpf_cnpj")));
        return empresa;
    }

    public boolean exists(final int empresaId) {
        try {
            return get(empresaId).get_empresaId() > 0;
        } catch (SQLException e) {
        }
        return false;
    }

    public boolean exists(final Empresas empresa) {
        try {
            return getByModel(empresa).get_empresaId() > 0;
        } catch (Exception e) {
        }
        return false;
    }

    public Empresas get(final int empresaId) throws SQLException {
        super.open();
        String[] whereArgs = new String[]{String.valueOf(empresaId)};
        Cursor cursor;
        try {
            cursor = getLiteDatabase().query(getTable(), getColumnsArray(), "_empresaId=?", whereArgs, null, null, null);
        } catch (Exception e) {
            this.onCreate(getLiteDatabase());
            cursor = getLiteDatabase().query(getTable(), getColumnsArray(), "_empresaId=?", whereArgs, null, null, null);
        }
        Empresas empresas;
        if (cursor.moveToFirst())
            empresas = convertCursorToEmpresas(cursor);
        else
            throw new SQLException("Cursor is empty!");
        super.close();
        return empresas;
    }

    public List<Empresas> get(final String pesquisa, final boolean qualificados) {
        super.open();
        final String whereClause = qualificados ? "nome LIKE ? AND nivelInteresse > 0" : "nome LIKE ? AND nivelInteresse = 0";
        final String[] whereArgs = new String[]{pesquisa + "%"};
        Cursor cursor;
        try {
            cursor = getLiteDatabase().query(getTable(), getColumnsArray(), whereClause, whereArgs, null, null, null);
        } catch (Exception e) {
            onCreate(getLiteDatabase());
            cursor = getLiteDatabase().query(getTable(), getColumnsArray(), whereClause, whereArgs, null, null, null);
        }
        final List<Empresas> empresasList = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                try {
                    empresasList.add(convertCursorToEmpresas(cursor));
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } while (cursor.moveToNext());
        }
        return empresasList;
    }

    public List<Empresas> get(String pesquisa) {
        super.open();
        String whereClause = "nome LIKE ? OR nome LIKE ?";
        String[] whereArgs = new String[]{pesquisa + "%",
                " " + pesquisa + "%"};
        if (pesquisa == null || pesquisa.isEmpty()) {
            whereClause = null;
            whereArgs = null;
        }
        Cursor cursor;
        try {
            cursor = getLiteDatabase().query(getTable(), getColumnsArray(), whereClause, whereArgs, null, null, null);
        } catch (Exception e) {
            onCreate(getLiteDatabase());
            cursor = getLiteDatabase().query(getTable(), getColumnsArray(), whereClause, whereArgs, null, null, null);
        }
        final List<Empresas> empresasList = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                try {
                    empresasList.add(convertCursorToEmpresas(cursor));
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } while (cursor.moveToNext());
        }
        return empresasList;
    }

    public Empresas getByModel(Empresas empresas) throws SQLException {
        super.open();
        String whereClause = "nome=? AND telefone=? AND celular=? AND responsavel=? AND email=? AND enderecoId=? AND nivelInteresse=? AND cpf_cnpj=?";
        String[] whereArgs = new String[]{
                empresas.getNome(),
                empresas.getTelefone(),
                empresas.getCelular(),
                empresas.getResponsavel(),
                empresas.getEmail(),
                String.valueOf(empresas.getEnderecoId()),
                String.valueOf(empresas.getNivelInteresse()),
                empresas.getCpf_cnpj()
        };
        Cursor cursor;
        try {
            cursor = getLiteDatabase().query(getTable(), getColumnsArray(), whereClause, whereArgs, null, null, null);
        } catch (Exception e) {
            onCreate(getLiteDatabase());
            cursor = getLiteDatabase().query(getTable(), getColumnsArray(), whereClause, whereArgs, null, null, null);
        }
        if (cursor.moveToFirst())
            empresas = convertCursorToEmpresas(cursor);
        else
            throw new SQLException("Cursor is empty!");
        super.close();
        return empresas;
    }

    public List<Empresas> getAll() throws SQLException {
        super.open();
        Cursor cursor;
        try {
            cursor = getLiteDatabase().query(getTable(), getColumnsArray(), null, null, null, null, null);
        } catch (SQLiteException e) {
            this.onCreate(getLiteDatabase());
            cursor = getLiteDatabase().query(getTable(), getColumnsArray(), null, null, null, null, null);
        }

        final List<Empresas> retorno = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                retorno.add(convertCursorToEmpresas(cursor));
            } while (cursor.moveToNext());
        }
        super.close();
        return retorno;
    }

    public List<Empresas> getAll(boolean isQualificados) throws SQLException {
        super.open();
        String whereClause = "nivelInteresse=?";
        if (isQualificados)
            whereClause = "nivelInteresse>?";
        String[] whereArgs = new String[]{"0"};
        Cursor cursor;

        try {
            cursor = getLiteDatabase().query(getTable(), getColumnsArray(), whereClause, whereArgs, null, null, "nivelInteresse, _empresaId DESC");
        } catch (SQLiteException e) {
            this.onCreate(getLiteDatabase());
            cursor = getLiteDatabase().query(getTable(), getColumnsArray(), whereClause, whereArgs, null, null, "nivelInteresse, _empresaId DESC");
        }

        List<Empresas> retorno = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                retorno.add(convertCursorToEmpresas(cursor));
            } while (cursor.moveToNext());
        }
        super.close();
        return retorno;
    }

    public Enumerador.ResponseCode delete(Empresas empresa) throws SQLException {
        if (isUsed(empresa))
            return ResponseCode.ERROR;

        super.open();
        int result = getLiteDatabase().delete(getTable(), "_empresaId=?", new String[]{String.valueOf(empresa.get_empresaId())});
        super.close();
        return ResponseCode.getResponse(result > 0);
    }

    public List<Empresas> find(final Findable pesquisa) {
        final List<Empresas> retorno = new ArrayList<>();
        super.open();
        try {
            final Cursor cursor = getLiteDatabase().query(getTable(),
                    getColumnsArray(),
                    pesquisa.getWhereClause(),
                    pesquisa.getWhereArgs(),
                    pesquisa.getGroupByClause(),
                    pesquisa.getHavingClause(),
                    pesquisa.getGroupByClause());
            
            while (cursor.moveToNext())
                retorno.add(convertCursorToEmpresas(cursor));
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
        super.close();
        return retorno;
    }

    @Override
    protected String getConstraint() {
        return "FOREIGN KEY(enderecoId) REFERENCES endereco(_enderecoId)";
    }

    public boolean isUsed(final Empresas empresa) {
        int rows = 0;
        final VendaCRUD vendaCRUD = new VendaCRUD(getContext());
        final VisitaCRUD visitaCRUD = new VisitaCRUD(getContext());
        final FollowUpCRUD followUpCRUD = new FollowUpCRUD(getContext());
        final String[] whereArgs = new String[]{
                String.valueOf(empresa.get_empresaId())
        };

        rows += getCountQuery(vendaCRUD.getTable(), "empresaId=?", whereArgs);
        rows += getCountQuery(followUpCRUD.getTable(), "empresaId=?", whereArgs);
        rows += getCountQuery(visitaCRUD.getTable(), "empresaId=?", whereArgs);

        return rows > 0;
    }

    public boolean isUsed(final int _empresaId) {
        try {
            return isUsed(get(_empresaId));
        } catch (SQLException e) {
            Log.e(TAG, e.getMessage(), e);
            return false;
        }
    }

}
