/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.example

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.paysafe.android.hostedfields.PSCardFormController

class ActivitySample : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        PSCardFormController.addCardinalObserver(this)
        setContentView(R.layout.activity_sample)
    }

}