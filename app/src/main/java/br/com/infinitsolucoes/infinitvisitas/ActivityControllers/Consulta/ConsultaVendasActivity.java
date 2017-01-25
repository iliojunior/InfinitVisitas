package br.com.infinitsolucoes.infinitvisitas.ActivityControllers.Consulta;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SearchView;
import android.util.Log;

import com.github.clans.fab.FloatingActionMenu;

import java.sql.SQLException;
import java.util.List;

import br.com.infinitsolucoes.infinitvisitas.ActivityControllers.InfinitAppCompatActivity;
import br.com.infinitsolucoes.infinitvisitas.Adapters.VendasCustomList;
import br.com.infinitsolucoes.infinitvisitas.Business.Data.CRUD.VendaCRUD;
import br.com.infinitsolucoes.infinitvisitas.Controllers.VendasController;
import br.com.infinitsolucoes.infinitvisitas.Finds.FindVendaByEmpresaName;
import br.com.infinitsolucoes.infinitvisitas.Models.Vendas;
import br.com.infinitsolucoes.infinitvisitas.R;
import br.com.infinitsolucoes.infinitvisitas.Utils.AsyncTasks.InfinitGetAsyncTask;

public class ConsultaVendasActivity extends InfinitAppCompatActivity {

    private static final String TAG = "ConsultaVendas";
    private List<Vendas> mVendasList;

    private SearchView.OnQueryTextListener onQueryTextListener = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String query) {
            new GetAsync(ConsultaVendasActivity.this).execute(query);
            return false;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            new GetAsync(ConsultaVendasActivity.this).execute(newText);
            return true;
        }
    };

    @Override
    protected DrawerLayout mGetDrawerLayout() {
        return (DrawerLayout) findViewById(R.id.activity_consulta_vendas);
    }

    @Override
    protected FloatingActionMenu mGetActionMenu() {
        return (FloatingActionMenu) findViewById(R.id.floating_action_menu);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_consulta_vendas);
        super.onCreate(savedInstanceState);
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
        setOnQueryTextListener(onQueryTextListener);
    }

    private void refreshDataSource() {
        new GetAllAsync(ConsultaVendasActivity.this).execute();
    }

    private final class GetAllAsync extends AsyncTask<Void, Void, List<Vendas>> {
        private final Context mContext;
        private final ProgressDialog mProgressDialog;
        private final AlertDialog.Builder mBuilder;

        public GetAllAsync(final Context mContext) {
            this.mContext = mContext;
            this.mProgressDialog = new ProgressDialog(mContext);
            this.mBuilder = new AlertDialog.Builder(mContext);
        }

        @Override
        protected List<Vendas> doInBackground(Void... params) {
            try {
                return new VendasController(mContext).getAll();
            } catch (SQLException e) {
                Log.e(TAG, e.getMessage(), e);
            }
            return null;
        }

        @Override
        protected void onCancelled() {
            mProgressDialog.dismiss();
            mBuilder.setTitle(R.string.title_activity_consulta_vendas);
            mBuilder.setMessage("Cancelado pelo usuário");
            mBuilder.setPositiveButton("OK", null);
            mBuilder.create().show();
        }

        @Override
        protected void onPostExecute(List<Vendas> vendasList) {
            mProgressDialog.dismiss();
            if (vendasList == null) {
                mBuilder.setTitle(R.string.title_activity_consulta_vendas);
                mBuilder.setMessage("Ocorreu um erro interno");
                mBuilder.setPositiveButton("OK", null);
                mBuilder.create().show();
            }

            final VendasCustomList listAdapter = new VendasCustomList(mContext, R.layout.activity_consulta_vendas, vendasList);
            listAdapter.sort(Vendas.sortComparator);
            getListView().setAdapter(listAdapter);
            mVendasList = vendasList;

            if (getSwipeRefreshLayout().isRefreshing())
                getSwipeRefreshLayout().setRefreshing(false);
        }

        @Override
        protected void onPreExecute() {
            if (!getSwipeRefreshLayout().isRefreshing()) {
                mProgressDialog.setTitle(R.string.title_activity_consulta_vendas);
                mProgressDialog.setMessage("Obtendo dados");
                mProgressDialog.setIndeterminate(true);
                mProgressDialog.show();
            }
        }
    }

    private final class GetAsync extends InfinitGetAsyncTask<String, Void, List<Vendas>> {

        public GetAsync(Context context) {
            super(context, getString(R.string.title_activity_consulta_vendas));
        }

        @Override
        protected void onAsyncFinish(List<Vendas> vendasList) {
            final VendasCustomList adapter = new VendasCustomList(getContext(), R.layout.list_item_vendas, vendasList);
            adapter.sort(Vendas.sortComparator);
            getListView().setAdapter(adapter);

            mVendasList = vendasList;

            if (getSwipeRefreshLayout().isRefreshing())
                getSwipeRefreshLayout().setRefreshing(false);
        }

        @Override
        protected List<Vendas> doInBackground(String... params) {
            return new VendaCRUD(getContext())
                    .find(new FindVendaByEmpresaName(params[0], getContext()));
        }
    }
}
