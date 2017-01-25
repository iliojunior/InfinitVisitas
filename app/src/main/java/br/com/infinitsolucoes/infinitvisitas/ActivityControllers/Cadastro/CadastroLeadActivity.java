package br.com.infinitsolucoes.infinitvisitas.ActivityControllers.Cadastro;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import br.com.infinitsolucoes.infinitvisitas.Business.Data.CRUD.EmpresaCRUD;
import br.com.infinitsolucoes.infinitvisitas.Models.Empresas;
import br.com.infinitsolucoes.infinitvisitas.Models.Enderecos;
import br.com.infinitsolucoes.infinitvisitas.R;
import br.com.infinitsolucoes.infinitvisitas.Utils.CpfCnpjMaskUtil;
import br.com.infinitsolucoes.infinitvisitas.Interfaces.IInfinitActivity;
import br.com.infinitsolucoes.infinitvisitas.Utils.LocationService;
import br.com.infinitsolucoes.infinitvisitas.Utils.TelefoneMaskUtil;

import static br.com.infinitsolucoes.infinitvisitas.Utils.Utils.EXTRA_EMPRESA_ID;
import static br.com.infinitsolucoes.infinitvisitas.Utils.Utils.getDataAtual;

public class CadastroLeadActivity extends AppCompatActivity implements IInfinitActivity {

    //region Fields
    private final String TAG = "CadastroLeadActivity";
    private EditText inputNomeEmpresa;
    private EditText inputEmail;
    private EditText inputEnderecoLogradouro;
    private EditText inputEnderecoNumero;
    private EditText inputEnderecoBairro;
    private EditText inputEnderecoMunicipio;
    private EditText inputEnderecoUF;
    private EditText inputTelefone;
    private EditText inputCelular;
    private EditText inputResponsavel;
    private EditText inputCpfCnpj;
    private SeekBar inputNivelInteresse;
    private ImageButton inputCurrentLocation;
    private TextView outputNivelInteresse;
    private ProgressDialog progressDialogLocation;
    //endregion

    private int idLead = 0;

    //region Override's
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_lead);

        initializeComponents();
        initializeListeners();
        initializeValues();

        if (getIntent().hasExtra(EXTRA_EMPRESA_ID)) {
            setLeadContext(getIntent().getIntExtra(EXTRA_EMPRESA_ID, 0));
        }
    }

    public void setLeadContext(int idLead) {
        final EmpresaCRUD crud = new EmpresaCRUD(CadastroLeadActivity.this);
        boolean isValid = false;
        if (crud.exists(idLead)) {
            Empresas empresas;
            try {
                empresas = crud.get(idLead);
                inputNomeEmpresa.setText(empresas.getNome());
                inputEmail.setText(empresas.getEmail());
                inputTelefone.setText(empresas.getTelefone());
                inputEnderecoLogradouro.setText(empresas.getEndereco().getLogradouro());
                inputEnderecoNumero.setText(empresas.getEndereco().getNumero());
                inputEnderecoBairro.setText(empresas.getEndereco().getBairro());
                inputEnderecoMunicipio.setText(empresas.getEndereco().getMunicipio());
                inputEnderecoUF.setText(empresas.getEndereco().getUf());
                inputCelular.setText(empresas.getCelular());
                inputResponsavel.setText(empresas.getResponsavel());
                inputNivelInteresse.setProgress(empresas.getNivelInteresse());
                inputCpfCnpj.setText(empresas.getCpf_cnpj());
                this.idLead = empresas.get_empresaId();

                isValid = true;
            } catch (SQLException e) {
                Log.e(TAG, e.getMessage(), e);
            }
        } else {
            final Exception e = new Exception("Not found lead: " + idLead);
            Log.e(TAG, e.getMessage(), e);
        }
        if (!isValid) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(CadastroLeadActivity.this);
            builder.setTitle("Falha");
            builder.setMessage("Ocorreu um falha interna");
            builder.setPositiveButton("OK", null);
            builder.create().show();
            setResult(Activity.RESULT_OK);
            finish();
        }
    }

    @Override
    public void initializeComponents() {
        inputNomeEmpresa = (EditText) findViewById(R.id.cadastro_nomeEmpresaCliente);
        inputEmail = (EditText) findViewById(R.id.input_email);
        inputTelefone = (EditText) findViewById(R.id.input_telefone);
        inputEnderecoLogradouro = (EditText) findViewById(R.id.input_endereco_logradouro);
        inputEnderecoNumero = (EditText) findViewById(R.id.input_endereco_numero);
        inputEnderecoBairro = (EditText) findViewById(R.id.input_endereco_bairro);
        inputEnderecoMunicipio = (EditText) findViewById(R.id.input_endereco_municipio);
        inputEnderecoUF = (EditText) findViewById(R.id.input_endereco_uf);
        inputCelular = (EditText) findViewById(R.id.input_celular);
        inputResponsavel = (EditText) findViewById(R.id.input_responsavel);
        inputCpfCnpj = (EditText) findViewById(R.id.input_cpf_cnpj);
        outputNivelInteresse = (TextView) findViewById(R.id.output_nivel_interesse_textView);
        inputNivelInteresse = (SeekBar) findViewById(R.id.input_nivel_interesse_seekbar);
        inputCurrentLocation = (ImageButton) findViewById(R.id.imageButton_current_location);
    }

    @Override
    public void initializeListeners() {
        inputCurrentLocation.setOnClickListener(v -> getMyLocation());

        inputNivelInteresse.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                outputNivelInteresse.setText(getString(R.string.nivel_interesse_text) + " " + String.valueOf(inputNivelInteresse.getProgress()));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        inputCpfCnpj.addTextChangedListener(new CpfCnpjMaskUtil().insert(inputCpfCnpj));
        inputTelefone.addTextChangedListener(new TelefoneMaskUtil().insert(inputTelefone));
        inputCelular.addTextChangedListener(new TelefoneMaskUtil().insert(inputCelular));
    }

    @Override
    public void initializeValues() {
        final Calendar calendar = getDataAtual();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        outputNivelInteresse.setText(getString(R.string.nivel_interesse_text) + " " + String.valueOf(inputNivelInteresse.getProgress()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.cadastro_visita_actionbar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.salvar_cadastro:
                salvarCadastro();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    //endregion

    private final class SaveCadastroAsync extends AsyncTask<Empresas, Void, Boolean> {
        private final Context context;
        private final ProgressDialog progressDialog;
        private final AlertDialog.Builder builder;

        public SaveCadastroAsync(final Context context) {
            this.context = context;
            progressDialog = new ProgressDialog(context);
            builder = new AlertDialog.Builder(context);
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
            builder.setPositiveButton("OK", (dialog, which) -> {
                Intent result = new Intent();
                setResult(Activity.RESULT_OK, result);
                ((Activity) context).finish();
            });
            builder.setCancelable(false);
            final AlertDialog alertDialog = builder.create();
            alertDialog.setCanceledOnTouchOutside(false);
            alertDialog.show();
        }

        @Override
        protected void onPreExecute() {

            progressDialog.setTitle("Lead");
            progressDialog.setMessage("Salvando, aguarde.");
            progressDialog.setIndeterminate(true);
            progressDialog.show();
        }

        @Override
        protected Boolean doInBackground(Empresas... params) {
            Empresas novaEmpresa = params[0];

            try {
                return (new EmpresaCRUD(context).save(novaEmpresa).get_empresaId() > 0);
            } catch (Exception e) {
                Log.e(TAG, e.getMessage(), e);
            }

            return null;
        }
    }

    private Empresas getEmpresaContext() {
        Enderecos endereco = new Enderecos(inputEnderecoLogradouro.getText().toString(),
                inputEnderecoMunicipio.getText().toString(),
                inputEnderecoUF.getText().toString());
        endereco.setNumero(inputEnderecoNumero.getText().toString());
        endereco.setBairro(inputEnderecoBairro.getText().toString());

        Empresas empresa = new Empresas(idLead, inputNomeEmpresa.getText().toString(), endereco);
        empresa.setTelefone(inputTelefone.getText().toString());
        empresa.setCelular(inputCelular.getText().toString());
        empresa.setCpf_cnpj(inputCpfCnpj.getText().toString());
        empresa.setResponsavel(inputResponsavel.getText().toString());
        empresa.setEmail(inputEmail.getText().toString());
        empresa.setNivelInteresse(inputNivelInteresse.getProgress());

        return empresa;
    }

    private void salvarCadastro() {
        List<String> fieldsEmpty = new ArrayList<>();
        if (inputTelefone.getText().toString().isEmpty()
                && inputCelular.getText().toString().isEmpty()) {
            inputTelefone.requestFocus();
            fieldsEmpty.add("Telefone");
        }
        if (inputNomeEmpresa.getText().toString().isEmpty()) {
            inputNomeEmpresa.requestFocus();
            fieldsEmpty.add("Nome da empresa");
        }
        if (fieldsEmpty.isEmpty()) {
            new SaveCadastroAsync(CadastroLeadActivity.this).execute(getEmpresaContext());
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(CadastroLeadActivity.this);
            builder.setTitle("Campos vazios");
            builder.setMessage(TextUtils.join("\r\n", fieldsEmpty));
            builder.setPositiveButton("OK", null);
            builder.create().show();
        }
    }

    private void getMyLocation() {
        progressDialogLocation = new ProgressDialog(CadastroLeadActivity.this);
        progressDialogLocation.setMessage("Obtendo endereço...");
        progressDialogLocation.setTitle("Aguarde...");
        progressDialogLocation.show();

        LocationService locationService = new LocationService(this);
        if (locationService.canGetLocation()) {
            Location location = new Location("");
            location.setLatitude(locationService.getLatitude());
            location.setLongitude(locationService.getLongitude());

            GetMyAdressTask getMyAdressTask = new GetMyAdressTask();
            getMyAdressTask.execute(location);
        } else {
            progressDialogLocation.dismiss();
            locationService.showSettingsAlert();
        }
    }

    private class GetMyAdressTask extends AsyncTask<Location, Integer, Address> {
        @Override
        protected Address doInBackground(Location... params) {
            if (params == null || params.length == 0)
                return null;
            double latitude = params[0].getLatitude();
            double longitute = params[0].getLongitude();
            Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
            List<Address> addresses;
            try {
                addresses = geocoder.getFromLocation(latitude, longitute, 1);
                return addresses.get(0);
            } catch (Exception e) {
                return null;
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            progressDialogLocation.setProgress(values[0]);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Address address) {
            progressDialogLocation.dismiss();
            showAdress(address);
        }
    }

    private String getSiglaUF(String UF) {
        if (UF.equalsIgnoreCase("Acre"))
            return "AC";
        else if (UF.equalsIgnoreCase("Alagoas"))
            return "AL";
        else if (UF.equalsIgnoreCase("Amapa"))
            return "SC";
        else if (UF.equalsIgnoreCase("Amazonas"))
            return "AM";
        else if (UF.equalsIgnoreCase("Bahia"))
            return "BA";
        else if (UF.equalsIgnoreCase("Ceará"))
            return "CE";
        else if (UF.equalsIgnoreCase("Distrito Federal"))
            return "DF";
        else if (UF.equalsIgnoreCase("Espírito Santo"))
            return "ES";
        else if (UF.equalsIgnoreCase("Goiás"))
            return "GO";
        else if (UF.equalsIgnoreCase("Maranhão"))
            return "MA";
        else if (UF.equalsIgnoreCase("Mato Grosso"))
            return "MT";
        else if (UF.equalsIgnoreCase("Mato Grosso do Sul"))
            return "MS";
        else if (UF.equalsIgnoreCase("Minas Gerais"))
            return "MG";
        else if (UF.equalsIgnoreCase("Pará"))
            return "PA";
        else if (UF.equalsIgnoreCase("Paraíba"))
            return "PB";
        else if (UF.equalsIgnoreCase("Paraná"))
            return "PR";
        else if (UF.equalsIgnoreCase("Pernambuco"))
            return "PE";
        else if (UF.equalsIgnoreCase("Piauí"))
            return "PI";
        else if (UF.equalsIgnoreCase("Rio de Janeiro"))
            return "RJ";
        else if (UF.equalsIgnoreCase("Rio Grande do Norte"))
            return "RN";
        else if (UF.equalsIgnoreCase("Rio Grande do Sul"))
            return "RS";
        else if (UF.equalsIgnoreCase("Rondônia"))
            return "RO";
        else if (UF.equalsIgnoreCase("Roraima"))
            return "RR";
        else if (UF.equalsIgnoreCase("Santa Catarina"))
            return "SC";
        else if (UF.equalsIgnoreCase("São Paulo"))
            return "SP";
        else if (UF.equalsIgnoreCase("Sergipe"))
            return "SE";
        else if (UF.equalsIgnoreCase("Tocantins"))
            return "TO";

        return "PR";
    }

    private void showAdress(Address address) {
        if (address != null) {
            inputEnderecoLogradouro.setText(address.getThoroughfare());
            inputEnderecoNumero.setText(address.getSubThoroughfare());
            inputEnderecoBairro.setText(address.getSubLocality());
            inputEnderecoMunicipio.setText(address.getLocality());
            inputEnderecoUF.setText(getSiglaUF(address.getAdminArea()));
        }
    }
}
