package com.aistudio.birbirbibleschool.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.Award
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aistudio.birbirbibleschool.model.Course
import com.aistudio.birbirbibleschool.model.NotificationType
import com.aistudio.birbirbibleschool.model.OfficialSignaturesConfig
import com.aistudio.birbirbibleschool.model.ProgramType
import com.aistudio.birbirbibleschool.model.Student
import com.aistudio.birbirbibleschool.ui.theme.Amber400
import com.aistudio.birbirbibleschool.ui.theme.Emerald800
import com.aistudio.birbirbibleschool.ui.theme.Emerald950
import com.aistudio.birbirbibleschool.ui.theme.Slate100
import com.aistudio.birbirbibleschool.ui.theme.Slate200
import com.aistudio.birbirbibleschool.ui.theme.Slate700
import com.aistudio.birbirbibleschool.ui.theme.Slate900

@Composable
fun DeanPortalScreen(
    courses: List<Course>,
    students: List<Student>,
    currentYear: String,
    currentSemester: String,
    gradePublished: Boolean,
    officialConfig: OfficialSignaturesConfig,
    onSaveOfficialConfig: (OfficialSignaturesConfig) -> Unit,
    onSelectPrintTranscript: (Student) -> Unit,
    showNotify: (String, NotificationType) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedProgram by remember { mutableStateOf(ProgramType.Degree) }
    var deanName by remember { mutableStateOf(officialConfig.deanName) }
    var deanTitle by remember { mutableStateOf(officialConfig.deanTitle) }

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
                        imageVector = Icons.Default.School,
                        contentDescription = null,
                        tint = Amber400,
                        modifier = Modifier.size(28.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "የዋና ዲን ፖርታል (Academic Dean Portal)",
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "የስርዓተ-ትምህርት፣ የትምህርት መርሃ-ግብር እና የአካዳሚክ ቁጥጥር ማዕከል",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 11.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Grade status card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(14.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "የውጤት ይፋዊ ሁኔታ (Grade Status):",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Slate700
                    )
                    Text(
                        text = if (gradePublished) "✅ ውጤት ለተማሪዎች ይፋ ተደርጓል" else "🔒 ውጤት በዲን/አድሚን ቁጥጥር ስር ነው (ይፋ አልተደረገም)",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (gradePublished) Emerald800 else Color(0xFFD97706)
                    )
                }
                Surface(
                    color = if (gradePublished) Emerald800.copy(alpha = 0.15f) else Amber400.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "$currentYear ዓ.ም | ሴሚስተር $currentSemester",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (gradePublished) Emerald800 else Color(0xFFB45309),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Official Signature & Title Settings Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "የትራንስክሪፕት የዋና ዲን ስምና ፊርማ መረጃ (Dean Credentials)",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Slate900
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = deanName,
                    onValueChange = { deanName = it },
                    label = { Text("የዋና ዲን ሙሉ ስም") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = deanTitle,
                    onValueChange = { deanTitle = it },
                    label = { Text("የዋና ዲን ይፋዊ ማዕረግ (Title)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = {
                        onSaveOfficialConfig(officialConfig.copy(deanName = deanName, deanTitle = deanTitle))
                        showNotify("የዲን መረጃ ተቀምጧል!", NotificationType.SUCCESS)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Emerald800),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("መረጃውን አስቀምጥ")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Curriculum Course Schedule
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
                        text = "የትምህርት መርሃ-ግብር (Curriculum Schedule)",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Slate900
                    )

                    // Program filter
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf(
                            ProgramType.Degree to "ዲግሪ",
                            ProgramType.Diploma to "ዲፕሎማ"
                        ).forEach { (prog, label) ->
                            val isSel = selectedProgram == prog
                            FilterChip(
                                selected = isSel,
                                onClick = { selectedProgram = prog },
                                label = { Text(label, fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Emerald800,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                val programCourses = courses.filter { it.program == selectedProgram }

                programCourses.forEachIndexed { index, c ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        colors = CardDefaults.cardColors(containerColor = Slate100),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${index + 1}. ${c.name}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = Slate900
                                )
                                Surface(
                                    color = Emerald800,
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = "${c.credits} Cr. Hrs",
                                        color = Color.White,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "መምህር: ${c.teacher}",
                                fontSize = 12.sp,
                                color = Emerald950,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "ቀን/መርሃ-ግብር: ${c.schedule}",
                                fontSize = 11.sp,
                                color = Color.Gray
                            )
                        }
                    }
                }
            }
        }
    }
}
