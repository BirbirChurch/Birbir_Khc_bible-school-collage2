import React, { useState, useEffect } from 'react';
import {
  GraduationCap,
  Save,
  BookOpen,
  AlertCircle,
  Calendar,
  CheckCircle2,
  SlidersHorizontal,
  RotateCcw,
  X,
  Check,
  Edit3
} from 'lucide-react';
import { Course, CourseAssessmentConfig, GradeBreakdown, Student } from '../types.ts';
import {
  calculateGradeDetails,
  DEFAULT_ASSESSMENT_CONFIG,
  getCourseAssessmentConfig,
  getEffectiveGradeValues,
  getGradeKey
} from '../utils/gradeCalculations.ts';
import { OFFICIAL_GRADING_SCALE } from '../data/initialData.ts';

interface TeacherPortalProps {
  teacherName: string;
  courses: Course[];
  students: Student[];
  grades: Record<string, GradeBreakdown>;
  currentYear: string;
  currentSemester: string;
  onSaveGrades: (updatedGrades: Record<string, GradeBreakdown>) => void;
  showNotify: (msg: string, type?: 'success' | 'error' | 'warning' | 'info') => void;
  onUpdateCourse?: (course: Course) => void;
}

export const TeacherPortal: React.FC<TeacherPortalProps> = ({
  teacherName,
  courses,
  students,
  grades,
  currentYear,
  currentSemester,
  onSaveGrades,
  showNotify,
  onUpdateCourse
}) => {
  const teacherCourses = courses.filter((c) => c.teacher === teacherName);
  const [selectedCourseId, setSelectedCourseId] = useState<string>(teacherCourses[0]?.id || '');
  const [localGrades, setLocalGrades] = useState<Record<string, GradeBreakdown>>({});

  // Assessment Scheme Modal State
  const [isSchemeModalOpen, setIsSchemeModalOpen] = useState(false);
  const [editingScheme, setEditingScheme] = useState<CourseAssessmentConfig>(DEFAULT_ASSESSMENT_CONFIG);

  useEffect(() => {
    if (teacherCourses.length > 0 && (!selectedCourseId || !teacherCourses.some((c) => c.id === selectedCourseId))) {
      setSelectedCourseId(teacherCourses[0].id);
    }
  }, [teacherCourses, selectedCourseId]);

  const activeCourse = courses.find((c) => c.id === selectedCourseId);
  const assessmentConfig = getCourseAssessmentConfig(activeCourse);

  // Sync scheme edit form when active course changes
  useEffect(() => {
    if (activeCourse) {
      setEditingScheme(getCourseAssessmentConfig(activeCourse));
    }
  }, [activeCourse?.id, activeCourse?.assessmentConfig]);

  // Sync initial local grades for this course
  useEffect(() => {
    const course = courses.find((c) => c.id === selectedCourseId);
    if (!course) return;

    const enrolled = students.filter((s) => s.program === course.program);
    const initial: Record<string, GradeBreakdown> = {};

    enrolled.forEach((s) => {
      const key = getGradeKey(s.uid, course.id, currentYear, currentSemester);
      const existing = grades[key] || { p1: 0, p2: 0, mid: 0, final: 0, att: 0 };
      initial[s.uid] = { ...existing };
    });

    setLocalGrades(initial);
  }, [selectedCourseId, courses, students, grades, currentYear, currentSemester]);

  const enrolledStudents = activeCourse ? students.filter((s) => s.program === activeCourse.program) : [];

  // Handle changing numeric criterion (p1, p2, mid, final, att)
  const handleInputChange = (
    studentUid: string,
    field: 'p1' | 'p2' | 'mid' | 'final' | 'att',
    val: string,
    maxVal: number
  ) => {
    const clean = val.trim();
    const parsed = clean === '' ? 0 : Math.min(maxVal, Math.max(0, parseFloat(clean) || 0));
    setLocalGrades((prev) => {
      const current = prev[studentUid] || { p1: 0, p2: 0, mid: 0, final: 0, att: 0 };
      const updatedItem = {
        ...current,
        [field]: parsed
      };

      // If user hadn't explicitly typed a custom total, auto-update standard details
      if (updatedItem.customTotal === undefined) {
        const sum = (updatedItem.p1 || 0) + (updatedItem.p2 || 0) + (updatedItem.mid || 0) + (updatedItem.final || 0) + (updatedItem.att || 0);
        if (sum > 0 && !updatedItem.customLetter) {
          const gd = calculateGradeDetails(sum);
          if (!updatedItem.customDesc) {
            // let desc follow default
          }
        }
      }

      return {
        ...prev,
        [studentUid]: updatedItem
      };
    });
  };

  // Handle direct edit of Total (ድምር 100%)
  const handleCustomTotalChange = (studentUid: string, val: string) => {
    const clean = val.trim();
    setLocalGrades((prev) => {
      const current = prev[studentUid] || { p1: 0, p2: 0, mid: 0, final: 0, att: 0 };
      if (clean === '') {
        // Remove customTotal override so it uses sum of components
        const copy = { ...current };
        delete copy.customTotal;
        return { ...prev, [studentUid]: copy };
      }

      const num = Math.min(100, Math.max(0, parseFloat(clean) || 0));
      return {
        ...prev,
        [studentUid]: {
          ...current,
          customTotal: num
        }
      };
    });
  };

  // Handle direct edit/override of Letter Grade (ፊደል ደረጃ)
  const handleCustomLetterChange = (studentUid: string, letter: string) => {
    setLocalGrades((prev) => {
      const current = prev[studentUid] || { p1: 0, p2: 0, mid: 0, final: 0, att: 0 };
      if (!letter) {
        // Auto mode
        const copy = { ...current };
        delete copy.customLetter;
        return { ...prev, [studentUid]: copy };
      }

      const matched = OFFICIAL_GRADING_SCALE.find((g) => g.letter === letter);
      return {
        ...prev,
        [studentUid]: {
          ...current,
          customLetter: letter,
          // If customDesc is not manually set, optionally default to scale desc
          customDesc: current.customDesc ?? matched?.desc
        }
      };
    });
  };

  // Handle direct edit of Description / Meaning (ትርጉም)
  const handleCustomDescChange = (studentUid: string, desc: string) => {
    setLocalGrades((prev) => {
      const current = prev[studentUid] || { p1: 0, p2: 0, mid: 0, final: 0, att: 0 };
      return {
        ...prev,
        [studentUid]: {
          ...current,
          customDesc: desc
        }
      };
    });
  };

  // Save customized assessment criteria & weights
  const handleSaveScheme = (e: React.FormEvent) => {
    e.preventDefault();
    if (!activeCourse) return;

    const totalWeight =
      Number(editingScheme.p1Max || 0) +
      Number(editingScheme.p2Max || 0) +
      Number(editingScheme.midMax || 0) +
      Number(editingScheme.finalMax || 0) +
      Number(editingScheme.attMax || 0);

    const updatedCourse: Course = {
      ...activeCourse,
      assessmentConfig: {
        p1Label: editingScheme.p1Label.trim() || 'ክፍል 1',
        p1Max: Number(editingScheme.p1Max) || 20,
        p2Label: editingScheme.p2Label.trim() || 'ክፍል 2',
        p2Max: Number(editingScheme.p2Max) || 20,
        midLabel: editingScheme.midLabel.trim() || 'ሚድ',
        midMax: Number(editingScheme.midMax) || 30,
        finalLabel: editingScheme.finalLabel.trim() || 'ፋይናል',
        finalMax: Number(editingScheme.finalMax) || 20,
        attLabel: editingScheme.attLabel.trim() || 'ተሳትፎ',
        attMax: Number(editingScheme.attMax) || 10
      }
    };

    if (onUpdateCourse) {
      onUpdateCourse(updatedCourse);
    }

    setIsSchemeModalOpen(false);
    showNotify(
      `የኮርሱ የውጤት መመዘኛዎችና መቶኛዎች በስኬት ተስተካክለዋል! (ጠቅላላ መቶኛ፡ ${totalWeight}%)`,
      'success'
    );
  };

  const handleResetSchemeToDefault = () => {
    setEditingScheme(DEFAULT_ASSESSMENT_CONFIG);
  };

  // Clear all course grades to blank
  const handleClearCourse = () => {
    if (!window.confirm('እርግጠኛ ነዎት? የዚህ ኮርስ ውጤቶች በሙሉ ባዶ (0) ይሆናሉ?')) return;
    const cleared: Record<string, GradeBreakdown> = {};
    enrolledStudents.forEach((s) => {
      cleared[s.uid] = { p1: 0, p2: 0, mid: 0, final: 0, att: 0 };
    });
    setLocalGrades(cleared);

    if (activeCourse) {
      const updated = { ...grades };
      enrolledStudents.forEach((s) => {
        const key = getGradeKey(s.uid, activeCourse.id, currentYear, currentSemester);
        updated[key] = { p1: 0, p2: 0, mid: 0, final: 0, att: 0 };
      });
      onSaveGrades(updated);
    }
    showNotify('የኮርሱ ውጤቶች በሙሉ ባዶ ሆነዋል!', 'info');
  };

  // Save all grades
  const handleSaveAll = () => {
    if (!activeCourse) return;

    const updated = { ...grades };
    enrolledStudents.forEach((s) => {
      const key = getGradeKey(s.uid, activeCourse.id, currentYear, currentSemester);
      updated[key] = localGrades[s.uid] || { p1: 0, p2: 0, mid: 0, final: 0, att: 0 };
    });

    onSaveGrades(updated);
    showNotify('የሁሉም ተማሪዎች ውጤቶች በስኬት ተመዝግበዋል!', 'success');
  };

  // Live class statistics
  let totalScoreSum = 0;
  let gradedCount = 0;
  const gradeDistribution: Record<string, number> = { 'A+': 0, A: 0, B: 0, C: 0, D: 0, F: 0 };

  enrolledStudents.forEach((s) => {
    const g = localGrades[s.uid];
    const eff = getEffectiveGradeValues(g);
    if (eff.hasAny) {
      totalScoreSum += eff.total;
      gradedCount++;
      if (gradeDistribution[eff.letter] !== undefined) {
        gradeDistribution[eff.letter]++;
      }
    }
  });

  const classAverage = gradedCount > 0 ? (totalScoreSum / gradedCount).toFixed(1) : '0.0';

  const totalSchemeWeight =
    editingScheme.p1Max + editingScheme.p2Max + editingScheme.midMax + editingScheme.finalMax + editingScheme.attMax;

  return (
    <div className="space-y-6 no-print">
      {/* Top Banner */}
      <div className="bg-white p-6 rounded-3xl shadow-sm border border-slate-200/80 flex flex-col md:flex-row justify-between items-start md:items-center gap-4">
        <div>
          <h2 className="text-xl sm:text-2xl font-bold text-slate-800 flex items-center gap-2">
            <GraduationCap className="w-7 h-7 text-emerald-800" />
            <span>የመምህራን ውጤት መሙያ ገጽ</span>
          </h2>
          <p className="text-xs sm:text-sm text-slate-500 mt-1">
            የትምህርት ዘመን {currentYear} ዓ.ም ({currentSemester}ኛ ሴሚስተር)
          </p>
        </div>

        {/* Course selection dropdown */}
        <div className="flex items-center gap-2 bg-emerald-50 border border-emerald-300 rounded-2xl px-3.5 py-2">
          <BookOpen className="w-4 h-4 text-emerald-800" />
          <label className="text-xs font-bold text-emerald-900">የሚያስተምሩት ኮርስ:</label>
          <select
            value={selectedCourseId}
            onChange={(e) => setSelectedCourseId(e.target.value)}
            className="text-xs font-bold bg-transparent text-emerald-950 focus:outline-none cursor-pointer"
          >
            {teacherCourses.length === 0 ? (
              <option value="">የተመደበ ኮርስ የለም</option>
            ) : (
              teacherCourses.map((c) => (
                <option key={c.id} value={c.id}>
                  [{c.program}] {c.name}
                </option>
              ))
            )}
          </select>
        </div>
      </div>

      {teacherCourses.length === 0 ? (
        <div className="bg-white rounded-3xl p-12 text-center border border-slate-200/80">
          <AlertCircle className="w-12 h-12 text-amber-500 mx-auto mb-3" />
          <h3 className="text-base font-bold text-slate-800">ምንም የተመደበ ኮርስ አልተገኘም</h3>
          <p className="text-xs text-slate-500 mt-1">ዋና ዲን ኮርስ ሲመድቡሎት በዚህ ገጽ ላይ ተዘርዝረው ይታያሉ።</p>
        </div>
      ) : (
        <>
          {/* Active Course Schedule Card & Grade Legend Banner */}
          <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
            <div className="md:col-span-1 bg-gradient-to-br from-emerald-900 to-slate-900 text-white rounded-3xl p-5 shadow-sm space-y-2">
              <span className="text-[10px] font-bold uppercase tracking-wider text-amber-300">
                የተመደበ ኮርስና የቀን ሰሌዳ
              </span>
              <h4 className="text-lg font-extrabold text-white">{activeCourse?.name}</h4>
              <p className="text-xs text-emerald-200 flex items-center gap-1.5 font-semibold">
                <Calendar className="w-4 h-4 text-amber-400" />
                <span>ቀን: {activeCourse?.schedule || 'አልተወሰነም'}</span>
              </p>
              <div className="pt-2 flex justify-between text-xs text-slate-300 border-t border-emerald-800">
                <span>
                  ፕሮግራም: <strong>{activeCourse?.program}</strong>
                </span>
                <span>
                  ተማሪዎች: <strong>{enrolledStudents.length}</strong>
                </span>
              </div>
            </div>

            {/* Official Grading Scale Reference Card */}
            <div className="md:col-span-2 bg-amber-50/70 border border-amber-300 rounded-3xl p-5 space-y-2">
              <p className="text-xs font-bold text-amber-950 flex items-center gap-1.5">
                <CheckCircle2 className="w-4 h-4 text-amber-700" />
                <span>ይፋዊ የውጤት መስፈርት (Grading Scale):</span>
              </p>
              <div className="grid grid-cols-2 sm:grid-cols-3 gap-2 text-[11px] text-slate-800">
                <div className="bg-white p-1.5 rounded-lg border border-amber-200">
                  <strong className="text-emerald-700 font-bold">90% – 100%</strong> ➔ A+ (እጅግ በጣም ጥሩ)
                </div>
                <div className="bg-white p-1.5 rounded-lg border border-amber-200">
                  <strong className="text-emerald-700 font-bold">80% – 89%</strong> ➔ A (በጣም ጥሩ)
                </div>
                <div className="bg-white p-1.5 rounded-lg border border-amber-200">
                  <strong className="text-indigo-700 font-bold">70% – 79%</strong> ➔ B (ጥሩ)
                </div>
                <div className="bg-white p-1.5 rounded-lg border border-amber-200">
                  <strong className="text-amber-700 font-bold">60% – 69%</strong> ➔ C (አልፏል)
                </div>
                <div className="bg-white p-1.5 rounded-lg border border-amber-200">
                  <strong className="text-orange-700 font-bold">50% – 59%</strong> ➔ D (ዝቅተኛ / ማለፊያ)
                </div>
                <div className="bg-white p-1.5 rounded-lg border border-amber-200">
                  <strong className="text-rose-700 font-bold">ከ 50% በታች</strong> ➔ F (ወድቋል)
                </div>
              </div>
            </div>
          </div>

          {/* Grade Entry Table with Fully Editable Criteria, Total, Letter & Meaning */}
          <div className="bg-white rounded-3xl shadow-sm border border-slate-200/80 p-6 space-y-4">
            <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-3">
              <div>
                <h3 className="text-base font-bold text-slate-800 flex items-center gap-2">
                  <span>የተማሪዎች ውጤት ማስገቢያ ሰንጠረዥ: {activeCourse?.name} ({activeCourse?.program})</span>
                  <button
                    onClick={() => setIsSchemeModalOpen(true)}
                    className="inline-flex items-center gap-1 text-[11px] text-emerald-800 hover:text-emerald-950 font-bold bg-emerald-50 hover:bg-emerald-100 border border-emerald-300 px-2.5 py-1 rounded-xl transition-all cursor-pointer shadow-2xs"
                    title="የመመዘኛ ስሞችንና መቶኛዎችን ኤዲት ለማድረግ ይጫኑ"
                  >
                    <SlidersHorizontal className="w-3.5 h-3.5" />
                    <span>የመመዘኛ ስሞችና % ኤዲት</span>
                  </button>
                </h3>
                <p className="text-xs text-slate-500 mt-0.5">
                  {assessmentConfig.p1Label} ({assessmentConfig.p1Max}%) + {assessmentConfig.p2Label} ({assessmentConfig.p2Max}%) +{' '}
                  {assessmentConfig.midLabel} ({assessmentConfig.midMax}%) + {assessmentConfig.finalLabel} ({assessmentConfig.finalMax}%) +{' '}
                  {assessmentConfig.attLabel} ({assessmentConfig.attMax}%) = ድምር (100%)
                </p>
              </div>

              <div className="flex items-center gap-2">
                <button
                  type="button"
                  onClick={() => setIsSchemeModalOpen(true)}
                  className="bg-amber-50 hover:bg-amber-100 text-amber-900 border border-amber-300 text-xs px-3.5 py-2 rounded-xl font-bold transition-all flex items-center gap-1.5 cursor-pointer shadow-xs"
                >
                  <Edit3 className="w-3.5 h-3.5 text-amber-700" />
                  <span>መስፈርቶችና መቶኛ ኤዲት</span>
                </button>

                <button
                  onClick={handleSaveAll}
                  className="bg-emerald-700 hover:bg-emerald-800 text-white text-xs px-4 py-2.5 rounded-xl font-bold shadow-md transition-all flex items-center gap-2 cursor-pointer"
                >
                  <Save className="w-4 h-4" />
                  <span>ውጤቶችን መዝግብ (Save All)</span>
                </button>
              </div>
            </div>

            <div className="overflow-x-auto">
              <table className="w-full text-left text-xs text-slate-600">
                <thead className="bg-slate-50 text-slate-700 font-bold uppercase text-[10px] rounded-lg">
                  <tr>
                    <th className="p-2.5 border-b border-slate-200">ተ.ቁ</th>
                    <th className="p-2.5 border-b border-slate-200">ID No</th>
                    <th className="p-2.5 border-b border-slate-200">የተማሪ ስም</th>
                    {/* Criterion 1 */}
                    <th className="p-2.5 border-b border-slate-200 text-center w-20">
                      <div className="flex flex-col items-center">
                        <span>{assessmentConfig.p1Label}</span>
                        <span className="text-[9px] text-emerald-800 font-mono">({assessmentConfig.p1Max}%)</span>
                      </div>
                    </th>
                    {/* Criterion 2 */}
                    <th className="p-2.5 border-b border-slate-200 text-center w-20">
                      <div className="flex flex-col items-center">
                        <span>{assessmentConfig.p2Label}</span>
                        <span className="text-[9px] text-emerald-800 font-mono">({assessmentConfig.p2Max}%)</span>
                      </div>
                    </th>
                    {/* Criterion 3 */}
                    <th className="p-2.5 border-b border-slate-200 text-center w-20">
                      <div className="flex flex-col items-center">
                        <span>{assessmentConfig.midLabel}</span>
                        <span className="text-[9px] text-emerald-800 font-mono">({assessmentConfig.midMax}%)</span>
                      </div>
                    </th>
                    {/* Criterion 4 */}
                    <th className="p-2.5 border-b border-slate-200 text-center w-20">
                      <div className="flex flex-col items-center">
                        <span>{assessmentConfig.finalLabel}</span>
                        <span className="text-[9px] text-emerald-800 font-mono">({assessmentConfig.finalMax}%)</span>
                      </div>
                    </th>
                    {/* Criterion 5 */}
                    <th className="p-2.5 border-b border-slate-200 text-center w-20">
                      <div className="flex flex-col items-center">
                        <span>{assessmentConfig.attLabel}</span>
                        <span className="text-[9px] text-emerald-800 font-mono">({assessmentConfig.attMax}%)</span>
                      </div>
                    </th>
                    {/* Total (100%) - Editable */}
                    <th className="p-2.5 border-b border-slate-200 text-center w-22">
                      <div className="flex flex-col items-center text-emerald-950">
                        <span>ድምር</span>
                        <span className="text-[9px] text-emerald-800 font-mono">(100%) ✎</span>
                      </div>
                    </th>
                    {/* Letter Grade - Editable */}
                    <th className="p-2.5 border-b border-slate-200 text-center w-24">
                      <div className="flex flex-col items-center text-amber-950">
                        <span>ፊደል ደረጃ</span>
                        <span className="text-[9px] text-amber-800">ምረጡ ✎</span>
                      </div>
                    </th>
                    {/* Meaning / Remark - Editable */}
                    <th className="p-2.5 border-b border-slate-200 text-center min-w-[130px]">
                      <div className="flex flex-col items-center text-slate-800">
                        <span>ትርጉም / አስተያየት</span>
                        <span className="text-[9px] text-slate-500">ኤዲት ✎</span>
                      </div>
                    </th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-slate-100">
                  {enrolledStudents.map((s, idx) => {
                    const g = localGrades[s.uid] || { p1: 0, p2: 0, mid: 0, final: 0, att: 0 };
                    const partsSum =
                      (g.p1 || 0) + (g.p2 || 0) + (g.mid || 0) + (g.final || 0) + (g.att || 0);
                    const hasParts = partsSum > 0;
                    const hasCustomTotal = typeof g.customTotal === 'number' && !isNaN(g.customTotal);
                    const eff = getEffectiveGradeValues(g);

                    // Standard auto values
                    const effectiveTotal = hasCustomTotal ? g.customTotal! : partsSum;
                    const autoGrade = calculateGradeDetails(effectiveTotal);
                    const defaultDesc = eff.hasAny ? autoGrade.desc : 'አልተሞላም';

                    return (
                      <tr key={s.uid} className="hover:bg-slate-50/80 transition-colors">
                        <td className="p-2.5 font-mono text-slate-400">{idx + 1}</td>
                        <td className="p-2.5 font-mono font-bold text-slate-800">{s.id}</td>
                        <td className="p-2.5 font-bold text-slate-900">{s.name}</td>

                        {/* 1. Criterion 1 */}
                        <td className="p-2 text-center">
                          <input
                            type="number"
                            min="0"
                            max={assessmentConfig.p1Max}
                            step="0.5"
                            placeholder="—"
                            value={g.p1 === 0 ? '' : g.p1}
                            onChange={(e) =>
                              handleInputChange(s.uid, 'p1', e.target.value, assessmentConfig.p1Max)
                            }
                            className="w-16 px-1.5 py-1 text-center font-mono font-bold text-xs border border-slate-300 rounded-lg focus:ring-2 focus:ring-emerald-700 bg-white placeholder:text-slate-300 shadow-2xs"
                            title={`${assessmentConfig.p1Label} (ከ ${assessmentConfig.p1Max})`}
                          />
                        </td>

                        {/* 2. Criterion 2 */}
                        <td className="p-2 text-center">
                          <input
                            type="number"
                            min="0"
                            max={assessmentConfig.p2Max}
                            step="0.5"
                            placeholder="—"
                            value={g.p2 === 0 ? '' : g.p2}
                            onChange={(e) =>
                              handleInputChange(s.uid, 'p2', e.target.value, assessmentConfig.p2Max)
                            }
                            className="w-16 px-1.5 py-1 text-center font-mono font-bold text-xs border border-slate-300 rounded-lg focus:ring-2 focus:ring-emerald-700 bg-white placeholder:text-slate-300 shadow-2xs"
                            title={`${assessmentConfig.p2Label} (ከ ${assessmentConfig.p2Max})`}
                          />
                        </td>

                        {/* 3. Criterion 3 */}
                        <td className="p-2 text-center">
                          <input
                            type="number"
                            min="0"
                            max={assessmentConfig.midMax}
                            step="0.5"
                            placeholder="—"
                            value={g.mid === 0 ? '' : g.mid}
                            onChange={(e) =>
                              handleInputChange(s.uid, 'mid', e.target.value, assessmentConfig.midMax)
                            }
                            className="w-16 px-1.5 py-1 text-center font-mono font-bold text-xs border border-slate-300 rounded-lg focus:ring-2 focus:ring-emerald-700 bg-white placeholder:text-slate-300 shadow-2xs"
                            title={`${assessmentConfig.midLabel} (ከ ${assessmentConfig.midMax})`}
                          />
                        </td>

                        {/* 4. Criterion 4 */}
                        <td className="p-2 text-center">
                          <input
                            type="number"
                            min="0"
                            max={assessmentConfig.finalMax}
                            step="0.5"
                            placeholder="—"
                            value={g.final === 0 ? '' : g.final}
                            onChange={(e) =>
                              handleInputChange(s.uid, 'final', e.target.value, assessmentConfig.finalMax)
                            }
                            className="w-16 px-1.5 py-1 text-center font-mono font-bold text-xs border border-slate-300 rounded-lg focus:ring-2 focus:ring-emerald-700 bg-white placeholder:text-slate-300 shadow-2xs"
                            title={`${assessmentConfig.finalLabel} (ከ ${assessmentConfig.finalMax})`}
                          />
                        </td>

                        {/* 5. Criterion 5 */}
                        <td className="p-2 text-center">
                          <input
                            type="number"
                            min="0"
                            max={assessmentConfig.attMax}
                            step="0.5"
                            placeholder="—"
                            value={g.att === 0 ? '' : g.att}
                            onChange={(e) =>
                              handleInputChange(s.uid, 'att', e.target.value, assessmentConfig.attMax)
                            }
                            className="w-16 px-1.5 py-1 text-center font-mono font-bold text-xs border border-slate-300 rounded-lg focus:ring-2 focus:ring-emerald-700 bg-white placeholder:text-slate-300 shadow-2xs"
                            title={`${assessmentConfig.attLabel} (ከ ${assessmentConfig.attMax})`}
                          />
                        </td>

                        {/* 6. Editable Total (ድምር 100%) */}
                        <td className="p-2 text-center">
                          <div className="relative inline-block">
                            <input
                              type="number"
                              min="0"
                              max="100"
                              step="0.5"
                              placeholder="—"
                              value={
                                hasCustomTotal
                                  ? g.customTotal === 0
                                    ? ''
                                    : g.customTotal
                                  : hasParts
                                  ? partsSum
                                  : ''
                              }
                              onChange={(e) => handleCustomTotalChange(s.uid, e.target.value)}
                              className={`w-18 px-1.5 py-1 text-center font-mono font-black text-xs rounded-lg border focus:ring-2 focus:ring-emerald-700 transition-colors ${
                                hasCustomTotal
                                  ? 'border-indigo-400 bg-indigo-50/80 text-indigo-950 font-extrabold ring-1 ring-indigo-300'
                                  : 'border-emerald-300 bg-emerald-50/50 text-emerald-900'
                              } placeholder:text-slate-300 shadow-2xs`}
                              title={
                                hasCustomTotal
                                  ? 'በቀጥታ በመምህሩ የተሞላ ድምር (Custom Total)። ለማስላት ባዶ አድርገው ይተዉት።'
                                  : 'የተደመረ ድምር (በቀጥታ መተየብም ይችላሉ)'
                              }
                            />
                            {hasCustomTotal && (
                              <span
                                className="absolute -top-1 -right-1 flex h-2 w-2 cursor-pointer"
                                title="በእጅ የተሞላ ድምር"
                              >
                                <span className="animate-ping absolute inline-flex h-full w-full rounded-full bg-indigo-400 opacity-75"></span>
                                <span className="relative inline-flex rounded-full h-2 w-2 bg-indigo-600"></span>
                              </span>
                            )}
                          </div>
                        </td>

                        {/* 7. Editable Letter Grade (ፊደል ደረጃ) */}
                        <td className="p-2 text-center">
                          <select
                            value={g.customLetter || ''}
                            onChange={(e) => handleCustomLetterChange(s.uid, e.target.value)}
                            className={`w-20 px-1 py-1 text-center font-black text-xs rounded-lg border focus:ring-2 focus:ring-amber-500 cursor-pointer shadow-2xs transition-colors ${
                              g.customLetter
                                ? 'bg-amber-100/90 text-amber-950 border-amber-400 font-extrabold ring-1 ring-amber-300'
                                : 'bg-amber-50/50 text-amber-900 border-amber-300'
                            }`}
                            title="የፊደል ደረጃን ኤዲት ማድረግ ይችላሉ"
                          >
                            <option value="">
                              {eff.hasAny ? `${autoGrade.letter} (አውቶ)` : '— (አውቶ)'}
                            </option>
                            <option value="A+">A+ (4.00)</option>
                            <option value="A">A (3.75)</option>
                            <option value="B">B (3.00)</option>
                            <option value="C">C (2.00)</option>
                            <option value="D">D (1.00)</option>
                            <option value="F">F (0.00)</option>
                          </select>
                        </td>

                        {/* 8. Editable Description / Meaning (ትርጉም) */}
                        <td className="p-2 text-center">
                          <input
                            type="text"
                            placeholder={defaultDesc}
                            value={g.customDesc !== undefined ? g.customDesc : ''}
                            onChange={(e) => handleCustomDescChange(s.uid, e.target.value)}
                            className={`w-32 px-2 py-1 text-xs text-slate-800 rounded-lg border focus:ring-2 focus:ring-emerald-700 placeholder:text-slate-400 font-medium shadow-2xs transition-colors ${
                              g.customDesc
                                ? 'bg-blue-50/80 border-blue-400 text-blue-950 ring-1 ring-blue-300'
                                : 'bg-white border-slate-300'
                            }`}
                            title="ትርጉሙን ወይም ልዩ አስተያየቱን ኤዲት ማድረግ ይችላሉ"
                          />
                        </td>
                      </tr>
                    );
                  })}
                </tbody>
              </table>
            </div>

            <div className="pt-3 border-t flex flex-wrap justify-between items-center gap-3">
              <span className="text-xs text-slate-500 font-semibold">
                የክፍሉ አማካይ ውጤት: <strong className="text-slate-800">{classAverage}%</strong> ({gradedCount} ከተማሪዎች ተሞልቷል)
              </span>

              <div className="flex items-center gap-2">
                <button
                  type="button"
                  onClick={handleClearCourse}
                  className="bg-slate-100 hover:bg-rose-50 text-slate-600 hover:text-rose-700 text-xs px-4 py-2.5 rounded-xl font-bold border border-slate-300 transition-colors cursor-pointer"
                >
                  ውጤቶችን አጽዳ / ባዶ አድርግ
                </button>
                <button
                  onClick={handleSaveAll}
                  className="bg-emerald-700 hover:bg-emerald-800 text-white text-xs px-5 py-2.5 rounded-xl font-bold shadow-md transition-all flex items-center gap-2 cursor-pointer"
                >
                  <Save className="w-4 h-4" />
                  <span>ውጤቶችን መዝግብ (Save All)</span>
                </button>
              </div>
            </div>
          </div>
        </>
      )}

      {/* MODAL: Edit Assessment Criteria & Weights (ለመምህራን የመመዘኛዎች ማስተካከያ) */}
      {isSchemeModalOpen && (
        <div className="fixed inset-0 bg-slate-950/70 backdrop-blur-xs z-50 flex items-center justify-center p-4">
          <div className="bg-white rounded-3xl shadow-2xl max-w-xl w-full flex flex-col overflow-hidden border border-slate-200">
            {/* Modal Header */}
            <div className="bg-gradient-to-r from-emerald-950 to-slate-900 text-white p-5 flex justify-between items-center">
              <div className="flex items-center gap-2.5">
                <div className="p-2 bg-emerald-700/60 rounded-xl">
                  <SlidersHorizontal className="w-5 h-5 text-amber-300" />
                </div>
                <div>
                  <h3 className="font-bold text-sm text-white">
                    የውጤት መመዘኛዎችንና ክብደት ኤዲት ማድረጊያ
                  </h3>
                  <p className="text-[11px] text-emerald-200">
                    ኮርስ: <strong>{activeCourse?.name}</strong> ({activeCourse?.program})
                  </p>
                </div>
              </div>

              <button
                type="button"
                onClick={() => setIsSchemeModalOpen(false)}
                className="p-1.5 rounded-xl hover:bg-white/10 text-slate-300 hover:text-white transition-colors cursor-pointer"
              >
                <X className="w-5 h-5" />
              </button>
            </div>

            {/* Modal Body */}
            <form onSubmit={handleSaveScheme} className="p-6 space-y-5">
              <p className="text-xs text-slate-600 bg-slate-50 p-3 rounded-xl border border-slate-200">
                መምህራን የመመዘኛዎቹን መጠሪያ ስሞች እና እያንዳንዱ መስፈርት የሚይዘውን የመቶኛ ክብደት (%) እዚህ ጋር ኤዲት ማድረግ ይችላሉ።
              </p>

              {/* 5 Assessment Criteria Rows */}
              <div className="space-y-3">
                {/* Criterion 1 */}
                <div className="flex items-center gap-3 p-2.5 rounded-2xl bg-slate-50 border border-slate-200">
                  <span className="w-8 text-center font-bold text-xs text-slate-400">1</span>
                  <div className="flex-1">
                    <label className="block text-[10px] font-bold text-slate-500 mb-0.5">የመመዘኛ 1 ስም</label>
                    <input
                      type="text"
                      value={editingScheme.p1Label}
                      onChange={(e) => setEditingScheme({ ...editingScheme, p1Label: e.target.value })}
                      required
                      placeholder="ምሳሌ: ክፍል 1 ወይም አሳይመንት 1"
                      className="w-full px-2.5 py-1.5 text-xs font-bold border border-slate-300 rounded-xl bg-white focus:ring-2 focus:ring-emerald-700"
                    />
                  </div>
                  <div className="w-24">
                    <label className="block text-[10px] font-bold text-slate-500 mb-0.5">መቶኛ (%)</label>
                    <input
                      type="number"
                      min="1"
                      max="100"
                      value={editingScheme.p1Max}
                      onChange={(e) =>
                        setEditingScheme({ ...editingScheme, p1Max: Math.max(0, parseFloat(e.target.value) || 0) })
                      }
                      required
                      className="w-full px-2 py-1.5 text-xs font-mono font-bold text-center border border-slate-300 rounded-xl bg-white focus:ring-2 focus:ring-emerald-700"
                    />
                  </div>
                </div>

                {/* Criterion 2 */}
                <div className="flex items-center gap-3 p-2.5 rounded-2xl bg-slate-50 border border-slate-200">
                  <span className="w-8 text-center font-bold text-xs text-slate-400">2</span>
                  <div className="flex-1">
                    <label className="block text-[10px] font-bold text-slate-500 mb-0.5">የመመዘኛ 2 ስም</label>
                    <input
                      type="text"
                      value={editingScheme.p2Label}
                      onChange={(e) => setEditingScheme({ ...editingScheme, p2Label: e.target.value })}
                      required
                      placeholder="ምሳሌ: ክፍል 2 ወይም የግል ስራ"
                      className="w-full px-2.5 py-1.5 text-xs font-bold border border-slate-300 rounded-xl bg-white focus:ring-2 focus:ring-emerald-700"
                    />
                  </div>
                  <div className="w-24">
                    <label className="block text-[10px] font-bold text-slate-500 mb-0.5">መቶኛ (%)</label>
                    <input
                      type="number"
                      min="1"
                      max="100"
                      value={editingScheme.p2Max}
                      onChange={(e) =>
                        setEditingScheme({ ...editingScheme, p2Max: Math.max(0, parseFloat(e.target.value) || 0) })
                      }
                      required
                      className="w-full px-2 py-1.5 text-xs font-mono font-bold text-center border border-slate-300 rounded-xl bg-white focus:ring-2 focus:ring-emerald-700"
                    />
                  </div>
                </div>

                {/* Criterion 3 */}
                <div className="flex items-center gap-3 p-2.5 rounded-2xl bg-slate-50 border border-slate-200">
                  <span className="w-8 text-center font-bold text-xs text-slate-400">3</span>
                  <div className="flex-1">
                    <label className="block text-[10px] font-bold text-slate-500 mb-0.5">የመመዘኛ 3 ስም</label>
                    <input
                      type="text"
                      value={editingScheme.midLabel}
                      onChange={(e) => setEditingScheme({ ...editingScheme, midLabel: e.target.value })}
                      required
                      placeholder="ምሳሌ: ሚድ ፈተና"
                      className="w-full px-2.5 py-1.5 text-xs font-bold border border-slate-300 rounded-xl bg-white focus:ring-2 focus:ring-emerald-700"
                    />
                  </div>
                  <div className="w-24">
                    <label className="block text-[10px] font-bold text-slate-500 mb-0.5">መቶኛ (%)</label>
                    <input
                      type="number"
                      min="1"
                      max="100"
                      value={editingScheme.midMax}
                      onChange={(e) =>
                        setEditingScheme({ ...editingScheme, midMax: Math.max(0, parseFloat(e.target.value) || 0) })
                      }
                      required
                      className="w-full px-2 py-1.5 text-xs font-mono font-bold text-center border border-slate-300 rounded-xl bg-white focus:ring-2 focus:ring-emerald-700"
                    />
                  </div>
                </div>

                {/* Criterion 4 */}
                <div className="flex items-center gap-3 p-2.5 rounded-2xl bg-slate-50 border border-slate-200">
                  <span className="w-8 text-center font-bold text-xs text-slate-400">4</span>
                  <div className="flex-1">
                    <label className="block text-[10px] font-bold text-slate-500 mb-0.5">የመመዘኛ 4 ስም</label>
                    <input
                      type="text"
                      value={editingScheme.finalLabel}
                      onChange={(e) => setEditingScheme({ ...editingScheme, finalLabel: e.target.value })}
                      required
                      placeholder="ምሳሌ: ፋይናል ፈተና"
                      className="w-full px-2.5 py-1.5 text-xs font-bold border border-slate-300 rounded-xl bg-white focus:ring-2 focus:ring-emerald-700"
                    />
                  </div>
                  <div className="w-24">
                    <label className="block text-[10px] font-bold text-slate-500 mb-0.5">መቶኛ (%)</label>
                    <input
                      type="number"
                      min="1"
                      max="100"
                      value={editingScheme.finalMax}
                      onChange={(e) =>
                        setEditingScheme({ ...editingScheme, finalMax: Math.max(0, parseFloat(e.target.value) || 0) })
                      }
                      required
                      className="w-full px-2 py-1.5 text-xs font-mono font-bold text-center border border-slate-300 rounded-xl bg-white focus:ring-2 focus:ring-emerald-700"
                    />
                  </div>
                </div>

                {/* Criterion 5 */}
                <div className="flex items-center gap-3 p-2.5 rounded-2xl bg-slate-50 border border-slate-200">
                  <span className="w-8 text-center font-bold text-xs text-slate-400">5</span>
                  <div className="flex-1">
                    <label className="block text-[10px] font-bold text-slate-500 mb-0.5">የመመዘኛ 5 ስም</label>
                    <input
                      type="text"
                      value={editingScheme.attLabel}
                      onChange={(e) => setEditingScheme({ ...editingScheme, attLabel: e.target.value })}
                      required
                      placeholder="ምሳሌ: ተሳትፎ ወይም ስነ ምግባር"
                      className="w-full px-2.5 py-1.5 text-xs font-bold border border-slate-300 rounded-xl bg-white focus:ring-2 focus:ring-emerald-700"
                    />
                  </div>
                  <div className="w-24">
                    <label className="block text-[10px] font-bold text-slate-500 mb-0.5">መቶኛ (%)</label>
                    <input
                      type="number"
                      min="1"
                      max="100"
                      value={editingScheme.attMax}
                      onChange={(e) =>
                        setEditingScheme({ ...editingScheme, attMax: Math.max(0, parseFloat(e.target.value) || 0) })
                      }
                      required
                      className="w-full px-2 py-1.5 text-xs font-mono font-bold text-center border border-slate-300 rounded-xl bg-white focus:ring-2 focus:ring-emerald-700"
                    />
                  </div>
                </div>
              </div>

              {/* Total Percentage Calculation Badge */}
              <div
                className={`p-3 rounded-2xl flex justify-between items-center text-xs font-bold ${
                  totalSchemeWeight === 100
                    ? 'bg-emerald-50 text-emerald-900 border border-emerald-300'
                    : 'bg-amber-50 text-amber-900 border border-amber-300'
                }`}
              >
                <span>የመመዘኛዎቹ ድምር መቶኛ (Total Weight):</span>
                <span className="font-mono text-sm font-black">
                  {totalSchemeWeight}% {totalSchemeWeight === 100 ? '✓ (ትክክል)' : '⚠️ (100% መሙላት ይመረጣል)'}
                </span>
              </div>

              {/* Footer buttons */}
              <div className="flex justify-between items-center pt-2 border-t border-slate-200">
                <button
                  type="button"
                  onClick={handleResetSchemeToDefault}
                  className="inline-flex items-center gap-1.5 text-xs font-bold text-slate-600 hover:text-slate-900 hover:bg-slate-100 px-3 py-2 rounded-xl transition-colors cursor-pointer"
                >
                  <RotateCcw className="w-3.5 h-3.5" />
                  <span>ወደ ነባሪ መልስ (Reset Default)</span>
                </button>

                <div className="flex items-center gap-2">
                  <button
                    type="button"
                    onClick={() => setIsSchemeModalOpen(false)}
                    className="px-4 py-2 text-xs font-semibold text-slate-600 hover:bg-slate-100 rounded-xl cursor-pointer"
                  >
                    ሰርዝ
                  </button>
                  <button
                    type="submit"
                    className="inline-flex items-center gap-1.5 bg-emerald-800 hover:bg-emerald-900 text-white text-xs px-5 py-2.5 rounded-xl font-bold shadow-md transition-all cursor-pointer"
                  >
                    <Check className="w-4 h-4" />
                    <span>ቅንብሩን መዝግብ (Save Scheme)</span>
                  </button>
                </div>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};
