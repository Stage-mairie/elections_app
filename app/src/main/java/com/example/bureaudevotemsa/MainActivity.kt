package com.example.bureaudevotemsa

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.example.bureaudevotemsa.ui.theme.BureauDeVoteMSATheme
import com.example.bureaudevotemsa.ui.theme.components.BackgroundGradient
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BureauDeVoteMSATheme {
                Scaffold { innerPadding ->
                    BackgroundGradient(
                        {
                            MainScreen(
                                modifier = Modifier.padding(innerPadding),
                                onNavigate = { context ->
                                    val intent = Intent(context, ResultsActivity::class.java)
                                    context.startActivity(intent)
                                }
                            )
                        },
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    BureauDeVoteMSATheme {
        Greeting("Android")
    }
}
@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    onNavigate: (context: android.content.Context) -> Unit
) {
    val context = LocalContext.current

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Bienvenue sur la première activity",
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { onNavigate(context) }) {
            Text("Aller à la page de rés", color = Color.Black)
        }
    }
}