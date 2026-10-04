<script lang="ts">
  import { buildMonthGrid, dayKey, WEEKDAY_LABELS, MONTH_LABELS } from "#lib/utils/calendar.ts";
  import type { FeedResponse } from "#lib/types/feed.ts";

  let {
    feeds,
    selectedDayKey = $bindable(),
    year = $bindable(),
    month = $bindable(),
  }: {
    feeds: FeedResponse[];
    selectedDayKey: string;
    year: number;
    month: number;
  } = $props();

  let grid = $derived(buildMonthGrid(year, month));

  let feedsByDay = $derived.by(() => {
    const map = new Map<string, FeedResponse[]>();
    for (const feed of feeds) {
      const key = dayKey(new Date(feed.startTime));
      if (!map.has(key)) map.set(key, []);
      map.get(key)!.push(feed);
    }
    return map;
  });

  function feedCount(key: string): number {
    return feedsByDay.get(key)?.length ?? 0;
  }

  function prevMonth() {
    if (month === 0) { month = 11; year -= 1; } else { month -= 1; }
  }
  function nextMonth() {
    if (month === 11) { month = 0; year += 1; } else { month += 1; }
  }
</script>

<div class="calendar">
  <div class="calendar-header">
    <button onclick={prevMonth} aria-label="Mois précédent">‹</button>
    <span>{MONTH_LABELS[month]} {year}</span>
    <button onclick={nextMonth} aria-label="Mois suivant">›</button>
  </div>

  <div class="weekdays">
    {#each WEEKDAY_LABELS as label}
      <span>{label}</span>
    {/each}
  </div>

  <div class="grid">
    {#each grid as day (day.dayKey)}
      <button
        class="day-cell"
        class:muted={!day.isCurrentMonth}
        class:today={day.isToday}
        class:selected={day.dayKey === selectedDayKey}
        onclick={() => (selectedDayKey = day.dayKey)}
      >
        <span class="day-number">{day.date.getDate()}</span>
        {#if feedCount(day.dayKey) > 0}
          <span class="feed-indicator" style:--count={Math.min(feedCount(day.dayKey), 5)}></span>
        {/if}
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
  .weekdays {
    display: grid;
    grid-template-columns: repeat(7, 1fr);
    margin-bottom: 6px;
  }
  .weekdays span {
    text-align: center;
    font-size: 11px;
    color: var(--muted);
    font-weight: 600;
  }
  .grid {
    display: grid;
    grid-template-columns: repeat(7, 1fr);
    gap: 4px;
  }
  .day-cell {
    aspect-ratio: 1;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    gap: 4px;
    border: 1px solid transparent;
    border-radius: 10px;
    background: var(--bg);
    cursor: pointer;
    padding: 0;
  }
  .day-number { font-size: 13px; color: var(--text); }
  .day-cell.muted .day-number { color: var(--muted); opacity: 0.5; }
  .day-cell.today { border-color: var(--accent); }
  .day-cell.selected { background: var(--accent); }
  .day-cell.selected .day-number { color: #fff; }
  .feed-indicator {
    width: calc(4px + var(--count) * 2px);
    height: 4px;
    border-radius: 999px;
    background: var(--accent);
  }
  .day-cell.selected .feed-indicator { background: #fff; }
</style>