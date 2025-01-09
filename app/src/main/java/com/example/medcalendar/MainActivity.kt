package com.example.medcalendar

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.medcalendar.presentation.MainViewModel
import com.example.medcalendar.ui.theme.MedCalendarTheme
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.medcalendar.presentation.cancelAlarm
import kotlinx.coroutines.launch
import java.text.Normalizer

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MedCalendarTheme {

                }
            }
        }
    }


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: MainViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val sheetState = rememberBottomSheetScaffoldState()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    BottomSheetScaffold (
        scaffoldState = sheetState,
        sheetContent = {
            Form(time = "", onTimeClick = {/*TODO*/}) { name, dosage, isTaken -> }
        }){
        Scaffold(
            topBar = {TopAppBar(
                title = {Text(text = "MedCalendar")},
                actions = {
                IconButton(onClick = {
                   scope.launch { sheetState.bottomSheetState.expand() }
                }){
                    Icon(imageVector = Icons.Default.Add, contentDescription = null)
                }
            })}) {
            if (uiState.data.isEmpty()) {
                Box(
                    modifier = Modifier
                        .padding(it)
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "No data")
                }
            } else {
                LazyColumn(modifier = Modifier.padding(it).fillMaxSize()){
                    items(uiState.data){
                        Card(modifier = Modifier.padding(8.dp)) {
                            Row(modifier = Modifier.padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically) {
                                Column (modifier = Modifier.weight(1f)){
                                    Text(text = it.name)
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(text = it.dosage)
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(text = it.time.toString())
                                }

                                if(it.isRepeating){
                                    IconButton(onClick = {
                                        cancelAlarm(context, it)
                                        viewModel.update(it.copy(isTaken = true, isRepeating = false))
                                    }
                                        ) {
                                        Icon(imageVector = Icons.Default.Schedule, contentDescription = null)
                                    }
                                }

                                IconButton(onClick = {
                                    cancelAlarm(context, it)
                                    viewModel.delete(it) }) {
                                    Icon(imageVector = Icons.Default.Delete, contentDescription = null)
                                }
                            }
                        }
                    }
                }
            }
        }

    }
}

@Composable
fun Form(time : String, onTimeClick : () -> Unit, onClick : (String, String, Boolean) -> Unit){

    val name = remember { mutableStateOf("") }
    val dosage = remember { mutableStateOf("") }

    val isChecked = remember { mutableStateOf(false) }

    Column (
        modifier = Modifier
            .padding(12.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = name.value,
            onValueChange = {name.value = it},
            modifier = Modifier.fillMaxWidth())

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = dosage.value,
            onValueChange = {dosage.value = it},
            modifier = Modifier.fillMaxWidth())

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = time,
            onValueChange = {onTimeClick.invoke()},
            modifier = Modifier
                .clickable {}
                .fillMaxWidth(),
            enabled = false)

        Spacer(modifier = Modifier.height(8.dp))

        Row (
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically)
        {
            Text(text = "Schedule")
            Spacer(modifier = Modifier.width(12.dp))

            Switch(checked = isChecked.value, onCheckedChange = { isChecked.value = it })

        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(onClick = { onClick.invoke(name.value, dosage.value, isChecked.value) }){
            Text(text = "Save")
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}