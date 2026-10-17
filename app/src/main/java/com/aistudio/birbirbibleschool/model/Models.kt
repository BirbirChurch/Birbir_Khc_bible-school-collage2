package com.aistudio.birbirbibleschool.model

enum class UserRole {
    ADMIN,
    DEAN,
    REGISTRAR,
    TEACHER,
    STUDENT
}

enum class ProgramType {
    Degree,
    Diploma
}

data class CurrentUser(
    val role: UserRole,
    val id: String,
    val uid: String? = null,
    val name: String,
    val program: ProgramType? = null
)

data class Student(
    val uid: String,
    val id: String,
    val name: String,
    val gender: String, // "ወንድ" | "ሴት"
    val program: ProgramType,
    val phone: String? = null,
    val photoUrl: String? = null
)

data class CourseAssessmentConfig(
    val p1Label: String = "ክፍል 1",
    val p1Max: Double = 20.0,
    val p2Label: String = "ክፍል 2",
    val p2Max: Double = 20.0,
    val midLabel: String = "ሚድ",
    val midMax: Double = 30.0,
    val finalLabel: String = "ፋይናል",
    val finalMax: Double = 20.0,
    val attLabel: String = "ተሳትፎ",
    val attMax: Double = 10.0
)

data class Course(
    val id: String,
    val program: ProgramType,
    val name: String,
    val teacher: String,
    val schedule: String,
    val credits: Int,
    val remarks: String = "",
    val assessmentConfig: CourseAssessmentConfig? = null
)

data class GradeBreakdown(
    val p1: Double = 0.0,
    val p2: Double = 0.0,
    val mid: Double = 0.0,
    val `final`: Double = 0.0,
    val att: Double = 0.0,
    val customTotal: Double? = null,
    val customLetter: String? = null,
    val customDesc: String? = null
)

data class GradeDetail(
    val score: Double,
    val letter: String,
    val points: Double,
    val desc: String
)

data class EffectiveGrade(
    val hasAny: Boolean,
    val total: Double,
    val letter: String,
    val points: Double,
    val desc: String
)

data class GradingScaleItem(
    val min: Double,
    val max: Double,
    val letter: String,
    val points: Double,
    val desc: String
)

data class OfficialSignaturesConfig(
    val showStamp: Boolean = false,
    val stampImage: String = "",
    val useChurchLogoAsStamp: Boolean = true,
    val showDeanSignature: Boolean = false,
    val deanSignatureImage: String = "",
    val deanName: String = "መ/ር ገላቲያ ድስታ",
    val deanTitle: String = "የዋና ዲን ስምና ፊርማ (Academic Dean Signature)",
    val showRegistrarSignature: Boolean = false,
    val registrarSignatureImage: String = "",
    val registrarName: String = "መ/ር አስራት አማኑኤል",
    val registrarTitle: String = "የሪጅስተራል ስምና ፊርማ (Registrar Signature)"
)

enum class NotificationType {
    SUCCESS,
    ERROR,
    WARNING,
    INFO
}

data class SystemNotification(
    val id: String,
    val text: String,
    val type: NotificationType
)
