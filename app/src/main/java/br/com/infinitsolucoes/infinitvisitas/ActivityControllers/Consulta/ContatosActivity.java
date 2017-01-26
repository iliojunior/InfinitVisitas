package br.com.infinitsolucoes.infinitvisitas.ActivityControllers.Consulta;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.ContextMenu;
import android.view.View;

import com.github.clans.fab.FloatingActionMenu;

import java.sql.SQLException;
import java.util.List;

import br.com.infinitsolucoes.infinitvisitas.ActivityControllers.Cadastro.CadastroLeadActivity;
import br.com.infinitsolucoes.infinitvisitas.Adapters.ContatosCustomList;
import br.com.infinitsolucoes.infinitvisitas.Business.Data.CRUD.EmpresaCRUD;
import br.com.infinitsolucoes.infinitvisitas.Business.Enumerador;
import br.com.infinitsolucoes.infinitvisitas.Controllers.ContatosController;
import br.com.infinitsolucoes.infinitvisitas.Models.Empresas;
import br.com.infinitsolucoes.infinitvisitas.R;
import br.com.infinitsolucoes.infinitvisitas.Utils.AsyncTasks.InfinitDeleteAsyncTask;
import br.com.infinitsolucoes.infinitvisitas.Utils.AsyncTasks.InfinitGetAllAsyncTask;
import br.com.infinitsolucoes.infinitvisitas.Utils.AsyncTasks.InfinitGetAsyncTask;

import static br.com.infinitsolucoes.infinitvisitas.Utils.Utils.EXTRA_EMPRESA_ID;

public class ContatosActivity extends InfinitAppCompatActivity {
    private static final String TAG = "ContatosActivity";
    private List<Empresas> mListEmpresas;

    @Override
    protected DrawerLayout mGetDrawerLayout() {
        return (DrawerLayout) findViewById(R.id.activity_contatos);
    }

    @Override
    protected FloatingActionMenu mGetActionMenu() {
        return (FloatingActionMenu) findViewById(R.id.floating_action_menu);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_contatos);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public void initializeComponents() {
        super.initializeComponents();
        registerForContextMenu(getListView());
    }

    @Override
    public void initializeListeners() {
        super.initializeListeners();
        getSwipeRefreshLayout().setOnRefreshListener(this::refreshDataSource);
        setOnEmptyButtonClickListener(v -> refreshDataSource());
    }

    @Override
    public void initializeValues() {
        super.initializeValues();
        refreshDataSource();
    }

    private void refreshDataSource() {
        new GetAllAsync(ContatosActivity.this).execute();
    }

    private void editarContato(final int position) {
        final Empresas empresa = mListEmpresas.get(position);
        final Intent intent = new Intent(ContatosActivity.this, CadastroLeadActivity.class);
        intent.putExtra(EXTRA_EMPRESA_ID, empresa.get_empresaId());
        startActivityForResult(intent, 0);
    }

    private final class GetAllAsync extends InfinitGetAllAsyncTask<Void, Void, List<Empresas>> {

        public GetAllAsync(Context context) {
            super(context, getString(R.string.title_activity_contatos));
        }

        @Override
        protected List<Empresas> doInBackground(Void... params) {
            try {
                return new ContatosController(getContext()).getAll();
            } catch (SQLException e) {
                Log.e(TAG, e.getMessage(), e);
                return null;
            }
        }

        @Override
        protected void onAsyncFinish(List<Empresas> empresasList) {
            final ContatosCustomList listAdapter = new ContatosCustomList(getContext(), R.layout.list_item_contatos, empresasList);
            listAdapter.sort(Empresas.SORT_ALPHABETICALLY);
            getListView().setAdapter(listAdapter);
            mListEmpresas = empresasList;

            if (getSwipeRefreshLayout().isRefreshing())
                getSwipeRefreshLayout().setRefreshing(false);
        }
    }

    private final class DeleteAsync extends InfinitDeleteAsyncTask<Empresas, Void, Enumerador.ResponseCode> {

        public DeleteAsync(Context context) {
            super(context, getString(R.string.title_activity_contatos));
        }

        @Override
        protected void onAsyncFinish(Enumerador.ResponseCode responseCode) {
            refreshDataSource();
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

    private final class GetAsync extends InfinitGetAsyncTask<String, Void, List<Empresas>> {

        public GetAsync(Context context) {
            super(context, getString(R.string.title_activity_contatos));
        }

        @Override
        protected void onAsyncFinish(List<Empresas> empresasList) {
            final ContatosCustomList listAdapter = new ContatosCustomList(getContext(), R.layout.list_item_contatos, empresasList);
            listAdapter.sort(Empresas.SORT_ALPHABETICALLY);
            getListView().setAdapter(listAdapter);
            mListEmpresas = empresasList;
        }

        @Override
        protected List<Empresas> doInBackground(String... params) {
            return new EmpresaCRUD(getContext()).get(params[0]);
        }
    }
}
