package com.example.myapplication.utils;

import android.util.Log;

import com.example.myapplication.models.Crystal;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.List;

public class CrystalSeeder {

    public static void seedCrystalsToFirestore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        List<Crystal> crystals = Arrays.asList(
                new Crystal("crystal001", "Amethyst", "Promotes peace and protection", "Calm & Stress Relief",
                        Arrays.asList("peace", "protection", "purple"),
                        Arrays.asList("https://firebasestorage.googleapis.com/v0/b/crysta-d58f8.firebasestorage.app/o/crystals%2Famethyst%2Famethyst_3.png?alt=media&token=0667de9e-55ae-489e-a34e-ec8e1994eacb", "https://firebasestorage.googleapis.com/v0/b/crysta-d58f8.firebasestorage.app/o/crystals%2Famethyst%2Famethyst_1.png?alt=media&token=a86de752-d0e9-4a01-8377-d2c76bdd4a3d", "https://firebasestorage.googleapis.com/v0/b/crysta-d58f8.firebasestorage.app/o/crystals%2Famethyst%2Famethyst_2.png?alt=media&token=8e329908-c060-4493-914d-e1f0668c6ad1"), 2.5, 10, 0),

                new Crystal("crystal002", "Citrine", "Brings joy and positivity", "Success",
                        Arrays.asList("joy", "wealth", "yellow"),
                        Arrays.asList("url4", "url5", "url6"), 3.0, 8, 0)

                // Add more crystals here...
        );

        for (Crystal crystal : crystals) {
            db.collection("crystals")
                    .document(crystal.getId())
                    .set(crystal)
                    .addOnSuccessListener(aVoid ->
                            Log.d("CrystalSeeder", "Inserted: " + crystal.getName()))
                    .addOnFailureListener(e ->
                            Log.e("CrystalSeeder", "Failed to insert: " + crystal.getName(), e));
        }
    }
}