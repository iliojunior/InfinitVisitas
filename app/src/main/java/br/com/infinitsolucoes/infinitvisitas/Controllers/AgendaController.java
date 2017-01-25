package br.com.infinitsolucoes.infinitvisitas.Controllers;

import android.content.Context;

import java.sql.SQLException;
import java.util.List;

import br.com.infinitsolucoes.infinitvisitas.Business.Data.CRUD.VisitaCRUD;
import br.com.infinitsolucoes.infinitvisitas.Models.Visitas;

public final class AgendaController {
    private final Context mContext;
    private final VisitaCRUD mCrud;
    private final VisitasController mVisitasController;

    public AgendaController(final Context mContext) {
        this.mContext = mContext;
        mCrud = new VisitaCRUD(mContext);
        mVisitasController = new VisitasController(mContext);
    }

    public List<Visitas> getAll() throws SQLException {
        return mCrud.getToday();
    }

    public boolean sellByVisita(final Visitas visita) {
        return mVisitasController.sellByVisita(visita);
    }
}
