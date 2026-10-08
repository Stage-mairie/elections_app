package com.example.bureaudevotemsa

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.bureaudevotemsa.ui.theme.BureauDeVoteMSATheme
import com.example.bureaudevotemsa.ui.theme.components.BackgroundGradient
import com.example.bureaudevotemsa.ui.theme.components.UserForm

class MainActivity : ComponentActivity() {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BureauDeVoteMSATheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    content = {
                        BackgroundGradient {
                            UserForm { nom, prenom, dateNaissance ->
                                val intent = Intent(this, ResultsActivity::class.java)
                                intent.putExtra("EXTRA_NOM", nom)
                                intent.putExtra("EXTRA_PRENOM", prenom)
                                intent.putExtra("EXTRA_DATE_NAISSANCE", dateNaissance)
                                startActivity(intent)

                            }
                        }
                    }
                )
            }
        }
    }
}

