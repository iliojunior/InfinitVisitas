package br.com.infinitsolucoes.infinitvisitas.ActivityControllers.Cadastro;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import br.com.infinitsolucoes.infinitvisitas.Business.Data.CRUD.EmpresaCRUD;
import br.com.infinitsolucoes.infinitvisitas.Business.Data.CRUD.VisitaCRUD;
import br.com.infinitsolucoes.infinitvisitas.Controllers.VisitasController;
import br.com.infinitsolucoes.infinitvisitas.Fragment.DatePickerFragment;
import br.com.infinitsolucoes.infinitvisitas.Fragment.TimePickerFragment;
import br.com.infinitsolucoes.infinitvisitas.Interfaces.IInfinitActivity;
import br.com.infinitsolucoes.infinitvisitas.Models.Empresas;
import br.com.infinitsolucoes.infinitvisitas.Models.Visitas;
import br.com.infinitsolucoes.infinitvisitas.R;

import static br.com.infinitsolucoes.infinitvisitas.Utils.Utils.EXTRA_EMPRESA_ID;
import static br.com.infinitsolucoes.infinitvisitas.Utils.Utils.EXTRA_VISITA_ID;

public class CadastroVisitasActivity extends AppCompatActivity implements IInfinitActivity {

    private static final String TAG = "CadastroVisita";
    private int mEmpresaId;
    private int mVisitaId;
    private Empresas mEmpresa;
    private Visitas mVisita;
    private TextView inputNomeEmpresa;
    private EditText inputData;
    private EditText inputHora;
    private EditText inputObservacao;
    private final SimpleDateFormat mDataFormat = new SimpleDateFormat("dd/MM/yyyy");
    private final SimpleDateFormat mHoraFormat = new SimpleDateFormat("HH:mm");
    private final Calendar mCalendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_visita);

        initializeComponents();
        initializeListeners();
        initializeValues();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.cadastro_visita_actionbar, menu);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.salvar_cadastro:
                salvarVisita();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void initializeComponents() {


        inputNomeEmpresa = (TextView) findViewById(R.id.nome_empresa);
        inputData = (EditText) findViewById(R.id.input_data);
        inputHora = (EditText) findViewById(R.id.input_hora);
        inputObservacao = (EditText) findViewById(R.id.input_observacao);
    }

    @Override
    public void initializeListeners() {
        inputData.setOnClickListener(this::selectDate);
        inputData.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus)
                selectDate(v);
        });
        inputHora.setOnClickListener(this::selectHour);
        inputHora.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus)
                selectHour(v);
        });
    }

    @Override
    public void initializeValues() {
        if (getIntent().hasExtra(EXTRA_EMPRESA_ID)) {
            mEmpresaId = getIntent().getIntExtra(EXTRA_EMPRESA_ID, 0);
        } else if (getIntent().hasExtra(EXTRA_VISITA_ID)) {
            mVisitaId = getIntent().getIntExtra(EXTRA_VISITA_ID, 0);
            setVisita(mVisitaId);
        } else {
            final AlertDialog.Builder builder = new AlertDialog.Builder(CadastroVisitasActivity.this);
            builder.setTitle("Visita");
            builder.setMessage("Empresa inválida!");
            builder.setPositiveButton("OK", (dialog, which) -> finish());
            builder.create().show();
        }

        inputNomeEmpresa.setText(getEmpresa().getNome());
        inputData.setText(mDataFormat.format(mCalendar.getTime()));
        inputHora.setText(mHoraFormat.format(mCalendar.getTime()));
    }

    public void setVisita(final int visitaId) {
        final Visitas visita;
        try {
            visita = new VisitaCRUD(CadastroVisitasActivity.this).get(visitaId);
        } catch (SQLException e) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(CadastroVisitasActivity.this);
            builder.setTitle("Visitas");
            builder.setMessage("Visita inválida");
            builder.setPositiveButton("OK", null);
            builder.create().show();
            Log.e(TAG, e.getMessage(), e);
            return;
        }

        mEmpresaId = visita.getEmpresaId();
        inputNomeEmpresa.setText(visita.getEmpresa().getNome());
        inputObservacao.setText(visita.getObservacao());
        inputData.setText(mDataFormat.format(visita.getData().getTime()));
        inputHora.setText(mHoraFormat.format(visita.getData().getTime()));
    }

    public Empresas getEmpresa() {
        if (mEmpresa != null
                && mEmpresa.get_empresaId() == mEmpresaId)
            return mEmpresa;
        try {
            mEmpresa = new EmpresaCRUD(CadastroVisitasActivity.this).get(mEmpresaId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return mEmpresa;
    }

    private Visitas getVisita() {
        if (mVisita != null
                && mVisita.get_visitaId() == mVisitaId) {
            return mVisita;
        }
        if (mVisitaId != 0) {
            try {
                mVisita = new VisitaCRUD(CadastroVisitasActivity.this).get(mVisitaId);
                return mVisita;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        final Visitas visita = new Visitas(getEmpresa());
        visita.setData(getDate());
        visita.setObservacao(inputObservacao.getText().toString());
        return visita;
    }

    private Calendar getDate() {
        final Calendar calendar = Calendar.getInstance();
        final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        final String dataString = inputData.getText().toString()
                + " " + inputHora.getText().toString();
        try {
            calendar.setTime(dateFormat.parse(dataString));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return calendar;
    }

    private void salvarVisita() {
        new SaveCadastro(CadastroVisitasActivity.this).execute(getVisita());
    }

    private void selectHour(View v) {
        final TimePickerFragment mFragment = new TimePickerFragment();
        final Calendar mCalendar = Calendar.getInstance();
        try {
            mCalendar.setTime(mHoraFormat.parse(inputHora.getText().toString()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        mFragment.setHour(mCalendar.get(Calendar.HOUR_OF_DAY));
        mFragment.setMinute(mCalendar.get(Calendar.MINUTE));

        mFragment.setOnTimeSetListener(this::showHour);

        mFragment.show(getSupportFragmentManager(), "TimePicker");
    }

    private void showHour(TimePicker timePicker, int hour, int minute) {
        final Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        showHour(calendar);
    }

    private void showHour(Calendar calendar) {
        inputHora.setText(mHoraFormat.format(calendar.getTime()));
    }

    private void selectDate(View v) {
        final DatePickerFragment mFragment = new DatePickerFragment();
        final Calendar mCalendar = Calendar.getInstance();
        try {
            mCalendar.setTime(mDataFormat.parse(inputData.getText().toString()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        mCalendar.set(Calendar.MONTH, mCalendar.get(Calendar.MONTH) - 1);
        mFragment.setDay(mCalendar.get(Calendar.DAY_OF_MONTH));
        mFragment.setMonth(mCalendar.get(Calendar.MONTH) + 1);
        mFragment.setYear(mCalendar.get(Calendar.YEAR));

        mFragment.setmOnDateSetListener(this::showDate);

        mFragment.show(getSupportFragmentManager(), "DatePicker");
    }

    private void showDate(DatePicker datePicker, int year, int month, int dayOfMonth) {
        final Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        showDate(calendar);
    }

    private void showDate(final Calendar calendar) {
        if (calendar.before(Calendar.getInstance())) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(CadastroVisitasActivity.this);
            builder.setTitle("Visitas");
            builder.setMessage("Data inválida.");
            builder.setPositiveButton("OK", (dialog, which) -> selectDate(null));
            builder.create().show();
        } else {
            inputData.setText(mDataFormat.format(calendar.getTime()));
        }
    }

    private final class SaveCadastro extends AsyncTask<Visitas, Void, Boolean> {
        private final Context mContext;
        private final ProgressDialog mProgressDialog;
        private final AlertDialog.Builder mBuilder;
        private final VisitasController mController;

        public SaveCadastro(final Context mContext) {
            this.mContext = mContext;
            this.mProgressDialog = new ProgressDialog(mContext);
            this.mBuilder = new AlertDialog.Builder(mContext);
            this.mController = new VisitasController(mContext);
        }

        @Override
        protected void onCancelled() {
            mProgressDialog.dismiss();

            mBuilder.setTitle(mContext.getString(R.string.title_activity_cadastro_visitas));
            mBuilder.setMessage("Cancelado pelo usuário");
            mBuilder.setPositiveButton("OK", null);
            mBuilder.create().show();
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            mProgressDialog.dismiss();

            String title = "Falha";
            String message = "Tente novamente";
            if (aBoolean == null)
                message = "Ocorreu um erro interno";
            else if (aBoolean) {
                title = "Sucesso";
                message = "Salvo com sucesso.";
            }

            mBuilder.setTitle(title);
            mBuilder.setMessage(message);
            mBuilder.setPositiveButton("OK", (d, w) -> {
                final Intent intent = new Intent();
                setResult(Activity.RESULT_OK, intent);
                ((Activity) mContext).finish();
            });
            mBuilder.setCancelable(false);
            final AlertDialog alertDialog = mBuilder.create();
            alertDialog.setCanceledOnTouchOutside(false);
            alertDialog.show();
        }

        @Override
        protected void onPreExecute() {
            mProgressDialog.setTitle(mContext.getString(R.string.title_activity_cadastro_visitas));
            mProgressDialog.setMessage("Salvando");
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.show();
        }

        @Override
        protected Boolean doInBackground(Visitas... params) {
            final Visitas visita = params[0];
            return mController.save(visita);
        }
    }
}
