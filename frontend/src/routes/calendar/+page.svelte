<script lang="ts">
  import { babyStore } from "#lib/stores/selectedBaby.svelte.ts";
  import { listFeedsInRange } from "#lib/services/feedService.ts";
  import { dayKey, startOfWeek } from "#lib/utils/calendar.ts";
  import type { FeedResponse } from "#lib/types/feed.ts";

  import Card from "#lib/components/Card.svelte";
  import MonthCalendar from "#lib/components/MonthCalendar.svelte";
  import WeekTimelineGrid from "#lib/components/WeekTimelineGrid.svelte";
  import DayTimeline from "#lib/components/DayTimeline.svelte";

  type ViewMode = "week" | "month";

  const today = new Date();
  let viewMode = $state<ViewMode>("week");
  let year = $state(today.getFullYear());
  let month = $state(today.getMonth());
  let weekStart = $state(startOfWeek(today));
  let selectedDayKey = $state(dayKey(today)); // utilisé seulement en mode mois

  let babyId = $derived(babyStore.selectedId);
  let rangeFeeds = $state<FeedResponse[]>([]);

  let rangeFrom = $derived(viewMode === "week" ? weekStart : new Date(year, month, 1));
  let rangeTo = $derived(
    viewMode === "week" ? new Date(weekStart.getTime() + 7 * 86400000) : new Date(year, month + 1, 1)
  );

  async function loadRange() {
    if (!babyId) return;
    rangeFeeds = await listFeedsInRange(babyId, rangeFrom.toISOString(), rangeTo.toISOString());
  }

  $effect(() => {
    rangeFrom; rangeTo; babyId;
    loadRange();
  });

  function switchMode(mode: ViewMode) {
    if (mode === "month") {
      const ref = weekStart;
      year = ref.getFullYear();
      month = ref.getMonth();
    } else {
      weekStart = startOfWeek(new Date(year, month, 1));
    }
    viewMode = mode;
  }

  let selectedDayFeeds = $derived(
    rangeFeeds.filter((f) => dayKey(new Date(f.startTime)) === selectedDayKey)
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
        <WeekTimelineGrid feeds={rangeFeeds} bind:weekStart />
      </Card>
    {:else}
      <Card>
        <MonthCalendar feeds={rangeFeeds} bind:selectedDayKey bind:year bind:month />
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
</style>