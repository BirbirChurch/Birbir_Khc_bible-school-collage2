package com.aistudio.birbirbibleschool.viewmodel

import androidx.lifecycle.ViewModel
import com.aistudio.birbirbibleschool.data.InitialData
import com.aistudio.birbirbibleschool.model.Course
import com.aistudio.birbirbibleschool.model.CurrentUser
import com.aistudio.birbirbibleschool.model.GradeBreakdown
import com.aistudio.birbirbibleschool.model.NotificationType
import com.aistudio.birbirbibleschool.model.OfficialSignaturesConfig
import com.aistudio.birbirbibleschool.model.ProgramType
import com.aistudio.birbirbibleschool.model.Student
import com.aistudio.birbirbibleschool.model.SystemNotification
import com.aistudio.birbirbibleschool.model.UserRole
import com.aistudio.birbirbibleschool.util.GradeCalculations
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

data class BibleSchoolUiState(
    val currentUser: CurrentUser? = null,
    val adminActiveView: UserRole = UserRole.ADMIN,
    val systemLocked: Boolean = false,
    val gradePublished: Boolean = true,
    val currentYear: String = "2019",
    val currentSemester: String = "1",
    val availableYears: List<String> = InitialData.DEFAULT_ACADEMIC_YEARS,
    val students: List<Student> = InitialData.PRELOADED_STUDENTS,
    val courses: List<Course> = InitialData.PRELOADED_COURSES,
    val teachers: List<String> = InitialData.PRELOADED_TEACHERS,
    val grades: Map<String, GradeBreakdown> = emptyMap(),
    val userPasswords: Map<String, String> = InitialData.generateInitialUserPasswords(),
    val officialConfig: OfficialSignaturesConfig = OfficialSignaturesConfig(),
    val notifications: List<SystemNotification> = emptyList(),
    val activeTranscriptStudent: Student? = null
)

class BibleSchoolViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(BibleSchoolUiState())
    val uiState: StateFlow<BibleSchoolUiState> = _uiState.asStateFlow()

    init {
        // Provide initial sample grades for testing so users immediately see transcript and GPA calculations
        val sample = GradeCalculations.generateInitialSampleGrades(
            InitialData.PRELOADED_STUDENTS,
            InitialData.PRELOADED_COURSES
        )
        _uiState.value = _uiState.value.copy(grades = sample)
    }

    fun showNotification(text: String, type: NotificationType = NotificationType.INFO) {
        val newNotif = SystemNotification(
            id = UUID.randomUUID().toString(),
            text = text,
            type = type
        )
        _uiState.value = _uiState.value.copy(
            notifications = _uiState.value.notifications + newNotif
        )
    }

    fun dismissNotification(id: String) {
        _uiState.value = _uiState.value.copy(
            notifications = _uiState.value.notifications.filter { it.id != id }
        )
    }

    fun login(user: CurrentUser) {
        _uiState.value = _uiState.value.copy(
            currentUser = user,
            adminActiveView = user.role
        )
        showNotification("እንኳን ደህና መጡ ${user.name}!", NotificationType.SUCCESS)
    }

    fun logout() {
        _uiState.value = _uiState.value.copy(
            currentUser = null,
            adminActiveView = UserRole.ADMIN,
            activeTranscriptStudent = null
        )
        showNotification("በስኬት ወጥተዋል", NotificationType.INFO)
    }

    fun setAdminActiveView(role: UserRole) {
        _uiState.value = _uiState.value.copy(adminActiveView = role)
    }

    fun toggleSystemLock() {
        val newLocked = !_uiState.value.systemLocked
        _uiState.value = _uiState.value.copy(systemLocked = newLocked)
        val msg = if (newLocked) "ሲስተሙ በጥገና ሁነታ ተቆልፏል!" else "ሲስተሙ ክፍት ተደርጓል!"
        showNotification(msg, if (newLocked) NotificationType.WARNING else NotificationType.SUCCESS)
    }

    fun toggleGradePublished() {
        val newPub = !_uiState.value.gradePublished
        _uiState.value = _uiState.value.copy(gradePublished = newPub)
        val msg = if (newPub) "ውጤት ለተማሪዎች ይፋ ተደርጓል!" else "ውጤት ለተማሪዎች እንዳይታይ ተዘግቷል!"
        showNotification(msg, NotificationType.SUCCESS)
    }

    fun setCalendar(year: String, sem: String) {
        _uiState.value = _uiState.value.copy(currentYear = year, currentSemester = sem)
        showNotification("የትምህርት ዘመን ወደ $year ዓ.ም ሴሚስተር $sem ተቀይሯል", NotificationType.INFO)
    }

    fun updatePassword(targetKey: String, newPass: String) {
        val updated = _uiState.value.userPasswords.toMutableMap()
        updated[targetKey] = newPass
        _uiState.value = _uiState.value.copy(userPasswords = updated)
        showNotification("የይለፍ ቃል በስኬት ተቀይሯል!", NotificationType.SUCCESS)
    }

    fun addTeacher(name: String, customKey: String? = null) {
        val trimmed = name.trim()
        if (trimmed.isEmpty()) return
        if (_uiState.value.teachers.contains(trimmed)) {
            showNotification("ይህ መምህር ቀደም ሲል ተመዝግቧል!", NotificationType.WARNING)
            return
        }
        val updatedTeachers = _uiState.value.teachers.toMutableList()
        // Insert before unassigned placeholder if present
        val unassignedIdx = updatedTeachers.indexOfFirst { it.contains("—") }
        if (unassignedIdx >= 0) {
            updatedTeachers.add(unassignedIdx, trimmed)
        } else {
            updatedTeachers.add(trimmed)
        }

        val key = customKey?.ifBlank { null } ?: "BK-TCH-${(1000..9999).random()}"
        val updatedPass = _uiState.value.userPasswords.toMutableMap()
        updatedPass[trimmed] = key

        _uiState.value = _uiState.value.copy(
            teachers = updatedTeachers,
            userPasswords = updatedPass
        )
        showNotification("መምህር $trimmed በስኬት ተመዝግቧል! ቁልፍ: $key", NotificationType.SUCCESS)
    }

    fun deleteTeacher(name: String) {
        val updatedTeachers = _uiState.value.teachers.filter { it != name }
        val updatedPass = _uiState.value.userPasswords.filterKeys { it != name }
        _uiState.value = _uiState.value.copy(
            teachers = updatedTeachers,
            userPasswords = updatedPass
        )
        showNotification("መምህር $name ተሰርዟል", NotificationType.INFO)
    }

    fun toggleTeacherLock(name: String) {
        val lockKey = "_locked_$name"
        val isLocked = _uiState.value.userPasswords[lockKey] == "true"
        val updatedPass = _uiState.value.userPasswords.toMutableMap()
        if (isLocked) {
            updatedPass.remove(lockKey)
            showNotification("የመምህር $name መለያ ተከፍቷል", NotificationType.SUCCESS)
        } else {
            updatedPass[lockKey] = "true"
            showNotification("የመምህር $name መለያ ታግዷል", NotificationType.WARNING)
        }
        _uiState.value = _uiState.value.copy(userPasswords = updatedPass)
    }

    fun addStudent(student: Student) {
        if (_uiState.value.students.any { it.id == student.id && it.program == student.program }) {
            showNotification("ይህ የተማሪ መለያ ቁጥር (${student.id}) ቀደም ሲል አለ!", NotificationType.ERROR)
            return
        }
        val updated = _uiState.value.students + student
        val updatedPass = _uiState.value.userPasswords.toMutableMap()
        updatedPass[student.uid] = student.id

        _uiState.value = _uiState.value.copy(
            students = updated,
            userPasswords = updatedPass
        )
        showNotification("ተማሪ ${student.name} በስኬት ተመዝግቧል!", NotificationType.SUCCESS)
    }

    fun updateStudent(updatedStudent: Student) {
        val updated = _uiState.value.students.map {
            if (it.uid == updatedStudent.uid) updatedStudent else it
        }
        _uiState.value = _uiState.value.copy(students = updated)
        showNotification("የተማሪ መረጃ ተሻሽሏል", NotificationType.SUCCESS)
    }

    fun deleteStudent(uid: String) {
        val student = _uiState.value.students.firstOrNull { it.uid == uid }
        val updated = _uiState.value.students.filter { it.uid != uid }
        _uiState.value = _uiState.value.copy(students = updated)
        showNotification("ተማሪ ${student?.name ?: ""} ተሰርዟል", NotificationType.INFO)
    }

    fun addCourse(course: Course) {
        val updated = _uiState.value.courses + course
        _uiState.value = _uiState.value.copy(courses = updated)
        showNotification("ትምህርት ${course.name} ተመዝግቧል", NotificationType.SUCCESS)
    }

    fun updateCourse(updatedCourse: Course) {
        val updated = _uiState.value.courses.map {
            if (it.id == updatedCourse.id) updatedCourse else it
        }
        _uiState.value = _uiState.value.copy(courses = updated)
        showNotification("የትምህርት መረጃ ተሻሽሏል", NotificationType.SUCCESS)
    }

    fun deleteCourse(id: String) {
        val updated = _uiState.value.courses.filter { it.id != id }
        _uiState.value = _uiState.value.copy(courses = updated)
        showNotification("ትምህርቱ ተሰርዟል", NotificationType.INFO)
    }

    fun saveGrade(studentUid: String, courseId: String, breakdown: GradeBreakdown) {
        val key = GradeCalculations.getGradeKey(
            studentUid,
            courseId,
            _uiState.value.currentYear,
            _uiState.value.currentSemester
        )
        val updatedGrades = _uiState.value.grades.toMutableMap()
        updatedGrades[key] = breakdown
        _uiState.value = _uiState.value.copy(grades = updatedGrades)
        showNotification("ውጤት በስኬት ተቀምጧል!", NotificationType.SUCCESS)
    }

    fun saveOfficialConfig(config: OfficialSignaturesConfig) {
        _uiState.value = _uiState.value.copy(officialConfig = config)
        showNotification("የዲንና ሪጅስተራል ፊርማ መረጃ ተቀምጧል!", NotificationType.SUCCESS)
    }

    fun clearAllGrades() {
        _uiState.value = _uiState.value.copy(grades = emptyMap())
        showNotification("ሁሉም ውጤቶች ፀድተዋል!", NotificationType.WARNING)
    }

    fun resetToDefaults() {
        val sample = GradeCalculations.generateInitialSampleGrades(
            InitialData.PRELOADED_STUDENTS,
            InitialData.PRELOADED_COURSES
        )
        _uiState.value = BibleSchoolUiState(grades = sample)
        showNotification("ሁሉም መረጃዎች ወደ መጀመሪያው ሁኔታ ተመልሰዋል!", NotificationType.INFO)
    }

    fun showTranscript(student: Student) {
        _uiState.value = _uiState.value.copy(activeTranscriptStudent = student)
    }

    fun dismissTranscript() {
        _uiState.value = _uiState.value.copy(activeTranscriptStudent = null)
    }
}
