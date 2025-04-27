package com.example.vivu_application.view.onboarding


import androidx.annotation.DrawableRes
import com.example.vivu_application.R

sealed class OnboardingModel(
    @DrawableRes val image: Int,
    val title: String,
    val description: String,
    val buttonText1 : String = "",
    val buttonText2 :String = ""
){

    data object FirstPages: OnboardingModel(
        image = R.drawable.image1,
        title = "Welcome to ViVu!",
        description = "Let's explore beautiful places with ViVu and enjoy memorable trips!"
    )

    data object SecondPages: OnboardingModel(
        title = "Top Destinations in Vietnam",
        image = R.drawable.img2_2,
        description = "View reviews, photos, and detailed information about the hottest travel spots in Vietnam!",
    )

    data object ThirdPages: OnboardingModel(
        title = "Discover Your Perfect Tour",
        image = R.drawable.img_3,
        description = "Easily search for the best tours that match your preferences in just a few simple steps."
    )
    data object FourthPages: OnboardingModel(
        title = "Join Now for the Full Experience!",
        image = R.drawable.image4,
        description = "Create an account to save your itinerary, book tours, and receive exclusive deals.",
        buttonText1 = "Sign in",
        buttonText2 = "Sign up"
    )
}