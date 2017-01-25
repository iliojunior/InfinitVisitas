package br.com.infinitsolucoes.infinitvisitas.Controllers;

import android.content.Context;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.List;

import br.com.infinitsolucoes.infinitvisitas.Business.Data.CRUD.VendaCRUD;
import br.com.infinitsolucoes.infinitvisitas.Models.Vendas;
import br.com.infinitsolucoes.infinitvisitas.Models.Visitas;

public final class VendasController {
    private final Context mContext;
    private final VendaCRUD mCrud;

    public VendasController(final Context mContext) {
        this.mContext = mContext;
        mCrud = new VendaCRUD(mContext);
    }

    public List<Vendas> getAll() throws SQLException {
        return mCrud.getAll();
    }

    public boolean save(final Visitas visita) throws InvocationTargetException, SQLException, IllegalAccessException, NoSuchMethodException {
        return mCrud.save(visita).get_vendaId() > 0;
    }
}
