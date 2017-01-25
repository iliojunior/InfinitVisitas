package br.com.infinitsolucoes.infinitvisitas.ActivityControllers.Cadastro;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import java.sql.SQLException;

import br.com.infinitsolucoes.infinitvisitas.Business.Data.CRUD.EmpresaCRUD;
import br.com.infinitsolucoes.infinitvisitas.Business.Data.CRUD.FollowUpCRUD;
import br.com.infinitsolucoes.infinitvisitas.Componentes.EditTextWithoutEmoji;
import br.com.infinitsolucoes.infinitvisitas.Interfaces.IInfinitActivity;
import br.com.infinitsolucoes.infinitvisitas.Models.Empresas;
import br.com.infinitsolucoes.infinitvisitas.Models.FollowUp;
import br.com.infinitsolucoes.infinitvisitas.R;

import static br.com.infinitsolucoes.infinitvisitas.Utils.ActivityUtils.loadToolbar;
import static br.com.infinitsolucoes.infinitvisitas.Utils.Utils.EXTRA_EMPRESA_ID;
import static br.com.infinitsolucoes.infinitvisitas.Utils.Utils.EXTRA_FOLLOW_UP_ID;

public class CadastroFollowUpActivity extends AppCompatActivity implements IInfinitActivity {
    private static final String TAG = "CadastroFollowUp";
    private Empresas empresa;
    private int empresaId;
    private int followUpId;
    private Toolbar toolbar;
    private TextView outputNivelInteresse, outputNomeEmpresa;
    private SeekBar inputNivelInteresse;
    private EditTextWithoutEmoji inputTempoRetorno, inputObservacao;
    private CheckBox inputNaoRetornar;
    private Spinner retornoMedida;
    private ArrayAdapter<CharSequence> adapterSpinner;

    private static final AdapterView.OnItemSelectedListener LISTENER_SPINNER = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    };

    private final SeekBar.OnSeekBarChangeListener CHANGE_LISTENER = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            changeNivelInteresse(progress);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
        }
    };

    private final CompoundButton.OnCheckedChangeListener checkedChangeListener = (buttonView, isChecked) -> {
        final int visibility = isChecked ? View.GONE : View.VISIBLE;
        findViewById(R.id.layout_tempo_retorno).setVisibility(visibility);
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_follow_up);

        initializeComponents();
        initializeListeners();
        initializeValues();

        if (!empresaIsValid() && !followUpValid()) {
            final AlertDialog.Builder builder
                    = new AlertDialog.Builder(CadastroFollowUpActivity.this);
            builder.setTitle("Follow-Up");
            builder.setMessage("Empresa inválida!");
            builder.setPositiveButton("OK", (a, b) -> finish());
            builder.create().show();
        }
    }

    @Override
    public void initializeComponents() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        outputNivelInteresse = (TextView) findViewById(R.id.output_nivel_interesse_textView);
        inputNivelInteresse = (SeekBar) findViewById(R.id.input_nivel_interesse_seekbar);
        inputTempoRetorno = (EditTextWithoutEmoji) findViewById(R.id.tempo_retorno);
        inputObservacao = (EditTextWithoutEmoji) findViewById(R.id.input_observacao);
        inputNaoRetornar = (CheckBox) findViewById(R.id.nao_retornar);
        outputNomeEmpresa = (TextView) findViewById(R.id.nome_empresa);
        retornoMedida = (Spinner) findViewById(R.id.retorno_medida);
        adapterSpinner = ArrayAdapter.createFromResource(CadastroFollowUpActivity.this, R.array.tempo_medida, android.R.layout.simple_spinner_item);
    }

    @Override
    public void initializeListeners() {
        loadToolbar(this, toolbar, () -> getSupportActionBar().setDisplayHomeAsUpEnabled(true));
        inputNivelInteresse.setOnSeekBarChangeListener(CHANGE_LISTENER);
        inputNaoRetornar.setOnCheckedChangeListener(checkedChangeListener);
    }

    @Override
    public void initializeValues() {
        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        retornoMedida.setAdapter(adapterSpinner);
        retornoMedida.setOnItemSelectedListener(LISTENER_SPINNER);
        changeNivelInteresse(inputNivelInteresse.getProgress());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.cadastro_follow_up_actionbar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.salvar_cadastro:
                save();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void changeNivelInteresse(int nivelInteresse) {
        outputNivelInteresse.setText(getString(R.string.nivel_interesse_text) + String.valueOf(nivelInteresse));
    }

    private boolean followUpValid() {
        return (getIntent().hasExtra(EXTRA_FOLLOW_UP_ID)
                && getIntent().getIntExtra(EXTRA_FOLLOW_UP_ID, 0) > 0
                && setFollowUp(getIntent().getIntExtra(EXTRA_FOLLOW_UP_ID, 0)));
    }

    private boolean setFollowUp(int followUpId) {
        try {
            final FollowUp followUp = new FollowUpCRUD(CadastroFollowUpActivity.this).get(followUpId);
            setEmpresa(followUp.getEmpresaId());
            inputNivelInteresse.setProgress(followUp.getNivelInteresse());
            inputNaoRetornar.setChecked(followUp.isNaoRetornarMais());
            inputTempoRetorno.setText(String.valueOf(followUp.getRetornoTempo()));
            retornoMedida.setSelection(adapterSpinner.getPosition(followUp.getRetornoMedida()));
            inputObservacao.setText(followUp.getObservacao());

            this.followUpId = followUpId;
            return true;
        } catch (SQLException e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return false;
    }

    private boolean empresaIsValid() {
        return (getIntent().hasExtra(EXTRA_EMPRESA_ID)
                && getIntent().getIntExtra(EXTRA_EMPRESA_ID, 0) > 0
                && setEmpresa(getIntent().getIntExtra(EXTRA_EMPRESA_ID, 0)));
    }

    private Empresas getEmpresa() {
        if (empresa != null && empresa.get_empresaId() > 0)
            return empresa;
        setEmpresa(empresaId);
        return empresa;
    }

    private boolean setEmpresa(int empresaId) {
        try {
            this.empresa = new EmpresaCRUD(CadastroFollowUpActivity.this).get(empresaId);
            this.empresaId = empresaId;

            inputNivelInteresse.setProgress(empresa.getNivelInteresse());
            outputNomeEmpresa.setText(empresa.getNome());
            return true;
        } catch (SQLException e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return false;
    }

    private FollowUp getFollowUp() {
        final FollowUp novoFollowUp = new FollowUp(followUpId, getEmpresa());
        novoFollowUp.setNivelInteresse(inputNivelInteresse.getProgress());
        novoFollowUp.setObservacao(inputObservacao.getText().toString());
        int tempoRetorno = 0;
        if (!inputTempoRetorno.getText().toString().isEmpty())
            tempoRetorno = Integer.parseInt(inputTempoRetorno.getText().toString());
        novoFollowUp.setRetornoTempo(tempoRetorno);
        novoFollowUp.setNaoRetornarMais(inputNaoRetornar.isChecked());
        novoFollowUp.setRetornoMedida(retornoMedida.getSelectedItem().toString());
        return novoFollowUp;
    }

    private void save() {
        if (inputTempoRetorno.getText().toString().isEmpty() && !inputNaoRetornar.isChecked()) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(CadastroFollowUpActivity.this);
            builder.setTitle("Campo Vazio");
            builder.setMessage("Tempo para retorno");
            builder.setPositiveButton("OK", null);
            builder.create().show();
        } else {
            new SaveAsync(CadastroFollowUpActivity.this).execute(getFollowUp());
        }
    }

    private final class SaveAsync extends AsyncTask<FollowUp, Void, Boolean> {
        private final Context context;
        private final ProgressDialog progressDialog;
        private final AlertDialog.Builder builder;

        public SaveAsync(Context context) {
            this.context = context;
            progressDialog = new ProgressDialog(context);
            builder = new AlertDialog.Builder(context);
        }

        @Override
        protected Boolean doInBackground(FollowUp... params) {
            try {
                return new FollowUpCRUD(context).save(params[0]).get_followUpId() > 0;
            } catch (Exception e) {
                Log.e(TAG, e.getMessage(), e);
            }
            return null;
        }

        @Override
        protected void onCancelled() {
            progressDialog.dismiss();
            builder.setTitle("Follow-Up");
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
                message = "Salvo com sucesso!";
            }

            builder.setTitle(title);
            builder.setMessage(message);
            builder.setPositiveButton("OK", (d, w) -> {
                final Intent intent = new Intent();
                setResult(Activity.RESULT_OK, intent);
                ((Activity) context).finish();
            });
            builder.setCancelable(false);
            final AlertDialog alertDialog = builder.create();
            alertDialog.setCanceledOnTouchOutside(false);
            alertDialog.show();
        }

        @Override
        protected void onPreExecute() {
            progressDialog.setTitle("Follow-Up");
            progressDialog.setMessage("Salvando...");
            progressDialog.setIndeterminate(true);
            progressDialog.show();
        }
    }
}
