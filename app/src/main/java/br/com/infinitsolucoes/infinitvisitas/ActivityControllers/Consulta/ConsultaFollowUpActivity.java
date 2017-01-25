package br.com.infinitsolucoes.infinitvisitas.ActivityControllers.Consulta;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.List;

import br.com.infinitsolucoes.infinitvisitas.ActivityControllers.Cadastro.CadastroFollowUpActivity;
import br.com.infinitsolucoes.infinitvisitas.Adapters.FollowUpCustomList;
import br.com.infinitsolucoes.infinitvisitas.Business.Data.CRUD.EmpresaCRUD;
import br.com.infinitsolucoes.infinitvisitas.Business.Data.CRUD.FollowUpCRUD;
import br.com.infinitsolucoes.infinitvisitas.Interfaces.IInfinitActivity;
import br.com.infinitsolucoes.infinitvisitas.Models.Empresas;
import br.com.infinitsolucoes.infinitvisitas.Models.FollowUp;
import br.com.infinitsolucoes.infinitvisitas.R;
import br.com.infinitsolucoes.infinitvisitas.Utils.AsyncTasks.InfinitDeleteAsyncTask;
import br.com.infinitsolucoes.infinitvisitas.Utils.AsyncTasks.InfinitGetAllAsyncTask;

import static br.com.infinitsolucoes.infinitvisitas.Utils.ActivityUtils.loadToolbar;
import static br.com.infinitsolucoes.infinitvisitas.Utils.Utils.EXTRA_EMPRESA_ID;
import static br.com.infinitsolucoes.infinitvisitas.Utils.Utils.EXTRA_FOLLOW_UP_ID;

public class ConsultaFollowUpActivity extends AppCompatActivity implements IInfinitActivity {
    private static final String TAG = "ConsultaFollowUp";
    private FloatingActionButton fabAdd;
    private ListView listView;
    private Toolbar toolbar;
    private Empresas empresa;
    private SwipeRefreshLayout swipeRefreshLayout;
    private int empresaId;
    private List<FollowUp> followUpList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consulta_follow_up);


        if (!validateEmpresa()) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(ConsultaFollowUpActivity.this);
            builder.setTitle(getString(R.string.title_activity_consulta_follow_up));
            builder.setMessage("Empresa inválida, tente novamente!");
            builder.setPositiveButton("OK", (v, d) -> finish());
            builder.create().show();
            return;
        }

        initializeComponents();
        initializeListeners();
        initializeValues();
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
    public void initializeComponents() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        listView = (ListView) findViewById(R.id.lista_consulta);
        fabAdd = (FloatingActionButton) findViewById(R.id.fab);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.pullToRefresh);
        registerForContextMenu(listView);
        ViewCompat.setNestedScrollingEnabled(listView, true);
        ViewCompat.setNestedScrollingEnabled(findViewById(R.id.nested_scroll), true);
    }

    @Override
    public void initializeListeners() {
        loadToolbar(this, toolbar, () -> {
            getSupportActionBar().setTitle(getEmpresa().getNome());
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        });
        fabAdd.setOnClickListener(v -> {
            final Intent intent = new Intent(ConsultaFollowUpActivity.this, CadastroFollowUpActivity.class);
            intent.putExtra(EXTRA_EMPRESA_ID, empresaId);
            startActivityForResult(intent, 0);
        });
        listView.setOnItemClickListener((parent, view, position, id) -> listView.showContextMenuForChild(view));
        swipeRefreshLayout.setOnRefreshListener(() -> refreshDataSource());
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId() == R.id.lista_consulta) {
            menu.setHeaderTitle("Follow-Up");
            getMenuInflater().inflate(R.menu.context_menu_follow_up, menu);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.edit_item:
                editarFollowUp(info.position);
                break;
            case R.id.delete_item:
                deleteFollowUp(info.position);
                break;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    public void initializeValues() {
        new GetAllAsync(ConsultaFollowUpActivity.this).execute(empresaId);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0 && resultCode == Activity.RESULT_OK)
            refreshDataSource();
    }

    private void deleteFollowUp(int position) {
        final FollowUp followUp = followUpList.get(position);
        final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM");
        final AlertDialog.Builder builder = new AlertDialog.Builder(ConsultaFollowUpActivity.this);
        builder.setTitle(dateFormat.format(followUp.getData().getTime()));
        builder.setMessage("Deseja realmente apagar?");
        builder.setPositiveButton("Sim", (d, w) -> new DeleteAsync(ConsultaFollowUpActivity.this).execute(followUp));
        builder.setNegativeButton("Não", null);
        final AlertDialog alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }

    private void refreshDataSource() {
        new GetAllAsync(ConsultaFollowUpActivity.this).execute(empresaId);
    }

    private void editarFollowUp(int position) {
        final Intent intent = new Intent(ConsultaFollowUpActivity.this, CadastroFollowUpActivity.class);
        intent.putExtra(EXTRA_FOLLOW_UP_ID, followUpList.get(position).get_followUpId());
        startActivityForResult(intent, 0);
    }

    private boolean validateEmpresa() {
        return (getIntent().hasExtra(EXTRA_EMPRESA_ID)
                && getIntent().getIntExtra(EXTRA_EMPRESA_ID, 0) > 0
                && setEmpresa(getIntent().getIntExtra(EXTRA_EMPRESA_ID, 0)));
    }

    private boolean setEmpresa(int empresaId) {
        try {
            final Empresas empresa = new EmpresaCRUD(ConsultaFollowUpActivity.this).get(empresaId);
            this.empresa = empresa;
            this.empresaId = empresaId;
            return true;
        } catch (SQLException e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return false;
    }

    private Empresas getEmpresa() {
        if (empresa != null && empresa.get_empresaId() == empresaId)
            return empresa;
        setEmpresa(empresaId);
        return empresa;
    }

    private final class GetAllAsync extends InfinitGetAllAsyncTask<Integer, Void, List<FollowUp>> {

        public GetAllAsync(Context context) {
            super(context, getString(R.string.title_activity_consulta_follow_up));
        }

        @Override
        protected void onAsyncFinish(List<FollowUp> followUps) {
            final FollowUpCustomList adapter = new FollowUpCustomList(getContext(), R.layout.list_item_follow_up, followUps);
            adapter.sort(FollowUp.SORT_DATE_DESC);
            listView.setAdapter(adapter);
            followUpList = followUps;

            if (swipeRefreshLayout.isRefreshing())
                swipeRefreshLayout.setRefreshing(false);
        }

        @Override
        protected List<FollowUp> doInBackground(Integer... params) {
            try {
                return new FollowUpCRUD(getContext()).getAll(params[0]);
            } catch (SQLException e) {
                Log.e(TAG, e.getMessage(), e);
            }
            return null;
        }
    }

    private final class DeleteAsync extends InfinitDeleteAsyncTask<FollowUp, Void, Boolean> {

        public DeleteAsync(Context context) {
            super(context, getString(R.string.title_activity_consulta_follow_up));
        }

        @Override
        protected void onAsyncFinish(Boolean aBoolean) {
            refreshDataSource();
        }

        @Override
        protected Boolean doInBackground(FollowUp... params) {
            try {
                return new FollowUpCRUD(getContext()).delete(params[0]);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
