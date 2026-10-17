package com.aistudio.birbirbibleschool

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aistudio.birbirbibleschool.model.UserRole
import com.aistudio.birbirbibleschool.ui.components.AppHeader
import com.aistudio.birbirbibleschool.ui.components.NotificationContainer
import com.aistudio.birbirbibleschool.ui.components.TranscriptDialog
import com.aistudio.birbirbibleschool.ui.screens.AdminPortalScreen
import com.aistudio.birbirbibleschool.ui.screens.DeanPortalScreen
import com.aistudio.birbirbibleschool.ui.screens.LoginScreen
import com.aistudio.birbirbibleschool.ui.screens.RegistrarPortalScreen
import com.aistudio.birbirbibleschool.ui.screens.StudentPortalScreen
import com.aistudio.birbirbibleschool.ui.screens.TeacherPortalScreen
import com.aistudio.birbirbibleschool.ui.theme.BirbirBibleSchoolTheme
import com.aistudio.birbirbibleschool.viewmodel.BibleSchoolViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BirbirBibleSchoolTheme {
                BibleSchoolApp()
            }
        }
    }
}

@Composable
fun BibleSchoolApp(viewModel: BibleSchoolViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            AppHeader(
                currentUser = uiState.currentUser,
                systemLocked = uiState.systemLocked,
                currentYear = uiState.currentYear,
                currentSemester = uiState.currentSemester,
                adminActiveView = uiState.adminActiveView,
                onAdminChangeView = { viewModel.setAdminActiveView(it) },
                onLogout = { viewModel.logout() }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            val user = uiState.currentUser
            if (user == null) {
                LoginScreen(
                    students = uiState.students,
                    teachers = uiState.teachers,
                    userPasswords = uiState.userPasswords,
                    systemLocked = uiState.systemLocked,
                    onLogin = { viewModel.login(it) },
                    showNotify = { text, type -> viewModel.showNotification(text, type) }
                )
            } else {
                val effectiveRole = if (user.role == UserRole.ADMIN) {
                    uiState.adminActiveView
                } else {
                    user.role
                }

                when (effectiveRole) {
                    UserRole.ADMIN -> {
                        AdminPortalScreen(
                            students = uiState.students,
                            courses = uiState.courses,
                            teachers = uiState.teachers,
                            grades = uiState.grades,
                            userPasswords = uiState.userPasswords,
                            systemLocked = uiState.systemLocked,
                            gradePublished = uiState.gradePublished,
                            currentYear = uiState.currentYear,
                            currentSemester = uiState.currentSemester,
                            officialConfig = uiState.officialConfig,
                            onToggleLock = { viewModel.toggleSystemLock() },
                            onToggleGradePublication = { viewModel.toggleGradePublished() },
                            onAddTeacher = { name, key -> viewModel.addTeacher(name, key) },
                            onDeleteTeacher = { viewModel.deleteTeacher(it) },
                            onToggleTeacherLock = { viewModel.toggleTeacherLock(it) },
                            onUpdatePassword = { key, pass -> viewModel.updatePassword(key, pass) },
                            onSaveOfficialConfig = { viewModel.saveOfficialConfig(it) },
                            onResetDefaults = { viewModel.resetToDefaults() },
                            onClearAllGrades = { viewModel.clearAllGrades() },
                            onSelectPrintTranscript = { viewModel.showTranscript(it) },
                            showNotify = { text, type -> viewModel.showNotification(text, type) }
                        )
                    }

                    UserRole.DEAN -> {
                        DeanPortalScreen(
                            courses = uiState.courses,
                            students = uiState.students,
                            currentYear = uiState.currentYear,
                            currentSemester = uiState.currentSemester,
                            gradePublished = uiState.gradePublished,
                            officialConfig = uiState.officialConfig,
                            onSaveOfficialConfig = { viewModel.saveOfficialConfig(it) },
                            onSelectPrintTranscript = { viewModel.showTranscript(it) },
                            showNotify = { text, type -> viewModel.showNotification(text, type) }
                        )
                    }

                    UserRole.REGISTRAR -> {
                        RegistrarPortalScreen(
                            students = uiState.students,
                            currentYear = uiState.currentYear,
                            currentSemester = uiState.currentSemester,
                            availableYears = uiState.availableYears,
                            onSetCalendar = { year, sem -> viewModel.setCalendar(year, sem) },
                            onAddStudent = { viewModel.addStudent(it) },
                            onDeleteStudent = { viewModel.deleteStudent(it) },
                            onSelectPrintTranscript = { viewModel.showTranscript(it) },
                            showNotify = { text, type -> viewModel.showNotification(text, type) }
                        )
                    }

                    UserRole.TEACHER -> {
                        TeacherPortalScreen(
                            currentUser = user,
                            courses = uiState.courses,
                            students = uiState.students,
                            grades = uiState.grades,
                            currentYear = uiState.currentYear,
                            currentSemester = uiState.currentSemester,
                            onSaveGrade = { uid, cid, breakdown -> viewModel.saveGrade(uid, cid, breakdown) },
                            showNotify = { text, type -> viewModel.showNotification(text, type) }
                        )
                    }

                    UserRole.STUDENT -> {
                        StudentPortalScreen(
                            currentUser = user,
                            students = uiState.students,
                            courses = uiState.courses,
                            grades = uiState.grades,
                            currentYear = uiState.currentYear,
                            currentSemester = uiState.currentSemester,
                            gradePublished = uiState.gradePublished,
                            onOpenTranscript = { viewModel.showTranscript(it) }
                        )
                    }
                }
            }

            // Notification Toasts Overlay
            NotificationContainer(
                notifications = uiState.notifications,
                onDismiss = { viewModel.dismissNotification(it) }
            )

            // Transcript Dialog Modal
            uiState.activeTranscriptStudent?.let { transcriptStudent ->
                TranscriptDialog(
                    student = transcriptStudent,
                    courses = uiState.courses,
                    grades = uiState.grades,
                    currentYear = uiState.currentYear,
                    currentSemester = uiState.currentSemester,
                    officialConfig = uiState.officialConfig,
                    onDismiss = { viewModel.dismissTranscript() }
                )
            }
        }
    }
}
