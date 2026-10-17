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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.HowToReg
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
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
import com.aistudio.birbirbibleschool.model.NotificationType
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
import java.util.UUID

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RegistrarPortalScreen(
    students: List<Student>,
    currentYear: String,
    currentSemester: String,
    availableYears: List<String>,
    onSetCalendar: (String, String) -> Unit,
    onAddStudent: (Student) -> Unit,
    onDeleteStudent: (String) -> Unit,
    onSelectPrintTranscript: (Student) -> Unit,
    showNotify: (String, NotificationType) -> Unit,
    modifier: Modifier = Modifier
) {
    var programFilter by remember { mutableStateOf(ProgramType.Diploma) }
    var searchQuery by remember { mutableStateOf("") }

    // Add student form states
    var isAddingStudent by remember { mutableStateOf(false) }
    var newStudentId by remember { mutableStateOf("") }
    var newStudentName by remember { mutableStateOf("") }
    var newStudentGender by remember { mutableStateOf("ወንድ") }
    var newStudentProgram by remember { mutableStateOf(ProgramType.Diploma) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Slate100)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Banner
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
                        imageVector = Icons.Default.HowToReg,
                        contentDescription = null,
                        tint = Amber400,
                        modifier = Modifier.size(28.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "የሪጅስተራል ፖርታል (Registrar Portal)",
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "የተማሪዎች ምዝገባ፣ የትምህርት ዘመን ማኔጅመንት እና ይፋዊ ትራንስክሪፕት መስጫ",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 11.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Academic Calendar Selector Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(14.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = Emerald800, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "የትምህርት ዘመንና ሴሚስተር መቀየሪያ:",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Slate900
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("ዓመተ-ምህረት:", fontSize = 11.sp, color = Slate700)
                    availableYears.take(6).forEach { y ->
                        val isSel = currentYear == y
                        FilterChip(
                            selected = isSel,
                            onClick = { onSetCalendar(y, currentSemester) },
                            label = { Text(y, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Emerald800,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("ሴሚስተር:", fontSize = 11.sp, color = Slate700)
                    listOf("1" to "ሴሚስተር 1", "2" to "ሴሚስተር 2").forEach { (s, label) ->
                        val isSel = currentSemester == s
                        FilterChip(
                            selected = isSel,
                            onClick = { onSetCalendar(currentYear, s) },
                            label = { Text(label, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Amber400,
                                selectedLabelColor = Emerald950
                            )
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Add Student Form / Toggle Button
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(14.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "የተማሪዎች ምዝገባ (Student Admission)",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Slate900
                    )
                    Button(
                        onClick = { isAddingStudent = !isAddingStudent },
                        colors = ButtonDefaults.buttonColors(containerColor = if (isAddingStudent) Slate700 else Emerald800),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("toggle_add_student_button")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(if (isAddingStudent) "ቅጹን ዝጋ" else "አዲስ ተማሪ መዝግብ", fontSize = 11.sp)
                    }
                }

                if (isAddingStudent) {
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = newStudentId,
                        onValueChange = { newStudentId = it },
                        label = { Text("የተማሪ መለያ ቁጥር (Student ID e.g. 201936)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = newStudentName,
                        onValueChange = { newStudentName = it },
                        label = { Text("የተማሪው ሙሉ ስም (Student Full Name)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("ፆታ:", fontSize = 11.sp, color = Slate700)
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                listOf("ወንድ", "ሴት").forEach { g ->
                                    FilterChip(
                                        selected = newStudentGender == g,
                                        onClick = { newStudentGender = g },
                                        label = { Text(g, fontSize = 11.sp) },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = Emerald800,
                                            selectedLabelColor = Color.White
                                        )
                                    )
                                }
                            }
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text("ፕሮግራም:", fontSize = 11.sp, color = Slate700)
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                listOf(ProgramType.Diploma to "ዲፕሎማ", ProgramType.Degree to "ዲግሪ").forEach { (p, l) ->
                                    FilterChip(
                                        selected = newStudentProgram == p,
                                        onClick = { newStudentProgram = p },
                                        label = { Text(l, fontSize = 11.sp) },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = Amber400,
                                            selectedLabelColor = Emerald950
                                        )
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = {
                            if (newStudentId.isNotBlank() && newStudentName.isNotBlank()) {
                                val prefix = if (newStudentProgram == ProgramType.Degree) "deg" else "dip"
                                val uid = "${prefix}_${newStudentId.trim()}"
                                onAddStudent(
                                    Student(
                                        uid = uid,
                                        id = newStudentId.trim(),
                                        name = newStudentName.trim(),
                                        gender = newStudentGender,
                                        program = newStudentProgram
                                    )
                                )
                                newStudentId = ""
                                newStudentName = ""
                                isAddingStudent = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Emerald800),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("ተማሪውን መዝግብ")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Student Roster with Filter & Search
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "የተማሪዎች ዝርዝር (Student Roster)",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Slate900
                    )

                    // Program filter
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf(
                            ProgramType.Diploma to "ዲፕሎማ (${students.count { it.program == ProgramType.Diploma }})",
                            ProgramType.Degree to "ዲግሪ (${students.count { it.program == ProgramType.Degree }})"
                        ).forEach { (prog, label) ->
                            val isSel = programFilter == prog
                            FilterChip(
                                selected = isSel,
                                onClick = { programFilter = prog },
                                label = { Text(label, fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Emerald800,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = { Text("ስም ወይም መለያ ቁጥር ይፈልጉ...") },
                    trailingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                val programStudents = students.filter { it.program == programFilter }
                val filtered = programStudents.filter {
                    searchQuery.isBlank() ||
                    it.name.contains(searchQuery.trim(), ignoreCase = true) ||
                    it.id.contains(searchQuery.trim())
                }

                filtered.forEachIndexed { index, s ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 3.dp)
                            .border(0.5.dp, Slate200, RoundedCornerShape(8.dp))
                            .background(if (index % 2 == 0) Color.White else Slate100)
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Text(
                                text = "${index + 1}.",
                                fontSize = 11.sp,
                                color = Color.Gray,
                                modifier = Modifier.width(26.dp)
                            )
                            Column {
                                Text(
                                    text = s.name,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = Slate900
                                )
                                Text(
                                    text = "ID: ${s.id} | ፆታ: ${s.gender}",
                                    fontSize = 11.sp,
                                    color = Color.Gray
                                )
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Button(
                                onClick = { onSelectPrintTranscript(s) },
                                colors = ButtonDefaults.buttonColors(containerColor = Emerald800),
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier.padding(end = 4.dp)
                            ) {
                                Icon(Icons.Default.Print, contentDescription = null, modifier = Modifier.size(13.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("ትራንስክሪፕት", fontSize = 10.sp)
                            }

                            IconButton(
                                onClick = { onDeleteStudent(s.uid) },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = "ሰርዝ", tint = Red600, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}
