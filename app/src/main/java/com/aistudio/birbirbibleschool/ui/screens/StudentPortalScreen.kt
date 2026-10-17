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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aistudio.birbirbibleschool.model.Course
import com.aistudio.birbirbibleschool.model.CurrentUser
import com.aistudio.birbirbibleschool.model.GradeBreakdown
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
fun StudentPortalScreen(
    currentUser: CurrentUser?,
    students: List<Student>,
    courses: List<Course>,
    grades: Map<String, GradeBreakdown>,
    currentYear: String,
    currentSemester: String,
    gradePublished: Boolean,
    onOpenTranscript: (Student) -> Unit,
    modifier: Modifier = Modifier
) {
    // Find active student record
    val student = if (currentUser?.role == com.aistudio.birbirbibleschool.model.UserRole.STUDENT) {
        students.firstOrNull { it.uid == currentUser.uid }
            ?: students.firstOrNull { it.id == currentUser.id }
            ?: students.firstOrNull()
    } else {
        students.firstOrNull()
    }

    if (student == null) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("ምንም የተማሪ መረጃ አልተገኘም")
        }
        return
    }

    val studentCourses = courses.filter { it.program == student.program }
    val gpa = GradeCalculations.calculateStudentGPA(student, courses, grades, currentYear, currentSemester)

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
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Emerald800),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.School,
                        contentDescription = null,
                        tint = Amber400,
                        modifier = Modifier.size(30.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "የተማሪ የግል ገጽ (Student Portal)",
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "የውጤት መመልከቻ እና ይፋዊ ትራንስክሪፕት",
                        color = Color.White.copy(alpha = 0.85f),
                        fontSize = 11.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Student Profile & GPA Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = student.name,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = Slate900
                        )
                        Text(
                            text = "መለያ ቁጥር: ${student.id} | ፆታ: ${student.gender}",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                        Text(
                            text = "ፕሮግራም: ${if (student.program == ProgramType.Degree) "የመጀመሪያ ዲግሪ (Degree)" else "ዲፕሎማ (Diploma)"}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Emerald800
                        )
                    }

                    // GPA Box
                    Surface(
                        color = Amber400.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.border(1.dp, Amber400, RoundedCornerShape(12.dp))
                    ) {
                        Column(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("የሴሚስተር GPA", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Emerald950)
                            Text(
                                text = if (gradePublished) gpa else "🔒",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Emerald800
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = { onOpenTranscript(student) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("open_transcript_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = Emerald800),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.Print, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("ይፋዊ ትራንስክሪፕት ተመልከት (View Official Transcript)")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (!gradePublished) {
            // Grades Locked Notice
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF3C7)),
                shape = RoundedCornerShape(14.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = Color(0xFFD97706),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "ውጤቶች በግምገማ ላይ ናቸው",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = Color(0xFF92400E)
                        )
                        Text(
                            text = "የ$currentYear ዓ.ም ሴሚስተር $currentSemester ውጤቶች በዲንና ሬጅስተራል ጽሕፈት ቤት በይፋ እስኪለቀቁ ድረስ ለተማሪዎች አይታዩም።",
                            fontSize = 11.sp,
                            color = Color(0xFF78350F)
                        )
                    }
                }
            }
        } else {
            // Enrolled Courses & Grades Table
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "የ$currentYear ዓ.ም ሴሚስተር $currentSemester የውጤት ዝርዝር:",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Slate900
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    studentCourses.forEachIndexed { idx, course ->
                        val key = GradeCalculations.getGradeKey(student.uid, course.id, currentYear, currentSemester)
                        val breakdown = grades[key]
                        val eff = GradeCalculations.getEffectiveGradeValues(breakdown)

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .border(0.5.dp, Slate200, RoundedCornerShape(8.dp))
                                .background(if (idx % 2 == 0) Color.White else Slate100)
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "${idx + 1}. ${course.name}",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Slate900
                                )
                                Text(
                                    text = "ክሬዲት: ${course.credits} ሰዓት | መምህር: ${course.teacher}",
                                    fontSize = 11.sp,
                                    color = Color.Gray
                                )
                            }

                            Surface(
                                color = if (eff.hasAny) Emerald800 else Slate200,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = if (eff.hasAny) "${eff.total.toInt()}%" else "—",
                                        color = if (eff.hasAny) Color.White else Slate700,
                                        fontSize = 11.sp
                                    )
                                    Text(
                                        text = eff.letter,
                                        color = if (eff.hasAny) Amber400 else Slate700,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
