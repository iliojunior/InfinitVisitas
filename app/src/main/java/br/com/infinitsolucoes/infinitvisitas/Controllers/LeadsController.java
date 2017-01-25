package br.com.infinitsolucoes.infinitvisitas.Controllers;

import android.content.Context;

import java.sql.SQLException;
import java.util.List;

import br.com.infinitsolucoes.infinitvisitas.Business.Data.CRUD.EmpresaCRUD;
import br.com.infinitsolucoes.infinitvisitas.Models.Empresas;

public final class LeadsController {
    private final Context context;
    private final EmpresaCRUD empresaCRUD;
    private static final boolean mIsQualifidados = false;


    public LeadsController(final Context context) {
        this.context = context;
        empresaCRUD = new EmpresaCRUD(context);
    }

    public List<Empresas> getAllLeads() throws SQLException {
        return empresaCRUD.getAll(mIsQualifidados);
    }

    public List<Empresas> get(final String search) {
        return empresaCRUD.get(search, mIsQualifidados);
    }
}
