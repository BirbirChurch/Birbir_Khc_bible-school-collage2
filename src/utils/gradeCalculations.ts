import { OFFICIAL_GRADING_SCALE } from '../data/initialData.ts';
import { Course, CourseAssessmentConfig, GradeBreakdown, GradeDetail, Student } from '../types.ts';

export const DEFAULT_ASSESSMENT_CONFIG: CourseAssessmentConfig = {
  p1Label: 'ክፍል 1',
  p1Max: 20,
  p2Label: 'ክፍል 2',
  p2Max: 20,
  midLabel: 'ሚድ',
  midMax: 30,
  finalLabel: 'ፋይናል',
  finalMax: 20,
  attLabel: 'ተሳትፎ',
  attMax: 10
};

export function getCourseAssessmentConfig(course?: Course): CourseAssessmentConfig {
  if (course?.assessmentConfig) {
    return {
      p1Label: course.assessmentConfig.p1Label || DEFAULT_ASSESSMENT_CONFIG.p1Label,
      p1Max: Number(course.assessmentConfig.p1Max) || DEFAULT_ASSESSMENT_CONFIG.p1Max,
      p2Label: course.assessmentConfig.p2Label || DEFAULT_ASSESSMENT_CONFIG.p2Label,
      p2Max: Number(course.assessmentConfig.p2Max) || DEFAULT_ASSESSMENT_CONFIG.p2Max,
      midLabel: course.assessmentConfig.midLabel || DEFAULT_ASSESSMENT_CONFIG.midLabel,
      midMax: Number(course.assessmentConfig.midMax) || DEFAULT_ASSESSMENT_CONFIG.midMax,
      finalLabel: course.assessmentConfig.finalLabel || DEFAULT_ASSESSMENT_CONFIG.finalLabel,
      finalMax: Number(course.assessmentConfig.finalMax) || DEFAULT_ASSESSMENT_CONFIG.finalMax,
      attLabel: course.assessmentConfig.attLabel || DEFAULT_ASSESSMENT_CONFIG.attLabel,
      attMax: Number(course.assessmentConfig.attMax) || DEFAULT_ASSESSMENT_CONFIG.attMax
    };
  }
  return DEFAULT_ASSESSMENT_CONFIG;
}

export function calculateGradeDetails(score: number): GradeDetail {
  const num = Math.min(100, Math.max(0, Math.round((Number(score) || 0) * 10) / 10));
  const grade = OFFICIAL_GRADING_SCALE.find(g => num >= g.min && num <= g.max) 
    || OFFICIAL_GRADING_SCALE[OFFICIAL_GRADING_SCALE.length - 1];
  
  return {
    score: num,
    letter: grade.letter,
    points: grade.points,
    desc: grade.desc
  };
}

export function getEffectiveGradeValues(breakdown?: GradeBreakdown): {
  hasAny: boolean;
  total: number;
  letter: string;
  points: number;
  desc: string;
} {
  if (!breakdown) {
    return { hasAny: false, total: 0, letter: '—', points: 0, desc: 'አልተሞላም' };
  }

  const partsSum = (breakdown.p1 || 0) + (breakdown.p2 || 0) + (breakdown.mid || 0) + (breakdown.final || 0) + (breakdown.att || 0);
  const hasCustomTotal = typeof breakdown.customTotal === 'number' && !isNaN(breakdown.customTotal);
  const total = hasCustomTotal ? breakdown.customTotal! : partsSum;
  
  const hasAny = total > 0 || Boolean(breakdown.customLetter) || Boolean(breakdown.customDesc) || hasCustomTotal;

  if (!hasAny) {
    return { hasAny: false, total: 0, letter: '—', points: 0, desc: 'አልተሞላም' };
  }

  const standard = calculateGradeDetails(total);

  let letter = breakdown.customLetter || standard.letter;
  let points = standard.points;
  if (breakdown.customLetter) {
    const matched = OFFICIAL_GRADING_SCALE.find(g => g.letter.toLowerCase() === breakdown.customLetter?.toLowerCase());
    if (matched) {
      points = matched.points;
    }
  }

  const desc = breakdown.customDesc || standard.desc;

  return {
    hasAny: true,
    total,
    letter,
    points,
    desc
  };
}

export function getGradeKey(studentUid: string, courseId: string, year: string, sem: string): string {
  return `${studentUid}_${courseId}_${year}_${sem}`;
}

export function calculateStudentGPA(
  student: Student,
  allCourses: Course[],
  gradesRecord: Record<string, GradeBreakdown>,
  year: string,
  sem: string
): string {
  const studentCourses = allCourses.filter(c => c.program === student.program);
  let totalQualityPoints = 0;
  let totalCredits = 0;

  studentCourses.forEach(course => {
    const key = getGradeKey(student.uid, course.id, year, sem);
    const breakdown = gradesRecord[key];
    if (breakdown) {
      const eff = getEffectiveGradeValues(breakdown);
      if (eff.hasAny) {
        totalQualityPoints += (eff.points * course.credits);
        totalCredits += course.credits;
      }
    }
  });

  if (totalCredits === 0) return '0.00';
  return (totalQualityPoints / totalCredits).toFixed(2);
}

export function generateInitialSampleGrades(
  students: Student[],
  courses: Course[]
): Record<string, GradeBreakdown> {
  const sample: Record<string, GradeBreakdown> = {};

  students.forEach((s, idx) => {
    const progCourses = courses.filter(c => c.program === s.program);
    progCourses.forEach(c => {
      const key = getGradeKey(s.uid, c.id, '2019', '1');
      // Produce realistic distribution (70 - 98)
      const baseScore = 70 + ((idx * 11 + c.name.length * 5) % 28);
      
      const p1 = Math.min(20, Math.round(baseScore * 0.20));
      const p2 = Math.min(20, Math.round(baseScore * 0.20));
      const mid = Math.min(30, Math.round(baseScore * 0.30));
      const final = Math.min(20, Math.round(baseScore * 0.20));
      const att = Math.min(10, Math.round(baseScore * 0.10));

      sample[key] = { p1, p2, mid, final, att };
    });
  });

  return sample;
}
