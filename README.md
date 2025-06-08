[![Review Assignment Due Date](https://classroom.github.com/assets/deadline-readme-button-22041afd0340ce965d47ae6ef1cefeee28c7c493a6346c4f15d667ab976d596c.svg)](https://classroom.github.com/a/BGseP8Hn

# Crysta

#Develoers:
Uday Jain (Developer 1), Oshan Premkumar (Developer 2), Nadia Askari (Developer 3)

## Introduction

Crysta is a marketplace for users to view and purchase crystals from 3 different categories: Me.
To ensure the app’s success, we created a responsive and easy-to-use platform that follows good software design practices while fulfilling all specifications given to us.
Our app contains several activities/pages that work together to provide a pleasant and efficient experience for users.

## How to run the app
1. Download the project ZIP file from this repository and unzip.
2. Install Android Studio and open the downloaded project.
3. In the device manager in android studio, create a virtual device or connect to an android device.
4. Run the application ensuring that you use the virtual or physical device created before.

## App Flow
1. When first opening the app users will see a splash screen.
2. From here users will see a page where they are able to either login or register and this uses Firebase Authentication to securely handle user information and handle session management.
3. Once logged in, users will be taken to the home page. Here they can see the three most popular items (in a recycler view with horizontal scroll and is clickable) based on how many times people have clicked it, a search bar where users can search for products based on its name or their tags, and our 3 categories in which all our crystals are split into which allows users to view crystals by category.
4. When a category is clicked, users will be taken to a page where they see a Recycler View of all the crystals in that category. From here they can either click on a crystal to view its information, add the crystal to their favourites, or search for crystals in this category using the search bar.
5. When a crystal is clicked, a details page is opened and users can view images of the crystal in a recycler view with horizontal scroll. They can also view crystal information and similar products. In this page they can also add the crystal to their favourites or add one or more of this crystal to their cart.
6. Using the bottom navigation bar users can go to their cart where they can see what crystals are in their cart, they can remove items from their cart, add more or an item and also see their total checkout cost.
7. Using the bottom navigation bar, uses can also go to their profile. Here they can see their profile information or go to pages such as the Privacy Policy or Terms of Use. They can also go to their cart or favourites.
8. When they go to see their favourites, users will see all the items they clicked the heart on. From here they can remove the item from their favourites or add it to their cart.
9. When users search for an item it takes them to a page with a recycler view of all the items that match their search by name or by their tags.

## Technologies Used
Firebase - Firestore Database was used as our database to store crystal and user information. Firebase Storage was used to store image urls for our images to our crystals.
Java - This application was programmed in Java.
Github - Github was used as our remote repository. This allowed for version control and collaboration on the project.

## Pivots made from design doc plans

1. Selling Items - While originally planning for users to make their own listings of items to sell, we did not implement this due to the time frame of the project. Due to this time constraint, this is a feature that we ope to implement in the future.
2. Classes and Activities - In our original class diagram we originally only had a few classes. As the project progressed we realised we needed more classes than originally planned and more functions in each class to ensure we could implement all the features we wanted. These new added classes include the 4 adapters, CartItem, Category, AuthActivity, BaseActivity, CategoryActivity (same as ListActivity in plan), LoginActivity, RegistrationActivity, PrivacyPolicyActivity, SplashActivity and TermsActivity.
3. Data Schemes - For users we originally planned to store the users first name, last name and phone number but we have decided against it as we believed it to be unnecessary to ask for and store.

## AI Declaration

For the creation of this app AI tools such as ChatGPT, Copilot and Deepseek were used for commenting to help others follow the code easier. These AI tools were also used to assist the development team in plugging gaps in knowledge on how to add minor features as well as assisting with bug fixes. 