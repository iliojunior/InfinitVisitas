package br.com.infinitsolucoes.infinitvisitas.ActivityControllers.Consulta;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import java.text.Collator;
import java.util.List;

import br.com.infinitsolucoes.infinitvisitas.ActivityControllers.Cadastro.CadastroVisitasActivity;
import br.com.infinitsolucoes.infinitvisitas.Adapters.SelecionarClienteCustomList;
import br.com.infinitsolucoes.infinitvisitas.Controllers.EmpresasController;
import br.com.infinitsolucoes.infinitvisitas.Interfaces.IInfinitActivity;
import br.com.infinitsolucoes.infinitvisitas.Models.Empresas;
import br.com.infinitsolucoes.infinitvisitas.R;

import static br.com.infinitsolucoes.infinitvisitas.Utils.Utils.EXTRA_EMPRESA_ID;

public class SelecionarClienteActivity extends AppCompatActivity implements IInfinitActivity {

    private Toolbar mToolbar;
    private SearchManager mSearchManager;
    private SearchView mSearchView;
    private ListView mListView;
    private GetClienteAsync mGetClienteAsync;
    private RelativeLayout mEmptyLayout;
    private FloatingActionButton avancarButton;
    private List<Empresas> mEmpresasList;

    private int mPosition = -1;
    private View lastView;
    private View currentView;


    private final AdapterView.OnItemClickListener mOnItemClickListener = (parent, view, position, id) -> {
        if (position != mPosition) {
            view.setBackgroundResource(R.color.colorPrimary100);
            mPosition = position;
            lastView = currentView;
            currentView = view;

            if (lastView != null)
                lastView.setBackgroundResource(android.R.color.transparent);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selecionar_cliente);

        initializeComponents();
        initializeListeners();
        initializeValues();
    }

    @Override
    public void initializeComponents() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mEmptyLayout = (RelativeLayout) findViewById(R.id.empty_layout);
        mListView = (ListView) findViewById(R.id.lista_consulta);

        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mSearchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        mGetClienteAsync = new GetClienteAsync(SelecionarClienteActivity.this);
        avancarButton = (FloatingActionButton) findViewById(R.id.fab_selecionar);
    }

    @Override
    public void initializeListeners() {
        mListView.setOnItemClickListener(mOnItemClickListener);
        avancarButton.setOnClickListener(this::Avancar);
    }

    @Override
    public void initializeValues() {
        mGetClienteAsync.execute("");
        mListView.setEmptyView(mEmptyLayout);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        mSearchView.setIconified(false);
        mSearchView.requestFocus();
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.selecionar_cliente_visita_menu, menu);

        final MenuItem searchMenuItem = menu.findItem(R.id.action_search);
        mSearchView = (SearchView) searchMenuItem.getActionView();
        mSearchView.setSearchableInfo(mSearchManager.getSearchableInfo(getComponentName()));
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                new GetClienteAsync(SelecionarClienteActivity.this).execute(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                new GetClienteAsync(SelecionarClienteActivity.this).execute(newText);
                return true;
            }
        });

        searchMenuItem.expandActionView();
        return super.onCreateOptionsMenu(menu);
    }

    private void Avancar(View v) {
        if (mPosition < 0) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(SelecionarClienteActivity.this);
            builder.setTitle(R.string.title_activity_selecionar_cliente);
            builder.setMessage("Selecione um cliente");
            builder.setPositiveButton("OK", null);
            builder.create().show();
            return;
        }
        final Intent intent = new Intent(SelecionarClienteActivity.this, CadastroVisitasActivity.class);
        intent.putExtra(EXTRA_EMPRESA_ID, mEmpresasList.get(mPosition).get_empresaId());
        startActivity(intent);
        finish();
    }

    private final class GetClienteAsync extends AsyncTask<String, Void, List<Empresas>> {
        private final Context mContext;
        private final EmpresasController mController;
        private SelecionarClienteCustomList mListAdapter;

        public GetClienteAsync(final Context context) {
            mContext = context;
            mController = new EmpresasController(mContext);
        }

        @Override
        protected List<Empresas> doInBackground(String... params) {
            return mController.get(params[0]);
        }

        @Override
        protected void onPostExecute(List<Empresas> aEmpresasList) {
            mListAdapter = new SelecionarClienteCustomList(mContext, R.layout.list_item_selecionar_cliente, aEmpresasList);
            final Collator collator = Collator.getInstance();
            mListAdapter.sort((o1, o2) -> collator.compare(o1.getNome(), o2.getNome()));
            mListView.setAdapter(mListAdapter);
            mEmpresasList = aEmpresasList;
            mPosition = -1;
            lastView = null;
            currentView = null;
        }
    }
}
