package com.aistudio.birbirbibleschool.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.filled.Church
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.aistudio.birbirbibleschool.data.InitialData
import com.aistudio.birbirbibleschool.model.Course
import com.aistudio.birbirbibleschool.model.GradeBreakdown
import com.aistudio.birbirbibleschool.model.OfficialSignaturesConfig
import com.aistudio.birbirbibleschool.model.Student
import com.aistudio.birbirbibleschool.ui.theme.Amber400
import com.aistudio.birbirbibleschool.ui.theme.Emerald800
import com.aistudio.birbirbibleschool.ui.theme.Emerald950
import com.aistudio.birbirbibleschool.ui.theme.Slate100
import com.aistudio.birbirbibleschool.ui.theme.Slate200
import com.aistudio.birbirbibleschool.ui.theme.Slate800
import com.aistudio.birbirbibleschool.ui.theme.Slate900
import com.aistudio.birbirbibleschool.util.GradeCalculations

@Composable
fun TranscriptDialog(
    student: Student,
    courses: List<Course>,
    grades: Map<String, GradeBreakdown>,
    currentYear: String,
    currentSemester: String,
    officialConfig: OfficialSignaturesConfig,
    onDismiss: () -> Unit
) {
    val studentCourses = courses.filter { it.program == student.program }
    val gpa = GradeCalculations.calculateStudentGPA(student, courses, grades, currentYear, currentSemester)

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .padding(vertical = 16.dp)
                .testTag("transcript_modal_card"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                // Header Toolbar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Emerald950)
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Print,
                            contentDescription = null,
                            tint = Amber400,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "የትራንስክሪፕት ቅድመ-እይታ (Official Transcript)",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "ዝጋ",
                            tint = Color.White
                        )
                    }
                }

                // Official Transcript Paper Body
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Church Crest & Header
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(Emerald800),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Church,
                            contentDescription = null,
                            tint = Amber400,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "የኢትዮጵያ ቃለ ሕይወት ቤተ ክርስቲያን",
                        color = Slate800,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "የብርብር ከተማ ቃለ ሕይወት ቤተ ክርስቲያን መጽሐፍ ቅዱስ ትምህርት ቤት",
                        color = Emerald950,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "Birbir Town Kale Heywet Church Bible School",
                        color = Color.Gray,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    Surface(
                        color = Emerald800,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "የተማሪ የትምህርት ማስረጃ / OFFICIAL STUDENT TRANSCRIPT",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Student Information Box
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Slate100),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "የተማሪው ስም: ${student.name}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = Slate900
                                )
                                Text(
                                    text = "መለያ ቁጥር: ${student.id}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = Emerald800
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "ፕሮግራም: ${if (student.program == com.aistudio.birbirbibleschool.model.ProgramType.Degree) "የመጀመሪያ ዲግሪ (Degree)" else "ዲፕሎማ (Diploma)"}",
                                    fontSize = 12.sp,
                                    color = Slate800
                                )
                                Text(
                                    text = "ፆታ: ${student.gender}",
                                    fontSize = 12.sp,
                                    color = Slate800
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "የትምህርት ዘመን: $currentYear ዓ.ም | ሴሚስተር: $currentSemester",
                                fontSize = 12.sp,
                                color = Slate800
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Grades Table
                    Text(
                        text = "የኮርስ ውጤቶች ዝርዝር (Enrolled Courses & Grades)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = Slate900,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    // Table Header
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Emerald800, RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "ተ.ቁ", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(32.dp))
                        Text(text = "የትምህርቱ ስም", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                        Text(text = "ክሬዲት", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(44.dp), textAlign = TextAlign.Center)
                        Text(text = "ውጤት", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(44.dp), textAlign = TextAlign.Center)
                        Text(text = "ደረጃ", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(44.dp), textAlign = TextAlign.Center)
                        Text(text = "ነጥብ", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(44.dp), textAlign = TextAlign.Center)
                    }

                    var totalCredits = 0
                    var totalQualityPoints = 0.0

                    studentCourses.forEachIndexed { index, course ->
                        val key = GradeCalculations.getGradeKey(student.uid, course.id, currentYear, currentSemester)
                        val breakdown = grades[key]
                        val eff = GradeCalculations.getEffectiveGradeValues(breakdown)

                        totalCredits += course.credits
                        if (eff.hasAny) {
                            totalQualityPoints += eff.points * course.credits
                        }

                        val rowBg = if (index % 2 == 0) Color.White else Slate100
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(rowBg)
                                .border(0.5.dp, Slate200)
                                .padding(horizontal = 8.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "${index + 1}", fontSize = 11.sp, modifier = Modifier.width(32.dp))
                            Text(text = course.name, fontSize = 11.sp, fontWeight = FontWeight.Medium, modifier = Modifier.weight(1f))
                            Text(text = "${course.credits}", fontSize = 11.sp, modifier = Modifier.width(44.dp), textAlign = TextAlign.Center)
                            Text(text = if (eff.hasAny) "${eff.total.toInt()}%" else "—", fontSize = 11.sp, modifier = Modifier.width(44.dp), textAlign = TextAlign.Center)
                            Text(
                                text = eff.letter,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (eff.letter == "F") Color.Red else Emerald800,
                                modifier = Modifier.width(44.dp),
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = if (eff.hasAny) String.format("%.2f", eff.points) else "—",
                                fontSize = 11.sp,
                                modifier = Modifier.width(44.dp),
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    // Total & GPA Summary Row
                    Surface(
                        color = Amber400.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(bottomStart = 8.dp, bottomEnd = 8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, Amber400, RoundedCornerShape(bottomStart = 8.dp, bottomEnd = 8.dp))
                                .padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "ጠቅላላ ክሬዲት: $totalCredits",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = Slate900
                            )
                            Text(
                                text = "የሴሚስተሩ የውጤት አማካይ (GPA): $gpa",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 14.sp,
                                color = Emerald800
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Official Stamp & Signatures Box
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        // Registrar Signature
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                modifier = Modifier
                                    .width(130.dp)
                                    .height(30.dp),
                                contentAlignment = Alignment.BottomCenter
                            ) {
                                Text(
                                    text = officialConfig.registrarName,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Emerald800
                                )
                            }
                            HorizontalDivider(modifier = Modifier.width(140.dp), thickness = 1.dp, color = Color.Gray)
                            Text(
                                text = "የሪጅስተራል ስምና ፊርማ",
                                fontSize = 10.sp,
                                color = Color.Gray
                            )
                        }

                        // Official Stamp
                        Box(
                            modifier = Modifier
                                .size(74.dp)
                                .border(2.dp, Amber400, CircleShape)
                                .padding(4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Default.Verified,
                                    contentDescription = null,
                                    tint = Emerald800,
                                    modifier = Modifier.size(24.dp)
                                )
                                Text(
                                    text = "ኦፊሴላዊ ማህተም\nOFFICIAL SEAL",
                                    fontSize = 7.sp,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center,
                                    color = Emerald800
                                )
                            }
                        }

                        // Dean Signature
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                modifier = Modifier
                                    .width(130.dp)
                                    .height(30.dp),
                                contentAlignment = Alignment.BottomCenter
                            ) {
                                Text(
                                    text = officialConfig.deanName,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Emerald800
                                )
                            }
                            HorizontalDivider(modifier = Modifier.width(140.dp), thickness = 1.dp, color = Color.Gray)
                            Text(
                                text = "የዋና ዲን ስምና ፊርማ",
                                fontSize = 10.sp,
                                color = Color.Gray
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Grading scale reference note
                    Text(
                        text = "የውጤት መስፈርት መለኪያ: A+ (97-100: 4.00) | A (93-96: 4.00) | B (83-86: 3.33) | C (73-76: 2.33) | D (63-66: 1.33) | F (0-59: 0.00)",
                        fontSize = 9.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                }

                // Modal Footer
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Slate100)
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = Emerald800),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("እሺ (Close)")
                    }
                }
            }
        }
    }
}
