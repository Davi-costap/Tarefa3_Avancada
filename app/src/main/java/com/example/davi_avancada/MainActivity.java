package com.example.davi_avancada;


import com.example.mylibraryRegion.Region;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Semaphore;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.CameraUpdateFactory;

import android.view.View;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class MainActivity extends AppCompatActivity implements OnMapReadyCallback  {

    private TextView latText;
    private TextView longText;
    private Button addRegion;
    private LocationManager locationManager;
    private Semaphore semaphore = new Semaphore(1);
    private Queue<Region> regionQueue = new LinkedList<>();
    private GoogleMap gMap;
    private Marker currentLocationMarker;
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://daviavancada-d0c0a-default-rtdb.firebaseio.com/").getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        addRegion = findViewById(R.id.addRegion);



        // Solicitar permissões
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

        // Inicializar o LocationManager
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        // Inicializar a referência do Firebase
        databaseReference = FirebaseDatabase.getInstance("https://daviavancada-d0c0a-default-rtdb.firebaseio.com/").getReference();

        // Verificar se a permissão foi concedida
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        // Criar e iniciar a GpsThread depois de garantir que as permissões foram concedidas
        GpsThread gpsThread = new GpsThread(this, locationManager, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                runOnUiThread(() -> {
                    // Atualizar os labels com as coordenadas atuais
                    TextView latText = findViewById(R.id.latText);
                    TextView longText = findViewById(R.id.longText);
                    latText.setText(String.format("Latitude: %s", location.getLatitude()));
                    longText.setText(String.format("Longitude: %s", location.getLongitude()));
                });
            }
        });
        gpsThread.start();


        // Listener botão Adicionar Região
        addRegion.setOnClickListener(v -> {
            // Verificar permissão de localização
            if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(MainActivity.this, "Permissão de localização não concedida", Toast.LENGTH_SHORT).show();
                return;
            }

            // Obter a última localização conhecida
            Location lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (lastLocation != null) {
                // Criar uma instância da classe RegionVerificationThread com os parâmetros necessários
                RegionVerificationThread regionVerificationThread = new RegionVerificationThread(
                        MainActivity.this,
                        lastLocation,
                        regionQueue,
                        databaseReference

                );

                // Iniciar a thread de verificação
                regionVerificationThread.start();
            } else {
                Toast.makeText(MainActivity.this, "Localização não disponível", Toast.LENGTH_SHORT).show();
            }
        });


        // Iniciar a atualização da localização
        startLocationUpdates();
    }


    // Método para iniciar a atualização da localização
    private void startLocationUpdates() {
        // Verificar permissão de localização
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Permissão de localização não concedida", Toast.LENGTH_SHORT).show();
            return;
        }


        // Configurar o fragmento do mapa
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);



        Button btnSaveToFirebase = findViewById(R.id.addNoBD);
        btnSaveToFirebase.setOnClickListener(v -> saveDataToFirebaseAndClearQueue());

        Button regionList = findViewById(R.id.regionList);
        regionList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RegionFila.class);
                intent.putExtra("listaRegioes", new ArrayList<>(regionQueue));
                startActivity(intent);
            }
        });
    }

    // Método para exibir mensagem na interface do usuário
    public void showMessage(String message) {
        runOnUiThread(() -> Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show());
    }


    public boolean checkRegionInFirebase(Location currentLocation) {
        final boolean[] canAdd = {true};

        // Acessa a coleção de regiões no Firebase
        databaseReference.child("regions").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Region region = snapshot.getValue(Region.class);
                    if (region != null) {
                        Location regionLocation = new Location("");
                        regionLocation.setLatitude(region.getLatitude());
                        regionLocation.setLongitude(region.getLongitude());

                        // Verifica se a região está dentro de um raio de 30 metros
                        if (currentLocation.distanceTo(regionLocation) < 30) {
                            canAdd[0] = false;
                            break;
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Tratar erro de acesso ao Firebase
                canAdd[0] = false;
            }
        });

        return canAdd[0];
    }


    private void saveDataToFirebaseAndClearQueue() {
        FirebaseSaveThread saveThread = new FirebaseSaveThread(semaphore, regionQueue, databaseReference);
        saveThread.start();
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;

        // Configurações iniciais do mapa
        gMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        gMap.getUiSettings().setZoomControlsEnabled(true);

        // Adicionar um marcador no mapa
        LatLng initialPosition = new LatLng(-18.7765, -46.4053); // Exemplo de coordenadas
        currentLocationMarker = gMap.addMarker(new MarkerOptions().position(initialPosition).title("Localização Atual"));

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        LocationListener locationListener = new LocationListener() {

            @Override
            public void onLocationChanged(Location location) {
                LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());

                // Verificar se o marcador já foi adicionado
                if (currentLocationMarker != null) {
                    currentLocationMarker.setPosition(currentLatLng);
                } else {
                    // Adicionar o marcador na localização atual
                    currentLocationMarker = gMap.addMarker(new MarkerOptions().position(currentLatLng).title("Localização Atual"));
                }

                // Mover a câmera para a localização atual
                gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 18));

                // Remover o listener após receber a primeira atualização de localização
                locationManager.removeUpdates(this);

                // Criar uma instância de RegionVerificationThread
                RegionVerificationThread regionVerificationThread = new RegionVerificationThread(MainActivity.this, location, regionQueue, databaseReference);


                // Verificar se a localização atual está dentro do raio de 30 metros de alguma região na fila local
                synchronized (regionQueue) {
                    for (Region region : regionQueue) {
                        Location regionLocation = new Location("");
                        regionLocation.setLatitude(region.getLatitude());
                        regionLocation.setLongitude(region.getLongitude());

                        if (Region.calcDist(location, regionLocation) < 30) {  // <30 metros
                            showMessage("A localização está dentro do raio de 30 metros de uma região na fila local.");
                        }
                    }
                }
            }




        };

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
    }
}