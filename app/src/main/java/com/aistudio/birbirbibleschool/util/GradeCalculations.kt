package com.aistudio.birbirbibleschool.util

import com.aistudio.birbirbibleschool.data.InitialData
import com.aistudio.birbirbibleschool.model.Course
import com.aistudio.birbirbibleschool.model.EffectiveGrade
import com.aistudio.birbirbibleschool.model.GradeBreakdown
import com.aistudio.birbirbibleschool.model.GradeDetail
import com.aistudio.birbirbibleschool.model.Student
import java.util.Locale
import kotlin.math.round

object GradeCalculations {

    fun calculateGradeDetails(score: Double): GradeDetail {
        val clamped = score.coerceIn(0.0, 100.0)
        val rounded = round(clamped * 10.0) / 10.0
        val scale = InitialData.OFFICIAL_GRADING_SCALE
        val grade = scale.firstOrNull { rounded >= it.min && rounded <= it.max }
            ?: scale.last()

        return GradeDetail(
            score = rounded,
            letter = grade.letter,
            points = grade.points,
            desc = grade.desc
        )
    }

    fun getEffectiveGradeValues(breakdown: GradeBreakdown?): EffectiveGrade {
        if (breakdown == null) {
            return EffectiveGrade(
                hasAny = false,
                total = 0.0,
                letter = "—",
                points = 0.0,
                desc = "አልተሞላም"
            )
        }

        val partsSum = breakdown.p1 + breakdown.p2 + breakdown.mid + breakdown.final + breakdown.att
        val total = breakdown.customTotal ?: partsSum
        val hasAny = total > 0.0 || breakdown.customLetter != null || breakdown.customDesc != null || breakdown.customTotal != null

        if (!hasAny) {
            return EffectiveGrade(
                hasAny = false,
                total = 0.0,
                letter = "—",
                points = 0.0,
                desc = "አልተሞላም"
            )
        }

        val standard = calculateGradeDetails(total)
        val letter = breakdown.customLetter?.ifBlank { null } ?: standard.letter
        var points = standard.points

        if (breakdown.customLetter != null) {
            val matched = InitialData.OFFICIAL_GRADING_SCALE.firstOrNull {
                it.letter.equals(breakdown.customLetter.trim(), ignoreCase = true)
            }
            if (matched != null) {
                points = matched.points
            }
        }

        val desc = breakdown.customDesc?.ifBlank { null } ?: standard.desc

        return EffectiveGrade(
            hasAny = true,
            total = total,
            letter = letter,
            points = points,
            desc = desc
        )
    }

    fun getGradeKey(studentUid: String, courseId: String, year: String, sem: String): String {
        return "${studentUid}_${courseId}_${year}_${sem}"
    }

    fun calculateStudentGPA(
        student: Student,
        allCourses: List<Course>,
        gradesRecord: Map<String, GradeBreakdown>,
        year: String,
        sem: String
    ): String {
        val studentCourses = allCourses.filter { it.program == student.program }
        var totalQualityPoints = 0.0
        var totalCredits = 0

        studentCourses.forEach { course ->
            val key = getGradeKey(student.uid, course.id, year, sem)
            val breakdown = gradesRecord[key]
            if (breakdown != null) {
                val eff = getEffectiveGradeValues(breakdown)
                if (eff.hasAny) {
                    totalQualityPoints += (eff.points * course.credits)
                    totalCredits += course.credits
                }
            }
        }

        if (totalCredits == 0) return "0.00"
        return String.format(Locale.US, "%.2f", totalQualityPoints / totalCredits)
    }

    fun generateInitialSampleGrades(
        students: List<Student>,
        courses: List<Course>
    ): Map<String, GradeBreakdown> {
        val sample = mutableMapOf<String, GradeBreakdown>()
        students.forEachIndexed { idx, s ->
            val progCourses = courses.filter { it.program == s.program }
            progCourses.forEach { c ->
                val key = getGradeKey(s.uid, c.id, "2019", "1")
                val baseScore = 70.0 + ((idx * 11 + c.name.length * 5) % 28)
                val p1 = (baseScore * 0.20).coerceAtMost(20.0)
                val p2 = (baseScore * 0.20).coerceAtMost(20.0)
                val mid = (baseScore * 0.30).coerceAtMost(30.0)
                val finalVal = (baseScore * 0.20).coerceAtMost(20.0)
                val att = (baseScore * 0.10).coerceAtMost(10.0)

                sample[key] = GradeBreakdown(
                    p1 = round(p1),
                    p2 = round(p2),
                    mid = round(mid),
                    `final` = round(finalVal),
                    att = round(att)
                )
            }
        }
        return sample
    }
}
