export interface CalendarDay {
  date: Date;
  isCurrentMonth: boolean;
  isToday: boolean;
  dayKey: string;
}

export function dayKey(d: Date): string {
  return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, "0")}-${String(d.getDate()).padStart(2, "0")}`;
}

export function buildMonthGrid(year: number, month: number): CalendarDay[] {
  const firstOfMonth = new Date(year, month, 1);
  const startOffset = (firstOfMonth.getDay() + 6) % 7;
  const gridStart = new Date(year, month, 1 - startOffset);

  const today = new Date();
  const todayKey = dayKey(today);

  const days: CalendarDay[] = [];
  for (let i = 0; i < 42; i++) {
    const d = new Date(gridStart);
    d.setDate(gridStart.getDate() + i);
    days.push({ date: d, isCurrentMonth: d.getMonth() === month, isToday: dayKey(d) === todayKey, dayKey: dayKey(d) });
  }
  return days;
}

// Retourne le lundi de la semaine contenant `date`
export function startOfWeek(date: Date): Date {
  const offset = (date.getDay() + 6) % 7; // dimanche=0 devient 6, lundi=1 devient 0
  const monday = new Date(date);
  monday.setDate(date.getDate() - offset);
  monday.setHours(0, 0, 0, 0);
  return monday;
}

export function buildWeekGrid(weekStart: Date): CalendarDay[] {
  const today = new Date();
  const todayKey = dayKey(today);

  const days: CalendarDay[] = [];
  for (let i = 0; i < 7; i++) {
    const d = new Date(weekStart);
    d.setDate(weekStart.getDate() + i);
    days.push({ date: d, isCurrentMonth: true, isToday: dayKey(d) === todayKey, dayKey: dayKey(d) });
  }
  return days;
}

export function addWeeks(date: Date, delta: number): Date {
  const d = new Date(date);
  d.setDate(d.getDate() + delta * 7);
  return d;
}

export function formatWeekRange(weekStart: Date): string {
  const weekEnd = new Date(weekStart);
  weekEnd.setDate(weekStart.getDate() + 6);
  const sameMonth = weekStart.getMonth() === weekEnd.getMonth();
  const startLabel = weekStart.toLocaleDateString("fr-FR", { day: "numeric", month: sameMonth ? undefined : "short" });
  const endLabel = weekEnd.toLocaleDateString("fr-FR", { day: "numeric", month: "short" });
  return `${startLabel} – ${endLabel}`;
}

export const WEEKDAY_LABELS = ["L", "M", "M", "J", "V", "S", "D"];
export const WEEKDAY_LABELS_LONG = ["Lun", "Mar", "Mer", "Jeu", "Ven", "Sam", "Dim"];
export const MONTH_LABELS = [
  "Janvier", "Février", "Mars", "Avril", "Mai", "Juin",
  "Juillet", "Août", "Septembre", "Octobre", "Novembre", "Décembre",
];