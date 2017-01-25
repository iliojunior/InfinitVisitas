package br.com.infinitsolucoes.infinitvisitas.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import br.com.infinitsolucoes.infinitvisitas.ActivityControllers.Consulta.ConsultaAgendaActivity;
import br.com.infinitsolucoes.infinitvisitas.ActivityControllers.Consulta.ConsultaLeadActivity;
import br.com.infinitsolucoes.infinitvisitas.ActivityControllers.Consulta.ConsultaLeadsQualificadosActivity;
import br.com.infinitsolucoes.infinitvisitas.ActivityControllers.Consulta.ConsultaOportunidadeActivity;
import br.com.infinitsolucoes.infinitvisitas.ActivityControllers.Consulta.ConsultaVendasActivity;
import br.com.infinitsolucoes.infinitvisitas.ActivityControllers.Consulta.ContatosActivity;
import br.com.infinitsolucoes.infinitvisitas.Interfaces.IToolbarExec;
import br.com.infinitsolucoes.infinitvisitas.R;

public final class ActivityUtils {

    private ActivityUtils() {
    }

    public static final NavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener(Context context, DrawerLayout drawerLayout) {
        NavigationView.OnNavigationItemSelectedListener listener = item -> {
            int itemID = item.getItemId();
            switch (itemID) {
                case R.id.nav_agenda:
                    openActivity(context, drawerLayout, ConsultaAgendaActivity.class);
                    break;
                case R.id.nav_leads:
                    openActivity(context, drawerLayout, ConsultaLeadActivity.class);
                    break;
                case R.id.nav_leads_qualificados:
                    openActivity(context, drawerLayout, ConsultaLeadsQualificadosActivity.class);
                    break;
                case R.id.nav_oportunidade:
                    openActivity(context, drawerLayout, ConsultaOportunidadeActivity.class);
                    break;
                case R.id.nav_venda:
                    openActivity(context, drawerLayout, ConsultaVendasActivity.class);
                    break;
                case R.id.nav_contatos:
                    openActivity(context, drawerLayout, ContatosActivity.class);
                case R.id.nav_logoff:
                    break;
            }
            return false;
        };
        return listener;
    }

    private static final void openActivity(Context context, DrawerLayout drawerLayout, Class<?> aClass) {
        if (context.getClass() == aClass) {
            drawerLayout.closeDrawers();
        } else {
            context.startActivity(new Intent(context, aClass));
            ((Activity) context).finish();
        }
    }

    public static final void loadToolbar(AppCompatActivity context, Toolbar toolbar, IToolbarExec exec) {
        context.setSupportActionBar(toolbar);
        if (context.getSupportActionBar() != null && exec != null) {
            exec.execute();
        }
    }
}
