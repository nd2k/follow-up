<script lang="ts">
  import { onMount } from "svelte";
  import { ongoingFeed } from "#lib/stores/ongoingFeed.svelte.ts";
  import { listFeeds, deleteFeed } from "#lib/services/feedService.ts";
  import { getStatsToday } from "#lib/services/statService.ts";
  import type { FeedResponse, BreastSide, StatsResponse } from "#lib/types/feed.ts";

  import SidePicker from "#lib/components/SidePicker.svelte";
  import FeedTimer from "#lib/components/FeedTimer.svelte";
  import StatsCards from "#lib/components/StatsCards.svelte";
  import FeedHistory from "#lib/components/FeedHistory.svelte";

  let pendingSide = $state<BreastSide | null>(null);
  let stats = $state<StatsResponse | null>(null);
  let finishedFeeds = $state<FeedResponse[]>([]);

  async function refreshHistoryAndStats() {
    const [feeds, statsResult] = await Promise.all([listFeeds(), getStatsToday()]);
    finishedFeeds = feeds.filter((f) => !f.ongoing);
    stats = statsResult;
  }

  async function handleStart() {
    if (!pendingSide) return;
    await ongoingFeed.start(pendingSide);
  }

  async function handleStop() {
    await ongoingFeed.stop();
    pendingSide = null;
    await refreshHistoryAndStats();
  }

  async function handleDelete(id: number) {
    await deleteFeed(id);
    await refreshHistoryAndStats();
  }

  onMount(async () => {
    await ongoingFeed.init();
    await refreshHistoryAndStats();
  });
</script>

<main>
  <h1>Suivi des tétées</h1>

  <SidePicker bind:selected={pendingSide} disabled={!!ongoingFeed.current} />

  <FeedTimer
    ongoing={!!ongoingFeed.current}
    breastSide={ongoingFeed.current?.breastSide ?? pendingSide}
    startTime={ongoingFeed.current?.startTime ?? null}
    loading={ongoingFeed.isLoading}
    onStart={handleStart}
    onStop={handleStop}
  />

  <StatsCards {stats} />

  <FeedHistory feeds={finishedFeeds} onDelete={handleDelete} />
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
</style>