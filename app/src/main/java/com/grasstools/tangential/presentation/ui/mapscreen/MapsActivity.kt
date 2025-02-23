package com.grasstools.tangential.presentation.ui.mapscreen

import AddNickNameDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.grasstools.tangential.App
import com.grasstools.tangential.domain.model.Geofence
import com.grasstools.tangential.domain.model.GeofenceType
import com.grasstools.tangential.presentation.ui.alarmscreen.AlarmScreen
import com.grasstools.tangential.ui.theme.TangentialTheme
import com.grasstools.tangential.presentation.ui.locationlist.LocationListActivity
import com.grasstools.tangential.presentation.ui.mapscreen.components.AddLocationCard
import com.grasstools.tangential.presentation.ui.mapscreen.components.GoogleMapComposable
import kotlinx.coroutines.launch
import kotlin.reflect.typeOf

class MapsActivity : ComponentActivity() {

    private val database by lazy { (application as App).database }
    private val viewModel by viewModels<MapsViewModel> {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return MapsViewModel(database.dao(), applicationContext) as T
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MapsScreen()
        }
    }

    @Composable
    private fun MapsScreen() {
        var showDialog by remember { mutableStateOf(false) }
        val latitude by viewModel.latitude.collectAsState()
        val longitude by viewModel.longitude.collectAsState()

        val type by viewModel.type.collectAsState()

        TangentialTheme {
            Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                Box(modifier = Modifier.fillMaxSize()) {
                    GoogleMapComposable(
                        modifier = Modifier.fillMaxSize(),
                        sliderPosition = viewModel.sliderPosition,
                        onLatLongChange = { viewModel.updateLatLong(it) }
                        , latLng = LatLng( latitude, longitude ),
                        vm = viewModel
                    )

                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                    ) {
                        Button(onClick = {
                            viewModel.getCurrentLocation()
                                         }, modifier = Modifier
                            .padding(4.dp)
                            .align(Alignment.CenterHorizontally)
                        ) {
                            Text(
                                text = "Current Location",
                                textAlign = TextAlign.Center
                            )
                        }

                        AddLocationCard(
                            modifier = Modifier
                                .fillMaxWidth().height(200.dp)
                                ,
                            onSavedLocationsClick = { onSavedLocationsClick() },
                            onAddLocationClick = { showDialog = true },
                            onSettingsClick = { onSettingsClick() },
                            sliderPosition = viewModel.sliderPosition,
                            onSliderChange = { viewModel.updateSliderPosition(it)
                            },
                            vm = viewModel
                        )
                    }



                    if (showDialog) {
                        AddNickNameDialog(
                            onDismissRequest = { showDialog = false },
                            onLocationAdded = { nickname ->
                                insertTrigger(
                                    nickname,
                                    type = viewModel.type.value
                                )
                                showDialog = false
                                navigateToLocationListActivity()
                            },
                            latitude = latitude,
                            longitude = longitude,
                            radius = viewModel.radius,
                            vm = viewModel
                        )
                    }
                }
            }
        }
    }
    private fun insertTrigger(nickname: String, type: GeofenceType) {
        viewModel.viewModelScope.launch {
            viewModel.insertLocationTrigger(
                Geofence(
                    name = nickname,
                    latitude = viewModel.latitude.value,
                    longitude = viewModel.longitude.value,
                    radius = viewModel.radius,
                    type = type,
                    config = "",
                    enabled = true
                )
            )
        }
    }

    private fun navigateToLocationListActivity() {
        startActivity(Intent(this, LocationListActivity::class.java))
    }

    private fun onSavedLocationsClick() {
        startActivity(Intent(this, LocationListActivity::class.java))

        Log.i("MapsActivity", "Saved Locations Clicked")
    }

    private fun onSettingsClick() {
        Log.i("MapsActivity", "Settings Clicked")
    }
}
