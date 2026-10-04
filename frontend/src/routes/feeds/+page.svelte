<script lang="ts">
    import Card from "#lib/components/Card.svelte";
	import TimeSinceLastFeed from "#lib/components/TimeSinceLastFeed.svelte";
	import { listOfFeeds } from "#lib/services/feedService.ts";
    import { babyStore } from "#lib/stores/baby.svelte.ts";
	import { initiateActiveFeed } from "#lib/stores/feed.svelte.ts";
    import type { FeedResponse, StatsResponse } from "#lib/types/feed.ts";
    import { getStatsToday } from "#lib/services/statsService.ts";
    import StatsCards from "#lib/components/StatsCards.svelte";
    import FeedHistory from "#lib/components/FeedHistory.svelte";
    import SideTimerCard from "#lib/components/SideTimerCard.svelte";
    import { feedSession } from "#lib/stores/feed.svelte.ts";
    import ManualFeedModal from "#lib/components/ManualFeedModal.svelte";

    let babyId =  $derived(babyStore.selectedId);
    let finishedFeeds = $state<FeedResponse[]>([]);
    let stats = $state<StatsResponse | null>(null);
    let showManualModal = $state(false);

    let lastFeedstartTime = $derived.by(() => {
        if (finishedFeeds.length === 0) return null;
        const now = new Date().toISOString();
        const latest = finishedFeeds.reduce(
        (latest, f) => (f.endTime && f.endTime > latest ? f.startTime : latest),
        finishedFeeds[0].startTime ?? "");
        return latest && latest <= now ? latest : null;
    })

    $effect(() => {
        if (!babyId) return;
        initiateActiveFeed(babyId);
        refreshHistoryAndStats(babyId);
    })

    async function refreshHistoryAndStats(babyId: number) {
        const [ feeds, statsResult ] = await Promise.all([listOfFeeds(babyId), getStatsToday(babyId)]);
        stats = statsResult;
        finishedFeeds = feeds.filter((f) => !f.ongoing);
    }

    async function handleToggle(side: "LEFT" | "RIGHT") {
    if (!babyId) return;
    await feedSession.toggleChrono(babyId, side);
    
  }

    async function handleManualSaved() {
        showManualModal = false;
        if (babyId) refreshHistoryAndStats(babyId);
    }

    async function handleFinish() {
        if (!babyId) return;
        await feedSession.finish(babyId);
        await refreshHistoryAndStats(babyId);
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
            <TimeSinceLastFeed {lastFeedstartTime} />
        </Card>
        <Card>
        <div class="timers-row">
            <SideTimerCard
                breastSide="LEFT"
                ongoing={feedSession.isRunning("LEFT")}
                isPaused={feedSession.isPaused("LEFT")}
                startTime={feedSession.entry("LEFT")?.startTime ?? null}
                loading={feedSession.isLoading}
                syncError={feedSession.hasSyncError}
                onToggle={() => handleToggle("LEFT")}
            />
            <SideTimerCard
                breastSide="RIGHT"
                ongoing={feedSession.isRunning("RIGHT")}
                isPaused={feedSession.isPaused("RIGHT")}
                startTime={feedSession.entry("RIGHT")?.startTime ?? null}
                loading={feedSession.isLoading}
                syncError={feedSession.hasSyncError}
                onToggle={() => handleToggle("RIGHT")}
            />
        </div>

        {#if feedSession.canFinish()}
            <button class="finish-btn" disabled={feedSession.isLoading} onclick={() => handleFinish()}>
                Terminer la tétée
            </button>
        {/if}

        <button class="manual-entry" onclick={() => (showManualModal = true)}>+ Ajouter manuellement</button>
    </Card>

    <Card title="Aujourd'hui">
      <StatsCards {stats} />
    </Card>

    <Card title="Historique">
      <FeedHistory
            feeds={finishedFeeds}
            {babyId}
            onChanged={() => babyId && refreshHistoryAndStats(babyId)} />
    </Card>

    {#if showManualModal && babyId}
      <ManualFeedModal {babyId} onClose={() => (showManualModal = false)} onSaved={handleManualSaved} />
    {/if}

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
  .manual-entry {
    width: 100%;
    margin-top: 2rem;
    padding: 11px 0;
    border-radius: 14px;
    border: 1.5px dashed var(--surface-border);
    background: none;
    color: var(--muted);
    font-weight: 500;
    font-size: 13.5px;
    cursor: pointer;
  }
  .finish-btn {
  width: 100%;
  margin-top: 14px;
  padding: 14px 0;
  border-radius: 16px;
  border: none;
  background: var(--accent);
  color: #fff;
  font-weight: 600;
  cursor: pointer;
}
</style>