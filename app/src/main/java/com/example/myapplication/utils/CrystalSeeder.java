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
                ),

                new Crystal(
                        "crystal018",
                        "Azurite",
                        "A soothing stone that enhances spiritual awareness and promotes inner peace.",
                        "Meditation",
                        Arrays.asList("clarity", "intuition", "blue"),
                        Arrays.asList(
                               "https://firebasestorage.googleapis.com/v0/b/crysta-d58f8.firebasestorage.app/o/crystals%2FMeditation%2FAzurite%2Fazurite_1.png?alt=media&token=d803411d-8fa6-4719-a77b-dbe16d429b70",
                                "https://firebasestorage.googleapis.com/v0/b/crysta-d58f8.firebasestorage.app/o/crystals%2FMeditation%2FAzurite%2Fazurite_2.png?alt=media&token=dfe2facb-cbf0-44e4-8ba9-a429893b6679",
                                "https://firebasestorage.googleapis.com/v0/b/crysta-d58f8.firebasestorage.app/o/crystals%2FMeditation%2FAzurite%2Fazurite_3.png?alt=media&token=45463811-7df3-42e5-a375-a3aed16fec2d"
                        ),
                        2.5,
                        10,
                        0
                ),

                new Crystal(
                        "crystal019",
                        "Black Tourmaline",
                        "A grounding crystal that shields against negative energies and EMFs.",
                        "Meditation",
                        Arrays.asList("protection", "grounding", "black"),
                        Arrays.asList(
                               "https://firebasestorage.googleapis.com/v0/b/crysta-d58f8.firebasestorage.app/o/crystals%2FMeditation%2FBlack%20Tourmaline%2FBlackTourmaline_1.png?alt=media&token=972dc212-ddd5-4e5c-8baa-5ec58d7ecff9",
                                "https://firebasestorage.googleapis.com/v0/b/crysta-d58f8.firebasestorage.app/o/crystals%2FMeditation%2FBlack%20Tourmaline%2FBlackTourmaline_2.png?alt=media&token=584b2ea5-a796-4e7c-b8f0-ddb357392a7a",
                                "https://firebasestorage.googleapis.com/v0/b/crysta-d58f8.firebasestorage.app/o/crystals%2FMeditation%2FBlack%20Tourmaline%2FBlackTourmaline_3.png?alt=media&token=416956b9-e690-4f3a-a79c-5efc9af0dc61"
                        ),
                        2.5,
                        10,
                        0
                ),

                new Crystal(
                        "crystal020",
                        "Clear Quartz",
                        "A powerful energy amplifier that brings clarity and balances all chakras.",
                        "Meditation",
                        Arrays.asList("clarity", "energy", "clear"),
                        Arrays.asList(
                                "https://firebasestorage.googleapis.com/v0/b/crysta-d58f8.firebasestorage.app/o/crystals%2FMeditation%2FClear%20Quartz%2FClearQuartz_1.png?alt=media&token=952732e5-c62c-4203-8593-e2bdd0abb657",
                                "https://firebasestorage.googleapis.com/v0/b/crysta-d58f8.firebasestorage.app/o/crystals%2FMeditation%2FClear%20Quartz%2FClearQuartz_2.png?alt=media&token=daa1d28d-8940-4ce7-a769-612bb1554e65",
                                "https://firebasestorage.googleapis.com/v0/b/crysta-d58f8.firebasestorage.app/o/crystals%2FMeditation%2FClear%20Quartz%2FClearQuartz_3.png?alt=media&token=b1f4cf6c-4f85-4138-a9d8-c91e47e64f77"
                        ),
                        2.5,
                        10,
                        0
                ),

                new Crystal(
                        "crystal021",
                        "Danburite",
                        "Facilitates deep spiritual connection and access to higher consciousness.",
                        "Meditation",
                        Arrays.asList("purity", "connection", "clear"),
                        Arrays.asList(
                                "https://firebasestorage.googleapis.com/v0/b/crysta-d58f8.firebasestorage.app/o/crystals%2FMeditation%2FDanburite%2FDanburite_1.png?alt=media&token=3111f140-36ff-4bab-b579-6417b060a58e",
                                "https://firebasestorage.googleapis.com/v0/b/crysta-d58f8.firebasestorage.app/o/crystals%2FMeditation%2FDanburite%2FDanburite_2.png?alt=media&token=953af613-5bcb-40cb-a54f-452e8acd7461",
                                "https://firebasestorage.googleapis.com/v0/b/crysta-d58f8.firebasestorage.app/o/crystals%2FMeditation%2FDanburite%2FDanburite_3.png?alt=media&token=5ed3921a-6de5-4589-84a0-4eca8bbf3d22"
                        ),
                        2.5,
                        10,
                        0
                ),

                new Crystal(
                        "crystal022",
                        "Hematite",
                        "Strongly grounding, it brings mental organization and focus.",
                        "Meditation",
                        Arrays.asList("grounding", "protection", "silver"),
                        Arrays.asList(
                               "https://firebasestorage.googleapis.com/v0/b/crysta-d58f8.firebasestorage.app/o/crystals%2FMeditation%2FHematite%2FHematite_1.png?alt=media&token=8d0a3ca1-574e-44ee-8a49-47cdd38463ef",
                                "https://firebasestorage.googleapis.com/v0/b/crysta-d58f8.firebasestorage.app/o/crystals%2FMeditation%2FHematite%2FHematite_2.png?alt=media&token=3748ef53-65de-45c6-8a92-a8fb64da3d31",
                                "https://firebasestorage.googleapis.com/v0/b/crysta-d58f8.firebasestorage.app/o/crystals%2FMeditation%2FHematite%2FHematite_3.png?alt=media&token=583c189a-4385-40d8-90b8-b0beae586d6b"
                        ),
                        2.5,
                        10,
                        0
                ),

                new Crystal(
                        "crystal023",
                        "Kyanite",
                        "Aligns all chakras instantly and promotes clear communication and energy flow.",
                        "Meditation",
                        Arrays.asList("energy", "alignment", "blue"),
                        Arrays.asList(
                                "https://firebasestorage.googleapis.com/v0/b/crysta-d58f8.firebasestorage.app/o/crystals%2FMeditation%2FKyanite%2FKyanite_1.png?alt=media&token=f4c327fa-129e-45b8-a777-36ef0dcca80f",
                                "https://firebasestorage.googleapis.com/v0/b/crysta-d58f8.firebasestorage.app/o/crystals%2FMeditation%2FKyanite%2FKyanite_2.png?alt=media&token=f0c25f9f-c9c4-42ef-aeb3-df8fd4c4148e",
                                "https://firebasestorage.googleapis.com/v0/b/crysta-d58f8.firebasestorage.app/o/crystals%2FMeditation%2FKyanite%2FKyanite_3.png?alt=media&token=5229b2b8-72dd-4901-9bf4-1b0a350fb392"
                        ),
                        2.5,
                        10,
                        0
                ),

                new Crystal(
                        "crystal024",
                        "Labradorite",
                        "Awakens psychic abilities and protects during spiritual work.",
                        "Meditation",
                        Arrays.asList("protection", "intuition", "chrome"),
                        Arrays.asList(
                                "https://firebasestorage.googleapis.com/v0/b/crysta-d58f8.firebasestorage.app/o/crystals%2FMeditation%2FLabradorite%2FLabradorite_1.png?alt=media&token=3897943c-34e2-474e-83ea-972c2478adb7",
                                "https://firebasestorage.googleapis.com/v0/b/crysta-d58f8.firebasestorage.app/o/crystals%2FMeditation%2FLabradorite%2FLabradorite_2.png?alt=media&token=656deddf-f8be-4ff2-9d85-659b7a9c10b6",
                                "https://firebasestorage.googleapis.com/v0/b/crysta-d58f8.firebasestorage.app/o/crystals%2FMeditation%2FLabradorite%2FLabradorite_3.png?alt=media&token=9af82882-8371-4e76-8761-3c0ba267fc65"
                        ),
                        2.5,
                        10,
                        0
                ),

                new Crystal(
                        "crystal025",
                        "Lapis Lazuli",
                        "Stimulates intuition and truth, supporting deep inner self-discovery.",
                        "Meditation",
                        Arrays.asList("intuition", "wisdom", "blue"),
                        Arrays.asList(
                               "https://firebasestorage.googleapis.com/v0/b/crysta-d58f8.firebasestorage.app/o/crystals%2FMeditation%2FLapis%20Lazuli%2FLapisLazuli_1.png?alt=media&token=103f5ba4-0c23-496b-8384-081d396ccb4b",
                                "https://firebasestorage.googleapis.com/v0/b/crysta-d58f8.firebasestorage.app/o/crystals%2FMeditation%2FLapis%20Lazuli%2FLapisLazuli_2.png?alt=media&token=2a162bd7-3781-435e-b0c0-4784de31d09d",
                                "https://firebasestorage.googleapis.com/v0/b/crysta-d58f8.firebasestorage.app/o/crystals%2FMeditation%2FLapis%20Lazuli%2FLapisLazuli_3.png?alt=media&token=3956bcc2-e03e-4af8-9f1d-5d8d3f69e90b"
                        ),
                        2.5,
                        10,
                        0
                ),

                new Crystal(
                        "crystal026",
                        "Rose Quartz",
                        "The stone of unconditional love, encouraging compassion and emotional healing.",
                        "Meditation",
                        Arrays.asList("love", "healing", "pink"),
                        Arrays.asList(
                                "https://firebasestorage.googleapis.com/v0/b/crysta-d58f8.firebasestorage.app/o/crystals%2FMeditation%2FRose%20Quartz%2FRoseQuartz_1.png?alt=media&token=acd68e2a-d2eb-4094-9c76-e03bee7af18e",
                                "https://firebasestorage.googleapis.com/v0/b/crysta-d58f8.firebasestorage.app/o/crystals%2FMeditation%2FRose%20Quartz%2FRoseQuartz_2.png?alt=media&token=3b211147-7e25-40a9-bde6-202c1b3090c4",
                                "https://firebasestorage.googleapis.com/v0/b/crysta-d58f8.firebasestorage.app/o/crystals%2FMeditation%2FRose%20Quartz%2FRoseQuartz_3.png?alt=media&token=008c68bf-0399-4c95-8564-876198523b43"
                        ),
                        2.5,
                        10,
                        0
                ),

                new Crystal(
                        "crystal027",
                        "Tigers Eye",
                        "Enhances willpower, confidence, and balanced decision-making.",
                        "Meditation",
                        Arrays.asList("protection", "confidence", "brown"),
                        Arrays.asList(
                                "https://firebasestorage.googleapis.com/v0/b/crysta-d58f8.firebasestorage.app/o/crystals%2FMeditation%2FTigers%20Eye%2FTigersEye_1.png?alt=media&token=62ba7930-33ff-4bc7-a713-4b65c0d47f62",
                                "https://firebasestorage.googleapis.com/v0/b/crysta-d58f8.firebasestorage.app/o/crystals%2FMeditation%2FTigers%20Eye%2FTigersEye_2.png?alt=media&token=480b2c15-bdbb-4917-9307-a3d241a30192",
                                "https://firebasestorage.googleapis.com/v0/b/crysta-d58f8.firebasestorage.app/o/crystals%2FMeditation%2FTigers%20Eye%2FTigersEye_3.png?alt=media&token=579fa4fe-89d0-427f-bab6-7a8d1f440c29"
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
