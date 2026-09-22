<script lang="ts">
  import { buildWeekGrid, dayKey, addWeeks, formatWeekRange, WEEKDAY_LABELS_LONG } from "#lib/utils/calendar.ts";
  import type { FeedResponse } from "#lib/types/feed.ts";

  let {
    feeds,
    selectedDayKey = $bindable(),
    weekStart = $bindable(),
  }: {
    feeds: FeedResponse[];
    selectedDayKey: string;
    weekStart: Date;
  } = $props();

  let grid = $derived(buildWeekGrid(weekStart));

  let feedsByDay = $derived.by(() => {
    const map = new Map<string, FeedResponse[]>();
    for (const feed of feeds) {
      const key = dayKey(new Date(feed.startTime));
      if (!map.has(key)) map.set(key, []);
      map.get(key)!.push(feed);
    }
    return map;
  });

  function daySummary(key: string): { count: number; minutes: number } {
    const dayFeeds = feedsByDay.get(key) ?? [];
    const finished = dayFeeds.filter((f) => !f.ongoing);
    return {
      count: finished.length,
      minutes: finished.reduce((sum, f) => sum + (f.durationMinutes ?? 0), 0),
    };
  }

  function prevWeek() { weekStart = addWeeks(weekStart, -1); }
  function nextWeek() { weekStart = addWeeks(weekStart, 1); }
</script>

<div class="calendar">
  <div class="calendar-header">
    <button onclick={prevWeek} aria-label="Semaine précédente">‹</button>
    <span>{formatWeekRange(weekStart)}</span>
    <button onclick={nextWeek} aria-label="Semaine suivante">›</button>
  </div>

  <div class="week-list">
    {#each grid as day, i (day.dayKey)}
      {@const summary = daySummary(day.dayKey)}
      <button
        class="day-row"
        class:today={day.isToday}
        class:selected={day.dayKey === selectedDayKey}
        onclick={() => (selectedDayKey = day.dayKey)}
      >
        <span class="weekday">{WEEKDAY_LABELS_LONG[i]}</span>
        <span class="day-number">{day.date.getDate()}</span>
        <span class="summary">
          {#if summary.count > 0}
            {summary.count} tétées · {summary.minutes} min
          {:else}
            <span class="no-feed">—</span>
          {/if}
        </span>
      </button>
    {/each}
  </div>
</div>

<style>
  .calendar { width: 100%; }
  .calendar-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: 12px;
    font-family: "Fraunces", serif;
    font-weight: 500;
    font-size: 15px;
  }
  .calendar-header button {
    background: none;
    border: none;
    color: var(--text);
    font-size: 20px;
    cursor: pointer;
    padding: 4px 10px;
  }
  .week-list {
    display: flex;
    flex-direction: column;
    gap: 4px;
  }
  .day-row {
    display: flex;
    align-items: center;
    gap: 12px;
    padding: 10px 12px;
    border-radius: 10px;
    border: 1px solid transparent;
    background: var(--bg);
    cursor: pointer;
    width: 100%;
    text-align: left;
  }
  .day-row.today { border-color: var(--accent); }
  .day-row.selected { background: var(--accent); }
  .day-row.selected .weekday,
  .day-row.selected .day-number,
  .day-row.selected .summary { color: #fff; }
  .weekday {
    font-size: 12px;
    color: var(--muted);
    width: 32px;
  }
  .day-number {
    font-family: "Fraunces", serif;
    font-size: 15px;
    width: 20px;
    color: var(--text);
  }
  .summary {
    margin-left: auto;
    font-size: 13px;
    color: var(--muted);
  }
  .no-feed { opacity: 0.4; }
</style>