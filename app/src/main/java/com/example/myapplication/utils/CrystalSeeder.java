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
                new Crystal(
                        "crystal001",
                        "Amethyst",
                        "Promotes peace and protection",
                        "Calm & Stress Relief",
                        Arrays.asList("peace", "protection", "purple"),
                        Arrays.asList(
                                "https://firebasestorage.googleapis.com/v0/b/crysta-d58f8.firebasestorage.app/o/crystals%2Famethyst%2Famethyst_3.png?alt=media&token=0667de9e-55ae-489e-a34e-ec8e1994eacb",
                                "https://firebasestorage.googleapis.com/v0/b/crysta-d58f8.firebasestorage.app/o/crystals%2Famethyst%2Famethyst_1.png?alt=media&token=a86de752-d0e9-4a01-8377-d2c76bdd4a3d",
                                "https://firebasestorage.googleapis.com/v0/b/crysta-d58f8.firebasestorage.app/o/crystals%2Famethyst%2Famethyst_2.png?alt=media&token=8e329908-c060-4493-914d-e1f0668c6ad1"
                        ),
                        2.5,
                        10,
                        0
                ),
                new Crystal("crystal002", "Citrine", "Brings joy and positivity", "Success",
                        Arrays.asList("joy", "wealth", "yellow"),
                        Arrays.asList(
                                "https://firebasestorage.googleapis.com/v0/b/crysta-d58f8.firebasestorage.app/o/crystals%2Fcitrine%2Fcitrine_1.png?alt=media&token=484b095d-0aa6-4e77-bb0f-6a4b33d2aae1",
                                "https://firebasestorage.googleapis.com/v0/b/crysta-d58f8.firebasestorage.app/o/crystals%2Fcitrine%2Fcitrine_2.png?alt=media&token=70ba580e-50ba-4cdf-a510-c2ddfdf33c00",
                                "https://firebasestorage.googleapis.com/v0/b/crysta-d58f8.firebasestorage.app/o/crystals%2Fcitrine%2Fcitrine_3.png?alt=media&token=6e011991-c021-45dd-9d61-8d14ad952cc1"),
                        3.0, 8, 0),
                new Crystal("crystal003", "Lepidolite", "Contains lithium; helps with emotional balance and anxiety", "Calm & Stress Relief",
                        Arrays.asList("calm", "anxiety", "lithium"),
                        Arrays.asList(
                                "https://firebasestorage.googleapis.com/v0/b/crysta-d58f8.firebasestorage.app/o/crystals%2FLepidolite%2Flepidolite_1.png?alt=media&token=e09950f1-a2ad-4616-abab-acc6ee92c216",
                                "https://firebasestorage.googleapis.com/v0/b/crysta-d58f8.firebasestorage.app/o/crystals%2FLepidolite%2Flepidolite_2.png?alt=media&token=3c6de074-7ad7-46c4-bd15-c7c70a3b1250",
                                "https://firebasestorage.googleapis.com/v0/b/crysta-d58f8.firebasestorage.app/o/crystals%2FLepidolite%2Flepidolite_3.png?alt=media&token=416936a8-5328-4aef-ad53-02c54c7f4766"),
                        2.5, 10, 0),
                new Crystal(
                        "crystal004",
                        "Blue Lace Agate",
                        "Promotes tranquility and gentle communication",
                        "Calm & Stress Relief",
                        Arrays.asList("calm", "communication", "blue"),
                        Arrays.asList(
                                "https://firebasestorage.googleapis.com/v0/b/crysta-d58f8.firebasestorage.app/o/crystals%2FBluelace%2Fbluelace_1.png?alt=media&token=a28928c2-a56e-44f7-9110-286beb0f211e",
                                "https://firebasestorage.googleapis.com/v0/b/crysta-d58f8.firebasestorage.app/o/crystals%2FBluelace%2Fbluelace_2.png?alt=media&token=481d975b-dfb1-4504-a057-30b9342c52da",
                                "https://firebasestorage.googleapis.com/v0/b/crysta-d58f8.firebasestorage.app/o/crystals%2FBluelace%2Fbluelace_3.png?alt=media&token=e5bfb5df-40f7-43e2-8875-ad0859f3a8a2"
                        ),
                        2.8,
                        9,
                        0
                ),
                new Crystal(
                        "crystal005",
                        "Howlite",
                        "Eases overthinking and promotes restful sleep",
                        "Calm & Stress Relief",
                        Arrays.asList("calm", "sleep", "white"),
                        Arrays.asList(
                                "https://firebasestorage.googleapis.com/v0/b/crysta-d58f8.firebasestorage.app/o/crystals%2FBluelace%2FHowlite%2Fhowlite_1.png?alt=media&token=abab7172-8428-4075-b515-422d76936610",
                                "https://firebasestorage.googleapis.com/v0/b/crysta-d58f8.firebasestorage.app/o/crystals%2FBluelace%2FHowlite%2Fhowlite_2.png?alt=media&token=4f8acfbc-3855-45d0-8d76-8746549b9360",
                                "https://firebasestorage.googleapis.com/v0/b/crysta-d58f8.firebasestorage.app/o/crystals%2FBluelace%2FHowlite%2Fhowlite_3.png?alt=media&token=c04ecfd0-c05e-4f48-8d1e-b7aaf4098f62"
                        ),
                        2.3,
                        12,
                        0
                )
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