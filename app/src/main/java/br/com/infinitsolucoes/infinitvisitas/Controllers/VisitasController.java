package br.com.infinitsolucoes.infinitvisitas.Controllers;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import br.com.infinitsolucoes.infinitvisitas.Business.Data.CRUD.VendaCRUD;
import br.com.infinitsolucoes.infinitvisitas.Business.Data.CRUD.VisitaCRUD;
import br.com.infinitsolucoes.infinitvisitas.Models.Visitas;

public final class VisitasController {
    private static final String TAG = "VisitasController";
    private final Context mContext;
    private final VisitaCRUD mCrud;
    private final VendaCRUD mVendaCrud;

    public VisitasController(final Context mContext) {
        this.mContext = mContext;
        mCrud = new VisitaCRUD(mContext);
        mVendaCrud = new VendaCRUD(mContext);
    }

    public List<Visitas> getAllVisitas() {
        return new ArrayList<>();
    }

    public Boolean save(final Visitas visita) {
        try {
            return mCrud.save(visita).get_visitaId() > 0;
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return false;
    }

    public Boolean sellByVisita(final Visitas visita) {
        try {
            return mVendaCrud.save(visita).get_vendaId() > 0;
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return false;
    }
}
