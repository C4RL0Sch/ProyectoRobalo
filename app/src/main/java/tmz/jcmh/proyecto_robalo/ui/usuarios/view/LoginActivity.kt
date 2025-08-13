package tmz.jcmh.proyecto_robalo.ui.usuarios.view

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import tmz.jcmh.proyecto_robalo.databinding.ActivityLoginBinding
import tmz.jcmh.proyecto_robalo.ui.MainActivity
import tmz.jcmh.proyecto_robalo.ui.usuarios.viewmodel.LoginViewModel
import tmz.jcmh.proyecto_robalo.ui.usuarios.viewmodel.UsuariosViewModel

class LoginActivity : AppCompatActivity() {
    val loginViewModel: LoginViewModel by viewModels()

    private lateinit var binding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLogin.setOnClickListener(){
            if(binding.txtUser.text.isBlank() || binding.txtPassword.text.isBlank()){
                Toast.makeText(this, "Debe llenar todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val username = binding.txtUser.text.toString()
            val password = binding.txtPassword.text.toString()

            if(username=="ADMIN" && password=="ADMIN"){
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("isAdmin",true)
                finish()
                startActivity(intent)
            }
            else{
                loginViewModel.login(username, password)
            }
        }

        loginViewModel.isLoged.observe(this){
            if(it==false){
                Toast.makeText(this, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show()
            }
            else{
                val intent = Intent(this, MainActivity::class.java)
                finish()
                startActivity(intent)
            }
        }
    }
}