package com.example.medcalendar

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import com.example.medcalendar.presentation.MainViewModel
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.example.medcalendar.auth.AuthState
import com.example.medcalendar.domain.model.Reminder
import com.example.medcalendar.presentation.setUpAlarm
import com.example.medcalendar.presentation.setUpPeriodicAlarm
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import com.example.medcalendar.presentation.UiState

@RequiresApi(Build.VERSION_CODES.S)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(navController: NavController, authViewModel: MainViewModel) {
    val reminderState by authViewModel.reminder.observeAsState(UiState.Loading)
    val isDialogOpen = remember { mutableStateOf(false) }
    val context = LocalContext.current
    val isTimePickerOpen = remember { mutableStateOf(false) }
    val timePickerState = rememberTimePickerState()
    val format = remember { SimpleDateFormat("hh:mm a", Locale.getDefault()) }
    val timeInMillis = remember { mutableStateOf(0L) }
    val isUpdateDialogOpen = remember { mutableStateOf(false) }
    val selectedReminder = remember { mutableStateOf<Reminder?>(null) }

    val authState = authViewModel.authState.observeAsState()

    LaunchedEffect(authState.value) {
        when (authState.value) {
            is AuthState.Unauthenticated -> navController.navigate("login")
            else -> Unit
        }
    }

    LaunchedEffect(Unit) {
        try {
            authViewModel.getReminders()
        } catch (e: Exception) {
            Log.e("MainScreen", "Error loading reminders", e)
        }
    }
    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                    actionIconContentColor = MaterialTheme.colorScheme.primary,
                    scrolledContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    navigationIconContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = { Text(
                    text = "MedCalendar",
                    style = MaterialTheme.typography.displaySmall,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis) },
                actions = {
                    IconButton(onClick = { isDialogOpen.value = true }) {
                        Icon(imageVector = Icons.Filled.AddCircle, contentDescription = null)
                    }
                    IconButton(onClick = { authViewModel.logout()}){
                        Icon(imageVector = Icons.Default.Logout, contentDescription = null)
                    }
                }
            )
        },
        content = { paddingValues ->
            Column(modifier = Modifier.padding(paddingValues)) {
                // Dialog do dodawania przypomnienia
                if (isDialogOpen.value) {
                    Dialog(onDismissRequest = { isDialogOpen.value = false }) {
                        Column(
                            modifier = Modifier
                                .padding(16.dp)
                                .background(
                                    color = MaterialTheme.colorScheme.surface,
                                    shape = MaterialTheme.shapes.medium
                                )
                        ) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Row{
                                Text(text = "Add Reminder",style = MaterialTheme.typography.titleLarge)
                                Spacer(modifier = Modifier.weight(1f))
                                IconButton( onClick = {isDialogOpen.value = false}) {
                                    Icon(imageVector = Icons.Default.Close, contentDescription = null)
                                }}
                            Spacer(modifier = Modifier.height(12.dp))
                            Form(time = format.format(timeInMillis.value),
                                onTimeClick = { isTimePickerOpen.value = true }) { name, dosage, isRepeating ->
                                val reminder = Reminder(
                                    name,
                                    dosage,
                                    timeInMillis.value,
                                    isTaken = false,
                                    isRepeating = isRepeating
                                )
                                authViewModel.insert(reminder)
                                if (isRepeating) {
                                    setUpPeriodicAlarm(context, reminder)
                                } else {
                                    setUpAlarm(context, reminder)
                                }
                                isDialogOpen.value = false
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }
                }

                // Update Dialog Open
                if (isUpdateDialogOpen.value) {
                    Dialog(onDismissRequest = {
                        isDialogOpen.value = false
                        selectedReminder.value = null
                    }) {
                        Column(
                            modifier = Modifier
                                .padding(16.dp)
                                .background(
                                    color = MaterialTheme.colorScheme.surface,
                                    shape = MaterialTheme.shapes.medium
                                )
                        ) { Spacer(modifier = Modifier.height(12.dp))
                            Row{
                                Text(text = "Update Reminder",style = MaterialTheme.typography.titleLarge)
                                Spacer(modifier = Modifier.weight(1f))
                                IconButton( onClick = {isUpdateDialogOpen.value = false}) {
                                    Icon(imageVector = Icons.Default.Close, contentDescription = null)
                                }
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Form(time = format.format(selectedReminder.value?.time ?: 0L),
                                onTimeClick = { isTimePickerOpen.value = true }) { name, dosage, isRepeating ->
                                val updatedReminder = selectedReminder.value?.copy(
                                    name,
                                    dosage,
                                    timeInMillis.value,
                                    isTaken = false,
                                    isRepeating = isRepeating
                                )
                                if (updatedReminder != null) {
                                    authViewModel.update(updatedReminder)
                                }
                                if (isRepeating) {
                                    if (updatedReminder != null) {
                                        setUpPeriodicAlarm(context, updatedReminder)
                                    }
                                } else {
                                    if (updatedReminder != null) {
                                        setUpAlarm(context, updatedReminder)
                                    }
                                }
                                isUpdateDialogOpen.value = false
                                selectedReminder.value = null
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                        }}
                }
            }

            // TimePicker Dialog
            if (isTimePickerOpen.value) {
                Dialog(onDismissRequest = { isTimePickerOpen.value = false }) {
                    Column (
                        Modifier.background(color = MaterialTheme.colorScheme.surface, shape = MaterialTheme.shapes.medium)
                    ){
                        Spacer(modifier = Modifier.height(12.dp))
                        TimePicker(state = timePickerState)
                        Row {
                            Spacer(modifier = Modifier.weight(1f))
                            Button(onClick = { isTimePickerOpen.value = false }) {
                                Text(text = "Cancel")
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Button(onClick = {
                                val calendar = Calendar.getInstance().apply {
                                    set(Calendar.HOUR_OF_DAY, timePickerState.hour)
                                    set(Calendar.MINUTE, timePickerState.minute)
                                }
                                timeInMillis.value = calendar.timeInMillis
                                isTimePickerOpen.value = false
                            }) {
                                Text(text = "Confirm")
                            }
                            Spacer(modifier = Modifier.weight(1f))
                        }
                        Spacer(modifier = Modifier.height(12.dp))

                    }
                }
            }

            // Wyświetlanie danych przypomnień
            when (val state = reminderState) {
                is UiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = "Loading")
                    }
                }
                is UiState.Success -> {
                    if (state.data.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(text = "Add your medicine reminders!")
                        }
                    } else {
                        LazyColumn(modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)) {
                            items(state.data) { reminder ->
                                ReminderCard(
                                    reminder,
                                    onDelete = { authViewModel.delete(reminder) },
                                    onUpdate = {
                                        isUpdateDialogOpen.value = true
                                        selectedReminder.value = reminder
                                        timeInMillis.value = reminder.time},
                                    onIsTakenChange = { updatedReminder ->
                                        authViewModel.update(updatedReminder)
                                    }
                                )
                            }
                        }}}

                is UiState.Failure -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = "Error: ${state.error}")
                    }
                }
                null -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = "Add your medicine reminders!")
                    }
                }
            }

        }
    )
}



@Composable
fun ReminderCard(
    reminder: Reminder,
    onDelete: () -> Unit,
    onUpdate: () -> Unit,
    onIsTakenChange: (Reminder) -> Unit) {
    val formattedTime = remember {
        SimpleDateFormat("hh:mm a", Locale.getDefault()).format(reminder.time)
    }
    Card(modifier = Modifier.padding(8.dp), colors = CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.secondaryContainer,
    )){
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ){
            Column(modifier = Modifier.weight(1f)) {
                Text(text = reminder.name, style = MaterialTheme.typography.headlineMedium)
                Spacer(modifier = Modifier.height(10.dp))
                Text(text = reminder.dosage, style = MaterialTheme.typography.bodyLarge)
                Spacer(modifier = Modifier.height(6.dp))
                Text(text = formattedTime, style = MaterialTheme.typography.bodyLarge)
            }
            Checkbox(checked = reminder.isTaken, onCheckedChange = { isChecked ->
                onIsTakenChange(reminder.copy(isTaken = isChecked)) })
            IconButton(onClick = onUpdate) {
                Icon(imageVector = Icons.Default.Edit, contentDescription = null)
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

    val keyboardController = LocalSoftwareKeyboardController.current

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
            singleLine = true,
            label = { Text(text = "Name") },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() })
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = dosage.value,
            onValueChange = {dosage.value = it},
            modifier = Modifier.fillMaxWidth(),
            label = { Text(text = "Dosage") },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { keyboardController?.hide()}
            ))

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = time,
            onValueChange = {onTimeClick.invoke()},
            modifier = Modifier
                .clickable { onTimeClick.invoke() }
                .fillMaxWidth(),
            enabled = false,
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { keyboardController?.hide()})
            )

        Spacer(modifier = Modifier.height(12.dp))

        Row (
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically)
        {
            Text(text = "Set reminder")
            Spacer(modifier = Modifier.width(12.dp))

            Switch(checked = isChecked.value, onCheckedChange = { isChecked.value = it })

        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(onClick = { onClick.invoke(name.value, dosage.value, isChecked.value) }){
            Text(text = "Save")
        }


        Spacer(modifier = Modifier.height(4.dp))
    }
}