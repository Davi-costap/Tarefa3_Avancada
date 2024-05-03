package com.example.davi_avancada;

import com.example.mylibraryRegion.Region;


import android.os.Bundle;
import android.widget.ListView;
import java.util.List;
import androidx.appcompat.app.AppCompatActivity;

public class RegionFila extends AppCompatActivity {
    private ListView listRegions;
    private RegionLista adapterLista;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regioes_fila);

        listRegions = findViewById(R.id.listVRegions);

        List<Region> regioes = (List<Region>) getIntent().getSerializableExtra("listaRegioes");
        adapterLista = new RegionLista(this, regioes);
        listRegions.setAdapter(adapterLista);

    }
}
