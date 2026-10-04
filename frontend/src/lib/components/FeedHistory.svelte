<script lang="ts">
  import type { FeedResponse } from "#lib/types/feed.ts";
  import type { DayGroup } from "#lib/types/feed.ts";
  import FeedDetailsModal from "./FeedDetailsModal.svelte";

let {
    feeds,
    babyId,
    onChanged,
  }: {
    feeds: FeedResponse[];
    babyId: number;
    onChanged: () => void;
  } = $props();

  let sorted = $derived([...feeds].sort((a, b) => b.startTime.localeCompare(a.startTime)));
  let selectedFeed = $state<FeedResponse | null>(null);

  let groups = $derived.by((): DayGroup[] => {
    const byDay = new Map<string, FeedResponse[]>();
    for (const feed of sorted) {
      const key = dayKey(feed.startTime);
      if (!byDay.has(key)) byDay.set(key, []);
      byDay.get(key)!.push(feed);
    }
    return Array.from(byDay.entries())
          .map(([key, dayFeeds]) => ({
            key,
            feeds: dayFeeds,
            count: dayFeeds.length,
            totalMinutes: dayFeeds.reduce((sum, f) => sum + f.entries.reduce((s, e) => s + (e.durationMinutes ?? 0), 0), 0),
          }));
  });

  function dayKey(iso: string): string {
    const d = new Date(iso);
    return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, "0")}-${String(d.getDate()).padStart(2, "0")}`;
  }

  function dayHeading(key: string): string {
    const todayKey = dayKey(new Date().toISOString());
    const yesterday = new Date(); yesterday.setDate(yesterday.getDate() - 1);
     if (key === todayKey) return "Aujourd'hui";
    if (key === dayKey(yesterday.toISOString())) return "Hier";
    const [y, m, d] = key.split("-");
    return `${d}/${m}/${y}`;
  }

  function fmtTime(iso: string): string {
    return new Date(iso).toLocaleTimeString("fr-FR", { hour: "2-digit", minute: "2-digit" });
  }

  function delayToNext(currentIndex: number): number | null {
    if (currentIndex === 0) return null;
    const current = sorted[currentIndex];
    const next = sorted[currentIndex - 1];
    if (!current.endTime) return null;
    return new Date(next.startTime).getTime() - new Date(current.startTime).getTime();
  }

  function fmtDelay(ms: number): string {
    const totalMin = Math.round(ms / 60000);
    const h = Math.floor(totalMin / 60), m = totalMin % 60;
    return h > 0 ? `${h}h${String(m).padStart(2, "0")}` : `${m} min`;
  }

</script>

<div class="history">
  {#if groups.length === 0}
    <div class="empty">Aucune tétée enregistrée pour l'instant.</div>
  {:else}
    {#each groups as group (group.key)}
        <div class="day-group">
            <div class="day-heading">
                <span>{dayHeading(group.key)}</span>
                <span>{group.count} tétées · {group.totalMinutes} min</span>
            </div>
            {#each group.feeds as feed (feed.id)}
               {@const globalIndex = sorted.findIndex((f) => f.id === feed.id)}
               {#if delayToNext(globalIndex) !== null}
                <div class="delay-divider">
                  <svg width="13" height="13" viewBox="0 0 16 16" fill="none">
                    <circle cx="8" cy="8" r="6.5" stroke="currentColor" stroke-width="1.3" />
                    <path d="M8 4.5V8l2.5 1.5" stroke="currentColor" stroke-width="1.3" stroke-linecap="round" />
                  </svg>
                  <span>{fmtDelay(delayToNext(globalIndex)!)}</span>
                </div>
              {/if}
               <button class="feed-entry" onclick={() => (selectedFeed = feed)}>
                <span class="feed-time">{fmtTime(feed.startTime)}</span>
                <div class="session-sides">
                  {#each feed.entries as entry (entry.id)}
                    <span class="side-chip" class:left={entry.breastSide === "LEFT"} class:right={entry.breastSide === "RIGHT"}>
                      <span class="side-dot"></span>
                      {entry.breastSide === "LEFT" ? "G" : "D"} · {entry.durationSeconds} min
                    </span>
                  {/each}
                </div>
              </button>
            {/each}
         </div>
    {/each}
  {/if}

  {#if selectedFeed}
    <FeedDetailsModal
      {babyId}
      feed={selectedFeed}
      onClose={() => (selectedFeed = null)}
      onSaved={() => { selectedFeed = null; onChanged(); }}
    />
  {/if}
</div>


<style>
  .history { width: 100%; }
  .day-group { margin-bottom: 20px; }
  .day-heading {
    font-size: 12.5px;
    color: var(--muted);
    margin-bottom: 6px;
    display: flex;
    justify-content: space-between;
  }
  .delay-divider {
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 11px;
    color: var(--muted);
    padding: 4px 0;
    gap: 6px;
  }
  .delay-divider::before,
  .delay-divider::after {
    content: "";
    flex: 1;
    height: 1px;
    background: var(--surface-border);
  }
  .feed-entry {
    display: flex;
    align-items: center;
    gap: 10px;
    background: var(--bg);
    border: 1px solid var(--surface-border);
    border-radius: 12px;
    padding: 10px 14px;
    margin-bottom: 6px;
    font-size: 14px;
    flex-wrap: wrap;
  }
  .feed-time { font-weight: 500; min-width: 44px; }
  .session-sides {
    display: flex;
    gap: 8px;
    flex-wrap: wrap;
    margin-left: auto;
  }
  .side-chip {
    display: flex;
    align-items: center;
    gap: 5px;
    padding: 4px 8px;
    border-radius: 999px;
    font-size: 12.5px;
    font-weight: 500;
  }
  .side-chip.left { background: var(--left-soft); color: var(--left); }
  .side-chip.right { background: var(--right-soft); color: var(--right); }
  .side-dot { width: 6px; height: 6px; border-radius: 50%; background: currentColor; }
  .empty {
    text-align: center;
    color: var(--muted);
    font-size: 14px;
    padding: 20px 0;
  }
  .delay-divider {
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 5px;
    font-size: 11px;
    color: var(--muted);
    padding: 4px 0;
  }
  .delay-divider svg {
    flex-shrink: 0;
  }
  .feed-entry {
    display: flex;
    align-items: center;
    gap: 10px;
    background: var(--bg);
    border: 1px solid var(--surface-border);
    border-radius: 12px;
    padding: 10px 14px;
    margin-bottom: 6px;
    font-size: 14px;
    flex-wrap: wrap;
    width: 100%;
    cursor: pointer;
    color: var(--text);
    text-align: left;
    font-family: inherit;
  }
  .feed-entry:active { opacity: 0.85; }
</style>