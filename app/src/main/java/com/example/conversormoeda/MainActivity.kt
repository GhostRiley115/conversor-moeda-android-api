package com.example.conversormoeda

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.conversormoeda.api.ClientApi
import com.example.conversormoeda.model.FinanceResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    var cotacaoUSD: Double = 0.0
    var cotacaoEUR: Double = 0.0
    var cotacaoARS: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars =
                insets.getInsets(WindowInsetsCompat.Type.systemBars())

            v.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                systemBars.bottom
            )

            insets
        }

        val moedas = listOf(
            "Dólar",
            "Euro",
            "Peso Argentino"
        )

        val spMoeda = findViewById<Spinner>(R.id.spMoeda)

        val adapter = ArrayAdapter(
            this,
            R.layout.spinner_item,
            moedas
        )

        adapter.setDropDownViewResource(
            R.layout.spinner_dropdown_item
        )

        spMoeda.adapter = adapter

        carregarCotacoes()

        val button = findViewById<Button>(R.id.btnConversor)
        val editValor = findViewById<EditText>(R.id.editValor)
        val resultado = findViewById<TextView>(R.id.txtResultado)

        button.setOnClickListener {

            val valor = editValor.text
                .toString()
                .toDoubleOrNull()

            if (valor == null) {
                resultado.text = "Digite um valor válido"
                return@setOnClickListener
            }

            val moedaSelecionada =
                spMoeda.selectedItem.toString()

            val valorCotacao = when (moedaSelecionada) {

                "Dólar" ->
                    valor * cotacaoUSD

                "Euro" ->
                    valor * cotacaoEUR

                "Peso Argentino" ->
                    valor * cotacaoARS

                else ->
                    0.0
            }

            resultado.text =
                "Valor em reais: R$ %.2f".format(valorCotacao)
        }
    }

    private fun carregarCotacoes() {

        ClientApi.api.getCotacoes().enqueue(
            object : Callback<FinanceResponse> {

                override fun onResponse(
                    call: Call<FinanceResponse>,
                    response: Response<FinanceResponse>
                ) {

                    if (response.isSuccessful) {

                        val body = response.body()

                        cotacaoUSD =
                            body?.results?.currencies?.USD?.buy ?: 0.0

                        cotacaoEUR =
                            body?.results?.currencies?.EUR?.buy ?: 0.0

                        cotacaoARS =
                            body?.results?.currencies?.ARS?.buy ?: 0.0
                    }
                }

                override fun onFailure(
                    call: Call<FinanceResponse>,
                    t: Throwable
                ) {

                    Toast.makeText(
                        this@MainActivity,
                        "Erro ao buscar cotações",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        )
    }
}