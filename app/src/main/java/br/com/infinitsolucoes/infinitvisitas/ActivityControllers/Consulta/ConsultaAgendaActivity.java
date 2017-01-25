package br.com.infinitsolucoes.infinitvisitas.ActivityControllers.Consulta;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import com.github.clans.fab.FloatingActionMenu;

import java.sql.SQLException;
import java.util.List;

import br.com.infinitsolucoes.infinitvisitas.ActivityControllers.Cadastro.CadastroVisitasActivity;
import br.com.infinitsolucoes.infinitvisitas.ActivityControllers.InfinitAppCompatActivity;
import br.com.infinitsolucoes.infinitvisitas.Adapters.AgendaCustomList;
import br.com.infinitsolucoes.infinitvisitas.Business.Data.CRUD.VendaCRUD;
import br.com.infinitsolucoes.infinitvisitas.Business.Data.CRUD.VisitaCRUD;
import br.com.infinitsolucoes.infinitvisitas.Controllers.AgendaController;
import br.com.infinitsolucoes.infinitvisitas.Finds.FindAgendaByEmpresaName;
import br.com.infinitsolucoes.infinitvisitas.Finds.FindVendaByEmpresaId;
import br.com.infinitsolucoes.infinitvisitas.Models.Visitas;
import br.com.infinitsolucoes.infinitvisitas.R;
import br.com.infinitsolucoes.infinitvisitas.Utils.AsyncTasks.InfinitGetAsyncTask;

import static br.com.infinitsolucoes.infinitvisitas.Utils.Utils.EXTRA_VISITA_ID;

public class ConsultaAgendaActivity extends InfinitAppCompatActivity {

    private final String TAG = "ConsultaAgendaActivity";
    private List<Visitas> visitasList;
    private final AgendaController mController = new AgendaController(ConsultaAgendaActivity.this);
    private final SearchView.OnQueryTextListener textListener = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String query) {
            new GetAsync(ConsultaAgendaActivity.this)
                    .execute(query);
            return false;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            new GetAsync(ConsultaAgendaActivity.this)
                    .execute(newText);
            return true;
        }
    };

    @Override
    protected DrawerLayout mGetDrawerLayout() {
        return (DrawerLayout) findViewById(R.id.drawer_layout_agenda);
    }

    @Override
    protected FloatingActionMenu mGetActionMenu() {
        return (FloatingActionMenu) findViewById(R.id.floating_action_menu);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_consulta_agenda);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void initializeComponents() {
        setOnEmptyButtonClickListener(v -> new GetAllVisitasAsync(ConsultaAgendaActivity.this).execute());
        super.initializeComponents();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId() == getListView().getId()) {
            final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
            final Visitas visita = visitasList.get(info.position);

            menu.setHeaderTitle("Agenda");
            getMenuInflater().inflate(R.menu.context_menu_visitas, menu);


            final List<Integer> ids = new VendaCRUD(ConsultaAgendaActivity.this).findIds(
                    new FindVendaByEmpresaId(visita.getEmpresaId())
            );

            if (!ids.isEmpty()) {
                menu.removeItem(R.id.sell_item);
            }
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.sell_item:
                venderByVisita(info.position);
                return true;
            case R.id.edit_item:
                editarVisita(info.position);
                return true;
            case R.id.delete_item:
                deletarVisita(info.position);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    public final void initializeValues() {
        new GetAllVisitasAsync(ConsultaAgendaActivity.this).execute();
        getSupportActionBar().setTitle(R.string.title_activity_agenda);
        registerForContextMenu(getListView());
        super.initializeValues();
    }

    @Override
    public final void initializeListeners() {
        super.initializeListeners();
        setOnQueryTextListener(textListener);
        getListView().setOnItemClickListener((parent, view, position, id) -> getListView().showContextMenuForChild(view));
        getSwipeRefreshLayout().setOnRefreshListener(this::refreshDataSource);
    }

    private void editarVisita(int position) {
        final Intent intent = new Intent(ConsultaAgendaActivity.this, CadastroVisitasActivity.class);
        intent.putExtra(EXTRA_VISITA_ID,
                visitasList.get(position)
                        .get_visitaId());
        startActivityForResult(intent, 0);
    }

    private void deletarVisita(int position) {
        new DeleteAsync(ConsultaAgendaActivity.this)
                .execute(visitasList.get(position));
    }

    private void venderByVisita(int position) {
        if (mController.sellByVisita(visitasList.get(position))) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(ConsultaAgendaActivity.this);
            builder.setTitle("Vendas");
            builder.setMessage("Vendido com sucesso!");
            builder.setPositiveButton("OK", (a, b) -> refreshDataSource());
            builder.create().show();
        }
    }

    private void refreshDataSource() {
        new GetAllVisitasAsync(ConsultaAgendaActivity.this).execute();
    }

    private class GetAllVisitasAsync extends AsyncTask<Void, Void, List<Visitas>> {
        private final ProgressDialog progressDialog;
        private final AlertDialog.Builder alertBuilder;
        private final Context context;

        public GetAllVisitasAsync(Context context) {
            progressDialog = new ProgressDialog(context);
            alertBuilder = new AlertDialog.Builder(context);
            this.context = context;
        }

        @Override
        protected List<Visitas> doInBackground(Void... params) {
            try {
                return new AgendaController(context).getAll();
            } catch (SQLException e) {
                Log.e(TAG, e.getMessage(), e);
            }
            return null;
        }

        @Override
        protected void onCancelled() {
            progressDialog.dismiss();

            alertBuilder.setTitle("Agenda");
            alertBuilder.setMessage("Cancelado pelo usuário.");
            alertBuilder.setPositiveButton("OK", null);
            alertBuilder.create().show();

            if (getSwipeRefreshLayout().isRefreshing())
                getSwipeRefreshLayout().setRefreshing(false);
        }

        @Override
        protected void onPostExecute(List<Visitas> visitases) {
            progressDialog.dismiss();

            if (visitases == null) {
                alertBuilder.setTitle("Agenda");
                alertBuilder.setMessage("Ocorreu um erro interno.");
                alertBuilder.setPositiveButton("OK", null);
                alertBuilder.create().show();
            }

            final AgendaCustomList listAdapter = new AgendaCustomList(ConsultaAgendaActivity.this, R.layout.list_item_agenda, visitases);
            listAdapter.sort(Visitas.SortAgenda);
            getListView().setAdapter(listAdapter);
            visitasList = visitases;

            if (getSwipeRefreshLayout().isRefreshing())
                getSwipeRefreshLayout().setRefreshing(false);
        }

        @Override
        protected void onPreExecute() {
            progressDialog.setTitle("Agenda");
            progressDialog.setMessage("Obtendo dados...");
            progressDialog.setIndeterminate(true);
            progressDialog.show();
        }
    }

    private class DeleteAsync extends AsyncTask<Visitas, Void, Boolean> {
        private final Context mContext;
        private final AlertDialog.Builder mBuilder;
        private final ProgressDialog mProgressDialog;

        public DeleteAsync(final Context mContext) {
            this.mContext = mContext;
            this.mBuilder = new AlertDialog.Builder(mContext);
            this.mProgressDialog = new ProgressDialog(mContext);
        }

        @Override
        protected Boolean doInBackground(Visitas... params) {
            try {
                return new VisitaCRUD(mContext).delete(params[0]);
            } catch (SQLException e) {
                return false;
            }
        }

        @Override
        protected void onCancelled() {
            mProgressDialog.dismiss();

            mBuilder.setTitle("Agenda");
            mBuilder.setMessage("Cancelado pelo usuário");
            mBuilder.setPositiveButton("OK", null);
            mBuilder.create().show();
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            mProgressDialog.dismiss();

            String title = "Falha";
            String message = "Tente novamente.";

            if (aBoolean != null && aBoolean) {
                title = "Sucesso";
                message = "Apagado com sucesso!";
            }

            mBuilder.setTitle(title);
            mBuilder.setMessage(message);
            mBuilder.setPositiveButton("OK", null);
            mBuilder.create().show();

            refreshDataSource();
        }

        @Override
        protected void onPreExecute() {
            mProgressDialog.setTitle("Visita");
            mProgressDialog.setMessage("Apagando visita...");
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.show();
        }
    }

    private class GetAsync extends InfinitGetAsyncTask<String, Void, List<Visitas>> {

        public GetAsync(Context context) {
            super(context, getString(R.string.title_activity_agenda));
        }

        @Override
        protected void onAsyncFinish(List<Visitas> visitases) {
            final AgendaCustomList adapter = new AgendaCustomList(getContext(), R.layout.list_item_agenda, visitases);
            adapter.sort(Visitas.SortAgenda);
            getListView().setAdapter(adapter);

            visitasList = visitases;

            if (getSwipeRefreshLayout().isRefreshing())
                getSwipeRefreshLayout().setRefreshing(false);
        }

        @Override
        protected List<Visitas> doInBackground(String... params) {
            return new VisitaCRUD(getContext())
                    .findToday(
                            new FindAgendaByEmpresaName(params[0], getContext())
                    );
        }
    }
}
