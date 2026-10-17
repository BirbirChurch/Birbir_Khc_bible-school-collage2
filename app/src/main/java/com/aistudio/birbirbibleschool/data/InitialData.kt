package com.aistudio.birbirbibleschool.data

import com.aistudio.birbirbibleschool.model.Course
import com.aistudio.birbirbibleschool.model.GradingScaleItem
import com.aistudio.birbirbibleschool.model.ProgramType
import com.aistudio.birbirbibleschool.model.Student

object InitialData {

    val OFFICIAL_GRADING_SCALE = listOf(
        GradingScaleItem(97.0, 100.0, "A+", 4.00, "እጅግ በጣም ጥሩ"),
        GradingScaleItem(93.0, 96.0,  "A",  4.00, "በጣም ጥሩ"),
        GradingScaleItem(90.0, 92.0,  "A-", 3.66, "በጣም ጥሩ"),
        GradingScaleItem(87.0, 89.0,  "B+", 3.66, "ጥሩ"),
        GradingScaleItem(83.0, 86.0,  "B",  3.33, "ጥሩ"),
        GradingScaleItem(80.0, 82.0,  "B-", 3.00, "ደህና"),
        GradingScaleItem(77.0, 79.0,  "C+", 2.66, "አጥጋቢ"),
        GradingScaleItem(73.0, 76.0,  "C",  2.33, "አልፏል"),
        GradingScaleItem(70.0, 72.0,  "C-", 2.00, "ዝቅተኛ ማለፊያ"),
        GradingScaleItem(67.0, 69.0,  "D+", 1.66, "ዝቅተኛ"),
        GradingScaleItem(63.0, 66.0,  "D",  1.33, "ዝቅተኛ / ማለፊያ"),
        GradingScaleItem(60.0, 62.0,  "D-", 1.00, "ዝቅተኛ"),
        GradingScaleItem(0.0,  59.0,  "F",  0.00, "ወድቋል")
    )

    val PRELOADED_TEACHERS = listOf(
        "መ/ር ሰሎሞን ሶርሳ",
        "ፓ/ር መስፍን ሞላ",
        "መ/ር ሕዝቅኤል ባካሎ",
        "መ/ር አሸናፊ ሆጋጎ",
        "መ/ር ታዴዎስ ሚልክያስ",
        "መ/ር አስራት አማኑኤል",
        "መ/ር አየለ አንጁሎ",
        "መ/ር ገላቲያ ድስታ",
        "ፓ/ር ጉጃ ኡባ",
        "አልተመደበም (—)"
    )

    val DEFAULT_TEACHER_KEYS = mapOf(
        "መ/ር ሰሎሞን ሶርሳ" to "BK-SOL-2019",
        "ፓ/ር መስፍን ሞላ" to "BK-MES-2019",
        "መ/ር ሕዝቅኤል ባካሎ" to "BK-HIZ-2019",
        "መ/ር አሸናፊ ሆጋጎ" to "BK-ASH-2019",
        "መ/ር ታዴዎስ ሚልክያስ" to "BK-TAD-2019",
        "መ/ር አስራት አማኑኤል" to "BK-ASR-2019",
        "መ/ር አየለ አንጁሎ" to "BK-AYE-2019",
        "መ/ር ገላቲያ ድስታ" to "BK-GEL-2019",
        "ፓ/ር ጉጃ ኡባ" to "BK-GUJ-2019"
    )

    val DEFAULT_ACADEMIC_YEARS = listOf(
        "2019", "2020", "2021", "2022", "2023", "2024",
        "2025", "2026", "2027", "2028", "2029", "2030"
    )

    val PRELOADED_STUDENTS_DIPLOMA = listOf(
        Student("dip_201901", "201901", "ሊዲያ ጳዉሎስ", "ሴት", ProgramType.Diploma),
        Student("dip_201902", "201902", "መሐመድ ቶማ", "ወንድ", ProgramType.Diploma),
        Student("dip_201903", "201903", "መስከረም ዳዊት", "ሴት", ProgramType.Diploma),
        Student("dip_201904", "201904", "መብራት ደምሴ", "ሴት", ProgramType.Diploma),
        Student("dip_201811", "201811", "ሙሉቀን ዘለቀ ፋልታሞ", "ወንድ", ProgramType.Diploma),
        Student("dip_201806", "201806", "ማርቆስ ቱማ", "ወንድ", ProgramType.Diploma),
        Student("dip_201905", "201905", "ማቴዎስ መስቀሌ", "ወንድ", ProgramType.Diploma),
        Student("dip_201906", "201906", "ማገኝ ማጮ", "ወንድ", ProgramType.Diploma),
        Student("dip_201907", "201907", "ምስራቅ ኩኬ", "ሴት", ProgramType.Diploma),
        Student("dip_201908", "201908", "ሠራዊት ሳሙኤል", "ሴት", ProgramType.Diploma),
        Student("dip_201909", "201909", "ስንቅነሽ ሴታ", "ሴት", ProgramType.Diploma),
        Student("dip_201803", "201803", "ስንታየሁ ቱማቶ", "ወንድ", ProgramType.Diploma),
        Student("dip_201910", "201910", "ቤተል መሸሻ", "ሴት", ProgramType.Diploma),
        Student("dip_201911", "201911", "ቤተል ሳሙኤል", "ሴት", ProgramType.Diploma),
        Student("dip_201912", "201912", "ቤዛነህ ማቴዎስ", "ወንድ", ProgramType.Diploma),
        Student("dip_201913", "201913", "ብንያም ሕዝቅኤል", "ወንድ", ProgramType.Diploma),
        Student("dip_201914", "201914", "ተረፈች ተስፋዬ", "ሴት", ProgramType.Diploma),
        Student("dip_201915", "201915", "ተመስገን መርድነህ", "ወንድ", ProgramType.Diploma),
        Student("dip_201916", "201916", "ቸርነት ገዛኸኝ", "ወንድ", ProgramType.Diploma),
        Student("dip_201917", "201917", "አልማዝ አበራ", "ሴት", ProgramType.Diploma),
        Student("dip_201918", "201918", "አመነች ነዉሌ", "ሴት", ProgramType.Diploma),
        Student("dip_201802", "201802", "አማኑኤል ባሳ", "ወንድ", ProgramType.Diploma),
        Student("dip_201807", "201807", "አስራት ኤልያስ", "ወንድ", ProgramType.Diploma),
        Student("dip_201801", "201801", "አስራት ዳንኤል", "ወንድ", ProgramType.Diploma),
        Student("dip_201919", "201919", "አበራ አለቆ", "ወንድ", ProgramType.Diploma),
        Student("dip_201920", "201920", "አበባየሁ አየለ", "ሴት", ProgramType.Diploma),
        Student("dip_201921", "201921", "አብራሃም ዱቃ", "ወንድ", ProgramType.Diploma),
        Student("dip_201805", "201805", "ከፍያለዉ ካስትሮ", "ወንድ", ProgramType.Diploma),
        Student("dip_201922", "201922", "ዘላለም ማዴቦ", "ሴት", ProgramType.Diploma),
        Student("dip_201923", "201923", "ደነቀ አለማየሁ", "ወንድ", ProgramType.Diploma),
        Student("dip_201808", "201808", "ጠቅላዩ ፎላ", "ወንድ", ProgramType.Diploma),
        Student("dip_201804", "201804", "ጥሩነሽ መሐመድ", "ሴት", ProgramType.Diploma),
        Student("dip_201924", "201924", "አማኑኤል አዴማ", "ወንድ", ProgramType.Diploma),
        Student("dip_201925", "201925", "ከፍያለዉ ከማል", "ወንድ", ProgramType.Diploma),
        Student("dip_201926", "201926", "አበራሽ አዳዬ", "ሴት", ProgramType.Diploma)
    )

    val PRELOADED_STUDENTS_DEGREE = listOf(
        Student("deg_201901", "201901", "ሕዝቅኤል ፁንቃ", "ወንድ", ProgramType.Degree),
        Student("deg_201902", "201902", "መሐመድ ሀርቶ", "ወንድ", ProgramType.Degree),
        Student("deg_201903", "201903", "መሰረት ማቴዎስ", "ሴት", ProgramType.Degree),
        Student("deg_201904", "201904", "መንግሥቱ ፎላ", "ወንድ", ProgramType.Degree),
        Student("deg_201905", "201905", "ማቱሳላ ጴጥሮስ", "ወንድ", ProgramType.Degree),
        Student("deg_201906", "201906", "ማቴዎስ መስቀሌ", "ወንድ", ProgramType.Degree),
        Student("deg_201907", "201907", "ማቴዎስ ጁታ", "ወንድ", ProgramType.Degree),
        Student("deg_201908", "201908", "ሚኖታ ደባልቄ", "ወንድ", ProgramType.Degree),
        Student("deg_201909", "201909", "ምህረት ሚልክያስ", "ሴት", ProgramType.Degree),
        Student("deg_201910", "201910", "ሳሙኤል ኬቶ", "ወንድ", ProgramType.Degree),
        Student("deg_201911", "201911", "ሳምሶን አብራሃም", "ወንድ", ProgramType.Degree),
        Student("deg_201912", "201912", "ሸሪፎ ሻራ", "ወንድ", ProgramType.Degree),
        Student("deg_201913", "201913", "በረከት ፎላ", "ወንድ", ProgramType.Degree),
        Student("deg_201914", "201914", "በርገነ ባፋ", "ወንድ", ProgramType.Degree),
        Student("deg_201915", "201915", "ተመስገን መንግሥቱ", "ወንድ", ProgramType.Degree),
        Student("deg_201916", "201916", "ታምራት በቀሌ", "ወንድ", ProgramType.Degree),
        Student("deg_201917", "201917", "ኃይልዬ አድሱ", "ወንድ", ProgramType.Degree),
        Student("deg_201918", "201918", "አለሙ ሌራ", "ወንድ", ProgramType.Degree),
        Student("deg_201919", "201919", "አለሙ አራ", "ሴት", ProgramType.Degree),
        Student("deg_201920", "201920", "አልማዝ አስራት", "ሴት", ProgramType.Degree),
        Student("deg_201921", "201921", "አማኑኤል ደቤና", "ሴት", ProgramType.Degree),
        Student("deg_201922", "201922", "አሰገደች አየለ", "ሴት", ProgramType.Degree),
        Student("deg_201923", "201923", "አሸብር ከበደ", "ወንድ", ProgramType.Degree),
        Student("deg_201924", "201924", "አበበች ለራንሶ", "ሴት", ProgramType.Degree),
        Student("deg_201925", "201925", "አይናለም ማንጁራ", "ሴት", ProgramType.Degree),
        Student("deg_201926", "201926", "አንዱዓለም ሉቃስ", "ወንድ", ProgramType.Degree),
        Student("deg_201927", "201927", "አንድነት ለማ", "ወንድ", ProgramType.Degree),
        Student("deg_201928", "201928", "አኬና አዴቶ", "ወንድ", ProgramType.Degree),
        Student("deg_201929", "201929", "ኢተነሽ በሩ", "ሴት", ProgramType.Degree),
        Student("deg_201930", "201930", "ደስታ ጉጋላ", "ወንድ", ProgramType.Degree),
        Student("deg_201931", "201931", "ጩቃሎ ጩቱሎ", "ወንድ", ProgramType.Degree),
        Student("deg_201932", "201932", "ጴጥሮስ ገዘኽኝ", "ወንድ", ProgramType.Degree),
        Student("deg_201933", "201933", "ፈቃደ ብርሃኑ", "ወንድ", ProgramType.Degree),
        Student("deg_201934", "201934", "አቤል ጴጥሮስ", "ወንድ", ProgramType.Degree),
        Student("deg_201935", "201935", "ትክክሏ ሴባ", "ሴት", ProgramType.Degree)
    )

    val PRELOADED_STUDENTS = PRELOADED_STUDENTS_DIPLOMA + PRELOADED_STUDENTS_DEGREE

    val PRELOADED_COURSES = listOf(
        // Degree Program (8 courses)
        Course("deg_c1", ProgramType.Degree, "ሥነ-አፈታት", "መ/ር ሰሎሞን ሶርሳ", "መስከረም 11 – 25/2019", 3),
        Course("deg_c2", ProgramType.Degree, "እግዚአብሔርና መጽሐፍ ቅዱስ", "ፓ/ር መስፍን ሞላ", "መስከረም 26 – ጥቅምት 9/2019", 3),
        Course("deg_c3", ProgramType.Degree, "አጠናን ዘዴ", "መ/ር ሕዝቅኤል ባካሎ", "ጥቅምት 10 – 23/2019", 2),
        Course("deg_c4", ProgramType.Degree, "ብሉይ ኪዳን 1", "መ/ር አሸናፊ ሆጋጎ", "ጥቅምት 24 – ህዳር 7/2019", 3),
        Course("deg_c5", ProgramType.Degree, "አዲስ ኪዳን 1", "መ/ር ታዴዎስ ሚልክያስ", "ህዳር 8 – 21/2019", 3),
        Course("deg_c6", ProgramType.Degree, "አማርኛ ሰዋሰው", "አልተመደበም (—)", "ህዳር 22 – ታህሳስ 5/2019", 2),
        Course("deg_c7", ProgramType.Degree, "ፍልስፍናና ሥነ-መለኮት", "መ/ር ሰሎሞን ሶርሳ", "ታህሳስ 6 – 19/2019", 3),
        Course("deg_c8", ProgramType.Degree, "የአዲስ ኪዳን ሥነ-መለኮት", "መ/ር ታዴዎስ ሚልክያስ", "ጥር 3 – 17/2019", 3),

        // Diploma Program (8 courses)
        Course("dip_c1", ProgramType.Diploma, "እግዚአብሔርና መጽሐፍ ቅዱስ", "መ/ር አስራት አማኑኤል", "መስከረም 11 – 25/2019", 3),
        Course("dip_c2", ProgramType.Diploma, "አጠናን ዘዴ", "መ/ር ሕዝቅኤል ባካሎ", "መስከረም 26 – ጥቅምት 9/2019", 2),
        Course("dip_c3", ProgramType.Diploma, "ሥነ-አፈታት", "መ/ር አየለ አንጁሎ", "ጥቅምት 10 – 23/2019", 3),
        Course("dip_c4", ProgramType.Diploma, "ሥነ-ምግባር", "መ/ር ገላቲያ ድስታ", "ጥቅምት 24 – ህዳር 7/2019", 2),
        Course("dip_c5", ProgramType.Diploma, "ብሉይ ኪዳን 1", "ፓ/ር መስፍን ሞላ", "ህዳር 8 – 21/2019", 3),
        Course("dip_c6", ProgramType.Diploma, "አዲስ ኪዳን 1", "መ/ር ታዴዎስ ሚልክያስ", "ህዳር 22 – ታህሳስ 5/2019", 3),
        Course("dip_c7", ProgramType.Diploma, "መንፈስ ቅዱስና መናፍስት", "ፓ/ር ጉጃ ኡባ", "ታህሳስ 6 – 19/2019", 2),
        Course("dip_c8", ProgramType.Diploma, "ክርስቶስና ደህንነት", "መ/ር ታዴዎስ ሚልክያስ", "ጥር 3 – 17/2019", 3)
    )

    fun generateInitialUserPasswords(): MutableMap<String, String> {
        val passwords = mutableMapOf(
            "admin" to "admin123",
            "dean" to "dean123",
            "registrar" to "reg123"
        )
        // Teachers
        PRELOADED_TEACHERS.forEachIndexed { index, teacher ->
            if (!teacher.contains("—")) {
                passwords[teacher] = DEFAULT_TEACHER_KEYS[teacher] ?: "BK-TCH-${1000 + index}"
            }
        }
        // Students: default password is their student ID
        PRELOADED_STUDENTS.forEach { s ->
            passwords[s.uid] = s.id
        }
        return passwords
    }
}
