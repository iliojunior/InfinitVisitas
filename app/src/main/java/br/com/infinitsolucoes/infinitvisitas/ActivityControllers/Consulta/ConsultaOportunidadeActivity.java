package br.com.infinitsolucoes.infinitvisitas.ActivityControllers.Consulta;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import com.github.clans.fab.FloatingActionMenu;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import br.com.infinitsolucoes.infinitvisitas.ActivityControllers.Cadastro.CadastroVisitasActivity;
import br.com.infinitsolucoes.infinitvisitas.ActivityControllers.InfinitAppCompatActivity;
import br.com.infinitsolucoes.infinitvisitas.Adapters.OportunidadeCustomList;
import br.com.infinitsolucoes.infinitvisitas.Business.Data.CRUD.VendaCRUD;
import br.com.infinitsolucoes.infinitvisitas.Business.Data.CRUD.VisitaCRUD;
import br.com.infinitsolucoes.infinitvisitas.Controllers.OportunidadesController;
import br.com.infinitsolucoes.infinitvisitas.Finds.FindOportunidadeByEmpresaName;
import br.com.infinitsolucoes.infinitvisitas.Finds.FindVendaByEmpresaId;
import br.com.infinitsolucoes.infinitvisitas.Models.Visitas;
import br.com.infinitsolucoes.infinitvisitas.R;
import br.com.infinitsolucoes.infinitvisitas.Utils.AsyncTasks.InfinitGetAsyncTask;

import static br.com.infinitsolucoes.infinitvisitas.Utils.Utils.EXTRA_VISITA_ID;

public class ConsultaOportunidadeActivity extends InfinitAppCompatActivity {
    private static final String TAG = "C.OportunidadeActivity";
    private List<Visitas> visitasList;
    private final SearchView.OnQueryTextListener textListener = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String query) {
            new GetAsync(ConsultaOportunidadeActivity.this).execute(query);
            return false;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            new GetAsync(ConsultaOportunidadeActivity.this).execute(newText);
            return false;
        }
    };

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId() == R.id.lista_consulta) {
            final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
            final Visitas visita = visitasList.get(info.position);
            menu.setHeaderTitle(R.string.title_activity_oportunidade);
            getMenuInflater().inflate(R.menu.context_menu_visitas, menu);

            final List<Integer> ids = new VendaCRUD(ConsultaOportunidadeActivity.this).findIds(
                    new FindVendaByEmpresaId(visita.getEmpresaId())
            );

            if (!ids.isEmpty()) {
                menu.removeItem(R.id.sell_item);
            }
        }
    }

    @Override
    public void initializeComponents() {
        super.initializeComponents();
    }

    @Override
    public void initializeListeners() {
        setOnEmptyButtonClickListener(v -> refreshDataSource());
        getSwipeRefreshLayout().setOnRefreshListener(this::refreshDataSource);
        getListView().setOnItemClickListener((p, v, position, id) -> getListView().showContextMenuForChild(v));
        super.initializeListeners();
        setOnQueryTextListener(textListener);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.sell_item:
                venderByVisita(info.position);
                return true;
            case R.id.edit_item:
                editarOportunidade(info.position);
                return true;
            case R.id.delete_item:
                deletarOportunidade(info.position);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    private void venderByVisita(int position) {
        if (new OportunidadesController(ConsultaOportunidadeActivity.this).sellByVisita(visitasList.get(position))) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(ConsultaOportunidadeActivity.this);
            builder.setTitle("Vendas");
            builder.setMessage("Vendido com sucesso!");
            builder.setPositiveButton("OK", (a, b) -> refreshDataSource());
            builder.create().show();
        }
    }

    @Override
    public void initializeValues() {
        new GetAllAsync(ConsultaOportunidadeActivity.this)
                .execute();
        registerForContextMenu(getListView());
        super.initializeValues();
    }

    @Override
    protected DrawerLayout mGetDrawerLayout() {
        return (DrawerLayout) findViewById(R.id.activity_consulta_oportunidade);
    }

    @Override
    protected FloatingActionMenu mGetActionMenu() {
        return (FloatingActionMenu) findViewById(R.id.floating_action_menu);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_consulta_oportunidade);
        super.onCreate(savedInstanceState);
    }

    private void refreshDataSource() {
        new GetAllAsync(ConsultaOportunidadeActivity.this).execute();
    }

    private final class GetAllAsync extends AsyncTask<Void, Void, List<Visitas>> {
        private final Context context;
        private final ProgressDialog progressDialog;
        private final AlertDialog.Builder builder;
        private OportunidadeCustomList listAdapter;

        public GetAllAsync(final Context context) {
            this.context = context;
            progressDialog = new ProgressDialog(context);
            builder = new AlertDialog.Builder(context);
        }

        @Override
        protected List<Visitas> doInBackground(Void... params) {
            try {
                return new OportunidadesController(context).getAll();
            } catch (Exception e) {
                Log.e(TAG, e.getMessage(), e);
            }
            return null;
        }

        @Override
        protected void onCancelled() {
            progressDialog.dismiss();
            builder.setTitle(R.string.title_activity_oportunidade);
            builder.setMessage("Cancelado pelo usuário");
            builder.setPositiveButton("OK", null);
            builder.create().show();
        }

        @Override
        protected void onPostExecute(List<Visitas> visitases) {
            progressDialog.dismiss();

            if (visitases == null) {
                builder.setTitle(R.string.title_activity_oportunidade);
                builder.setMessage("Ocorreu um erro interno");
                builder.setPositiveButton("OK", null);
                builder.create().show();
                visitases = new ArrayList<>();
            }

            visitasList = visitases;
            listAdapter = new OportunidadeCustomList(context, R.layout.activity_consulta_oportunidade, visitases);
            listAdapter.sort(Visitas.SortOportunidade);
            getListView().setAdapter(listAdapter);

            if (getSwipeRefreshLayout().isRefreshing())
                getSwipeRefreshLayout().setRefreshing(false);
        }

        @Override
        protected void onPreExecute() {
            if (!getSwipeRefreshLayout().isRefreshing()) {
                progressDialog.setTitle(R.string.title_activity_oportunidade);
                progressDialog.setMessage("Obtendo dados.");
                progressDialog.setIndeterminate(true);
                progressDialog.show();
            }
        }
    }

    private void editarOportunidade(int position) {
        final Visitas visita = visitasList.get(position);
        final Intent intent = new Intent(ConsultaOportunidadeActivity.this, CadastroVisitasActivity.class);
        intent.putExtra(EXTRA_VISITA_ID, visita.get_visitaId());
        startActivityForResult(intent, 0);
    }

    private void deletarOportunidade(int position) {
        final Visitas visita = visitasList.get(position);
        final AlertDialog.Builder builder = new AlertDialog.Builder(ConsultaOportunidadeActivity.this);
        builder.setTitle(visita.getEmpresa().getNome());
        builder.setMessage("Deseja apagar a visita?");
        builder.setPositiveButton("Sim", (d, w) -> {
            new DeleteAsync(ConsultaOportunidadeActivity.this).execute(visita);
        });
        builder.setNegativeButton("Não", null);
        builder.create().show();
    }

    private void sellByVisita(int position) {
        new OportunidadesController(ConsultaOportunidadeActivity.this).sellByVisita(visitasList.get(position));
    }

    private final class DeleteAsync extends AsyncTask<Visitas, Void, Boolean> {
        private final Context mContext;
        private final ProgressDialog mProgressDialog;
        private final AlertDialog.Builder mBuilder;

        public DeleteAsync(final Context mContext) {
            this.mContext = mContext;
            this.mProgressDialog = new ProgressDialog(mContext);
            this.mBuilder = new AlertDialog.Builder(mContext);
        }

        @Override
        protected Boolean doInBackground(Visitas... params) {
            try {
                return new VisitaCRUD(mContext)
                        .delete(params[0]);
            } catch (SQLException e) {
                Log.e(TAG, e.getMessage(), e);
                return false;
            }
        }

        @Override
        protected void onCancelled() {
            mProgressDialog.dismiss();

            mBuilder.setTitle("Visita");
            mBuilder.setMessage("Cancelado pelo usuário");
            mBuilder.setPositiveButton("OK", null);
            mBuilder.create().show();
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            mProgressDialog.dismiss();

            String title = "Falha";
            String message = "Tente novamente";

            if (aBoolean != null && aBoolean) {
                title = "Sucesso";
                message = "Apagado com sucesso";
            }

            mBuilder.setTitle(title);
            mBuilder.setMessage(message);
            mBuilder.setPositiveButton("OK", null);
            mBuilder.create().show();
        }

        @Override
        protected void onPreExecute() {
            mProgressDialog.setTitle("Visita");
            mProgressDialog.setMessage("Apagando visita...");
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.show();
        }
    }

    private final class GetAsync extends InfinitGetAsyncTask<String, Void, List<Visitas>> {

        public GetAsync(Context context) {
            super(context, getString(R.string.title_activity_oportunidade));
        }

        @Override
        protected void onAsyncFinish(List<Visitas> visitases) {

        }

        @Override
        protected List<Visitas> doInBackground(String... params) {
            return new VisitaCRUD(getContext())
                    .find(
                            new FindOportunidadeByEmpresaName(getContext(), params[0])
                    );
        }
    }

}
