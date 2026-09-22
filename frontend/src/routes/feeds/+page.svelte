<script lang="ts">
  import { onMount } from "svelte";
  import { leftTimer, rightTimer, initOngoingFeeds } from "#lib/stores/ongoingFeed.svelte.ts";
  import { babyStore } from "#lib/stores/selectedBaby.svelte.ts";
  import { listFeeds, deleteFeed } from "#lib/services/feedService.ts";
  import { getStatsToday } from "#lib/services/statService.ts";
  import { checkHealth } from "#lib/services/healthService.ts";
  import type { FeedResponse, StatsResponse } from "#lib/types/feed.ts";

  import StatsCards from "#lib/components/StatsCards.svelte";
  import FeedHistory from "#lib/components/FeedHistory.svelte";
  import SideTimerCard from "#lib/components/SideTimerCard.svelte";
  import Card from "#lib/components/Card.svelte";

  let stats = $state<StatsResponse | null>(null);
  let finishedFeeds = $state<FeedResponse[]>([]);
  let backendStatus = $state<"waking" | "ready" | "error">("waking");

  let babyId = $derived(babyStore.selectedId);

  async function wakeBackend() {
    try {
      console.log('wakeup')
      await checkHealth();
      console.log('wakeup ok');
      backendStatus = "ready";
    } catch(error) {
      console.error(error)
      backendStatus = "error";
    }
  }

  async function refreshHistoryAndStats(id: number) {
    if (!id) return;
    const [feeds, statsResult] = await Promise.all([listFeeds(id), getStatsToday(id)]);
    finishedFeeds = feeds.filter((f) => !f.ongoing);
    stats = statsResult;
  }

 async function handleStopLeft() {
    await leftTimer.stop();
    if (babyId) await refreshHistoryAndStats(babyId);
  }

  async function handleStopRight() {
    await rightTimer.stop();
    if (babyId) await refreshHistoryAndStats(babyId);
  }

  async function handleDelete(id: number) {
    if (!babyId) return;
    await deleteFeed(babyId, id);
    await refreshHistoryAndStats(babyId);
  }


  onMount(async () => {
    await wakeBackend();
    await babyStore.refresh();
  });

   $effect(() => {
    if (!babyId) return;
    initOngoingFeeds(babyId);
    refreshHistoryAndStats(babyId);
  });
</script>

<main>
  {#if backendStatus === "waking"}
    <div class="status-banner">
      <span class="spinner"></span>
      Réveil du serveur, un instant…
    </div>
  {:else if backendStatus === "error"}
    <div class="status-banner error">
      Connexion impossible — réessaie dans quelques secondes.
      <button onclick={wakeBackend}>Réessayer</button>
    </div>
  {/if}
  <h1>Suivi des tétées</h1>

  {#if !babyId}
    <Card>
      <p class="empty">Aucun bébé enregistré.</p>
      <a href="/babies" class="link-button">Ajouter un bébé</a>
    </Card>
  {:else}
   <Card>
      <div class="timers-row">
        <SideTimerCard
          breastSide="LEFT"
          ongoing={!!leftTimer.current}
          startTime={leftTimer.current?.startTime ?? null}
          loading={leftTimer.isLoading}
          syncError={leftTimer.hasSyncError}
          onStart={() => leftTimer.start(babyId)}
          onStop={handleStopLeft}
        />
        <SideTimerCard
          breastSide="RIGHT"
          ongoing={!!rightTimer.current}
          startTime={rightTimer.current?.startTime ?? null}
          loading={rightTimer.isLoading}
          syncError={rightTimer.hasSyncError}
          onStart={() => rightTimer.start(babyId)}
          onStop={handleStopRight}
        />
      </div>
      <Card title="Aujourd'hui">
        <StatsCards {stats} />
      </Card>

      <Card title="Historique">
        <FeedHistory feeds={finishedFeeds} onDelete={handleDelete} />
      </Card>
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
  h1 {
    font-family: "Fraunces", serif;
    font-weight: 500;
    font-size: 22px;
    margin: 4px 0 28px;
  }
  .link-button {
    padding: 14px 24px;
    border-radius: 16px;
    background: var(--accent);
    color: #fff;
    font-weight: 600;
    text-decoration: none;
  }
</style>