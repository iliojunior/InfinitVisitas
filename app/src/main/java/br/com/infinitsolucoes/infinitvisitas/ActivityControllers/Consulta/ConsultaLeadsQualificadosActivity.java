package br.com.infinitsolucoes.infinitvisitas.ActivityControllers.Consulta;

import android.app.Activity;
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

import br.com.infinitsolucoes.infinitvisitas.ActivityControllers.Cadastro.CadastroLeadActivity;
import br.com.infinitsolucoes.infinitvisitas.ActivityControllers.InfinitAppCompatActivity;
import br.com.infinitsolucoes.infinitvisitas.Adapters.LeadQualificadoCustomList;
import br.com.infinitsolucoes.infinitvisitas.Business.Data.CRUD.EmpresaCRUD;
import br.com.infinitsolucoes.infinitvisitas.Business.Enumerador;
import br.com.infinitsolucoes.infinitvisitas.Controllers.LeadsQualificadosController;
import br.com.infinitsolucoes.infinitvisitas.Finds.FindLeadQualificadoByName;
import br.com.infinitsolucoes.infinitvisitas.Models.Empresas;
import br.com.infinitsolucoes.infinitvisitas.R;
import br.com.infinitsolucoes.infinitvisitas.Utils.AsyncTasks.InfinitDeleteAsyncTask;
import br.com.infinitsolucoes.infinitvisitas.Utils.AsyncTasks.InfinitGetAsyncTask;

import static br.com.infinitsolucoes.infinitvisitas.Utils.Utils.EXTRA_EMPRESA_ID;

public class ConsultaLeadsQualificadosActivity extends InfinitAppCompatActivity {
    private static final String TAG = "ConsultaLQualificados";
    private int posicaoContext = 0;
    private List<Empresas> leadsQualificados;
    private final SearchView.OnQueryTextListener textListener = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String query) {
            new GetAsync(ConsultaLeadsQualificadosActivity.this).execute(query);
            return false;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            new GetAsync(ConsultaLeadsQualificadosActivity.this).execute(newText);
            return false;
        }
    };

    @Override
    protected DrawerLayout mGetDrawerLayout() {
        return (DrawerLayout) findViewById(R.id.activity_consulta_leads_qualificados);
    }

    @Override
    protected FloatingActionMenu mGetActionMenu() {
        return (FloatingActionMenu) findViewById(R.id.floating_action_menu);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_consulta_leads_qualificados);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void initializeComponents() {
        super.initializeComponents();
    }

    @Override
    public void initializeListeners() {
        setOnEmptyButtonClickListener((v) -> refreshDataAdapter());
        getSwipeRefreshLayout().setOnRefreshListener(this::refreshDataAdapter);
        getListView().setOnItemClickListener((parent, view, position, id) -> getListView().showContextMenuForChild(view));
        super.initializeListeners();
        setOnQueryTextListener(textListener);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId() == R.id.lista_consulta) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
            menu.setHeaderTitle(R.string.title_activity_leads_qualificados);
            getMenuInflater().inflate(R.menu.context_menu_lead, menu);
        }
    }

    @Override
    public void initializeValues() {
        super.initializeValues();
        new GetAllLeadsQualificadosAsync(ConsultaLeadsQualificadosActivity.this).execute();
        registerForContextMenu(getListView());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK)
                new GetAllLeadsQualificadosAsync(ConsultaLeadsQualificadosActivity.this).execute();
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.followup_item:
                final Intent intent = new Intent(ConsultaLeadsQualificadosActivity.this, ConsultaFollowUpActivity.class);
                intent.putExtra(EXTRA_EMPRESA_ID, leadsQualificados.get(info.position).get_empresaId());
                startActivity(intent);
                return true;
            case R.id.edit_item:
                editarLead(info.position);
                return true;
            case R.id.delete_item:
                deleteLead(info.position);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    private void refreshDataAdapter() {
        new GetAllLeadsQualificadosAsync(ConsultaLeadsQualificadosActivity.this).execute();
    }

    private void editarLead(int position) {
        Intent intent = new Intent(ConsultaLeadsQualificadosActivity.this, CadastroLeadActivity.class);
        intent.putExtra(EXTRA_EMPRESA_ID, leadsQualificados.get(position).get_empresaId());
        startActivityForResult(intent, 1);
    }

    private void deleteLead(int position) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(ConsultaLeadsQualificadosActivity.this);
        Empresas empresa = leadsQualificados.get(position);
        builder.setTitle(empresa.getNome());
        builder.setMessage("Deseja apagar ou retornar para Leads?");
        builder.setNeutralButton("Cancelar", null);
        builder.setNegativeButton("Retornar", (d, w) -> new RetornarLeadAsync(ConsultaLeadsQualificadosActivity.this).execute(empresa));
        builder.setPositiveButton("Apagar", (d, w) -> new DeleteLeadAsync(ConsultaLeadsQualificadosActivity.this).execute(empresa));
        builder.create().show();
    }

    private final class RetornarLeadAsync extends AsyncTask<Empresas, Void, Boolean> {
        private final Context context;
        private final ProgressDialog progressDialog;
        private AlertDialog.Builder builder;
        private LeadQualificadoCustomList listAdapter;

        public RetornarLeadAsync(Context context) {
            this.context = context;
            progressDialog = new ProgressDialog(context);
            builder = new AlertDialog.Builder(context);
        }

        @Override
        protected Boolean doInBackground(Empresas... params) {
            Empresas empresa = params[0];
            try {
                empresa.setNivelInteresse(0);
                return new EmpresaCRUD(context).save(empresa).getNivelInteresse() == 0;
            } catch (Exception e) {
                Log.e(TAG, e.getMessage(), e);
            }
            return null;
        }

        @Override
        protected void onCancelled() {
            progressDialog.dismiss();
            builder.setTitle(R.string.title_activity_leads_qualificados);
            builder.setMessage("Cancelado pelo usuário");
            builder.setPositiveButton("OK", null);
            builder.create().show();
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            progressDialog.dismiss();
            String title = "Falha";
            String message = "Tente novamente.";

            if (aBoolean == null) {
                message = "Ocorreu um erro interno.";
            } else if (aBoolean) {
                title = "Sucesso";
                message = "Retornado com sucesso!";
                refreshDataAdapter();
            }

            builder.setTitle(title);
            builder.setMessage(message);
            builder.setPositiveButton("OK", (d, w) -> new GetAllLeadsQualificadosAsync(context).execute());
            builder.create().show();
        }

        @Override
        protected void onPreExecute() {
            progressDialog.setTitle(R.string.title_activity_leads_qualificados);
            progressDialog.setMessage("Retornando lead, aguarde.");
            progressDialog.setIndeterminate(true);
            progressDialog.show();
        }
    }

    private final class DeleteLeadAsync extends InfinitDeleteAsyncTask<Empresas, Void, Enumerador.ResponseCode> {

        public DeleteLeadAsync(Context context) {
            super(context, getString(R.string.title_activity_contatos));
        }

        @Override
        protected void onAsyncFinish(Enumerador.ResponseCode aBoolean) {
            refreshDataAdapter();
        }

        @Override
        protected Enumerador.ResponseCode doInBackground(Empresas... params) {
            try {
                return new EmpresaCRUD(getContext()).delete(params[0]);
            } catch (SQLException e) {
                Log.e(TAG, e.getMessage(), e);
                return null;
            }
        }
    }

    private final class GetAllLeadsQualificadosAsync extends AsyncTask<Void, Void, List<Empresas>> {
        private final Context context;
        private final ProgressDialog progressDialog;
        private AlertDialog.Builder builder;
        private LeadQualificadoCustomList listAdapter;

        public GetAllLeadsQualificadosAsync(final Context context) {
            this.context = context;
            progressDialog = new ProgressDialog(context);
            builder = new AlertDialog.Builder(context);
        }

        @Override
        protected void onCancelled() {
            progressDialog.dismiss();
            builder.setTitle(R.string.title_activity_leads_qualificados);
            builder.setMessage("Cancelado pelo usuário");
            builder.setPositiveButton("OK", null);
            builder.create().show();
        }

        @Override
        protected void onPostExecute(List<Empresas> empresasList) {
            progressDialog.dismiss();

            if (empresasList == null) {
                builder.setTitle(R.string.title_activity_leads);
                builder.setMessage("Ocorreu um erro interno");
                builder.setPositiveButton("OK", null);
                builder.create().show();
                empresasList = new ArrayList<>();
            }

            leadsQualificados = empresasList;
            listAdapter = new LeadQualificadoCustomList(context, R.layout.activity_consulta_leads_qualificados, empresasList);
            listAdapter.sort(Empresas.COMPARATOR_QUALIFICADOS);
            getListView().setAdapter(listAdapter);

            if (getSwipeRefreshLayout().isRefreshing())
                getSwipeRefreshLayout().setRefreshing(false);
        }

        @Override
        protected void onPreExecute() {
            if (!getSwipeRefreshLayout().isRefreshing()) {
                progressDialog.setTitle(R.string.title_activity_leads);
                progressDialog.setMessage("Obtendo dados.");
                progressDialog.setIndeterminate(true);
                progressDialog.show();
            }
        }

        @Override
        protected List<Empresas> doInBackground(Void... params) {
            try {
                return new LeadsQualificadosController(context).getAllLeads();
            } catch (Exception e) {
                Log.e(TAG, e.getMessage(), e);
            }
            return null;
        }
    }

    private final class GetAsync extends InfinitGetAsyncTask<String, Void, List<Empresas>> {

        public GetAsync(Context context) {
            super(context, getString(R.string.title_activity_leads_qualificados));
        }

        @Override
        protected void onAsyncFinish(List<Empresas> empresasList) {
            final LeadQualificadoCustomList adapter = new LeadQualificadoCustomList(getContext(), R.layout.list_item_leads_qualificados, empresasList);
            adapter.sort(Empresas.COMPARATOR_QUALIFICADOS);
            getListView().setAdapter(adapter);
            leadsQualificados = empresasList;

            if (getSwipeRefreshLayout().isRefreshing())
                getSwipeRefreshLayout().setRefreshing(false);
        }

        @Override
        protected List<Empresas> doInBackground(String... params) {
            return new EmpresaCRUD(getContext())
                    .find(new FindLeadQualificadoByName(params[0]));
        }
    }

}

