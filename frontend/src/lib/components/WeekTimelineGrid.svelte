<script lang="ts">
  import { addWeeks, formatWeekRange, dayKey, WEEKDAY_LABELS } from "#lib/utils/calendar.ts";
  import type { FeedResponse } from "#lib/types/feed.ts";

  let {
    feeds,
    weekStart = $bindable(),
  }: {
    feeds: FeedResponse[];
    weekStart: Date;
  } = $props();

  const HOUR_MARKS = [0, 3, 6, 9, 12, 15, 18, 21, 24];

  interface DayColumn {
    date: Date;
    dayKey: string;
    weekdayLabel: string;
    isToday: boolean;
  }

  let days = $derived.by((): DayColumn[] => {
    const todayKey = dayKey(new Date());
    const cols: DayColumn[] = [];
    for (let i = 0; i < 7; i++) {
      const d = new Date(weekStart);
      d.setDate(weekStart.getDate() + i);
      cols.push({
        date: d,
        dayKey: dayKey(d),
        weekdayLabel: WEEKDAY_LABELS[i],
        isToday: dayKey(d) === todayKey,
      });
    }
    return cols;
  });

  function percentOfDay(iso: string): number {
    const d = new Date(iso);
    return ((d.getHours() * 60 + d.getMinutes()) / 1440) * 100;
  }

  interface Segment {
    id: number;
    side: "LEFT" | "RIGHT";
    topPercent: number;
    heightPercent: number;
  }

  let segmentsByDay = $derived.by(() => {
    const map = new Map<string, Segment[]>();
    for (const feed of feeds) {
      if (feed.ongoing) continue;
      const key = dayKey(new Date(feed.startTime));
      const top = percentOfDay(feed.startTime);
      const end = feed.endTime ? percentOfDay(feed.endTime) : top;
      const height = Math.max(end - top, 0.8);
      if (!map.has(key)) map.set(key, []);
      map.get(key)!.push({ id: feed.id, side: feed.breastSide, topPercent: top, heightPercent: height });
    }
    return map;
  });

  function prevWeek() { weekStart = addWeeks(weekStart, -1); }
  function nextWeek() { weekStart = addWeeks(weekStart, 1); }
</script>

<div class="week-timeline">
  <div class="header">
    <button onclick={prevWeek} aria-label="Semaine précédente">‹</button>
    <span>{formatWeekRange(weekStart)}</span>
    <button onclick={nextWeek} aria-label="Semaine suivante">›</button>
  </div>

  <div class="day-headers">
    <div class="hour-axis-spacer"></div>
    {#each days as day (day.dayKey)}
      <div class="day-header" class:today={day.isToday}>
        <span class="weekday">{day.weekdayLabel}</span>
        <span class="date-num">{day.date.getDate()}</span>
      </div>
    {/each}
  </div>

  <div class="grid-wrap">
    <div class="hour-axis">
      {#each HOUR_MARKS as h}
        <span class="hour-mark" style:top="{(h / 24) * 100}%">{h}h</span>
      {/each}
    </div>

    <div class="days-track">
      {#each days as day (day.dayKey)}
        <div class="day-col" class:today={day.isToday}>
          {#each segmentsByDay.get(day.dayKey) ?? [] as seg (seg.id)}
            <div
              class="segment"
              class:left={seg.side === "LEFT"}
              class:right={seg.side === "RIGHT"}
              style:top="{seg.topPercent}%"
              style:height="{seg.heightPercent}%"
            ></div>
          {/each}
        </div>
      {/each}
    </div>
  </div>

  <div class="legend">
    <span class="legend-item"><span class="dot left"></span> Gauche</span>
    <span class="legend-item"><span class="dot right"></span> Droite</span>
  </div>
</div>

<style>
  .week-timeline { width: 100%; }
  .header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: 12px;
    font-family: "Fraunces", serif;
    font-weight: 500;
    font-size: 15px;
  }
  .header button {
    background: none;
    border: none;
    color: var(--text);
    font-size: 20px;
    cursor: pointer;
    padding: 4px 10px;
  }

  .day-headers {
    display: grid;
    grid-template-columns: 28px repeat(7, 1fr);
    gap: 2px;
    margin-bottom: 4px;
  }
  .hour-axis-spacer { width: 28px; }
  .day-header {
    display: flex;
    flex-direction: column;
    align-items: center;
    padding: 2px 0;
    border-radius: 8px;
  }
  .day-header.today { background: var(--accent); }
  .day-header.today .weekday,
  .day-header.today .date-num { color: #fff; }
  .weekday { font-size: 10px; color: var(--muted); font-weight: 600; }
  .date-num { font-size: 12px; color: var(--text); font-family: "Fraunces", serif; }

  .grid-wrap {
    display: grid;
    grid-template-columns: 28px 1fr;
    height: 420px;
  }
  .hour-axis {
    position: relative;
  }
  .hour-mark {
    position: absolute;
    transform: translateY(-50%);
    font-size: 9.5px;
    color: var(--muted);
    right: 4px;
  }
  .days-track {
    display: grid;
    grid-template-columns: repeat(7, 1fr);
    gap: 2px;
    position: relative;
  }
  .days-track::before {
    /* lignes horizontales discrètes tous les 3h, sur toute la largeur */
    content: "";
    position: absolute;
    inset: 0;
    background-image: repeating-linear-gradient(
      to bottom,
      var(--surface-border) 0,
      var(--surface-border) 1px,
      transparent 1px,
      transparent calc(100% / 8)
    );
    pointer-events: none;
  }
  .day-col {
    position: relative;
    background: var(--bg);
    border-radius: 6px;
  }
  .day-col.today {
    background: color-mix(in srgb, var(--accent) 8%, var(--bg));
  }
  .segment {
    position: absolute;
    left: 1px;
    right: 1px;
    border-radius: 3px;
    min-height: 3px;
  }
  .segment.left { background: var(--left); }
  .segment.right { background: var(--right); }

  .legend {
    display: flex;
    gap: 16px;
    margin-top: 14px;
    justify-content: center;
  }
  .legend-item {
    display: flex;
    align-items: center;
    gap: 6px;
    font-size: 13px;
    color: var(--muted);
  }
  .dot { width: 8px; height: 8px; border-radius: 50%; }
  .dot.left { background: var(--left); }
  .dot.right { background: var(--right); }
</style>