package br.com.infinitsolucoes.infinitvisitas.Controllers;

import android.content.Context;

import java.sql.SQLException;
import java.util.List;

import br.com.infinitsolucoes.infinitvisitas.Business.Data.CRUD.VisitaCRUD;
import br.com.infinitsolucoes.infinitvisitas.Models.Visitas;

public final class OportunidadesController {
    private final Context mContext;
    private final VisitaCRUD mVisitaCRUD;
    private final VisitasController mVisitasController;

    public OportunidadesController(final Context context) {
        this.mContext = context;
        this.mVisitaCRUD = new VisitaCRUD(mContext);
        mVisitasController = new VisitasController(mContext);
    }

    public List<Visitas> getAll() throws SQLException {
        return mVisitaCRUD.getAll();
    }

    public boolean sellByVisita(final Visitas visita) {
        return mVisitasController.sellByVisita(visita);
    }
}
