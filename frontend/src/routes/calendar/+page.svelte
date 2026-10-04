<script lang="ts">
  import { babyStore } from "#lib/stores/baby.svelte.ts";
  import Card from "#lib/components/Card.svelte";
	import { dayKey, flattenEntries, startOfWeek, monthGridRange, type ViewMode } from "#lib/utils/calendar.ts";
	import type { FeedResponse } from "#lib/types/feed.ts";
	import { listFeedsInRange } from "#lib/services/feedService.ts";
	import WeekTimelineGrid from "#lib/components/WeekTimelineGrid.svelte";
	import MonthCalendar from "#lib/components/MonthCalendar.svelte";
	import DayTimeline from "#lib/components/DayTimeline.svelte";

    let babyId = $derived(babyStore.selectedId);
    let rangeFeeds = $state<FeedResponse[]>([]);

    let focusDate = $state(new Date());
    let viewMode = $state<ViewMode>("week");
    let year = $derived(focusDate.getFullYear());
    let month = $derived(focusDate.getMonth());
    let weekStart = $derived(startOfWeek(focusDate));
    let selectedDayKey = $state(dayKey(new Date()));

    
    let rangeFrom = $derived(
      viewMode === "week" ? weekStart : monthGridRange(year, month).from
    );
    let rangeTo = $derived(
      viewMode === "week" ? new Date(weekStart.getTime() + 7 * 86400000) : monthGridRange(year, month).to
    );
    let latestRequestId = 0;

    async function loadRange() {
        if (!babyId) return;
        const requestId = ++latestRequestId;
        const result = await listFeedsInRange(babyId, rangeFrom.toISOString(), rangeTo.toISOString());
        if (requestId === latestRequestId) {
          rangeFeeds = result;
        }
    }

     $effect(() => {
        rangeFrom; 
        rangeTo; 
        babyId;
        loadRange();        
    });

     function switchMode(mode: ViewMode) {
      viewMode = mode;
    }

    function prevWeek() { focusDate = new Date(focusDate.getTime() - 7 * 86400000); }
    function nextWeek() { focusDate = new Date(focusDate.getTime() + 7 * 86400000); }
    function prevMonth() { focusDate = new Date(year, month - 1, 1); }
    function nextMonth() { focusDate = new Date(year, month + 1, 1); }

    function selectDay(key: string) {
      selectedDayKey = key;
      focusDate = new Date(key);
    }

    let selectedDayFeeds = $derived(
      flattenEntries(rangeFeeds.filter((f) => dayKey(new Date(f.startTime)) === selectedDayKey))
    );
</script>

<main>  
  {#if !babyId}
    <Card>
      <p class="empty">Aucun bébé enregistré.</p>
    </Card>
  {:else}
    <div class="mode-switch">
      <button class:active={viewMode === "week"} onclick={() => switchMode("week")}>Semaine</button>
      <button class:active={viewMode === "month"} onclick={() => switchMode("month")}>Mois</button>
    </div>

    {#if viewMode === "week"}
      <Card>
        <WeekTimelineGrid feeds={flattenEntries(rangeFeeds)} {weekStart} onPrev={prevWeek} onNext={nextWeek} />
      </Card>
    {:else}
      <Card>
        <MonthCalendar feeds={rangeFeeds} {year} {month} {selectedDayKey} onSelectDay={selectDay} onPrev={prevMonth} onNext={nextMonth} />
      </Card>
      <Card>
        <DayTimeline feeds={selectedDayFeeds} dayKey={selectedDayKey} />
      </Card>
    {/if}
  {/if}
  <a href="/feeds" class="back-link">← Retour au suivi</a>
</main>

<style>
  main {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 16px;
    padding: 4px 20px 48px;
    max-width: 420px;
    margin: 0 auto;
  }
  .mode-switch {
    display: flex;
    gap: 6px;
    width: 100%;
  }
  .mode-switch button {
    flex: 1;
    padding: 10px 0;
    border-radius: 12px;
    border: 1.5px solid var(--surface-border);
    background: var(--surface);
    color: var(--muted);
    font-weight: 500;
    font-size: 14px;
    cursor: pointer;
  }
  .mode-switch button.active {
    background: var(--accent);
    border-color: var(--accent);
    color: #fff;
  }
  .back-link {
    margin-top: 24px;
    color: var(--muted);
    font-size: 14px;
    text-decoration: none;
  }
</style>