package com.example.mp_team_prj

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity: AppCompatActivity(){
    var googleSigninClient : GoogleSignInClient? = null

    val RC_SIGN_IN = 1000




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState )
        setContentView(R.layout.activity_login)


        var gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()

    googleSigninClient = GoogleSignIn.getClient(this,gso)


        google_login_button.setOnClickListener {

            var signInIntent = googleSigninClient?.signInIntent


            startActivityForResult(signInIntent,RC_SIGN_IN)
        }

    }
    fun FirebaseAuthWithGoogle(acct : GoogleSignInAccount?){
        var credential = GoogleAuthProvider.getCredential(acct?.idToken,null)
        FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener{
            task ->
            if(task.isSuccessful){
                println("googleLogin Success ")

                var auth = FirebaseAuth.getInstance()
                val nextIntent = Intent(this,MainActivity::class.java)
                nextIntent.putExtra("uid",auth.currentUser?.uid)

                startActivity(nextIntent)



            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN){
            var task = GoogleSignIn.getSignedInAccountFromIntent(data)
            var account = task.getResult(ApiException::class.java)
            FirebaseAuthWithGoogle(account)

        }
    }




    }
