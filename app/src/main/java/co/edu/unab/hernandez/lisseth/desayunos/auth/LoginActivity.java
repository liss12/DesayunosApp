package co.edu.unab.hernandez.lisseth.desayunos.auth;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import co.edu.unab.hernandez.lisseth.desayunos.MainActivity;
import co.edu.unab.hernandez.lisseth.desayunos.R;
import co.edu.unab.hernandez.lisseth.desayunos.models.UsuarioEmpresa;
import co.edu.unab.hernandez.lisseth.desayunos.pagesAdmin.InicioActivity;

public class LoginActivity extends AppCompatActivity {
    private EditText etCorreo, etContrasena;
    private ImageButton ibAtras;
    private Button btnLogin, btnOlvideContrasena, btnRegistro;
    private FirebaseAuth mAuth;
    private UsuarioEmpresa usuarioEmpresa;
    private FirebaseFirestore db ;
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        etCorreo = findViewById(R.id.et_correo_login);
        etContrasena = findViewById(R.id.et_login_contrasena);
        btnLogin = findViewById(R.id.btn_login_login);
        ibAtras = findViewById(R.id.btn_atras_login);
        btnOlvideContrasena = findViewById(R.id.tv_login_olvido);
        btnRegistro = findViewById(R.id.btn_registro_login);
        etCorreo.addTextChangedListener(loginTextWatcher);
        etContrasena.addTextChangedListener(loginTextWatcher);


        ibAtras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
            }
        });
        btnRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                iniciarSesion(etCorreo.getText().toString(), etContrasena.getText().toString());
            }
        });
        btnOlvideContrasena.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, ClaveActivity.class));
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    public void iniciarSesion(String email, String pass){
        mAuth.signInWithEmailAndPassword(email,pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("Login", "Entrando con correo: Aprobado");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("Login", "Entrando con correo: Denegado", task.getException());
                            Toast.makeText(LoginActivity.this, "Autenticación fallida.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                            // ...
                        }

                        // ...
                    }
                });
    }
    private void updateUI(FirebaseUser user) {
        if (user != null) {
            if (user.isEmailVerified()) {
                DocumentReference docRef = db.collection("Usuarios").document(user.getUid());
                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                Log.d("GetUserSata", "DocumentSnapshot data: " + document.getData());
                                usuarioEmpresa =document.toObject(UsuarioEmpresa.class);
                                Intent i =new Intent(LoginActivity.this, InicioActivity.class);
                                i.putExtra("usuario", usuarioEmpresa);
                                startActivity(i);

                            } else {
                                Log.d("ObtenerDatosDeUsuario", "No existe el documento");
                            }
                        } else {
                            Log.d("ObtenerDatosDeUsuario", "Fallas con ", task.getException());
                        }
                    }
                });
            } else {
                usuarioEmpresa = new UsuarioEmpresa();
                Toast.makeText(LoginActivity.this, "Por favor da clic en el enlace que llegó a tu correo para verificar la cuenta.", Toast.LENGTH_LONG).show();
            }
        }
    }
    public TextWatcher loginTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }
        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if(!etCorreo.getText().toString().isEmpty()&&!etContrasena.getText().toString().isEmpty()){
                btnLogin.setEnabled(true);
                btnLogin.setTextColor(getColor(R.color.btn_enabled));
            }else{
                btnLogin.setEnabled(false);
                btnLogin.setTextColor(getColor(R.color.btn_disabled));}

        }
        @Override
        public void afterTextChanged(Editable s) {
        }
    };

}
                        /*usuarioEmpres.setIduser(document.getString("iduser"));
                        usuarioEmpres.setCorreo(document.getString("correo"));
                        usuarioEmpres.setDescripcion(document.getString("descripcion"));
                        usuarioEmpres.setDireccion(document.getString("direccion"));
                        usuarioEmpres.setTelefono(document.getString("telefono"));
                        usuarioEmpres.setUrl_foto(document.getString("url_foto"));
                        usuarioEmpres.setNombre(document.getString("nombre"));
                        usuarioEmpres.setLatitud(document.getString("latitud"));
                        usuarioEmpres.setLongitud(document.getString("longitud"));*/
