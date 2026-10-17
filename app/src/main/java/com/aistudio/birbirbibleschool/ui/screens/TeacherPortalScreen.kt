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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Save
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aistudio.birbirbibleschool.model.Course
import com.aistudio.birbirbibleschool.model.CurrentUser
import com.aistudio.birbirbibleschool.model.GradeBreakdown
import com.aistudio.birbirbibleschool.model.NotificationType
import com.aistudio.birbirbibleschool.model.ProgramType
import com.aistudio.birbirbibleschool.model.Student
import com.aistudio.birbirbibleschool.ui.theme.Amber400
import com.aistudio.birbirbibleschool.ui.theme.Emerald800
import com.aistudio.birbirbibleschool.ui.theme.Emerald950
import com.aistudio.birbirbibleschool.ui.theme.Slate100
import com.aistudio.birbirbibleschool.ui.theme.Slate200
import com.aistudio.birbirbibleschool.ui.theme.Slate700
import com.aistudio.birbirbibleschool.ui.theme.Slate800
import com.aistudio.birbirbibleschool.ui.theme.Slate900
import com.aistudio.birbirbibleschool.util.GradeCalculations

@Composable
fun TeacherPortalScreen(
    currentUser: CurrentUser?,
    courses: List<Course>,
    students: List<Student>,
    grades: Map<String, GradeBreakdown>,
    currentYear: String,
    currentSemester: String,
    onSaveGrade: (String, String, GradeBreakdown) -> Unit,
    showNotify: (String, NotificationType) -> Unit,
    modifier: Modifier = Modifier
) {
    val teacherName = currentUser?.name ?: "መምህር"
    // Find courses assigned to this teacher
    val teacherCourses = courses.filter { it.teacher == teacherName || currentUser?.role == com.aistudio.birbirbibleschool.model.UserRole.ADMIN }
    val effectiveCourses = if (teacherCourses.isNotEmpty()) teacherCourses else courses

    var selectedCourseId by remember {
        mutableStateOf(effectiveCourses.firstOrNull()?.id ?: "")
    }

    val activeCourse = courses.firstOrNull { it.id == selectedCourseId }
        ?: effectiveCourses.firstOrNull()

    val enrolledStudents = if (activeCourse != null) {
        students.filter { it.program == activeCourse.program }
    } else emptyList()

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
                        imageVector = Icons.Default.EditNote,
                        contentDescription = null,
                        tint = Amber400,
                        modifier = Modifier.size(28.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "የመምህራን የውጤት መመዝገቢያ ፖርታል (Teacher Portal)",
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "እንኳን ደህና መጡ $teacherName! የትምህርት ክፍለ ጊዜ ውጤቶችን ይመዝግቡ",
                        color = Color.White.copy(alpha = 0.85f),
                        fontSize = 11.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Assigned Courses Selection
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(14.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "የተመደቡልዎት ኮርሶች (Assigned Courses):",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Slate900
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    effectiveCourses.forEach { c ->
                        val isSel = activeCourse?.id == c.id
                        FilterChip(
                            selected = isSel,
                            onClick = { selectedCourseId = c.id },
                            label = {
                                Text(
                                    text = "${c.name} (${if (c.program == ProgramType.Degree) "ዲግሪ" else "ዲፕሎማ"})",
                                    fontSize = 11.sp
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Emerald800,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }

                if (activeCourse != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        color = Slate100,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("መርሃ-ግብር: ${activeCourse.schedule}", fontSize = 11.sp, color = Slate800)
                            Text("ክሬዲት ሰዓት: ${activeCourse.credits}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Emerald800)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Student Grading Cards List
        Text(
            text = "የተማሪዎች ውጤት ማስገቢያ (${enrolledStudents.size} ተማሪዎች ተመዝግበዋል)",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = Slate900
        )
        Spacer(modifier = Modifier.height(8.dp))

        enrolledStudents.forEachIndexed { index, student ->
            if (activeCourse != null) {
                StudentGradingCard(
                    student = student,
                    index = index + 1,
                    course = activeCourse,
                    currentYear = currentYear,
                    currentSemester = currentSemester,
                    existingGrade = grades[GradeCalculations.getGradeKey(student.uid, activeCourse.id, currentYear, currentSemester)],
                    onSave = { breakdown ->
                        onSaveGrade(student.uid, activeCourse.id, breakdown)
                    }
                )
                Spacer(modifier = Modifier.height(10.dp))
            }
        }
    }
}

@Composable
fun StudentGradingCard(
    student: Student,
    index: Int,
    course: Course,
    currentYear: String,
    currentSemester: String,
    existingGrade: GradeBreakdown?,
    onSave: (GradeBreakdown) -> Unit
) {
    var p1 by remember(existingGrade) { mutableStateOf(existingGrade?.p1?.toInt()?.toString() ?: "") }
    var p2 by remember(existingGrade) { mutableStateOf(existingGrade?.p2?.toInt()?.toString() ?: "") }
    var mid by remember(existingGrade) { mutableStateOf(existingGrade?.mid?.toInt()?.toString() ?: "") }
    var finalVal by remember(existingGrade) { mutableStateOf(existingGrade?.final?.toInt()?.toString() ?: "") }
    var att by remember(existingGrade) { mutableStateOf(existingGrade?.att?.toInt()?.toString() ?: "") }

    val p1Num = (p1.toDoubleOrNull() ?: 0.0).coerceIn(0.0, 20.0)
    val p2Num = (p2.toDoubleOrNull() ?: 0.0).coerceIn(0.0, 20.0)
    val midNum = (mid.toDoubleOrNull() ?: 0.0).coerceIn(0.0, 30.0)
    val finalNum = (finalVal.toDoubleOrNull() ?: 0.0).coerceIn(0.0, 20.0)
    val attNum = (att.toDoubleOrNull() ?: 0.0).coerceIn(0.0, 10.0)

    val currentTotal = p1Num + p2Num + midNum + finalNum + attNum
    val gradeDetail = GradeCalculations.calculateGradeDetails(currentTotal)

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "$index.",
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray,
                        fontSize = 12.sp,
                        modifier = Modifier.width(24.dp)
                    )
                    Column {
                        Text(
                            text = student.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = Slate900
                        )
                        Text(
                            text = "ID: ${student.id}",
                            fontSize = 11.sp,
                            color = Emerald800
                        )
                    }
                }

                // Live Grade Badge
                Surface(
                    color = if (currentTotal > 0) Emerald800 else Slate200,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${currentTotal.toInt()}% (${gradeDetail.letter})",
                            color = if (currentTotal > 0) Color.White else Slate700,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Score Input Fields Row (Part 1, Part 2, Mid, Final, Att)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("ክፍል 1 (20)", fontSize = 10.sp, color = Slate700)
                    OutlinedTextField(
                        value = p1,
                        onValueChange = { p1 = it },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text("ክፍል 2 (20)", fontSize = 10.sp, color = Slate700)
                    OutlinedTextField(
                        value = p2,
                        onValueChange = { p2 = it },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text("ሚድ (30)", fontSize = 10.sp, color = Slate700)
                    OutlinedTextField(
                        value = mid,
                        onValueChange = { mid = it },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text("ፋይናል (20)", fontSize = 10.sp, color = Slate700)
                    OutlinedTextField(
                        value = finalVal,
                        onValueChange = { finalVal = it },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text("ተሳትፎ (10)", fontSize = 10.sp, color = Slate700)
                    OutlinedTextField(
                        value = att,
                        onValueChange = { att = it },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "ትርጉም: ${gradeDetail.desc} (ነጥብ: ${gradeDetail.points})",
                    fontSize = 11.sp,
                    color = Color.Gray
                )
                Button(
                    onClick = {
                        onSave(
                            GradeBreakdown(
                                p1 = p1Num,
                                p2 = p2Num,
                                mid = midNum,
                                `final` = finalNum,
                                att = attNum
                            )
                        )
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Emerald800),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("አስቀምጥ", fontSize = 11.sp)
                }
            }
        }
    }
}
