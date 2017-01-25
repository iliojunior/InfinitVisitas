package br.com.infinitsolucoes.infinitvisitas.ActivityControllers;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import br.com.infinitsolucoes.infinitvisitas.ActivityControllers.Consulta.ConsultaAgendaActivity;
import br.com.infinitsolucoes.infinitvisitas.R;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_login);
    }

    public void Logar(View view) {
        startActivity(new Intent(this, ConsultaAgendaActivity.class));
    }
}
