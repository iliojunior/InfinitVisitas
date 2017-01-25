package br.com.infinitsolucoes.infinitvisitas.Controllers;

import android.content.Context;

import java.sql.SQLException;
import java.util.List;

import br.com.infinitsolucoes.infinitvisitas.Business.Data.CRUD.EmpresaCRUD;
import br.com.infinitsolucoes.infinitvisitas.Models.Empresas;

public final class ContatosController {
    private final Context mContext;
    private final EmpresaCRUD mCrud;

    public ContatosController(final Context mContext){
        this.mContext = mContext;
        mCrud = new EmpresaCRUD(mContext);
    }

    public List<Empresas> getAll() throws SQLException {
        return mCrud.getAll();
    }
}
