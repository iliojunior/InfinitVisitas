package br.com.infinitsolucoes.infinitvisitas.ActivityControllers.Consulta;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import br.com.infinitsolucoes.infinitvisitas.ActivityControllers.Cadastro.CadastroLeadActivity;
import br.com.infinitsolucoes.infinitvisitas.Interfaces.IInfinitActivity;
import br.com.infinitsolucoes.infinitvisitas.R;

import static br.com.infinitsolucoes.infinitvisitas.Utils.ActivityUtils.mOnNavigationItemSelectedListener;

public abstract class InfinitAppCompatActivity extends AppCompatActivity implements IInfinitActivity {
    //region Fields
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private ActionBarDrawerToggle barDrawerToggle;
    private SearchManager searchManager;
    private SearchView searchView;
    private SearchView.OnQueryTextListener onQueryTextListener;
    private FloatingActionMenu actionMenu;
    private FloatingActionButton cadastrarLead_fab;
    private FloatingActionButton cadastrarVisita_fab;
    private ListView listView;
    private RelativeLayout emptyLayout;
    private Button emptyButton;
    private SwipeRefreshLayout swipeRefreshLayout;

    private View.OnClickListener onEmptyButtonClickListener = v -> {
    };
    private final AbsListView.OnScrollListener onScrollListener = new AbsListView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {

        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            if (firstVisibleItem > mPreviousVisibleItem) {
                mGetActionMenu().hideMenu(true);
            } else if (firstVisibleItem < mPreviousVisibleItem) {
                mGetActionMenu().showMenu(true);
            }
            mPreviousVisibleItem = firstVisibleItem;
        }
    };

    private int mPreviousVisibleItem;
    //endregion

    //region Load Fields
    protected abstract DrawerLayout mGetDrawerLayout();

    protected abstract FloatingActionMenu mGetActionMenu();
    //endregion

    //region Getter methods
    protected final DrawerLayout getDrawerLayout() {
        if (this.drawerLayout != null)
            return this.drawerLayout;
        this.drawerLayout = mGetDrawerLayout();
        return this.drawerLayout;
    }

    protected final NavigationView getNavigationView() {
        if (this.navigationView != null)
            return this.navigationView;
        this.navigationView = (NavigationView) findViewById(R.id.nav_view);
        return this.navigationView;
    }

    protected final Toolbar getToolbar() {
        if (this.toolbar != null)
            return this.toolbar;
        this.toolbar = (Toolbar) findViewById(R.id.toolbar);
        return this.toolbar;
    }

    protected final FloatingActionButton getCadastrarLead_fab() {
        if (this.cadastrarLead_fab != null)
            return this.cadastrarLead_fab;
        this.cadastrarLead_fab = (FloatingActionButton) findViewById(R.id.fab_cadastrar_lead);
        return this.cadastrarLead_fab;
    }

    protected final FloatingActionButton getCadastrarVisita_fab() {
        if (this.cadastrarVisita_fab != null)
            return this.cadastrarVisita_fab;
        this.cadastrarVisita_fab = (FloatingActionButton) findViewById(R.id.fab_cadastrar_visita);
        return this.cadastrarVisita_fab;
    }

    protected final FloatingActionMenu getActionMenu() {
        if (this.actionMenu != null)
            return this.actionMenu;
        this.actionMenu = (FloatingActionMenu) findViewById(R.id.floating_action_menu);
        return this.actionMenu;
    }

    protected final ListView getListView() {
        if (this.listView != null)
            return this.listView;
        this.listView = (ListView) findViewById(R.id.lista_consulta);
        return this.listView;
    }

    protected final RelativeLayout getEmptyLayout() {
        if (this.emptyLayout != null)
            return this.emptyLayout;
        this.emptyLayout = (RelativeLayout) findViewById(R.id.empty_layout);
        return this.emptyLayout;
    }

    protected final Button getEmptyButton() {
        if (this.emptyButton != null)
            return this.emptyButton;
        this.emptyButton = (Button) getEmptyLayout().findViewById(R.id.empty_button);
        return this.emptyButton;
    }

    protected final SwipeRefreshLayout getSwipeRefreshLayout() {
        if (this.swipeRefreshLayout != null)
            return this.swipeRefreshLayout;
        this.swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.pullToRefresh);
        return this.swipeRefreshLayout;
    }

    public final AbsListView.OnScrollListener getOnScrollListener() {
        return onScrollListener;
    }

    protected View.OnClickListener getOnEmptyButtonClickListener() {
        return this.onEmptyButtonClickListener;
    }

    public SearchView.OnQueryTextListener getOnQueryTextListener() {
        if (onQueryTextListener != null)
            return onQueryTextListener;
        onQueryTextListener = new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        };
        return onQueryTextListener;
    }
    //endregion

    //region Setter methods
    protected void setOnEmptyButtonClickListener(View.OnClickListener listener) {
        this.onEmptyButtonClickListener = listener;
    }

    public void setOnQueryTextListener(SearchView.OnQueryTextListener listener) {
        onQueryTextListener = listener;
    }
    //endregion

    //region Override's methods
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        if (Utils.isNetworkAvailable(this) && Session.getUsuarioLogado() == null) {
//            Intent intent = new Intent(this, LoginActivity.class);
//            intent.putExtra("keep", false);
//            startActivity(intent);
//            this.finish();
//        }

        initializeComponents();
        initializeListeners();
        initializeValues();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.principal_actionbar, menu);

        searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setOnQueryTextListener(getOnQueryTextListener());

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {
        if (getDrawerLayout().isDrawerOpen(GravityCompat.START)) {
            getDrawerLayout().closeDrawers();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void initializeValues() {
        barDrawerToggle.syncState();
        getActionMenu().hideMenu(false);
        getListView().setEmptyView(getEmptyLayout());
    }

    @Override
    public void initializeListeners() {
        new Handler().postDelayed(() -> {
            getActionMenu().showMenu(true);
            getActionMenu().setMenuButtonShowAnimation(AnimationUtils.loadAnimation(this, R.anim.show_from_bottom));
            getActionMenu().setMenuButtonHideAnimation(AnimationUtils.loadAnimation(this, R.anim.hide_to_bottom));
        }, 300);

        getCadastrarLead_fab().setOnClickListener((v) -> {
            actionMenu.close(true);
            startActivityForResult(new Intent(this, CadastroLeadActivity.class), 0);
        });
        getCadastrarVisita_fab().setOnClickListener(v -> {
            actionMenu.close(true);
            startActivityForResult(new Intent(this, SelecionarClienteActivity.class), 0);
        });
        getListView().setOnScrollListener(getOnScrollListener());
        getEmptyButton().setOnClickListener(getOnEmptyButtonClickListener());
        getDrawerLayout().addDrawerListener(barDrawerToggle);
        getNavigationView().setNavigationItemSelectedListener(mOnNavigationItemSelectedListener(this, getDrawerLayout()));
    }

    @Override
    public void initializeComponents() {
        toolbar = getToolbar();

        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_white);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        barDrawerToggle = new ActionBarDrawerToggle(this, getDrawerLayout(), getToolbar(), R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    }
    //endregion
}
