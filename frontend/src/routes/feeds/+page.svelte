<script lang="ts">
  import { onMount } from "svelte";
  import { ongoingFeed } from "#lib/stores/ongoingFeed.svelte.ts";
  import { babyStore } from "#lib/stores/selectedBaby.svelte.ts";
  import { listFeeds, deleteFeed } from "#lib/services/feedService.ts";
  import { getStatsToday } from "#lib/services/statService.ts";
  import { checkHealth } from "#lib/services/healthService.ts";
  import type { FeedResponse, BreastSide, StatsResponse } from "#lib/types/feed.ts";

  import SidePicker from "#lib/components/SidePicker.svelte";
  import FeedTimer from "#lib/components/FeedTimer.svelte";
  import StatsCards from "#lib/components/StatsCards.svelte";
  import FeedHistory from "#lib/components/FeedHistory.svelte";

  let pendingSide = $state<BreastSide | null>(null);
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

  async function refreshHistoryAndStats() {
    if (!babyId) return;
    const [feeds, statsResult] = await Promise.all([listFeeds(babyId), getStatsToday(babyId)]);
    finishedFeeds = feeds.filter((f) => !f.ongoing);
    stats = statsResult;
  }

  async function handleStart() {
    if (!pendingSide || !babyId) return;
    await ongoingFeed.start(babyId, pendingSide);
  }

  async function handleStop() {
    await ongoingFeed.stop();
    pendingSide = null;
    await refreshHistoryAndStats();
  }

  async function handleDelete(id: number) {
    if (!babyId) return;
    await deleteFeed(babyId, id);
    await refreshHistoryAndStats();
  }

  onMount(async () => {
    await wakeBackend();
    await babyStore.refresh();
    if (babyId) {
      await ongoingFeed.init(babyId);
      await refreshHistoryAndStats();
    }
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
    <p class="empty">Aucun bébé enregistré.</p>
    <a href="/babies" class="link-button">Ajouter un bébé</a>
  {:else}
    <SidePicker bind:selected={pendingSide} disabled={!!ongoingFeed.current} />

    <FeedTimer
      ongoing={!!ongoingFeed.current}
      breastSide={ongoingFeed.current?.breastSide ?? pendingSide}
      startTime={ongoingFeed.current?.startTime ?? null}
      loading={ongoingFeed.isLoading}
      onStart={handleStart}
      onStop={handleStop}
    />

    {#if ongoingFeed.hasSyncError}
      <div class="sync-warning">Synchronisation en cours…</div>
    {/if}

    <StatsCards {stats} />
    <FeedHistory feeds={finishedFeeds} onDelete={handleDelete} />
  {/if}
</main>

<style>
  main {
    display: flex;
    flex-direction: column;
    align-items: center;
    padding: 24px 20px 48px;
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