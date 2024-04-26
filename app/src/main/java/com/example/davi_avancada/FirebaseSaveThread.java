package com.example.davi_avancada;

import com.example.mylibraryRegion.Region;
import com.example.mylibraryCrypto.Cryptography;


import com.google.gson.Gson;
import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import java.util.Queue;
import java.util.concurrent.Semaphore;
public class FirebaseSaveThread extends Thread {
    private final Semaphore semaphore;
    private final Queue<Region> regionQueue;
    private final DatabaseReference databaseReference;

    public FirebaseSaveThread(Semaphore semaphore, Queue<Region> regionQueue, DatabaseReference dbRef) {
        this.semaphore = semaphore;
        this.regionQueue = regionQueue;
        this.databaseReference = dbRef; // Use a variável do construtor para inicializar
    }

    @Override
    public void run() {
        try {
            semaphore.acquire();
            while (!regionQueue.isEmpty()) {
                Region region = regionQueue.poll();
                String regionJson = new Gson().toJson(region);
                String encryptedRegionJson = Cryptography.encrypt(regionJson);
                saveRegionToFirebase(encryptedRegionJson);
            }
        } catch (InterruptedException e) {
            Log.e("FirebaseSaveThread", "Thread interrompida: " + e.getMessage());
            Thread.currentThread().interrupt();
        } finally {
            semaphore.release();
        }
    }

    private void saveRegionToFirebase(String encryptedRegionJson) {
        databaseReference.child("regions").push().setValue(encryptedRegionJson)
                .addOnSuccessListener(aVoid -> {
                    Log.d("Firebase", "Região salva com sucesso!");
                })
                .addOnFailureListener(e -> {
                    Log.e("Firebase", "Erro ao salvar região: " + e.getMessage());
                });
    }
}
