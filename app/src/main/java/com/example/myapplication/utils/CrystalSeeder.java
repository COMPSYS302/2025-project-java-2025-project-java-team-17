package com.example.myapplication.utils;

import android.util.Log;

import com.example.myapplication.models.Crystal;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.List;

public class CrystalSeeder {

    private static final FirebaseFirestore db = FirebaseFirestore.getInstance();
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
                                "https://firebasestorage.googleapis.com/v0/b/crysta-d58f8.firebasestorage.app/o/crystals%2FCalm%20%26%20Stress%20Relief%2FAmethyst%2Famethyst_3.png?alt=media&token=5b62c349-6557-47d9-a199-6d52924a6135",
                                "https://firebasestorage.googleapis.com/v0/b/crysta-d58f8.firebasestorage.app/o/crystals%2FCalm%20%26%20Stress%20Relief%2FAmethyst%2Famethyst_1.png?alt=media&token=b1b4187c-1630-4d71-bb41-4b5df17e0829",
                                "https://firebasestorage.googleapis.com/v0/b/crysta-d58f8.firebasestorage.app/o/crystals%2FCalm%20%26%20Stress%20Relief%2FAmethyst%2Famethyst_2.png?alt=media&token=06f024b7-12a7-469c-80d7-8a793eb48ab2"
                        ),
                        2.5,
                        10,
                        0
                ),
                new Crystal("crystal002", "Citrine", "Brings joy and positivity", "Success",
                        Arrays.asList("joy", "wealth", "yellow"),
                        Arrays.asList(
                                "https://firebasestorage.googleapis.com/v0/b/crysta-d58f8.firebasestorage.app/o/crystals%2FSuccess%2FCitrine%2Fcitrine_1.png?alt=media&token=199d8baf-78ef-4008-8521-0f1fbb079f62",
                                "https://firebasestorage.googleapis.com/v0/b/crysta-d58f8.firebasestorage.app/o/crystals%2FSuccess%2FCitrine%2Fcitrine_3.png?alt=media&token=b76423e1-3d87-4d67-a161-35737f271b58",
                                "https://firebasestorage.googleapis.com/v0/b/crysta-d58f8.firebasestorage.app/o/crystals%2FSuccess%2FCitrine%2Fcitrine_2.png?alt=media&token=2b338b45-f84c-4c1d-bcf1-ef2cd27d56ee"),
                        3.0, 8, 0),
                new Crystal("crystal003", "Lepidolite", "Contains lithium; helps with emotional balance and anxiety", "Calm & Stress Relief",
                        Arrays.asList("calm", "anxiety", "lithium"),
                        Arrays.asList(
                                "https://firebasestorage.googleapis.com/v0/b/crysta-d58f8.firebasestorage.app/o/crystals%2FCalm%20%26%20Stress%20Relief%2FLepidolite%2Flepidolite_1.png?alt=media&token=6370fc8d-d5dd-44ba-b9e4-d9dc3512a696",
                                "https://firebasestorage.googleapis.com/v0/b/crysta-d58f8.firebasestorage.app/o/crystals%2FCalm%20%26%20Stress%20Relief%2FLepidolite%2Flepidolite_2.png?alt=media&token=6777bdf5-93a6-4695-9835-0a8b619f3f7a",
                                "https://firebasestorage.googleapis.com/v0/b/crysta-d58f8.firebasestorage.app/o/crystals%2FCalm%20%26%20Stress%20Relief%2FLepidolite%2Flepidolite_3.png?alt=media&token=8c3cae53-f059-4016-aec3-b242353c43ff"),


                        2.5, 10, 0),
                new Crystal(
                        "crystal004",
                        "Blue Lace Agate",
                        "Promotes tranquility and gentle communication",
                        "Calm & Stress Relief",
                        Arrays.asList("calm", "communication", "blue"),
                        Arrays.asList(
                                "https://firebasestorage.googleapis.com/v0/b/crysta-d58f8.firebasestorage.app/o/crystals%2FCalm%20%26%20Stress%20Relief%2FBluelace%2Fbluelace_3.png?alt=media&token=e9f4c6fc-8a8a-45b2-9899-26d2f4ab34af",
                                "https://firebasestorage.googleapis.com/v0/b/crysta-d58f8.firebasestorage.app/o/crystals%2FCalm%20%26%20Stress%20Relief%2FBluelace%2Fbluelace_2.png?alt=media&token=6d9039a7-4506-4767-9999-dbaa7810cfc8",
                                "https://firebasestorage.googleapis.com/v0/b/crysta-d58f8.firebasestorage.app/o/crystals%2FCalm%20%26%20Stress%20Relief%2FBluelace%2Fbluelace_1.png?alt=media&token=850b32c7-0679-49b0-956d-5c34d5b7aa4a"
                        ),
                        2.8,
                        9,
                        0
                ),
                new Crystal(
                        "crystal011",
                        "Angelite",
                        "Calms fear and enhances serenity and compassion",
                        "Calm & Stress Relief",
                        Arrays.asList("peace", "compassion", "light blue"),
                        Arrays.asList(
                                "https://firebasestorage.googleapis.com/v0/b/crysta-d58f8.firebasestorage.app/o/crystals%2FCalm%20%26%20Stress%20Relief%2FAngelite%2FAngelite_2.png?alt=media&token=ce825aa7-5958-4643-976d-00226f1acb7f",
                                "https://firebasestorage.googleapis.com/v0/b/crysta-d58f8.firebasestorage.app/o/crystals%2FCalm%20%26%20Stress%20Relief%2FAngelite%2FAngelite_1.png?alt=media&token=7fb85d82-abbd-4e3b-ac72-239e70dc127c",
                                "https://firebasestorage.googleapis.com/v0/b/crysta-d58f8.firebasestorage.app/o/crystals%2FCalm%20%26%20Stress%20Relief%2FAngelite%2FAngelite_3.png?alt=media&token=2f790d69-7442-41d7-8662-360fc1ce7e04"

                        ),
                        2.5,
                        10,
                        0
                ),
                new Crystal(
                        "crystal012",
                        "Celestite",
                        "Brings mental clarity and divine peace",
                        "Calm & Stress Relief",
                        Arrays.asList("calm", "clarity", "sky blue"),
                        Arrays.asList(
                              "https://firebasestorage.googleapis.com/v0/b/crysta-d58f8.firebasestorage.app/o/crystals%2FCalm%20%26%20Stress%20Relief%2FCelestite%2Fcelestite_3.png?alt=media&token=9f666f2b-0916-4903-ade8-1e9ee0d9645c",
                                "https://firebasestorage.googleapis.com/v0/b/crysta-d58f8.firebasestorage.app/o/crystals%2FCalm%20%26%20Stress%20Relief%2FCelestite%2Fcelestite_1.png?alt=media&token=98c8f0b1-c305-4043-b506-5a81c3165e8d",
                                "https://firebasestorage.googleapis.com/v0/b/crysta-d58f8.firebasestorage.app/o/crystals%2FCalm%20%26%20Stress%20Relief%2FCelestite%2Fcelestite_2.png?alt=media&token=3557050a-040f-4306-9930-24225cf0efa3"

                        ),
                        2.5,
                        10,
                        0
                ),
                new Crystal(
                        "crystal013",
                        "Chrysocolla",
                        "Encourages emotional healing and soothes anxiety",
                        "Calm & Stress Relief",
                        Arrays.asList("soothing", "expression", "blue-green"),
                        Arrays.asList(
                                "https://firebasestorage.googleapis.com/v0/b/crysta-d58f8.firebasestorage.app/o/crystals%2FCalm%20%26%20Stress%20Relief%2FChrysocolla%2Fchrysocolla_1.png?alt=media&token=9fbc20ac-af1c-44c0-8e5c-2adcd514affd",
                                "https://firebasestorage.googleapis.com/v0/b/crysta-d58f8.firebasestorage.app/o/crystals%2FCalm%20%26%20Stress%20Relief%2FChrysocolla%2Fchrysocolla_2.png?alt=media&token=4f951045-5700-44d0-a296-cf43bf2bd658",
                                "https://firebasestorage.googleapis.com/v0/b/crysta-d58f8.firebasestorage.app/o/crystals%2FCalm%20%26%20Stress%20Relief%2FChrysocolla%2Fchrysocolla_3.png?alt=media&token=f2adbcc0-c88b-45b4-8714-938e75870226"
                        ),
                        2.5,
                        10,
                        0
                ),
                new Crystal(
                        "crystal014",
                        "Fluorite",
                        "Reduces mental fog and helps with emotional balance",
                        "Calm & Stress Relief",
                        Arrays.asList("clarity", "focus", "multicolor"),
                        Arrays.asList(
                                "https://firebasestorage.googleapis.com/v0/b/crysta-d58f8.firebasestorage.app/o/crystals%2FCalm%20%26%20Stress%20Relief%2FFluorite%2Ffluorite_1.png?alt=media&token=98d86a0a-0160-4692-96dd-086c0ecf9752",
                                "https://firebasestorage.googleapis.com/v0/b/crysta-d58f8.firebasestorage.app/o/crystals%2FCalm%20%26%20Stress%20Relief%2FFluorite%2Ffluorite_3.png?alt=media&token=393153a8-25aa-4f05-bb02-c7af23593dd8",
                                "https://firebasestorage.googleapis.com/v0/b/crysta-d58f8.firebasestorage.app/o/crystals%2FCalm%20%26%20Stress%20Relief%2FFluorite%2Ffluorite_2.png?alt=media&token=9b6f5386-4a7d-451c-8444-85938f40e9bf"
                        ),
                        2.5,
                        10,
                        0
                ),
                new Crystal(
                        "crystal015",
                        "Moonstone",
                        "Balances emotions and calms emotional reactivity",
                        "Calm & Stress Relief",
                        Arrays.asList("balance", "intuition", "white"),
                        Arrays.asList(
                                "https://firebasestorage.googleapis.com/v0/b/crysta-d58f8.firebasestorage.app/o/crystals%2FCalm%20%26%20Stress%20Relief%2FMoonstone%2Fmoonstone_2.png?alt=media&token=5e6d2416-af1e-4521-a3b2-7016fe8cbebe",
                                "https://firebasestorage.googleapis.com/v0/b/crysta-d58f8.firebasestorage.app/o/crystals%2FCalm%20%26%20Stress%20Relief%2FMoonstone%2Fmoonstone_1.png?alt=media&token=890f3c04-ca4d-425b-96da-444aeed866b7",
                                "https://firebasestorage.googleapis.com/v0/b/crysta-d58f8.firebasestorage.app/o/crystals%2FCalm%20%26%20Stress%20Relief%2FMoonstone%2Fmoonstone_3.png?alt=media&token=dea2e37a-51b8-4ea0-b588-bca71a673a38"
                        ),
                        2.5,
                        10,
                        0
                ),
                new Crystal(
                        "crystal016",
                        "Selenite",
                        "Cleanses energy and brings deep peace",
                        "Calm & Stress Relief",
                        Arrays.asList("cleansing", "peace", "white"),
                        Arrays.asList(
                                "https://firebasestorage.googleapis.com/v0/b/crysta-d58f8.firebasestorage.app/o/crystals%2FCalm%20%26%20Stress%20Relief%2FSelenite%2Fselenite_1.png?alt=media&token=bb69e507-73ac-4a85-a15d-4619d3733888",
                                "https://firebasestorage.googleapis.com/v0/b/crysta-d58f8.firebasestorage.app/o/crystals%2FCalm%20%26%20Stress%20Relief%2FSelenite%2Fselenite_2.png?alt=media&token=2236e29b-67c2-4a41-903b-c96de0d046f7",
                                "https://firebasestorage.googleapis.com/v0/b/crysta-d58f8.firebasestorage.app/o/crystals%2FCalm%20%26%20Stress%20Relief%2FSelenite%2Fselenite_3.png?alt=media&token=cfdcdde9-e975-49e9-887f-c67130b5daf7"
                        ),
                        2.5,
                        10,
                        0
                ),
                new Crystal(
                        "crystal017",
                        "Smoky Quartz",
                        "Grounds stress and neutralizes negative energy",
                        "Calm & Stress Relief",
                        Arrays.asList("grounding", "balance", "brown"),
                        Arrays.asList(
                                "https://firebasestorage.googleapis.com/v0/b/crysta-d58f8.firebasestorage.app/o/crystals%2FCalm%20%26%20Stress%20Relief%2FSmoky%20Quartz%2FSmokyQuartz_2.png?alt=media&token=454cd7c3-48e7-4173-acc4-3761732dd409",
                                "https://firebasestorage.googleapis.com/v0/b/crysta-d58f8.firebasestorage.app/o/crystals%2FCalm%20%26%20Stress%20Relief%2FSmoky%20Quartz%2FSmokyQuartz_1.png?alt=media&token=400fd344-74de-4222-b89a-24adef14c8df",
                                "https://firebasestorage.googleapis.com/v0/b/crysta-d58f8.firebasestorage.app/o/crystals%2FCalm%20%26%20Stress%20Relief%2FSmoky%20Quartz%2FSmokyQuartz_3.png?alt=media&token=b44d3089-67c8-419c-8155-186a0ac91437"
                        ),
                        2.5,
                        10,
                        0
                )
        );
        for (Crystal crystal : crystals) {
            seedSingleCrystalIfMissing(crystal);
        }

    }

    private static void seedSingleCrystalIfMissing(Crystal crystal) {
        db.collection("crystals").document(crystal.getId())
                .get()
                .addOnSuccessListener(document -> {
                    if (!document.exists()) {
                        db.collection("crystals").document(crystal.getId()).set(crystal);
                        Log.d("SEEDER", "Added crystal: " + crystal.getName());
                    } else {
                        Log.d("SEEDER", "Crystal already exists: " + crystal.getName());
                    }
                })
                .addOnFailureListener(e -> Log.e("SEEDER", "Error checking crystal: " + crystal.getName(), e));
    }
}
