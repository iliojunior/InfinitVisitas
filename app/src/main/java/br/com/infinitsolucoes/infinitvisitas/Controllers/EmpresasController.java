package br.com.infinitsolucoes.infinitvisitas.Controllers;

import android.content.Context;

import java.util.List;

import br.com.infinitsolucoes.infinitvisitas.Business.Data.CRUD.EmpresaCRUD;
import br.com.infinitsolucoes.infinitvisitas.Models.Empresas;

public final class EmpresasController {
    private final Context mContext;
    private final EmpresaCRUD mEmpresaCRUD;

    public EmpresasController(final Context mContext) {
        this.mContext = mContext;
        mEmpresaCRUD = new EmpresaCRUD(mContext);
    }

    public List<Empresas> get(final String search) {
        return mEmpresaCRUD.get(search);
    }
}
