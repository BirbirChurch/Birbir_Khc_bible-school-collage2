package com.aistudio.birbirbibleschool.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aistudio.birbirbibleschool.data.InitialData
import com.aistudio.birbirbibleschool.model.Course
import com.aistudio.birbirbibleschool.model.GradeBreakdown
import com.aistudio.birbirbibleschool.model.NotificationType
import com.aistudio.birbirbibleschool.model.OfficialSignaturesConfig
import com.aistudio.birbirbibleschool.model.ProgramType
import com.aistudio.birbirbibleschool.model.Student
import com.aistudio.birbirbibleschool.ui.theme.Amber400
import com.aistudio.birbirbibleschool.ui.theme.Emerald800
import com.aistudio.birbirbibleschool.ui.theme.Emerald950
import com.aistudio.birbirbibleschool.ui.theme.Red600
import com.aistudio.birbirbibleschool.ui.theme.Slate100
import com.aistudio.birbirbibleschool.ui.theme.Slate200
import com.aistudio.birbirbibleschool.ui.theme.Slate700
import com.aistudio.birbirbibleschool.ui.theme.Slate800
import com.aistudio.birbirbibleschool.ui.theme.Slate900

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AdminPortalScreen(
    students: List<Student>,
    courses: List<Course>,
    teachers: List<String>,
    grades: Map<String, GradeBreakdown>,
    userPasswords: Map<String, String>,
    systemLocked: Boolean,
    gradePublished: Boolean,
    currentYear: String,
    currentSemester: String,
    officialConfig: OfficialSignaturesConfig,
    onToggleLock: () -> Unit,
    onToggleGradePublication: () -> Unit,
    onAddTeacher: (String, String?) -> Unit,
    onDeleteTeacher: (String) -> Unit,
    onToggleTeacherLock: (String) -> Unit,
    onUpdatePassword: (String, String) -> Unit,
    onSaveOfficialConfig: (OfficialSignaturesConfig) -> Unit,
    onResetDefaults: () -> Unit,
    onClearAllGrades: () -> Unit,
    onSelectPrintTranscript: (Student) -> Unit,
    showNotify: (String, NotificationType) -> Unit,
    modifier: Modifier = Modifier
) {
    var activeTab by remember { mutableStateOf(0) }
    val tabTitles = listOf("አጠቃላይ ዳሽቦርድ", "መምህራን ማኔጅመንት", "የይለፍ ቃሎች", "ማህተምና ፊርማ", "ፈጣን ፍተሻ")

    // Teacher Add dialog state
    var newTeacherName by remember { mutableStateOf("") }
    var newTeacherKey by remember { mutableStateOf("") }

    // Password change dialog state
    var targetAccountKey by remember { mutableStateOf("admin") }
    var newPasswordValue by remember { mutableStateOf("") }

    // Official config editable states
    var deanName by remember { mutableStateOf(officialConfig.deanName) }
    var deanTitle by remember { mutableStateOf(officialConfig.deanTitle) }
    var registrarName by remember { mutableStateOf(officialConfig.registrarName) }
    var registrarTitle by remember { mutableStateOf(officialConfig.registrarTitle) }

    // Student Search query
    var searchQuery by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Slate100)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Portal Header Banner
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Emerald950)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Emerald800),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = null,
                        tint = Amber400,
                        modifier = Modifier.size(28.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "የዋና አድሚን ቁጥጥር ማዕከል (Super Admin Portal)",
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "የሙሉ ሲስተሙ መረጃዎች፣ መምህራን፣ የይለፍ ቃሎች እና የቁልፍ ማስተዳደሪያ",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 11.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Navigation Tabs
        TabRow(
            selectedTabIndex = activeTab,
            containerColor = Color.White,
            contentColor = Emerald800,
            modifier = Modifier.clip(RoundedCornerShape(12.dp))
        ) {
            tabTitles.forEachIndexed { index, title ->
                Tab(
                    selected = activeTab == index,
                    onClick = { activeTab = index },
                    text = {
                        Text(
                            text = title,
                            fontSize = 12.sp,
                            fontWeight = if (activeTab == index) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        when (activeTab) {
            0 -> {
                // TAB 0: OVERVIEW & SYSTEM TOGGLES
                // Metric Summary Cards
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("ተማሪዎች በጠቅላላ", fontSize = 11.sp, color = Slate700)
                            Text("${students.size}", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Emerald800)
                            Text(
                                "ዲግሪ: ${students.count { it.program == ProgramType.Degree }} | ዲፕሎማ: ${students.count { it.program == ProgramType.Diploma }}",
                                fontSize = 10.sp,
                                color = Color.Gray
                            )
                        }
                    }

                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("ትምህርቶች (Courses)", fontSize = 11.sp, color = Slate700)
                            Text("${courses.size}", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Amber400)
                            Text("በ2ቱም ፕሮግራሞች", fontSize = 10.sp, color = Color.Gray)
                        }
                    }

                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("መምህራን", fontSize = 11.sp, color = Slate700)
                            Text("${teachers.count { !it.contains("—") }}", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Slate900)
                            Text("ተመዝጋቢ", fontSize = 10.sp, color = Color.Gray)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // System Control Switches Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "የሲስተም ቁልፎችና ፍቃዶች (System Controls)",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Slate900
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        // Maintenance Lock Switch
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "የጥገና ሁነታ (Maintenance Lock)",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (systemLocked) Red600 else Slate800
                                )
                                Text(
                                    text = if (systemLocked) "ሲስተሙ ተቆልፏል — ከአድሚን ውጪ ማንም መግባት አይችልም" else "ሲስተሙ ለሁሉም ክፍት ነው",
                                    fontSize = 11.sp,
                                    color = Color.Gray
                                )
                            }
                            Switch(
                                checked = systemLocked,
                                onCheckedChange = { onToggleLock() },
                                colors = SwitchDefaults.colors(checkedThumbColor = Red600),
                                modifier = Modifier.testTag("maintenance_toggle")
                            )
                        }

                        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Slate200)

                        // Grade Release Switch
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "ውጤት ለተማሪዎች ይፋ ማድረጊያ (Release Grades)",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (gradePublished) Emerald800 else Color(0xFFD97706)
                                )
                                Text(
                                    text = if (gradePublished) "ውጤቶች ለተማሪዎች በግል ገጻቸው ይታያሉ" else "ውጤት ለተማሪዎች ተዘግቷል (መምህራን ብቻ ያያሉ)",
                                    fontSize = 11.sp,
                                    color = Color.Gray
                                )
                            }
                            Switch(
                                checked = gradePublished,
                                onCheckedChange = { onToggleGradePublication() },
                                colors = SwitchDefaults.colors(checkedThumbColor = Emerald800),
                                modifier = Modifier.testTag("publish_grades_toggle")
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Reset System Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "የመረጃ ዳግም ማስጀመሪያ (Reset / Database Controls)",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Slate900
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "የመጀመሪያውን 70 ተማሪዎች፣ 16 ኮርሶች እና የፈተና ውጤቶች ዳግም ወደነበሩበት ይመልሳል:",
                            fontSize = 11.sp,
                            color = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Button(
                                onClick = onResetDefaults,
                                colors = ButtonDefaults.buttonColors(containerColor = Emerald800),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.testTag("reset_defaults_button")
                            ) {
                                Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("ወደ መጀመሪያው መልስ", fontSize = 12.sp)
                            }

                            OutlinedButton(
                                onClick = onClearAllGrades,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("ውጤቶችን አጽዳ", fontSize = 12.sp, color = Red600)
                            }
                        }
                    }
                }
            }

            1 -> {
                // TAB 1: TEACHER MANAGEMENT
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "አዲስ መምህር መመዝገቢያ (Register New Teacher)",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Slate900
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = newTeacherName,
                            onValueChange = { newTeacherName = it },
                            label = { Text("የመምህሩ ሙሉ ስም (Teacher Name)") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = newTeacherKey,
                            onValueChange = { newTeacherKey = it },
                            label = { Text("የመግቢያ ቁልፍ (አማራጭ - ካልተሞላ በራሱ ያዘጋጃል)") },
                            placeholder = { Text("ለምሳሌ: BK-SOL-2019") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Button(
                            onClick = {
                                if (newTeacherName.isNotBlank()) {
                                    onAddTeacher(newTeacherName, newTeacherKey)
                                    newTeacherName = ""
                                    newTeacherKey = ""
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Emerald800),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("add_teacher_button")
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("መምህር መዝግብ")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Teachers Roster Table
                Text(
                    text = "የተመዘገቡ መምህራንና የመግቢያ ቁልፎቻቸው (Registered Teachers & Dedicated Keys)",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Slate900
                )
                Spacer(modifier = Modifier.height(8.dp))

                teachers.filter { !it.contains("—") }.forEachIndexed { idx, t ->
                    val key = userPasswords[t] ?: InitialData.DEFAULT_TEACHER_KEYS[t] ?: "BK-TCH-$idx"
                    val isLocked = userPasswords["_locked_$t"] == "true"

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        colors = CardDefaults.cardColors(containerColor = if (isLocked) Slate200 else Color.White),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = t,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isLocked) Color.Gray else Slate900
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Surface(
                                    color = Amber400.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = "ቁልፍ: $key",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Emerald950,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(onClick = { onToggleTeacherLock(t) }) {
                                    Icon(
                                        imageVector = if (isLocked) Icons.Default.Lock else Icons.Default.LockOpen,
                                        contentDescription = "መቆለፊያ",
                                        tint = if (isLocked) Red600 else Emerald800
                                    )
                                }
                                IconButton(onClick = { onDeleteTeacher(t) }) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "ሰርዝ",
                                        tint = Red600
                                    )
                                }
                            }
                        }
                    }
                }
            }

            2 -> {
                // TAB 2: PASSWORD MANAGEMENT
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "የይለፍ ቃል መቀየሪያ (Change Passwords)",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Slate900
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        Text("የአካውንት አይነት ይምረጡ:", fontSize = 12.sp, color = Slate700)
                        Spacer(modifier = Modifier.height(4.dp))
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            listOf(
                                "admin" to "ዋና አድሚን (admin)",
                                "dean" to "ዋና ዲን (dean)",
                                "registrar" to "ሪጅስተራል (reg)"
                            ).forEach { (k, label) ->
                                val isSel = targetAccountKey == k
                                FilterChip(
                                    selected = isSel,
                                    onClick = { targetAccountKey = k },
                                    label = { Text(label, fontSize = 11.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = Emerald800,
                                        selectedLabelColor = Color.White
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedTextField(
                            value = newPasswordValue,
                            onValueChange = { newPasswordValue = it },
                            label = { Text("አዲስ የይለፍ ቃል (New Password)") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = {
                                if (newPasswordValue.isNotBlank()) {
                                    onUpdatePassword(targetAccountKey, newPasswordValue.trim())
                                    newPasswordValue = ""
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Emerald800),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.Key, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("የይለፍ ቃል ቀይር")
                        }
                    }
                }
            }

            3 -> {
                // TAB 3: OFFICIAL SIGNATURES & STAMP
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "የትራንስክሪፕት ይፋዊ ፊርማዎችና ማህተም (Official Signatures & Seal)",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Slate900
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = deanName,
                            onValueChange = { deanName = it },
                            label = { Text("የዋና ዲን ስም (Academic Dean Name)") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = deanTitle,
                            onValueChange = { deanTitle = it },
                            label = { Text("የዋና ዲን ማዕረግ (Dean Title)") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(14.dp))
                        OutlinedTextField(
                            value = registrarName,
                            onValueChange = { registrarName = it },
                            label = { Text("የሪጅስተራል ስም (Registrar Name)") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = registrarTitle,
                            onValueChange = { registrarTitle = it },
                            label = { Text("የሪጅስተራል ማዕረግ (Registrar Title)") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = {
                                onSaveOfficialConfig(
                                    officialConfig.copy(
                                        deanName = deanName,
                                        deanTitle = deanTitle,
                                        registrarName = registrarName,
                                        registrarTitle = registrarTitle
                                    )
                                )
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Emerald800),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("ለውጦቹን አስቀምጥ")
                        }
                    }
                }
            }

            4 -> {
                // TAB 4: QUICK STUDENT TRANSCRIPT SEARCH
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "ፈጣን የተማሪ ትራንስክሪፕት ፍለጋ (Quick Search & Print)",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Slate900
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            label = { Text("የተማሪ ስም ወይም መለያ ቁጥር ይፈልጉ...") },
                            trailingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(12.dp))
                        val filtered = students.filter {
                            searchQuery.isBlank() ||
                            it.name.contains(searchQuery.trim(), ignoreCase = true) ||
                            it.id.contains(searchQuery.trim())
                        }.take(15)

                        filtered.forEach { s ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(0.5.dp, Slate200, RoundedCornerShape(8.dp))
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(s.name, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Text("ID: ${s.id} | ${if (s.program == ProgramType.Degree) "ዲግሪ" else "ዲፕሎማ"}", fontSize = 11.sp, color = Color.Gray)
                                }
                                Button(
                                    onClick = { onSelectPrintTranscript(s) },
                                    colors = ButtonDefaults.buttonColors(containerColor = Emerald800),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Icon(Icons.Default.Print, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("ትራንስክሪፕት", fontSize = 11.sp)
                                }
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                        }
                    }
                }
            }
        }
    }
}
