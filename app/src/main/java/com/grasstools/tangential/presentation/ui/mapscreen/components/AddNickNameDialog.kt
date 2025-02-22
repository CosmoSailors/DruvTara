import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@Composable
fun AddNickNameDialog(
    onDismissRequest: () -> Unit,
    onLocationAdded: (String) -> Unit
) {
    var nickname by remember { mutableStateOf("") }

    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                DialogHeader(onDismissRequest, onLocationAdded, nickname)
                DialogContent(nickname) { nickname = it }
            }
        }
    }
}

@Composable
private fun DialogHeader(
    onDismissRequest: () -> Unit,
    onLocationAdded: (String) -> Unit,
    nickname: String
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onDismissRequest) {
            Icon(Icons.Filled.Close, contentDescription = "Close")
        }
        Text(text = "Location Details", style = MaterialTheme.typography.titleMedium)
        TextButton(onClick = { onLocationAdded(nickname) }) {
            Text("Save")
        }
    }
}

@Composable
private fun DialogContent(nickname: String, onNicknameChange: (String) -> Unit) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(text = "Latitude: ", style = MaterialTheme.typography.titleMedium)
        Text(text = "Longitude: ", style = MaterialTheme.typography.titleMedium)

        OutlinedTextField(
            value = nickname,
            onValueChange = onNicknameChange,
            label = { Text("Location Name") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
        )

        Text(text = "Save As", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 32.dp))
        SaveAsChips()
    }
}

@Composable
private fun SaveAsChips() {
    Row(modifier = Modifier.fillMaxWidth()) {
        listOf(
            "Home" to Icons.Filled.Home,
            "Work" to Icons.Filled.AccountBox,
            "Other" to Icons.Filled.Favorite
        ).forEach { (label, icon) ->
            AssistChip(
                modifier = Modifier.padding(end = 8.dp),
                onClick = { Log.d("AssistChip", "$label clicked") },
                label = { Text(label) },
                leadingIcon = {
                    Icon(icon, contentDescription = "$label Icon", Modifier.size(AssistChipDefaults.IconSize))
                }
            )
        }
    }
}
