package com.example.davi_avancada;

import com.example.mylibraryRegion.Region;


import android.os.Bundle;
import android.widget.ListView;
import java.util.List;
import androidx.appcompat.app.AppCompatActivity;

public class RegionFila extends AppCompatActivity {
    private ListView listVRegions;
    private RegionLista adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regioes_fila);

        listVRegions = findViewById(R.id.listVRegions);

        // Obter a lista de regiões passada pelo Intent
        List<Region> regioes = (List<Region>) getIntent().getSerializableExtra("listaRegioes");
        adapter = new RegionLista(this, regioes);
        listVRegions.setAdapter(adapter);

        // Aqui você pode adicionar outras configurações ou interações com a lista
    }
}
