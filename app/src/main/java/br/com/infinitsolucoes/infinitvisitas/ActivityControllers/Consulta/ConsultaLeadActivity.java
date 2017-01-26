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
import java.text.Collator;
import java.util.ArrayList;
import java.util.List;

import br.com.infinitsolucoes.infinitvisitas.ActivityControllers.Cadastro.CadastroLeadActivity;
import br.com.infinitsolucoes.infinitvisitas.Adapters.LeadCustomList;
import br.com.infinitsolucoes.infinitvisitas.Business.Data.CRUD.EmpresaCRUD;
import br.com.infinitsolucoes.infinitvisitas.Business.Enumerador;
import br.com.infinitsolucoes.infinitvisitas.Controllers.LeadsController;
import br.com.infinitsolucoes.infinitvisitas.Models.Empresas;
import br.com.infinitsolucoes.infinitvisitas.R;
import br.com.infinitsolucoes.infinitvisitas.Utils.AsyncTasks.InfinitDeleteAsyncTask;

import static br.com.infinitsolucoes.infinitvisitas.Utils.Utils.EXTRA_EMPRESA_ID;

public class ConsultaLeadActivity extends InfinitAppCompatActivity {
    private static final String TAG = "ConsultaLeadActivity";
    private static final int CODE_NEW = 0;
    private static final int CODE_EDIT = 1;

    private int startList = 0;
    private int limitList = 10;
    private boolean loadingMore = false;
    private boolean isGetting = false;

    private List<Empresas> leadsList;

    private SearchView.OnQueryTextListener mOnQueryTextListener = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String query) {
            new GetAsync(ConsultaLeadActivity.this).execute(query);
            return false;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            new GetAsync(ConsultaLeadActivity.this).execute(newText);
            return false;
        }
    };

    //region Override's
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_consulta_lead);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void initializeListeners() {
        setOnEmptyButtonClickListener(v -> new GetAllLeadsAsync(ConsultaLeadActivity.this).execute());
        getSwipeRefreshLayout().setOnRefreshListener(() -> new GetAllLeadsAsync(ConsultaLeadActivity.this).execute());
        getListView().setOnItemClickListener((parent, view, position, id) -> getListView().showContextMenuForChild(view));
        super.initializeListeners();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CODE_EDIT || requestCode == CODE_NEW) {
            if (resultCode == Activity.RESULT_OK)
                new GetAllLeadsAsync(ConsultaLeadActivity.this).execute();
        }
    }

    @Override
    protected DrawerLayout mGetDrawerLayout() {
        return (DrawerLayout) findViewById(R.id.activity_consulta_lead_drawer_layout);
    }

    @Override
    protected FloatingActionMenu mGetActionMenu() {
        return (FloatingActionMenu) findViewById(R.id.floating_action_menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.followup_item:
                final Intent intent = new Intent(ConsultaLeadActivity.this, ConsultaFollowUpActivity.class);
                intent.putExtra(EXTRA_EMPRESA_ID, leadsList.get(info.position).get_empresaId());
                startActivity(intent);
                return true;
            case R.id.edit_item:
                editarLead(info.position);
                return true;
            case R.id.delete_item:
                deletarLead(info.position);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId() == R.id.lista_consulta) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
            menu.setHeaderTitle(R.string.title_activity_leads);
            getMenuInflater().inflate(R.menu.context_menu_lead, menu);
        }
    }

    @Override
    public void initializeValues() {
        super.initializeValues();
        new GetAllLeadsAsync(ConsultaLeadActivity.this).execute();
        registerForContextMenu(getListView());
        setOnQueryTextListener(mOnQueryTextListener);
    }

    //endregion

    private void editarLead(int position) {
        Intent intent = new Intent(ConsultaLeadActivity.this, CadastroLeadActivity.class);
        intent.putExtra(EXTRA_EMPRESA_ID, leadsList.get(position).get_empresaId());
        startActivityForResult(intent, 1);
    }

    private void deletarLead(int position) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(ConsultaLeadActivity.this);
        final Empresas empresas = leadsList.get(position);
        builder.setTitle(empresas.getNome());
        builder.setMessage("Deseja realmente apagar?");
        builder.setNegativeButton("Não", null);
        builder.setPositiveButton("Sim", (d, w) -> new DeleteLeadAsync(ConsultaLeadActivity.this).execute(empresas));
        builder.create().show();
    }

    private final class DeleteLeadAsync extends InfinitDeleteAsyncTask<Empresas, Void, Enumerador.ResponseCode> {

        public DeleteLeadAsync(Context context) {
            super(context, getString(R.string.title_activity_leads));
        }

        @Override
        protected void onAsyncFinish(Enumerador.ResponseCode responseCode) {
            new GetAllLeadsAsync(getContext()).execute();
        }

        @Override
        protected Enumerador.ResponseCode doInBackground(Empresas... params) {
            try {
                return new EmpresaCRUD(getContext()).delete(params[0]);
            } catch (SQLException e) {
                e.printStackTrace();
                return Enumerador.ResponseCode.ERROR;
            }
        }
    }

    private final class GetAllLeadsAsync extends AsyncTask<Void, Void, List<Empresas>> {
        private final ProgressDialog progressDialog;
        private AlertDialog.Builder alertBuilder;
        private LeadCustomList listAdapter;
        private final Context context;

        public GetAllLeadsAsync(Context context) {
            this.context = context;
            progressDialog = new ProgressDialog(context);
            alertBuilder = new AlertDialog.Builder(context);
        }

        @Override
        protected void onPostExecute(List<Empresas> empresases) {
            isGetting = false;
            progressDialog.dismiss();

            leadsList = empresases;
            listAdapter = new LeadCustomList(context, R.layout.list_item_leads, empresases);
            listAdapter.sort(Empresas.COMPARATOR_LEADS);
            getListView().setAdapter(listAdapter);
            if (getSwipeRefreshLayout().isRefreshing())
                getSwipeRefreshLayout().setRefreshing(false);
        }

        @Override
        protected void onPreExecute() {

            if (isGetting)
                this.cancel(true);
            else {
                if (!getSwipeRefreshLayout().isRefreshing()) {
                    isGetting = false;
                    progressDialog.setTitle(R.string.title_activity_leads);
                    progressDialog.setMessage("Obtendo dados...");
                    progressDialog.setIndeterminate(true);
                    progressDialog.show();
                }
            }
        }

        @Override
        protected void onCancelled() {
            isGetting = false;
            progressDialog.dismiss();

            alertBuilder.setTitle(R.string.title_activity_leads);
            alertBuilder.setMessage("Cancelado pelo usuário.");
            alertBuilder.setPositiveButton("OK", null);
            alertBuilder.create().show();
        }

        @Override
        protected List<Empresas> doInBackground(Void... params) {
            try {
                return new LeadsController(context).getAllLeads();
            } catch (Exception e) {
                progressDialog.dismiss();
                runOnUiThread(() -> {
                    alertBuilder.setTitle(R.string.title_activity_leads);
                    alertBuilder.setMessage("Ocorreu um erro interno.");
                    alertBuilder.setPositiveButton("OK", null);
                    alertBuilder.create().show();
                });
                Log.e(TAG, e.getMessage(), e);
            }
            return new ArrayList<>();
        }
    }

    private final class GetAsync extends AsyncTask<String, Void, List<Empresas>> {
        private final AlertDialog.Builder mBuilder;
        private final Context mContext;
        private final LeadsController mLeadsController;

        public GetAsync(final Context context) {
            this.mContext = context;
            mBuilder = new AlertDialog.Builder(mContext);
            mLeadsController = new LeadsController(mContext);
        }

        @Override
        protected void onCancelled() {
            mBuilder.setTitle(R.string.title_activity_leads);
            mBuilder.setMessage("Cancelado pelo usuário.");
            mBuilder.setPositiveButton("OK", null);
            mBuilder.create().show();
        }

        @Override
        protected void onPostExecute(List<Empresas> empresasList) {
            final LeadCustomList mList = new LeadCustomList(mContext,
                    R.layout.list_item_leads,
                    empresasList);
            final Collator mCollator = Collator.getInstance();
            mList.sort((e1, e2) -> mCollator.compare(e1.getNome(), e2.getNome()));
            getListView().setAdapter(mList);
            leadsList = empresasList;
        }

        @Override
        protected List<Empresas> doInBackground(String... params) {
            return mLeadsController.get(params[0]);
        }
    }
}
