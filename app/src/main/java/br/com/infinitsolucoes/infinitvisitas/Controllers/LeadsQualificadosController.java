package br.com.infinitsolucoes.infinitvisitas.Controllers;

import android.content.Context;

import java.sql.SQLException;
import java.util.List;

import br.com.infinitsolucoes.infinitvisitas.Business.Data.CRUD.EmpresaCRUD;
import br.com.infinitsolucoes.infinitvisitas.Models.Empresas;

public final class LeadsQualificadosController {
    private final Context mContext;
    private final EmpresaCRUD mEmpresaCRUD;
    private static final boolean mIsQualificados = true;

    public LeadsQualificadosController(Context context) {
        this.mContext = context;
        mEmpresaCRUD = new EmpresaCRUD(context);
    }

    public List<Empresas> getAllLeads() throws SQLException {
        return mEmpresaCRUD.getAll(mIsQualificados);
    }

    public List<Empresas> get(final String search) {
        return mEmpresaCRUD.get(search, mIsQualificados);
    }
}
