package com.aistudio.birbirbibleschool.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Church
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aistudio.birbirbibleschool.data.InitialData
import com.aistudio.birbirbibleschool.model.CurrentUser
import com.aistudio.birbirbibleschool.model.NotificationType
import com.aistudio.birbirbibleschool.model.ProgramType
import com.aistudio.birbirbibleschool.model.Student
import com.aistudio.birbirbibleschool.model.UserRole
import com.aistudio.birbirbibleschool.ui.theme.Amber400
import com.aistudio.birbirbibleschool.ui.theme.Emerald800
import com.aistudio.birbirbibleschool.ui.theme.Emerald900
import com.aistudio.birbirbibleschool.ui.theme.Emerald950
import com.aistudio.birbirbibleschool.ui.theme.Slate100
import com.aistudio.birbirbibleschool.ui.theme.Slate200
import com.aistudio.birbirbibleschool.ui.theme.Slate700
import com.aistudio.birbirbibleschool.ui.theme.Slate800
import com.aistudio.birbirbibleschool.ui.theme.Slate900

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun LoginScreen(
    students: List<Student>,
    teachers: List<String>,
    userPasswords: Map<String, String>,
    systemLocked: Boolean,
    onLogin: (CurrentUser) -> Unit,
    showNotify: (String, NotificationType) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedRole by remember { mutableStateOf(UserRole.STUDENT) }
    var studentProgram by remember { mutableStateOf(ProgramType.Diploma) }
    var studentId by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var teacherKey by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }

    val roles = listOf(
        UserRole.STUDENT to "ተማሪ",
        UserRole.TEACHER to "መምህር",
        UserRole.DEAN to "ዲን",
        UserRole.REGISTRAR to "ሪጅስተራል",
        UserRole.ADMIN to "አድሚን"
    )

    fun attemptLogin() {
        if (systemLocked && selectedRole != UserRole.ADMIN) {
            showNotify("ሲስተሙ በጥገና ሁነታ ላይ ስለሆነ ከአድሚን ውጪ መግባት አይቻልም!", NotificationType.ERROR)
            return
        }

        when (selectedRole) {
            UserRole.STUDENT -> {
                val cleanId = studentId.trim()
                if (cleanId.isEmpty()) {
                    showNotify("እባክዎ የተማሪ መለያ ኮድ ያስገቡ!", NotificationType.ERROR)
                    return
                }

                val found = students.firstOrNull { it.id == cleanId && it.program == studentProgram }
                    ?: students.firstOrNull { it.id == cleanId }

                if (found == null) {
                    showNotify("የተሳሳተ የተማሪ መለያ ኮድ ($cleanId)!", NotificationType.ERROR)
                    return
                }

                val expectedPass = userPasswords[found.uid] ?: found.id
                if (password.trim() != expectedPass) {
                    showNotify("የተሳሳተ የተማሪ የይለፍ ቃል!", NotificationType.ERROR)
                    return
                }

                onLogin(
                    CurrentUser(
                        role = UserRole.STUDENT,
                        id = found.id,
                        uid = found.uid,
                        name = found.name,
                        program = found.program
                    )
                )
            }

            UserRole.TEACHER -> {
                val cleanKey = teacherKey.trim()
                if (cleanKey.isEmpty()) {
                    showNotify("እባክዎ የመምህር የመግቢያ ቁልፍ ያስገቡ!", NotificationType.ERROR)
                    return
                }

                var matchedTeacher: String? = null
                for (t in teachers) {
                    if (t.contains("—")) continue
                    val assignedKey = userPasswords[t] ?: InitialData.DEFAULT_TEACHER_KEYS[t]
                    if (assignedKey?.equals(cleanKey, ignoreCase = true) == true) {
                        matchedTeacher = t
                        break
                    }
                }

                if (matchedTeacher == null) {
                    showNotify("የተሳሳተ የመምህር የመግቢያ ቁልፍ!", NotificationType.ERROR)
                    return
                }

                if (userPasswords["_locked_$matchedTeacher"] == "true") {
                    showNotify("ይህ የመምህር መለያ በዋና አድሚን ታግዷል!", NotificationType.ERROR)
                    return
                }

                onLogin(
                    CurrentUser(
                        role = UserRole.TEACHER,
                        id = matchedTeacher,
                        name = matchedTeacher
                    )
                )
            }

            UserRole.ADMIN, UserRole.DEAN, UserRole.REGISTRAR -> {
                val roleKey = selectedRole.name.lowercase()
                val defaultPass = when (selectedRole) {
                    UserRole.ADMIN -> "admin123"
                    UserRole.DEAN -> "dean123"
                    UserRole.REGISTRAR -> "reg123"
                    else -> ""
                }
                val expectedPass = userPasswords[roleKey] ?: defaultPass

                if (password.trim() != expectedPass) {
                    showNotify("የተሳሳተ የይለፍ ቃል!", NotificationType.ERROR)
                    return
                }

                val roleName = when (selectedRole) {
                    UserRole.ADMIN -> "አድሚን (Admin)"
                    UserRole.DEAN -> "ዋና ዲን (Academic Dean)"
                    UserRole.REGISTRAR -> "ሪጅስተራል (Registrar)"
                    else -> selectedRole.name
                }

                onLogin(
                    CurrentUser(
                        role = selectedRole,
                        id = roleKey,
                        name = roleName
                    )
                )
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Slate100)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Hero Card with Branding
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Emerald950),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(Emerald800),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Church,
                        contentDescription = null,
                        tint = Amber400,
                        modifier = Modifier.size(40.dp)
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "የብርብር ከተማ ቃለ ሕይወት ቤተ ክርስቲያን",
                    color = Amber400,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "መጽሐፍ ቅዱስ ትምህርት ቤት የመረጃ ማኔጅመንት ሲስተም",
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Login Box
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Text(
                    text = "ወደ ሲስተሙ ይግቡ (Sign In)",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Slate900
                )
                Spacer(modifier = Modifier.height(14.dp))

                // Role Selector Chips
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    roles.forEach { (r, label) ->
                        val isSelected = selectedRole == r
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedRole = r },
                            label = {
                                Text(
                                    text = label,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Emerald800,
                                selectedLabelColor = Color.White,
                                containerColor = Slate100,
                                labelColor = Slate800
                            ),
                            modifier = Modifier.testTag("role_tab_${r.name.lowercase()}")
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Form based on role
                when (selectedRole) {
                    UserRole.STUDENT -> {
                        // Program Type
                        Text(
                            text = "የትምህርት ፕሮግራም ይምረጡ:",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Slate700
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf(
                                ProgramType.Diploma to "ዲፕሎማ (Diploma)",
                                ProgramType.Degree to "ዲግሪ (Degree)"
                            ).forEach { (prog, label) ->
                                val isSel = studentProgram == prog
                                FilterChip(
                                    selected = isSel,
                                    onClick = { studentProgram = prog },
                                    label = { Text(label, fontSize = 11.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = Amber400,
                                        selectedLabelColor = Emerald950,
                                        containerColor = Slate100
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedTextField(
                            value = studentId,
                            onValueChange = { studentId = it },
                            label = { Text("የተማሪ መለያ ቁጥር (Student ID e.g. 201901)") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("student_id_input"),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )

                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            label = { Text("የይለፍ ቃል (Password)") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("password_input"),
                            singleLine = true,
                            visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                            trailingIcon = {
                                IconButton(onClick = { showPassword = !showPassword }) {
                                    Icon(
                                        imageVector = if (showPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                        contentDescription = null
                                    )
                                }
                            }
                        )
                    }

                    UserRole.TEACHER -> {
                        Text(
                            text = "መምህራን በዋና አድሚኑ የተዘጋጀላቸውን ልዩ ቁልፍ (Teacher Key) በማስገባት በቀጥታ ይገባሉ:",
                            fontSize = 12.sp,
                            color = Slate700
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedTextField(
                            value = teacherKey,
                            onValueChange = { teacherKey = it },
                            label = { Text("የመምህር መግቢያ ቁልፍ (Teacher Key)") },
                            placeholder = { Text("ለምሳሌ: BK-SOL-2019") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("teacher_key_input"),
                            singleLine = true
                        )
                    }

                    UserRole.ADMIN, UserRole.DEAN, UserRole.REGISTRAR -> {
                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            label = { Text("የይለፍ ቃል (${selectedRole.name} Password)") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("staff_password_input"),
                            singleLine = true,
                            visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                            trailingIcon = {
                                IconButton(onClick = { showPassword = !showPassword }) {
                                    Icon(
                                        imageVector = if (showPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                        contentDescription = null
                                    )
                                }
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = { attemptLogin() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("submit_login_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = Emerald800),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "ይግቡ (Login)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Color.White
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Demo Quick-Access Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Amber400.copy(alpha = 0.15f))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "⚡ ፈጣን መግቢያ (Quick Test Accounts):",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = Emerald950
                )
                Spacer(modifier = Modifier.height(6.dp))
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Button(
                        onClick = {
                            selectedRole = UserRole.ADMIN
                            password = "admin123"
                            attemptLogin()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Emerald800),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("አድሚን (admin123)", fontSize = 11.sp)
                    }

                    Button(
                        onClick = {
                            selectedRole = UserRole.DEAN
                            password = "dean123"
                            attemptLogin()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Emerald800),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("ዲን (dean123)", fontSize = 11.sp)
                    }

                    Button(
                        onClick = {
                            selectedRole = UserRole.REGISTRAR
                            password = "reg123"
                            attemptLogin()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Emerald800),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("ሪጅስተራል (reg123)", fontSize = 11.sp)
                    }

                    Button(
                        onClick = {
                            selectedRole = UserRole.TEACHER
                            teacherKey = "BK-SOL-2019"
                            attemptLogin()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Amber400),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("መምህር ሰሎሞን", fontSize = 11.sp, color = Emerald950, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = {
                            selectedRole = UserRole.STUDENT
                            studentProgram = ProgramType.Diploma
                            studentId = "201901"
                            password = "201901"
                            attemptLogin()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Slate800),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("ተማሪ ሊዲያ ጳዉሎስ (201901)", fontSize = 11.sp)
                    }
                }
            }
        }
    }
}
