package com.example.myapplication.utils;

import android.util.Log;

import com.example.myapplication.models.Crystal;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.List;

/**
 * Utility class for seeding the Firestore database with initial crystal data.
 * This class is designed to be run once or as needed to populate the 'crystals'
 * collection in Firestore with a predefined set of {@link Crystal} objects.
 * It checks if a crystal already exists by its ID before adding it to prevent duplicates.
 */
public class CrystalSeeder {

    // Firestore database instance.
    private static final FirebaseFirestore db = FirebaseFirestore.getInstance();
    // Log tag for this class.
    private static final String TAG = "CrystalSeeder";

    /**
     * Seeds the Firestore database with a predefined list of crystals.
     * Iterates through a hardcoded list of {@link Crystal} objects and attempts
     * to add each one to Firestore if it doesn't already exist.
     */
    public static void seedCrystals() {
        // List of Crystal objects to be seeded into the database.
        // Each Crystal is defined with its ID, name, description, category, tags,
        // image URLs, price, stock, and initial view count.
        List<Crystal> crystals = Arrays.asList(
                new Crystal(
                        "crystal001",
                        "Amethyst Geode",
                        "Amethyst is a violet variety of quartz. The name comes from the Koine Greek αμέθυστος amethystos from α- a-, \"not\" and μεθύσκω methysko / μεθώ metho, \"intoxicate\", a reference to the belief that the stone protected its owner from drunkenness. The ancient Greeks wore amethyst and carved drinking vessels from it in the belief that it would prevent intoxication.",
                        "Healing Stones",
                        Arrays.asList("calming", "spirituality", "purple"),
                        Arrays.asList(
                                "https://firebasestorage.googleapis.com/v0/b/crysta-d58f8.firebasestorage.app/o/crystals%2FHealing%20Stones%2FAmethyst%20Geode%2FAmethyst_1.png?alt=media&token=3c260c8b-57a5-4424-9c02-22533789077d",
                                "https://firebasestorage.googleapis.com/v0/b/crysta-d58f8.firebasestorage.app/o/crystals%2FHealing%20Stones%2FAmethyst%20Geode%2FAmethyst_2.png?alt=media&token=1f25e7a9-fd1f-4f81-bd78-ba5a5e305e9e",
                                "https://firebasestorage.googleapis.com/v0/b/crysta-d58f8.firebasestorage.app/o/crystals%2FHealing%20Stones%2FAmethyst%20Geode%2FAmethyst_3.png?alt=media&token=e966aa68-3d14-41d4-811c-2c9749174a72"
                        ),
                        75.99,
                        10,
                        0
                ),
                new Crystal(
                        "crystal002",
                        "Rose Quartz",
                        "Rose Quartz is a type of quartz which exhibits a pale pink to rose red hue. The color is usually considered as due to trace amounts of titanium, iron, or manganese, in the massive material. Some rose quartz contains microscopic rutile needles which produces an asterism in transmitted light. Recent X-ray diffraction studies suggest that the color is due to thin microscopic fibers of possibly dumortierite within the massive quartz.",
                        "Healing Stones",
                        Arrays.asList("love", "compassion", "pink"),
                        Arrays.asList(
                                "https://firebasestorage.googleapis.com/v0/b/crysta-d58f8.firebasestorage.app/o/crystals%2FHealing%20Stones%2FRose%20Quartz%2FRoseQuartz_1.png?alt=media&token=e937397b-a913-4427-9a84-7a1a2b721868",
                                "https://firebasestorage.googleapis.com/v0/b/crysta-d58f8.firebasestorage.app/o/crystals%2FHealing%20Stones%2FRose%20Quartz%2FRoseQuartz_2.png?alt=media&token=2eb28a50-689b-449e-b52f-1a98292882a1",
                                "https://firebasestorage.googleapis.com/v0/b/crysta-d58f8.firebasestorage.app/o/crystals%2FHealing%20Stones%2FRose%20Quartz%2FRoseQuartz_3.png?alt=media&token=963b519b-c19a-4f51-871d-536967845f5c"
                        ),
                        45.00,
                        10,
                        0
                ),
                // ... (rest of the crystal data remains the same) ...

                new Crystal(
                        "crystal030",
                        "Amazonite",
                        "Amazonite is a soothing blue-green stone known for its calming energy and ability to balance both logic and intuition. Named after the Amazon River, it has long been associated with courage and communication, helping individuals express their thoughts clearly and honestly. In the realm of success, Amazonite encourages you to speak your truth, advocate for yourself, and move forward with a sense of inner peace and resilience. It is especially helpful for those who struggle with self-doubt or fear of judgment, as it nurtures confidence while promoting compassion and understanding in communication.",
                        "Success",
                        Arrays.asList("communication", "confidence", "blue"),
                        Arrays.asList(
                                "https://firebasestorage.googleapis.com/v0/b/crysta-d58f8.firebasestorage.app/o/crystals%2FSuccess%2FAmazonite%2FAmazonite_1.png?alt=media&token=52dd393c-f657-472e-94fb-3f7f45f1e075",
                                "https://firebasestorage.googleapis.com/v0/b/crysta-d58f8.firebasestorage.app/o/crystals%2FSuccess%2FAmazonite%2FAmazonite_2.png?alt=media&token=3539c008-5a4e-4a4c-a594-f17c4a38afcf",
                                "https://firebasestorage.googleapis.com/v0/b/crysta-d58f8.firebasestorage.app/o/crystals%2FSuccess%2FAmazonite%2FAmazonite_3.png?alt=media&token=cd4f413c-2805-400a-ae49-c848a794af8e"
                        ),
                        21.99,
                        10,
                        0
                ),


                new Crystal(
                        "crystal031",
                        "Black Obsidian",
                        "Black Obsidian is a powerful volcanic glass that forms from rapidly cooled lava. It has been used for centuries as a protective shield against negativity, both external and internal. Often called the “mirror stone,” it reflects back one’s deepest truths and encourages deep self-reflection. For success, Black Obsidian is essential when you need to cut ties with limiting beliefs, bad habits, or toxic environments. It clears mental clutter and provides clarity so you can focus on your true goals. It is especially useful during times of major change or when you are pushing past your comfort zone.",
                        "Success",
                        Arrays.asList("protection", "truth", "black"),
                        Arrays.asList(
                                "https://firebasestorage.googleapis.com/v0/b/crysta-d58f8.firebasestorage.app/o/crystals%2FSuccess%2FBlack%20Obsidian%2FBlackObsidian_1.png?alt=media&token=f49c42e2-d687-43dc-b8e2-3085fc6b4f70",
                                "https://firebasestorage.googleapis.com/v0/b/crysta-d58f8.firebasestorage.app/o/crystals%2FSuccess%2FBlack%20Obsidian%2FBlackObsidian_2.png?alt=media&token=1bb86bef-2885-48eb-9c3c-52e777551225",
                                "https://firebasestorage.googleapis.com/v0/b/crysta-d58f8.firebasestorage.app/o/crystals%2FSuccess%2FBlack%20Obsidian%2FBlackObsidian_3.png?alt=media&token=af41d16c-e065-4d5b-b686-1c59f3c6fcf5"
                        ),
                        14.99,
                        10,
                        0
                ),


                new Crystal(
                        "crystal032",
                        "Carnelian",
                        "Carnelian is a vibrant orange-red stone that has been revered since ancient times for its energizing and motivational properties. Often linked with the sacral chakra, it stirs creativity, passion, and boldness. In the pursuit of success, Carnelian acts as a catalyst that helps you overcome procrastination and take inspired action. It boosts self-esteem and reminds you that your ambitions are worth chasing. Whether you are starting a new project, launching a business, or simply trying to find the courage to be seen and heard, Carnelian infuses your energy field with confidence and drive.",
                        "Success",
                        Arrays.asList("motivation", "confidence", "orange"),
                        Arrays.asList(
                                "https://firebasestorage.googleapis.com/v0/b/crysta-d58f8.firebasestorage.app/o/crystals%2FSuccess%2FCarnelian%2FCarnelian_1.png?alt=media&token=9c4b8f62-a553-4275-aee0-4ec46826508b",
                                "https://firebasestorage.googleapis.com/v0/b/crysta-d58f8.firebasestorage.app/o/crystals%2FSuccess%2FCarnelian%2FCarnelian_2.png?alt=media&token=30d275ff-766e-4410-8119-d1ea8fd73824",
                                "https://firebasestorage.googleapis.com/v0/b/crysta-d58f8.firebasestorage.app/o/crystals%2FSuccess%2FCarnelian%2FCarnelian_3.png?alt=media&token=c8f9e018-d437-4416-b963-8cee679b25fc"
                        ),
                        45.99,
                        10,
                        0
                ),


                new Crystal(
                        "crystal033",
                        "Citrine",
                        "Citrine, often known as the “merchant’s stone,” is a golden-yellow crystal that radiates joy, optimism, and prosperity. Unlike most crystals, it does not absorb negative energy but instead transforms it into positive vibrations. Associated with the solar plexus chakra, Citrine enhances confidence, personal power, and manifestation. It is ideal for anyone aiming to attract financial success, career breakthroughs, or greater self-worth. Carrying Citrine is like carrying a beam of sunshine. It uplifts your spirit, clears blockages, and inspires you to envision and create your ideal future.",
                        "Success",
                        Arrays.asList("joy", "confidence", "gold-yellow"),
                        Arrays.asList(
                                "https://firebasestorage.googleapis.com/v0/b/crysta-d58f8.firebasestorage.app/o/crystals%2FSuccess%2FCitrine%2Fcitrine_1.png?alt=media&token=199d8baf-78ef-4008-8521-0f1fbb079f62",
                                "https://firebasestorage.googleapis.com/v0/b/crysta-d58f8.firebasestorage.app/o/crystals%2FSuccess%2FCitrine%2Fcitrine_2.png?alt=media&token=2b338b45-f84c-4c1d-bcf1-ef2cd27d56ee",
                                "https://firebasestorage.googleapis.com/v0/b/crysta-d58f8.firebasestorage.app/o/crystals%2FSuccess%2FCitrine%2Fcitrine_3.png?alt=media&token=b76423e1-3d87-4d67-a161-35737f271b58"
                        ),
                        37.99,
                        10,
                        0
                ),


                new Crystal(
                        "crystal034",
                        "Garnet",
                        "Garnet is a deep red crystal known for its grounding and revitalizing energy. Historically worn as a talisman by warriors, it symbolizes strength, loyalty, and perseverance. In modern use, Garnet is a crystal of devotion to your goals. It supports sustained effort, commitment to your passions, and resilience through hardship. Garnet also activates the root chakra, keeping you grounded and focused even when the path gets challenging. It is the perfect companion when you are working on long-term success and need to maintain emotional and physical stamina.",
                        "Success",
                        Arrays.asList("commitment", "loyalty", "red"),
                        Arrays.asList(
                                "https://firebasestorage.googleapis.com/v0/b/crysta-d58f8.firebasestorage.app/o/crystals%2FSuccess%2FGarnet%2FGarnet_1.png?alt=media&token=9e70f19b-5dde-4de6-aab4-262eac70115e",
                                "https://firebasestorage.googleapis.com/v0/b/crysta-d58f8.firebasestorage.app/o/crystals%2FSuccess%2FGarnet%2FGarnet_2.png?alt=media&token=de8fd9ed-caf8-4a26-8801-8b846ccba17a",
                                "https://firebasestorage.googleapis.com/v0/b/crysta-d58f8.firebasestorage.app/o/crystals%2FSuccess%2FGarnet%2FGarnet_3.png?alt=media&token=a842387f-2022-4733-bfdd-1f9ab9ac15f0"
                        ),
                        119.99,
                        10,
                        0
                ),


                new Crystal(
                        "crystal035",
                        "Green Aventurine",
                        "Green Aventurine is a gentle, heart-centered stone that promotes growth, prosperity, and opportunity. Its sparkly shimmer, caused by mica inclusions, symbolizes the light of new possibilities. Often referred to as the “luckiest crystal,” Green Aventurine is perfect for those starting new ventures, careers, or chapters in life. It encourages risk-taking, boosts optimism, and helps you recognize and seize chances for advancement. It is especially useful when dealing with setbacks or rejections, as it nurtures a sense of hope and resilience through change.",
                        "Success",
                        Arrays.asList("luck", "growth", "green"),
                        Arrays.asList(
                                "https://firebasestorage.googleapis.com/v0/b/crysta-d58f8.firebasestorage.app/o/crystals%2FSuccess%2FGreen%20Aventurine%2FGreenAventurine_1.png?alt=media&token=d3ce8dc1-b602-4319-b19c-de4a7d1fa2c5",
                                "https://firebasestorage.googleapis.com/v0/b/crysta-d58f8.firebasestorage.app/o/crystals%2FSuccess%2FGreen%20Aventurine%2FGreenAventurine_2.png?alt=media&token=e9f4be7d-7b2a-471b-9a4b-f1fdc6a4a7bb",
                                "https://firebasestorage.googleapis.com/v0/b/crysta-d58f8.firebasestorage.app/o/crystals%2FSuccess%2FGreen%20Aventurine%2FGreenAventurine_3.png?alt=media&token=822bf720-5acc-4de9-8e4d-e5bae5ffa397"
                        ),
                        54.99,
                        10,
                        0
                ),


                new Crystal(
                        "crystal036",
                        "Malachite",
                        "Malachite is a rich green banded stone known for its powerful transformative energy. Used since ancient Egypt for protection and healing, Malachite is deeply connected to personal evolution and emotional strength. It reveals and clears energetic blockages, helping you face fears, change harmful patterns, and embrace growth. For those pursuing success, Malachite acts as a forceful yet supportive guide that pushes you to level up in your goals and take bold steps, even when uncomfortable. It is also a strong protective stone that guards your heart and intentions during transitions.",
                        "Success",
                        Arrays.asList("protection", "growth", "green"),
                        Arrays.asList(
                                "https://firebasestorage.googleapis.com/v0/b/crysta-d58f8.firebasestorage.app/o/crystals%2FSuccess%2FMalachite%2FMalachite_1.png?alt=media&token=3e25c23b-64ab-40c9-897a-58d0dcf9121a",
                                "https://firebasestorage.googleapis.com/v0/b/crysta-d58f8.firebasestorage.app/o/crystals%2FSuccess%2FMalachite%2FMalachite_2.png?alt=media&token=1e4c6dce-8678-4a2d-b9a6-e007f90d2be5",
                                "https://firebasestorage.googleapis.com/v0/b/crysta-d58f8.firebasestorage.app/o/crystals%2FSuccess%2FMalachite%2FMalachite_3.png?alt=media&token=f5f9d87b-878f-448b-9803-bcfe5e161472"
                        ),
                        159.99,
                        10,
                        0
                ),


                new Crystal(
                        "crystal037",
                        "Pyrite",
                        "Pyrite, also known as “Fool’s Gold,” is a metallic golden crystal that symbolizes wealth, determination, and masculine energy. It is a powerhouse when it comes to boosting mental clarity and productivity. Pyrite encourages a go-getter attitude, helping you stay focused, ambitious, and strategically sharp. It blocks energy leaks by shielding your aura and aligns you with an assertive and confident mindset. Whether you're aiming to improve finances, land a promotion, or bring an idea to life, Pyrite helps you believe in your value and take bold action toward abundance.",
                        "Success",
                        Arrays.asList("determination", "confidence", "gold"),
                        Arrays.asList(
                                "https://firebasestorage.googleapis.com/v0/b/crysta-d58f8.firebasestorage.app/o/crystals%2FSuccess%2FPyrite%2FPyrite_1.png?alt=media&token=f09e2ebb-e210-4ef7-9c4b-306d32b3ec82",
                                "https://firebasestorage.googleapis.com/v0/b/crysta-d58f8.firebasestorage.app/o/crystals%2FSuccess%2FPyrite%2FPyrite_2.png?alt=media&token=f8ee867f-8377-4b2e-bc6d-203147d7490f",
                                "https://firebasestorage.googleapis.com/v0/b/crysta-d58f8.firebasestorage.app/o/crystals%2FSuccess%2FPyrite%2FPyrite_3.png?alt=media&token=1e3372d6-7ef9-45ec-8649-ed7937c9ef72"
                        ),
                        31.99,
                        10,
                        0
                ),


                new Crystal(
                        "crystal038",
                        "Sodalite",
                        "Sodalite is a deep blue stone with white calcite streaks, known for enhancing rational thinking, truth, and emotional balance. It calms mental noise and sharpens analytical skills, making it an excellent tool for problem-solving and decision-making. For those seeking success, Sodalite fosters clarity of thought and confidence in judgment. It aligns the head with the heart so you can pursue your ambitions with integrity and purpose. Sodalite is especially beneficial for those in leadership, communication, or any field that requires calm intelligence under pressure.",
                        "Success",
                        Arrays.asList("truth", "clarity", "blue"),
                        Arrays.asList(
                                "https://firebasestorage.googleapis.com/v0/b/crysta-d58f8.firebasestorage.app/o/crystals%2FSuccess%2FSodalite%2FSodalite_1.png?alt=media&token=f3fcdc1b-8024-476e-8515-8c36d8bf4e0e",
                                "https://firebasestorage.googleapis.com/v0/b/crysta-d58f8.firebasestorage.app/o/crystals%2FSuccess%2FSodalite%2FSodalite_2.png?alt=media&token=abc200f0-0639-4f57-bd89-772456e80263",
                                "https://firebasestorage.googleapis.com/v0/b/crysta-d58f8.firebasestorage.app/o/crystals%2FSuccess%2FSodalite%2FSodalite_3.png?alt=media&token=d2d597eb-0b31-44e6-91ba-502df068b775"
                        ),
                        31.99,
                        10,
                        0
                ),


                new Crystal(
                        "crystal039",
                        "Sunstone",
                        "Sunstone is a radiant peach-gold stone associated with the sun’s energy such as empowerment, vitality, and optimism. In ancient times, it was believed to hold the energy of the sun god and was used for courage and fortune. Sunstone is excellent for those looking to embrace their leadership potential and let their talents shine. It brings warmth, self-assurance, and a sense of freedom, helping you release self-doubt and embrace your authenticity. It is the perfect stone when you're stepping into the spotlight or need a boost of courage and charisma to reach your goals.",
                        "Success",
                        Arrays.asList("optimism", "confidence", "orange"),
                        Arrays.asList(
                                "https://firebasestorage.googleapis.com/v0/b/crysta-d58f8.firebasestorage.app/o/crystals%2FSuccess%2FSunstone%2FSunstone_1.png?alt=media&token=8d506241-5a96-498c-9f99-12e170dc24d1",
                                "https://firebasestorage.googleapis.com/v0/b/crysta-d58f8.firebasestorage.app/o/crystals%2FSuccess%2FSunstone%2FSunstone_2.png?alt=media&token=8215e80a-3bf3-44bd-8b5b-9b035d7266e4",
                                "https://firebasestorage.googleapis.com/v0/b/crysta-d58f8.firebasestorage.app/o/crystals%2FSuccess%2FSunstone%2FSunstone_3.png?alt=media&token=55ce3d50-9b22-43d6-b7c1-fd7cfa7620b0"
                        ),
                        25.99,
                        10,
                        0
                )
        );

        // Iterate through the list of crystals and seed each one if it's not already in the database.
        for (Crystal crystal : crystals) {
            seedSingleCrystalIfMissing(crystal);
        }
    }

    /**
     * Seeds a single crystal into Firestore if it does not already exist.
     * Checks for the existence of a document with the crystal's ID in the 'crystals'
     * collection. If the document does not exist, it is created.
     *
     * @param crystal The {@link Crystal} object to be seeded.
     */
    private static void seedSingleCrystalIfMissing(Crystal crystal) {
        // Get a reference to the document in Firestore using the crystal's ID.
        db.collection("crystals").document(crystal.getId())
                .get()
                .addOnSuccessListener(document -> {
                    if (!document.exists()) {
                        // If the document does not exist, add the crystal to Firestore.
                        db.collection("crystals").document(crystal.getId()).set(crystal);
                        Log.d(TAG, "Added crystal: " + crystal.getName());
                    } else {
                        // If the document already exists, log that it's being skipped.
                        Log.d(TAG, "Crystal already exists: " + crystal.getName());
                    }
                })
                .addOnFailureListener(e -> Log.e(TAG, "Error checking crystal: " + crystal.getName(), e));
    }
}