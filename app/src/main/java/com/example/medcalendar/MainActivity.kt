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
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.medcalendar.presentation.MainViewModel
import com.example.medcalendar.ui.theme.MedCalendarTheme
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.medcalendar.domain.model.Reminder
import com.example.medcalendar.presentation.cancelAlarm
import com.example.medcalendar.presentation.setUpAlarm
import com.example.medcalendar.presentation.setUpPeriodicAlarm
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.medcalendar.presentation.UiState
import java.lang.String.format


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MedCalendarTheme {
                    Surface(modifier = Modifier.fillMaxSize()){
                        val viewModel = hiltViewModel<MainViewModel>()
                        MainScreen(viewModel)
                    }
                }
            }
        }
    }


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: MainViewModel) {
    val reminderState by viewModel.reminder.observeAsState()
    val sheetState = rememberBottomSheetScaffoldState()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val isTimePickerOpen = remember { mutableStateOf(false) }
    val timePickerState = rememberTimePickerState()
    val format = remember{ SimpleDateFormat("hh:mm a", Locale.getDefault()) }
    val timeInMillis = remember { mutableStateOf(0L) }

    BottomSheetScaffold (
        scaffoldState = sheetState,
        sheetContent = {
            Form(time = format.format(timeInMillis.value),
                onTimeClick = {
                    isTimePickerOpen.value = true
                }) { name, dosage, isTaken ->
                val reminder = Reminder(
                    name,
                    dosage,
                    timeInMillis.value,
                    isTaken = false,
                    isRepeating = isTaken
                )
                viewModel.insert(reminder)
                if(isTaken){
                    setUpPeriodicAlarm(context, reminder)
                }else {
                    setUpAlarm(context, reminder)
                }
                scope.launch {
                    sheetState.bottomSheetState.hide()
                }
            }
        }
    ){
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {Text(text = "MedCalendar")},
                    actions = {
                    IconButton(onClick = {
                       scope.launch { sheetState.bottomSheetState.expand() }
                }){
                    Icon(imageVector = Icons.Default.Add, contentDescription = null)
                }
            })})
        { if(isTimePickerOpen.value){
                Dialog(onDismissRequest = { isTimePickerOpen.value = false }) {
                    Column {
                        TimePicker(state = timePickerState)
                        Row {
                            Button(onClick = {
                                isTimePickerOpen.value = false
                            }) {
                                Text(text = "Cancel")
                            }
                            Button(onClick = {
                                val calendar = Calendar.getInstance().apply(){
                                    set(Calendar.HOUR_OF_DAY, timePickerState.hour)
                                    set(Calendar.MINUTE, timePickerState.minute)
                                }
                                timeInMillis.value = calendar.timeInMillis
                                isTimePickerOpen.value = false
                            }) {
                                Text(text = "Confirm")
                            }
                        }
                    }
                }
            }

            when (val state = reminderState){
                is UiState.Loading -> {
                    Box(
                        modifier = Modifier
                            .padding(it)
                            .fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "Loading")
                    }
                }

                is UiState.Success -> {
                    LazyColumn(modifier = Modifier.padding(it).fillMaxSize()){
                        items(state.data){ reminder ->
                            ReminderCard(
                                reminder,
                                onDelete = {viewModel.delete(reminder)},
                                onUpdate = {
                                    val updatedReminder = reminder.copy(name = reminder.name, dosage = reminder.dosage)
                                    viewModel.update(updatedReminder)
                                    if(updatedReminder.isRepeating){
                                        cancelAlarm(context, reminder)
                                        setUpPeriodicAlarm(context, updatedReminder)

                                    }
                                })
                        }
                    }
                }

                is UiState.Failure -> {
                    Box(
                        modifier = Modifier
                            .padding(it)
                            .fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "Error: ${state.error}")
                    }
                }
                null -> {
                    Box(
                        modifier = Modifier
                            .padding(it)
                            .fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "No data")
                    }
                }
            }

        }

    }
}

@Composable
fun ReminderCard(
    reminder: Reminder,
    onDelete: () -> Unit,
    onUpdate: () -> Unit) {
    Card(modifier = Modifier.padding(8.dp)){
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ){
            Column(modifier = Modifier.weight(1f)) {
                Text(text = reminder.name)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = reminder.dosage)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = format(reminder.time.toString()))
            }
            if(reminder.isRepeating){
                IconButton(onClick = onUpdate) {
                    Icon(imageVector = Icons.Default.Schedule, contentDescription = null)
                }
            }

            IconButton(onClick = onDelete) {
                Icon(imageVector = Icons.Default.Delete, contentDescription = null)
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
            modifier = Modifier.fillMaxWidth(),
            singleLine = true)

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
            enabled = false,
            singleLine = true)

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