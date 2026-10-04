<script lang="ts">
  import { onMount } from "svelte";
  import { babyStore } from "#lib/stores/baby.svelte.ts";
  import { listFeedsInRange } from "#lib/services/feedService.ts";
  import { dayKey, startOfWeek, flattenEntries, monthGridRange } from "#lib/utils/calendar.ts";
  import type { FeedResponse } from "#lib/types/feed.ts";

  import MonthCalendar from "#lib/components/MonthCalendar.svelte";
  import WeekTimelineGrid from "#lib/components/WeekTimelineGrid.svelte";
  import DayTimeline from "#lib/components/DayTimeline.svelte";

  let focusDate = $state(new Date());
  let selectedDayKey = $state(dayKey(new Date()));

  let weekStart = $derived(startOfWeek(focusDate));
  let year = $derived(focusDate.getFullYear());
  let month = $derived(focusDate.getMonth());

  let babyId = $derived(babyStore.selectedId);

  let weekFeeds = $state<FeedResponse[]>([]);
  let monthFeeds = $state<FeedResponse[]>([]);

  let weekSection: HTMLElement;
  let scrollContainer: HTMLDivElement;

  let latestWeekRequestId = 0;
  let latestMonthRequestId = 0;

  async function loadWeek() {
    if (!babyId) return;
    const requestId = ++latestWeekRequestId;
    const to = new Date(weekStart.getTime() + 7 * 86400000);
    const result = await listFeedsInRange(babyId, weekStart.toISOString(), to.toISOString());
    if (requestId === latestWeekRequestId) weekFeeds = result;
  }

  async function loadMonth() {
    if (!babyId) return;
    const requestId = ++latestMonthRequestId;
    const { from, to } = monthGridRange(year, month);
    const result = await listFeedsInRange(babyId, from.toISOString(), to.toISOString());
    if (requestId === latestMonthRequestId) monthFeeds = result;
  }

  $effect(() => {
    weekStart; babyId;
    loadWeek();
  });

  $effect(() => {
    year; month; babyId;
    loadMonth();
  });

  function prevWeek() { focusDate = new Date(focusDate.getTime() - 7 * 86400000); }
  function nextWeek() { focusDate = new Date(focusDate.getTime() + 7 * 86400000); }
  function prevMonth() { focusDate = new Date(year, month - 1, 1); }
  function nextMonth() { focusDate = new Date(year, month + 1, 1); }

  function selectDay(key: string) {
    selectedDayKey = key;
    focusDate = new Date(key);
  }

  let selectedDayFeeds = $derived(
    flattenEntries(monthFeeds.filter((f) => dayKey(new Date(f.startTime)) === selectedDayKey))
  );

  onMount(() => {
    weekSection.scrollIntoView({ behavior: "instant" as ScrollBehavior });
  });
</script>

<div class="scroll-container" bind:this={scrollContainer}>

  <section class="snap-section week-section" bind:this={weekSection}>
  <button class="scroll-hint down" onclick={() => weekSection.scrollIntoView({ behavior: "smooth" })}>
        Mois ↓
      </button>
    {#if babyId}
      <div class="week-wrap">
        <WeekTimelineGrid feeds={flattenEntries(weekFeeds)} {weekStart} onPrev={prevWeek} onNext={nextWeek} />
      </div>
    {/if}
  </section>

  <section class="snap-section month-section">
    {#if !babyId}
      <p class="empty">Aucun bébé enregistré.</p>
    {:else}
      <div class="month-wrap">
        <MonthCalendar feeds={monthFeeds} {year} {month} {selectedDayKey} onSelectDay={selectDay} onPrev={prevMonth} onNext={nextMonth} />
        <DayTimeline feeds={selectedDayFeeds} dayKey={selectedDayKey} />
      </div>
    {/if}
    <button class="scroll-hint up" onclick={() => scrollContainer.scrollTo({ top: 0, behavior: "smooth" })}>
      ↑ Semaine
    </button>
    <a href="/feeds" class="back-link">← Retour au suivi</a>
  </section>
</div>

<style>
  .scroll-container {
    height: 100dvh;
    overflow-y: auto;
    scroll-snap-type: y mandatory;
    scroll-behavior: smooth;
  }
  .snap-section {
    scroll-snap-align: start;
    min-height: 100dvh;
    display: flex;
    flex-direction: column;
    padding: 4px 20px 24px;
    max-width: 420px;
    margin: 0 auto;
    box-sizing: border-box;
  }
  .month-section {
    justify-content: flex-start;
    gap: 16px;
    padding-top: calc(16px + env(safe-area-inset-top, 0px));
  }
  .month-wrap {
    display: flex;
    flex-direction: column;
    gap: 16px;
    background: var(--surface);
    border: 1px solid var(--surface-border);
    border-radius: var(--card-radius);
    box-shadow: var(--card-shadow);
    padding: 20px;
  }
  .week-section {
    justify-content: center;
    gap: 16px;
  }
  .week-wrap {
    background: var(--surface);
    border: 1px solid var(--surface-border);
    border-radius: var(--card-radius);
    box-shadow: var(--card-shadow);
    padding: 20px;
    flex: 1;
    display: flex;
    flex-direction: column;
    justify-content: center;
  }
  .scroll-hint {
    background: none;
    border: none;
    color: var(--muted);
    font-size: 13px;
    font-weight: 500;
    cursor: pointer;
    padding: 10px 0;
    text-align: center;
  }
  .scroll-hint.down { margin-top: auto; }
  .empty {
    text-align: center;
    color: var(--muted);
    padding: 40px 0;
  }
  .back-link {
    margin-top: 24px;
    color: var(--muted);
    font-size: 14px;
    text-decoration: none;
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 8px;
  }
</style>