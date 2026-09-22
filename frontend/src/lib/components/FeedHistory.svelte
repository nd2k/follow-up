<script lang="ts">
  import type { FeedResponse } from "#lib/types/feed.ts";

  let {
    feeds,
    onDelete,
  }: {
    feeds: FeedResponse[];
    onDelete: (id: number) => void;
  } = $props();

  function dayKey(iso: string): string {
    const d = new Date(iso);
    return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, "0")}-${String(d.getDate()).padStart(2, "0")}`;
  }

  function dayHeading(key: string): string {
    const todayKey = dayKey(new Date().toISOString());
    const yesterday = new Date();
    yesterday.setDate(yesterday.getDate() - 1);
    const yesterdayKey = dayKey(yesterday.toISOString());

    if (key === todayKey) return "Aujourd'hui";
    if (key === yesterdayKey) return "Hier";
    const [y, m, d] = key.split("-");
    return `${d}/${m}/${y}`;
  }

  function fmtTime(iso: string): string {
    return new Date(iso).toLocaleTimeString("fr-FR", { hour: "2-digit", minute: "2-digit" });
  }

  interface DayGroup {
    key: string;
    feeds: FeedResponse[];
    totalMinutes: number;
  }

  let groups = $derived.by((): DayGroup[] => {
    const map = new Map<string, FeedResponse[]>();
    for (const feed of feeds) {
      const key = dayKey(feed.startTime);
      if (!map.has(key)) map.set(key, []);
      map.get(key)!.push(feed);
    }
    return Array.from(map.entries()).map(([key, dayFeeds]) => ({
      key,
      feeds: dayFeeds,
      totalMinutes: dayFeeds.reduce((sum, f) => sum + (f.durationMinutes ?? 0), 0),
    }));
  });
</script>

<div class="history">
  <h2>Historique</h2>

  {#if groups.length === 0}
    <div class="empty">Aucune tétée enregistrée pour l'instant.</div>
  {:else}
    {#each groups as group (group.key)}
      <div class="day-group">
        <div class="day-heading">
          <span>{dayHeading(group.key)}</span>
          <span>{group.feeds.length} tétées · {group.totalMinutes} min</span>
        </div>
        {#each group.feeds as feed (feed.id)}
          <div class="feed-entry">
            <span class="side-dot" class:left={feed.breastSide === "LEFT"} class:right={feed.breastSide === "RIGHT"}></span>
            <span class="feed-time">{fmtTime(feed.startTime)}</span>
            <span class="feed-duration">{feed.durationMinutes} min</span>
            <button class="feed-delete" aria-label="Supprimer" onclick={() => onDelete(feed.id)}>✕</button>
          </div>
        {/each}
      </div>
    {/each}
  {/if}
</div>

<style>
  .history { width: 100%; }
  .history h2 {
    font-family: "Fraunces", serif;
    font-weight: 500;
    font-size: 16px;
    margin: 0 0 12px;
  }
  .day-group { margin-bottom: 20px; }
  .day-heading {
    font-size: 12.5px;
    color: var(--muted);
    margin-bottom: 6px;
    display: flex;
    justify-content: space-between;
  }
  .feed-entry {
    display: flex;
    align-items: center;
    gap: 10px;
    background: var(--surface);
    border: 1px solid var(--surface-border);
    border-radius: 12px;
    padding: 10px 14px;
    margin-bottom: 6px;
    font-size: 14px;
  }
  .side-dot { width: 8px; height: 8px; border-radius: 50%; flex-shrink: 0; }
  .side-dot.left { background: var(--left); }
  .side-dot.right { background: var(--right); }
  .feed-time { font-weight: 500; }
  .feed-duration { color: var(--muted); margin-left: auto; }
  .feed-delete {
    background: none;
    border: none;
    color: var(--muted);
    font-size: 16px;
    cursor: pointer;
    padding: 0 2px;
    line-height: 1;
  }
  .empty {
    text-align: center;
    color: var(--muted);
    font-size: 14px;
    padding: 20px 0;
  }
</style>