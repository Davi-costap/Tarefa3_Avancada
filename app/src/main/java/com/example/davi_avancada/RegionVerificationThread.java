package com.example.davi_avancada;

import com.example.mylibraryRegion.Region;
import com.example.mylibraryRegion.SubRegion;
import com.example.mylibraryRegion.RestrictedRegion;
import com.example.mylibraryCrypto.Cryptography;

import android.location.Location;
import android.util.Log;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DatabaseReference;
import java.util.Queue;
import com.google.gson.Gson;

public class RegionVerificationThread extends Thread {
    private final MainActivity mainActivity;
    private final Location currentLocation;
    private final Queue<Region> regionQueue;
    private final DatabaseReference databaseReference;
    private final Gson gson = new Gson();

    public RegionVerificationThread(MainActivity mainActivity, Location currentLocation, Queue<Region> regionQueue, DatabaseReference databaseReference) {
        this.mainActivity = mainActivity;
        this.currentLocation = currentLocation;
        this.regionQueue = regionQueue;
        this.databaseReference = databaseReference;
    }

    @Override
    public void run() {
        // Verificação Local para Regiões Próximas
        if (isInQueueProximity(regionQueue, currentLocation)) {
            mainActivity.showMessage("Existe uma região dentro do limite de 30 metros cadastrada na Fila.");
            return; // Retorna se a localização estiver dentro do limite
        }
        // Verificação de Regiões no Firebase
        verifyRegionInFirebase(currentLocation, (canAddFirebase) -> {
            if (!canAddFirebase) {
                mainActivity.showMessage("Existe uma região dentro do limite de 30 metros cadastrada no Firebase.");
                return; // Retorna se não for possível adicionar
            }
            synchronized (regionQueue) {
                Region lastRegion = getLastRegion(regionQueue); // Pega o último item da fila
                if (lastRegion == null) {
                    // Se a fila estiver vazia, adicione uma nova Região
                    regionQueue.add(new Region("Region", currentLocation));
                    mainActivity.showMessage("Região adicionada à fila.");
                } else if (lastRegion instanceof SubRegion) {
                    handleRestrictedRegionAddition(regionQueue, currentLocation); // Adiciona RestrictedRegion se a última for SubRegion
                } else if (lastRegion instanceof RestrictedRegion) {
                    handleSubRegionAddition(regionQueue, currentLocation); // Adiciona SubRegion se a última for RestrictedRegion
                } else {
                    // Se for uma Região comum, adicione SubRegion
                    regionQueue.add(new SubRegion("Sub Region", currentLocation, lastRegion));
                    mainActivity.showMessage("SubRegion adicionada à fila.");
                }
            }
        });
    }

    // Métodos de verificação de proximidade e adição de regiões

    private boolean isInQueueProximity(Queue<Region> regionQueue, Location currentLocation) {
        synchronized (regionQueue) {
            for (Region region : regionQueue) {
                if (isInProximity(currentLocation, region)) {
                    return true; // Retorna verdadeiro se estiver muito próximo
                }
            }
        }
        return false; // Retorna falso se não houver proximidade
    }

    private boolean isInProximity(Location currentLocation, Region region) {
        Location regionLocation = new Location("");
        regionLocation.setLatitude(region.getLatitude());
        regionLocation.setLongitude(region.getLongitude());
        return currentLocation.distanceTo(regionLocation) < 30; // Verifica se a distância é menor que 30 metros
    }

    private void verifyRegionInFirebase(Location currentLocation, FirebaseCheckCallback callback) {
        // Verificação de Regiões no Firebase
        databaseReference.child("regions").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean canAdd = true; // Assume que pode adicionar inicialmente
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String encryptedRegionJson = snapshot.getValue(String.class);
                    String regionJson = Cryptography.decrypt(encryptedRegionJson);
                    Region region = gson.fromJson(regionJson, Region.class);
                    if (region != null && isInProximity(currentLocation, region)) {
                        canAdd = false; // Não pode adicionar se estiver muito próximo
                        break;
                    }
                }
                callback.onCheckCompleted(canAdd); // Notifica se pode adicionar
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Firebase", "Erro ao verificar regiões: " + databaseError.getMessage());
                mainActivity.showMessage("Erro ao verificar regiões no Firebase."); // Loga e mostra erro
                callback.onCheckCompleted(false); // Notifica erro
            }
        });
    }

    private Region getLastRegion(Queue<Region> regionQueue) {
        synchronized (regionQueue) {
            Region lastRegion = null;
            for (Region region : regionQueue) {
                lastRegion = region; // Atualiza para o último item
            }
            return lastRegion; // Retorna o último item da fila
        }
    }

    private void handleRestrictedRegionAddition(Queue<Region> regionQueue, Location currentLocation) {
        synchronized (regionQueue) {
            Region lastRegion = getLastRegion(regionQueue);
            if (lastRegion instanceof SubRegion) {
                // Se a última região for SubRegion, adicione RestrictedRegion
                regionQueue.add(new RestrictedRegion("Restricted Region", currentLocation, lastRegion, true));
                mainActivity.showMessage("RestrictedRegion adicionada à fila.");
            } else {
                mainActivity.showMessage("Não é possível adicionar RestrictedRegion, pois a última região não é SubRegion.");
            }
        }
    }

    private void handleSubRegionAddition(Queue<Region> regionQueue, Location currentLocation) {
        synchronized (regionQueue) {
            Region lastRegion = getLastRegion(regionQueue);
            if (lastRegion instanceof RestrictedRegion) {
                // Se a última região for RestrictedRegion, adicione SubRegion
                regionQueue.add(new SubRegion("Sub Region", currentLocation, lastRegion));
                mainActivity.showMessage("SubRegion adicionada à fila.");
            } else {
                mainActivity.showMessage("Não é possível adicionar SubRegion, pois a última região não é RestrictedRegion.");
            }
        }
    }

    // Interface para callback de verificação no Firebase
    public interface FirebaseCheckCallback {
        void onCheckCompleted(boolean canAdd);
    }
}
