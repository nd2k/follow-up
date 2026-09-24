<script lang="ts">
  import { leftTimer, rightTimer, initOngoingFeeds } from "#lib/stores/ongoingFeed.svelte.ts";
  import { babyStore } from "#lib/stores/selectedBaby.svelte.ts";
  import { listFeeds, deleteFeed } from "#lib/services/feedService.ts";
  import { getStatsToday } from "#lib/services/statService.ts";
  import type { FeedResponse, StatsResponse } from "#lib/types/feed.ts";

  import Card from "#lib/components/Card.svelte";
  import SideTimerCard from "#lib/components/SideTimerCard.svelte";
  import StatsCards from "#lib/components/StatsCards.svelte";
  import FeedHistory from "#lib/components/FeedHistory.svelte";
  import TimeSinceLastFeed from "#lib/components/TimeSinceLastFeed.svelte";
  import ManualFeedModal from "#lib/components/ManualFeedModal.svelte";

  let stats = $state<StatsResponse | null>(null);
  let finishedFeeds = $state<FeedResponse[]>([]);
  let saving = $state(false);

  let babyId = $derived(babyStore.selectedId);
  let hasPendingSave = $derived(leftTimer.isPendingSave || rightTimer.isPendingSave);
  let showManualModal = $state(false);

  $effect(() => {
    if (!babyId) return;
    initOngoingFeeds(babyId);
    refreshHistoryAndStats(babyId);
  });

  let lastFeedEndTime = $derived.by(() => {
    if (finishedFeeds.length === 0) return null;
    return finishedFeeds.reduce(
      (latest, f) => (f.endTime && f.endTime > latest ? f.endTime : latest),
      finishedFeeds[0].endTime ?? ""
    ) || null;
  });

  async function refreshHistoryAndStats(id: number) {
    const [feeds, statsResult] = await Promise.all([listFeeds(id), getStatsToday(id)]);
    finishedFeeds = feeds.filter((f) => !f.ongoing);
    stats = statsResult;
  }

  async function handleDelete(id: number) {
    if (!babyId) return;
    await deleteFeed(babyId, id);
    await refreshHistoryAndStats(babyId);
  }

  async function handleSaveAll() {
    saving = true;
    try {
      // Sauvegarde chaque côté effectivement figé — l'un, l'autre, ou les deux
      const tasks: Promise<void>[] = [];
      if (leftTimer.isPendingSave) tasks.push(leftTimer.save());
      if (rightTimer.isPendingSave) tasks.push(rightTimer.save());
      await Promise.all(tasks);
      if (babyId) await refreshHistoryAndStats(babyId);
    } finally {
      saving = false;
    }
  }

  function handleManualSaved() {
    showManualModal = false;
    if (babyId) refreshHistoryAndStats(babyId);
  }
</script>

<main>
  {#if !babyId}
    <Card>
      <p class="empty">Aucun bébé enregistré.</p>
      <a href="/babies" class="link-button">Ajouter un bébé</a>
    </Card>
  {:else}
    <Card>
      <TimeSinceLastFeed {lastFeedEndTime} />
    </Card>
    <Card>
      <div class="timers-row">
        <SideTimerCard
          breastSide="LEFT"
          ongoing={!!leftTimer.current}
          startTime={leftTimer.current?.startTime ?? null}
          isPendingSave={leftTimer.isPendingSave}
          frozenEndTime={leftTimer.frozenEndTime}
          loading={leftTimer.isLoading}
          syncError={leftTimer.hasSyncError}
          onStart={() => leftTimer.start(babyId)}
          onStopClock={() => leftTimer.stopClock()}
        />
        <SideTimerCard
          breastSide="RIGHT"
          ongoing={!!rightTimer.current}
          startTime={rightTimer.current?.startTime ?? null}
          isPendingSave={rightTimer.isPendingSave}
          frozenEndTime={rightTimer.frozenEndTime}
          loading={rightTimer.isLoading}
          syncError={rightTimer.hasSyncError}
          onStart={() => rightTimer.start(babyId)}
          onStopClock={() => rightTimer.stopClock()}
        />
      </div>

      {#if hasPendingSave}
        <button class="save-all" disabled={saving} onclick={handleSaveAll}>
          {saving ? "Enregistrement…" : "Enregistrer la tétée"}
        </button>
      {/if}
      <button class="manual-entry" onclick={() => (showManualModal = true)}>
        + Ajouter manuellement
      </button>
    </Card>

    {#if showManualModal && babyId}
      <ManualFeedModal {babyId} onClose={() => (showManualModal = false)} onSaved={handleManualSaved} />
    {/if}

    <Card title="Aujourd'hui">
      <StatsCards {stats} />
    </Card>

    <Card title="Historique">
      <FeedHistory feeds={finishedFeeds} onDelete={handleDelete} />
    </Card>
  {/if}
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
  .timers-row {
    display: flex;
    gap: 16px;
    width: 100%;
  }
  .save-all {
    width: 100%;
    margin-top: 14px;
    padding: 14px 0;
    border-radius: 16px;
    border: none;
    background: var(--accent);
    color: #fff;
    font-weight: 600;
    font-size: 15px;
    cursor: pointer;
  }
  .save-all:disabled { opacity: 0.6; cursor: not-allowed; }
  .manual-entry {
    width: 100%;
    margin-top: 10px;
    padding: 11px 0;
    border-radius: 14px;
    border: 1.5px dashed var(--surface-border);
    background: none;
    color: var(--muted);
    font-weight: 500;
    font-size: 13.5px;
    cursor: pointer;
  }
</style>